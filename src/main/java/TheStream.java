import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TheStream {
  public String theFunction() {
    return Stream.of(
        List.of(new int[] {0, 0}, new int[] {1, 0}, new int[] {2, 2}),
        List.of(new int[] {0, 2}, new int[] {1, 0}, new int[] {0, 1}),
        List.of(new int[] {1, 2}, new int[] {2, 1}, new int[] {1, 1}, new int[] {2, 0}, new int[] {1, 0})
    ).map(it ->
        it.stream()
            .map(i -> new int[][][] {
                {{-11, -9, 4, -13}, {-14, -3, 5, -4}, {-6, -13, 2}},
                {{7, -3, 3}, {-17, -4, -14}, {2, 3, 0, -4}},
                {{-14, -13, 1, -13, 0, 2}, {-17, 0, -3, 3, -4, -14}, {3, -2}}
            }[i[0]][i[1]])
            .map(a -> Arrays.stream(a).boxed())
            .map(s -> s.map(ick -> (char) ('r' + ick) + "").collect(Collectors.joining("")))
            .collect(Collectors.joining(
                " ",
                Arrays.stream(new int[][] {
                    {13, -9, 17, -17, 13},
                    {6, 8, -1, 0, -13}
                }).map(z ->
                    Arrays.stream(z).boxed().reduce(
                        new AbstractMap.SimpleEntry<>(0, ""),
                        (acc, in) -> {
                          final int stley = acc.getKey() + in;
                          return new AbstractMap.SimpleEntry<>(stley, acc.getValue() + (char) ('a' + stley));
                        },
                        (a, b) -> a
                    ).getValue()
                ).collect(Collectors.joining(" ", "", " ")),
                ""
            ))
    ).collect(Collectors.joining(", "));
  }
}
