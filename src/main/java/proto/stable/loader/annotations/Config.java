package proto.stable.loader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class tagged with this annotation is a config class that can be used with the loader.
 */
// equivalent of spring @ConfigurationProperties
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
  // Root ref of the config class
  String value();
}
