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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mwc.debrief.pepys.model.PepsysException;
import org.mwc.debrief.pepys.model.db.Condition;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.annotation.Filterable;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.TableName;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.mwc.debrief.pepys.model.db.config.LoaderOption;
import org.mwc.debrief.pepys.model.db.config.LoaderOption.LoaderType;

import junit.framework.TestCase;

@TableName(name = "Datafiles")
public class Datafile implements AbstractBean {

	public static class DatafilesTest extends TestCase {

		public void testDatafilesQuery() {
			if (System.getProperty("os.name").toLowerCase().indexOf("mac") == -1) {
				try {
					final DatabaseConfiguration _config = new DatabaseConfiguration();
					ConfigurationReader.loadDatabaseConfiguration(_config,
							new LoaderOption[] { new LoaderOption(LoaderType.DEFAULT_FILE,
									DatabaseConnection.DEFAULT_SQLITE_TEST_DATABASE_FILE) });
					final SqliteDatabaseConnection sqlite = new SqliteDatabaseConnection();
					sqlite.initializeInstance(_config);
					final List<Datafile> list = sqlite.listAll(Datafile.class, (Collection<Condition>) null);

					assertTrue("Datafiles - database entries", list.size() == 25);

					final String[][] datafilesSomeReferences = new String[][] {
							{ "db8692a392924d27bfacdbddc4eb9a29", "NMEA_TRIAL.log" },
							{ "ab39eec7c29f4cee871b98474b00bfdc", "sen_ssk_freq.dsf" },
							{ "f6384dd27282439aa164acf6be1bb393", "rep_test1.rep" },
							{ "265db426cf194e498ce38911e06d17e0", "uk_track.rep" } };

					for (int i = 0; i < datafilesSomeReferences.length; i++) {
						boolean exist = false;
						for (final Datafile dataFile : list) {
							exist |= datafilesSomeReferences[i][0].equals(dataFile.getDatafile_id())
									&& datafilesSomeReferences[i][1].equals(dataFile.getReference());
						}

						assertTrue("Datafiles - Reference Name", exist);
					}

				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException
						| ClassNotFoundException | IOException | PepsysException e) {
					e.printStackTrace();
					fail("Couldn't connect to database or query error:" + e);
				}
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
	@Filterable
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
