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

package org.mwc.debrief.pepys.model.db;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.mwc.debrief.pepys.Activator;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.sqlite.SQLiteConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import MWC.GenericData.WorldLocation;

public class SqliteDatabaseConnection extends DatabaseConnection {

	public static final String LOCATION_COORDINATES = "XYZ";
	public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.000000";

	public SqliteDatabaseConnection() {
		super(); // Just formality :)
	}
	
	@Override
	public DatabaseConnection createInstance(final DatabaseConfiguration _config) throws PropertyVetoException, FileNotFoundException {
		if (INSTANCE == null) {
			final SqliteDatabaseConnection newInstance = new SqliteDatabaseConnection();
			newInstance.databaseConfiguration = _config;
			newInstance.initialize(_config);
			INSTANCE = newInstance;
		}
		return INSTANCE;
	}

	@Override
	protected String createLocationQuery(final String tableName, final String columnName) {
		final StringBuilder query = new StringBuilder();
		for (final char c : LOCATION_COORDINATES.toCharArray()) {
			query.append(c);
			query.append(" (");
			query.append(getAlias(tableName));
			query.append(".");
			query.append(columnName);
			query.append(") as ");
			query.append(getAlias(tableName + columnName));
			query.append("_");
			query.append(c);
			query.append(", ");
		}
		return query.toString();
	}

	@Override
	protected WorldLocation createWorldLocation(final ResultSet result, final String columnName) throws SQLException {
		// WARNING.
		// THIS WILL CLASSIFY NULL LOCATION AT (0,0,0)
		// SAUL
		final double[] values = new double[3];
		for (int i = 0; i < values.length && i < LOCATION_COORDINATES.length(); i++) {
			values[i] = result.getDouble(columnName + "_" + LOCATION_COORDINATES.charAt(i));
		}
		return new WorldLocation(values[1], values[0], values[2]);
	}

	@Override
	public String databasePrefix() {
		return "";
	}

	@Override
	public String databaseSuffix() {
		return "";
	}

	@Override
	protected void initialize(final DatabaseConfiguration _config) throws PropertyVetoException, FileNotFoundException {
		// enabling dynamic extension loading
		// absolutely required by SpatiaLite
		final SQLiteConfig config = new SQLiteConfig();
		config.enableLoadExtension(true);

		pool = new ComboPooledDataSource();
		pool.setCheckoutTimeout(TIME_OUT);

		final HashMap<String, String> databaseTagConfiguration = databaseConfiguration.getCategory(CONFIGURATION_TAG);

		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			// Let's remove the initial / from the windows path;
			path = path.substring(1);
		}
		final String completePath = "jdbc:sqlite:" + path + databaseTagConfiguration.get("db_name");
		pool.setJdbcUrl(completePath);
		pool.setDriverClass("org.sqlite.JDBC");
		pool.setProperties(config.toProperties());
	}

	@Override
	protected void loadExtention(final Connection connection, final Statement statement) throws SQLException {
		// loading SpatiaLite
		
		statement.execute("SELECT load_extension('" + Activator.nativeFolderPath + File.separator+ "mod_spatialite.so')");

		// enabling Spatial Metadata
		// using v.2.4.0 this automatically initializes SPATIAL_REF_SYS and
		// GEOMETRY_COLUMNS
		final String sql = "SELECT InitSpatialMetadata()";
		statement.execute(sql);
	}
}
