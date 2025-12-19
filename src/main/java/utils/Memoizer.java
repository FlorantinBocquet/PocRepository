package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A simple object executing code and keeping the result in a local map to skip future executions. It takes a function
 * as constructor parameter then will execute it when calling {@code Memoizer.compute()}.
 *
 * @param <K> the type of the input to the function
 * @param <V> the type of the result of the function
 */
public class Memoizer<K, V> {
  private final Map<K, V> cache = new ConcurrentHashMap<>();

  private final Function<K, V> function;

  public Memoizer(final Function<K, V> function) {
    this.function = function;
  }

  public V compute(final K key) {
    return cache.computeIfAbsent(key, function);
  }
}
