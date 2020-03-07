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

public class Privacies implements AbstractBean {

	private int privacy_id;
	private String name;
	private Date created_date;

	public Privacies() {

	}

	public Date getCreated_date() {
		return created_date;
	}

	@Override
	public String getIdField() {
		return "privacy_id";
	}

	public String getName() {
		return name;
	}

	public int getPrivacy_id() {
		return privacy_id;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPrivacy_id(final int privacy_id) {
		this.privacy_id = privacy_id;
	}
}
