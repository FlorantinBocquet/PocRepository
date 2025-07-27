package proto.loader.v6.tests;


import proto.loader.v6.annotations.ProtoGenerableV6;
import proto.loader.v6.annotations.ProtoGeneratorV6;
import proto.loader.v6.annotations.ProtoQualifierV6;

@ProtoGeneratorV6
public class ClassTestGeneratorOneV6 {
  @ProtoGenerableV6
  public String generateAString() {
    return "A string";
  }

  @ProtoQualifierV6("my_string")
  @ProtoGenerableV6
  public String generateAQualifiedString() {
    return "A qualified string";
  }

  @ProtoQualifierV6("my_integer")
  @ProtoGenerableV6
  public Integer generateAnInteger() {
    return 42;
  }
}
