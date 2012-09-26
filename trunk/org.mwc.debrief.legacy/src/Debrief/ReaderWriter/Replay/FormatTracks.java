// Copyright MWC 1999, Debrief 3 Project
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

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

/**
 * class to apply default formatting to a set of tracks
 */
public final class FormatTracks implements ImportReplay.LayersFormatter
{

	/**
	 * the format we use for the first point on a track plus the point equal to or
	 * greater than 2400 hrs
	 */
	private java.text.SimpleDateFormat _dayFormat = null;

	/**
	 * the default format we use
	 */
	private java.text.SimpleDateFormat _normalFormat = null;

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
	private static void formatThisTrack(TrackWrapper track,
			java.text.SimpleDateFormat dayFormat,
			java.text.SimpleDateFormat normalFormat)
	{
		try
		{
			// the last hour we stamped
			long lastStamp = -1;

			// right, get the tarck segments in the track
			Enumeration<Editable> iter = track.elements();

			// working values
			Date worker = new Date();
			String thisLabel = null;

			// loop through the segments
			while (iter.hasMoreElements())
			{
				Object next = iter.nextElement();

				// right, this is prob a track segment. have a look at it
				if (next instanceof TrackSegment)
				{
					TrackSegment thisSeg = (TrackSegment) next;
					Enumeration<Editable> items = thisSeg.elements();

					// ok, loop through the segment
					while (items.hasMoreElements())
					{
						Object item = items.nextElement();
						if (item instanceof FixWrapper)
						{
							FixWrapper fw = (FixWrapper) item;

							// does this fix already have a label? if so, we'll leave it
							if (!fw.getUserLabelSupplied())
							{
								long thisTime = fw.getTime().getDate().getTime();

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
									long hour = thisTime / _day;

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
		catch (Exception e)
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
	public static void formatTrack(TrackWrapper theTrack)
	{
		// initialise the formatters

		// first the style
		SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("ddHHmm");
		SimpleDateFormat normalFormat = new java.text.SimpleDateFormat("HHmm");

		// now the time zone
		dayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		normalFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		// and format it
		formatThisTrack(theTrack, dayFormat, normalFormat);
	}

	/**
	 * have a go at setting the detault time/date values for imported tracks
	 */
	public final void formatLayers(Layers newData)
	{

		// initialise the formatters
		if (_dayFormat == null)
		{
			_dayFormat = new java.text.SimpleDateFormat("ddHHmm");
			_normalFormat = new java.text.SimpleDateFormat("HHmm");

			_dayFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			_normalFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}

		// have we received valid data?
		if (newData == null)
			return;

		// step through the layers
		for (int i = 0; i < newData.size(); i++)
		{
			// get this layer
			Layer thisL = newData.elementAt(i);

			// is this a track?
			if (thisL instanceof Debrief.Wrappers.TrackWrapper)
			{
				// yes, go for it!
				Debrief.Wrappers.TrackWrapper thisTrack = (TrackWrapper) thisL;

				// now, apply the formatting to this track
				formatThisTrack(thisTrack, _dayFormat, _normalFormat);
			}
		}

	}

}
