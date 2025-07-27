package proto.loader.v4.items;

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

public class ProtoInstanceLoaderV4 {
  private final ProtoClassLoaderV4 loader = new ProtoClassLoaderV4();

  private Map<String, Object> objects = null;

  private static class MissingParamException extends Exception {
    public MissingParamException(String message) {
      super(message);
    }
  }

  public void create() throws IllegalStateException {
    if (objects == null) {
      objects = new HashMap<>();

      // This map is used to store the objects that are needed for methods to be loaded, it is fill and used in the
      // loadItem method.
      final Map<String, Object> loadedForMethods = new HashMap<>();

      // This list is used to store the elements that are not loaded yet in a given loop.
      List<ElementV4> toLoad = new ArrayList<>(loader.retrieveLoadable().values());

      // If an element fails to load because of missing parameters, it will be stored in this list.
      List<ElementV4> notLoaded = new ArrayList<>();

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
          final ElementV4 current = toLoad.remove(toLoad.size() - 1);

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
      final ElementV4 current,
      final Map<String, Object> loadedForMethods
  ) throws MissingParamException {
    if (current.method() != null) {
      // If necessary, build the object possessing the method
      if (!loadedForMethods.containsKey(current.clazz().getSimpleName())) {
        final Object loadedObject = loadByClass(current);

        loadedForMethods.put(loadedObject.getClass().getSimpleName(), loadedObject);
      }

      return loadByMethod(current, loadedForMethods);
    } else {

      return loadByClass(current);
    }
  }

  private void assertExactlyOneConstructor(final ElementV4 element) {
    if (element.clazz().getConstructors().length == 0) {
      throw new IllegalStateException("No public constructors detected, unable to create instance");
    }
    if (element.clazz().getConstructors().length != 1) {
      throw new IllegalStateException("Multiple public constructors detected, unable to decide which one to use");
    }
  }

  private Object loadByClass(
      final ElementV4 current
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

  private Object loadByMethod(
      final ElementV4 current,
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
      final Object arg = get(parameters[i].getName(), parameters[i].getType());

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

  @SuppressWarnings("unchecked")
  public <T> T get(final Class<T> paramType) {
    try {
      return (T) objects.get(UtilsV4.getQualifierFromClass(paramType));
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

        return objects.get(UtilsV4.getQualifierFromClass(paramType));
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
