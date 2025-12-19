package tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class NBinary2<V> {
  private final Comparator<?>[] keyComparators;

  private BinaryNode<V> root = null;
  private boolean needsRebalancing = false;

  public NBinary2(final Comparator<?>... keyComparators) {
    for (final Comparator<?> keyClass : keyComparators) {
      if (keyClass == null) {
        throw new IllegalArgumentException("Key class cannot be null");
      }
    }

    this.keyComparators = keyComparators;
  }

  public final void add(final V value, final Object... keys) {
    validateKeys(keys);

    if (root == null) {
      root = of(value, keys, 0);
    } else {
      if (root.add(value, keys, 0)) {
        needsRebalancing = true;
      }
    }
  }

  public final List<V> get(final Object... keys) {
    validateKeys(keys);

    if (root == null) {
      return null;
    }

    return root.getData(keys, 0);
  }

  private void validateKeys(final Object[] keys) {
    if (keys.length != keyComparators.length) {
      throw new IllegalArgumentException("Number of keys does not match the number of discriminants");
    }
  }

  private BinaryNode<V> of(final V value, final Object[] keys, final int depthIndex) {
    if (keys.length - 1 == depthIndex) {
      return new DeepEndNode(value, keys[depthIndex]);
    } else {
      return new IntermediateNode(value, keys, depthIndex);
    }
  }

  public void rebalance() {
    rebalanceRootBinary(root);

    if (root instanceof IntermediateNode n) {
      LinkedList<IntermediateNode> nodes = new LinkedList<>();
      LinkedList<IntermediateNode> nextLevel = new LinkedList<>();
      nodes.add(n);
      int depth = 1;

      while (!nodes.isEmpty()) {
        final IntermediateNode current = nodes.remove(0);

        if (current.subNeedsRebalancing) {
          rebalanceBinary(current, current.subTreeRoot, depth);
        }

        if (current.subTreeRoot instanceof IntermediateNode sub) {
          nextLevel.add(sub);
        }
        if (current.getLeft() != null) {
          nodes.add(current.getLeft());
        }
        if (current.getRight() != null) {
          nodes.add(current.getRight());
        }

        if (nodes.isEmpty()) {
          nodes = nextLevel;
          nextLevel = new LinkedList<>();
          depth++;
        }
      }
    }
  }

  private interface BinaryNode<D> {
    Object getDiscriminant();

    BinaryNode<D> getLeft();

    void setLeft(final BinaryNode<D> left);

    BinaryNode<D> getRight();

    void setRight(final BinaryNode<D> right);

    /**
     *
     * @return true if a node was created, indicating a possible need for rebalancing for a given level
     */
    boolean add(final D data, final Object[] keys, final int depthIndex);

    List<D> getData(final Object[] keys, final int depthIndex);
  }

  private class IntermediateNode implements BinaryNode<V> {
    @Getter
    private final Object discriminant;
    @Getter
    private IntermediateNode left, right = null;
    private BinaryNode<V> subTreeRoot;

    private final Comparator comparator;
    private boolean subNeedsRebalancing = false;

    private IntermediateNode(final V value, final Object[] keys, final int depthIndex) {
      this.discriminant = keys[depthIndex];

      this.subTreeRoot = of(value, keys, depthIndex + 1);
      this.comparator = keyComparators[depthIndex];
    }

    @Override
    public void setLeft(final BinaryNode<V> left) {
      this.left = (IntermediateNode) left;
    }

    @Override
    public void setRight(final BinaryNode<V> right) {
      this.right = (IntermediateNode) right;
    }

    @Override
    public boolean add(final V value, final Object[] keys, final int depthIndex) {
      final Object key = keys[depthIndex];
      final int compare = comparator.compare(key, this.discriminant);

      if (compare == 0) {
        if (subTreeRoot.add(value, keys, depthIndex + 1)) {
          subNeedsRebalancing = true;
        }

        return false;
      } else if (compare > 0 && left == null) {
        left = new IntermediateNode(value, keys, depthIndex);

        return true;
      } else if (compare > 0) {
        return left.add(value, keys, depthIndex);
      } else if (right == null) {
        right = new IntermediateNode(value, keys, depthIndex);

        return true;
      } else {
        return right.add(value, keys, depthIndex);
      }
    }

    @Override
    public List<V> getData(final Object[] keys, final int depthIndex) {
      final Object key = keys[depthIndex];

      final int compare = comparator.compare(key, this.discriminant);

      if (compare == 0) {
        return subTreeRoot.getData(keys, depthIndex + 1);
      } else if (compare > 0) {
        return left != null ? left.getData(keys, depthIndex) : null;
      } else {
        return right != null ? right.getData(keys, depthIndex) : null;
      }
    }
  }

  private class DeepEndNode implements BinaryNode<V> {
    @Getter
    private final Object discriminant;
    private final List<V> values = new ArrayList<>();
    @Getter
    private DeepEndNode left, right = null;
    private final Comparator comparator;

    public DeepEndNode(final V value, final Object discriminant) {
      this.discriminant = discriminant;
      this.values.add(value);

      this.comparator = keyComparators[keyComparators.length - 1];
    }

    @Override
    public void setLeft(final BinaryNode<V> left) {
      this.left = (DeepEndNode) left;
    }

    @Override
    public void setRight(final BinaryNode<V> right) {
      this.right = (DeepEndNode) right;
    }


    @Override
    public boolean add(final V value, final Object[] keys, final int depthIndex) {
      if (keys.length - 1 != depthIndex) {
        throw new IllegalArgumentException("DeepEndNode can only handle one key");
      }

      return add(value, keys[depthIndex]);
    }

    private boolean add(final V value, final Object key) {
      final int compare = comparator.compare(key, this.discriminant);
      if (compare == 0) {
        values.add(value);

        return false;
      } else if (compare > 0 && left == null) {
        left = new DeepEndNode(value, key);

        return true;
      } else if (compare > 0) {
        return left.add(value, key);
      } else if (right == null) {
        right = new DeepEndNode(value, key);

        return true;
      } else {
        return right.add(value, key);
      }
    }

    @Override
    public List<V> getData(final Object[] keys, final int depthIndex) {
      if (keys.length - 1 != depthIndex) {
        throw new IllegalArgumentException("DeepEndNode can only handle one key");
      }

      return getData(keys[depthIndex]);
    }

    private List<V> getData(final Object key) {
      final int compare = comparator.compare(key, this.discriminant);

      if (compare == 0) {
        return values;
      } else if (compare > 0) {
        return left != null ? left.getData(key) : null;
      } else {
        return right != null ? right.getData(key) : null;
      }
    }

    @Override
    public String toString() {
      return discriminant.toString();
    }
  }

  @AllArgsConstructor
  private class Level {
    BinaryNode<V> node;
    int minIndex;
    int nodeIndex;
    int maxIndex;
  }

  private void rebalanceBinary(
      final IntermediateNode parentNode,
      final BinaryNode<V> rebalanceRoot,
      final int depthIndex
  ) {
    if (rebalanceRoot == null || !parentNode.subNeedsRebalancing) {
      return;
    }

    final List<BinaryNode<V>> nodes = severeNodeLinks(rebalanceRoot);
    nodes.sort(Comparator.comparing(
        BinaryNode::getDiscriminant,
        (Comparator<? super Object>) keyComparators[depthIndex]
    ));

    final int rootIndex = nodes.size() / 2;

    parentNode.subTreeRoot = nodes.get(rootIndex);

    final List<Level> levels = new LinkedList<>();
    levels.add(new Level(parentNode.subTreeRoot, 0, rootIndex, nodes.size()));
    // [0, rootIndex[ + [rootIndex + 1, nodes.size()[

    // leftSize = [minIndex, nodeIndex[,  rightSize = [nodeIndex + 1, maxIndex[
    // leftIndex = minIndex + leftSize / 2, rightIndex = nodeIndex + 1 + rightSize / 2
    while (!levels.isEmpty()) {
      final Level level = levels.remove(0);
      final BinaryNode<V> node = level.node;

      final int leftSize = level.nodeIndex - level.minIndex;
      final int leftIndex = level.minIndex + leftSize / 2;
      final int rightSize = level.maxIndex - level.nodeIndex - 1;
      final int rightIndex = level.nodeIndex + 1 + rightSize / 2;

      if (leftSize > 0) {
        node.setLeft(nodes.get(leftIndex));

        if (leftSize > 1) {
          levels.add(new Level(node.getLeft(), level.minIndex, leftIndex, level.nodeIndex));
        }
      }

      if (rightSize > 0) {
        node.setRight(nodes.get(rightIndex));

        if (rightSize > 1) {
          levels.add(new Level(node.getRight(), level.nodeIndex + 1, rightIndex, level.maxIndex));
        }
      }
    }

    parentNode.subNeedsRebalancing = false;
  }

  private void rebalanceRootBinary(final BinaryNode<V> rebalanceRoot) {
    if (rebalanceRoot == null || !this.needsRebalancing) {
      return;
    }

    final List<BinaryNode<V>> nodes = severeNodeLinks(rebalanceRoot);
    nodes.sort(Comparator.comparing(
        BinaryNode::getDiscriminant,
        (Comparator<? super Object>) keyComparators[0]
    ));

    final int rootIndex = nodes.size() / 2;

    root = nodes.get(rootIndex);

    final List<Level> levels = new LinkedList<>();
    levels.add(new Level(root, 0, rootIndex, nodes.size()));
    // [0, rootIndex[ + [rootIndex + 1, nodes.size()[

    // leftSize = [minIndex, nodeIndex[,  rightSize = [nodeIndex + 1, maxIndex[
    // leftIndex = minIndex + leftSize / 2, rightIndex = nodeIndex + 1 + rightSize / 2
    while (!levels.isEmpty()) {
      final Level level = levels.remove(0);
      final BinaryNode<V> node = level.node;

      final int leftSize = level.nodeIndex - level.minIndex;
      final int leftIndex = level.minIndex + leftSize / 2;
      final int rightSize = level.maxIndex - level.nodeIndex - 1;
      final int rightIndex = level.nodeIndex + 1 + rightSize / 2;

      if (leftSize > 0) {
        node.setLeft(nodes.get(leftIndex));

        if (leftSize > 1) {
          levels.add(new Level(node.getLeft(), level.minIndex, leftIndex, level.nodeIndex));
        }
      }

      if (rightSize > 0) {
        node.setRight(nodes.get(rightIndex));

        if (rightSize > 1) {
          levels.add(new Level(node.getRight(), level.nodeIndex + 1, rightIndex, level.maxIndex));
        }
      }
    }

    needsRebalancing = false;
  }


  private List<BinaryNode<V>> severeNodeLinks(final BinaryNode<V> rebalanceRoot) {
    final List<BinaryNode<V>> nodes = new ArrayList<>();

    final List<BinaryNode<V>> notRetrievedNodes = new LinkedList<>();
    notRetrievedNodes.add(rebalanceRoot);

    while (!notRetrievedNodes.isEmpty()) {
      final BinaryNode<V> current = notRetrievedNodes.remove(0);
      nodes.add(current);

      final BinaryNode<V> left = current.getLeft();
      if (left != null) {
        notRetrievedNodes.add(left);
        current.setLeft(null);
      }

      final BinaryNode<V> right = current.getRight();
      if (right != null) {
        notRetrievedNodes.add(right);
        current.setRight(null);
      }
    }

    return nodes;
  }
}
