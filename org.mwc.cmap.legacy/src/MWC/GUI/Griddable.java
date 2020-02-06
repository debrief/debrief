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

package MWC.GUI;

import java.beans.PropertyDescriptor;

public abstract class Griddable extends Editable.EditorType {
	public static interface HasNonBeanPropertyDescriptors {
		public Object getValue(String fieldName);

		public void setValue(String fieldName, Object newVal);
	}

	public static interface NonBeanPropertyDescriptor {
		public HasNonBeanPropertyDescriptors getDataObject();

		public Class<?> getDataType();

		public String getFieldName();
	}

	public Griddable(final Object data, final String name, final String displayName) {
		super(data, name, displayName);
	}

	abstract public PropertyDescriptor[] getGriddablePropertyDescriptors();

	abstract public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors();
}
