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

package ASSET.Scenario.Observers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.Track;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 24-Jul-2003 Time: 12:47:01 To
 * change this template use Options | File Templates.
 */
public class WriteToCloudObserver extends RecordToFileObserverType implements ASSET.Scenario.ScenarioSteppedListener {

	// ////////////////////////////////////////////////
	// editable properties
	// ////////////////////////////////////////////////
	static public class TrackPlotObserverInfo extends EditorType {

		/**
		 * constructor for editable details
		 *
		 * @param data the object we're going to edit
		 */
		public TrackPlotObserverInfo(final WriteToCloudObserver data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this observer"),
						prop("OnlyFinalPositions", "whether to only show final positions"),

				};
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class TrackPlotObsTest extends SupportTesting.EditableTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public TrackPlotObsTest(final String val) {
			super(val);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			final WriteToCloudObserver tpo = new WriteToCloudObserver("a", "test observer", true);
			return tpo;
		}

		// TODO FIX-TEST
		public void NtestWrite() {

			final String directoryName = "./test_reports/";
			final String fileName = "res.png";

			final WriteToCloudObserver tpo = new WriteToCloudObserver(directoryName, "test observer", true);
			final CoreScenario cs = new CoreScenario();
			tpo.setup(cs);

			WorldLocation loc = new WorldLocation(1, 2, 3);
			final Status stat = new Status(12, 0);
			stat.setCourse(21);
			stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

			ParticipantType ssn = new CoreParticipant(12);
			ssn.setName("Bingo");
			ssn.setCategory(
					new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

			tpo.processTheseDetails(loc, stat, ssn);

			// move location
			loc = new WorldLocation(loc.add(new WorldVector(0.101, 0.01, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			loc = new WorldLocation(loc.add(new WorldVector(0.201, 0.05, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			loc = new WorldLocation(loc.add(new WorldVector(0.101, 0.03, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			loc = new WorldLocation(loc.add(new WorldVector(0.011, 0.05, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			ssn = new CoreParticipant(14);
			ssn.setName("Spooner");
			ssn.setCategory(new Category(Category.Force.RED, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

			loc = new WorldLocation(loc.add(new WorldVector(2.701, 0.12, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			// move location
			loc = new WorldLocation(loc.add(new WorldVector(2.201, 0.12, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			loc = new WorldLocation(loc.add(new WorldVector(2.101, 0.05, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			loc = new WorldLocation(loc.add(new WorldVector(1.301, 0.02, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			loc = new WorldLocation(loc.add(new WorldVector(2.031, 0.04, 1)));
			tpo.processTheseDetails(loc, stat, ssn);

			tpo.tearDown(cs);

			// check file exists
			final File file = new File(directoryName + fileName);
			assertTrue("file got created", file.exists());
			System.out.println("file size is:" + file.length());
			assertEquals("file is of correct size", 7540, file.length(), 400);
		}
	}

	/**
	 * build up the tracks - so we can output them at the end (indexed by the
	 * participant
	 */
	private HashMap<ParticipantType, Track> _myTracks = null;

	/**
	 * keep track of the area covered by each track
	 *
	 */
	private HashMap<ParticipantType, WorldArea> _mySpatialCoverages = null;

	/**********************************************************************
	 * scenario mangaement
	 *********************************************************************/

	/**
	 * keep track of the time period covered by each track
	 *
	 */
	private HashMap<ParticipantType, TimePeriod> _myTemporalCoverages = null;

	/***************************************************************
	 * constructor
	 ***************************************************************/
	/**
	 * create a new monitor
	 *
	 * @param directoryName the directory to output the plots to
	 * @param fileName      file name to use for results plot (or null to
	 *                      auto-generate one)
	 * @param isActive      the separation to use for the grid lines - or null for
	 *                      no grid
	 */
	public WriteToCloudObserver(final String directoryName, final String fileName, final boolean isActive) {
		super(directoryName, fileName, fileName, isActive);
	}

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		_myScenario.addScenarioSteppedListener(this);
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType createEditor() {
		return new TrackPlotObserverInfo(this);
	}

	/**
	 * determine the normal suffix for this file type
	 */
	@Override
	protected String getMySuffix() {
		return "png";
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	protected String newName(final String scenario_name) {
		String res;
		if (getFileName() == null) {
			res = "res_" + scenario_name + "_"
					+ MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System.currentTimeMillis()) + ".png";
		} else
			res = getFileName();

		return res;

	}

	private void outputThisTrack(final Track theTrack, final String scenarioName) {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode root = mapper.createObjectNode();
		final ObjectNode metadata = mapper.createObjectNode();
		root.put("metadata", metadata);

		// sort out the metadata
		metadata.put("Platform", theTrack.getName());
		metadata.put("PlatformType", theTrack.getName());
		metadata.put("Sensor", theTrack.getName());

		// sort out the data points
		// and the data arrays
		final ArrayNode lat = mapper.createArrayNode();
		final ArrayNode lon = mapper.createArrayNode();
		final ArrayNode time = mapper.createArrayNode();
		final ArrayNode altitude = mapper.createArrayNode();

		int ctr = 0;

		final Enumeration<Fix> enumer = theTrack.getFixes();
		while (enumer.hasMoreElements() && ctr++ < 5) {
			final Fix fix = enumer.nextElement();

			lat.add(fix.getLocation().getLat());
			lon.add(fix.getLocation().getLong());
			time.add(fix.getTime().getDate().toString());
			altitude.add(-fix.getLocation().getDepth());

		}

		root.put("lat", lat);
		root.put("lon", lon);
		root.put("depth", altitude);
		root.put("time", time);

		try {
			final StringWriter sw = new StringWriter();
			mapper.writeValue(sw, root);
			// now send to the cloud
			System.out.println("here:" + sw.toString());
		} catch (final JsonGenerationException e) {
			e.printStackTrace();
		} catch (final JsonMappingException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * right, export our tracks
	 *
	 * @param scenario the scenario the tracks are from
	 *
	 */
	private void outputTracks(final String scenario) {
		// step through our tracks
		for (final Iterator<ParticipantType> thisTrack = _myTracks.keySet().iterator(); thisTrack.hasNext();) {
			// get the next participant
			final ParticipantType cp = thisTrack.next();

			// retrieve its track
			final Track track = _myTracks.get(cp);

			outputThisTrack(track, scenario);

		}

	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 *
	 * @param scenario the scenario we're closing from
	 */
	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {
		// do we have any data?
		if (_myTracks == null) {
			System.err.println("NO TRACKS FOR TRACK PLOT OBSERVER");
			return;
		}

		if (_myTracks.size() > 0) {
			outputTracks(scenario.getName());
		} // whether we had any tracks

		// clear the coverages
		_mySpatialCoverages = null;
		_myTemporalCoverages = null;
		_myTracks = null;
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {

		// are we holding any tracks?
		if (_myTracks != null) {
			_myTracks.clear();
			_myTracks = null;
		}

		// and create a new holder
		_myTracks = new HashMap<ParticipantType, Track>();
		_mySpatialCoverages = new HashMap<ParticipantType, WorldArea>();
		_myTemporalCoverages = new HashMap<ParticipantType, TimePeriod>();
	}

	// ////////////////////////////////////////////////
	// property editing
	// ////////////////////////////////////////////////

	/**
	 * store the data for this time step
	 *
	 * @param loc  the current location of this participant
	 * @param stat the status of this participant
	 * @param pt   this participant
	 */
	public void processTheseDetails(final WorldLocation loc, final Status stat, final ParticipantType pt) {
		// ok, now output these details in our special format
		writeTheseDetails(loc, stat, pt);
	}

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		_myScenario.removeScenarioSteppedListener(this);
	}

	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {
		if (!isActive())
			return;

		// get the positions of the participants
		final Integer[] lst = _myScenario.getListOfParticipants();
		for (int thisIndex = 0; thisIndex < lst.length; thisIndex++) {
			final Integer integer = lst[thisIndex];
			if (integer != null) {
				final ParticipantType pt = _myScenario.getThisParticipant(integer.intValue());
				final Status stat = pt.getStatus();
				final WorldLocation loc = stat.getLocation();

				// and store the data
				processTheseDetails(loc, stat, pt);

			}
		}

	}

	/**
	 * write this set of details to file
	 *
	 * @param loc  the current location
	 * @param stat the current status
	 * @param pt   the participant in question
	 */
	protected void writeTheseDetails(final WorldLocation loc, final Status stat, final ParticipantType pt) {
		// we may not even have any tracks. just check
		if (_myTracks == null)
			return;

		// do we hold this participant
		Track trk = _myTracks.get(pt);

		// did we find it?
		if (trk == null) {
			trk = new Track();
			trk.setName(pt.getName());
			_myTracks.put(pt, trk);

			final HiResDate dt = new HiResDate(stat.getTime());
			final HiResDate dt2 = new HiResDate(dt);
			_myTemporalCoverages.put(pt, new TimePeriod.BaseTimePeriod(dt, dt2));
		}

		if (_mySpatialCoverages.get(pt) == null) {
			if (stat.getLocation() != null)
				_mySpatialCoverages.put(pt, new WorldArea(stat.getLocation(), stat.getLocation()));
		} else
			_mySpatialCoverages.get(pt).extend(pt.getStatus().getLocation());

		_myTemporalCoverages.get(pt).extend(new HiResDate(stat.getTime()));

		if (_mySpatialCoverages == null) {

		}

		// create the fix
		final HiResDate hrd = new HiResDate(stat.getTime(), 0);
		final Fix fix = new Fix(hrd, loc, stat.getCourse(), stat.getSpeed().getValueIn(WorldSpeed.Kts));

		// and add it
		trk.addFix(fix);
	}

}
