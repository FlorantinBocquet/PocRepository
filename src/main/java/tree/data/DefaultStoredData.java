package tree.data;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

public class DefaultStoredData<D extends Comparable<D>, I> extends StoredData<D, I> {
  @Getter
  private final List<I> data;

  public DefaultStoredData(final D discriminant, final I data) {
    this.discriminant = discriminant;
    this.data = new LinkedList<>();
    this.data.add(data);
  }

  @Override
  public void manageData(final I inputValue) {
    data.add(inputValue);
  }

  @Override
  public String toString() {
    return data.toString();
  }
}
