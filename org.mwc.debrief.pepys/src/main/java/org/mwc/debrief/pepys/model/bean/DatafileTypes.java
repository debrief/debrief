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

package org.mwc.debrief.pepys.model.bean;

import java.util.Date;

public class DatafileTypes implements AbstractBean{
	
	private int datafile_type_id;
	private String name;
	private Date created_date;
	
	public DatafileTypes()
	{
		
	}
	
	public int getDatafile_type_id() {
		return datafile_type_id;
	}

	public void setDatafile_type_id(int datafile_type_id) {
		this.datafile_type_id = datafile_type_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	@Override
	public String getIdField() {
		return "datafile_type_id";
	}
	
	
}
