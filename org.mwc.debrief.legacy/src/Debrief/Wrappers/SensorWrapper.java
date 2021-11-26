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

// $RCSfile: SensorWrapper.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.17 $
// $Log: SensorWrapper.java,v $
// Revision 1.17  2006/09/25 14:51:15  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.16  2006/02/13 16:19:07  Ian.Mayo
// Sort out problem with creating sensor data
//
// Revision 1.15  2006/01/06 10:37:42  Ian.Mayo
// Reflect tidying of sensor wrapper naming
//
// Revision 1.14  2005/06/06 14:45:06  Ian.Mayo
// Refactor how we support tma & sensor data
//
// Revision 1.13  2005/06/06 14:17:32  Ian.Mayo
// Reproduce TMAWrapper workaround for sensor data where track visible but none of the individual items
//
// Revision 1.12  2005/02/28 14:57:05  Ian.Mayo
// Handle situation when we have sensor & TUA data outside track period.
//
// Revision 1.11  2005/02/22 09:31:58  Ian.Mayo
// Refactor snail plotting sensor & tma data - so that getting & managing valid data points are handled in generic fashion.  We did have two very similar implementations, tracking errors introduced after hi-res-date changes was proving expensive/unreliable.  All fine now though.
//
// Revision 1.10  2005/01/28 10:52:57  Ian.Mayo
// Fix problems where last data point not shown.
//
// Revision 1.9  2005/01/24 10:30:42  Ian.Mayo
// Provide accessor for host track - to help snail plotting
//
// Revision 1.8  2004/12/17 15:54:00  Ian.Mayo
// Get on top of some problems plotting sensor & tma data.
//
// Revision 1.7  2004/11/25 11:04:38  Ian.Mayo
// More test fixing after hi-res switch, largely related to me removing some unused accessors which were property getters
//
// Revision 1.6  2004/11/25 10:24:48  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.5  2004/11/22 13:41:05  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.4  2004/09/10 09:11:28  Ian.Mayo
// Correct prior mistaken implementation of add(Editable) - we should have just changed the signature of add(Plottable) et al
//
// Revision 1.3  2004/09/09 10:51:56  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.2  2004/09/09 10:23:13  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:49:25  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.12  2003-06-23 13:40:12+01  ian_mayo
// Change line width, if necessary
//
// Revision 1.11  2003-06-16 11:57:33+01  ian_mayo
// Improve tests to check we can add/remove sensor contact data
//
// Revision 1.10  2003-03-27 11:22:54+00  ian_mayo
// reflect new strategy where we return all data when asked to filter by invalid time
//
// Revision 1.9  2003-03-19 15:36:52+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.8  2003-01-15 15:48:23+00  ian_mayo
// With getNearestTo, return annotation when no DTG supplied
//
// Revision 1.7  2002-10-30 16:27:25+00  ian_mayo
// tidy up (shorten) display names of editables
//
// Revision 1.6  2002-10-28 09:04:34+00  ian_mayo
// provide support for variable thickness of lines in tracks, etc
//
// Revision 1.5  2002-10-01 15:41:40+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.4  2002-07-10 14:58:57+01  ian_mayo
// correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.3  2002-07-09 15:27:28+01  ian_mayo
// Return zero-length list instead of null
//
// Revision 1.2  2002-05-28 09:25:13+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:38+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:54+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:27+01  ian_mayo
// Initial revision
//
// Revision 1.9  2001-10-02 09:32:15+01  administrator
// Use new methods for supporting sorted-lists, we aren't getting correct values for tailSet and subSet now that we have changed the comparable implementation within SensorContactWrapper.  We had to do this to allow more than one contact per DTG
//
// Revision 1.8  2001-10-01 12:49:50+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.7  2001-10-01 11:21:39+01  administrator
// Add tests to check we correctly add/manage multiple contacts with the same DTG
//
// Revision 1.6  2001-08-29 19:17:50+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.5  2001-08-24 12:40:25+01  administrator
// Implement remove method
//
// Revision 1.4  2001-08-21 15:19:06+01  administrator
// Improve RangeFrom method
//
// Revision 1.3  2001-08-21 12:05:01+01  administrator
// getFarEnd no longer tries to get its location from the parent
// class testing extended
//
// Revision 1.2  2001-08-17 07:59:19+01  administrator
// Tidying up comments
//
// Revision 1.1  2001-08-14 14:08:17+01  administrator
// finish the implementation
//
// Revision 1.0  2001-08-09 14:16:50+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-31 16:37:21+01  administrator
// show the length of the narrative list when we get its name
//
// Revision 1.0  2001-07-17 08:41:10+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-16 15:02:10+01  novatech
// provide methods to meet new Plottable signature (setVisible)
//
// Revision 1.2  2001-07-09 14:02:47+01  novatech
// let SensorWrapper handle the stepper control
//
// Revision 1.1  2001-07-06 16:00:27+01  novatech
// Initial revision
//

package Debrief.Wrappers;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import Debrief.ReaderWriter.Replay.extensions.TA_COG_ABS_DataHandler;
import Debrief.Tools.Properties.ArrayCentreModePropertyEditor;
import Debrief.Wrappers.Extensions.AdditionalData;
import Debrief.Wrappers.Extensions.AdditionalProvider;
import Debrief.Wrappers.Extensions.AdditionalProvider.ExistingChildrenMayNeedToBeWrapped;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble2;
import Debrief.Wrappers.Track.ArrayOffsetHelper;
import Debrief.Wrappers.Track.ArrayOffsetHelper.ArrayCentreMode;
import Debrief.Wrappers.Track.ArrayOffsetHelper.DeferredDatasetArrayMode;
import Debrief.Wrappers.Track.ArrayOffsetHelper.LegacyArrayOffsetModes;
import Debrief.Wrappers.Track.ArrayOffsetHelper.MeasuredDatasetArrayMode;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.PlainWrapper;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.FullFormatDateTime;

public class SensorWrapper extends TacticalDataWrapper
		implements GriddableSeriesMarker, Cloneable, AdditionalProvider, ExistingChildrenMayNeedToBeWrapped {

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public final class SensorInfo extends Editable.EditorType implements Editable.DynamicDescriptors {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public SensorInfo(final SensorWrapper data) {
			super(data, data.getName(), "Sensor");
		}

		/**
		 * The things about these Layers which are editable. We don't really use this
		 * list, since we have our own custom editor anyway
		 *
		 * @return property descriptions
		 */
		@Override
		public final PropertyDescriptor[] getPropertyDescriptors() {
			try {
				PropertyDescriptor[] res = { prop("Name", "the name for this sensor"),
						prop("Visible", "whether this sensor data is visible"),
						displayProp("LineThickness", "Line tickness", "the thickness to draw these sensor lines"),
						displayProp("DefaultColor", "Default color",
								"the default colour to plot this set of sensor data"),
						displayReadOnlyProp("Coverage", "Start/Finish DTG", "the time coverage for this sensor"),
						displayLongProp("VisibleFrequency", "Visible frequency",
								"How frequently to display sensor cuts",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						displayExpertProp("BaseFrequency", "Base frequency",
								"The base frequency of the source for this sound", OPTIONAL),
						displayExpertLongProp("ResampleDataAt", "Resample data at", "the sensor cut sample rate",
								TEMPORAL, MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						displayExpertLongProp("ArrayCentreMode", "Array Centre Mode",
								"the method used to calculate the array centre", SPATIAL,
								ArrayCentreModePropertyEditor.class) };

				res[2].setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

				// the array centre editor needs to know about our data. Inject the suitable
				// array centre options
				final List<ArrayCentreMode> arrayCentres = getAdditionalArrayCentreModes();
				ArrayCentreModePropertyEditor.setCustomModes(arrayCentres);

				// hey, only bother with the sensor offset if we're in a legacy mode
				final ArrayCentreMode curMode = getArrayCentreMode();
				if (curMode instanceof LegacyArrayOffsetModes) {
					final List<PropertyDescriptor> tmpList = new ArrayList<PropertyDescriptor>();
					tmpList.addAll(Arrays.asList(res));

					// ok, add the sensor offset distance
					tmpList.add(displayProp("SensorOffset", "Sensor offset",
							"the forward/backward offset (m) of this sensor from the attack datum"));

					res = tmpList.toArray(res);

					// NOTE: this info class to implement Editable.DynamicDescriptors
					// to avoid these descriptors being cached
				}

				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	static public final class testSensors extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testSensors(final String val) {
			super(val);
		}

		public final void testDuplicates() {
			// ok, create the test object
			final SensorWrapper sensor = new SensorWrapper("tester");

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 24);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 01);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 05);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 55);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			// so, we've now build up the list
			// check it has the correct quantity
			assertEquals("Count of items", 8, sensor._myContacts.size());

			// check the correct number get returned
			cal.set(2001, 10, 4, 4, 4, 25);
			final MWC.GenericData.Watchable[] list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			assertEquals("after end of data", 2, list.length);

		}

		public void testMeasuredData() throws ParseException {
			final TrackWrapper tw = new TrackWrapper();
			tw.setName("SENSOR");
			final SensorWrapper sw = new SensorWrapper("TA_ARRAY");
			tw.add(sw);
			final Layers layers = new Layers();
			layers.addThisLayer(tw);

			// get some data
			final TA_COG_ABS_DataHandler reader = new TA_COG_ABS_DataHandler();
			reader.setLayers(layers);
			reader.readThisLine(";TA_COG_ABS: 100112 120200 SENSOR TA_ARRAY 4.0 5.0 19.02");
			reader.readThisLine(";TA_COG_ABS: 100112 120300 SENSOR TA_ARRAY 5.0 6.0 19.02");
			reader.readThisLine(";TA_COG_ABS: 100112 120400 SENSOR TA_ARRAY 6.0 7.0 19.02");
			reader.readThisLine(";TA_COG_ABS: 100112 120500 SENSOR TA_ARRAY 7.0 8.0 19.02");
			reader.readThisLine(";TA_COG_ABS: 100112 120600 SENSOR TA_ARRAY 8.0 9.0 19.02");
			reader.readThisLine(";TA_COG_ABS: 100112 120700 SENSOR TA_ARRAY 9.0 10.0 19.02");
			final String dateToken = "100112";

			// create the datasets
			reader.finalise();

			// check data added
			final AdditionalData data = sw.getAdditionalData();

			assertNotNull(data);
			assertEquals(1, data.size());

			// have a go at setting the mode
			final List<ArrayCentreMode> modes = sw.getAdditionalArrayCentreModes();

			final ArrayCentreMode plainMode = modes.get(2);

			// check we found it
			assertNotNull(plainMode);

			// check we've got the right mode
			assertTrue("check we have the correct mode", plainMode instanceof MeasuredDatasetArrayMode);

			final MeasuredDatasetArrayMode arrayMode = (MeasuredDatasetArrayMode) plainMode;

			// force interpolation off
			arrayMode.setInterpolatePositions(false);

			// check we're not interpolating
			assertFalse("We should not be interpolating", arrayMode.getInterpolatePositions());

			HiResDate theDate = DebriefFormatDateTime.parseThis(dateToken, "120430");
			WorldLocation loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
			assertNotNull(loc);
			assertEquals("next location", " 07\u00B000'00.00\"N 008\u00B000\'00.00\"E ", loc.toString());

			// try on a time
			theDate = DebriefFormatDateTime.parseThis(dateToken, "120400");
			loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
			assertNotNull(loc);
			assertEquals("next location", " 06\u00B000'00.00\"N 007\u00B000\'00.00\"E ", loc.toString());

			// try before start
			theDate = DebriefFormatDateTime.parseThis(dateToken, "120100");
			loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
			assertNull(loc);

			// try after end
			theDate = DebriefFormatDateTime.parseThis(dateToken, "121100");
			loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
			assertNull(loc);

			// try interpolation
			theDate = DebriefFormatDateTime.parseThis(dateToken, "120430");
			arrayMode.setInterpolatePositions(true);
			loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
			assertNotNull(loc);
			assertEquals("next location", " 06\u00B030'00.00\"N 007\u00B030\'00.00\"E ", loc.toString());

			// try before the start

		}

		public final void testMergeDiffColors1() {
			// ok, create the test object
			final SensorWrapper sensorA = new SensorWrapper("tester");
			sensorA.setColor(Color.blue);

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 5, 02);
			final SensorWrapper sensorB = new SensorWrapper("tester");
			sensorB.setColor(Color.red);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			cal.set(2001, 10, 4, 4, 5, 03);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					Color.green, null, 1, sensorB.getName()));

			cal.set(2001, 10, 4, 4, 5, 05);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			cal.set(2001, 10, 4, 4, 5, 55);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			final Layer sensorHolder = new BaseLayer();
			sensorHolder.add(sensorA);
			sensorHolder.add(sensorB);

			final Editable[] selection = new Editable[] { sensorA, sensorB };

			assertEquals("sensorA has elements", 3, sensorA.size());
			assertEquals("sensorB has elements", 4, sensorB.size());

			// ok, do the merge
			mergeSensors(sensorA, null, sensorHolder, selection);

			assertEquals("sensorA has elements", 7, sensorA.size());
			assertEquals("sensorB has elements", 0, sensorB.size());

			// now look at the colors
			final Enumeration<Editable> numer = sensorA.elements();
			int ctr = 0;
			while (numer.hasMoreElements()) {
				final SensorContactWrapper scw = (SensorContactWrapper) numer.nextElement();
				if (ctr < 3) {
					assertEquals("Correct color for A", Color.blue, scw.getColor());
					assertEquals("Correct default for A", null, scw.getActualColor());
				} else if (ctr == 4) {
					assertEquals("Correct color for B", Color.green, scw.getColor());
				} else {
					assertEquals("Correct color for B", Color.red, scw.getColor());
				}
				ctr++;
			}
		}

		public final void testMergeDiffColors2() {
			// ok, create the test object
			final SensorWrapper sensorA = new SensorWrapper("tester");
			sensorA.setColor(Color.blue);

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 4, 27);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 5, 02);
			final SensorWrapper sensorB = new SensorWrapper("tester");
			sensorB.setColor(Color.red);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			cal.set(2001, 10, 4, 4, 5, 03);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			cal.set(2001, 10, 4, 4, 5, 05);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			final Layer sensorHolder = new BaseLayer();
			sensorHolder.add(sensorA);
			sensorHolder.add(sensorB);

			final Editable[] selection = new Editable[] { sensorA, sensorB };

			assertEquals("sensorA has elements", 4, sensorA.size());
			assertEquals("sensorB has elements", 3, sensorB.size());

			// ok, do the merge
			mergeSensors(sensorB, null, sensorHolder, selection);

			assertEquals("sensorA has elements", 0, sensorA.size());
			assertEquals("sensorB has elements", 7, sensorB.size());

			// now look at the colors
			final Enumeration<Editable> numer = sensorB.elements();
			int ctr = 0;
			while (numer.hasMoreElements()) {
				final SensorContactWrapper scw = (SensorContactWrapper) numer.nextElement();
				if (ctr++ < 4) {
					assertEquals("Correct color for A", Color.blue, scw.getColor());
					assertEquals("Correct default for A", Color.blue, scw.getActualColor());
				} else {
					assertEquals("Correct color for B", Color.red, scw.getColor());
					assertEquals("Correct default for A", null, scw.getActualColor());
				}
			}
		}

		public final void testMergeSameColors() {
			// ok, create the test object
			final SensorWrapper sensorA = new SensorWrapper("tester");
			sensorA.setColor(Color.blue);

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorA.getName()));

			cal.set(2001, 10, 4, 4, 5, 02);
			final SensorWrapper sensorB = new SensorWrapper("tester");
			sensorB.setColor(Color.blue);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			cal.set(2001, 10, 4, 4, 5, 03);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			cal.set(2001, 10, 4, 4, 5, 05);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			cal.set(2001, 10, 4, 4, 5, 55);
			sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensorB.getName()));

			final Layer sensorHolder = new BaseLayer();
			sensorHolder.add(sensorA);
			sensorHolder.add(sensorB);

			final Editable[] selection = new Editable[] { sensorA, sensorB };

			assertEquals("sensorA has elements", 3, sensorA.size());
			assertEquals("sensorB has elements", 4, sensorB.size());

			// ok, do the merge
			mergeSensors(sensorA, null, sensorHolder, selection);

			assertEquals("sensorA has elements", 7, sensorA.size());
			assertEquals("sensorB has elements", 0, sensorB.size());

			// now look at the colors
			final Enumeration<Editable> numer = sensorB.elements();
			while (numer.hasMoreElements()) {
				final SensorContactWrapper scw = (SensorContactWrapper) numer.nextElement();
				assertEquals("Correct (Default) color for B", null, scw.getColor());
			}
		}

		public void testMultipleContacts() {
			final SensorWrapper sw = new SensorWrapper("bbb");
			final SensorContactWrapper sc1 = new SensorContactWrapper("bbb", new HiResDate(0, 9), null, null, null,
					null, "first", 0, sw.getName());
			final SensorContactWrapper sc2 = new SensorContactWrapper("bbb", new HiResDate(0, 12), null, null, null,
					null, "first", 0, sw.getName());
			final SensorContactWrapper sc3 = new SensorContactWrapper("bbb", new HiResDate(0, 7), null, null, null,
					null, "first", 0, sw.getName());
			final SensorContactWrapper sc4 = new SensorContactWrapper("bbb", new HiResDate(0, 13), null, null, null,
					null, "first", 0, sw.getName());

			sw.add(sc1);
			sw.add(sc2);
			sw.add(sc3);
			sw.add(sc4);

			assertEquals("four contacts loaded", 4, sw._myContacts.size());

			// check we can delete from it
			sw.removeElement(sc3);

			assertEquals("now only three contacts loaded", 3, sw._myContacts.size());

		}

		public final void testMyParams() {
			MWC.GUI.Editable ed = new SensorWrapper("my name");
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		public final void testValues() {
			// ok, create the test object
			final SensorWrapper sensor = new SensorWrapper("tester");

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			final long start_time = cal.getTime().getTime();
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 27);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 02);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 01);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 05);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 55);
			final long end_time = cal.getTime().getTime();
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			// so, we've now build up the list
			// check it has the correct quantity
			assertTrue("Count of items", (sensor._myContacts.size() == 8));

			// check the outer limits
			final HiResDate start = sensor.getStartDTG();
			final HiResDate end = sensor.getEndDTG();
			assertEquals("first time", start.getDate().getTime(), start_time);
			assertEquals("last time", end.getDate().getTime(), end_time);

			// //////////////////////////////////////////////////////////////////////
			// finding the nearest entry
			cal.set(2001, 10, 4, 4, 4, 05);
			MWC.GenericData.Watchable[] list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime()));
			SensorContactWrapper nearest = (SensorContactWrapper) list[0];
			assertEquals("Nearest matching fix", nearest.getDTG().getDate().getTime(), cal.getTime().getTime());

			final java.util.Calendar cal_other = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);
			cal_other.set(2001, 10, 4, 4, 4, 03);
			list = sensor.getNearestTo(new HiResDate(cal_other.getTime().getTime()));
			nearest = (SensorContactWrapper) list[0];
			assertTrue("Nearest or greater than fix", (nearest.getDTG().getMicros() / 1000 == cal.getTime().getTime()));

			// ///////////////////////////////////////////////////////////////////
			// filter the list
			cal.set(2001, 10, 4, 4, 4, 22);
			cal_other.set(2001, 10, 4, 4, 4, 25);

			// ////////////////////////////////////////////////////////////////////////
			// do the filter
			sensor.filterListTo(new HiResDate(cal.getTime().getTime()), new HiResDate(cal_other.getTime().getTime()));

			// see how many remain visible
			java.util.Enumeration<Editable> iter = sensor.elements();
			int counter = 0;
			while (iter.hasMoreElements()) {
				final SensorContactWrapper contact = (SensorContactWrapper) iter.nextElement();
				if (contact.getVisible())
					counter++;
			}
			// check that the correct number are visible
			assertTrue("Correct filtering of list", (counter == 2));

			// clear the filter
			sensor.filterListTo(sensor.getStartDTG(), sensor.getEndDTG());
			// see how many remain visible
			iter = sensor.elements();
			counter = 0;
			while (iter.hasMoreElements()) {
				final SensorContactWrapper contact = (SensorContactWrapper) iter.nextElement();
				if (contact.getVisible())
					counter++;
			}
			// check that the correct number are visible
			assertTrue("Correct removal of list filter", (counter == 8));

			// //////////////////////////////////////////////////////
			// get items between
			java.util.Collection<Editable> res = sensor.getItemsBetween(new HiResDate(cal.getTime().getTime()),
					new HiResDate(cal_other.getTime().getTime()));
			assertTrue("get items between", (res.size() == 2));

			// do recheck, since this time we will be resetting the working
			// variables, rather and creating them
			cal.set(2001, 10, 4, 4, 4, 5);
			cal_other.set(2001, 10, 4, 4, 4, 27);
			res = sensor.getItemsBetween(new HiResDate(cal.getTime().getTime()),
					new HiResDate(cal_other.getTime().getTime()));
			assertEquals("recheck get items between:" + res.size(), 4, res.size());

			// and show all of the data
			res = sensor.getItemsBetween(sensor.getStartDTG(), sensor.getEndDTG());
			assertTrue("recheck get items between:" + res.size(), (res.size() == 8));

			// /////////////////////////////////////////////////////////
			// test the position related stuff
			final TrackWrapper track = new TrackWrapper();

			// and add the fixes
			cal.set(2001, 10, 4, 4, 4, 0);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new MWC.GenericData.WorldLocation(2.0, 2.0, 0.0), 12, 12)));

			cal.set(2001, 10, 4, 4, 4, 01);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new MWC.GenericData.WorldLocation(2.0, 2.25, 0.0), 12, 12)));

			cal.set(2001, 10, 4, 4, 4, 02);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new MWC.GenericData.WorldLocation(2.0, 2.5, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 05);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new MWC.GenericData.WorldLocation(2.0, 2.75, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 23);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new MWC.GenericData.WorldLocation(2.25, 2.0, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 25);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new MWC.GenericData.WorldLocation(2.5, 2.0, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 28);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new MWC.GenericData.WorldLocation(2.75, 2.0, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 55);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new MWC.GenericData.WorldLocation(2.25, 2.25, 0.0), 12, 12)));

			// ok, put the sensor data into the track
			track.add(sensor);

			track.setInterpolatePoints(false);

			// now find the location of an item, any item!
			cal.set(2001, 10, 4, 4, 4, 27);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			nearest = (SensorContactWrapper) list[0];
			WorldLocation nearestPoint = nearest.getCalculatedOrigin(track);
			WorldLocation tgtLoc = new MWC.GenericData.WorldLocation(2.66666, 2.0, 0.0);
			assertEquals("first test", 0, tgtLoc.rangeFrom(nearestPoint), 0.001);

			// ah-ha! what about a contact between two fixes
			cal.set(2001, 10, 4, 4, 4, 26);
			final HiResDate theTime = new HiResDate(cal.getTime().getTime(), 0);
			list = sensor.getNearestTo(theTime);
			nearest = (SensorContactWrapper) list[0];
			nearestPoint = nearest.getCalculatedOrigin(track);
			assertEquals("test mid way", 0, tgtLoc.rangeFrom(nearestPoint), 0.001);

			// ok, that was half-way, what making it nearer to one of the fixes
			cal.set(2001, 10, 4, 4, 4, 25);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			nearest = (SensorContactWrapper) list[0];
			nearestPoint = nearest.getCalculatedOrigin(track);
			tgtLoc = new MWC.GenericData.WorldLocation(2.5, 2.0, 0.0);
			assertEquals("test nearer first point", 0, tgtLoc.rangeFrom(nearestPoint), 0.001);

			// start point?
			cal.set(2001, 10, 4, 4, 4, 0);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			nearest = (SensorContactWrapper) list[0];
			nearestPoint = nearest.getCalculatedOrigin(track);
			assertEquals("test start point", new MWC.GenericData.WorldLocation(2.0, 2.0, 0.0), nearestPoint);

			// end point?
			cal.set(2001, 10, 4, 4, 4, 55);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			nearest = (SensorContactWrapper) list[0];
			nearestPoint = nearest.getCalculatedOrigin(track);
			assertEquals("test end point", nearestPoint, new MWC.GenericData.WorldLocation(2.25, 2.25, 0.0));

			// before start of track data?
			cal.set(2001, 10, 4, 4, 3, 0);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			assertEquals("before range of data", list.length, 0);

			// after end of track data?
			cal.set(2001, 10, 4, 4, 7, 0);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			assertEquals("after end of data", list.length, 1);

		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		final testSensors ts = new testSensors("Ian");
		ts.testDuplicates();
		ts.testValues();
	}

	/**
	 * perform a merge of the supplied tracks.
	 *
	 * @param target    the final recipient of the other items
	 * @param theLayers
	 * @param parent    the parent tracks for the supplied items
	 * @param subjects  the actual selected items
	 * @return sufficient information to undo the merge
	 */
	public static int mergeSensors(final Editable targetE, final Layers theLayers, final Layer parent,
			final Editable[] subjects) {
		final SensorWrapper target = (SensorWrapper) targetE;
		final Color defaultColor = target.getColor();

		for (int i = 0; i < subjects.length; i++) {
			final SensorWrapper sensor = (SensorWrapper) subjects[i];
			if (sensor != target) {
				target.append(sensor, defaultColor);
				parent.removeElement(sensor);
			}
		}

		return MessageProvider.OK;
	}

	/**
	 * more optimisatons
	 */
	transient private SensorContactWrapper nearestContact;

	/**
	 * the (optional) sensor offset value, indicating the forward/backward offset
	 * compared to the attack datum of the platform.
	 */
	private WorldDistance.ArrayLength _sensorOffset = new WorldDistance.ArrayLength(0);

	/**
	 * the (optional) indicator for whether the centre of this sensor is in a
	 * straight line fwd/backward of the attack datum, or whether it's a dragged
	 * sensor that follows the track of it's host platform (like a towed array).
	 */
	private ArrayCentreMode _arrayCentreMode = LegacyArrayOffsetModes.WORM;

	/**
	 * the radiated (source) transmitted frequency
	 *
	 */
	private double _baseFrequency = 0;

	// //////////////////////////////////////
	// member methods to meet plain wrapper responsibilities
	// //////////////////////////////////////

	private HiResDate _lastDataFrequency = new HiResDate(0, TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	/**
	 * handler for supplemental data
	 *
	 */
	private AdditionalData _additionalData;

	/**
	 * create a copy of the supplied sensor wrapper
	 *
	 * @param other wrapper to copy
	 */
	public SensorWrapper(final SensorWrapper other) {
		super(other.getName());
		this.setTrackName(other.getTrackName());
		this.setHost(other.getHost());
		this.setColor(other.getColor());
		this.setVisible(other.getVisible());
		this.setSensorOffset(other.getSensorOffset());
		this.setLineThickness(other.getLineThickness());
		this.setArrayCentreMode(other.getArrayCentreMode());
		// note: we no longer use the boolean worm in hole
		// mode, it's part of the above array centre mode
		// this.setWormInHole(other.getWormInHole());
	}

	// //////////////////////////////////////
	// constructors
	/**
	 * ////////////////////////////////////////
	 */
	public SensorWrapper(final String title) {
		super(title);
	}

	/**
	 * add
	 *
	 * @param plottable parameter for add
	 */
	@Override
	public final void add(final MWC.GUI.Editable plottable) {
		// check it's a sensor contact entry
		if (plottable instanceof SensorContactWrapper) {
			_myContacts.add(plottable);

			final SensorContactWrapper scw = (SensorContactWrapper) plottable;

			// maintain our time period
			if (_timePeriod == null)
				_timePeriod = new MWC.GenericData.TimePeriod.BaseTimePeriod(scw.getDTG(), scw.getDTG());
			else
				_timePeriod.extend(scw.getDTG());

			// and tell the contact about us
			scw.setSensor(this);
		}
	}

	/**
	 * append the sensor cuts from new sensor to this one
	 *
	 * @param theSensor
	 * @param defaultColor
	 */
	public final void append(final SensorWrapper theSensor, final Color defaultColor) {
		final SortedSet<Editable> otherC = theSensor._myContacts;
		final Color hisColor = theSensor.getColor();
		final Color colorToUse;
		if (!hisColor.equals(defaultColor)) {
			colorToUse = hisColor;
		} else {
			colorToUse = null;
		}
		for (final Iterator<Editable> iterator = otherC.iterator(); iterator.hasNext();) {
			final SensorContactWrapper thisC = (SensorContactWrapper) iterator.next();
			if (thisC.getActualColor() == null && colorToUse != null) {
				thisC.setColor(colorToUse);
			}
			this.add(thisC);
		}

		// and clear him out...
		otherC.clear();

		// and drop the links
		theSensor.setHost(null);
		theSensor.setTrackName(null);
	}

	@Override
	public boolean childrenNeedWrapping() {
		// this is always true, since objects using a sensor-wrapper
		// always expect the cuts to be returned from the
		// elements() call
		return true;
	}

	// ///////////////////////////////////////
	// other member functions
	// ///////////////////////////////////////

	/**
	 * our parent has changed, clear data that depends on it
	 *
	 */
	private void clearChildOffsets() {
		// we also need to reset the origins on our child elements, since
		// the offset will have changed
		final java.util.Iterator<Editable> it = this._myContacts.iterator();
		while (it.hasNext()) {
			final SensorContactWrapper fw = (SensorContactWrapper) it.next();
			fw.clearCalculatedOrigin();

			// and tell it we're the boss
			fw.setSensor(this);
		}
	}

	/**
	 * create a new instance of an entity of this type, interpolated between the
	 * supplied sample objects
	 *
	 */
	@Override
	protected PlottableWrapperWithTimeAndOverrideableColor createItem(
			final PlottableWrapperWithTimeAndOverrideableColor last,
			final PlottableWrapperWithTimeAndOverrideableColor next, final LinearInterpolator interp, final long tNow) {
		final SensorContactWrapper _next = (SensorContactWrapper) next;
		final SensorContactWrapper _last = (SensorContactWrapper) last;

		final double brg = interp.interp(_last.getBearing(), _next.getBearing());
		double ambig = 0;
		// note - don't bother checking for has ambig, just do the interpolation
		ambig = interp.interp(_last.getAmbiguousBearing(), _next.getAmbiguousBearing());

		final double freq = interp.interp(_last.getFrequency(), _next.getFrequency());
		// do we have range?
		WorldDistance theRng = null;
		if ((_last.getRange() != null) && (_next.getRange() != null)) {
			// are they both in the same units?
			if (_last.getRange().getUnits() == _last.getRange().getUnits()) {
				// they're in the same units, stick with it.
				final int theUnits = _last.getRange().getUnits();
				final double theVal = interp.interp(_last.getRange().getValue(), _next.getRange().getValue());
				theRng = new WorldDistance(theVal, theUnits);
			} else {
				// they're in different units, do it all in degrees
				final double rngDegs = interp.interp(_last.getRange().getValueIn(WorldDistance.DEGS),
						_next.getRange().getValueIn(WorldDistance.DEGS));
				theRng = new WorldDistance(rngDegs, WorldDistance.DEGS);
			}
		}
		// do we have an origin?
		WorldLocation origin = null;
		if ((_last.getOrigin() != null) && (_next.getOrigin() != null)) {
			final double orLat = interp.interp(_last.getOrigin().getLat(), _next.getOrigin().getLat());
			final double orLong = interp.interp(_last.getOrigin().getLong(), _next.getOrigin().getLong());
			origin = new WorldLocation(orLat, orLong, 0);
		}

		// now, go create the new data item
		final SensorContactWrapper newS = new SensorContactWrapper(_last.getTrackName(), new HiResDate(0, tNow), theRng,
				brg, ambig, freq, origin, _last.getActualColor(), _last.getName(), _last.getLineStyle().intValue(),
				_last.getSensorName());

		// sort out the ambiguous data
		newS.setHasAmbiguousBearing(_last.getHasAmbiguousBearing());

		return newS;
	}

	@Override
	public void doSave(final String message) {
		throw new RuntimeException("should not have called manual save for Sensor Wrapper");
	}

	/**
	 * retrieve any measured datasets that are capable of providing a location for
	 * the sensor
	 *
	 * @return list of suitable datasets
	 */
	private List<ArrayCentreMode> getAdditionalArrayCentreModes() {
		return ArrayOffsetHelper.getAdditionalArrayCentreModes(this);
	}

	// /////////////////////////////////////////////////////////////////
	// support for WatchableList interface (required for Snail Trail plotting)
	// //////////////////////////////////////////////////////////////////

	@Override
	public AdditionalData getAdditionalData() {
		if (_additionalData == null) {
			_additionalData = new AdditionalData();
		}
		return _additionalData;
	}

	public WorldLocation getArrayCentre(final HiResDate time, final WorldLocation hostLocation,
			final TrackWrapper track) {
		return ArrayOffsetHelper.getArrayCentre(this, time, hostLocation, track);
	}

	/**
	 * get the current array centre mode. Note: this now includes the ability to
	 * name a measured data source as the origin for the sensor
	 *
	 * @return one of {@link #ArrayCentreMode} or the name of a specific dataset
	 */
	public ArrayCentreMode getArrayCentreMode() {
		// ok, see if we're using a deferred mode. If we are, we should correct it
		if (_arrayCentreMode instanceof DeferredDatasetArrayMode) {
			_arrayCentreMode = ArrayOffsetHelper.sortOutDeferredMode((DeferredDatasetArrayMode) _arrayCentreMode, this);
		}

		return _arrayCentreMode;
	}

	// ///////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////

	public double getBaseFrequency() {
		return _baseFrequency;
	}

	/**
	 * the real getBounds object, which uses properties of the parent
	 */
	@Override
	public final MWC.GenericData.WorldArea getBounds() {
		// we no longer just return the bounds of the track, because a portion
		// of the track may have been made invisible.
		// instead, we will pass through the full dataset and find the outer
		// bounds of the visible area
		WorldArea res = null;

		if (!getVisible()) {
			// hey, we're invisible, return null
		} else {
			final java.util.Iterator<Editable> it = this._myContacts.iterator();
			while (it.hasNext()) {
				final SensorContactWrapper fw = (SensorContactWrapper) it.next();

				// is this point visible?
				if (fw.getVisible()) {

					// has our data been initialised?
					if (res == null) {
						// no, initialise it
						final WorldLocation startOfLine = fw.getCalculatedOrigin(_myHost);

						// we may not have a sensor-data origin, since the
						// sensor may be out of the time period of the track
						if (startOfLine != null)
							res = new WorldArea(startOfLine, startOfLine);
					} else {
						// yes, extend to include the new area
						res.extend(fw.getCalculatedOrigin(_myHost));
					}

					// do we have a far end?

					if (fw.getRange() != null) {
						final WorldLocation farEnd = fw.getFarEnd(null);
						if (farEnd != null) {
							if (res == null)
								res = new WorldArea(farEnd, farEnd);
							else
								res.extend(fw.getFarEnd(null));
						}
					}
				}
			}
		}

		return res;
	}

	/**
	 * provide the time coverage, in text form
	 *
	 * @return
	 */
	public String getCoverage() {
		final String res;
		if (_myContacts.isEmpty()) {
			res = "n/a";
		} else {
			final SensorContactWrapper first = (SensorContactWrapper) _myContacts.first();
			final SensorContactWrapper last = (SensorContactWrapper) _myContacts.last();
			res = FullFormatDateTime.toString(first.getDTG().getDate().getTime()) + " - "
					+ FullFormatDateTime.toString(last.getDTG().getDate().getTime());
		}

		return res;
	}

	/**
	 * get the parent's color Note: we're wrapping the color paramter with
	 * defaultColor so that we can provide more understable attribute names in
	 * property editor
	 *
	 * @return
	 */
	public Color getDefaultColor() {
		return super.getColor();
	}

	/**
	 * getInfo
	 *
	 * @return the returned MWC.GUI.Editable.EditorType
	 */
	@Override
	public final MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new SensorInfo(this);

		return _myEditor;
	}

	@Override
	public String getItemsName() {
		return "Cuts (" + this.size() + " items)";
	}

	/**
	 * use the specified measured dataset to produce a sensor origin
	 *
	 * @param dataset
	 * @param time
	 * @param nearestTrackFix
	 * @return
	 */
	public WorldLocation getMeasuredLocationAt(final MeasuredDatasetArrayMode measuredMode, final HiResDate time,
			final WorldLocation hostLocation) {
		// ok, is it a relative or absolute dataset
		final TimeSeriesDatasetDouble2 dataset = measuredMode.getDataset();

		final String units = dataset.getUnits();

		final int index = dataset.getIndexNearestTo(time.getDate().getTime());

		final WorldLocation res;

		if (index == TimeSeriesCore.INVALID_INDEX) {
			// ok, failed
			res = null;
		} else {
			final double after1 = dataset.getValue1At(index);
			final double after2 = dataset.getValue2At(index);
			final long afterTime = dataset.getTimes().get(index);

			// ok, get the location
			final WorldLocation afterLoc = getValueAt(hostLocation, units, after1, after2);

			// right, see if we should be interpolating
			if (measuredMode.getInterpolatePositions()) {
				// check the time of the data
				if (afterTime == time.getDate().getTime()) {
					// don't bother, we've got the best value
					res = afterLoc;
				} else {
					// ok, we need to interpolate
					if (index > 0) {
						final double before1 = dataset.getValue1At(index - 1);
						final double before2 = dataset.getValue2At(index - 1);
						final long beforeTime = dataset.getTimes().get(index - 1);

						// ok, get the location
						final WorldLocation beforeLoc = getValueAt(hostLocation, units, before1, before2);

						// put the locations into fixes, so we can do fix interpolation (with time)
						final Fix beforeFix = new Fix(new HiResDate(beforeTime), beforeLoc, 0d, 0d);
						final FixWrapper before = new FixWrapper(beforeFix);

						final Fix afterFix = new Fix(new HiResDate(afterTime), afterLoc, 0d, 0d);
						final FixWrapper after = new FixWrapper(afterFix);

						// and now interpolate to find the right value
						final FixWrapper interp = FixWrapper.interpolateFix(before, after, time);

						res = interp.getLocation();
					} else {
						// we're using the first value, we can't go back any further
						res = afterLoc;
					}
				}
			} else {
				// we don't want to interpolate
				res = afterLoc;
			}
		}

		return res;
	}

	/**
	 * get the watchable in this list nearest to the specified DTG - we take most of
	 * this processing from the similar method in TrackWrappper. If the DTG is after
	 * our end, return our last point
	 *
	 * @param DTG the DTG to search for
	 * @return the nearest Watchable
	 */
	@Override
	public final MWC.GenericData.Watchable[] getNearestTo(final HiResDate DTG) {

		/**
		 * we need to end up with a watchable, not a fix, so we need to work our way
		 * through the fixes
		 */
		MWC.GenericData.Watchable[] res = new MWC.GenericData.Watchable[] {};

		// check that we do actually contain some data
		if (_myContacts.size() == 0)
			return res;

		// see if this is the DTG we have just requestsed
		if ((DTG.equals(lastDTG)) && (lastContact != null)) {
			res = lastContact;
		} else {
			// see if this DTG is inside our data range
			// in which case we will just return null
			final SensorContactWrapper theFirst = (SensorContactWrapper) _myContacts.first();
			final SensorContactWrapper theLast = (SensorContactWrapper) _myContacts.last();

			if ((DTG.greaterThanOrEqualTo(theFirst.getDTG())) && (DTG.lessThanOrEqualTo(theLast.getDTG()))) {
				// yes it's inside our data range, find the first fix
				// after the indicated point

				// see if we have to create our local temporary fix
				if (nearestContact == null) {
					nearestContact = new SensorContactWrapper(null, DTG, null, null, null, null, null, 0, getName());
				} else
					nearestContact.setDTG(DTG);

				// get the data..
				final java.util.Vector<SensorContactWrapper> list = new java.util.Vector<SensorContactWrapper>(0, 1);
				boolean finished = false;
				final java.util.Iterator<Editable> it = _myContacts.iterator();
				while ((it.hasNext()) && (!finished)) {
					final SensorContactWrapper scw = (SensorContactWrapper) it.next();
					final HiResDate thisDate = scw.getTime();
					if (thisDate.lessThan(DTG)) {
						// before it, ignore!
					} else if (thisDate.greaterThan(DTG)) {
						// hey, it's a possible - if we haven't found an exact
						// match
						if (list.size() == 0) {
							list.add(scw);
						} else {
							// hey, we're finished!
							finished = true;
						}
					} else {
						// hey, it must be at the same time!
						list.add(scw);
					}

				}

				if (list.size() > 0) {
					final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[] { null };
					res = list.toArray(dummy);
				}
			} else if (DTG.greaterThanOrEqualTo(theLast.getDTG())) {
				// is it after the last one? If so, just plot the last one. This
				// helps us when we're doing snail trails.
				final java.util.Vector<SensorContactWrapper> list = new java.util.Vector<SensorContactWrapper>(0, 1);
				list.add(theLast);
				final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[] { null };
				res = list.toArray(dummy);
			}

			// and remember this fix
			lastContact = res;
			lastDTG = DTG;
		}

		return res;

	}

	/**
	 * method to allow the setting of data sampling frequencies for the track &
	 * sensor data
	 *
	 * @return frequency to use
	 */
	public final HiResDate getResampleDataAt() {
		return this._lastDataFrequency;
	}

	@Override
	public Editable getSampleGriddable() {
		Editable res = null;

		// check we have an item before we edit it
		final Enumeration<Editable> eles = this.elements();
		if (eles.hasMoreElements())
			res = eles.nextElement();
		return res;
	}

	// ////////////////////////////////////////////////////
	// nested class for testing
	// /////////////////////////////////////////////////////

	public WorldDistance.ArrayLength getSensorOffset() {
		return _sensorOffset;
	}

	private WorldLocation getValueAt(final WorldLocation hostLocation, final String units, final double val1,
			final double val2) {
		final WorldLocation res;
		if (units.equals("m")) {
			// ok, relative calculation
			final double rangeM = Math.sqrt(Math.pow(val1, 2) + Math.pow(val2, 2));
			final double angleRads = Math.atan2(val1, val2);

			res = hostLocation.add(new WorldVector(angleRads, new WorldDistance(rangeM, WorldDistance.METRES),
					new WorldDistance(0, WorldDistance.METRES)));

		} else {
			// ok, absolute location
			res = new WorldLocation(val1, val2, 0);
		}
		return res;
	}

	/**
	 *
	 * @return yes/no for whether to use worm in hole
	 * @deprecated we no long use this boolean mode. We now allow custom array modes
	 *             - so please use {@link #setArrayCentreMode(String)}
	 */
	@Deprecated
	public Boolean getWormInHole() {
		return getArrayCentreMode().equals(LegacyArrayOffsetModes.WORM);
	}

	@Override
	public TimeStampedDataItem makeCopy(final TimeStampedDataItem item) {
		if (false == item instanceof SensorContactWrapper) {
			throw new IllegalArgumentException("I am expecting the Observation's, don't know how to copy " + item);
		}
		final SensorContactWrapper template = (SensorContactWrapper) item;
		final SensorContactWrapper result = new SensorContactWrapper();
		result.setAmbiguousBearing(template.getAmbiguousBearing());
		result.setBearing(template.getBearing());
		result.setColor(template.getColor());
		result.setDTG(template.getDTG());
		result.setFrequency(template.getFrequency());
		result.setHasAmbiguousBearing(template.getHasAmbiguousBearing());
		result.setHasFrequency(template.getHasFrequency());
		result.setLabel(template.getLabel());
		result.setLabelLocation(template.getLabelLocation());
		result.setLabelVisible(template.getLabelVisible());
		result.setLineStyle(template.getLineStyle());
		result.setOrigin(template.getOrigin());
		result.setPutLabelAt(template.getPutLabelAt());
		result.setRange(template.getRange());
		result.setSensor(template.getSensor());
		return result;
	}

	/**
	 * how far away are we from this point? or return null if it can't be calculated
	 */
	@Override
	public final double rangeFrom(final WorldLocation other) {
		double res = INVALID_RANGE;

		// if we have a nearest contact, see how far away it is.
		if (nearestContact != null)
			res = nearestContact.rangeFrom(other);

		return res;
	}

	@Override
	public boolean requiresManualSave() {
		return false;
	}

	/**
	 * get the current array centre mode. Note: this now includes the ability to
	 * name a measured data source as the origin for the sensor
	 *
	 * @param one of {@link #ArrayCentreMode} or the name of a specific dataset
	 */
	public void setArrayCentreMode(final ArrayCentreMode mode) {
		if (mode != _arrayCentreMode) {
			// remember the new value
			_arrayCentreMode = mode;

			// we've got to recalculate our positions now, really.
			clearChildOffsets();

			// ok, fire the property change - to tell folks we've moved
			firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, _arrayCentreMode);
		}
	}

	public void setBaseFrequency(final double baseFrequency) {
		final double oldFreq = _baseFrequency;

		_baseFrequency = baseFrequency;

		// hmm, can we fire an update?
		final TrackWrapper hostTrack = this.getHost();
		if (hostTrack != null) {
			hostTrack.firePropertyChange(SupportsPropertyListeners.FORMAT, oldFreq, _baseFrequency);
		}
	}

	/**
	 * just pass the property onto the parent
	 *
	 * @param defaultColor
	 */
	@FireReformatted
	public void setDefaultColor(final Color defaultColor) {
		super.setColor(defaultColor);
	}

	/**
	 * override the parent method - since we want to reset the origin for our child
	 * sensor data items
	 */
	@Override
	public void setHost(final TrackWrapper host) {
		super.setHost(host);

		// and clear offsets
		clearChildOffsets();
	}

	/**
	 * set the data frequency (in seconds) for the track & sensor data
	 *
	 * @param theVal frequency to use
	 */
	@FireExtended
	public final void setResampleDataAt(final HiResDate theVal) {
		this._lastDataFrequency = theVal;

		// have a go at trimming the start time to a whole number of intervals
		final long interval = theVal.getMicros();

		// do we have a start time (we may just be being tested...)
		if (this.getStartDTG() == null) {
			return;
		}

		final long currentStart = this.getStartDTG().getMicros();

		// determine when the resampling should start
		final long startTime;
		if (interval > 0) {
			long tmpStartTime = (currentStart / interval) * interval;

			// just check we're in the range
			if (tmpStartTime < currentStart)
				tmpStartTime += interval;

			startTime = tmpStartTime;
		} else {
			startTime = currentStart;
		}

		// just check it's not a barking frequency
		if (theVal.getDate().getTime() <= 0) {
			// ignore, we don't need to do anything for a zero or a -1
		} else {
			decimate(theVal, startTime);
		}
	}

	public void setSensorOffset(final WorldDistance.ArrayLength sensorOffset) {
		if (sensorOffset != null && !sensorOffset.equals(_sensorOffset)) {
			_sensorOffset = sensorOffset;

			if (_sensorOffset != null) {
				clearChildOffsets();
			}

			// ok, fire the property change
			firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, sensorOffset);
		}
	}

	/**
	 * Set the array centre mode to worm in hole.
	 *
	 * @param wormInHole boolean yes/no
	 * @deprecated we no long use this boolean mode. We now allow custom array modes
	 *             - so please use {@link #setArrayCentreMode(String)}.
	 */
	@Deprecated
	public void setWormInHole(final Boolean wormInHole) {
		// sort out the new mode
		final ArrayCentreMode mode = wormInHole ? LegacyArrayOffsetModes.WORM : LegacyArrayOffsetModes.PLAIN;

		setArrayCentreMode(mode);
	}

	@Override
	public boolean supportsAddRemove() {
		return true;
	}

	/**
	 */

	@Override
	public final String toString() {
		return "Sensor:" + getName() + " (" + _myContacts.size() + " cuts)";
	}

}
