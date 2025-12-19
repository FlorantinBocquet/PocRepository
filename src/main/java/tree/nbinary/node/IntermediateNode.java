package tree.nbinary.node;

import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import tree.nbinary.NBinaryTree;

public class IntermediateNode<V> implements BinaryNode<V> {
  @Getter
  private final Object id;
  @Getter
  private IntermediateNode<V> left, right = null;
  @Getter
  @Setter
  private BinaryNode<V> subTreeRoot;
  @Getter
  @Setter
  private boolean subNeedsRebalancing = false;
  @Getter
  private final Comparator comparator;
  private final NBinaryTree<V> ref;

  public IntermediateNode(
      final V value,
      final Object[] keys,
      final int depthIndex,
      final NBinaryTree<V> ref
  ) {
    this.id = keys[depthIndex];

    this.ref = ref;
    this.subTreeRoot = ref.of(value, keys, depthIndex + 1);
    this.comparator = ref.getComparators().get(depthIndex);
  }

  @Override
  public void setLeft(final BinaryNode<V> left) {
    this.left = (IntermediateNode<V>) left;
  }

  @Override
  public void setRight(final BinaryNode<V> right) {
    this.right = (IntermediateNode<V>) right;
  }

  @Override
  public boolean add(final V value, final Object[] keys, final int depthIndex) {
    final int compare = comparator.compare(this.id, keys[depthIndex]);

    if (compare == 0) {
      if (subTreeRoot.add(value, keys, depthIndex + 1)) {
        subNeedsRebalancing = true;
      }

      return false;
    } else if (compare > 0 && left == null) {
      left = new IntermediateNode<>(value, keys, depthIndex, ref);

      return true;
    } else if (compare > 0) {
      return left.add(value, keys, depthIndex);
    } else if (right == null) {
      right = new IntermediateNode<>(value, keys, depthIndex, ref);

      return true;
    } else {
      return right.add(value, keys, depthIndex);
    }
  }

  @Override
  public List<V> getData(final Object[] keys, final int depthIndex) {
    final int compare = comparator.compare(this.id, keys[depthIndex]);

    if (compare == 0) {
      return subTreeRoot.getData(keys, depthIndex + 1);
    } else if (compare > 0) {
      return left != null ? left.getData(keys, depthIndex) : null;
    } else {
      return right != null ? right.getData(keys, depthIndex) : null;
    }
  }
}
