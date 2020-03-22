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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.postgis.PGbox3d;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PGobject;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import com.mchange.v2.c3p0.impl.NewProxyConnectionUnwrapper;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class PostgresDatabaseConnection extends DatabaseConnection {
	
	private static final String HOST_HEADER = "jdbc:postgresql://";

	private static final String CONGIG_TAG = "configuration";
	private static final String CONNECTION_TAG = "connection";

	public PostgresDatabaseConnection() {
		super(); // Just formality :)
		
		configurationFilename = "../org.mwc.debrief.pepys/postgres.ini";
	}

	@Override
	public DatabaseConnection createInstance() throws PropertyVetoException, FileNotFoundException {
		if (INSTANCE == null) {
			final PostgresDatabaseConnection newInstance = new PostgresDatabaseConnection();
			newInstance.initialize();
			INSTANCE = newInstance;
		}
		return INSTANCE;
	}

	@Override
	protected String createLocationQuery(final String tableName, final String columnName) {
		return getAlias(tableName) + "." + columnName + " AS " + getAlias(tableName + columnName) + ", ";
	}

	@Override
	protected WorldLocation createWorldLocation(final ResultSet result, final String columnName) throws SQLException {
		try {
			final PGobject object = (PGobject) result.getObject(columnName);
			if (object instanceof PGpoint) {
				final PGpoint newPoint = (PGpoint) object;
				return new WorldLocation(newPoint.y, newPoint.x, 0);
			} else if (object instanceof PGgeometry) {
				final PGgeometry geometry = (PGgeometry) object;
				if (geometry.getGeometry().getDimension() > 0) {
					final Point point = geometry.getGeometry().getFirstPoint();
					return new WorldLocation(point.getY(), point.getX(), point.getZ());
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String databasePrefix() {
		return "pepys.\"";
	}

	@Override
	public String databaseSuffix() {
		return "\"";
	}

	@Override
	protected void initialize() throws PropertyVetoException, FileNotFoundException {
		super.initialize();
		final Properties props = new Properties();

		final HashMap<String, String> databaseTagConfig = databaseConfiguration.getCategory(CONGIG_TAG);
		final HashMap<String, String> databaseTagConnection = databaseConfiguration.getCategory(CONNECTION_TAG);

		props.setProperty("user", databaseTagConnection.get("user"));
		props.setProperty("password", databaseTagConnection.get("password"));
		props.setProperty("ssl", databaseTagConnection.get("ssl"));

		pool = new ComboPooledDataSource();
		pool.setCheckoutTimeout(TIME_OUT);
		pool.setDriverClass(databaseTagConfig.get("driver"));
		final String completePath = HOST_HEADER + databaseTagConnection.get("host") + ":"
				+ databaseTagConnection.get("db_port") + "/" + databaseTagConnection.get("db_name");
		pool.setJdbcUrl(completePath);
		pool.setProperties(props);
	}

	@Override
	protected void loadExtention(final Connection connection, final Statement statement)
			throws SQLException, ClassNotFoundException {
		/*
		 * Add the geometry types to the connection.
		 */
		((org.postgresql.PGConnection) NewProxyConnectionUnwrapper
				.unWrapperInnerConnection((NewProxyConnection) connection)).addDataType("geometry", PGgeometry.class);
		((org.postgresql.PGConnection) NewProxyConnectionUnwrapper
				.unWrapperInnerConnection((NewProxyConnection) connection)).addDataType("box3d", PGbox3d.class);
	}

	@Override
	public Collection<? extends Condition> createAreaFilter(final WorldArea currentArea, final Class<?> type) {
		final WorldLocation topLeft = currentArea.getTopLeft();
		final WorldLocation bottomRight = currentArea.getBottomRight();
		final WorldLocation topRight = currentArea.getTopRight();
		final WorldLocation bottomLeft = currentArea.getBottomLeft();

		final String polygonArea = "POLYGON((" + topLeft.getLat() + " " + topLeft.getLong() + "," + bottomLeft.getLat()
				+ " " + bottomLeft.getLong() + "," + bottomRight.getLat() + " " + bottomRight.getLong() + ","
				+ topRight.getLat() + " " + topRight.getLong() + "," + topLeft.getLat() + " " + topLeft.getLong()
				+ "))";

		return null;
	}

}
