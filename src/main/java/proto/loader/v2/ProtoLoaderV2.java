package proto.loader.v2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import proto.loader.global.ClassBrowser;
import proto.loader.global.Pair;
import proto.loader.v2.annotations.ProtoGenerableV2;
import proto.loader.v2.annotations.ProtoGeneratorV2;
import proto.loader.v2.annotations.ProtoLoadableV2;
import proto.loader.v2.annotations.ProtoQualifierV2;

public class ProtoLoaderV2 {
  private final ClassBrowser classBrowser = new ClassBrowser(".");

  private final Map<Class<? extends Annotation>, Function<Class<?>, List<Element>>> readableAnnotations;

  public ProtoLoaderV2() {
    readableAnnotations = new HashMap<>();

    addMatchableAnnotation(
        ProtoLoadableV2.class,
        clazz -> List.of(new Element(getQualifierFromClass(clazz), ProtoLoadableV2.class, clazz, null))
    );

    addMatchableAnnotation(
        ProtoGeneratorV2.class,
        clazz -> Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .filter(m -> m.isAnnotationPresent(ProtoGenerableV2.class))
            .map(method -> new Element(getQualifierFromMethod(method), ProtoGenerableV2.class, clazz, method))
            .toList()
    );

    classBrowser.load();
  }

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
  }

  public void addMatchableAnnotation(
      final Class<? extends Annotation> classAnnotation,
      final Function<Class<?>, List<Element>> function
  ) {
    if (readableAnnotations.containsKey(classAnnotation)) {
      throw new IllegalStateException("Annotation already registered");
    }

    readableAnnotations.put(classAnnotation, function);
  }

  @SneakyThrows
  public Map<String, Element> retrieveLoadable() {
    final Map<String, Element> founds = new HashMap<>();

    classBrowser.browseClasses(loadedClass -> {
      final List<Element> matchingElements = Optional.of(loadedClass)
          .filter(clazz -> !clazz.isInterface())
          .map(clazz -> new Pair<>(getReadableAnnotationKey(clazz), clazz))
          .filter(p -> p.first() != null)
          .map(p -> readableAnnotations.get(p.first()).apply(p.second()))
          .orElse(Collections.emptyList());

      matchingElements.forEach(e -> {
        if (founds.containsKey(e.qualifier)) {
          throw new IllegalStateException("Duplicated qualifier : " + e.qualifier);
        }

        founds.put(e.qualifier(), e);
      });
    });

    return founds;
  }

  @Nullable
  private Class<? extends Annotation> getReadableAnnotationKey(final Class<?> clazz) {
    return readableAnnotations.keySet().stream()
        .filter(clazz::isAnnotationPresent)
        .findFirst()
        .orElse(null);
  }

  public static String getQualifierFromClass(final Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(ProtoQualifierV2.class))
        .map(ProtoQualifierV2::value)
        .orElse(removeKtSuffix(clazz.getSimpleName()));
  }

  public static String getQualifierFromMethod(final Method method) {
    return Optional.ofNullable(method.getAnnotation(ProtoQualifierV2.class))
        .map(ProtoQualifierV2::value)
        .orElse(method.getName());
  }

  private static String removeKtSuffix(final String s) {
    return s.endsWith("Kt") ? s.substring(0, s.length() - 2) : s;
  }
}
