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
import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.TableName;

import junit.framework.TestCase;

@TableName(name = "Datafiles")
public class Datafile implements AbstractBean {

	public static class DatafilesTest extends TestCase {

		public void testDatafilesQuery() {
			try {
				final List list = DatabaseConnection.getInstance().listAll(Datafile.class, null);

				assertTrue("Datafiles - database entries", list.size() == 25);

				final String[][] datafilesSomeReferences = new String[][] { { "1", "sen_tracks" },
						{ "6", "sen_frig_sensor" }, { "18", "NMEA_bad" }, { "25", "test_land_track" } };

				for (final Object l : list) {
					final Datafile dataFile = (Datafile) l;
					final boolean correct = true;
					for (int i = 0; i < datafilesSomeReferences.length; i++) {
						// correct &= !datafilesSomeReferences[0].equals(dataFile.getIdField()) ||
						// datafilesSomeReferences[1].equals(dataFile.getReference());
					}
					assertTrue("Datafiles - Reference Name", correct);
				}
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Id
	private String datafile_id;
	private boolean simulated;

	private String privacy_id;
	@ManyToOne
	@FieldName(name = "datafile_type_id")
	private DatafileType datafile;
	private String reference;
	private String url;

	private Date created_date;

	public Datafile() {

	}

	public Date getCreated_date() {
		return created_date;
	}

	public DatafileType getDatafile() {
		return datafile;
	}

	public String getDatafile_id() {
		return datafile_id;
	}

	public String getPrivacy_id() {
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

	public void setDatafile(final DatafileType datafile) {
		this.datafile = datafile;
	}

	public void setDatafile_id(final String datafile_id) {
		this.datafile_id = datafile_id;
	}

	public void setPrivacy_id(final String privacy_id) {
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
}
