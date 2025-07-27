package proto.stable.loader.loaders;

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
import proto.stable.loader.annotations.Config;
import proto.stable.loader.annotations.Generable;
import proto.stable.loader.annotations.Generator;
import proto.stable.loader.annotations.Loadable;
import proto.stable.loader.utils.ClassBrowser;
import proto.stable.loader.utils.Element;
import proto.stable.loader.utils.Utils;

public class ClassLoader {
  private final ClassBrowser classBrowser = new ClassBrowser(".");

  private final Map<Class<? extends Annotation>, Function<Class<?>, List<Element>>> readableAnnotations;

  public ClassLoader() {
    readableAnnotations = new HashMap<>();

    addMatchableAnnotation(
        Loadable.class,
        clazz -> List.of(new Element(Utils.getQualifierFromClass(clazz), Loadable.class, clazz, null))
    );

    addMatchableAnnotation(
        Config.class,
        clazz -> List.of(new Element(Utils.getQualifierFromClass(clazz), Config.class, clazz, null))
    );

    addMatchableAnnotation(
        Generator.class,
        clazz -> Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> Modifier.isPublic(m.getModifiers()) && m.isAnnotationPresent(Generable.class))
            .map(method -> new Element(Utils.getQualifierFromMethod(method), Generable.class, clazz, method))
            .toList()
    );
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

    classBrowser.browseClasses(loadedClass -> Optional.of(loadedClass)
        .filter(clazz -> !clazz.isInterface())
        .flatMap(this::buildElementFromClass)
        .orElse(Collections.emptyList())
        .forEach(e -> {
          if (founds.containsKey(e.qualifier())) {
            throw new IllegalStateException("Duplicated qualifier : " + e.qualifier());
          }

          founds.put(e.qualifier(), e);
        })
    );

    return founds;
  }

  private Optional<List<Element>> buildElementFromClass(final Class<?> clazz) {
    return readableAnnotations.keySet().stream()
        .filter(clazz::isAnnotationPresent)
        .findFirst()
        .map(annotation -> readableAnnotations.get(annotation).apply(clazz));
  }
}
