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
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.mwc.debrief.model.utils.NativeLibrariesLoader;
import org.mwc.debrief.model.utils.NativeLibrariesLoader.ModSpatialiteAssigner;
import org.mwc.debrief.pepys.Activator;
import org.mwc.debrief.pepys.model.PepsysException;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.sqlite.SQLiteConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import MWC.GenericData.WorldLocation;

public class SqliteDatabaseConnection extends DatabaseConnection {

	public static final String LOCATION_COORDINATES = "XYZ";
	public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.000000";

	public static File nativeFolderPath = null;

	public static String modSpatialiteName = Activator.MOD_SPATIALITE_NAME;

	/**
	 * Method that receives a DatabaseConfiguration and checks if it suits to this
	 * type of connection.
	 *
	 * @param _config
	 * @return
	 */
	public static boolean validateDatabaseConfiguration(final DatabaseConfiguration _config) {
		try {
			final HashMap<String, String> databaseTagConnection = _config
					.getCategory(DatabaseConnection.CONFIGURATION_TAG);
			return (databaseTagConnection.get(DatabaseConnection.CONFIGURATION_DATABASE_TYPE)
					.equals(DatabaseConnection.SQLITE) && databaseTagConnection.containsKey(CONFIGURATION_DB_NAME));
		} catch (final Exception e) {
			return false;
		}
	}

	public SqliteDatabaseConnection() {
		super(); // Just formality :)
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
	public WorldLocation createWorldLocation(final ResultSet result, final String columnName) throws SQLException {
		// WARNING.
		// THIS WILL INTERPRETE NULL LOCATION AT (0,0,0)
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
	public String getBasicDescription() {
		final StringBuilder answer = new StringBuilder();

		final HashMap<String, String> category = getDatabaseConfiguration()
				.getCategory(DatabaseConnection.CONFIGURATION_TAG);
		if (category != null) {
			answer.append("Database Type: ");
			answer.append(category.get(DatabaseConnection.CONFIGURATION_DATABASE_TYPE));
			answer.append("\n");
			answer.append("Database Name: ");
			answer.append(category.get(DatabaseConnection.CONFIGURATION_DB_NAME));
			return answer.toString();
		} else {
			return "No Database configuration found";
		}
	}

	@Override
	/**
	 * We need to disable the SRID for Sqlite.
	 */
	public String getSRID() {
		return "";
	}

	@Override
	protected void initialize(final DatabaseConfiguration _config)
			throws PropertyVetoException, IOException, PepsysException {
		super.initialize(_config);
		// enabling dynamic extension loading
		// absolutely required by SpatiaLite
		final SQLiteConfig config = new SQLiteConfig();
		config.enableLoadExtension(true);

		pool = new ComboPooledDataSource();
		pool.setCheckoutTimeout(TIME_OUT);

		final HashMap<String, String> databaseTagConfiguration = databaseConfiguration.getCategory(CONFIGURATION_TAG);

		final String completePath;
		final String configurationFileName = databaseTagConfiguration.get(CONFIGURATION_DB_NAME);
		if (new File(configurationFileName).exists()) {
			completePath = "jdbc:sqlite:" + configurationFileName;
		} else {
			// let's try a relative path
			String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			path = path.substring(0, path.indexOf(Activator.PLUGIN_ID));
			completePath = "jdbc:sqlite:" + path + configurationFileName;
		}

		pool.setJdbcUrl(completePath);
		pool.setDriverClass("org.sqlite.JDBC");
		pool.setProperties(config.toProperties());

		if (Activator.nativeFolderPath != null) {
			// Ok, we are working from a bundle, let's use that path.
			nativeFolderPath = Activator.nativeFolderPath;
		} else {
			// Let's use a temporary folder
			nativeFolderPath = new File(System.getProperty("java.io.tmpdir") + "/native/");
		}

		try {
			// Let's load the libraries in the initialization.
			NativeLibrariesLoader.loadBundledSpatialite(nativeFolderPath, new ModSpatialiteAssigner() {

				@Override
				public void assign(final String path) {
					modSpatialiteName = path;
				}
			});
		} catch (final Exception e) {
			throw new PepsysException("Database Import",
					"Error while loading the database libraries. Please, check that the \"native\" "
							+ "folder is in \"org.mwc.debrief.pepys\", containing the SQL Spatial Lite Libraries");
		}
	}

	@Override
	protected void loadExtention(final Connection connection, final Statement statement)
			throws SQLException, IOException {
		// loading SpatiaLite

		final String extentionToLoad;
		if (nativeFolderPath != null) {
			final String newPath = nativeFolderPath.getCanonicalPath().toString() + "/";
			if (new File(newPath + modSpatialiteName).isFile()) {
				extentionToLoad = newPath + modSpatialiteName;
			} else {
				extentionToLoad = Activator.MOD_SPATIALITE_NAME;
			}
		} else {
			extentionToLoad = Activator.MOD_SPATIALITE_NAME;
		}
		final String sqlExt = "SELECT load_extension('" + extentionToLoad + "')";
		statement.execute(sqlExt);

		// enabling Spatial Metadata
		// using v.2.4.0 this automatically initializes SPATIAL_REF_SYS and
		// GEOMETRY_COLUMNS
		final String sql = "SELECT InitSpatialMetadata()";
		statement.execute(sql);
	}
}
