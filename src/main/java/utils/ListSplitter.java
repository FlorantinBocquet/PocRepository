package utils;

import java.util.ArrayList;
import java.util.List;

public class ListSplitter {
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
