/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * -------------------
 * JdbcPieDataset.java
 * -------------------
 * (C) Copyright 2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott; Andy
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * Changes
 * -------
 * 26-Apr-2002 : Creation based on JdbcXYDataSet, but extending DefaultPieDataset (BS);
 * 24-Jun-2002 : Removed unnecessary import and local variable (DG);
 * 13-Aug-2002 : Updated Javadoc comments and imports, removed default constructor (DG);
 * 18-Sep-2002 : Updated to support BIGINT (BS);
 *
 */

package com.jrefinery.legacy.data;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Types;

/**
 * A pie dataset that reads data from a database via JDBC.
 * <P>
 * A query should be supplied that returns data in two columns, the first containing
 * VARCHAR data, and the second containing numerical data.  The data is cached in-memory
 * and can be refreshed at any time.
 */
public class JdbcPieDataset extends DefaultPieDataset {

    /** The database connection. */
    Connection connection;

    /** A statement. */
    Statement statement;

    /** The query result set. */
    ResultSet resultSet;

    /** Meta data about the result set. */
    ResultSetMetaData metaData;

    /**
     * Creates a new JdbcPieDataset and establishes a new database connection.
     *
     * @param  url         URL of the database connection.
     * @param  driverName  The database driver class name.
     * @param  user        The database user.
     * @param  passwd      The database users password.
     */
    public JdbcPieDataset(String url,
                          String driverName,
                          String user,
                          String passwd) {

        try {
            Class.forName(driverName);
            this.connection = DriverManager.getConnection(url, user, passwd);
            this.statement = connection.createStatement();
        }
        catch (ClassNotFoundException ex) {
            System.err.println("JdbcPieDataset: cannot find the database driver classes.");
            System.err.println(ex);
        }
        catch (SQLException ex) {
            System.err.println("JdbcPieDataset: cannot connect to this database.");
            System.err.println(ex);
        }
    }

    /**
     * Creates a new JdbcPieDataset using a pre-existing database connection.
     * <P>
     * The dataset is initially empty, since no query has been supplied yet.
     *
     * @param  con  The database connection.
     */
    public JdbcPieDataset(Connection con) {

        try {
            this.connection = con;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new JdbcPieDataset using a pre-existing database connection.
     * <P>
     * The dataset is initialised with the supplied query.
     *
     * @param  con    The database connection.
     * @param  query  The database connection.
     */
    public JdbcPieDataset(Connection con, String query) {
        this(con);
        executeQuery(query);
    }

    /**
     *  ExecuteQuery will attempt execute the query passed to it against the
     *  existing database connection.  If no connection exists then no action
     *  is taken.
     *  The results from the query are extracted and cached locally, thus
     *  applying an upper limit on how many rows can be retrieved successfully.
     *
     * @param  query  The query to be executed
     */
    public void executeQuery(String query) {

        Object xObject = null;
        int column = 0;
        int numberOfColumns = 0;
        int numberOfValidColumns = 0;
        int columnTypes[] = null;

        if (connection == null) {
            System.err.println("There is no database to execute the query.");
            return;
        }

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            numberOfColumns = metaData.getColumnCount();
            if (numberOfColumns != 2) {
                System.err.println("Invalid sql generated.  PieDataSet requires 2 columns only");
            }
            else {

                columnTypes = new int[numberOfColumns];
                columnTypes[0] = Types.VARCHAR;

                /// yes this could be simple but I left it for now, as it follows
                /// standard jdbcXXXXdataset format
                for (column = 1; column < numberOfColumns; column++) {
                    try {
                        int type = metaData.getColumnType(column + 1);
                        switch (type) {

                            case Types.NUMERIC:
                            case Types.REAL:
                            case Types.INTEGER:
                            case Types.DOUBLE:
                            case Types.FLOAT:
                            case Types.BIT:
                            case Types.DATE:
                            case Types.TIMESTAMP:
                            case Types.BIGINT:
                                ++numberOfValidColumns;
                                columnTypes[column] = type;
                                break;
                            default:
                                System.err.println("Unable to load column "
                                                   + column + "(" + type + ")");
                                columnTypes[column] = Types.NULL;
                                break;
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        columnTypes[column] = Types.NULL;
                    }
                }

                /// might need to add, to free memory from any previous result sets
                this.keys.clear();
                this.vals.clear();

                while (resultSet.next()) {
                    Object category = resultSet.getString(1);
                    Number value = null;

                    xObject = resultSet.getObject(2);
                    switch (columnTypes[1]) {
                        case Types.NUMERIC:
                        case Types.REAL:
                        case Types.INTEGER:
                        case Types.DOUBLE:
                        case Types.FLOAT:
                        case Types.BIGINT:
                            value = (Number) xObject;
                            break;

                        case Types.DATE:
                        case Types.TIMESTAMP:
                            value = new Long(((java.util.Date) xObject).getTime());
                            break;
                        case Types.NULL:
                            break;
                        default:
                            System.err.println("Unknown data type");
                            columnTypes[1] = Types.NULL;
                            break;
                    }
                    keys.add(category);
                    vals.add(value);
                }
            }

            fireDatasetChanged();// Tell the listeners a new table has arrived.

        }
        catch (SQLException ex) {
            System.err.println(ex);
        }

        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    System.err.println("JdbcPieDataset: swallowing exception.");
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e) {
                    System.err.println("JdbcPieDataset: swallowing exception.");
                }
            }
        }
    }

}
