package info.limpet.impl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Provides UI metadata for Java bean getter methods
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UIProperty
{
	String CATEGORY_LABEL = "Label";
	String CATEGORY_METADATA = "Metadata";
	String CATEGORY_CALCULATION = "Calculation";
	String CATEGORY_VALUE = "Value";

	/**
	 * @return user-friendly name of this property that will be displayed in the
	 *         UI
	 */
	String name();

	String category();

	/**
	 * Some properties are visible when certain condition is met.
	 * @return a boolean expression string that must evaluate to <code>true</code> 
	 * or <code>false</code>. The expression might refer to Java bean properties,
	 * for example <code>"size==1"</code> is a valid expression if the bean contains
	 * getter for a property named "size". Empty string means always visible.
	 */
	String visibleWhen() default "";
	
	int min() default Integer.MIN_VALUE;

	int max() default Integer.MAX_VALUE;

	/**
	 * @return default value for integer properties
	 */
	int defaultInt() default 0;

	/**
	 * @return default value for double properties
	 */
	double defaultDouble() default 0.0;

	/**
	 * @return default value for boolean properties
	 */
	boolean defaultBoolean() default false;

	/**
	 * @return default value for String properties
	 */
	String defaultString() default "";
}
