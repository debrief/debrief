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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mwc.debrief.model.utils.OSUtils;
import org.mwc.debrief.pepys.Activator;
import org.mwc.debrief.pepys.model.PepsysException;
import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.bean.PlainBean;
import org.mwc.debrief.pepys.model.db.annotation.AnnotationsUtils;
import org.mwc.debrief.pepys.model.db.annotation.AnnotationsUtils.FieldsTable;
import org.mwc.debrief.pepys.model.db.annotation.Filterable;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.Location;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.OneToOne;
import org.mwc.debrief.pepys.model.db.annotation.Time;
import org.mwc.debrief.pepys.model.db.annotation.Transient;
import org.mwc.debrief.pepys.model.db.config.ConfigurationReader;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * Created by Saul - saulhidalgoaular@gmail.com
 */
public abstract class DatabaseConnection {
	public static final boolean SHOW_SQL = true;
	public static final String AND_CONNECTOR = " AND ";
	public static final String OR_CONNECTOR = " OR ";
	public static final String LIKE_CONNECTOR = " LIKE ";
	public static final char ESCAPE_CHARACTER = '\'';

	public static final String CONFIG_FILE_ENV_NAME = "PEPYS_CONFIG_FILE";

	public static final String CONFIGURATION_TAG = "database";

	public static final String CONFIGURATION_DATABASE_TYPE = "db_type";

	public static final String CONFIGURATION_DB_NAME = "db_name";

	public static final String POSTGRES = "postgres";

	public static final String SQLITE = "sqlite";

	public static final String DEFAULT_SQLITE_DATABASE_FILE = "/sqlite.ini";

	public static final String DEFAULT_SQLITE_TEST_DATABASE_FILE = "/sqlite.ini";

	public static final String DEFAULT_POSTGRES_DATABASE_FILE = "/postgres.ini";

	public static final String DEFAULT_DATABASE_FILE = DEFAULT_SQLITE_DATABASE_FILE;

	public static final String GENERIC_CONNECTION_ERROR = "Please, check that the username, password and URL in the configuration file are correct.\nDebrief has been unable to connect to the database.";

	public static void loadDatabaseConfiguration(final DatabaseConfiguration _config,
			final InputStream configurationStream) throws FileNotFoundException {
		if (configurationStream != null) {
			ConfigurationReader.parseConfigurationFile(_config, configurationStream);
		} else {
			throw new FileNotFoundException("DatabaseConnectionException we have received a null inputstream");
		}
	}

	public static void loadDatabaseConfiguration(final DatabaseConfiguration _config, final String _defaultConfigFile)
			throws PropertyVetoException, IOException {

		final String configurationFile = System.getenv(CONFIG_FILE_ENV_NAME);

		InputStream configurationFileStream = null;
		try {
			if (configurationFile != null) {
				if (new File(configurationFile).isFile()) {
					// Here we are simply load the file as given
					configurationFileStream = new FileInputStream(new File(configurationFile));
				} else {
					// show error:
					// "Config file specified in "+ CONFIG_FILE_ENV_NAME + " environment variable
					// not found:" + configurationFilename
					throw new FileNotFoundException("DatabaseConnectionException requested file " + configurationFile
							+ " but it is not a valid file");

				}
			} else {
				configurationFileStream = OSUtils.getInputStreamResource(DatabaseConnection.class, _defaultConfigFile,
						Activator.PLUGIN_ID);
			}

			loadDatabaseConfiguration(_config, configurationFileStream);
		} finally {
			if (configurationFileStream != null) {
				configurationFileStream.close();
			}
		}
	}

	protected HashMap<String, String> aliasRenamingMap = new HashMap<String, String>();

	protected DatabaseConfiguration databaseConfiguration;

	protected ComboPooledDataSource pool;

	protected final int TIME_OUT = 3000;

	public DatabaseConnection() {

	}

	private String capitalizeFirstLetter(final String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public void cleanRenamingBuffer() {
		aliasRenamingMap.clear();
	}

	protected void close() {
		if (pool != null) {
			pool.close();
			pool = null;
		}
	}

	public Collection<? extends Condition> createAreaFilter(final WorldArea currentArea, final Class<?> type) {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();

		final Field locationField = AnnotationsUtils.getField(type, Location.class);
		if (locationField != null && currentArea != null) {

			final WorldLocation topLeft = currentArea.getTopLeft();
			final WorldLocation bottomRight = currentArea.getBottomRight();
			final WorldLocation topRight = currentArea.getTopRight();
			final WorldLocation bottomLeft = currentArea.getBottomLeft();

			final String polygonArea = getSRID() + "POLYGON((" + topLeft.getLong() + " " + topLeft.getLat() + ","
					+ bottomLeft.getLong() + " " + bottomLeft.getLat() + "," + bottomRight.getLong() + " "
					+ bottomRight.getLat() + "," + topRight.getLong() + " " + topRight.getLat() + ","
					+ topLeft.getLong() + " " + topLeft.getLat() + "))";
			final String fieldName = getAlias(AnnotationsUtils.getTableName(type)) + "."
					+ AnnotationsUtils.getColumnName(locationField);

			final String geom = "ST_GeomFromText(" + ESCAPE_CHARACTER + polygonArea + ESCAPE_CHARACTER + ")";
			final String contains = "ST_Contains(" + geom + "," + fieldName + ")";

			conditions.add(new Condition(contains));
		}

		return conditions;
	}

	protected abstract String createLocationQuery(final String tableName, final String columnName);

	public Collection<Condition> createPeriodFilter(final TimePeriod period, final Class<?> type) {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();

		// Let's filter by Period.
		final Field timeField = AnnotationsUtils.getField(type, Time.class);
		if (timeField != null) {
			final SimpleDateFormat sqlDateFormat = new SimpleDateFormat(SqliteDatabaseConnection.SQLITE_DATE_FORMAT);
			final String initDate = sqlDateFormat.format(period.getStartDTG().getDate());
			final String endDate = sqlDateFormat.format(period.getEndDTG().getDate());

			final String fieldName = getAlias(AnnotationsUtils.getTableName(type)) + "."
					+ AnnotationsUtils.getColumnName(timeField);

			conditions.add(new Condition(DatabaseConnection.ESCAPE_CHARACTER + initDate
					+ DatabaseConnection.ESCAPE_CHARACTER + " <= " + fieldName));
			conditions.add(new Condition(fieldName + " <= " + DatabaseConnection.ESCAPE_CHARACTER + endDate
					+ DatabaseConnection.ESCAPE_CHARACTER));
		}

		return conditions;
	}

	private String createQuery(final Class<?> type) {
		final ArrayList<String> joins = new ArrayList<String>();
		final String tableName = AnnotationsUtils.getTableName(type);

		final StringBuilder query = new StringBuilder();
		query.append("SELECT ");
		query.append(prepareSelect(type, joins, ""));
		query.append(" FROM ");
		query.append(databasePrefix());
		query.append(tableName);
		query.append(databaseSuffix());
		query.append(" AS ");
		query.append(getAlias(tableName));

		for (final String join : joins) {
			query.append(join);
		}
		return query.toString();
	}

	private <T> String createQueryQueryIDPart(final Class<T> type) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (AbstractBean.class.isAssignableFrom(type)) {
			return " WHERE " + AnnotationsUtils.getField(type, Id.class).getName() + " = ?";
		}
		return "";
	}

	public Collection<? extends Condition> createTextFilter(final String textFilter, final Class<?> type) {
		final ArrayList<Condition> conditions = new ArrayList<Condition>();

		if (textFilter != null && !textFilter.isEmpty()) {
			final Collection<FieldsTable> filterableFields = AnnotationsUtils.getRecursiveFields(type, Filterable.class,
					"");

			final StringBuilder builder = new StringBuilder();
			builder.append("(");
			for (final FieldsTable field : filterableFields) {
				builder.append("LOWER( ");
				builder.append(getAlias(field.getTableName()));
				builder.append(".");
				builder.append(field.getFieldName());
				builder.append(") ");
				builder.append(LIKE_CONNECTOR);
				builder.append(" ");
				builder.append("\'%");
				builder.append(textFilter.toLowerCase());
				builder.append("%\'");
				builder.append(" ");

				builder.append(OR_CONNECTOR);
			}
			builder.setLength(builder.length() - OR_CONNECTOR.length());
			builder.append(")");
			conditions.add(new Condition(builder.toString()));
		}

		return conditions;
	}

	public abstract WorldLocation createWorldLocation(final ResultSet result, final String columnName)
			throws SQLException;

	public abstract String databasePrefix();

	public abstract String databaseSuffix();

	public boolean doTestQuery(final Class<AbstractBean>[] testBeans) throws SQLException {
		final Connection connection = pool.getConnection();
		final Statement statement = connection.createStatement();

		for (final Class<AbstractBean> bean : testBeans) {
			final String tableName = AnnotationsUtils.getTableName(bean);

			final Field id = AnnotationsUtils.getField(bean, Id.class);
			final String idName = AnnotationsUtils.getColumnName(id);

			final String sql = "SELECT " + idName + " from " + databasePrefix() + tableName + databaseSuffix()
					+ " where false";
			statement.execute(sql);
		}

		return true;
	}

	protected String getAlias(final String realTableName) {
		if (!aliasRenamingMap.containsKey(realTableName)) {
			// It hasn't been previously renamed.
			aliasRenamingMap.put(realTableName, intToString(aliasRenamingMap.size() + 10000));
		}
		return aliasRenamingMap.get(realTableName);

	}

	public abstract String getBasicDescription();

	public DatabaseConfiguration getDatabaseConfiguration() {
		return databaseConfiguration;
	}

	public abstract String getSRID();

	protected void initialize(final DatabaseConfiguration _config)
			throws PropertyVetoException, FileNotFoundException, IOException, PepsysException {
		close();

		// expected to be overwritten by the implementation
	}

	public void initializeInstance(final DatabaseConfiguration _config)
			throws PropertyVetoException, IOException, PepsysException {
		databaseConfiguration = _config;
		initialize(_config);
	}

	protected String intToString(final int _id) {
		int id = _id;
		final int BASE = 26;
		final StringBuilder ans = new StringBuilder();
		while (id > 0) {
			final int letter = (id - 1) % BASE;
			ans.append((char) ('A' + letter));
			id = (id - letter) / BASE;
		}

		return ans.reverse().toString();
	}

	/**
	 * Method that automatically query a list of objects that satisfy a collection
	 * of conditions. This method generates the SQL Query, connects to database, and
	 * run the query, building the list of objects by reflection.
	 *
	 * @param <T>
	 * @param type
	 * @param conditions
	 * @return
	 * @throws PropertyVetoException
	 * @throws SQLException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public <T> List<T> listAll(final Class<T> type, final Collection<Condition> conditions)
			throws PropertyVetoException, SQLException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException, IOException {
		final Connection connection = pool.getConnection();
		final List<T> ans = new ArrayList<>();
		ResultSet resultSet = null;
		Statement statement = null;
		try {
			final StringBuilder queryBuilder = new StringBuilder(createQuery(type));

			if (conditions != null && !conditions.isEmpty()) {
				queryBuilder.append(" WHERE ");
				for (final Condition condition : conditions) {
					queryBuilder.append(condition.getConditionQuery());
					queryBuilder.append(AND_CONNECTOR);
				}
				queryBuilder.setLength(queryBuilder.length() - AND_CONNECTOR.length());
			}

			final String query = queryBuilder.toString();
			if (SHOW_SQL) {
				System.out.println("Query: " + query);
			}

			statement = connection.createStatement();

			loadExtention(connection, statement);

			resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				final T instance = storeFieldValue(type, resultSet, "", true);
				ans.add(instance);
			}

			return ans;
		} finally {
			if (connection != null) {
				connection.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	/**
	 * Method similar to listAll(final Class<T> type, final Collection<Condition>
	 * conditions)
	 *
	 * but it receives a custom query hand-made, creating a list of objects from it.
	 *
	 * @param <T>
	 * @param type
	 * @param customQuery Query to run
	 * @param parameters  Parameters to replace inside the query.
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public <T> List<T> listAll(final Class<T> type, final String customQuery, final List<Object> parameters)
			throws SQLException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (SHOW_SQL) {
			System.out.println(customQuery);
		}

		final Connection connection = pool.getConnection();
		final List<T> ans = new ArrayList<>();
		ResultSet resultSet = null;
		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(customQuery);

			for (int i = 0; parameters != null && i < parameters.size(); i++) {
				final Object valueToAssign = parameters.get(i);
				if (valueToAssign instanceof List) {
					// For now let's assume that all the arrays are String
					final List<Object> listValue = (List<Object>) valueToAssign;
					final String[] arrayValue = new String[listValue.size()];
					for (int j = 0; j < arrayValue.length; j++) {
						arrayValue[j] = listValue.get(j).toString();
					}
					final Array arrayToSet = connection.createArrayOf("text", arrayValue);
					statement.setArray(i + 1, arrayToSet);
				} else {
					statement.setObject(i + 1, valueToAssign);
				}
			}

			loadExtention(connection, statement);

			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				final T instance = storeFieldValue(type, resultSet, "", false);
				ans.add(instance);
			}

			return ans;
		} finally {
			if (connection != null) {
				connection.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	public <T> T listById(final Class<T> type, final Object id)
			throws SQLException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Connection connection = pool.getConnection();
		ResultSet resultSet = null;
		try {
			final String query = createQuery(type) + createQueryQueryIDPart(type);

			if (SHOW_SQL) {
				System.out.println("Query: " + query);
			}

			final PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, id.toString());
			resultSet = statement.executeQuery();

			final T instance = storeFieldValue(type, resultSet, "", true);

			connection.close();

			return instance;
		} finally {
			if (connection != null) {
				connection.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	protected abstract void loadExtention(final Connection connection, final Statement statement)
			throws SQLException, ClassNotFoundException, IOException;

	private String prepareSelect(final Class<?> type, final List<String> join, final String prefix) {
		final String baseTableName = AnnotationsUtils.getTableName(type);
		final String tableName = prefix + baseTableName;
		final StringBuilder query = new StringBuilder();
		final Field[] fields = type.getDeclaredFields();
		for (final Field field : fields) {
			if (field.isAnnotationPresent(Transient.class)) {
				// let's just skip it, because it is probably being used
				// by another process or it doesn't come from database.
			} else {
				final String columnName = AnnotationsUtils.getColumnName(field);

				if (field.getType().equals(WorldLocation.class)) {
					query.append(createLocationQuery(tableName, columnName));
				} else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
					final StringBuilder newJoin = new StringBuilder();
					if (field.isAnnotationPresent(ManyToOne.class)) {
						newJoin.append(" INNER ");
					} else if (field.isAnnotationPresent(OneToOne.class)) {
						newJoin.append(" LEFT ");
					}
					newJoin.append(" JOIN ");
					newJoin.append(databasePrefix());
					newJoin.append(AnnotationsUtils.getTableName(field.getType()));
					newJoin.append(databaseSuffix());
					newJoin.append(" AS ");
					newJoin.append(getAlias(prefix + baseTableName + AnnotationsUtils.getColumnName(field)
							+ AnnotationsUtils.getTableName(field.getType())));
					newJoin.append(" ON ");
					newJoin.append(getAlias(prefix + baseTableName + AnnotationsUtils.getColumnName(field)
							+ AnnotationsUtils.getTableName(field.getType())));
					newJoin.append(".");
					newJoin.append(AnnotationsUtils.getField(field.getType(), Id.class).getName());
					newJoin.append(" = ");
					newJoin.append(getAlias(prefix + baseTableName));
					newJoin.append(".");
					newJoin.append(AnnotationsUtils.getColumnName(field));
					join.add(newJoin.toString());
					query.append(prepareSelect(field.getType(), join,
							prefix + baseTableName + AnnotationsUtils.getColumnName(field)));
					query.append(", ");
				} else {
					query.append(getAlias(prefix + baseTableName));
					query.append(".");
					query.append(field.getName());
					query.append(" AS ");
					query.append(getAlias(prefix + baseTableName + field.getName()));
					query.append(", ");
				}
			}
		}
		query.setLength(query.length() - 2);
		return query.toString();
	}

	public <T> T storeFieldValue(final Class<T> type, final ResultSet resultSet, final String prefix,
			final boolean useAlias) throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, SQLException {
		final Constructor<T> constructor = type.getConstructor();
		final T instance = constructor.newInstance();
		if (PlainBean.class.isAssignableFrom(type)) {
			final PlainBean plainBean = (PlainBean) instance;
			plainBean.retrieveObject(resultSet, this);
		} else {
			final Field[] fields = type.getDeclaredFields();
			for (final Field field : fields) {
				storeField(type, resultSet, prefix, useAlias, instance, field);
			}
		}

		return instance;
	}

  private <T> void storeField(final Class<T> type, final ResultSet resultSet,
      final String prefix, final boolean useAlias, final T instance,
      final Field field)
  {
    final Class<?> fieldType = field.getType();
    try {
    	final Method method = type.getDeclaredMethod("set" + capitalizeFirstLetter(field.getName()),
    			fieldType);

    	final String thisColumnName;
    	if (useAlias) {
    		thisColumnName = getAlias(
    				prefix + AnnotationsUtils.getTableName(type) + AnnotationsUtils.getColumnName(field));
    	} else {
    		thisColumnName = AnnotationsUtils.getColumnName(field);
    	}

    	if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
    		method.invoke(instance, storeFieldValue(fieldType, resultSet,
    				prefix + AnnotationsUtils.getTableName(type) + AnnotationsUtils.getColumnName(field),
    				useAlias));
    	} else if (int.class == fieldType) {
    		try {
    			method.invoke(instance, resultSet.getInt(thisColumnName));
    		} catch (final Exception e) {
    			e.printStackTrace();
    		}
    	} else if (String.class == fieldType) {
    		method.invoke(instance, resultSet.getString(thisColumnName));
    	} else if (Date.class == fieldType) {
    		method.invoke(instance, resultSet.getDate(thisColumnName));
    	} else if (boolean.class == fieldType) {
    		method.invoke(instance, resultSet.getBoolean(thisColumnName));
    	} else if (Timestamp.class == fieldType) {
    		method.invoke(instance, resultSet.getTimestamp(thisColumnName));
    	} else if (double.class == fieldType) {
    		method.invoke(instance, resultSet.getDouble(thisColumnName));
    	} else if (WorldLocation.class == fieldType) {
    		method.invoke(instance, createWorldLocation(resultSet, thisColumnName));
    	} else {
    		try {
    			// Unknown type. We will find out what to do here later.
    			method.invoke(instance, resultSet.getObject(field.getName()));
    		} catch (final Exception e) {
    			e.printStackTrace();
    		}

    	}
    } catch (final Exception e) {
    	// somehow we haven't found the method
    }
  }

}