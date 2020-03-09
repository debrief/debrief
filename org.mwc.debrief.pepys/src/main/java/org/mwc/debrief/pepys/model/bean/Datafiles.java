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

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;

import junit.framework.TestCase;

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

	public Date getCreated_date() {
		return created_date;
	}

	public int getDatafile_id() {
		return datafile_id;
	}

	public int getDatafile_type_id() {
		return datafile_type_id;
	}

	@Override
	public String getIdField() {
		return "datafile_id";
	}

	public int getPrivacy_id() {
		return privacy_id;
	}

	public String getReference() {
		return reference;
	}

	public boolean getSimulated() {
		return simulated;
	}

	public String getUrl() {
		return url;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setDatafile_id(final int datafile_id) {
		this.datafile_id = datafile_id;
	}

	public void setDatafile_type_id(final int datafile_type_id) {
		this.datafile_type_id = datafile_type_id;
	}

	public void setPrivacy_id(final int privacy_id) {
		this.privacy_id = privacy_id;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public void setSimulated(final boolean simulated) {
		this.simulated = simulated;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public static class DatafilesTest extends TestCase{
		
		public void testDatafilesQuery(){
			try {
				final List list = DatabaseConnection.getInstance().listAll(Datafiles.class, null);
				
				assertTrue("Datafiles - database entries", list.size() == 25);
				
				final String [][] datafilesSomeReferences = new String[][] {{"1", "sen_tracks"},
					{"6", "sen_frig_sensor"}, {"18", "NMEA_bad"}, {"25", "test_land_track"}};
					
				for ( Object l : list ) {
					final Datafiles dataFile = (Datafiles) l;
					boolean correct = true;
					for (int i = 0 ; i < datafilesSomeReferences.length; i++) {
						correct &= !datafilesSomeReferences[0].equals(dataFile.getIdField()) || datafilesSomeReferences[1].equals(dataFile.getReference());
					}
					assertTrue("Datafiles - Reference Name",  correct);
				}
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
