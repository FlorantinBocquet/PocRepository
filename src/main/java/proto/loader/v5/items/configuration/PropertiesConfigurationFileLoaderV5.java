package proto.loader.v5.items.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Truth is : it doesn't work really well
 */
@Deprecated
public class PropertiesConfigurationFileLoaderV5 extends ConfigurationFileLoaderV5 {
  private Map<String, String> params;

  public PropertiesConfigurationFileLoaderV5(final File configFile) throws IOException {
    super(configFile);
  }

  @Override
  protected void load(final File configFile) throws IOException {
    try (
        final FileReader fr = new FileReader(configFile);
        final BufferedReader bos = new BufferedReader(fr)
    ) {
      params = bos.lines()
          .filter(line -> line.contains("="))
          .map(line -> line.split("="))
          .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }
  }

  @Override
  public String get(final String key) {
    return params.get(key);
  }

  @SuppressWarnings("unchecked")
  @Override
  // TODO: This method is not implemented yet
  //  Only validate config that have basic types
  public <T> T get(final String rootPath, final Class<T> paramType) {
    final Constructor<?> constructor = paramType.getConstructors()[0];
    final Parameter[] parameters = constructor.getParameters();

    final Object[] args = new Object[parameters.length];

    try {
      if (parameters.length == 0) {
        throw new IllegalStateException("Configuration class must have at least one parameter");
      }

      /*
        For each parameter, we need to check if it's available in the configuration file.
        If it is available, we try to retrieve it, if wrong type, will fail during constructor call.
        If it is not, we need to check if it may be build (can be another part of the config)
        ISSUE : how to check if it's a complex object ?
        Could be possible to check on keys if there are multiple keys with the same base rootPath ? can be
        time-consuming ?
        ISSUE : avoiding recursive calls --> use a stack to store the current param, and remove it to add it to found
        params of the next element of the stack.
       */

      for (int i = 0; i < parameters.length; i++) {
        final Parameter parameter = parameters[i];

        final String fullName = rootPath + "." + parameter.getName();

        final Object arg = get(fullName, parameter.getType());
        if (arg == null) {
          // TODO if args == null --> check for a more complex item
          //  warning : avoid recursive call
          throw new IllegalStateException("Unable to find required argument");
        }

        args[i] = get(fullName, parameter.getType());
      }

      return (T) constructor.newInstance(args);
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to create instance", e);
    }
  }
}
