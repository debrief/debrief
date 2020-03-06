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

public class Datafiles implements AbstractBean {

	private int datafile_id;
	private boolean simulated;
	private int privacy_id;
	private int datafile_type_id;
	private String reference;
	private String url;
	private Date created_date;
	
	public Datafiles() {
		
	}

	public int getDatafile_id() {
		return datafile_id;
	}

	public void setDatafile_id(int datafile_id) {
		this.datafile_id = datafile_id;
	}

	public boolean getSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}
	
	public int getPrivacy_id() {
		return privacy_id;
	}

	public void setPrivacy_id(int privacy_id) {
		this.privacy_id = privacy_id;
	}

	public int getDatafile_type_id() {
		return datafile_type_id;
	}

	public void setDatafile_type_id(int datafile_type_id) {
		this.datafile_type_id = datafile_type_id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	@Override
	public String getIdField() {
		return "datafile_id";
	}
	
	
}
