package proto.loader.v6.tests;

import lombok.Getter;
import proto.loader.v6.annotations.ProtoLoadableV6;
import proto.loader.v6.annotations.ProtoQualifierV6;
import proto.loader.v6.annotations.ProtoValueV6;

@Getter
@ProtoQualifierV6("loadedByValueQualifier")
@ProtoLoadableV6
public class ClassLoaderWithValuesV6 {
  private final int myProperty1;
  private final boolean myProperty2;
  private final String myProperty3;
  private final String myProperty4;
  private final double myProperty5;
  private final Integer myProperty6;
  private final String myProperty7;


  public ClassLoaderWithValuesV6(
      @ProtoValueV6("myProperty1") final int myProperty1,
      @ProtoValueV6("myProperty2") final boolean myProperty2,
      @ProtoValueV6("myProperty3") final String myProperty3,
      @ProtoValueV6("myProperty4") final String myProperty4,
      @ProtoValueV6("myProperty5") final double myProperty5,
      @ProtoValueV6("myProperty6.first") final Integer myProperty6,
      @ProtoValueV6("myProperty6.second") final String myProperty7
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
    return "ClassLoaderWithValuesV6{" +
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
