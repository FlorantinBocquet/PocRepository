package proto.loader.v4.items;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import proto.loader.global.ClassBrowser;
import proto.loader.global.Pair;
import proto.loader.v4.annotations.ProtoGenerableV4;
import proto.loader.v4.annotations.ProtoGeneratorV4;
import proto.loader.v4.annotations.ProtoLoadableV4;

public class ProtoClassLoaderV4 {
  private final ClassBrowser classBrowser = new ClassBrowser(".");

  private final Map<Class<? extends Annotation>, Function<Class<?>, List<ElementV4>>> readableAnnotations;

  public ProtoClassLoaderV4() {
    readableAnnotations = new HashMap<>();

    addMatchableAnnotation(
        ProtoLoadableV4.class,
        clazz -> List.of(new ElementV4(UtilsV4.getQualifierFromClass(clazz), ProtoLoadableV4.class, clazz, null))
    );

    addMatchableAnnotation(
        ProtoGeneratorV4.class,
        clazz -> Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .filter(m -> m.isAnnotationPresent(ProtoGenerableV4.class))
            .map(method -> new ElementV4(UtilsV4.getQualifierFromMethod(method), ProtoGenerableV4.class, clazz, method))
            .toList()
    );

    classBrowser.load();
  }

  public void addMatchableAnnotation(
      final Class<? extends Annotation> classAnnotation,
      final Function<Class<?>, List<ElementV4>> function
  ) {
    if (readableAnnotations.containsKey(classAnnotation)) {
      throw new IllegalStateException("Annotation already registered");
    }

    readableAnnotations.put(classAnnotation, function);
  }

  @SneakyThrows
  public Map<String, ElementV4> retrieveLoadable() {
    final Map<String, ElementV4> founds = new HashMap<>();

    classBrowser.browseClasses(loadedClass -> {
      final List<ElementV4> matchingElements = Optional.of(loadedClass)
          .filter(clazz -> !clazz.isInterface())
          .map(clazz -> new Pair<>(getReadableAnnotationKey(clazz), clazz))
          .filter(p -> p.first() != null)
          .map(p -> readableAnnotations.get(p.first()).apply(p.second()))
          .orElse(Collections.emptyList());

      matchingElements.forEach(e -> {
        if (founds.containsKey(e.qualifier())) {
          throw new IllegalStateException("Duplicated qualifier : " + e.qualifier());
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
}
