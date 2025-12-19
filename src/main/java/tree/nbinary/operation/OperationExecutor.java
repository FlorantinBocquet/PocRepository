package tree.nbinary.operation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import tree.nbinary.node.BinaryNode;
import tree.nbinary.node.DeepEndNode;
import tree.nbinary.node.IntermediateNode;

public class OperationExecutor<V> {
  private static final int[][] operations = {
      // value of node compared to value passed
      // -1 0 1 (validate current, check left, check right) as bits: (v << 2) | (l << 1) | r
      // left node value is < to current node value, right node value is > to current node value
      // ALL
      {0b111, 0b111, 0b111},
      // EQUALS
      {0b001, 0b100, 0b010},
      // NOT_EQUALS
      {0b111, 0b011, 0b111},
      // SUPERIOR_STRICT
      {0b001, 0b001, 0b111},
      // SUPERIOR_OR_EQUALS
      {0b001, 0b101, 0b111},
      // INFERIOR_STRICT
      {0b111, 0b010, 0b010},
      // INFERIOR_OR_EQUALS
      {0b111, 0b110, 0b010}
  };

  @NotNull
  public List<BinaryNode<V>> runIntermediate(
      final IntermediateNode<V> levelRootNode,
      final int operation,
      final Object[] values
  ) {
    return validate(levelRootNode, values, operation, IntermediateNode::getSubTreeRoot, List::add);
  }

  @NotNull
  public List<V> runDeepEnd(
      final DeepEndNode<V> levelRootNode,
      final int operation,
      final Object[] values
  ) {
    return validate(levelRootNode, values, operation, DeepEndNode::getValues, List::addAll);
  }

  private static <V> int check(final BinaryNode<V> node, final Object[] values, final int operation) {
    if (operation == 0) {
      return 0b111; // ALL
    }

    int checks = 0;

    // the only case with multiple values is the IN case, represented as EQUALS with multiple values
    for (Object value : values) {
      int comp = node.getComparator().compare(node.getId(), value);
      int reSizeComp = Math.max(-1, Math.min(1, comp)) + 1; // to be in [0,2]
      int currentChecks = operations[operation][reSizeComp];
      checks |= currentChecks;
    }

    return checks;
  }

  private   <X, Y, N extends BinaryNode<V>> List<X> validate(
      final N root,
      final Object[] values,
      final int operation,
      final Function<N, Y> selector,
      final BiConsumer<List<X>, Y> collector
  ) {
    final List<X> results = new ArrayList<>();

    final List<BinaryNode<V>> toTreat = new LinkedList<>();
    toTreat.add(root);

    while (!toTreat.isEmpty()) {
      final BinaryNode<V> currentNode = toTreat.remove(0);

      final int checks = check(currentNode, values, operation);
      if ((checks & 0b100) == 0b100) {
        selectAndAdd(selector, collector, currentNode, results);
      }
      if ((checks & 0b010) == 0b010 && currentNode.getLeft() != null) {
        toTreat.add(currentNode.getLeft());
      }
      if ((checks & 0b001) == 0b001 && currentNode.getRight() != null) {
        toTreat.add(currentNode.getRight());
      }
    }

    return results;
  }

  private <X, Y, N extends BinaryNode<V>> void selectAndAdd(
      final Function<N, Y> selector,
      final BiConsumer<List<X>, Y> collector,
      final BinaryNode<V> currentNode,
      final List<X> results
  ) {
    final Y found = selector.apply((N) currentNode);
    if (found != null) {
      collector.accept(results, found);
    }
  }
}
