/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI;

import java.beans.PropertyDescriptor;

public abstract class Griddable extends Editable.EditorType
{
	public Griddable(final Object data, final String name, final String displayName)
	{
		super(data, name, displayName);
	}

	public static interface NonBeanPropertyDescriptor
	{
		public String getFieldName();
		public Class<?> getDataType();
		public HasNonBeanPropertyDescriptors getDataObject();
	}
	
	public static interface HasNonBeanPropertyDescriptors
	{
		public Object getValue(String fieldName);
		public void setValue(String fieldName, Object newVal);
	}
	
	abstract public PropertyDescriptor[] getGriddablePropertyDescriptors();
	abstract public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors();
}
