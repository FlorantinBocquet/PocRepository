package tree.nbinary.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationType {
  ALL(0),
  EQUALS(1),
  NOT_EQUALS(2),
  SUPERIOR_STRICT(3),
  SUPERIOR_OR_EQUALS(4),
  INFERIOR_STRICT(5),
  INFERIOR_OR_EQUALS(6);

  private final int value;
}
