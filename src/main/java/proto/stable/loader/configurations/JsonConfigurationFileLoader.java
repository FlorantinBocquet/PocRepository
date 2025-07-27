package proto.stable.loader.configurations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JsonConfigurationFileLoader extends ConfigurationFileLoader {
  private JsonNode root;
  private final ObjectMapper mapper = new ObjectMapper();

  public JsonConfigurationFileLoader(final File configFile) throws IOException {
    super(configFile);
  }

  @Override
  protected void load(final File configFile) throws IOException {
    final ObjectMapper mapper = new ObjectMapper();

    try (final InputStream in = configFile.toURI().toURL().openStream()) {
      root = mapper.readValue(in, JsonNode.class);
    }
  }

  private JsonNode getNode(final String key) {
    final String[] path = key.split("\\.");

    JsonNode node = this.root;
    for (final String s : path) {
      node = node.get(s);
    }

    return node;
  }

  @Override
  public String get(final String key) {
    return getNode(key).asText();
  }

  @Override
  public <T> T get(final String value, final Class<T> paramType) {
    try {
      return mapper.readValue(getNode(value).toString(), paramType);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
