package proto.loader.v6.items.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JsonConfigurationFileLoaderV6 extends ConfigurationFileLoaderV6 {
  private JsonNode root;

  public JsonConfigurationFileLoaderV6(final File configFile) throws IOException {
    super(configFile);
  }

  @Override
  protected void load(final File configFile) throws IOException {
    final ObjectMapper mapper = new ObjectMapper();

    try (final InputStream in = configFile.toURI().toURL().openStream()) {
      root = mapper.readValue(in, JsonNode.class);
    }
  }

  @Override
  public String get(final String key) {
    final String[] path = key.split("\\.");

    JsonNode node = this.root;
    for (final String s : path) {
      node = node.get(s);
    }

    return node.asText();
  }

  @Override
  public <T> T get(final String value, final Class<T> paramType) {
    final String[] path = value.split("\\.");

    JsonNode node = this.root;
    for (final String s : path) {
      node = node.get(s);
    }

    try {
      final ObjectMapper mapper = new ObjectMapper();

      return mapper.readValue(node.toString(), paramType);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
