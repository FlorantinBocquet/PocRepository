package proto.loader.v5.tests;

import proto.loader.v5.annotations.ProtoConfigV5;
import proto.loader.v5.annotations.ProtoQualifierV5;

@ProtoQualifierV5("Config_class")
@ProtoConfigV5("myProperty6")
public record ProtoConfigClassV5(int first, String second, int[] third) {

  @Override
  public String toString() {
    return "ProtoConfigClassV5{" +
           "first=" + first +
           ", second='" + second + '\'' +
           ", third=" + java.util.Arrays.toString(third) +
           '}';
  }
}
