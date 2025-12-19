package tree.data;

import tree.Binary;

public class DefaultSubBinaryStoredData<
    D extends Comparable<D>,
    K extends Comparable<K>,
    V
    > extends StoredData<D, DefaultSubBinaryStoredData.ValueInput<K, V>> {

  public record ValueInput<K, V>(K discriminant, V inputValue) {
  }

  private final Binary<K, V, DefaultStoredData<K, V>> binary;

  public DefaultSubBinaryStoredData(
      final D discriminant,
      final DefaultSubBinaryStoredData.ValueInput<K, V> inputValue
  ) {
    this.discriminant = discriminant;
    this.binary = Binary.binary();

    manageData(inputValue);
  }

  @Override
  public void manageData(final DefaultSubBinaryStoredData.ValueInput<K, V> inputValue) {
    binary.addData(inputValue.discriminant, inputValue.inputValue);
  }
}
