package proto.loader.v6.items.configuration;

import java.io.File;
import java.io.IOException;


public abstract class ConfigurationFileLoaderV6 {
  protected ConfigurationFileLoaderV6(final File configFile) throws IOException {
    load(configFile);
  }

  protected abstract void load(final File configFile) throws IOException;

  public abstract String get(final String key);

  public abstract <T> T get(final String value, final Class<T> paramType);
}
