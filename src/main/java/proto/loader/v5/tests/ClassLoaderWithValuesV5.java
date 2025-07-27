package proto.loader.v5.tests;

import lombok.Getter;
import proto.loader.v5.annotations.ProtoLoadableV5;
import proto.loader.v5.annotations.ProtoQualifierV5;
import proto.loader.v5.annotations.ProtoValueV5;

@Getter
@ProtoQualifierV5("loadedByValueQualifier")
@ProtoLoadableV5
public class ClassLoaderWithValuesV5 {
  private final int myProperty1;
  private final boolean myProperty2;
  private final String myProperty3;
  private final String myProperty4;
  private final double myProperty5;
  private final Integer myProperty6;
  private final String myProperty7;


  public ClassLoaderWithValuesV5(
      @ProtoValueV5("myProperty1") final int myProperty1,
      @ProtoValueV5("myProperty2") final boolean myProperty2,
      @ProtoValueV5("myProperty3") final String myProperty3,
      @ProtoValueV5("myProperty4") final String myProperty4,
      @ProtoValueV5("myProperty5") final double myProperty5,
      @ProtoValueV5("myProperty6.first") final Integer myProperty6,
      @ProtoValueV5("myProperty6.second") final String myProperty7
  ) {
    this.myProperty1 = myProperty1;
    this.myProperty2 = myProperty2;
    this.myProperty3 = myProperty3;
    this.myProperty4 = myProperty4;
    this.myProperty5 = myProperty5;
    this.myProperty6 = myProperty6;
    this.myProperty7 = myProperty7;
  }

  @Override
  public String toString() {
    return "ClassLoaderWithValuesV5{" +
           "myProperty1=" + myProperty1 +
           ", myProperty2=" + myProperty2 +
           ", myProperty3='" + myProperty3 + '\'' +
           ", myProperty4='" + myProperty4 + '\'' +
           ", myProperty5=" + myProperty5 +
           ", myProperty6=" + myProperty6 +
           ", myProperty7='" + myProperty7 + '\'' +
           '}';
  }
}
