package weighted.selector.rule;

import java.util.Map;

public interface Rule<K> {
  boolean test(final K item, final Map<String, Object> data);

  double computeWeight(final K item, final Map<String, Object> data);

  /**
   * Get the name of the rule. By default, it returns the simple class name. Used for debug purposes.
   *
   * @return the name of the rule
   */
  default String name() {
    return this.getClass().getSimpleName();
  }
}