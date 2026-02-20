package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public class JsonUtil {
  private static final ObjectMapper om = new ObjectMapper();

  public static <T> T readResource(String path, Class<T> clazz) {
    try (InputStream in = JsonUtil.class.getClassLoader().getResourceAsStream(path)) {
      if (in == null) throw new IllegalArgumentException("Missing resource: " + path);
      return om.readValue(in, clazz);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
