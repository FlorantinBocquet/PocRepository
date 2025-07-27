package proto.loader.v4.items;

import java.lang.reflect.Method;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import proto.loader.v4.annotations.ProtoQualifierV4;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UtilsV4 {
  public static String getQualifierFromClass(final Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(ProtoQualifierV4.class))
        .map(ProtoQualifierV4::value)
        .orElse(removeKtSuffix(clazz.getSimpleName()));
  }

  public static String getQualifierFromMethod(final Method method) {
    return Optional.ofNullable(method.getAnnotation(ProtoQualifierV4.class))
        .map(ProtoQualifierV4::value)
        .orElse(method.getName());
  }

  public static String removeKtSuffix(final String s) {
    return s.endsWith("Kt") ? s.substring(0, s.length() - 2) : s;
  }
}
