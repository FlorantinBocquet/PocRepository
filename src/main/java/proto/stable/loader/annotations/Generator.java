package proto.stable.loader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class tagged generator indicate it contains method that can be used to generate data for the loader. Is used with
 * the {@link Generable} annotation.
 */
// equivalent of spring @Configuration
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Generator {
}
