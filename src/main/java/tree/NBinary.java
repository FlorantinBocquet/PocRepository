package tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;

public class NBinary<V, T extends Comparable<T>> {
  private final Class<T>[] keyClasses;

  private BinaryNode<V, T> root = null;

  @SafeVarargs
  public NBinary(final Class<T>... keyClasses) {
    for (final Class<?> keyClass : keyClasses) {
      if (keyClass == null) {
        throw new IllegalArgumentException("Key class cannot be null");
      }

      if (!Comparable.class.isAssignableFrom(keyClass)) {
        throw new IllegalArgumentException("Key class must implement Comparable");
      }
    }

    this.keyClasses = keyClasses;
  }

  @SafeVarargs
  public final void add(final V value, final T... keys) {
    validateKeys(keys);

    if (root == null) {
      root = of(value, keys, 0);
    } else {
      root.add(value, keys, 0);
    }
  }

  @SafeVarargs
  public final List<V> get(final T... keys) {
    validateKeys(keys);

    if (root == null) {
      return null;
    }

    return root.getData(keys, 0);
  }

  private void validateKeys(final T[] keys) {
    if (keys.length != keyClasses.length) {
      throw new IllegalArgumentException("Number of keys does not match the number of discriminants");
    }

    for (int i = 0; i < keys.length; i++) {
      if (keyClasses[i] != keys[i].getClass()) {
        throw new IllegalArgumentException("Invalid key type for key with index" + i);
      }
    }
  }

  private BinaryNode<V, T> of(final V value, final T[] keys, final int depthIndex) {
    if (keys.length - 1 == depthIndex) {
      return new DeepEndNode(value, keys[depthIndex]);
    } else {
      return new IntermediateNode(value, keys, depthIndex);
    }
  }

  private interface BinaryNode<D, T extends Comparable<T>> {
    void add(final D data, final T[] keys, final int depthIndex);

    List<D> getData(final T[] keys, final int depthIndex);
  }

  private class IntermediateNode implements BinaryNode<V, T> {
    private final T discriminant;
    private IntermediateNode left, right = null;
    private final BinaryNode<V, T> subTreeRoot;

    private IntermediateNode(final V value, final T[] keys, final int levelIndex) {
      this.discriminant = keys[levelIndex];

      this.subTreeRoot = of(value, keys, levelIndex + 1);
    }

    @Override
    public void add(final V value, final T[] keys, final int depthIndex) {
      final T key = keys[depthIndex];
      final int compare = key.compareTo(discriminant);
      if (compare == 0) {
        subTreeRoot.add(value, keys, depthIndex + 1);
      } else if (compare > 0 && left == null) {
        left = new IntermediateNode(value, keys, depthIndex);
      } else if (compare > 0) {
        left.add(value, keys, depthIndex);
      } else if (right == null) {
        right = new IntermediateNode(value, keys, depthIndex);
      } else {
        right.add(value, keys, depthIndex);
      }
    }

    @Override
    public List<V> getData(final T[] keys, final int depthIndex) {
      final T key = keys[depthIndex];

      final int compare = key.compareTo(this.discriminant);

      if (compare == 0) {
        return subTreeRoot.getData(keys, depthIndex + 1);
      } else if (compare > 0) {
        return left != null ? left.getData(keys, depthIndex) : null;
      } else {
        return right != null ? right.getData(keys, depthIndex) : null;
      }
    }
  }

  private class DeepEndNode implements BinaryNode<V, T> {
    private final T discriminant;
    private final List<V> values = new ArrayList<>();
    private DeepEndNode left, right = null;

    public DeepEndNode(final V value, final T discriminant) {
      this.discriminant = discriminant;
      this.values.add(value);
    }

    @Override
    public void add(final V value, final T[] keys, final int depthIndex) {
      if (keys.length - 1 != depthIndex) {
        throw new IllegalArgumentException("DeepEndNode can only handle one key");
      }

      add(value, keys[depthIndex]);
    }

    private void add(final V value, final T key) {
      final int compare = key.compareTo(this.discriminant);
      if (compare == 0) {
        values.add(value);
      } else if (compare > 0 && left == null) {
        left = new DeepEndNode(value, key);
      } else if (compare > 0) {
        left.add(value, key);
      } else if (right == null) {
        right = new DeepEndNode(value, key);
      } else {
        right.add(value, key);
      }
    }

    @Override
    public List<V> getData(final T[] keys, final int depthIndex) {
      if (keys.length - 1 != depthIndex) {
        throw new IllegalArgumentException("DeepEndNode can only handle one key");
      }

      return getData(keys[depthIndex]);
    }

    private List<V> getData(final T key) {
      final int compare = key.compareTo(this.discriminant);

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
}
