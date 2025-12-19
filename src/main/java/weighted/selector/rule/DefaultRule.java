package weighted.selector.rule;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.ToDoubleBiFunction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultRule<K> implements Rule<K> {
  @NonNull
  private final BiPredicate<K, Map<String, Object>> predicate;
  @NonNull
  private final ToDoubleBiFunction<K, Map<String, Object>> function;

  private String ruleName = null;

  @Override
  public boolean test(final K item, final Map<String, Object> data) {
    return predicate.test(item, data);
  }

  @Override
  public double computeWeight(final K item, final Map<String, Object> data) {
    return function.applyAsDouble(item, data);
  }

  public DefaultRule<K> setRuleName(String name) {
    this.ruleName = name;
    return this;
  }

  @Override
  public String name() {
    return ruleName != null ? ruleName : Rule.super.name();
  }

  @Override
  public String toString() {
    return name();
  }
}
