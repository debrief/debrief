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

package com.borlander.rac353542.bislider.cdata;

import com.borlander.rac353542.bislider.DefaultBiSliderDataModel;

public class DataObjectDataModel extends DefaultBiSliderDataModel {
	private final DataObjectMapper myMapper;

	public DataObjectDataModel(final DataObjectMapper mapper) {
		super(mapper.getPrecision());
		myMapper = mapper;
	}

	public DataObjectMapper getMapper() {
		return myMapper;
	}

	protected Object getTotalMaximumObject() {
		return myMapper.double2object(this.getTotalMaximum());
	}

	protected Object getTotalMinimumObject() {
		return myMapper.double2object(this.getTotalMinimum());
	}

	protected Object getUserMaximumObject() {
		return myMapper.double2object(this.getUserMaximum());
	}

	protected Object getUserMinimumObject() {
		return myMapper.double2object(this.getUserMinimum());
	}

	protected void setTotalObjectRange(final Object minimumDataObject, final Object maximumDataObject) {
		this.setTotalRange(myMapper.object2double(minimumDataObject), myMapper.object2double(maximumDataObject));
	}

	protected void setUserMaximumObject(final Object dataObject) {
		this.setUserMaximum(myMapper.object2double(dataObject));
	}

	protected void setUserMinimumObject(final Object dataObject) {
		this.setUserMinimum(myMapper.object2double(dataObject));
	}

}
