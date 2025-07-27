package proto.loader.v5.items;

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
import proto.loader.v5.annotations.ProtoConfigV5;
import proto.loader.v5.annotations.ProtoGenerableV5;
import proto.loader.v5.annotations.ProtoGeneratorV5;
import proto.loader.v5.annotations.ProtoLoadableV5;

public class ProtoClassLoaderV5 {
  private final ClassBrowser classBrowser = new ClassBrowser(".");

  private final Map<Class<? extends Annotation>, Function<Class<?>, List<ElementV5>>> readableAnnotations;

  public ProtoClassLoaderV5() {
    readableAnnotations = new HashMap<>();

    addMatchableAnnotation(
        ProtoLoadableV5.class,
        clazz -> List.of(new ElementV5(UtilsV5.getQualifierFromClass(clazz), ProtoLoadableV5.class, clazz, null))
    );

    addMatchableAnnotation(
        ProtoConfigV5.class,
        clazz -> List.of(new ElementV5(UtilsV5.getQualifierFromClass(clazz), ProtoConfigV5.class, clazz, null))
    );

    addMatchableAnnotation(
        ProtoGeneratorV5.class,
        clazz -> Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .filter(m -> m.isAnnotationPresent(ProtoGenerableV5.class))
            .map(method -> new ElementV5(UtilsV5.getQualifierFromMethod(method), ProtoGenerableV5.class, clazz, method))
            .toList()
    );

    classBrowser.load();
  }

  public void addMatchableAnnotation(
      final Class<? extends Annotation> classAnnotation,
      final Function<Class<?>, List<ElementV5>> function
  ) {
    if (readableAnnotations.containsKey(classAnnotation)) {
      throw new IllegalStateException("Annotation already registered");
    }

    readableAnnotations.put(classAnnotation, function);
  }

  @SneakyThrows
  public Map<String, ElementV5> retrieveLoadable() {
    final Map<String, ElementV5> founds = new HashMap<>();

    classBrowser.browseClasses(loadedClass -> {
      final List<ElementV5> matchingElements = Optional.of(loadedClass)
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
