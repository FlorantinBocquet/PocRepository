package wrapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Collector<E> {
  private final Iterator<E> iterator;

  public Collector(final Iterator<E> iterator) {
    this.iterator = iterator;
  }

  public static <E> Collector<E> of(final Iterator<E> iterator) {
    return new Collector<>(iterator);
  }

  public List<E> asList() {
    final List<E> result = new ArrayList<>();

    while (iterator.hasNext()) {
      result.add(iterator.next());
    }

    return result;
  }

  public <O> List<O> asList(final Function<E, O> transformation) {
    final List<O> result = new ArrayList<>();

    while (iterator.hasNext()) {
      result.add(transformation.apply(iterator.next()));
    }

    return result;
  }

  public Set<E> asSet() {
    final Set<E> result = new HashSet<>();

    while (iterator.hasNext()) {
      result.add(iterator.next());
    }

    return result;
  }

  public <O> Set<O> asSet(final Function<E, O> transformation) {
    final Set<O> result = new HashSet<>();

    while (iterator.hasNext()) {
      result.add(transformation.apply(iterator.next()));
    }

    return result;
  }

  public <K> Map<K, E> asMap(final Function<E, K> keyBuilder) {
    final Map<K, E> result = new HashMap<>();

    while (iterator.hasNext()) {
      final E next = iterator.next();
      result.put(keyBuilder.apply(next), next);
    }

    return result;
  }

  public <K, V> Map<K, V> asMap(final Function<E, K> keyBuilder, final Function<E, V> valueMapper) {
    final Map<K, V> result = new HashMap<>();

    while (iterator.hasNext()) {
      final E next = iterator.next();
      result.put(keyBuilder.apply(next), valueMapper.apply(next));
    }

    return result;
  }

  public <K, V> Map<K, List<V>> groupBy(final Function<E, K> selector, final Function<E, V> valueMapper) {
    final Map<K, List<V>> result = new HashMap<>();

    while (iterator.hasNext()) {
      final E element = iterator.next();
      final K key = selector.apply(element);

      result.computeIfAbsent(key, k -> new ArrayList<>()).add(valueMapper.apply(element));
    }

    return result;
  }

  public <K> Map<K, List<E>> groupBy(final Function<E, K> selector) {
    return groupBy(selector, Function.identity());
  }

  public <V> Map<Boolean, List<V>> partitionBy(final Predicate<E> selector, final Function<E, V> valueMapper) {
    final Map<Boolean, List<V>> result = new HashMap<>();
    result.put(true, new ArrayList<>());
    result.put(false, new ArrayList<>());

    while (iterator.hasNext()) {
      final E element = iterator.next();
      final Boolean key = selector.test(element);

      result.get(key).add(valueMapper.apply(element));
    }

    return result;
  }

  public Map<Boolean, List<E>> partitionBy(final Predicate<E> selector) {
    return partitionBy(selector, Function.identity());
  }

  public Optional<E> first() {
    return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
  }

  public Optional<E> firstBy(final Predicate<E> predicate) {
    while (iterator.hasNext()) {
      final E next = iterator.next();

      if (predicate.test(next)) {
        return Optional.of(next);
      }
    }

    return Optional.empty();
  }

  public boolean any() {
    return iterator.hasNext();
  }

  public boolean any(final Predicate<E> predicate) {
    while (iterator.hasNext()) {
      if (predicate.test(iterator.next())) {
        return true;
      }
    }

    return false;
  }

  public boolean all(final Predicate<E> predicate) {
    while (iterator.hasNext()) {
      if (!predicate.test(iterator.next())) {
        return false;
      }
    }

    return true;
  }

  public void foEach(final Consumer<E> action) {
    while (iterator.hasNext()) {
      action.accept(iterator.next());
    }
  }
}
