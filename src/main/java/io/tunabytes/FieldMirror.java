package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an annotation to mark a field inside a {@link Mixin} class as a <em>mirror</em>
 * field:
 * <ul>
 *     <li>Any access to the mirror field will be translated into access to the actual field
 *     in the class being manipulated.</li>
 *     <li>Setting the field value will also translate into setting the actual field
 *     in the class being manipulated.</li>
 *     <li>Adding {@link Definalize} to a mirrored field will make the actual field non-final
 *     in the class being manipulated.</li>
 *     <li>The mirror field name <strong>must</strong> match the underlying field name. Alternatively,
 *     the target field name can be specified in {@link FieldMirror#value()}.</li>
 * </ul>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMirror {

    /**
     * The underlying field that this field is mirrorring.
     *
     * @return The target field name
     */
    String value() default "";

}
