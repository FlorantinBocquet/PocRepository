package proto.loader.v4.tests;

import proto.loader.v4.annotations.ProtoGenerableV4;
import proto.loader.v4.annotations.ProtoGeneratorV4;

@ProtoGeneratorV4
public class ClassTestGeneratorTwoV4 {
  @ProtoGenerableV4
  public String generateAString2() {
    return "A string";
  }

  @ProtoGenerableV4
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoGenerableV4
  public Integer generateAnInteger() {
    return 42;
  }
}
