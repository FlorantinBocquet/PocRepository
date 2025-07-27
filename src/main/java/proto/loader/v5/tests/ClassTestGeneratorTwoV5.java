package proto.loader.v5.tests;

import proto.loader.v5.annotations.ProtoGenerableV5;
import proto.loader.v5.annotations.ProtoGeneratorV5;

@ProtoGeneratorV5
public class ClassTestGeneratorTwoV5 {
  @ProtoGenerableV5
  public String generateAString2() {
    return "A string";
  }

  @ProtoGenerableV5
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoGenerableV5
  public Integer generateAnInteger() {
    return 42;
  }
}
