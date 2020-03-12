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

@TableName(name = "Privacies")
public class Privacy implements AbstractBean {

	public static class PrivaciesTest extends TestCase {

		public void testPrivaciesQuery() {
			try {
				final List list = DatabaseConnection.getInstance().listAll(Privacy.class, null);

				assertTrue("Privacies - database entries", list.size() == 1);

				final Privacy type = (Privacy) list.get(0);
				// assertTrue("Datafiletypes - database entries",
				// "1".equals(type.getIdField()));
				assertTrue("Datafiletypes - database entries", "PRIVACY-1".equals(type.getName()));
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Id
	private int privacy_id;
	private String name;

	private Date created_date;

	public Privacy() {

	}

	public Date getCreated_date() {
		return created_date;
	}

	public String getName() {
		return name;
	}

	public int getPrivacy_id() {
		return privacy_id;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPrivacy_id(final int privacy_id) {
		this.privacy_id = privacy_id;
	}
}
