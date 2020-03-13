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

package org.mwc.debrief.pepys.model.bean;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.TableName;

import junit.framework.TestCase;

@TableName(name = "DatafileTypes")
public class DatafileType implements AbstractBean {

	public static class DatafileTypesTest extends TestCase {

		public void testDatafileTypesQuery() {
			try {
				final List list = DatabaseConnection.getInstance().listAll(DatafileType.class, null);

				assertTrue("Datafiletypes - database entries", list.size() == 1);

				final DatafileType type = (DatafileType) list.get(0);
				// assertTrue("Datafiletypes - database entries",
				// "1".equals(type.getIdField()));
				assertTrue("Datafiletypes - database entries", "DATAFILE-TYPE-1".equals(type.getName()));
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Id
	private String datafile_type_id;
	private String name;

	private Date created_date;

	public DatafileType() {

	}

	public Date getCreated_date() {
		return created_date;
	}

	public String getDatafile_type_id() {
		return datafile_type_id;
	}

	public String getName() {
		return name;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setDatafile_type_id(final String datafile_type_id) {
		this.datafile_type_id = datafile_type_id;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
