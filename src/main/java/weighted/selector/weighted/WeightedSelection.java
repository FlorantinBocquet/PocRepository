package weighted.selector.weighted;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import weighted.selector.rule.Rule;

public abstract class WeightedSelection<K> {
  protected final Map<String, Collection<Rule<K>>> rulesBook = new HashMap<>();
  protected Predicate<Map<String, Object>> runWhile = (data) -> true;
  protected BiFunction<K, Map<String, Object>, K> mapSelection = (k, data) -> k;

  public WeightedSelection<K> setRules(final String ruleSet, final Collection<Rule<K>> newRules) {
    rulesBook.put(ruleSet, newRules);

    return this;
  }

  public WeightedSelection<K> runWhile(final Predicate<Map<String, Object>> predicate) {
    this.runWhile = predicate;

    return this;
  }

  public WeightedSelection<K> mapSelected(final BiFunction<K, Map<String, Object>, K> function) {
    this.mapSelection = function;

    return this;
  }

  public List<K> selectItems(
      final String ruleSet,
      final List<K> items
  ) {
    return selectItems(ruleSet, items, null);
  }

  public abstract List<K> selectItems(final String ruleSet, final List<K> items, final Map<String, Object> data);


  public static <K> WeightedSelection<K> getInstance() {
    return new ClassicWeightedSelection<>();
  }

  public static <K> WeightedSelection<K> getInstance(final boolean debug) {
    return debug ? new DebugWeightedSelection<>() : new ClassicWeightedSelection<>();
  }
}
