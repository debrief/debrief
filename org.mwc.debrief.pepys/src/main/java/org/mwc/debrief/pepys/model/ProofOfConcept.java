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
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import org.mwc.debrief.pepys.model.bean.DatafileTypes;
import org.mwc.debrief.pepys.model.bean.Datafiles;
import org.mwc.debrief.pepys.model.bean.States;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;

public class ProofOfConcept {
	public static void main(final String[] args) {
		try {
			final List list = DatabaseConnection.getInstance().listAll(Datafiles.class, null);

			final List list2 = DatabaseConnection.getInstance().listAll(DatafileTypes.class, null);

			final List list3 = DatabaseConnection.getInstance().listAll(States.class, null);

			final List list4 = DatabaseConnection.getInstance().listAll(States.class, "source_id = 16");

			for (final Object l : list) {
				final Datafiles dataFile = (Datafiles) l;
				System.out.println("DataFile ID: " + dataFile.getDatafile_id() + ", " + dataFile.getReference());
			}

			for (final Object l : list2) {
				final DatafileTypes datafileTypes = (DatafileTypes) l;
				System.out.println("DataFileType ID: " + datafileTypes.getDatafile_type_id() + ", " + datafileTypes.getName());
			}

			for (final Object l : list3) {
				final States state = (States) l;
				System.out.println("State ID: " + state.getState_id());
			}

			for (final Object l : list4) {
				final States state = (States) l;
				System.out.println("Filtered State ID: " + state.getState_id() + ", " + state.getLocation());
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException e) {
			e.printStackTrace();
		}
	}
}
