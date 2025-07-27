package proto.loader.v6.tests;


import proto.loader.v6.annotations.ProtoConfigV6;
import proto.loader.v6.annotations.ProtoQualifierV6;

@ProtoQualifierV6("Config_class")
@ProtoConfigV6("myProperty6")
public record ProtoConfigClassV6(int first, String second, int[] third) {

  @Override
  public String toString() {
    return "ProtoConfigClassV6{" +
           "first=" + first +
           ", second='" + second + '\'' +
           ", third=" + java.util.Arrays.toString(third) +
           '}';
  }
}
