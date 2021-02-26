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
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.TableName;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.mwc.debrief.pepys.model.db.config.LoaderOption;
import org.mwc.debrief.pepys.model.db.config.LoaderOption.LoaderType;

import junit.framework.TestCase;

@TableName(name = "Privacies")
public class Privacy implements AbstractBean {

	public static class PrivaciesTest extends TestCase {

		public void testPrivaciesQuery() {
			try {
				final DatabaseConfiguration _config = new DatabaseConfiguration();
				ConfigurationReader.loadDatabaseConfiguration(_config,
						new LoaderOption[] { new LoaderOption(LoaderType.DEFAULT_FILE,
								DatabaseConnection.DEFAULT_SQLITE_TEST_DATABASE_FILE) });
				final SqliteDatabaseConnection sqlite = new SqliteDatabaseConnection();
				sqlite.initializeInstance(_config);
				final List<Privacy> list = sqlite.listAll(Privacy.class, (Collection<Condition>) null);

				assertTrue("Privacies - database entries", list.size() == 8);

				final Privacy privacy = sqlite.listById(Privacy.class, "db27a3c5c03347fbbaa5bb3912b52da1");

				assertTrue("Datafiletypes - database entries",
						"db27a3c5c03347fbbaa5bb3912b52da1".equals(privacy.getPrivacy_id())
								&& "Public".equals(privacy.getName()));
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException
					| ClassNotFoundException | IOException | PepsysException e) {
				e.printStackTrace();
				fail("Couldn't connect to database or query error");
			}

		}
	}

	@Id
	private String privacy_id;
	private String name;

	private Date created_date;

	public Privacy() {

	}

	public Date getCreated_date() {
		return created_date;
	}

	public String getName() {
		return name;
	}

	public String getPrivacy_id() {
		return privacy_id;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPrivacy_id(final String privacy_id) {
		this.privacy_id = privacy_id;
	}
}
