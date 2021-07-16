package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an annotation to mark a field or a method inside a {@link Mixin} class as a <em>mirror</em>:
 * <ul>
 *     <li>Any access to the mirrored field or method will be translated into access to the actual
 *     field or method in the class being manipulated.</li>
 *     <li>Setting a mirror field value will also translate into setting the actual field
 *     in the class being manipulated.</li>
 *     <li>Invoking the mirror method will translate into invoking the actual underlying method
 *     in the class being manipulated</li>
 *     <li>Adding {@link Definalize} to a mirrored field or method will make the actual field or method
 *     non-final in the class being manipulated.</li>
 *     <li>The mirror field name <strong>must</strong> match the underlying field name. Alternatively,
 *     the target field name can be specified in {@link Mirror#value()}.</li>
 *     <li>The mirror method <strong>must</strong> match the underlying method with the name
 *     and signature (parameters and return types).</li>
 *     <li>Mirror methods must be abstract.</li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mirror {

    /**
     * The underlying field or method that is being mirrored.
     *
     * @return The target field or method name
     */
    String value() default "";

}
