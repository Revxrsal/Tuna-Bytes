package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method or property accessor.
 * <p>
 * This can be used to access inaccessible fields or methods, in which it will
 * infer the method's functionality and target from the annotated method name.
 * <p>
 * Example:
 * <pre>
 * public class Point {
 *
 *     private final int x;
 *     private final int y;
 *
 *     public Point(int x, int y) {
 *         this.x = x;
 *         this.y = y;
 *     }
 *
 *     private void secretMethod() {
 *         System.out.println("top secret");
 *     }
 *
 * }
 *
 * &#64;Mixin(Point.class)
 * public interface PointAccessor {
 *
 *     &#64;Accessor int getX();
 *
 *     &#64;Accessor int getY();
 *
 *     &#64;Accessor void callSecretMethod();
 *
 * }
 * </pre>
 * We can then access the properties by simply casting the <em>Point</em> object
 * to a <em>PointAccessor</em>:
 * <pre>
 *     Point point = new Point(10, -41);
 *     ((PointAccessor) point).getX();
 *     ((PointAccessor) point).getY();
 *     ((PointAccessor) point).callSecretMethod();
 * </pre>
 * <p>
 * Getter and setter accessor method names should follow the common naming conventions
 * to get the property name evaluated correctly. For invoking methods, they should be prefixed
 * with <em>invoke</em> or <em>call</em>:
 * <ul>
 *     <li>getAbc() will infer the property 'abc'</li>
 *     <li>setURL() will infer the property 'uRL'!</li>
 *     <li>callMethod() will infer the method method()</li>
 *     <li>invokeSomething() will infer the method something()</li>
 * </ul>
 * <p>
 * Alternatively, you can specify the property or method name inside the {@link Accessor#value()}
 * when inferring may go wrong.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Accessor {

    /**
     * The name of the method or property being accessed.
     *
     * @return The property or method name.
     */
    String value() default "";

}
