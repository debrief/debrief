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
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import org.mwc.debrief.pepys.model.PepsysException;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;
import org.postgis.PGbox3d;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PGobject;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import com.mchange.v2.c3p0.impl.NewProxyConnectionUnwrapper;

import MWC.GenericData.WorldLocation;

public class PostgresDatabaseConnection extends DatabaseConnection {

	private static final String HOST_HEADER = "jdbc:postgresql://";

	public PostgresDatabaseConnection() {
		super(); // Just formality :)
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
	protected void initialize(final DatabaseConfiguration _config)
			throws PropertyVetoException, IOException, PepsysException {
		super.initialize(_config);

		final Properties props = new Properties();
		final HashMap<String, String> databaseTagConnection = _config.getCategory(DatabaseConnection.CONFIGURATION_TAG);

		props.setProperty("user", databaseTagConnection.get("db_username"));
		props.setProperty("password", databaseTagConnection.get("db_password"));
		props.setProperty("ssl", "false");

		pool = new ComboPooledDataSource();
		pool.setCheckoutTimeout(TIME_OUT);
		pool.setDriverClass("org.postgresql.Driver");
		final String completePath = HOST_HEADER + databaseTagConnection.get("db_host") + ":"
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
	public String getSRID() {
		return "SRID=4326;";
	}

}
