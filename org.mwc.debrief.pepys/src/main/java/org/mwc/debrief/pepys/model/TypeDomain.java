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

package org.mwc.debrief.pepys.model;

import org.mwc.debrief.pepys.model.bean.FilterableBean;

/**
 * Class used for the Data Type Filtering Model Item.
 * 
 */
public class TypeDomain {
	private Class<FilterableBean> datatype;
	private String name;
	
	public Class<FilterableBean> getDatatype() {
		return datatype;
	}
	public void setDatatype(Class<FilterableBean> datatype) {
		this.datatype = datatype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
