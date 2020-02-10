
package ASSET.Util;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class DebriefLogger implements java.beans.PropertyChangeListener {

	private static java.io.FileWriter os;

	static public void trace(final String msg) {
		try {
			os.write(msg + System.getProperty("line.separator"));
			os.flush();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public DebriefLogger() {
		this("c:\\res" + System.currentTimeMillis() + ".rep");
	}

	private DebriefLogger(final String path) {
		try {
			os = new java.io.FileWriter(path);
			System.out.println("File opened");

			try {
				os.write(";; ASSET Output" + new java.util.Date());
				os.write("" + System.getProperty("line.separator"));
				os.flush();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void propertyChange(final java.beans.PropertyChangeEvent pe) {
		/*
		 * String res = null;
		 *
		 * if(pe.getPropertyName() == ASSET.ServerType.DECISION) { ASSET.ParticipantType
		 * pt = (ASSET.ParticipantType)pe.getNewValue(); ASSET.Participants.Status stat
		 * = pt.getStatus(); MWC.GenericData.WorldLocation loc = stat.getLocation();
		 *
		 * String locStr =
		 * MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(loc); String
		 * dateStr =
		 * MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(stat.getTime());
		 *
		 * res = dateStr + " " + pt.getName() + " @C " + locStr + " " +
		 * df.format(stat.getCourse()) + df.format(stat.getSpeed()) +
		 * df.format(loc.getDepth());
		 *
		 * } else if(pe.getPropertyName() == ASSET.ServerType.DECISION) {
		 * ASSET.Participants.DemandedStatus ds =
		 * (ASSET.Participants.DemandedStatus)pe.getNewValue(); double demCourse =
		 * ds.getCourse(); double demSpd = ds.getSpeed(); double demDepth =
		 * ds.getDepth();
		 *
		 * String dateStr =
		 * MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(ds.getTime());
		 *
		 * //;NARRATIVE: YYMMDD HHMMSS TTT.TTT XX..XX //;; dtg, track name, narrative
		 * entry
		 *
		 *
		 * res = ";NARRATIVE: " + dateStr + " " + "track_name" + " " + "message";
		 *
		 * }
		 *
		 *
		 *
		 * if(res != null) { try { os.write(res); os.write("" +
		 * System.getProperty("line.separator")); os.flush(); } catch(Exception e) {
		 * e.printStackTrace(); } }
		 */
	}

	public void removeServer(final ASSET.ServerType server) {
		// server.removeListener(ASSET.UPDATE, this);
	}

	public void setServer(final ASSET.ServerType server) {
		// server.addListener(ASSET.UPDATE, this);
	}

}