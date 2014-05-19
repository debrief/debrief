package MWC.GUI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** declare an annotation as a marker, used for methods that have changed the fundamental
 *  data in debrief, and need a 'fireExtended' event versus a 'fireModified' event.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FireExtended{
	
}