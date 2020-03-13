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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.db.annotation.AnnotationsUtils;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.OneToOne;
import org.mwc.debrief.pepys.model.db.annotation.Time;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldLocation;

/**
 * Created by Saul - saulhidalgoaular@gmail.com
 */
public abstract class DatabaseConnection {
	public static DatabaseConnection INSTANCE = null;
	public static final boolean SHOW_SQL = true;
	public static final String WHERE_CONNECTOR = " AND ";
	public static final char ESCAPE_CHARACTER = '\'';
	
	protected HashMap<String, String> aliasRenamingMap = new HashMap<String, String>();

	public DatabaseConnection() {
		
	}
	
	public abstract DatabaseConnection createInstance() throws PropertyVetoException;

	protected abstract String createLocationQuery(final String tableName, final String columnName);

	protected abstract WorldLocation createWorldLocation(final ResultSet result, final String columnName)
			throws SQLException;
	
	protected abstract void loadExtention(final Connection connection, final Statement statement) throws SQLException, ClassNotFoundException;
	
	public static DatabaseConnection getInstance() throws PropertyVetoException {
		return INSTANCE;
	}
	
	public abstract String databasePrefix();
	
	public abstract String databaseSuffix();

	protected ComboPooledDataSource pool;

	protected final int TIME_OUT = 60000;

	private String capitalizeFirstLetter(final String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	private String createQuery(final Class type) {
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
			final Constructor constructor = type.getConstructor();
			final AbstractBean ans = (AbstractBean) constructor.newInstance();
			return " WHERE " + AnnotationsUtils.getField(type, Id.class).getName() + " = ?";
		}
		return "";
	}
	
	public void cleanRenamingBuffer() {
		aliasRenamingMap.clear();
	}

	public <T> List<T> listAll(final Class<T> type, final Collection<Condition> conditions)
			throws PropertyVetoException, SQLException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
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
					queryBuilder.append(WHERE_CONNECTOR);
				}
				queryBuilder.setLength(queryBuilder.length() - WHERE_CONNECTOR.length());
			}

			final String query = queryBuilder.toString();
			if (SHOW_SQL) {
				System.out.println("Query: " + query);
			}

			statement = connection.createStatement();

			loadExtention(connection, statement);
			
			resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				final T instance = storeFieldValue(type, resultSet, "");
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

	private String prepareSelect(final Class<?> type, final List<String> join, final String prefix) {
		final String baseTableName = AnnotationsUtils.getTableName(type);
		final String tableName = prefix + baseTableName;
		final StringBuilder query = new StringBuilder();
		final Field[] fields = type.getDeclaredFields();
		for (final Field field : fields) {
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
				newJoin.append(getAlias(prefix + baseTableName + AnnotationsUtils.getColumnName(field) + AnnotationsUtils.getTableName(field.getType())));
				newJoin.append(" ON ");
				newJoin.append(getAlias(prefix + baseTableName + AnnotationsUtils.getColumnName(field) + AnnotationsUtils.getTableName(field.getType())));
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
		query.setLength(query.length() - 2);
		return query.toString();
	}

	protected <T> T retrieveById(final Object id, final Class<T> type)
			throws SQLException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Connection connection = pool.getConnection();
		ResultSet resultSet = null;
		try {
			final Constructor<T> constructor = type.getConstructor();
			final T ans = constructor.newInstance();
			final String query = createQuery(type) + createQueryQueryIDPart(type);

			if (SHOW_SQL) {
				System.out.println("Query: " + query);
			}

			final PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, id.toString());
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				final Field[] fields = type.getFields();
				for (final Field field : fields) {
					final Class fieldType = field.getClass();
					final Method method = type.getMethod("set" + capitalizeFirstLetter(field.getName()), fieldType);
					method.invoke(ans, resultSet.getObject(field.getName()));
				}
			}

			connection.close();

			return ans;
		} finally {
			if (connection != null) {
				connection.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	public <T> T storeFieldValue(final Class<T> type, final ResultSet resultSet, final String prefix)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			SQLException {
		final Constructor<T> constructor = type.getConstructor();
		final T instance = constructor.newInstance();
		final Field[] fields = type.getDeclaredFields();
		for (final Field field : fields) {
			final Class fieldType = field.getType();
			final Method method = type.getDeclaredMethod("set" + capitalizeFirstLetter(field.getName()), fieldType);

			final String thisColumnName = getAlias(prefix + AnnotationsUtils.getTableName(type)
					+ AnnotationsUtils.getColumnName(field));
			if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
				method.invoke(instance, storeFieldValue(fieldType, resultSet,
						prefix + AnnotationsUtils.getTableName(type) + AnnotationsUtils.getColumnName(field)));
			} else if (int.class == fieldType) {
				try {
					method.invoke(instance, resultSet.getInt(thisColumnName));
				}catch (Exception e) {
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

		}
		return instance;
	}
	
	public Collection<Condition> createPeriodFilter(final TimePeriod period, final Class type){
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
	
	protected String intToString(final int _id) {
		int id = _id;
		final int BASE = 26;
		final StringBuilder ans = new StringBuilder();
		while(id > 0) {
			int letter = (id - 1) % BASE;
			ans.append((char)('A' + letter));
			id = (id - letter) / BASE;
		}
		
		return ans.reverse().toString();
	}
	
	protected String getAlias(final String realTableName) {
		if (!aliasRenamingMap.containsKey(realTableName)) {
			// It hasn't been previously renamed.
			aliasRenamingMap.put(realTableName, intToString(aliasRenamingMap.size() + 10000));
		}
		return aliasRenamingMap.get(realTableName);
		
	}
}