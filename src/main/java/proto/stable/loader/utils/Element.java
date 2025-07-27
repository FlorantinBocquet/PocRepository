package proto.stable.loader.utils;

import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Element(
    @NotNull
    String qualifier,
    @NotNull
    Class<?> annotationClass,
    @NotNull
    Class<?> clazz,
    @Nullable
    Method method
) {
  public void assertExactlyOneConstructor() {
    if (clazz.getConstructors().length == 0) {
      throw new IllegalStateException("No public constructors detected, unable to create instance");
    }
    if (clazz.getConstructors().length != 1) {
      throw new IllegalStateException("Multiple public constructors detected, unable to decide which one to use");
    }
  }
}
