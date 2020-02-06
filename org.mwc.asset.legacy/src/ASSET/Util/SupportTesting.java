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

package ASSET.Util;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.GUI.Core.CoreGUISwing;
import ASSET.Models.Decision.TargetType;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Scenario.Observers.Recording.CSVTrackObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;

/**
 * ******************************************************************* utility
 * class providing testing
 * *******************************************************************
 */
public class SupportTesting extends junit.framework.TestCase {

	//////////////////////////////////////////////////
	// add property editing testing
	//////////////////////////////////////////////////
	abstract public static class EditableTesting extends SupportTesting {
		public EditableTesting() {
			super("Testing editable properties");
		}

		public EditableTesting(final String name) {
			super(name);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		abstract public Editable getEditable();

		/**
		 * run through tests of the editable properties
		 */
		// TODO FIX-TEST
		public final void NtestMyParams() {
			// just check that our ASSET-specific editors are loaded
			final PropertyEditor pe = PropertyEditorManager.findEditor(TargetType.class);
			if (pe == null)
				CoreGUISwing.registerEditors();

			// ok, get on with it
			final Editable toBeTested = getEditable();
			Editable.editableTesterSupport.testTheseParameters(toBeTested);
		}

	}

	static final String TEST_DIR = "./test_reports/";

	/**
	 * a destination for writing our narrative
	 */
	private static java.io.FileWriter _fo = null;

	public static void callTestMethods(final SupportTesting tt) {
		// find and run all methods beginning with test
		final Method[] methods = tt.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			final Method thisMethod = methods[i];
			if (thisMethod.getName().startsWith("test")) {
				final Object params[] = {};
				try {
					thisMethod.invoke(tt, params);
					System.out.println("called:" + thisMethod.getName());
				} catch (final IllegalAccessException e) {
					e.printStackTrace(); // To change body of catch statement use Options | File Templates.
				} catch (final IllegalArgumentException e) {
					e.printStackTrace(); // To change body of catch statement use Options | File Templates.
				} catch (final InvocationTargetException e) {
					e.printStackTrace(); // To change body of catch statement use Options | File Templates.
				}
			}
		}
	}

	/**
	 * quickly create a test location, using metre coordinates
	 *
	 * @param x_m longitude in metres
	 * @param y_m latitude in metres
	 * @return the new location
	 */
	public static WorldLocation createLocation(final double x_m, final double y_m) {
		return new WorldLocation(MWC.Algorithms.Conversions.m2Degs(y_m), MWC.Algorithms.Conversions.m2Degs(x_m), 0);
	}

	/**
	 * create a random location within the indicated area
	 *
	 * @param bounding_area the area to create a location within
	 * @return the new location
	 */
	public static WorldLocation createLocation(final WorldArea bounding_area) {
		final double theLat = bounding_area.getBottomLeft().getLat();
		final double theLong = bounding_area.getBottomLeft().getLong();

		final double theLatDelta = ASSET.Util.RandomGenerator.nextRandom()
				* (bounding_area.getTopRight().getLat() - theLat);
		final double theLongDelta = ASSET.Util.RandomGenerator.nextRandom()
				* (bounding_area.getTopRight().getLong() - theLong);

		return new WorldLocation(theLat + theLatDelta, theLong + theLongDelta, 0);
	}

	/**
	 * create a location using user-configurable units
	 *
	 * @param latVal
	 * @param longVal
	 * @return
	 */
	public static WorldLocation createLocation(final WorldDistance latVal, final WorldDistance longVal) {
		return new WorldLocation(latVal.getValueIn(WorldDistance.DEGS), longVal.getValueIn(WorldDistance.DEGS), 0);
	}

	public static void outputLocation(final WorldLocation loc) {
		if (loc != null) {
			final String res = toXYString(loc);
			System.out.print(res);
		}
	}

	public static void outputThis(final Document theDoc, final String title) {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		final javax.xml.transform.TransformerFactory factory = TransformerFactory.newInstance();

		try {
			final javax.xml.transform.Transformer transformer = factory.newTransformer();

			final StreamResult sr = new StreamResult(bos);

			transformer.transform(new DOMSource(theDoc), sr);

			bos.close();
		} catch (final TransformerException e) {
			e.printStackTrace(); // To change body of catch statement use Options | File Templates.
		} catch (final IOException e) {
			e.printStackTrace(); // To change body of catch statement use Options | File Templates.
		}

		System.out.println("=======" + title + "================");
		System.out.println(bos.toString());
		System.out.println("=====================");
	}

	/**
	 * utility testing method to write a line of text to the narrative file (if one
	 * has been setup)
	 *
	 * @param msg    the message to store
	 * @param trk    the track this message relates to
	 * @param dtg    the time of the message
	 * @param source
	 */
	public static void recordThis(final String msg, final String trk, final long dtg, final Object source) {
		try {
			if (_fo != null) {
				_fo.write(";NARRATIVE: " + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(dtg) + " " + trk
						+ " " + msg + " (" + source.toString() + ")");
				_fo.write(System.getProperty("line.separator"));
				_fo.flush();
			}
		} catch (final IOException e) {
			e.printStackTrace(); // To change body of catch statement use File | Settings | File
									// Templates.
		}

	}

	/**
	 * get ready to record a narrative
	 *
	 * @param fileName the file to record the narrative to
	 */
	public static void setupNarrative(final String fileName) {
		if (_fo == null) {
			try {
				_fo = new FileWriter(fileName);
			} catch (final IOException e) {
				e.printStackTrace(); // To change body of catch statement use File | Settings | File
										// Templates.
			}
		}
	}

	public static void stopNarrative() {
		if (_fo != null) {
			try {
				_fo.flush();
				_fo.close();
			} catch (final IOException e) {
				e.printStackTrace(); // To change body of catch statement use File | Settings | File
										// Templates.
			}
		}
	}

	public static String toXYString(final WorldLocation loc) {
		String res = " x," + (int) MWC.Algorithms.Conversions.Degs2m(loc.getLong());
		res += ",y," + (int) MWC.Algorithms.Conversions.Degs2m(loc.getLat());
		return res;
	}

	/**
	 * our track plot observer, if we have one
	 */
	protected TrackPlotObserver _tpo;

	/**
	 * the list of participants we're listening to
	 */
	protected HashMap<CoreParticipant, ParticipantMovedListener> _listeningList;

	/**
	 * our debrief plot observer, if we have one
	 */
	protected DebriefReplayObserver _dro;

	/**
	 * our csv track observer, if we have one
	 */
	private CSVTrackObserver _cvo;

	/**
	 * constructor - takes the name of this set of tests
	 *
	 * @param s
	 */
	public SupportTesting(final String s) {
		super(s);
	}

	/**
	 * tidy things up, close files
	 *
	 * @param theScenario
	 */
	protected void endRecording(final ScenarioType theScenario) {
		// stop listening to the participants
		if (_listeningList != null) {
			for (final Iterator<CoreParticipant> iterator = _listeningList.keySet().iterator(); iterator.hasNext();) {
				final ParticipantType coreParticipant = iterator.next();
				final ParticipantMovedListener pml = _listeningList.get(coreParticipant);
				coreParticipant.removeParticipantMovedListener(pml);
			}

			// now clear it
			_listeningList.clear();
			_listeningList = null;
		}

		if (_tpo != null) {
			_tpo.tearDown(theScenario);
			_tpo = null;
		}

		if (_dro != null) {
			_dro.tearDown(theScenario);
			_dro = null;
		}
		if (_cvo != null) {
			_cvo.tearDown(theScenario);
			_cvo = null;
		}
	}

	////////////////////////////////////////////////////////////
	// narrative support
	////////////////////////////////////////////////////////////

	/**
	 * output this series of destinations to a file, in replay format
	 *
	 * @param fileName
	 * @param destinations
	 */
	public void outputTheseToRep(final String fileName, final WorldPath destinations) {
		try {
			final FileWriter writer = new FileWriter(TEST_DIR + fileName);

			final Collection<WorldLocation> points = destinations.getPoints();
			int counter = 1;
			for (final Iterator<WorldLocation> iterator = points.iterator(); iterator.hasNext();) {
				final WorldLocation worldLocation = iterator.next();
				String thisLine = ";CIRCLE: @@ ";
				thisLine += MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(worldLocation);
				thisLine += " 50  pt_" + counter++ + System.getProperty("line.separator");
				writer.write(thisLine);
			}

			writer.close();
		} catch (final IOException e) {
			e.printStackTrace(); // To change body of catch statement use Options | File Templates.
		}

	}

	protected void outputThisDocument(final Document theDoc, final String title) {
		outputThis(theDoc, title);
	}

	/**
	 * record this status snapshot
	 *
	 * @param stat current status
	 * @param part participant we're looking at
	 */
	protected void recordThis(final Status stat, final ParticipantType part, final long newTime) {
		if (_tpo != null)
			_tpo.processTheseDetails(stat.getLocation(), stat, part);

		if (_dro != null) {
			_dro.writeThesePositionDetails(stat.getLocation(), stat, part, newTime);
		}
		if (_cvo != null) {
			_cvo.writeThesePositionDetails(stat.getLocation(), stat, part, newTime);
		}
	}

	/**
	 * start listening to this particular participant
	 */
	protected void startListeningTo(final CoreParticipant cp, final String name, final boolean doPlot,
			final boolean doRep, final boolean doCSV, final ScenarioType theScenario) {
		// are we up and running?
		if ((_dro == null) && (_tpo == null) && (_cvo == null)) {
			// no, start recording
			this.startRecording(name, doPlot, doRep, doCSV, theScenario);
		}

		final ParticipantMovedListener pml = new ParticipantMovedListener() {
			@Override
			public void moved(final Status newStatus) {
				recordThis(newStatus, cp, newStatus.getTime());
			}

			@Override
			public void restart(final ScenarioType scenario) {
			}
		};

		// now listen to it
		cp.addParticipantMovedListener(pml);

		// and remember it
		if (_listeningList == null)
			_listeningList = new HashMap<CoreParticipant, ParticipantMovedListener>();

		_listeningList.put(cp, pml);
	}

	////////////////////////////////////////////////////////////
	// auto-test support
	////////////////////////////////////////////////////////////

	/**
	 * set up to start recording
	 *
	 * @param name        the name to prefix any files
	 * @param doPlot      whether to produce a plot
	 * @param doREP       whether to produce a replay file
	 * @param theScenario
	 */
	protected void startRecording(final String name, final boolean doPlot, final boolean doREP, final boolean doCSV,
			final ScenarioType theScenario) {
		if (doPlot) {
			_tpo = new TrackPlotObserver(TEST_DIR, 600, 600, name + ".png", null, true, true, false, "track plot",
					true);
			_tpo.setup(theScenario);
		}

		if (doREP) {
			_dro = new DebriefReplayObserver(TEST_DIR, name + ".rep", false, false, true, null, "debrief plot", true,
					null);
			_dro.setup(theScenario);
		}

		if (doCSV) {
			_cvo = new CSVTrackObserver(TEST_DIR, name + ".csv", false, false, true, null, "CSV track", true);
			_cvo.setup(theScenario);
		}
	}

	/**
	 * dummy test to make Eclipse's automated test finder work satisfactorily
	 */
	public void testDummy() {
		assertTrue("I'm not really in right now", true);
	}

}
