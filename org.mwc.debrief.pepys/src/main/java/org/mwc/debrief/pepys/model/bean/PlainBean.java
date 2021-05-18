/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2021, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.pepys.model.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;

/**
 * Interface used to indicate that the bean can be mapped directly to some query
 * results, which means that we don't need to do reflection over the fields,
 * leading to a improvement in performance in the mapping.
 *
 */
public interface PlainBean {

	/**
	 * Method that will assign to the values from the resultset using the setters
	 *
	 * @param resultSet  to retrieve values from
	 * @param connection Needed to parse the Database type fields, for example,
	 *                   WorldLocation
	 * @throws SQLException
	 *
	 */
	void retrieveObject(final ResultSet resultSet, final DatabaseConnection connection) throws SQLException;
}
