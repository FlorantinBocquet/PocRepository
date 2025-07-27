package proto.loader.v6.items;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import proto.loader.global.Pair;
import proto.loader.v6.annotations.ProtoConfigV6;
import proto.loader.v6.annotations.ProtoGenerableV6;
import proto.loader.v6.annotations.ProtoLoadableV6;
import proto.loader.v6.annotations.ProtoValueV6;
import proto.loader.v6.items.configuration.ConfigurationFileLoaderV6;
import proto.loader.v6.items.configuration.EmptyConfigurationFileLoaderV6;

public class ProtoInstanceLoaderV6 {
  private final ProtoClassLoaderV6 loader;
  private final ConfigurationFileLoaderV6 conf;

  private Map<String, Object> objects = null;

  private static class MissingParamException extends Exception {
    public MissingParamException(String message) {
      super(message);
    }
  }

  // CONSTRUCTORS

  public ProtoInstanceLoaderV6() {
    this(EmptyConfigurationFileLoaderV6.getInstance());
  }

  public ProtoInstanceLoaderV6(final ConfigurationFileLoaderV6 conf) {
    loader = new ProtoClassLoaderV6();
    this.conf = conf;
  }

  // METHODS

  public void create() throws IllegalStateException {
    if (objects == null) {
      objects = new HashMap<>();

      // This map is used to store the objects that are needed for methods to be loaded, it is fill and used in the
      // loadItem method.
      final Map<String, Object> loadedForMethods = new HashMap<>();

      // This list is used to store the elements that are not loaded yet in a given loop.
      List<ElementV6> toLoad = new ArrayList<>(loader.retrieveLoadable().values());

      // If an element fails to load because of missing parameters, it will be stored in this list.
      List<ElementV6> notLoaded = new ArrayList<>();

      // This variable is used to check if any element were loaded in the last run of the internal loop.
      boolean hasChanged;

      /*
        The internal do / while try to load all elements that are not loaded yet.

        The external do / while check if all element are loaded, and if any element were loaded in the last run of the
        internal loop. If nothing were loaded in a given run, but there still is remaining elements, it means that
        there is at least one unavailable parameter, and the loop will end, and an exception will be thrown, because
        it would be impossible to load all elements. It would also happen if there is a circular dependency between
        elements.
       */
      do {
        // This variable store the size of the list before the internal loop starts, allowing to check if any element
        // were loaded during the loop.
        final int preLoadedSize = toLoad.size();

        do {
          final ElementV6 current = toLoad.remove(toLoad.size() - 1);

          assertExactlyOneConstructor(current);

          // Technically no duplicate qualifiers can be found at this point
          try {
            final Object loadedItem = loadItem(current, loadedForMethods);

            objects.put(current.qualifier(), loadedItem);
          } catch (final MissingParamException e) {
            notLoaded.add(current);
          }
        } while (!toLoad.isEmpty());

        hasChanged = preLoadedSize != notLoaded.size();

        toLoad = notLoaded;
        notLoaded = new ArrayList<>();
      } while (hasChanged && !toLoad.isEmpty());

      if (!toLoad.isEmpty()) {
        throw new IllegalStateException("Unable to load all elements");
      }
    }
  }

  private Object loadItem(
      final ElementV6 current,
      final Map<String, Object> loadedForMethods
  ) throws MissingParamException {
    if (current.annotationClass() == ProtoGenerableV6.class) {
      // If necessary, build the object possessing the method
      if (!loadedForMethods.containsKey(current.clazz().getSimpleName())) {
        final Object loadedObject = loadForLoadable(current);

        loadedForMethods.put(loadedObject.getClass().getSimpleName(), loadedObject);
      }

      return loadForGenerable(current, loadedForMethods);
    } else if (current.annotationClass() == ProtoLoadableV6.class) {
      return loadForLoadable(current);
    } else if (current.annotationClass() == ProtoConfigV6.class) {
      return loadForConfig(current);
    }

    throw new IllegalStateException("Unknown annotation");
  }

  private void assertExactlyOneConstructor(final ElementV6 element) {
    if (element.clazz().getConstructors().length == 0) {
      throw new IllegalStateException("No public constructors detected, unable to create instance");
    }
    if (element.clazz().getConstructors().length != 1) {
      throw new IllegalStateException("Multiple public constructors detected, unable to decide which one to use");
    }
  }

  private Object loadForConfig(final ElementV6 current) {
    final String rootPath = current.clazz().getAnnotation(ProtoConfigV6.class).value();

    return conf.get(rootPath, current.clazz());
  }

  private Object loadForLoadable(
      final ElementV6 current
  ) throws MissingParamException {
    final Constructor<?> constructor = current.clazz().getConstructors()[0];
    final Parameter[] parameters = constructor.getParameters();

    try {
      if (parameters.length == 0) {
        return constructor.newInstance();
      }

      final Object[] args = loadArgs(parameters);

      return constructor.newInstance(args);
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to create instance", e);
    }
  }

  private Object loadForGenerable(
      final ElementV6 current,
      final Map<String, Object> loadedForMethods
  ) throws MissingParamException {
    final Method method = current.method();
    if (method == null) {
      return null;
    }

    final Object loadedObject = loadedForMethods.get(current.clazz().getSimpleName());
    final Parameter[] parameters = method.getParameters();

    try {
      if (parameters.length == 0) {
        return method.invoke(loadedObject);
      }

      final Object[] args = loadArgs(parameters);

      return method.invoke(loadedObject, args);
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to create instance", e);
    }
  }

  private Object[] loadArgs(final Parameter[] parameters) throws MissingParamException {
    final Object[] args = new Object[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      final Parameter parameter = parameters[i];
      final ProtoValueV6 value = parameter.getAnnotation(ProtoValueV6.class);

      final Object arg;
      if (value != null) {
        arg = conf.get(value.value(), parameter.getType());
      } else {
        arg = get(parameter.getName(), parameter.getType());
      }

      if (arg == null) {
        throw new MissingParamException("Unable to find required argument");
      }

      args[i] = arg;
    }
    return args;
  }

  public Set<String> getQualifiers() {
    return objects.keySet();
  }

  @SuppressWarnings("unchecked")
  public <T> T get(final String paramName) {
    try {
      return (T) objects.get(paramName);
    } catch (Exception e) {
      return null;
    }
  }

  public Object get(@Nullable final String paramName, @Nullable final Class<?> paramType) {
    try {
      final Object item = objects.get(paramName);

      if (item == null) {
        throw new Exception();
      }

      return item;
    } catch (Exception e) {
      try {
        if (paramType == null) {
          throw new Exception();
        }

        return objects.get(UtilsV6.getQualifierFromClass(paramType));
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
