package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that allows to explicitly specify the type of a field, method or parameter.
 * <p>
 * Since the JVM requires matching the exact field or method signature in the bytecode,
 * it may be impossible to get certain methods or fields whose types or signatures
 * are of inaccessible classes. This annotation allows to override this and
 * get the type remapped accordingly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface ActualType {

    /**
     * The class name of which this type is composed of
     *
     * @return The full binary class name.
     */
    String value();

}
