package proto.loader.v5.tests;

import lombok.Getter;
import proto.loader.v5.annotations.ProtoLoadableV5;
import proto.loader.v5.annotations.ProtoQualifierV5;

@Getter
@ProtoQualifierV5("class-with-constructor")
@ProtoLoadableV5
public class ClassWithConstructorV5 {
  private final String name;

  public ClassWithConstructorV5(final String generateAQualifiedString) {
    this.name = generateAQualifiedString;
  }
}
