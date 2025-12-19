package tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import tree.data.DefaultStoredData;
import tree.data.StoredData;


public class Binary<D extends Comparable<D>, I, S extends StoredData<D, I>> {
  private final Comparator<BinaryNode> COMPARATOR = Comparator.comparing(node -> node.data.getDiscriminant());

  private final BiFunction<D, I, S> builder;
  private BinaryNode root;

  public Binary(final BiFunction<D, I, S> builder) {
    this.builder = builder;
  }

  public static <D extends Comparable<D>, I, S extends StoredData<D, I>> Binary<D, I, S> binaryWith(
      final BiFunction<D, I, S> builder
  ) {
    return new Binary<>(builder);
  }

  public static <D extends Comparable<D>, I> Binary<D, I, DefaultStoredData<D, I>> binary() {
    return new Binary<>(DefaultStoredData::new);
  }

  public void addData(final D discriminant, final I inputValue) {
    if (root == null) {
      root = new BinaryNode(discriminant, inputValue);
    } else {
      root.add(new InputData<>(discriminant, inputValue));
    }
  }

  public S get(D discriminant) {
    if (root == null) {
      return null;
    }
    return root.getData(discriminant);
  }

  public S equals(final D discriminant) {
    return get(discriminant);
  }

  public List<S> superiorOrEquals(final D discriminant) {
    return validate(result -> {
      final List<BinaryNode> toTreat = new LinkedList<>();
      toTreat.add(root);

      while (!toTreat.isEmpty()) {
        final BinaryNode node = toTreat.remove(0);

        // right node is the superior node
        if (node.right != null) {
          toTreat.add(node.right);
        }

        final int discriminantComparison = node.data.getDiscriminant().compareTo(discriminant);
        if (node.left != null && discriminantComparison >= 1) {
          toTreat.add(node.left);
        }
        if (discriminantComparison >= 0) {
          result.add(node.data);
        }
      }
    });
  }

  public List<S> inferiorOrEquals(final D discriminant) {
    return validate(result -> {
      final List<BinaryNode> toTreat = new LinkedList<>();
      toTreat.add(root);

      while (!toTreat.isEmpty()) {
        final BinaryNode node = toTreat.remove(0);

        // left node is the inferior node
        if (node.left != null) {
          toTreat.add(node.left);
        }

        final int discriminantComparison = node.data.getDiscriminant().compareTo(discriminant);
        if (node.right != null && discriminantComparison <= -1) {
          toTreat.add(node.right);
        }
        if (discriminantComparison <= 0) {
          result.add(node.data);
        }
      }
    });
  }

  public List<S> superiorStrict(final D discriminant) {
    return validate(result -> {
      final List<BinaryNode> toTreat = new LinkedList<>();
      toTreat.add(root);

      while (!toTreat.isEmpty()) {
        final BinaryNode node = toTreat.remove(0);

        // right node is the superior node
        if (node.right != null) {
          toTreat.add(node.right);
        }

        if (node.data.getDiscriminant().compareTo(discriminant) >= 1) {
          if (node.left != null) {
            toTreat.add(node.left);
          }
          result.add(node.data);
        }
      }
    });
  }

  public List<S> inferiorStrict(final D discriminant) {
    return validate(result -> {
      final List<BinaryNode> toTreat = new LinkedList<>();
      toTreat.add(root);

      while (!toTreat.isEmpty()) {
        final BinaryNode node = toTreat.remove(0);

        // left node is the inferior node
        if (node.left != null) {
          toTreat.add(node.left);
        }

        if (node.data.getDiscriminant().compareTo(discriminant) <= -1) {
          if (node.right != null) {
            toTreat.add(node.right);
          }
          result.add(node.data);
        }
      }
    });
  }

  public List<S> betweenInclusive(final D from, final D to) {
    if (from == null) {
      return inferiorOrEquals(to);
    } else if (to == null) {
      return superiorOrEquals(from);
    } else if (from.compareTo(to) == 0) {
      return List.of(equals(from));
    } else if (from.compareTo(to) > 0) {
      return List.of();
    }

    return validate(result -> {
      final List<BinaryNode> toTreat = new LinkedList<>();
      toTreat.add(root);

      while (!toTreat.isEmpty()) {
        final BinaryNode node = toTreat.remove(0);

        final int inferior = node.data.getDiscriminant().compareTo(from);
        final int superior = node.data.getDiscriminant().compareTo(to);

        if (inferior >= 0 && superior <= 0) {
          result.add(node.data);
        }

        if (node.left != null && inferior > 0) {
          toTreat.add(node.left);
        }

        if (node.right != null && superior < 0) {
          toTreat.add(node.right);
        }
      }
    });
  }

  public List<S> betweenExclusive(final D from, final D to) {
    if (from == null) {
      return inferiorStrict(to);
    } else if (to == null) {
      return superiorStrict(from);
    } else if (from.compareTo(to) >= 0) {
      return List.of();
    }

    return validate(result -> {
      final List<BinaryNode> toTreat = new LinkedList<>();
      toTreat.add(root);

      while (!toTreat.isEmpty()) {
        final BinaryNode node = toTreat.remove(0);

        final int inferior = node.data.getDiscriminant().compareTo(from);
        final int superior = node.data.getDiscriminant().compareTo(to);

        if (inferior > 0 && superior < 0) {
          result.add(node.data);
        }

        if (node.left != null && inferior > 0) {
          toTreat.add(node.left);
        }

        if (node.right != null && superior < 0) {
          toTreat.add(node.right);
        }
      }
    });
  }

  public List<S> in(final List<D> in) {
    if (root == null) {
      return List.of();
    }

    final List<S> result = new ArrayList<>();

    for (final D data : in) {
      final S item = root.getData(data);
      if (item != null) {
        result.add(item);
      }
    }

    return result;
  }

  public void balanceBinary() {
    if (root == null) {
      return;
    }

    final List<BinaryNode> nodes = severeNodeLinks();
    nodes.sort(COMPARATOR);

    final int rootIndex = nodes.size() / 2;

    root = nodes.get(rootIndex);

    final List<Level> levels = new LinkedList<>();
    levels.add(new Level(root, 0, rootIndex, nodes.size()));
    // [0, rootIndex[ + [rootIndex + 1, nodes.size()[

    // leftSize = [minIndex, nodeIndex[,  rightSize = [nodeIndex + 1, maxIndex[
    // leftIndex = minIndex + leftSize / 2, rightIndex = nodeIndex + 1 + rightSize / 2
    while (!levels.isEmpty()) {
      final Level level = levels.remove(0);
      final BinaryNode node = level.node;

      final int leftSize = level.nodeIndex - level.minIndex;
      final int leftIndex = level.minIndex + leftSize / 2;
      final int rightSize = level.maxIndex - level.nodeIndex - 1;
      final int rightIndex = level.nodeIndex + 1 + rightSize / 2;

      if (leftSize > 0) {
        node.left = nodes.get(leftIndex);

        if (leftSize > 1) {
          levels.add(new Level(node.left, level.minIndex, leftIndex, level.nodeIndex));
        }
      }

      if (rightSize > 0) {
        node.right = nodes.get(rightIndex);

        if (rightSize > 1) {
          levels.add(new Level(node.right, level.nodeIndex + 1, rightIndex, level.maxIndex));
        }
      }
    }
  }

  private List<BinaryNode> severeNodeLinks() {
    final List<BinaryNode> nodes = new ArrayList<>();

    final List<BinaryNode> notRetrievedNodes = new LinkedList<>();
    notRetrievedNodes.add(root);

    while (!notRetrievedNodes.isEmpty()) {
      final BinaryNode current = notRetrievedNodes.remove(0);
      nodes.add(current);

      if (current.left != null) {
        notRetrievedNodes.add(current.left);
        current.left = null;
      }
      if (current.right != null) {
        notRetrievedNodes.add(current.right);
        current.right = null;
      }
    }
    return nodes;
  }

  private List<S> validate(final Consumer<List<S>> filler) {
    if (root == null) {
      return List.of();
    }

    final List<S> result = new ArrayList<>();
    filler.accept(result);

    return result;
  }

  @AllArgsConstructor
  private class Level {
    BinaryNode node;
    int minIndex;
    int nodeIndex;
    int maxIndex;
  }

  private class BinaryNode {
    private final S data;
    private BinaryNode left, right;

    public BinaryNode(final D discriminant, final I item) {
      data = builder.apply(discriminant, item);
      left = right = null;
    }

    public void add(final InputData<D, I> inputData) {
      final int compare = inputData.getDiscriminant().compareTo(this.data.getDiscriminant());
      if (compare == 0) {
        data.manageData(inputData.getInputValue());
      } else if (compare > 0 && left == null) {
        left = new BinaryNode(inputData.getDiscriminant(), inputData.getInputValue());
      } else if (compare > 0) {
        left.add(inputData);
      } else if (right == null) {
        right = new BinaryNode(inputData.getDiscriminant(), inputData.getInputValue());
      } else {
        right.add(inputData);
      }
    }

    public S getData(final D discriminant) {
      final int compare = this.data.getDiscriminant().compareTo(discriminant);
      if (compare == 0) {
        return data;
      } else if (compare > 0) {
        return left != null ? left.getData(discriminant) : null;
      } else {
        return right != null ? right.getData(discriminant) : null;
      }
    }

    @Override
    public String toString() {
      return data.getDiscriminant().toString();
    }
  }

  @Override
  public String toString() {
    if (root == null) {
      return "empty binary tree";
    }

    final StringBuilder sb = new StringBuilder();

    final Stack<Map.Entry<Integer, BinaryNode>> filo = new Stack<>();
    filo.add(Map.entry(0, root));

    while (!filo.isEmpty()) {
      var current = filo.pop();

      sb
          .append("   ".repeat(Math.max(0, current.getKey())))
          .append("\\-")
          .append(current.getValue().data.getDiscriminant())
          .append("\n");

      final BinaryNode left = current.getValue().left;
      final BinaryNode right = current.getValue().right;

      if (left != null) {
        filo.add(Map.entry(current.getKey() + 1, left));
      }

      if (right != null) {
        filo.add(Map.entry(current.getKey() + 1, right));
      }
    }

    return sb.toString();
  }
}
