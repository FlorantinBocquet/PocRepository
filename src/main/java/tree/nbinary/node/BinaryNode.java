package tree.nbinary.node;

import java.util.Comparator;
import java.util.List;

public interface BinaryNode<D> {
  Object getId();

  BinaryNode<D> getLeft();

  void setLeft(final BinaryNode<D> left);

  BinaryNode<D> getRight();

  void setRight(final BinaryNode<D> right);

  Comparator getComparator();

  /**
   *
   * @return true if a node was created, indicating a possible need for rebalancing for a given level
   */
  boolean add(final D data, final Object[] keys, final int depthIndex);

  List<D> getData(final Object[] keys, final int depthIndex);
}

