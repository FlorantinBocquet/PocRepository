package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
