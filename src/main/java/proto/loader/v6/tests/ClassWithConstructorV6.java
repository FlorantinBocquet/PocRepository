package proto.loader.v6.tests;

import lombok.Getter;
import proto.loader.v6.annotations.ProtoLoadableV6;
import proto.loader.v6.annotations.ProtoQualifierV6;

@Getter
@ProtoQualifierV6("class-with-constructor")
@ProtoLoadableV6
public class ClassWithConstructorV6 {
  private final String name;

  public ClassWithConstructorV6(final String generateAQualifiedString) {
    this.name = generateAQualifiedString;
  }
}
