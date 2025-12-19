package tree;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InputData<D extends Comparable<D>, I> {
  private D discriminant;
  private I inputValue;
}
