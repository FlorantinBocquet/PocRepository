package proto.loader.v5.items.configuration;

import java.io.File;
import java.io.IOException;

public class EmptyConfigurationFileLoaderV5 extends ConfigurationFileLoaderV5 {
  private static EmptyConfigurationFileLoaderV5 INSTANCE = null;

  public static EmptyConfigurationFileLoaderV5 getInstance() {
    if (INSTANCE == null) {
      try {
        INSTANCE = new EmptyConfigurationFileLoaderV5();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return INSTANCE;
  }

  private EmptyConfigurationFileLoaderV5() throws IOException {
    super(null);
  }

  @Override
  protected void load(final File configFile) throws IOException {
  }

  @Override
  public String get(final String key) {
    return null;
  }

  @Override
  public <T> T get(String value, Class<T> paramType) {
    return null;
  }
}
