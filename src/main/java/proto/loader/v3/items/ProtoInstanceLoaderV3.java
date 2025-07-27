package proto.loader.v3.items;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import proto.loader.global.Pair;

public class ProtoInstanceLoaderV3 {
  private final ProtoClassLoaderV3 loader = new ProtoClassLoaderV3();

  private Map<String, Object> objects = null;

  public void create() {
    if (objects == null) {
      final Map<String, ElementV3> loaded = loader.retrieveLoadable();

      objects = loaded.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> load(entry.getValue())));
    }
  }

  public Set<String> getQualifiers() {
    return objects.keySet();
  }

  private Object load(final ElementV3 element) {
    if (element.method() == null) {
      return loadClass(element);
    } else {
      return loadMethod(element);
    }
  }

  private Object loadClass(final ElementV3 element) {
    try {
      return element.clazz().getConstructor().newInstance();
    } catch (Exception e) {
      return null;
    }
  }

  private Object loadMethod(final ElementV3 element) {
    try {
      if (element.method() == null) {
        return null;
      }

      return element.method().invoke(element.clazz().getConstructor().newInstance());
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T get(final String qualifier) {
    try {
      return (T) objects.get(qualifier);
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T get(final Class<T> classQualifier) {
    try {
      return (T) objects.get(UtilsV3.getQualifierFromClass(classQualifier));
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T get(@Nullable final String qualifier, @Nullable final Class<T> classQualifier) {
    try {
      return (T) objects.get(qualifier);
    } catch (Exception e) {
      try {
        assert classQualifier != null;

        return (T) objects.get(UtilsV3.getQualifierFromClass(classQualifier));
      } catch (Exception e2) {
        return null;
      }
    }
  }

  @SuppressWarnings("unchecked")
  public <T> List<Pair<String, T>> getMatching(final Class<T> clazz) {
    return objects.entrySet().stream()
        .filter(o -> clazz.isInstance(o.getValue()))
        .map(o -> new Pair<>(o.getKey(), (T) o.getValue()))
        .toList();
  }
}
