package tree.data;

import lombok.Getter;

public abstract class StoredData<D extends Comparable<D>, I> {
  @Getter
  protected D discriminant;

  public abstract void manageData(final I inputValue);
}
