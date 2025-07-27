package wrapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

final class Transform<I, O> implements Iterator<O> {
  private final Iterator<I> previousStep;
  private final Function<I, O> function;

  private O foundNext = null;

  public Transform(final Iterator<I> previousStep, final Function<I, O> transformation) {
    this.previousStep = previousStep;
    this.function = transformation;
  }

  @Override
  public O next() {
    if (hasNext()) {
      final O result = foundNext;
      foundNext = null;
      return result;
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public boolean hasNext() {
    if (foundNext != null) {
      return true;
    }

    while (previousStep.hasNext()) {
      final O next = function.apply(previousStep.next());

      if (next != null) {
        foundNext = next;
        return true;
      }
    }

    return false;
  }
}

final class FlatTransform<I, O> implements Iterator<O> {
  private final Iterator<I> previousStep;
  private final Function<I, Iterable<O>> function;

  private Iterator<O> foundNext = Collections.emptyListIterator();

  public FlatTransform(final Iterator<I> previousStep, final Function<I, Iterable<O>> transformation) {
    this.previousStep = previousStep;
    this.function = transformation;
  }

  @Override
  public O next() {
    if (hasNext()) {
      return foundNext.next();
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public boolean hasNext() {
    if (foundNext.hasNext()) {
      return true;
    }

    while (previousStep.hasNext()) {
      final Iterable<O> next = function.apply(previousStep.next());

      if (next != null) {
        final Iterator<O> nextIterator = next.iterator();

        if (nextIterator.hasNext()) {
          this.foundNext = nextIterator;

          return true;
        }
      }
    }

    return false;
  }
}

final class Filter<I> implements Iterator<I> {
  private final Iterator<I> previousStep;
  private final Predicate<I> predicate;

  private I foundNext = null;

  public Filter(final Iterator<I> previousStep, final Predicate<I> predicate) {
    this.previousStep = previousStep;
    this.predicate = predicate;
  }

  @Override
  public I next() {
    if (hasNext()) {
      final I result = foundNext;
      this.foundNext = null;
      return result;
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public boolean hasNext() {
    if (foundNext != null) {
      return true;
    }

    while (previousStep.hasNext()) {
      final I next = previousStep.next();

      if (next != null && predicate.test(next)) {
        this.foundNext = next;
        return true;
      }
    }

    return false;
  }
}

final class Peek<I> implements Iterator<I> {
  private final Iterator<I> previousStep;
  private final Consumer<I> peek;

  public Peek(final Iterator<I> previousStep, final Consumer<I> peek) {
    this.previousStep = previousStep;
    this.peek = peek;
  }

  @Override
  public I next() {
    if (hasNext()) {
      final I next = previousStep.next();
      peek.accept(next);
      return next;
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public boolean hasNext() {
    return previousStep.hasNext();
  }
}

final class GroupBy<I, K, V> implements Iterator<Map.Entry<K, List<V>>> {
  private final Iterator<Map.Entry<K, List<V>>> iterator;

  public GroupBy(final Iterator<I> previousStep, final Function<I, K> selector, final Function<I, V> valueMapper) {
    final Map<K, List<V>> map = new HashMap<>();

    while (previousStep.hasNext()) {
      final I element = previousStep.next();

      final K key = selector.apply(element);
      final V value = valueMapper.apply(element);

      if (value != null) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
      }
    }

    iterator = map.entrySet().iterator();
  }

  @Override
  public Map.Entry<K, List<V>> next() {
    if (iterator.hasNext()) {
      return iterator.next();
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }
}

final class PartitionBy<I, V> implements Iterator<Map.Entry<Boolean, List<V>>> {
  private final Iterator<Map.Entry<Boolean, List<V>>> iterator;

  public PartitionBy(final Iterator<I> previousStep, final Predicate<I> selector, final Function<I, V> valueMapper) {
    final Map<Boolean, List<V>> map = new HashMap<>();
    map.put(true, new ArrayList<>());
    map.put(false, new ArrayList<>());

    while (previousStep.hasNext()) {
      final I element = previousStep.next();

      final Boolean key = selector.test(element);
      final V value = valueMapper.apply(element);

      if (value != null) {
        map.get(key).add(value);
      }
    }

    iterator = map.entrySet().iterator();
  }

  @Override
  public Map.Entry<Boolean, List<V>> next() {
    if (iterator.hasNext()) {
      return iterator.next();
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }
}

final class DistinctBy<E, V> implements Iterator<E> {
  private final Iterator<E> previousStep;
  private final Function<E, V> distinctBy;

  private final HashSet<V> seen = new HashSet<>();

  private E foundNext = null;

  public DistinctBy(final Iterator<E> previousStep, final Function<E, V> distinctBy) {
    this.previousStep = previousStep;
    this.distinctBy = distinctBy;
  }

  @Override
  public E next() {
    if (hasNext()) {
      final E result = foundNext;
      foundNext = null;
      return result;
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override
  public boolean hasNext() {
    if (foundNext != null) {
      return true;
    }

    while (previousStep.hasNext() && foundNext == null) {
      final E item = previousStep.next();
      final V discriminant = distinctBy.apply(item);

      if (!seen.contains(discriminant)) {
        foundNext = item;
        seen.add(discriminant);

        return true;
      }
    }

    return false;
  }
}
