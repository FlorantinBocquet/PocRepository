package proto.loader.v2.tests;

import proto.loader.v2.annotations.ProtoGenerableV2;
import proto.loader.v2.annotations.ProtoGeneratorV2;
import proto.loader.v2.annotations.ProtoQualifierV2;

@ProtoGeneratorV2
public class ClassTestGeneratorTwoV2 {
  @ProtoGenerableV2
  public String generateAString2() {
    return "A string";
  }

  @ProtoQualifierV2("my_string_2")
  @ProtoGenerableV2
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoGenerableV2
  public Integer generateAnInteger() {
    return 42;
  }
}
