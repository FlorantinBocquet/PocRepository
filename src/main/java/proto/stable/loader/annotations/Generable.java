package proto.stable.loader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method annotated with this annotation can be used to generate data for the loader. Must be in a class annotated
 * with {@link Generator}.
 */
// equivalent of spring @Bean
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Generable {
}
