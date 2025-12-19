package tree.nbinary.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;

public class DeepEndNode<V> implements BinaryNode<V> {
  @Getter
  private final Object id;
  @Getter
  private final List<V> values = new ArrayList<>();
  @Getter
  private DeepEndNode<V> left, right = null;
  @Getter
  private final Comparator comparator;

  public DeepEndNode(final V value, final Object key, final Comparator comparator) {
    this.id = key;
    this.values.add(value);
    this.comparator = comparator;
  }

  @Override
  public void setLeft(final BinaryNode<V> left) {
    this.left = (DeepEndNode<V>) left;
  }

  @Override
  public void setRight(final BinaryNode<V> right) {
    this.right = (DeepEndNode<V>) right;
  }


  @Override
  public boolean add(final V value, final Object[] keys, final int depthIndex) {
    if (keys.length - 1 != depthIndex) {
      throw new IllegalArgumentException("DeepEndNode can only handle final depth");
    }

    return add(value, keys[depthIndex]);
  }

  private boolean add(final V value, final Object key) {
    final int compare = comparator.compare(this.id, key);

    if (compare == 0) {
      values.add(value);

      return false;
    } else if (compare > 0 && left == null) {
      left = new DeepEndNode<>(value, key, comparator);

      return true;
    } else if (compare > 0) {
      return left.add(value, key);
    } else if (right == null) {
      right = new DeepEndNode<>(value, key, comparator);

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
    final int compare = comparator.compare(this.id, key);

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
    return String.valueOf(id);
  }
}
