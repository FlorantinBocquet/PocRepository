package wrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Wrapper<E> {
  private final Iterator<E> iterator;

  private Wrapper(final Iterator<E> iterator) {
    this.iterator = iterator;
  }

  public static <E> Wrapper<E> of(final Iterable<E> iterable) {
    return new Wrapper<>(iterable.iterator());
  }

  public static <E> Wrapper<E> of(final Iterator<E> iterator) {
    return new Wrapper<>(iterator);
  }

  @SafeVarargs
  public static <E> Wrapper<E> of(final E... elements) {
    return new Wrapper<>(List.of(elements).iterator());
  }

  public <O> Wrapper<O> map(final Function<E, O> transform) {
    final Transform<E, O> operation = new Transform<>(iterator, transform);

    return new Wrapper<>(operation);
  }

  public <O> Wrapper<O> flatMap(final Function<E, Iterable<O>> transform) {
    final FlatTransform<E, O> operation = new FlatTransform<>(iterator, transform);

    return new Wrapper<>(operation);
  }

  public <O> Wrapper<O> flatten(final Function<E, Wrapper<O>> transform) {
    final FlatTransform<E, O> operation = new FlatTransform<>(iterator, e -> transform.apply(e).collect().asList());

    return new Wrapper<>(operation);
  }

  public Wrapper<E> filter(final Predicate<E> predicate) {
    final Filter<E> operation = new Filter<>(iterator, predicate);

    return new Wrapper<>(operation);
  }

  public Wrapper<E> peek(final Consumer<E> action) {
    final Peek<E> operation = new Peek<>(iterator, action);

    return new Wrapper<>(operation);
  }

  public <K> Wrapper<Map.Entry<K, List<E>>> groupBy(final Function<E, K> selector) {
    final GroupBy<E, K, E> operation = new GroupBy<>(iterator, selector, Function.identity());

    return new Wrapper<>(operation);
  }

  public <K, V> Wrapper<Map.Entry<K, List<V>>> groupBy(
      final Function<E, K> selector,
      final Function<E, V> valueMapper
  ) {
    final GroupBy<E, K, V> operation = new GroupBy<>(iterator, selector, valueMapper);

    return new Wrapper<>(operation);
  }

  public Wrapper<Map.Entry<Boolean, List<E>>> partitionBy(final Predicate<E> predicate) {
    final PartitionBy<E, E> operation = new PartitionBy<>(iterator, predicate, Function.identity());

    return new Wrapper<>(operation);
  }

  public <V> Wrapper<Map.Entry<Boolean, List<V>>> partitionBy(
      final Predicate<E> predicate,
      final Function<E, V> valueMapper
  ) {
    final PartitionBy<E, V> operation = new PartitionBy<>(iterator, predicate, valueMapper);

    return new Wrapper<>(operation);
  }

  public <O> Wrapper<E> distinctBy(final Function<E, O> distinctBy) {
    final DistinctBy<E, O> operation = new DistinctBy<>(iterator, distinctBy);

    return new Wrapper<>(operation);
  }

  public Wrapper<E> distinct() {
    return distinctBy(Function.identity());
  }

  public Collector<E> collect() {
    return new Collector<>(iterator);
  }
}
