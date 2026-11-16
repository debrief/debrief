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

package Debrief.Tools.FilterOperations;

import static Debrief.Tools.FilterOperations.ReformatFixes.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Tools.FilterOperations.ReformatFixes.*;
import java.awt.Color;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JOptionPane;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.ColorPropertyEditor;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

/////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this public class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public final class ReformatFixesTest {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";


		/**
		 * Get a list of objects used for testing
		 *
		 * @param num the number of objects to return
		 * @return a list of sample objects
		 */
		private Vector<WatchableList> getTestObjects(final int num) {
			final Vector<WatchableList> res = new Vector<WatchableList>();

			// ok, start with a track
			final Debrief.Wrappers.TrackWrapper tw = new Debrief.Wrappers.TrackWrapper();
			tw.setColor(DebriefColors.RED);
			res.add(tw);

			if (num < 2)
				return res;

			// and another...
			final Debrief.Wrappers.TrackWrapper tw2 = new Debrief.Wrappers.TrackWrapper();
			tw2.setColor(MWC.GUI.Properties.DebriefColors.RED);
			tw2.setTrackColor(MWC.GUI.Properties.DebriefColors.GREEN);
			res.add(tw2);

			if (num < 3)
				return res;

			// and a shape
			final MWC.GenericData.WorldLocation theLocation = new MWC.GenericData.WorldLocation(0, 1, 1d);
			final MWC.GUI.Shapes.PlainShape theShape = new MWC.GUI.Shapes.CircleShape(theLocation, 0.03d);
			final Debrief.Wrappers.ShapeWrapper sw = new Debrief.Wrappers.ShapeWrapper("bingo", theShape,
					MWC.GUI.Properties.DebriefColors.RED, null);
			res.add(sw);

			if (num < 4)
				return res;

			// and a label
			final Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("here", theLocation,
					MWC.GUI.Properties.DebriefColors.RED);
			res.add(lw);

			if (num < 5)
				return res;

			// and collate the list

			return res;
		}

		// TODO FIX-TEST
		public void NtestGetProperties() {
			// System.out.println("test 2");
			final Debrief.Wrappers.TrackWrapper tw = new Debrief.Wrappers.TrackWrapper();
			tw.setColor(DebriefColors.RED);
			tw.setName("scrap track");

			final Editable.EditorType et = tw.getInfo();
			// check we got it
			assertNotNull(et, "found editable data");

			// now check we can get the suitable properties
			final HashMap<String, SetterHolder> props = getSuitablePropertiesFor(tw,
					ReformatFixes.getPropertyManager());

			// so, how many are there?
			final Collection<String> keys = props.keySet();

			// did we get the correct number
//      for(Iterator iterator = keys.iterator(); iterator.hasNext();)
//      {
//        String s = (String) iterator.next();
//        System.out.println("this key:" + s);
//      }

			assertEquals(9, keys.size(), "found correct number of properties for track");

			// see if we've got a property we expect
			assertTrue(keys.contains("Color"), "found get color");

			// check we haven't got one we don't want
			assertTrue(!keys.contains("TrackFont"), "not found get track font");

		}

		@Test
		public void testCollateProperties() {

			// System.out.println("test 1");
			// produce the objects to test
			final Vector<WatchableList> theObjects2 = getTestObjects(5);

			// now find the common list of properties
			final HashMap<String, Vector<SetterHolder>> res = ReformatFixes.getCommonProperties(theObjects2);

			// check we have our two items
			assertEquals(3, res.keySet().size(), "found right number of properties");

			// get the first list
			final Vector<SetterHolder> firstList = res.get(res.keySet().iterator().next());
			assertEquals(4, firstList.size(), "have editors for correct number of properties");
		}

		@Test
		public void testIntersect() {
			// System.out.println("test 3");
			final TreeSet<String> t1 = new TreeSet<String>();
			t1.add("a");
			t1.add("b");
			t1.add("c");
			t1.add("d");
			t1.add("e");

			final TreeSet<String> t2 = new TreeSet<String>();
			t2.add("c");
			t2.add("d");
			t2.add("e");
			t2.add("f");

			t1.retainAll(t2);
			t2.retainAll(t1);

			// check we've got the ones we're after
			assertTrue(t2.contains("c"), "found it");
			assertTrue(t2.contains("d"), "found it");
			assertTrue(t2.contains("e"), "found it");
		}

		@Test
		public void testMatchingAnnotations() {
			final LabelWrapper lw = new LabelWrapper("first label", null, MWC.GUI.Properties.DebriefColors.BLUE);

			final WorldLocation loc = new WorldLocation(0d, 0d, 0d);
			final PlainShape ps = new CircleShape(loc, 12d);
			final ShapeWrapper sw = new ShapeWrapper("first shape", ps, MWC.GUI.Properties.DebriefColors.BLUE, null);

			final TrackWrapper tw = new TrackWrapper();
			tw.setColor(MWC.GUI.Properties.DebriefColors.BLUE);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(5, 0), loc, 0d, 0d));
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(7, 0), loc, 0d, 0d));
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(9, 0), loc, 0d, 0d));
			fw1.setLabelShowing(true);
			fw1.setVisible(true);
			fw2.setVisible(true);
			fw3.setVisible(true);
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			fw1.setTrackWrapper(tw);
			fw2.setTrackWrapper(tw);
			fw3.setTrackWrapper(tw);
			fw1.setColor(MWC.GUI.Properties.DebriefColors.BLUE);
			fw2.setColor(MWC.GUI.Properties.DebriefColors.BLUE);
			fw3.setColor(MWC.GUI.Properties.DebriefColors.BLUE);

			// check they're visible
			assertTrue(lw.getVisible(), "label is visible");
			assertTrue(sw.getVisible(), "shape is visible");
			assertTrue(tw.getVisible(), "track wrapper is visible");

			assertEquals(MWC.GUI.Properties.DebriefColors.BLUE, tw.getColor(), "track is blue");
			assertEquals(MWC.GUI.Properties.DebriefColors.BLUE, lw.getColor(), "label is blue");
			assertEquals(MWC.GUI.Properties.DebriefColors.BLUE, sw.getColor(), "shape is blue");

			final Vector<WatchableList> theTracks = new Vector<WatchableList>(0, 1);
			theTracks.add(lw);
			theTracks.add(sw);
			theTracks.add(tw);

			final Layers ly = new Layers();
			final ReformatFixes rf = new ReformatFixes(ly);

			// find the list of properties to be edited
			final HashMap<String, Vector<SetterHolder>> commonProps = ReformatFixes.getCommonProperties(theTracks);

			// get the color setter
			final Vector<SetterHolder> theSetters = commonProps.get("Color");

			// find out what types of position to use
			String position_type = ALL_ITEMS;

			// the value selected by the user
			Object newVal = null;

			final SetterHolder firstEditor = theSetters.firstElement();
			final PropertyEditor pe = firstEditor._propertyEditor;
			final String tags[] = pe.getTags();
			if (tags != null) {
				Object res;
				pe.setAsText(tags[0]);
				res = pe.getValue();
				newVal = res;
			}

			// set the start/end times
			rf._start_time = new HiResDate(5);
			rf._end_time = new HiResDate(6);

			// ok, the user data is collated, lets do it!
			ReformatAction res = rf.performTheOperation(firstEditor, newVal, theSetters, position_type);

			assertNotNull(res, "created action item");

			// execute it
			res.execute();

			// check if colours updates
			final Color newRed = DebriefColors.RED;
			assertEquals(newRed, fw1.getColor(), "track changed to red");
			assertEquals(newRed, sw.getColor(), "shape changed to red");
			assertEquals(newRed, lw.getColor(), "label changed to red");

			// and check the undo
			res.undo();

			// check if colours updates
			assertEquals(MWC.GUI.Properties.DebriefColors.BLUE, fw1.getColor(), "track changed to red");
			assertEquals(MWC.GUI.Properties.DebriefColors.BLUE, sw.getColor(), "shape changed to red");
			assertEquals(MWC.GUI.Properties.DebriefColors.BLUE, lw.getColor(), "label changed to red");

			newVal = MWC.GUI.Properties.DebriefColors.YELLOW;
			position_type = ReformatFixes.ONLY_VISIBLE;

			// ok, the user data is collated, lets do it!
			res = rf.performTheOperation(firstEditor, newVal, theSetters, position_type);

			assertNotNull(res, "created action item");

			// execute it
			res.execute();

			// check if colours updates
			assertEquals(MWC.GUI.Properties.DebriefColors.YELLOW, fw1.getColor(), "fix 1 changed to yellow");
			assertEquals(MWC.GUI.Properties.DebriefColors.BLUE, fw2.getColor(), "fix 2 not changed to yellow");
			assertEquals(MWC.GUI.Properties.DebriefColors.YELLOW, sw.getColor(), "shape changed to yellow");
			assertEquals(MWC.GUI.Properties.DebriefColors.YELLOW, lw.getColor(), "label changed to yellow");

		}
	}
