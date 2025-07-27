package proto.loader.v3.items;

import java.lang.reflect.Method;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import proto.loader.v3.annotations.ProtoQualifierV3;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UtilsV3 {
  public static String getQualifierFromClass(final Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(ProtoQualifierV3.class))
        .map(ProtoQualifierV3::value)
        .orElse(removeKtSuffix(clazz.getSimpleName()));
  }

  public static String getQualifierFromMethod(final Method method) {
    return Optional.ofNullable(method.getAnnotation(ProtoQualifierV3.class))
        .map(ProtoQualifierV3::value)
        .orElse(method.getName());
  }

  public static String removeKtSuffix(final String s) {
    return s.endsWith("Kt") ? s.substring(0, s.length() - 2) : s;
  }
}
