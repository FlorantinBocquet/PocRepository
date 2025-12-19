package tree.data;

import lombok.Getter;

public class IntStoredData<D extends Comparable<D>> extends StoredData<D, Integer> {
  @Getter
  private Integer data;

  public IntStoredData(final D discriminant, final Integer data) {
    this.discriminant = discriminant;
    this.data = data;
  }

  @Override
  public void manageData(final Integer inputValue) {
    data = inputValue;
  }

  @Override
  public String toString() {
    return data.toString();
  }
}