package proto.loader.v5.tests;

import proto.loader.v5.annotations.ProtoGenerableV5;
import proto.loader.v5.annotations.ProtoGeneratorV5;
import proto.loader.v5.annotations.ProtoQualifierV5;

@ProtoGeneratorV5
public class ClassTestGeneratorOneV5 {
  @ProtoGenerableV5
  public String generateAString() {
    return "A string";
  }

  @ProtoQualifierV5("my_string")
  @ProtoGenerableV5
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoQualifierV5("my_integer")
  @ProtoGenerableV5
  public Integer generateAnInteger() {
    return 42;
  }
}
