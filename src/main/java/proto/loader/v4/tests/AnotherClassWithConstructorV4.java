package proto.loader.v4.tests;

import lombok.Getter;
import proto.loader.v4.annotations.ProtoLoadableV4;

@Getter
@ProtoLoadableV4
public class AnotherClassWithConstructorV4 {
  private final ClassTestOneV4 one;
  private final ClassTestTwoV4 two;
  private final ClassWithConstructorV4 three;

  public AnotherClassWithConstructorV4(final ClassTestOneV4 one, final ClassTestTwoV4 two, final ClassWithConstructorV4 three) {
    this.one = one;
    this.two = two;
    this.three = three;
  }
}
