package proto.loader.v6.items;

import java.lang.reflect.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ElementV6(
    @NotNull
    String qualifier,
    @NotNull
    Class<?> annotationClass,
    @NotNull
    Class<?> clazz,
    @Nullable
    Method method
) {
}
