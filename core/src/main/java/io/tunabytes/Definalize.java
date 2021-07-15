package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation added to {@link FieldMirror}s to mark their targetted fields
 * as non-final.
 * <p>
 * See {@link FieldMirror} for more information
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Definalize {
}
