package proto.loader.v4.tests;

import proto.loader.v4.annotations.ProtoGenerableV4;
import proto.loader.v4.annotations.ProtoGeneratorV4;
import proto.loader.v4.annotations.ProtoQualifierV4;

@ProtoGeneratorV4
public class ClassTestGeneratorOneV4 {
  @ProtoGenerableV4
  public String generateAString() {
    return "A string";
  }

  @ProtoQualifierV4("my_string")
  @ProtoGenerableV4
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoQualifierV4("my_integer")
  @ProtoGenerableV4
  public Integer generateAnInteger() {
    return 42;
  }
}
