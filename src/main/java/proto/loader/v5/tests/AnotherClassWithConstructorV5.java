package proto.loader.v5.tests;

import lombok.Getter;
import proto.loader.v5.annotations.ProtoLoadableV5;

@Getter
@ProtoLoadableV5
public class AnotherClassWithConstructorV5 {
  private final ClassTestOneV5 one;
  private final ClassTestTwoV5 two;
  private final ClassWithConstructorV5 three;

  public AnotherClassWithConstructorV5(final ClassTestOneV5 one, final ClassTestTwoV5 two, final ClassWithConstructorV5 three) {
    this.one = one;
    this.two = two;
    this.three = three;
  }
}
