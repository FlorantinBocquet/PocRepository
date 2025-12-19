package weighted.selector.weighted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import weighted.selector.rule.Rule;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClassicWeightedSelection<K> extends WeightedSelection<K> {
  @Override
  public List<K> selectItems(
      final String ruleSet,
      final List<K> items,
      final Map<String, Object> data
  ) {
    if (items == null || items.isEmpty()) {
      return Collections.emptyList();
    }

    final Collection<Rule<K>> rules = rulesBook.get(ruleSet);
    if (rules == null || rules.isEmpty()) {
      return Collections.emptyList();
    }

    final Map<String, Object> nullSafeData = new HashMap<>(data == null ? Collections.emptyMap() : data);

    final List<K> remainingItems = new ArrayList<>(items);
    final List<K> selectedItems = new ArrayList<>();

    while (runWhile.test(nullSafeData) && !remainingItems.isEmpty()) {
      final K selectedItem = remainingItems.remove(findBestMatch(rules, remainingItems, nullSafeData));

      selectedItems.add(mapSelection.apply(selectedItem, nullSafeData));
    }

    return selectedItems;
  }

  private record Result(int index, double weight) {
    public static final Result MIN = new Result(-1, Double.NEGATIVE_INFINITY);
  }

  private static <K> int findBestMatch(
      final Collection<Rule<K>> rules,
      final List<K> items,
      final Map<String, Object> data
  ) {
    Result bestMatch = Result.MIN;
    for (int i = 0; i < items.size(); i++) {
      final K item = items.get(i);

      double weight = 0.0;
      for (final Rule<K> rule : rules) {
        if (rule.test(item, data)) {
          weight += rule.computeWeight(item, data);
        }
      }

      if (weight > bestMatch.weight) {
        bestMatch = new Result(i, weight);
      }
    }

    return bestMatch.index;
  }
}
