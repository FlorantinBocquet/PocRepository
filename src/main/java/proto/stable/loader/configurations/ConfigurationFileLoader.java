package proto.stable.loader.configurations;

import java.io.File;
import java.io.IOException;


public abstract class ConfigurationFileLoader {
  protected ConfigurationFileLoader(final File configFile) throws IOException {
    load(configFile);
  }

  protected abstract void load(final File configFile) throws IOException;

  public abstract String get(final String key);

  public abstract <T> T get(final String value, final Class<T> paramType);
}
