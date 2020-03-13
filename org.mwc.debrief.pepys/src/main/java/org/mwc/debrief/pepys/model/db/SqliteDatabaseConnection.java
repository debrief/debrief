package org.mwc.debrief.pepys.model.db;

import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import MWC.GenericData.WorldLocation;

public class SqliteDatabaseConnection extends DatabaseConnection {

	public static final String LOCATION_COORDINATES = "XYZ";
	public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.000000";
	public static final String DATABASE_FILE_PATH = "test2.db";

	public SqliteDatabaseConnection() throws PropertyVetoException {
		super(); // Just formality :)
	}
	
	protected void initialize() throws PropertyVetoException {
		// enabling dynamic extension loading
		// absolutely required by SpatiaLite
		final SQLiteConfig config = new SQLiteConfig();
		config.enableLoadExtension(true);

		pool = new ComboPooledDataSource();
		pool.setCheckoutTimeout(TIME_OUT);
		pool.setDriverClass("org.sqlite.JDBC");
		final String completePath = "jdbc:sqlite:" + DATABASE_FILE_PATH;
		pool.setJdbcUrl(completePath);
		pool.setProperties(config.toProperties());
	}

	@Override
	protected String createLocationQuery(String tableName, String columnName) {
		final StringBuilder query = new StringBuilder();
		for (final char c : LOCATION_COORDINATES.toCharArray()) {
			query.append(c);
			query.append(" (");
			query.append(tableName);
			query.append(".");
			query.append(columnName);
			query.append(") as ");
			query.append(tableName);
			query.append(columnName);
			query.append("_");
			query.append(c);
			query.append(", ");
		}
		return query.toString();
	}

	@Override
	protected WorldLocation createWorldLocation(ResultSet result, String columnName) throws SQLException {
		// WARNING.
		// THIS WILL CLASSIFY NULL LOCATION AT (0,0,0)
		// SAUL
		final double[] values = new double[3];
		for (int i = 0; i < values.length && i < LOCATION_COORDINATES.length(); i++) {
			values[i] = result.getDouble(columnName + "_" + LOCATION_COORDINATES.charAt(i));
		}
		return new WorldLocation(values[0], values[1], values[2]);
	}

	@Override
	public DatabaseConnection createInstance() throws PropertyVetoException {
		if (INSTANCE == null) {
			final SqliteDatabaseConnection newInstance = new SqliteDatabaseConnection();
			newInstance.initialize();
			INSTANCE = newInstance;
		}
		return INSTANCE;
	}

	
}
