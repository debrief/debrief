/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.pepys.model.bean.custom;

import org.mwc.debrief.pepys.model.db.annotation.FieldName;

public class Measurement {
	
	@FieldName(name = "PLATFORM_NAME")
	private String platformName;

	@FieldName(name = "platform_id")
	private String platformId;

	@FieldName(name = "datatype")
	private String dataType;

	@FieldName(name = "SENSOR_NAME")
	private String sensorName;

	@FieldName(name = "sensor_id")
	private String sensorId;
	
	private String reference;

	@FieldName(name = "datafile_id")
	private String datafileId;

	@FieldName(name = "state_agg_count")
	private int stateAggCount;
	
	public Measurement() {
		
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDatafileId() {
		return datafileId;
	}

	public void setDatafileId(String datafileId) {
		this.datafileId = datafileId;
	}

	public int getStateAggCount() {
		return stateAggCount;
	}

	public void setStateAggCount(int stateAggCount) {
		this.stateAggCount = stateAggCount;
	}
	
}
