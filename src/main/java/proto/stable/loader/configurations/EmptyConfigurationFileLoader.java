package proto.stable.loader.configurations;

import java.io.File;
import java.io.IOException;

public class EmptyConfigurationFileLoader extends ConfigurationFileLoader {
  private static EmptyConfigurationFileLoader INSTANCE = null;

  public static EmptyConfigurationFileLoader getInstance() {
    if (INSTANCE == null) {
      try {
        INSTANCE = new EmptyConfigurationFileLoader();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return INSTANCE;
  }

  private EmptyConfigurationFileLoader() throws IOException {
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
