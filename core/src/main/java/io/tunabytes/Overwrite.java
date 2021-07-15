package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark the mixin method as <em>completely overwrite</em> the
 * targeted method's code.
 * <p>
 * The mixins method name and signature must exactly match the targeted method.
 * That is:
 * <ul>
 *     <li>The method's name. This can also be specifind in {@link Overwrite#value()}</li>
 *     <li>The method's return type</li>
 *     <li>The method's parameter types in order</li>
 * </ul>
 * <p>
 * Note that having multiple {@link Overwrite}s on the same method would simply lead
 * to one overwrite being dismissed. Hence, it is not recommended to have more
 * than one {@link Overwrite} for each method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Overwrite {

    /**
     * The name of the method being overwritten.
     *
     * @return The method name
     */
    String value() default "";

}
