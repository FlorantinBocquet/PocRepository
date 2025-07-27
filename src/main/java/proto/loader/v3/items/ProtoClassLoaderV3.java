package proto.loader.v3.items;

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
import proto.loader.v3.annotations.ProtoGenerableV3;
import proto.loader.v3.annotations.ProtoGeneratorV3;
import proto.loader.v3.annotations.ProtoLoadableV3;

public class ProtoClassLoaderV3 {
  private final ClassBrowser classBrowser = new ClassBrowser(".");

  private final Map<Class<? extends Annotation>, Function<Class<?>, List<ElementV3>>> readableAnnotations;

  public ProtoClassLoaderV3() {
    readableAnnotations = new HashMap<>();

    addMatchableAnnotation(
        ProtoLoadableV3.class,
        clazz -> List.of(new ElementV3(UtilsV3.getQualifierFromClass(clazz), ProtoLoadableV3.class, clazz, null))
    );

    addMatchableAnnotation(
        ProtoGeneratorV3.class,
        clazz -> Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .filter(m -> m.isAnnotationPresent(ProtoGenerableV3.class))
            .map(method -> new ElementV3(UtilsV3.getQualifierFromMethod(method), ProtoGenerableV3.class, clazz, method))
            .toList()
    );

    classBrowser.load();
  }

  public void addMatchableAnnotation(
      final Class<? extends Annotation> classAnnotation,
      final Function<Class<?>, List<ElementV3>> function
  ) {
    if (readableAnnotations.containsKey(classAnnotation)) {
      throw new IllegalStateException("Annotation already registered");
    }

    readableAnnotations.put(classAnnotation, function);
  }

  @SneakyThrows
  public Map<String, ElementV3> retrieveLoadable() {
    final Map<String, ElementV3> founds = new HashMap<>();

    classBrowser.browseClasses(loadedClass -> {
      final List<ElementV3> matchingElements = Optional.of(loadedClass)
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
