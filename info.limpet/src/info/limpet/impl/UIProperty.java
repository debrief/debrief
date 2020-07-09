/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package info.limpet.impl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Provides UI metadata for Java bean getter methods
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UIProperty {
	String CATEGORY_LABEL = "Label";
	String CATEGORY_METADATA = "Metadata";
	String CATEGORY_CALCULATION = "Calculation";
	String CATEGORY_VALUE = "Value";

	String category();

	/**
	 * @return default value for boolean properties
	 */
	boolean defaultBoolean() default false;

	/**
	 * @return default value for double properties
	 */
	double defaultDouble() default 0.0;

	/**
	 * @return default value for integer properties
	 */
	int defaultInt() default 0;

	/**
	 * @return default value for String properties
	 */
	String defaultString() default "";

	int max() default Integer.MAX_VALUE;

	int min() default Integer.MIN_VALUE;

	/**
	 * @return user-friendly name of this property that will be displayed in the
	 * UI
	 */
	String name();

	/**
	 * Some properties are visible when certain condition is met.
	 *
	 * @return a boolean expression string that must evaluate to
	 * <code>true</code> or <code>false</code>. The expression might refer to
	 * Java bean properties, for example <code>"size==1"</code> is a valid
	 * expression if the bean contains getter for a property named "size". Empty
	 * string means always visible.
	 */
	String visibleWhen() default "";
}
