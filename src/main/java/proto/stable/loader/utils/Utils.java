package proto.stable.loader.utils;

import java.lang.reflect.Method;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import proto.stable.loader.annotations.Qualifier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
  public static String getQualifierFromClass(final Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(Qualifier.class))
        .map(Qualifier::value)
        .orElse(removeKtSuffix(clazz.getSimpleName()));
  }

  public static String getQualifierFromMethod(final Method method) {
    return Optional.ofNullable(method.getAnnotation(Qualifier.class))
        .map(Qualifier::value)
        .orElse(method.getName());
  }

  public static String removeKtSuffix(final String s) {
    return s.endsWith("Kt") ? s.substring(0, s.length() - 2) : s;
  }
}
