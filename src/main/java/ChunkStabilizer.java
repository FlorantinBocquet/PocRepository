import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ChunkStabilizer {
  public static void main(String[] args) {

    List<Integer> inputListOfData = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

    final List<ArrayList<Integer>> result = iterate(
        inputListOfData,
        input -> {
          List<Integer> l = new ArrayList<>();
          for (int i = 1; i <= input; i++) {
            l.add(i);
          }
          return l;
        },
        ArrayList::new,
        20
    );

    System.out.println("Result: " + result);
  }

  /**
   * Take a list of data as input.
   * <p>
   * For each item, transform it into a list of items with unknown length with {variableLengthOutputFunction}.
   * <p>
   * Elements returned by the {variableLengthOutputFunction} are stored in a buffer until the buffer contains at least
   * {chunkSize} elements, then {chunkSize} elements are passed to the {bufferedLengthInputFunction}, starting from
   * the oldest elements.
   * <p>
   * If there are remaining elements in the buffer, they are passed to the {bufferedLengthInputFunction}.
   *
   * @param inputListOfData              a list of data to process
   * @param variableLengthOutputFunction a function that takes an item of data and returns a list of items of
   *                                     variable length
   * @param bufferedLengthInputFunction  a function that takes a list of items of fixed length (chunkSize) or
   *                                     (remaining) and returns an item of data
   * @param chunkSize                    the size of the chunks to process
   * @return a list of items of data
   */
  static <I, T, O> List<O> iterate(
      List<I> inputListOfData,
      Function<I, List<T>> variableLengthOutputFunction,
      Function<List<T>, O> bufferedLengthInputFunction,
      int chunkSize
  ) {
    List<T> counted = new ArrayList<>(chunkSize);

    final List<O> result = new ArrayList<>(inputListOfData.size());

    for (I itemOfData : inputListOfData) {
      counted.addAll(variableLengthOutputFunction.apply(itemOfData));

      while (counted.size() >= chunkSize) {
        List<T> chunk = counted.subList(0, chunkSize);

        result.add(bufferedLengthInputFunction.apply(chunk));

        counted = counted.subList(chunkSize, counted.size());
      }
    }

    if (!counted.isEmpty()) {
      result.add(bufferedLengthInputFunction.apply(counted));
    }

    return result;
  }
}
