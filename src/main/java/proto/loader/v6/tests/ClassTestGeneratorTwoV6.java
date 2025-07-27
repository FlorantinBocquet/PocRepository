package proto.loader.v6.tests;


import proto.loader.v6.annotations.ProtoGenerableV6;
import proto.loader.v6.annotations.ProtoGeneratorV6;

@ProtoGeneratorV6
public class ClassTestGeneratorTwoV6 {
  @ProtoGenerableV6
  public String generateAString2() {
    return "A string";
  }

  @ProtoGenerableV6
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoGenerableV6
  public Integer generateAnInteger() {
    return 42;
  }
}
