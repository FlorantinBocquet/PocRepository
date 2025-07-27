package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamPartitioner {
  public static <T> Stream<List<T>> partition(final List<T> items, final int batchSize) {
    return IntStream
        .range(0, (int) Math.ceil(items.size() * 1.0 / batchSize))
        .mapToObj(partitioner(items, batchSize));
  }

  public static <T> Stream<List<T>> parallelPartition(final List<T> items, final int batchSize) {
    return IntStream
        .range(0, (int) Math.ceil(items.size() * 1.0 / batchSize))
        .parallel()
        .mapToObj(partitioner(items, batchSize));
  }

  private static <T> IntFunction<List<T>> partitioner(final List<T> items, final int batchSize) {
    return batchIndex -> {
      final List<T> partition = new ArrayList<>(batchSize);

      final int stopper = Math.min(items.size(), (batchIndex + 1) * batchSize);

      for (int subIndex = batchIndex * batchSize; subIndex < stopper; subIndex++) {
        partition.add(items.get(subIndex));
      }

      return partition;
    };
  }
}
