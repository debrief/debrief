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

package org.mwc.debrief.pepys.model;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.sqlite.SQLiteConfig;

import java.lang.reflect.Field;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import MWC.GenericData.WorldLocation;

/**
 * Created by Saul on 10/23/2016.
 */
public class DatabaseConnection {
    private static DatabaseConnection INSTANCE = null;
    private ComboPooledDataSource pool ;
    private final int TIME_OUT = 60000;
    public static final boolean SHOW_SQL = true;
    public static final String LOCATION_COORDINATES = "XYZ"; 

    private DatabaseConnection() {
    }

    public static DatabaseConnection getInstance() throws PropertyVetoException, SQLException {
        if ( INSTANCE == null ){
            INSTANCE = new DatabaseConnection();
            INSTANCE.initialize("test2.db");
        }
        return INSTANCE;
    }

    protected void initialize(final String path) throws PropertyVetoException, SQLException  {
    	Connection connection = null;
    	Statement stmt = null;
    	try {
            // enabling dynamic extension loading
            // absolutely required by SpatiaLite
            SQLiteConfig config = new SQLiteConfig();
            config.enableLoadExtension(true);
            
    		pool = new ComboPooledDataSource();
    		pool.setCheckoutTimeout(TIME_OUT);
            pool.setDriverClass("org.sqlite.JDBC");
            final String completePath = "jdbc:sqlite:" + path;
            pool.setJdbcUrl(completePath);
            pool.setProperties(config.toProperties());
    	}finally {
    		if (stmt != null) {
    			stmt.close();
    		}
    		if (connection != null) {
    			connection.close();
    		}
    	}
    }

    public List<AbstractBean> listAll(final Class type, final String condition) throws PropertyVetoException, SQLException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	Connection connection = pool.getConnection();
    	List<AbstractBean> ans = new ArrayList<>();
    	ResultSet resultSet = null;
    	Statement statement = null; 
    	try
    	{
    		final Constructor constructor = type.getConstructor();
    		String query = createQuery(type);
    		if ( condition != null ) {
    			query = query + " WHERE " + condition;
    		}
    		if (SHOW_SQL) {
    			System.out.println("Query: " + query);
    		}

    		statement = connection.createStatement();
    		
            // loading SpatiaLite
    		statement.execute("SELECT load_extension('mod_spatialite')");
            
            // enabling Spatial Metadata
            // using v.2.4.0 this automatically initializes SPATIAL_REF_SYS and GEOMETRY_COLUMNS
            String sql = "SELECT InitSpatialMetadata()";
            statement.execute(sql);
            
            resultSet = statement.executeQuery(query);

            while ( resultSet.next() ){
        		AbstractBean instance = (AbstractBean) constructor.newInstance();
            	final Field[] fields = type.getDeclaredFields();
            	for (Field field : fields) {
            		final Class fieldType = field.getType();
            		final Method method = type.getDeclaredMethod("set" + capitalizeFirstLetter(field.getName()), fieldType);
            		if (int.class == fieldType) {
            			method.invoke(instance, resultSet.getInt(field.getName()));
            		}else if (String.class == fieldType) {
            			method.invoke(instance, resultSet.getString(field.getName()));
            		}else if (Date.class == fieldType) {
            			method.invoke(instance, resultSet.getDate(field.getName()));
            		}else if (boolean.class == fieldType) {
            			method.invoke(instance, resultSet.getBoolean(field.getName()));
            		}else if (Timestamp.class == fieldType){
            			method.invoke(instance, resultSet.getTimestamp(field.getName()));
            		}else if (double.class == fieldType){
            			method.invoke(instance, resultSet.getDouble(field.getName()));
            		}else if (WorldLocation.class == fieldType){
            			// WARNING.
            			// THIS WILL CLASSIFY NULL LOCATION AT (0,0,0)
            			// SAUL
            			double [] values = new double[3];
            			for (int i = 0; i < values.length && i < LOCATION_COORDINATES.length(); i++) {
            				values[i] = resultSet.getDouble(field.getName() + "_" + LOCATION_COORDINATES.charAt(i));
            			}
            			final WorldLocation newLocation = new WorldLocation(values[0], values[1], values[2]);
            			method.invoke(instance, newLocation);
            		}else {
            			try
            			{
                			// Unknown type. We will find out what to do here later.
                			method.invoke(instance, resultSet.getObject(field.getName()));            				
            			}catch (Exception e) {
							e.printStackTrace();
						}

            		}
            		
            	}
            	ans.add(instance);
            }
    		
            return ans;
    	}finally {
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
    
    private String capitalizeFirstLetter(final String str) {
    	if (str == null || str.isEmpty()) {
    		return str;
    	}
    	return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    protected <T> T retrieveById(Object id, Class<T> type) throws SQLException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	Connection connection = pool.getConnection();
    	ResultSet resultSet = null;
    	try
    	{
    		Constructor<T> constructor = type.getConstructor();
    		T ans = constructor.newInstance();
    		final String query = createQuery(type) + createQueryQueryIDPart(type);

    		if (SHOW_SQL) {
    			System.out.println("Query: " + query);
    		}
    		
    		PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, id.toString());
            resultSet = statement.executeQuery();

            if ( resultSet.next() ){
            	final Field[] fields = type.getFields();
            	for (Field field : fields) {
            		final Class fieldType = field.getClass();
            		final Method method = type.getMethod("set" + capitalizeFirstLetter(field.getName()), fieldType);
            		method.invoke(ans, resultSet.getObject(field.getName()));
            	}
            }

            connection.close();
    		
            return ans;
    	}finally {
    		if (connection != null) {
        		connection.close();
    		}
    		if (resultSet != null) {
    			resultSet.close();
    		}
    	}
    }
    
    private String createQuery(Class type) {
    	final StringBuilder query = new StringBuilder();
    	query.append("SELECT ");
    	final Field[] fields = type.getDeclaredFields();
    	for (Field field : fields) {
    		if (field.getType().equals(WorldLocation.class)) {
    			for (char c : LOCATION_COORDINATES.toCharArray()) {
    				query.append(c);
    				query.append(" (");
        			query.append(field.getName());
        			query.append(") as ");
        			query.append(field.getName());
        			query.append("_");
        			query.append(c);
            		query.append(",");
    			}
    		}else{
    			query.append(field.getName());
        		query.append(",");
    		}
    	}
    	query.setLength(query.length() - 1);
    	query.append(" FROM ");
    	query.append(type.getSimpleName());
    	return query.toString();
    }

    private String createQueryQueryIDPart(Class type) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	if (AbstractBean.class.isAssignableFrom(type)) {
    		final Constructor constructor = type.getConstructor();
    		final AbstractBean ans = (AbstractBean) constructor.newInstance();
    		return " WHERE " + ans.getIdField() + " = ?";
    	}
    	return "";
    }
}