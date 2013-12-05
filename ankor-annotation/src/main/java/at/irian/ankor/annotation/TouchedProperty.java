package at.irian.ankor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Manfred Geiler
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TouchedProperty {
    String value();
    int diffThreshold() default 10;
    Class<?> diffHandler() default DefaultHandler.class;

    static class DefaultHandler {}
}
