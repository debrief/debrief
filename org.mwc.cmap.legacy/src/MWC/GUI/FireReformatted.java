package MWC.GUI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** declare an annotation as a marker, used for methods that have changed the fundamental
 *  data in debrief, and need a 'fireReformatted' event versus a 'fireModified' event.
 *  
 *  The difference between Reformatted and Modified, is that Reformatted affects how the item is displayed
 *  off the plot (such as in the layer manager).  The layer manager doesn't normally listen out for modified changes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FireReformatted{
	
}