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

public class Comments implements AbstractBean, FilterableBean{

	private int comment_id;
	private int platform_id;
	private Date time;
	private int comment_type_id;
	private String content;
	private int source_id;
	private int privacy_id;
	private Date created_date;

	public Comments() {
		
	}
	
	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public int getPlatform_id() {
		return platform_id;
	}

	public void setPlatform_id(int platform_id) {
		this.platform_id = platform_id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getComment_type_id() {
		return comment_type_id;
	}

	public void setComment_type_id(int comment_type_id) {
		this.comment_type_id = comment_type_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSource_id() {
		return source_id;
	}

	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}

	public int getPrivacy_id() {
		return privacy_id;
	}

	public void setPrivacy_id(int privacy_id) {
		this.privacy_id = privacy_id;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	
	@Override
	public String getTimeField() {
		return "time";
	}

	@Override
	public String getLocationField() {
		return null;
	}

	@Override
	public String getIdField() {
		return "comment_id";
	}

}
