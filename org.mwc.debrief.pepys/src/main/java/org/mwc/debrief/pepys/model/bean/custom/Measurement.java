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

import java.util.List;
import java.util.Scanner;

import org.mwc.debrief.model.utils.OSUtils;
import org.mwc.debrief.pepys.Activator;
import org.mwc.debrief.pepys.model.bean.Comment;
import org.mwc.debrief.pepys.model.bean.Contact;
import org.mwc.debrief.pepys.model.bean.Datafile;
import org.mwc.debrief.pepys.model.bean.Platform;
import org.mwc.debrief.pepys.model.bean.Sensor;
import org.mwc.debrief.pepys.model.bean.SensorType;
import org.mwc.debrief.pepys.model.bean.State;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.PostgresDatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.mwc.debrief.pepys.model.db.config.LoaderOption;
import org.mwc.debrief.pepys.model.db.config.LoaderOption.LoaderType;
import org.mwc.debrief.pepys.model.tree.TreeStructurable;

import junit.framework.TestCase;

public class Measurement {
	
	public static final String STATE_TYPE = "STATES";
	public static final String COMMENT_TYPE = "COMMENTS";
	public static final String CONTACTS_TYPE = "CONTACTS";
	
	public static final String MEASUREMENTS_FILE = "/measurements.sql";
	
	public static class MeasurementTest extends TestCase{
		
		public void testCustomQuery() {

			final DatabaseConfiguration _config = new DatabaseConfiguration();
			try {
				ConfigurationReader.loadDatabaseConfiguration(_config,
						new LoaderOption[] { new LoaderOption(LoaderType.DEFAULT_FILE,
								DatabaseConnection.DEFAULT_POSTGRES_DATABASE_FILE) });
				
				final PostgresDatabaseConnection postgresDatabaseConnection = new PostgresDatabaseConnection();
				postgresDatabaseConnection.initializeInstance(_config);
				final Scanner scanner = new Scanner(OSUtils.getInputStreamResource(Measurement.class, MEASUREMENTS_FILE, Activator.PLUGIN_ID));
				final StringBuilder builder = new StringBuilder();
				while (scanner.hasNextLine()) {
					builder.append(scanner.nextLine());
					builder.append("\n");
				}
				final List<Measurement> list = postgresDatabaseConnection.listAll(Measurement.class, builder.toString(), null);
				
				for (Measurement measurement : list) {
					System.out.println(measurement.getDatafileId());
				}
				scanner.close();
				
			} catch (Exception e) {
				fail("Error running custom query on Measurements " + e.getMessage());
			}
			
		}
		
	}
	
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

	public TreeStructurable export() {
		if (STATE_TYPE.equals(getDataType())) {
			return exportToState();
		}else if (COMMENT_TYPE.equals(getDataType())) {
			return exportToComment();
		}else if (CONTACTS_TYPE.equals(getDataType())) {
			return exportToContact();
		}
		// This should not happen
		return null;
	}
	
	public Comment exportToComment() {
		final Comment answer = new Comment();
		final Platform platform = new Platform();
		final Datafile datafile = new Datafile();
		
		platform.setPlatform_id(getPlatformId());
		platform.setName(getPlatformName());
		answer.setPlatform(platform);
		datafile.setReference(getReference());
		datafile.setDatafile_id(getDatafileId());
		answer.setDatafile(datafile);
		
		return answer;
	}
	
	public Contact exportToContact() {
		final Contact answer = new Contact();
		final Sensor sensor = new Sensor();
		final Platform platform = new Platform();
		final SensorType sensorType = new SensorType();
		final Datafile datafile = new Datafile();
		
		answer.setSensor(sensor);
		sensor.setSensor_id(getSensorId());
		sensorType.setName(getSensorName());
		
		sensor.setSensorType(sensorType);
		sensor.setPlatform(platform);
		platform.setName(getPlatformName());
		platform.setPlatform_id(getPlatformId());
		
		datafile.setDatafile_id(getDatafileId());
		datafile.setReference(getReference());
		answer.setDatafile(datafile);
		
		return answer;
	}
	
	public State exportToState() {
		final State answer = new State();
		final Sensor sensor = new Sensor();
		final Platform platform = new Platform();
		final SensorType sensorType = new SensorType();
		final Datafile datafile = new Datafile();
		
		answer.setSensor(sensor);
		sensor.setSensor_id(getSensorId());
		sensorType.setName(getSensorName());
		
		sensor.setSensorType(sensorType);
		sensor.setPlatform(platform);
		platform.setName(getPlatformName());
		platform.setPlatform_id(getPlatformId());
		
		datafile.setDatafile_id(getDatafileId());
		datafile.setReference(getReference());
		answer.setDatafile(datafile);
		
		return answer;
	}
	
}
