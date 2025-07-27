package proto.loader.v3.tests;

import proto.loader.v3.annotations.ProtoGenerableV3;
import proto.loader.v3.annotations.ProtoGeneratorV3;
import proto.loader.v3.annotations.ProtoQualifierV3;

@ProtoGeneratorV3
public class ClassTestGeneratorOneV3 {
  @ProtoGenerableV3
  public String generateAString() {
    return "A string";
  }

  @ProtoQualifierV3("my_string")
  @ProtoGenerableV3
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoQualifierV3("my_integer")
  @ProtoGenerableV3
  public Integer generateAnInteger() {
    return 42;
  }
}
