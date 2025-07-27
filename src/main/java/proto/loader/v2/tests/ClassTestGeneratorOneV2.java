package proto.loader.v2.tests;

import proto.loader.v2.annotations.ProtoGenerableV2;
import proto.loader.v2.annotations.ProtoGeneratorV2;
import proto.loader.v2.annotations.ProtoQualifierV2;

@ProtoGeneratorV2
public class ClassTestGeneratorOneV2 {
  @ProtoGenerableV2
  public String generateAString() {
    return "A string";
  }

  @ProtoQualifierV2("my_string")
  @ProtoGenerableV2
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoQualifierV2("my_integer")
  @ProtoGenerableV2
  public Integer generateAnInteger() {
    return 42;
  }
}
