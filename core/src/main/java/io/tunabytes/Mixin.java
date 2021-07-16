package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The main entrypoint for a tuna mixin class. This annotation marks that
 * the annotated member will be manipulating the specified class.
 * <p>
 * This annotation will be scanned by {@link io.tunabytes.ap.MixinsProcessor the annotation processor},
 * which is required for tuna mixins to work.
 * <p>
 * This annotation can be added to <em>classes</em> and <em>interfaces</em>:
 * <ul>
 *     <li>Classes will get necessary bytecode cloned into the class being
 *     manipulated. This includes fields and methods.</li>
 *     <li>Interfaces will be implemented by the class being manipulated, in
 *     which abstract methods must <strong>only</strong> be {@link Accessor accessor} methods.
 *     Interfaces can define {@link Inject} and {@link Overwrite} methods as well, as long
 *     as methods are default and not abstract.</li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Mixin {

    Class<?> value() default Object.class;
    String name() default "";
}
