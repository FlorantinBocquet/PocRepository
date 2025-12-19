package tree.nbinary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tree.nbinary.node.BinaryNode;
import tree.nbinary.node.DeepEndNode;
import tree.nbinary.node.IntermediateNode;
import tree.nbinary.operation.Operation;
import tree.nbinary.operation.OperationExecutor;

public class NBinaryTree<V> {
  @Getter
  private final List<Comparator> comparators = new ArrayList<>();

  private BinaryNode<V> root = null;
  private boolean needsRebalancing = false;

  private final OperationExecutor<V> ops = new OperationExecutor<>();

  private NBinaryTree(List<Comparator> comparators) {
    if (comparators == null || comparators.isEmpty()) {
      throw new IllegalArgumentException("At least one comparator must be provided");
    }

    this.comparators.addAll(comparators);
  }

  public final void add(final V value, final Object... keys) {
    validateKeys(keys);

    if (root == null) {
      root = of(value, keys, 0);
    } else if (root.add(value, keys, 0)) {
      needsRebalancing = true;
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
    if (keys.length != comparators.size()) {
      throw new IllegalArgumentException("Number of keys does not match the depth");
    }

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] == null) {
        throw new IllegalArgumentException("Key at index " + i + " cannot be null");
      }
    }
  }

  public BinaryNode<V> of(final V value, final Object[] keys, final int depthIndex) {
    if (keys.length - 1 == depthIndex) {
      return new DeepEndNode<>(value, keys[depthIndex], comparators.get(depthIndex));
    } else {
      return new IntermediateNode<>(value, keys, depthIndex, this);
    }
  }

  public void rebalance() {
    rebalanceRootBinary();

    if (root instanceof IntermediateNode<V> n) {
      LinkedList<IntermediateNode<V>> nodes = new LinkedList<>();
      nodes.add(n);

      while (!nodes.isEmpty()) {
        final IntermediateNode<V> current = nodes.remove(0);

        rebalanceSubBinaryOf(current);

        if (current.getSubTreeRoot() instanceof IntermediateNode<V> sub) {
          nodes.add(sub);
        }
        if (current.getLeft() != null) {
          nodes.add(current.getLeft());
        }
        if (current.getRight() != null) {
          nodes.add(current.getRight());
        }
      }
    }
  }

  private void rebalanceRootBinary() {
    if (root == null || !this.needsRebalancing) {
      return;
    }

    this.root = rebalance(root);
    this.needsRebalancing = false;
  }

  private void rebalanceSubBinaryOf(final IntermediateNode<V> parentNode) {
    if (parentNode.getSubTreeRoot() == null || !parentNode.isSubNeedsRebalancing()) {
      return;
    }

    parentNode.setSubTreeRoot(rebalance(parentNode.getSubTreeRoot()));
    parentNode.setSubNeedsRebalancing(false);
  }

  private BinaryNode<V> rebalance(final BinaryNode<V> currentRootNode) {
    final List<BinaryNode<V>> nodes = severeNodeLinks(currentRootNode);
    nodes.sort(Comparator.comparing(BinaryNode::getId, (a, b) -> currentRootNode.getComparator().compare(a, b)));

    final int rootIndex = nodes.size() / 2;

    final BinaryNode<V> newRoot = nodes.get(rootIndex);

    final List<Level> levels = new LinkedList<>();
    levels.add(new Level(newRoot, 0, rootIndex, nodes.size()));
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

    return newRoot;
  }

  private static <V> List<BinaryNode<V>> severeNodeLinks(final BinaryNode<V> rebalanceRoot) {
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

  public List<V> search(final Operation... operations) {
    if (operations.length != comparators.size()) {
      throw new IllegalArgumentException("Number of operations does not match the depth");
    }

    List<BinaryNode<V>> nodes = new LinkedList<>();
    nodes.add(root);
    List<BinaryNode<V>> nextLevelNodes = new LinkedList<>();

    int currentDepth = 0;
    final List<V> results = new ArrayList<>();

    do {
      final Operation co = operations[currentDepth];
      final int opValue = co.getType().getValue();

      while (!nodes.isEmpty()) {
        final BinaryNode<V> current = nodes.remove(0);

        if (current instanceof IntermediateNode<V> n) {
          nextLevelNodes.addAll(ops.runIntermediate(n, opValue, co.getKeys()));
        } else if (current instanceof DeepEndNode<V> n) {
          results.addAll(ops.runDeepEnd(n, opValue, co.getKeys()));
        }
      }

      if (!nextLevelNodes.isEmpty()) {
        nodes = nextLevelNodes;
        nextLevelNodes = new LinkedList<>();
        currentDepth++;
      }
    } while (!nodes.isEmpty());

    return results;
  }


  @AllArgsConstructor
  private class Level {
    BinaryNode<V> node;
    int minIndex;
    int nodeIndex;
    int maxIndex;
  }

  public static Builder builder() {
    return new Builder();
  }

  @NoArgsConstructor
  public static class Builder {
    private final List<Comparator> comparators = new ArrayList<>();

    public Builder addLayer(final Comparator comparator) {
      if (comparator == null) {
        throw new IllegalArgumentException("Comparator cannot be null");
      }
      this.comparators.add(comparator);

      return this;
    }

    public <V> NBinaryTree<V> build() {
      if (comparators.isEmpty()) {
        throw new IllegalStateException("At least one comparator must be added");
      }

      return new NBinaryTree<>(comparators);
    }
  }
}
