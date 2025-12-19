package utils;

import java.util.ArrayList;
import java.util.List;

public class ListSplitter {
  /**
   * A function to split a list in two, taking the first {@code sizeOfLeft} into a list, and putting the remaining in
   * another list. If the list has less than {@code sizeOfLeft} left element, the original list is returned with an
   * empty second list.
   *
   * @param list the list to split in two.
   * @param sizeOfLeft the max size of the first list returned.
   * @param <T> the type of element in the list.
   * @return an array of two lists.
   */
  public static <T> List<T>[] splitList(final List<T> list, final int sizeOfLeft) {
    if (sizeOfLeft >= list.size()) {
      return new List[] {list, List.<T>of()};
    }

    final List<T> first = new ArrayList<>(sizeOfLeft);
    for (int i = 0; i < sizeOfLeft; i++) {
      first.add(list.get(i));
    }

    final List<T> second = new ArrayList<>(list.size() - sizeOfLeft);
    for (int i = sizeOfLeft; i < list.size(); i++) {
      second.add(list.get(i));
    }

    return new List[] {first, second};
  }
}
