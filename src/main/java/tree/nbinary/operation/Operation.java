package tree.nbinary.operation;


import lombok.Getter;

@Getter
public final class Operation {
  private final OperationType type;
  private final Object[] keys;

  private Operation(final OperationType type, final Object[] keys) {
    this.type = type;
    this.keys = keys;

    if (keys == null) {
      throw new IllegalArgumentException("At least one key must be provided");
    }

    for (Object key : keys) {
      if (key == null) {
        throw new IllegalArgumentException("Keys cannot be null");
      }
    }
  }

  public static Operation all() {
    return new Operation(OperationType.ALL, new Object[] {});
  }

  public static Operation equalz(final Object key) {
    return new Operation(OperationType.EQUALS, new Object[] {key});
  }

  public static Operation notEquals(final Object key) {
    return new Operation(OperationType.NOT_EQUALS, new Object[] {key});
  }

  public static Operation in(final Object... keys) {
    return new Operation(OperationType.EQUALS, keys);
  }

  public static Operation superiorStrict(final Object key) {
    return new Operation(OperationType.SUPERIOR_STRICT, new Object[] {key});
  }

  public static Operation superiorOrEquals(final Object key) {
    return new Operation(OperationType.SUPERIOR_OR_EQUALS, new Object[] {key});
  }

  public static Operation inferiorStrict(final Object key) {
    return new Operation(OperationType.INFERIOR_STRICT, new Object[] {key});
  }

  public static Operation inferiorOrEquals(final Object key) {
    return new Operation(OperationType.INFERIOR_OR_EQUALS, new Object[] {key});
  }
}
