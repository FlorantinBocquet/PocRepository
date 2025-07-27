package proto.loader.v6.items.configuration;

import java.io.File;
import java.io.IOException;

public class EmptyConfigurationFileLoaderV6 extends ConfigurationFileLoaderV6 {
  private static EmptyConfigurationFileLoaderV6 INSTANCE = null;

  public static EmptyConfigurationFileLoaderV6 getInstance() {
    if (INSTANCE == null) {
      try {
        INSTANCE = new EmptyConfigurationFileLoaderV6();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return INSTANCE;
  }

  private EmptyConfigurationFileLoaderV6() throws IOException {
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
