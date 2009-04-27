// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportReplay.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.18 $
// $Log: ImportReplay.java,v $
// Revision 1.18  2007/06/01 13:46:08  ian.mayo
// Improve performance of export text to clipboard
//
// Revision 1.17  2006/08/08 12:55:30  Ian.Mayo
// Restructure loading narrative entries (so we can see it from CMAP)
//
// Revision 1.16  2006/07/17 11:04:21  Ian.Mayo
// Append to the clipboard, don't keep writing afresh
//
// Revision 1.15  2006/05/24 15:01:28  Ian.Mayo
// Reflect change in exportThis method
//
// Revision 1.14  2006/05/23 14:53:55  Ian.Mayo
// Make readLine public (so our data-importers can read it in)
//
// Revision 1.13  2006/02/13 16:19:06  Ian.Mayo
// Sort out problem with creating sensor data
//
// Revision 1.12  2005/12/13 09:04:37  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.11  2005/05/12 14:11:45  Ian.Mayo
// Allow import of typed-narrative entry
//
// Revision 1.10  2005/05/12 09:52:42  Ian.Mayo
// Stop it being final - since in CMAP we want to override line counting method
//
// Revision 1.9  2004/12/17 15:53:57  Ian.Mayo
// Get on top of some problems plotting sensor & tma data.
//
// Revision 1.8  2004/11/25 10:24:18  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.7  2004/11/22 13:53:28  Ian.Mayo
// Replace variable name previously used for counting through enumeration - now part of JDK1.5
//
// Revision 1.6  2004/11/22 13:40:56  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.5  2004/11/11 11:52:44  Ian.Mayo
// Reflect new directory structure
//
// Revision 1.4  2004/09/09 10:22:58  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.3  2004/08/20 08:18:04  Ian.Mayo
// Allow 4-figure dates in REP files
//
// Revision 1.2  2003/08/12 09:28:43  Ian.Mayo
// Include import of DTF files
//
// Revision 1.1.1.2  2003/07/21 14:47:51  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.11  2003-07-03 15:47:03+01  ian_mayo
// Improved error checking
//
// Revision 1.10  2003-06-23 13:39:36+01  ian_mayo
// Add TMA Handling code
//
// Revision 1.9  2003-06-23 08:39:38+01  ian_mayo
// Initialise colours on request - not just in constructor (support for testing)
//
// Revision 1.8  2003-06-16 11:49:41+01  ian_mayo
// Output completed message which was failing ANT built
//
// Revision 1.7  2003-04-30 16:05:46+01  ian_mayo
// Correctly set GMT time zone for importing narratives
//
// Revision 1.6  2003-03-19 15:37:29+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.5  2003-02-25 14:36:08+00  ian_mayo
// Just use \n as line-break when exporting to clipboard
//
// Revision 1.4  2003-02-14 10:16:11+00  ian_mayo
// Set the symbol type according to symbology in Replay file
//
// Revision 1.3  2002-10-01 15:40:18+01  ian_mayo
// improve testing
//
// Revision 1.2  2002-05-28 11:34:21+01  ian_mayo
// Implemented correct way of breaking out of a loop
//
// Revision 1.1  2002-05-28 09:12:09+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:39+01  ian_mayo
// Initial revision
//
// Revision 1.15  2002-03-12 09:18:00+00  administrator
// Provide "Fallback" handler for missing milliseconds
//
// Revision 1.14  2002-02-18 09:20:19+00  administrator
// Change tests so that we no longer write progress statements to screen
//
// Revision 1.13  2002-02-01 12:37:37+00  administrator
// Allow track colours to be specified per-fix instead of one colour for the whole track
//
// Revision 1.12  2002-01-29 07:53:14+00  administrator
// Use Trace method instead of System.out
//
// Revision 1.11  2001-11-14 19:48:49+00  administrator
// Handle lines beginning with comment, but report error for unrecognised lines
//
// Revision 1.10  2001-11-14 19:39:14+00  administrator
// Add error message for when annotation not recognised
//
// Revision 1.9  2001-11-13 21:11:32+00  administrator
// improve testing
//
// Revision 1.8  2001-10-02 09:29:28+01  administrator
// improve debug comments
//
// Revision 1.7  2001-09-09 08:40:49+01  administrator
// Add read/write testing
//
// Revision 1.6  2001-08-29 19:16:53+01  administrator
// Remove ContactWrapper stuff
//
// Revision 1.5  2001-08-21 15:17:45+01  administrator
// Allow use of dsf suffix
//
// Revision 1.4  2001-08-17 07:59:57+01  administrator
// Clear up memory leaks
//
// Revision 1.3  2001-08-13 12:53:34+01  administrator
// Provide support for line styles, and support Sensor data
//
// Revision 1.2  2001-08-06 12:46:19+01  administrator
// Use our monitor instead of a normal buffered reader
//
// Revision 1.1  2001-08-01 20:07:39+01  administrator
// Provide interface for, and handle a list of formatting objects which apply some kind of formatting to imported data
//
// Revision 1.0  2001-07-17 08:41:33+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-09 14:01:58+01  novatech
// add narrative importer to list of importers, and handle creation of layer for Narratives
//
// Revision 1.1  2001-01-03 13:40:46+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:24  ianmayo
// initial import of files
//
// Revision 1.22  2000-11-17 09:10:45+00  ian_mayo
// reflect changes in parent
//
// Revision 1.21  2000-11-08 11:48:50+00  ian_mayo
// insert debug line
//
// Revision 1.20  2000-11-03 12:08:49+00  ian_mayo
// add support for importBearing, reflect new status of TrackWrapper as layer, not just plottable
//
// Revision 1.19  2000-11-02 16:45:50+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer, also changed TrackWrapper so that it implements Layer,  and as we read in files, we put them into track and add Track to Layers, not to Layer then Layers
//
// Revision 1.18  2000-10-09 13:37:35+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.17  2000-10-03 14:18:25+01  ian_mayo
// add reference to ImportWheel class
//
// Revision 1.16  2000-09-28 12:09:32+01  ian_mayo
// switch to GMT time zone
//
// Revision 1.15  2000-09-21 12:22:39+01  ian_mayo
// check for an empty clipboard
//
// Revision 1.14  2000-04-19 11:24:01+01  ian_mayo
// add new import types
//
// Revision 1.13  2000-03-17 13:37:25+00  ian_mayo
// Handle replay text colour symbols more tidily
//
// Revision 1.12  2000-03-07 14:48:16+00  ian_mayo
// optimised algorithms
//
// Revision 1.11  2000-02-22 13:49:19+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.10  2000-02-02 14:27:35+00  ian_mayo
// ensure that the "Importers" static vector is initialised before use
//
// Revision 1.9  2000-01-12 15:40:19+00  ian_mayo
// added concept of contacts
//
// Revision 1.8  1999-12-03 14:41:05+00  ian_mayo
// remove d-line
//
// Revision 1.7  1999-12-02 09:44:28+00  ian_mayo
// removed "Sleep" command, Boy what speed gains this provided!
//
// Revision 1.6  1999-11-26 15:51:40+00  ian_mayo
// tidying up
//
// Revision 1.5  1999-11-12 14:36:36+00  ian_mayo
// make classes do export aswell as import
//
// Revision 1.4  1999-11-09 11:26:41+00  ian_mayo
// added ellipses
//
// Revision 1.3  1999-10-14 12:00:33+01  ian_mayo
// added support for lines
//
// Revision 1.2  1999-10-13 17:24:01+01  ian_mayo
// add support for Rectangles
//
// Revision 1.1  1999-10-12 15:34:12+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-07-27 09:27:28+01  administrator
// added more error handlign
//
// Revision 1.2  1999-07-12 08:09:22+01  administrator
// Property editing added
//
// Revision 1.6  1999-06-16 15:24:23+01  sm11td
// before move around/ end of phase 1
//
// Revision 1.5  1999-06-04 08:45:26+01  sm11td
// Ending phase 1, adding colours to annotations
//
// Revision 1.4  1999-06-01 16:49:17+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.3  1999-02-04 08:02:24+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 16:08:47+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:04+00  sm11td
// Initial revision
//

package Debrief.ReaderWriter.Replay;

import java.awt.*;
import java.io.*;
import java.util.*;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import Debrief.Tools.Tote.Watchable;
import Debrief.Wrappers.*;
import MWC.GUI.*;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.*;
import MWC.Utilities.ReaderWriter.*;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to read in a complete replay file. The class knows of the types of data
 * in Replay format, and users the correct import filters accordingly.
 */

public class ImportReplay extends PlainImporterBase
{

	/**
	 * the format we use to parse text
	 */
	private static final java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(
			"yyMMdd HHmmss.SSS");

	private static Vector<PlainLineImporter> _theImporters;

	static private Vector<doublet> colors; // list of Replay colours

	static public final String NARRATIVE_LAYER = "Narratives";

	static private final String ANNOTATION_LAYER = "Annotations";

	/**
	 * the stepper we are currently using. This gets set immediately before the
	 * import process, and is only valid for a single import. It's static because
	 * it gets assigned through a static method
	 */
	private static Debrief.GUI.Tote.StepControl _theStepper;

	/**
	 * the list of formatting objects we know about
	 */
	private final LayersFormatter[] _myFormatters = { new FormatTracks() };

	/**
	 * constructor, initialise Vector with the list of non-Fix items which we will
	 * be reading in
	 */
	public ImportReplay()
	{

		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		_myTypes = new String[] { ".rep", ".dsf", ".dtf" };

		checkImporters();

		initialiseColours();
	}

	private static void initialiseColours()
	{
		// create a list of colours
		if (colors == null)
		{
			colors = new Vector<doublet>(0, 1);
			colors.addElement(new doublet("@", Color.white));
			colors.addElement(new doublet("A", Color.blue));
			colors.addElement(new doublet("B", Color.green));
			colors.addElement(new doublet("C", Color.red));
			colors.addElement(new doublet("D", Color.yellow));
			colors.addElement(new doublet("E", new Color(169, 1, 132)));
			colors.addElement(new doublet("F", Color.orange));
			colors.addElement(new doublet("G", new Color(188, 93, 6)));
			colors.addElement(new doublet("H", Color.cyan));
			colors.addElement(new doublet("I", new Color(100, 240, 100)));
			colors.addElement(new doublet("J", new Color(230, 200, 20)));
			colors.addElement(new doublet("K", Color.pink));
		}
	}

	/*****************************************************************************
	 * member methods
	 ****************************************************************************/

	/**
	 * format a date using our format
	 */
	public static String formatThis(HiResDate val)
	{
		String res = DebriefFormatDateTime.toStringHiRes(val);
		return res;
	}

	/**
	 * function to initialise the list of importers
	 */
	private static void checkImporters()
	{
		if (_theImporters == null)
		{
			// create the array of import handlers, by
			_theImporters = new Vector<PlainLineImporter>(0, 1);

			// adding handler we (currently) know of
			_theImporters.addElement(new ImportCircle());
			_theImporters.addElement(new ImportRectangle());
			_theImporters.addElement(new ImportLine());
			_theImporters.addElement(new ImportEllipse());
			_theImporters.addElement(new ImportPeriodText());
			_theImporters.addElement(new ImportTimeText());
			_theImporters.addElement(new ImportLabel());
			_theImporters.addElement(new ImportWheel());
			_theImporters.addElement(new ImportBearing());
			_theImporters.addElement(new ImportNarrative());
			_theImporters.addElement(new ImportNarrative2());
			_theImporters.addElement(new ImportSensor());
			_theImporters.addElement(new ImportSensor2());
			_theImporters.addElement(new ImportTMA_Pos());
			_theImporters.addElement(new ImportTMA_RngBrg());
			// note that we don't rely on ImportFix for importing Replay fixes, since
			// they are handled by the ImportReplay method. We are including it in
			// this list so that we can use it as an exporter
			_theImporters.addElement(new ImportFix());
		}
	}

	/**
	 * set the step control to be used for the next import process
	 */
	static public void setStepper(Debrief.GUI.Tote.StepControl stepper)
	{
		_theStepper = stepper;
	}

	/**
	 * parse this line
	 * 
	 * @param theLine
	 *          the line to parse
	 */
	public HiResDate readLine(String theLine) throws java.io.IOException
	{
		HiResDate res = null;

		// is this line valid?
		if (theLine.length() > 0)
		{
			// what type of item is this?
			PlainLineImporter thisOne = getImporterFor(theLine);

			// check that we have found an importer
			if (thisOne == null)
			{
				// just check it wasn't a comment
				if (theLine.startsWith(";;"))
				{
					// don't bother, it's just a comment
				}
				else
				{
					MWC.Utilities.Errors.Trace.trace("Annotation type not recognised for:"
							+ theLine);
				}
				return null;
			}

			// now read it in.
			Object thisObject = null;
			try{
				thisObject = thisOne.readThisLine(theLine);
			}catch(ParseException pe)
			{
				System.err.println("Failed to read in:" + theLine);
				pe.printStackTrace();
			}

			// see if we are going to do any special processing

			// is this a fix?
			if (thisObject instanceof ReplayFix)
			{

				// so, we are handling a fix
				ReplayFix rf = (ReplayFix) thisObject;

				// remember the dtg
				res = rf.theFix.getTime();

				// find the track name
				String theTrack = rf.theTrackName;
				Color thisColor = replayColorFor(rf.theSymbology);

				// create the wrapper for this annotation
				PlainWrapper thisWrapper = new FixWrapper(rf.theFix);

				// keep track of the wrapper for this track
				TrackWrapper trkWrapper = null;

				// is there a layer for this track?
				trkWrapper = (TrackWrapper) getLayerFor(theTrack);

				// have we found the layer?
				if (trkWrapper == null)
				{
					// now create the wrapper
					trkWrapper = new TrackWrapper();

					// get the colour for this track
					trkWrapper.setColor(thisColor);

					// set the sym type for the track
					String theSymType = replayTrackSymbolFor(rf.theSymbology);
					trkWrapper.setSymbolType(theSymType);

					// store the track-specific data
					trkWrapper.setName(theTrack);

					// add our new layer to the Layers object
					addLayer(trkWrapper);

				}

				// add the fix to the track
				trkWrapper.addFix((FixWrapper) thisWrapper);

				// let's also tell the fix about it's track
				((FixWrapper) thisWrapper).setTrackWrapper(trkWrapper);

				// also, see if this fix is specifying a different colour to use
				if (thisColor != trkWrapper.getColor())
				{
					// give this fix it's unique colour
					thisWrapper.setColor(thisColor);
				}

			}
			else if (thisObject instanceof SensorContactWrapper)
			{
				// represent as contactwrapper
				SensorContactWrapper sw = (SensorContactWrapper) thisObject;

				// remember the dtg
				res = sw.getTime();

				SensorWrapper thisWrapper = null;

				// do we have a sensor capable of handling this contact?
				String sensorName = sw.getSensorName();
				String trackName = sw.getTrackName();
				Object val = getLayerFor(trackName);

				// did we get anything?
				if (val != null)
				{
					// is this indeed a sensor?
					if (val instanceof TrackWrapper)
					{
						// so, we've found a track - see if it holds this sensor
						TrackWrapper tw = (TrackWrapper) val;
						Enumeration<SensorWrapper> iter = tw.getSensors();

						// step through this track' sensors
						if (iter != null)
						{
							while (iter.hasMoreElements())
							{
								//
								SensorWrapper sensorw = iter.nextElement();

								// is this our sensor?
								if (sensorw.getName().equals(sensorName))
								{
									// cool, drop out
									thisWrapper = sensorw;
									break;
								}
							} // looping through the sensors
						} // whether there are any sensors
					} // if the item found is a track
				}

				// did we find it?
				if (thisWrapper == null)
				{
					// then create it
					thisWrapper = new SensorWrapper(sensorName);

					// set it's colour to the colour of the first data point
					thisWrapper.setColor(sw.getColor());

					// also set it's name
					thisWrapper.setTrackName(sw.getTrackName());

					// also get the track for this sensor
					Layer thisTrk = getLayerFor(sw.getTrackName());

					try
					{
						if (thisTrk != null)
						{
							TrackWrapper tw = (TrackWrapper) thisTrk;
							tw.add(thisWrapper);
						}
						else
						{
							// no, maybe it's a track or something like that!
							// ditch the process and warn the users
							throw new java.io.IOException("Sorry we cannot find details for track:"
									+ sw.getTrackName());
						}
					}
					catch (java.lang.ClassCastException ce)
					{
						// no, maybe it's a track or something like that!
						// ditch the process and warn the users
						throw new java.io.IOException("Whilst there is a data item named:"
								+ sw.getTrackName() + " it doesn't appear to be a track");
					}

				}

				// so, we now have the wrapper. have a look to see if the colour of this
				// data item is the same
				// as the sensor - in which case we will erase the colour for this data
				// item so that it
				// always takes the colour of it's parent
				if (sw.getColor().equals(thisWrapper.getColor()))
				{
					// clear the colour - so it takes it form it's parent
					sw.setColor(null);
				}

				// now add the new contact to this sensor
				thisWrapper.add(sw);

			}
			else if (thisObject instanceof TMAContactWrapper)
			{
				// represent as contactwrapper
				TMAContactWrapper sw = (TMAContactWrapper) thisObject;

				// remember the dtg
				res = sw.getTime();

				TMAWrapper thisWrapper = null;

				// do we have a sensor capable of handling this contact?
				String solutionName = sw.getSolutionName();

				String trackName = sw.getTrackName();
				Object val = getLayerFor(trackName);

				// did we get anything?
				if (val != null)
				{
					// is this indeed a sensor?
					if (val instanceof TrackWrapper)
					{
						// so, we've found a track - see if it holds this solution
						TrackWrapper tw = (TrackWrapper) val;
						Enumeration<TMAWrapper> iter = tw.getSolutions();

						// step through this track's solutions
						if (iter != null)
						{
							while (iter.hasMoreElements())
							{
								//
								TMAWrapper sensorw = iter.nextElement();

								// is this our sensor?
								if (sensorw.getName().equals(solutionName))
								{
									// cool, drop out
									thisWrapper = sensorw;
									break;
								}
							} // looping through the sensors
						} // whether there are any sensors
					} // if the item found is a track
				}

				// did we find it?
				if (thisWrapper == null)
				{
					// then create it
					thisWrapper = new TMAWrapper(solutionName);

					// set it's colour to the colour of the first data point
					thisWrapper.setColor(sw.getColor());

					// also set it's name
					thisWrapper.setTrackName(sw.getTrackName());

					// also get the track for this sensor
					Layer thisTrk = getLayerFor(sw.getTrackName());

					try
					{
						if (thisTrk != null)
						{
							TrackWrapper tw = (TrackWrapper) thisTrk;
							tw.add(thisWrapper);
						}
						else
						{
							// no, maybe it's a track or something like that!
							// ditch the process and warn the users
							throw new java.io.IOException("Sorry we cannot find details for track:"
									+ sw.getTrackName());
						}
					}
					catch (java.lang.ClassCastException ce)
					{
						// no, maybe it's a track or something like that!
						// ditch the process and warn the users
						throw new java.io.IOException("Whilst there is a data item named:"
								+ sw.getTrackName() + " it doesn't appear to be a track");
					}

				}

				// so, we now have the wrapper. have a look to see if the colour of this
				// data item is the same
				// as the sensor - in which case we will erase the colour for this data
				// item so that it
				// always takes the colour of it's parent
				if (sw.getColor().equals(thisWrapper.getColor()))
				{
					// clear the colour - so it takes it form it's parent
					sw.setColor(null);
				}

				// lastly inform the sensor contact of it's parent
				sw.setTMATrack(thisWrapper);

				// now add the new contact to this sensor
				thisWrapper.add(sw);

			}
			else if (thisObject instanceof NarrativeEntry)
			{
				NarrativeEntry entry = (NarrativeEntry) thisObject;

				// remember the dtg
				res = entry.getDTG();

				// have we got a narrative wrapper?
				Layer dest = getLayerFor(NARRATIVE_LAYER);
				if (dest == null)
				{
					dest = new NarrativeWrapper(NARRATIVE_LAYER, _theStepper);
					addLayer(dest);
				}

				addToLayer(entry, dest);
			}

			// ////////
			// PlainWrapper is our "fallback" operator, so it's important to leave it
			// to last
			// ////////
			else if (thisObject instanceof PlainWrapper)
			{

				// create the wrapper for this annotation
				PlainWrapper thisWrapper = (PlainWrapper) thisObject;

				// remember the dtg
				if (thisWrapper instanceof Watchable)
				{
					Watchable wat = (Watchable) thisWrapper;
					res = wat.getTime();
				}

				// not fix, must be annotation, just add it to the correct
				// layer
				Layer dest = getLayerFor(ANNOTATION_LAYER);
				if (dest == null)
				{
					dest = createLayer(ANNOTATION_LAYER);
					addLayer(dest);
				}

				addToLayer(thisWrapper, dest);

			}

		}

		return res;
	}

	/**
	 * import data from this stream
	 */
	public final void importThis(String fName, java.io.InputStream is)
	{
		// declare linecounter
		int lineCounter = 0;

		int numLines = countLinesFor(fName);

		Reader reader = new InputStreamReader(is);
		BufferedReader br = new ReaderMonitor(reader, numLines, fName);
		String thisLine = null;
		try
		{

			// check stream is valid
			if (is.available() > 0)
			{

				thisLine = br.readLine();

				long start = System.currentTimeMillis();

				// loop through the lines
				while (thisLine != null)
				{

					// keep line counter
					lineCounter++;

					// catch import problems
					readLine(thisLine);

					// read another line
					thisLine = br.readLine();

				}

				// lastly have a go at formatting these tracks
				for (int k = 0; k < _myFormatters.length; k++)
				{
					_myFormatters[k].formatLayers(getLayers());
				}

				long end = System.currentTimeMillis();
				System.out.print(" |Elapsed:" + (end - start) + " ");

			}
		}
		catch (java.lang.NumberFormatException e)
		{
			// produce the error message
			MWC.Utilities.Errors.Trace.trace(e);
			// show the message dialog
			super.readError(fName, lineCounter, "Number format error", thisLine);
		}
		catch (IOException e)
		{
			// produce the error message
			MWC.Utilities.Errors.Trace.trace(e);
			// show the message dialog
			super.readError(fName, lineCounter, "Unknown read error:" + e, thisLine);
		}
		catch (java.util.NoSuchElementException e)
		{
			// produce the error message
			MWC.Utilities.Errors.Trace.trace(e);
			// show the message dialog
			super.readError(fName, lineCounter, "Missing field error", thisLine);
		}

		// finally clear the stepper, it will get re-assigned for the next import
		_theStepper = null;
	}

	private PlainLineImporter getImporterFor(String theLine)
	{

		PlainLineImporter res = null;

		// so, determine if this is a comment
		if (theLine.charAt(0) == ';')
		{
			// look through types of import handler
			Enumeration<PlainLineImporter> iter = _theImporters.elements();

			// get the type for this comment
			StringTokenizer st = new StringTokenizer(theLine);
			String type = st.nextToken();

			// cycle through my types
			while (iter.hasMoreElements())
			{
				PlainLineImporter thisImporter = iter.nextElement();

				// get the handler correct type?
				String thisType = thisImporter.getYourType();

				if (thisType == null)
					MWC.Utilities.Errors.Trace.trace("null returned by: " + thisImporter);

				// does this one fit?
				if (thisType.equals(type))
				{
					res = thisImporter;
					break;
				}
			}
		}
		else
		{
			res = new ImportFix();
		}

		// done
		return res;
	}

	/**
	 * convert the item to text, add it to the block we're building up
	 */
	public final void exportThis(Plottable item)
	{

		// check it's real
		if (item == null)
			throw new IllegalArgumentException("duff wrapper");

		checkImporters();

		// just see if it is a track which we are trying to export
		if (item instanceof Layer)
		{

			Layer tw = (Layer) item;
			// ha-ha! export the points one at a time
			java.util.Enumeration<Editable> iter = tw.elements();
			while (iter.hasMoreElements())
			{
				Plottable pt = (Plottable) iter.nextElement();
				exportThis(pt);
			}
			// ta-da! done.
		}
		else
		{
			// check we have some importers
			if (_theImporters != null)
			{
				Enumeration<PlainLineImporter> iter = _theImporters.elements();

				// step though our importers, to see if any will 'do the deal;
				while (iter.hasMoreElements())
				{
					PlainLineImporter thisImporter = iter.nextElement();

					if (thisImporter.canExportThis(item))
					{
						// export it, add it to the data we're building up
						String thisLine = thisImporter.exportThis(item);
						addThisToExport(thisLine);

						// ok, we can drop out of the loop
						break;
					}
				}
			}
		}
	}

	public final boolean canImportThisFile(String theFile)
	{
		boolean res = false;
		String theSuffix = null;
		int pos = theFile.lastIndexOf(".");
		theSuffix = theFile.substring(pos, theFile.length());

		for (int i = 0; i < _myTypes.length; i++)
		{
			if (theSuffix.equalsIgnoreCase(_myTypes[i]))
			{
				res = true;
				break;
			}
		}

		return res;
	}

	static public int replayLineStyleFor(String theSym)
	{
		int res = 0;
		String theStyle = theSym.substring(0, 1);

		if (theStyle.equals("@"))
		{
			res = MWC.GUI.CanvasType.SOLID;
		}
		else if (theStyle.equals("A"))
		{
			res = MWC.GUI.CanvasType.DOTTED;
		}
		else if (theStyle.equals("B"))
		{
			res = MWC.GUI.CanvasType.DOT_DASH;
		}
		else if (theStyle.equals("C"))
		{
			res = MWC.GUI.CanvasType.SHORT_DASHES;
		}
		else if (theStyle.equals("D"))
		{
			res = MWC.GUI.CanvasType.LONG_DASHES;
		}
		else if (theStyle.equals("E"))
		{
			res = MWC.GUI.CanvasType.UNCONNECTED;
		}

		return res;
	}

	public final static String replayTrackSymbolFor(String theSym)
	{
		String res = null;
		String colorVal = theSym.substring(0, 1);

		res = SymbolFactory.createSymbolFromId(colorVal);

		// did we manage to find it?
		if (res == null)
			res = SymbolFactory.createSymbolFromId(SymbolFactory.DEFAULT_SYMBOL_TYPE);

		return res;
	}

	static public Color replayColorFor(String theSym)
	{
		Color res = null;
		String colorVal = theSym.substring(1, 2);

		// check we have the colours
		initialiseColours();

		// step through our list of colours
		java.util.Enumeration<doublet> iter = colors.elements();
		while (iter.hasMoreElements())
		{
			doublet db = iter.nextElement();
			if (db.label.equals(colorVal))
			{
				res = db.color;
				break;
			}
		}

		// if label not found, make it RED
		if (res == null)
			res = Color.red;

		return res;
	}

	static public String replaySymbolFor(Color theCol)
	{
		String res = null;

		// step through our list of colours
		java.util.Enumeration<doublet> iter = colors.elements();
		while (iter.hasMoreElements())
		{
			doublet db = iter.nextElement();
			if (db.color.equals(theCol))
			{
				res = db.label;
				continue;
			}
		}

		// label not found, make it RED
		if (res == null)
			res = "@A";
		else
			res = "@" + res;

		return res;

	}

	public final void exportThis(String val)
	{
		if (val != null)
		{
			java.awt.datatransfer.Clipboard cl = java.awt.Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(
					val);
			cl.setContents(ss, ss);
		}
	}

	final static class doublet
	{
		public final String label;

		public final Color color;

		public doublet(String theLabel, Color theColor)
		{
			label = theLabel;
			color = theColor;
		}
	}

	/**
	 * interface which we use to implement class capable of formatting a set of
	 * layers once they've been read in
	 */
	public static interface LayersFormatter
	{
		public void formatLayers(Layers newData);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testImport extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		private static String fileName = "test.rep";

		boolean fileFinished = false;

		boolean allFilesFinished = false;

		public testImport(String val)
		{
			super(val);
			String fileRoot = System.getProperty("dataDir");
			
			// 
			assertNotNull("Check data directory is configured", fileRoot);
			fileName = fileRoot + File.separator + fileName;
						
			// and check the file exists
			java.io.File iFile = new File(fileName);
			assertTrue("Test file not found", iFile.exists());
		}

		public final void testReadREP()
		{
			java.io.File testFile = null;

			// can we load it directly
			testFile = new java.io.File(fileName);

			if (!testFile.exists())
			{

				// first try to get the URL of the image
				java.lang.ClassLoader loader = getClass().getClassLoader();
				if (loader != null)
				{
					java.net.URL imLoc = loader.getResource(fileName);
					if (imLoc != null)
					{
						testFile = new java.io.File(imLoc.getFile());
					}
				}
				else
				{
					fail("Failed to find class loader");
				}
			}

			// did we find it?
			assertTrue("Failed to find file:" + fileName, testFile.exists());

			// ok, now try to read it in
			MWC.GUI.Layers _theLayers = new MWC.GUI.Layers();
			File[] _theFiles = new File[] { testFile };

			// add the REP importer
			MWC.Utilities.ReaderWriter.ImportManager
					.addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());

			// get our thread to import this
			MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller reader = new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
					_theFiles, _theLayers)
			{
				// handle the completion of each file
				public void fileFinished(File fName, Layers newData)
				{
					fileFinished = true;
				}

				// handle completion of the full import process
				public void allFilesFinished(File[] fNames, Layers newData)
				{
					allFilesFinished = true;
				}
			};

			// and start it running
			reader.start();

			// wait for the results
			while (reader.isAlive())
			{
				try
				{
					Thread.sleep(100);
				}
				catch (java.lang.InterruptedException e)
				{
				}
			}

			// check it went ok
			assertTrue("File finished received", fileFinished);
			assertTrue("All Files finished received", allFilesFinished);

			assertEquals("Count of layers", _theLayers.size(), 2);

			// area of coverage
			MWC.GenericData.WorldArea area = _theLayers.elementAt(0).getBounds();
			super.assertEquals("tl lat of first layer", area.getTopLeft().getLat(), 11.92276,
					0.001);
			super.assertEquals("tl long of first layer", area.getTopLeft().getLong(),
					-11.59394, 0.00001);
			super.assertEquals("tl depth of first layer", area.getTopLeft().getDepth(), 0,
					0.00001);

			super.assertEquals("br lat of first layer", area.getBottomRight().getLat(),
					11.89421, 0.001);
			super.assertEquals("br long of first layer", area.getBottomRight().getLong(),
					-11.59376, 0.00001);
			super.assertEquals("br depth of first layer", area.getBottomRight().getDepth(), 0,
					0.00001);

		}
	}

	public static void main(String[] args)
	{
		System.setProperty("dataDir", "d:\\dev\\debrief\\src\\java\\Debrief");
		testImport ti = new testImport("some name");
		ti.testReadREP();
		System.exit(0);
	}

}
