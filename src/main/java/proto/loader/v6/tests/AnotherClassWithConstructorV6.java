package proto.loader.v6.tests;

import lombok.Getter;
import proto.loader.v6.annotations.ProtoLoadableV6;

@Getter
@ProtoLoadableV6
public class AnotherClassWithConstructorV6 {
  private final ClassTestOneV6 one;
  private final ClassTestTwoV6 two;
  private final ClassWithConstructorV6 three;

  public AnotherClassWithConstructorV6(final ClassTestOneV6 one, final ClassTestTwoV6 two, final ClassWithConstructorV6 three) {
    this.one = one;
    this.two = two;
    this.three = three;
  }
}
