package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ChunkStabilizerTest {
  /*
   * Input data
   */
  public static final List<Integer> INPUT_LIST_OF_DATA = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

  /*
   * Arbitrary size list creator. For the test, create a list of element the size of the inputted number form 1 to the the value.
   */
  public static final Function<Integer, List<Integer>> INTEGER_LIST_FUNCTION = input -> {
    List<Integer> l = new ArrayList<>();
    for (int i = 1; i <= input; i++) {
      l.add(i);
    }
    return l;
  };

  @Test
  void stabilizeFor10() {
    final List<List<Integer>> expectedResult = List.of(
        List.of(1, 1, 2, 1, 2, 3, 1, 2, 3, 4),
        List.of(1, 2, 3, 4, 5, 1, 2, 3, 4, 5),
        List.of(6, 1, 2, 3, 4, 5, 6, 7, 1, 2),
        List.of(3, 4, 5, 6, 7, 8, 1, 2, 3, 4),
        List.of(5, 6, 7, 8, 9)
    );

    final List<ArrayList<Integer>> result = ChunkStabilizer.iterate(
        INPUT_LIST_OF_DATA,
        INTEGER_LIST_FUNCTION,
        ArrayList::new,
        10
    );

    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void stabilizeFor20() {
    final List<List<Integer>> expectedResult = List.of(
        List.of(1, 1, 2, 1, 2, 3, 1, 2, 3, 4, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5),
        List.of(6, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4),
        List.of(5, 6, 7, 8, 9)
    );

    final List<ArrayList<Integer>> result = ChunkStabilizer.iterate(
        INPUT_LIST_OF_DATA,
        INTEGER_LIST_FUNCTION,
        ArrayList::new,
        20
    );

    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void failingTest() {
    final List<List<Integer>> expectedResult = List.of();

    final List<ArrayList<Integer>> result = ChunkStabilizer.iterate(
        INPUT_LIST_OF_DATA,
        INTEGER_LIST_FUNCTION,
        ArrayList::new,
        20
    );

    Assertions.assertEquals(expectedResult, result);
  }
}
