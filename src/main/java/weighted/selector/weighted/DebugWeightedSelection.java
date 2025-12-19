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
public class DebugWeightedSelection<K> extends WeightedSelection<K> {
  @Override
  public List<K> selectItems(
      final String ruleSet,
      final List<K> items,
      final Map<String, Object> data
  ) {
    final StringBuilder builder = new StringBuilder("Selection of item using rule set '").append(ruleSet).append("'");

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
      builder
          .append("\n//////////////////////////////////")
          .append("\nA new cycle of selection with ").append(remainingItems.size()).append(" remaining items begin");

      final int bestResultIndex = findBestMatch(rules, remainingItems, nullSafeData, builder);
      final K selectedItem = remainingItems.remove(bestResultIndex);
      final K finalItem = mapSelection.apply(selectedItem, nullSafeData);

      builder
          .append("\n  Item selected : ").append(selectedItem)
          .append("\n  Item after mapping: ").append(finalItem);

      selectedItems.add(finalItem);
    }

    builder
        .append("\n//////////////////////////////////")
        .append("\nSelection completed. Selected ").append(selectedItems.size()).append(" items in total.");
    System.out.println(builder);

    return selectedItems;
  }

  private record Result(int index, double weight) {
    public static final Result MIN = new Result(-1, Double.NEGATIVE_INFINITY);
  }

  private static <K> int findBestMatch(
      final Collection<Rule<K>> rules,
      final List<K> items,
      final Map<String, Object> data,
      final StringBuilder builder
  ) {
    Result bestMatch = Result.MIN;
    for (int i = 0; i < items.size(); i++) {
      final K item = items.get(i);

      builder.append("\n  Evaluating item ").append(i).append(": ").append(item);

      double weight = 0.0;
      for (final Rule<K> rule : rules) {
        if (rule.test(item, data)) {
          final double ruleWeight = rule.computeWeight(item, data);

          weight += ruleWeight;

          builder.append("\n      - Rule : ").append(rule).append(", Weight: ").append(ruleWeight);
        } else {
          builder.append("\n      - Rule : ").append(rule).append(", not applicable");
        }
      }

      builder.append("\n    Final weight: ").append(weight);
      builder.append("\n-------------------------");

      if (weight > bestMatch.weight) {
        bestMatch = new Result(i, weight);
      }
    }

    return bestMatch.index;
  }
}
