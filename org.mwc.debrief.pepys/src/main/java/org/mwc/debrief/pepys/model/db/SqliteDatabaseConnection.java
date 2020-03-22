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
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.mwc.debrief.pepys.model.db.annotation.AnnotationsUtils;
import org.mwc.debrief.pepys.model.db.annotation.Location;
import org.sqlite.SQLiteConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class SqliteDatabaseConnection extends DatabaseConnection {

	public static final String LOCATION_COORDINATES = "XYZ";
	public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.000000";
	public static final String CONFIGURATION_TAG = "database";

	public SqliteDatabaseConnection() {
		super(); // Just formality :)
		configurationFilename = "../org.mwc.debrief.pepys/sqlite.ini";
	}

	@Override
	public DatabaseConnection createInstance() throws PropertyVetoException, FileNotFoundException {
		if (INSTANCE == null) {
			final SqliteDatabaseConnection newInstance = new SqliteDatabaseConnection();
			newInstance.initialize();
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
	protected void initialize() throws PropertyVetoException, FileNotFoundException {
		super.initialize();
		// enabling dynamic extension loading
		// absolutely required by SpatiaLite
		final SQLiteConfig config = new SQLiteConfig();
		config.enableLoadExtension(true);

		pool = new ComboPooledDataSource();
		pool.setCheckoutTimeout(TIME_OUT);

		final HashMap<String, String> databaseTagConfiguration = databaseConfiguration.getCategory(CONFIGURATION_TAG);
		
		pool.setDriverClass(databaseTagConfiguration.get("driver"));

		final String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		pool.setJdbcUrl(path + databaseTagConfiguration.get("path"));
		pool.setProperties(config.toProperties());
	}

	@Override
	protected void loadExtention(final Connection connection, final Statement statement) throws SQLException {
		// loading SpatiaLite
		statement.execute("SELECT load_extension('mod_spatialite')");

		// enabling Spatial Metadata
		// using v.2.4.0 this automatically initializes SPATIAL_REF_SYS and
		// GEOMETRY_COLUMNS
		final String sql = "SELECT InitSpatialMetadata()";
		statement.execute(sql);
	}

	@Override
	public Collection<? extends Condition> createAreaFilter(WorldArea currentArea, final Class<?> type) {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();
		
		final Field locationField = AnnotationsUtils.getField(type, Location.class);
		if (locationField != null && currentArea != null) {
			
			final WorldLocation topLeft = currentArea.getTopLeft();
			final WorldLocation bottomRight = currentArea.getBottomRight();
			final WorldLocation topRight = currentArea.getTopRight();
			final WorldLocation bottomLeft = currentArea.getBottomLeft();
	
			final String polygonArea = "POLYGON((" + topLeft.getLat() + " " + topLeft.getLong() + "," + bottomLeft.getLat()
					+ " " + bottomLeft.getLong() + "," + bottomRight.getLat() + " " + bottomRight.getLong() + ","
					+ topRight.getLat() + " " + topRight.getLong() + "," + topLeft.getLat() + " " + topLeft.getLong()
					+ "))";
			final String fieldName = getAlias(AnnotationsUtils.getTableName(type)) + "."
					+ AnnotationsUtils.getColumnName(locationField);
			
			final String geom = "ST_GeomFromText(" + ESCAPE_CHARACTER + polygonArea + ESCAPE_CHARACTER + ")";
			final String contains = "ST_Contains(" + geom + "," + fieldName + ")";
			System.out.println(contains);

			conditions.add(new Condition(contains));
		}

		return conditions;
	}

}
