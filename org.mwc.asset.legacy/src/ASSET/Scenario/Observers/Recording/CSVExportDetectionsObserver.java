/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package ASSET.Scenario.Observers.Recording;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

public class CSVExportDetectionsObserver extends RecordStatusToFileObserverType
{
	/**
	 * ************************************************************ member
	 * variables *************************************************************
	 */
	protected boolean _haveOutputPositions = false;

	private ArrayList<Integer> _recordedDetections = new ArrayList<Integer>();

	private String _subjectName;

	/***************************************************************
	 * constructor
	 ***************************************************************/

	/**
	 * create a new monitor
	 * 
	 * @param directoryName
	 *          the directory to output the plots to
	 * @param recordDetections
	 *          whether to record detections
	 */
	public CSVExportDetectionsObserver(final String directoryName,
			final String fileName, final TargetType subjectToTrack, final String observerName,
			boolean isActive, String subjectName)
	{
		super(directoryName, fileName, true, false, false, subjectToTrack,
				observerName, isActive);
		_subjectName = subjectName;
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	static public String writeDetailsToBuffer(
			final MWC.GenericData.WorldLocation loc,
			final ASSET.Participants.Status stat, final NetworkParticipant pt,
			long newTime)
	{

		StringBuffer buff = new StringBuffer();

		final String locStr = MWC.Utilities.TextFormatting.DebriefFormatLocation
				.toString(loc);

		long theTime = stat.getTime();

		if (theTime == TimePeriod.INVALID_TIME)
			theTime = newTime;

		final String dateStr = MWC.Utilities.TextFormatting.DebriefFormatDateTime
				.toString(theTime);

		// which force is it?
		buff.append(dateStr);
		buff.append(" ");
		buff.append(pt.getName());
		buff.append(" ");
		buff.append(locStr);
		buff.append(" ");
		buff.append(df.format(stat.getCourse()));
		buff.append(" ");
		buff.append(df.format(stat.getSpeed().getValueIn(WorldSpeed.Kts)));
		buff.append(" ");
		buff.append(df.format(loc.getDepth()));
		buff.append(System.getProperty("line.separator"));

		return buff.toString();
	}

	@Override
	public void restart(ScenarioType scenario)
	{
		super.restart(scenario);

		// and clear the stored detections
		_recordedDetections.clear();
	}

	public void writeThesePositionDetails(
			final MWC.GenericData.WorldLocation loc,
			final ASSET.Participants.Status stat, final ASSET.ParticipantType pt,
			long newTime)
	{
	}

	/**
	 * write these detections to file
	 * 
	 * @param pt
	 *          the participant we're on about
	 * @param detections
	 *          the current set of detections
	 * @param dtg
	 *          the dtg at which the detections were observed
	 */
	protected void writeTheseDetectionDetails(ParticipantType pt,
			DetectionList detections, long dtg)
	{
		// just double check this is us
		if (_subjectName != null && _subjectName.equals(pt.getName()))
		{

			Iterator<DetectionEvent> iter = detections.iterator();
			while (iter.hasNext())
			{
				DetectionEvent de = (DetectionEvent) iter.next();

				StringBuffer buff = new StringBuffer();

				long theTime = de.getTime();

				final String dateStr = MWC.Utilities.TextFormatting.FullFormatDateTime
						.toISOString(theTime);
				WorldLocation loc = de.getSensorLocation();

				// _os.write("Date, Bearing (Degs), Frequency (Hz), Strength");

				buff.append(dateStr);
				buff.append(", ");
				buff.append(loc.getLat());
				buff.append(", ");
				buff.append(loc.getLong());				
				buff.append(", ");
				buff.append(de.getBearing());
				buff.append(", ");
				buff.append(de.getFreq());
				buff.append(", ");
				buff.append(de.getStrength());
				
				buff.append(System.getProperty("line.separator"));

				try
				{
					_os.write(buff.toString());
					_os.flush();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * write the current decision description to file
	 * 
	 * @param pt
	 *          the participant we're looking at
	 * @param activity
	 *          a description of the current activity
	 * @param dtg
	 *          the dtg at which the description was recorded
	 */
	protected void writeThisDecisionDetail(NetworkParticipant pt,
			String activity, long dtg)
	{
	}

	/**
	 * ok, create the property editor for this class
	 * 
	 * @return the custom editor
	 */
	protected Editable.EditorType createEditor()
	{
		return new CSVExportDetectionsObserver.DebriefReplayInfo(this);
	}

	protected String newName(final String name)
	{
		return name
				+ "_"
				+ MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System
						.currentTimeMillis()) + ".csv";
	}

	/**
	 * determine the normal suffix for this file type
	 */
	protected String getMySuffix()
	{
		return "csv";
	}

	/**
	 * write out the file header details for this scenario
	 * 
	 * @param title
	 *          the scenario we're describing
	 * @throws IOException
	 */

	protected void writeFileHeaderDetails(final String title, long currentDTG)
			throws IOException
	{
		_os.write("Time,Lat, Long,  Bearing (Degs), Frequency (Hz), Strength");

		// end the line
		_os.write(System.getProperty("line.separator"));
	}

	/**
	 * output the build details to file
	 */
	protected void writeBuildDate(String theBuildDate) throws IOException
	{
	}

	/**
	 * output this series of locations
	 * 
	 * @param thePath
	 */
	public void outputTheseLocations(WorldPath thePath)
	{
		Collection<WorldLocation> pts = thePath.getPoints();
		int counter = 0;
		for (Iterator<WorldLocation> iterator = pts.iterator(); iterator.hasNext();)
		{
			WorldLocation location = (WorldLocation) iterator.next();
			outputThisLocation(location, _os, "p:" + ++counter);
		}
	}

	private void outputThisLocation(WorldLocation loc,
			java.io.OutputStreamWriter os, String message)
	{
		String locStr = MWC.Utilities.TextFormatting.DebriefFormatLocation
				.toString(loc);
		String msg = ";TEXT: AA " + locStr + " " + message
				+ System.getProperty("line.separator");
		try
		{
			os.write(msg);
		}
		catch (IOException e)
		{
			e.printStackTrace(); // To change body of catch statement use Options |
														// File Templates.
		}
	}

	public void outputThisArea(WorldArea area)
	{
		String topLeft = MWC.Utilities.TextFormatting.DebriefFormatLocation
				.toString(area.getTopLeft());
		String botRight = MWC.Utilities.TextFormatting.DebriefFormatLocation
				.toString(area.getBottomRight());
		// String msg = ";TEXT: AA " + locStr + " " + message +
		// System.getProperty("line.separator");
		String msg = ";RECT: @@ " + topLeft + " " + botRight + " some area "
				+ System.getProperty("line.separator");
		try
		{
			// check our output file is created
			if (_os == null)
				super.createOutputFile();

			super._os.write(msg);
		}
		catch (IOException e)
		{
			e.printStackTrace(); // To change body of catch statement use Options |
														// File Templates.
		}
	}

	// ////////////////////////////////////////////////////////////////////
	// editable properties
	// ////////////////////////////////////////////////////////////////////

	static public class DebriefReplayInfo extends MWC.GUI.Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public DebriefReplayInfo(final CSVExportDetectionsObserver data)
		{
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 * 
		 * @return property descriptions
		 */
		public java.beans.PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final java.beans.PropertyDescriptor[] res = {
						prop("Directory", "The directory to place Debrief data-files"),
						prop("Active", "Whether this observer is active"), };

				return res;
			}
			catch (java.beans.IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

	}
}
