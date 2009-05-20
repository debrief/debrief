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
 * ------------------
 * JdbcXYDataset.java
 * ------------------
 * (C) Copyright 2002, by Bryan Scott and Contributors.
 *
 * Original Author:  Bryan Scott;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Bryan Scott (DG);
 * 19-Apr-2002 : Updated executeQuery, to close cursors and to improve support for types.
 * 26-Apr-2002 : Renamed JdbcXYDataset to better fit in with the existing data source conventions.
 * 26-Apr-2002 : Changed to extend AbstractDataset.
 * 13-Aug-2002 : Updated Javadoc comments and imports (DG);
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
import java.util.Vector;

/**
 * This class provides an chart XYDataset implementation over a database JDBC
 * result set. The dataset is populated via a call to executeQuery with the
 * string sql query. The sql query must return at least two columns. The first
 * column will be the x-axis and remaining columns y-axis values. executeQuery
 * can be called a number of times.
 * 
 * The database connection is read-only and no write back facility exists.
 */
public class JdbcXYDataset extends AbstractDataset implements XYDataset,
		RangeInfo
{

	/** The database connection. */
	Connection connection;

	/** The statement. */
	Statement statement;

	/** The result set. */
	ResultSet resultSet;

	/** Information about the result set. */
	ResultSetMetaData metaData;

	/** Column names. */
	String[] columnNames =
	{};

	/** Rows. */
	Vector<Vector<Number>> rows = new Vector<Vector<Number>>(0);

	/** The maximum y value of the returned result set */
	protected double maxValue = 0.0;

	/** The minimum y value of the returned result set */
	protected double minValue = 0.0;

	public boolean isTimeSeries = false;

	/**
	 * Creates a new JdbcXYDataset (initially empty) and establishes a new
	 * database connection.
	 * 
	 * @param url
	 *          URL of the database connection.
	 * @param driverName
	 *          The database driver class name.
	 * @param user
	 *          The database user.
	 * @param passwd
	 *          The database users password.
	 */
	public JdbcXYDataset(String url, String driverName, String user, String passwd)
	{

		try
		{
			Class.forName(driverName);
			connection = DriverManager.getConnection(url, user, passwd);
			statement = connection.createStatement();
		} catch (ClassNotFoundException ex)
		{
			System.err.println("Cannot find the database driver classes.");
			System.err.println(ex);
		} catch (SQLException ex)
		{
			System.err.println("Cannot connect to this database.");
			System.err.println(ex);
		}
	}

	/**
	 * Creates a new JdbcXYDataset (initially empty) using the specified database
	 * connection.
	 * 
	 * @param con
	 *          The database connection.
	 */
	public JdbcXYDataset(Connection con)
	{
		try
		{
			connection = con;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new JdbcXYDataset using the specified database connection, and
	 * populates it using data obtained with the supplied query.
	 * 
	 * @param con
	 *          The connection.
	 * @param query
	 *          The SQL query.
	 */
	public JdbcXYDataset(Connection con, String query)
	{
		this(con);
		executeQuery(query);
	}

	/**
	 * ExecuteQuery will attempt execute the query passed to it against the
	 * existing database connection. If no connection exists then no action is
	 * taken.
	 * 
	 * The results from the query are extracted and cached locally, thus applying
	 * an upper limit on how many rows can be retrieved successfully.
	 * 
	 * @param query
	 *          The query to be executed
	 */
	public void executeQuery(String query)
	{

		Object xObject = null;
		int column = 0;
		int currentColumn = 0;
		int numberOfColumns = 0;
		int numberOfValidColumns = 0;
		int columnTypes[] = null;

		if (connection == null)
		{
			System.err.println("There is no database to execute the query.");
			return;
		}

		try
		{
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			metaData = resultSet.getMetaData();

			numberOfColumns = metaData.getColumnCount();
			columnTypes = new int[numberOfColumns];
			for (column = 0; column < numberOfColumns; column++)
			{
				try
				{
					int type = metaData.getColumnType(column + 1);
					switch (type)
					{

					case Types.NUMERIC:
					case Types.REAL:
					case Types.INTEGER:
					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.BIT:
					case Types.DATE:
					case Types.TIME:
					case Types.TIMESTAMP:
					case Types.BIGINT:
						++numberOfValidColumns;
						columnTypes[column] = type;
						break;
					default:
						System.err.println("Unable to load column " + column + " (" + type
								+ "," + metaData.getColumnClassName(column + 1) + ")");
						columnTypes[column] = Types.NULL;
						break;
					}
				} catch (SQLException e)
				{
					e.printStackTrace();
					columnTypes[column] = Types.NULL;
				}
			}

			// / First column is X data
			columnNames = new String[numberOfValidColumns - 1];
			// / Get the column names and cache them.
			currentColumn = 0;
			for (column = 1; column < numberOfColumns; column++)
			{
				if (columnTypes[column] != Types.NULL)
				{
					columnNames[currentColumn] = metaData.getColumnLabel(column + 1);
					++currentColumn;
				}
			}

			// Might need to add, to free memory from any previous result sets
			if (rows != null)
			{
				for (column = 0; column < rows.size(); column++)
				{
					Vector<Number> row = rows.get(column);
					row.removeAllElements();
				}
				rows.removeAllElements();
			}

			// Are we working with a time series.
			switch (columnTypes[0])
			{
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				isTimeSeries = true;
				break;
			default:
				isTimeSeries = false;
				break;
			}

			// Get all rows.
			rows = new Vector<Vector<Number>>();
			while (resultSet.next())
			{
				Vector<Number> newRow = new Vector<Number>();
				for (column = 0; column < numberOfColumns; column++)
				{
					xObject = resultSet.getObject(column + 1);
					switch (columnTypes[column])
					{
					case Types.NUMERIC:
					case Types.REAL:
					case Types.INTEGER:
					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.BIGINT:
						newRow.addElement((Number) xObject);
						break;

					case Types.DATE:
					case Types.TIME:
					case Types.TIMESTAMP:
						newRow.addElement(new Long(((java.util.Date) xObject).getTime()));
						break;
					case Types.NULL:
						break;
					default:
						System.err.println("Unknown data");
						columnTypes[column] = Types.NULL;
						break;
					}
				}
				rows.addElement(newRow);
			}

			// / a kludge to make everything work when no rows returned
			if (rows.size() == 0)
			{
				Vector<Number> newRow = new Vector<Number>();
				for (column = 0; column < numberOfColumns; column++)
				{
					if (columnTypes[column] != Types.NULL)
					{
						newRow.addElement(new Integer(0));
					}
				}
				rows.addElement(newRow);
			}

			// / Determine max and min values.
			if (rows.size() < 1)
			{
				maxValue = 0.0;
				minValue = 0.0;
			} else
			{
				Vector<Number> row = rows.elementAt(0);
				double test;
				maxValue = ((Number) row.get(1)).doubleValue();
				minValue = maxValue;
				for (int rowNum = 0; rowNum < rows.size(); ++rowNum)
				{
					row = rows.elementAt(rowNum);
					for (column = 1; column < numberOfColumns; column++)
					{
						test = ((Number) row.get(column)).doubleValue();
						if (test < minValue)
						{
							minValue = test;
						}
						if (test > maxValue)
						{
							maxValue = test;
						}
					}
				}
			}

			fireDatasetChanged();// Tell the listeners a new table has arrived.
		} catch (SQLException ex)
		{
			System.err.println(ex);
			ex.printStackTrace();
		} finally
		{
			if (resultSet != null)
			{
				try
				{
					resultSet.close();
				} catch (Exception e)
				{
				}
			}
			if (statement != null)
			{
				try
				{
					statement.close();
				} catch (Exception e)
				{
				}
			}
		}

	}

	/**
	 * Returns the x-value for the specified series and item. The implementation
	 * is responsible for ensuring that the x-values are presented in ascending
	 * order.
	 * 
	 * @param seriesIndex
	 *          The series (zero-based index).
	 * @param itemIndex
	 *          The item (zero-based index).
	 * 
	 * @return The x-value
	 * 
	 * @see XYDataset
	 */
	public Number getXValue(int seriesIndex, int itemIndex)
	{
		Vector<Number> row =  rows.elementAt(itemIndex);
		return (Number) row.elementAt(0);
	}

	/**
	 * Returns the y-value for the specified series and item.
	 * 
	 * @param seriesIndex
	 *          The series (zero-based index).
	 * @param itemIndex
	 *          The item (zero-based index).
	 * 
	 * @return The yValue value
	 * 
	 * @see XYDataset
	 */
	public Number getYValue(int seriesIndex, int itemIndex)
	{
		Vector<Number> row = rows.elementAt(itemIndex);
		return (Number) row.elementAt(seriesIndex + 1);
	}

	/**
	 * Returns the number of items in the specified series.
	 * 
	 * @param seriesIndex
	 *          The series (zero-based index).
	 * 
	 * @return The itemCount value
	 * 
	 * @see XYDataset
	 */
	public int getItemCount(int seriesIndex)
	{
		return rows.size();
	}

	/**
	 * Returns the number of series in the dataset.
	 * 
	 * @return The seriesCount value
	 * 
	 * @see XYDataset
	 * @see Dataset
	 */
	public int getSeriesCount()
	{
		return columnNames.length;
	}

	/**
	 * Returns the name of the specified series.
	 * 
	 * @param seriesIndex
	 *          The series (zero-based index).
	 * 
	 * @return The seriesName value
	 * 
	 * @see XYDataset
	 * @see Dataset
	 */
	public String getSeriesName(int seriesIndex)
	{

		if ((seriesIndex < columnNames.length)
				&& (columnNames[seriesIndex] != null))
		{
			return columnNames[seriesIndex];
		} else
		{
			return "";
		}

	}

	/**
	 * Returns the number of items that should be displayed in the legend.
	 * 
	 * @return The legendItemCount value
	 */
	public int getLegendItemCount()
	{
		return getSeriesCount();
	}

	/**
	 * Returns the legend item labels.
	 * 
	 * @return The legend item labels.
	 */
	public String[] getLegendItemLabels()
	{
		return columnNames;
	}

	/**
	 * Returns the minimum data value in the dataset's range.
	 * 
	 * @return The minimum value.
	 * 
	 * @see RangeInfo
	 */
	public Number getMinimumRangeValue()
	{
		return new Double(minValue);
	}

	/**
	 * Returns the maximum data value in the dataset's range.
	 * 
	 * @return The maximum value.
	 * 
	 * @see RangeInfo
	 */
	public Number getMaximumRangeValue()
	{
		return new Double(maxValue);
	}

	/**
	 * Close the database connection
	 */
	public void close()
	{

		try
		{
			resultSet.close();
		} catch (Exception e)
		{
			System.err.println("JdbcXYDataset: swallowing exception.");
		}
		try
		{
			statement.close();
		} catch (Exception e)
		{
			System.err.println("JdbcXYDataset: swallowing exception.");
		}
		try
		{
			connection.close();
		} catch (Exception e)
		{
			System.err.println("JdbcXYDataset: swallowing exception.");
		}

	}

	/**
	 * Returns the range of the values in this dataset's range (y-values).
	 * 
	 * @return The range.
	 */
	public Range getValueRange()
	{
		return new Range(minValue, maxValue);
	}

}
