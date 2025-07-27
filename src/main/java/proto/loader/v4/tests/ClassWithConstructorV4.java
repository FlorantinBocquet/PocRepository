package proto.loader.v4.tests;

import lombok.Getter;
import proto.loader.v4.annotations.ProtoLoadableV4;
import proto.loader.v4.annotations.ProtoQualifierV4;

@Getter
@ProtoQualifierV4("class-with-constructor")
@ProtoLoadableV4
public class ClassWithConstructorV4 {
  private final String name;

  public ClassWithConstructorV4(final String generateAQualifiedString) {
    this.name = generateAQualifiedString;
  }
}
