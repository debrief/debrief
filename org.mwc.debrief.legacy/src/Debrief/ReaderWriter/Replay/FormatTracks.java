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
// $RCSfile: FormatTracks.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: FormatTracks.java,v $
// Revision 1.4  2004/11/25 10:24:14  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/22 13:40:55  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.2  2004/09/09 10:22:57  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:47:43  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-25 15:52:23+00  ian_mayo
// Make track formatting visible from the outside
//
// Revision 1.3  2003-03-19 15:37:44+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:11+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:13+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:52+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:32+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-09-14 09:42:42+01  administrator
// Remembered to set time zone
//
// Revision 1.1  2001-08-13 12:52:32+01  administrator
// check that plottable returned from track is actually a fix
//
// Revision 1.0  2001-08-01 20:07:50+01  administrator
// Initial revision
//

package Debrief.ReaderWriter.Replay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DateFormatPropertyEditor;

/**
 * class to apply default formatting to a set of tracks
 */
public final class FormatTracks implements ImportReplay.LayersFormatter
{
//
//	/**
//	 * the format we use for the first point on a track plus the point equal to or
//	 * greater than 2400 hrs
//	 */
//	private static java.text.SimpleDateFormat _dayFormat = null;
//
//	/**
//	 * the default format we use
//	 */
//	private static java.text.SimpleDateFormat _normalFormat = null;

	/**
	 * const to represent a day in millis
	 */
	private static final long _day = 24 * 60 * 60 * 1000;

	/**
	 * do the formatting for this particular track
	 * 
	 */

	/**
	 * do the formatting for this particular track
	 * 
	 * @param track
	 *          the track to reformat
	 * @param dayFormat
	 *          the day format to use
	 * @param normalFormat
	 *          the normal format to use
	 */
	private static void formatThisTrack(final TrackWrapper track)
	{
		try
		{
			
			final SimpleDateFormat dayFormat = new java.text.SimpleDateFormat(DateFormatPropertyEditor.DATE_FORMAT);
			final SimpleDateFormat normalFormat = new java.text.SimpleDateFormat(DateFormatPropertyEditor.TIME_FORMAT);

			dayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			normalFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			
			// the last hour we stamped
			long lastStamp = -1;

			// right, get the tarck segments in the track
			final Enumeration<Editable> iter = track.elements();

			// working values
			final Date worker = new Date();
			String thisLabel = null;

			// loop through the segments
			while (iter.hasMoreElements())
			{
				final Object next = iter.nextElement();

				// right, this is prob a track segment. have a look at it
				if (next instanceof TrackSegment)
				{
					final TrackSegment thisSeg = (TrackSegment) next;
					final Enumeration<Editable> items = thisSeg.elements();

					// ok, loop through the segment
					while (items.hasMoreElements())
					{
						final Object item = items.nextElement();
						if (item instanceof FixWrapper)
						{
							final FixWrapper fw = (FixWrapper) item;

							// does this fix already have a label? if so, we'll leave it
							if (!fw.getUserLabelSupplied())
							{
								final long thisTime = fw.getTime().getDate().getTime();

								// update the time
								worker.setTime(thisTime);

								// is this the first element?
								if (lastStamp == -1)
								{
									// show the days anyway
									thisLabel = dayFormat.format(worker);

									// ok, done
									lastStamp = thisTime / _day;
								}
								else
								{
									// find the last hour stamp before this time
									final long hour = thisTime / _day;

									if (hour > lastStamp)
									{
										lastStamp = hour;
										thisLabel = dayFormat.format(worker);
									}
									else
									{
										thisLabel = normalFormat.format(worker);
									}
								}
								fw.setLabel(thisLabel);
							}
						} // whether this was a fix
					}
				}

			}
		}
		catch (final Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(
					e,
					"Failed whilst setting default formatting for track:"
							+ track.getName());
		}
	}

	/**
	 * apply formatting to this track according to our default styles
	 * 
	 * @param theTrack
	 *          the track to be reformatted
	 */
	public static void formatTrack(final TrackWrapper theTrack)
	{
		// and format it
		formatThisTrack(theTrack);
	}
	
	/**
	 * have a go at setting the detault time/date values for imported tracks
	 */
	public final void formatLayers(final Layers newData)
	{
		// have we received valid data?
		if (newData == null)
			return;

		// step through the layers
		for (int i = 0; i < newData.size(); i++)
		{
			// get this layer
			final Layer thisL = newData.elementAt(i);

			// is this a track?
			if (thisL instanceof Debrief.Wrappers.TrackWrapper)
			{
				// yes, go for it!
				final Debrief.Wrappers.TrackWrapper thisTrack = (TrackWrapper) thisL;

				// now, apply the formatting to this track
				formatThisTrack(thisTrack);
			}
		}

	}

}
