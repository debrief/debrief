package ASSET.Scenario.Observers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

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
public class WriteToCloudObserver extends RecordToFileObserverType implements
		ASSET.Scenario.ScenarioSteppedListener
{

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
	 * @param directoryName
	 *          the directory to output the plots to
	 * @param fileName
	 *          file name to use for results plot (or null to auto-generate one)
	 * @param isActive
	 *          the separation to use for the grid lines - or null for no grid
	 */
	public WriteToCloudObserver(final String directoryName,
			final String fileName, final boolean isActive)
	{
		super(directoryName, fileName, fileName, isActive);
	}

	/**********************************************************************
	 * scenario mangaement
	 *********************************************************************/

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 * 
	 * @param scenario
	 *          the new scenario we're looking at
	 */
	protected void performSetupProcessing(ScenarioType scenario)
	{

		// are we holding any tracks?
		if (_myTracks != null)
		{
			_myTracks.clear();
			_myTracks = null;
		}

		// and create a new holder
		_myTracks = new HashMap<ParticipantType, Track>();
		_mySpatialCoverages = new HashMap<ParticipantType, WorldArea>();
		_myTemporalCoverages = new HashMap<ParticipantType, TimePeriod>();
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 * 
	 * @param scenario
	 *          the scenario we're closing from
	 */
	protected void performCloseProcessing(ScenarioType scenario)
	{
//		 do we have any data?
		if (_myTracks == null)
		{
			System.err.println("NO TRACKS FOR TRACK PLOT OBSERVER");
			return;
		}

		if (_myTracks.size() > 0)
		{
			outputTracks(scenario.getName());
		} // whether we had any tracks

		// clear the coverages
		_mySpatialCoverages = null;
		_myTemporalCoverages = null;
		_myTracks = null;
	}

	/**
	 * add any applicable listeners
	 */
	protected void addListeners(ScenarioType scenario)
	{
		_myScenario.addScenarioSteppedListener(this);
	}

	/**
	 * remove any listeners
	 */
	protected void removeListeners(ScenarioType scenario)
	{
		_myScenario.removeScenarioSteppedListener(this);
	}

	protected String newName(String scenario_name)
	{
		String res;
		if (getFileName() == null)
		{
			res = "res_"
					+ scenario_name
					+ "_"
					+ MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System
							.currentTimeMillis()) + ".png";
		}
		else
			res = getFileName();

		return res;

	}

	/**
	 * determine the normal suffix for this file type
	 */
	protected String getMySuffix()
	{
		return "png";
	}

	/**
	 * right, export our tracks
	 * @param scenario  the scenario the tracks are from
	 * 
	 */
	private void outputTracks(String scenario)
	{
		// step through our tracks
		for (Iterator<ParticipantType> thisTrack = _myTracks.keySet().iterator(); thisTrack
				.hasNext();)
		{
			// get the next participant
			CoreParticipant cp = (CoreParticipant) thisTrack.next();
			
			// retrieve its track
			Track track = (Track) _myTracks.get(cp);
			
			outputThisTrack(track, scenario);
			
		}

	}
	
	private void outputThisTrack(Track theTrack, String scenarioName)
	{
		// create the document
		
		// sort out the metadata
		
		// sort out the data points
		
		Enumeration<Fix> enumer = theTrack.getFixes();
		while (enumer.hasMoreElements())
		{
			@SuppressWarnings("unused")
			Fix fix = (Fix) enumer.nextElement();
		}
		
		// now send to the cloud
		
		
	}
	

	/**
	 * the scenario has stepped forward
	 */
	public void step(ScenarioType scenario, long newTime)
	{
		if (!isActive())
			return;

		// get the positions of the participants
		final Integer[] lst = _myScenario.getListOfParticipants();
		for (int thisIndex = 0; thisIndex < lst.length; thisIndex++)
		{
			final Integer integer = lst[thisIndex];
			if (integer != null)
			{
				final ParticipantType pt = _myScenario.getThisParticipant(integer
						.intValue());
				final Status stat = pt.getStatus();
				final WorldLocation loc = stat.getLocation();

				// and store the data
				processTheseDetails(loc, stat, pt);

			}
		}

	}

	/**
	 * store the data for this time step
	 * 
	 * @param loc
	 *          the current location of this participant
	 * @param stat
	 *          the status of this participant
	 * @param pt
	 *          this participant
	 */
	public void processTheseDetails(final WorldLocation loc, final Status stat,
			final ParticipantType pt)
	{
		// ok, now output these details in our special format
		writeTheseDetails(loc, stat, pt);
	}

	/**
	 * write this set of details to file
	 * 
	 * @param loc
	 *          the current location
	 * @param stat
	 *          the current status
	 * @param pt
	 *          the participant in question
	 */
	protected void writeTheseDetails(WorldLocation loc, Status stat,
			ParticipantType pt)
	{
		// we may not even have any tracks. just check
		if (_myTracks == null)
			return;

		// do we hold this participant
		Track trk = (Track) _myTracks.get(pt);

		// did we find it?
		if (trk == null)
		{
			trk = new Track();
			trk.setName(pt.getName());
			_myTracks.put(pt, trk);

			_mySpatialCoverages.put(pt, new WorldArea(pt.getStatus().getLocation(),
					pt.getStatus().getLocation()));
			HiResDate dt = new HiResDate(stat.getTime());
			HiResDate dt2 = new HiResDate(dt);
			_myTemporalCoverages.put(pt, new TimePeriod.BaseTimePeriod(dt, dt2));
		}
		else
		{
			_mySpatialCoverages.get(pt).extend(pt.getStatus().getLocation());
			_myTemporalCoverages.get(pt).extend(new HiResDate(stat.getTime()));
		}

		// create the fix
		HiResDate hrd = new HiResDate(stat.getTime(), 0);
		Fix fix = new Fix(hrd, loc, stat.getCourse(), stat.getSpeed().getValueIn(
				WorldSpeed.Kts));

		// and add it
		trk.addFix(fix);
	}

	// ////////////////////////////////////////////////
	// property editing
	// ////////////////////////////////////////////////

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public boolean hasEditor()
	{
		return true;
	}

	/**
	 * get the editor for this item
	 * 
	 * @return the BeanInfo data for this editable object
	 */
	public EditorType createEditor()
	{
		return new TrackPlotObserverInfo(this);
	}

	// ////////////////////////////////////////////////
	// editable properties
	// ////////////////////////////////////////////////
	static public class TrackPlotObserverInfo extends EditorType
	{

		/**
		 * constructor for editable details
		 * 
		 * @param data
		 *          the object we're going to edit
		 */
		public TrackPlotObserverInfo(final WriteToCloudObserver data)
		{
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 * 
		 * @return property descriptions
		 */
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Name", "the name of this observer"),
						prop("OnlyFinalPositions", "whether to only show final positions"),

				};
				return res;
			}
			catch (IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class TrackPlotObsTest extends SupportTesting.EditableTesting
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public TrackPlotObsTest(final String val)
		{
			super(val);
		}

		/**
		 * get an object which we can test
		 * 
		 * @return Editable object which we can check the properties for
		 */
		public Editable getEditable()
		{
			WriteToCloudObserver tpo = new WriteToCloudObserver("a", "test observer",
					true);
			return tpo;
		}

		public void testWrite()
		{

			final String directoryName = "./test_reports/";
			final String fileName = "res.png";

			WriteToCloudObserver tpo = new WriteToCloudObserver(directoryName,
					"test observer", true);
			CoreScenario cs = new CoreScenario();
			tpo.setup(cs);

			WorldLocation loc = new WorldLocation(1, 2, 3);
			Status stat = new Status(12, 0);
			stat.setCourse(21);
			stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

			CoreParticipant ssn = new CoreParticipant(12);
			ssn.setName("Bingo");
			ssn.setCategory(new Category(Category.Force.BLUE,
					Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

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
			ssn.setCategory(new Category(Category.Force.RED,
					Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));

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
			File file = new File(directoryName + fileName);
			assertTrue("file got created", file.exists());
			System.out.println("file size is:" + file.length());
			assertEquals("file is of correct size", (float) 7540,
					(float) file.length(), 400);
		}
	}

}
