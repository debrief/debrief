package ASSET.Scenario.Observers.Recording;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Sensor.Initial.OpticSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.CoreObserver;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 28-Oct-2003 Time: 14:17:34 To
 * change this template use Options | File Templates.
 */
public class RecordStatusToDBObserverType extends CoreObserver implements
		ASSET.Scenario.ScenarioSteppedListener
{
	private static final String POSITION_FORMAT = "data.pos";

	private static final String NARRATIVE_FORMAT = "data.narr";

	private static final String DETECTION_FORMAT = "data.detect";

	private static final String DEV_PASSWORD = "MISSING";

	/**
	 * keep track of whether the analyst wants detections recorded
	 */
	protected boolean _recordDetections = false;

	/**
	 * keep track of whether the analyst wants decisions recorded
	 */
	private boolean _recordDecisions;

	/**
	 * keep track of whether the analyst wants positions recorded
	 */
	private boolean _recordPositions;

	/**
	 * keep track of what target the analyst wants recorded
	 */
	private TargetType _subjectToTrack;

	private Connection _conn;

	private HashMap<String, Integer> _participantIds;

	private String _datasetPrefix;

	private String _scenarioName;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * an observer which wants to record it's data to file
	 * 
	 * @param directoryName
	 *          the directory to output to
	 * @param fileName
	 *          the filename to output to
	 * @param recordDetections
	 *          whether to record detections
	 * @param recordDecisions
	 *          whether to record decisions
	 * @param recordPositions
	 *          whether to record positions
	 * @param subjectToTrack
	 *          the type of target to track (or null for all targets)
	 * @param observerName
	 *          what to call this narrative observer
	 * @param isActive
	 *          whether this observer is active
	 * @param datasetPrefix
	 */
	public RecordStatusToDBObserverType(final boolean recordDetections,
			boolean recordDecisions, final boolean recordPositions,
			final TargetType subjectToTrack, final String observerName,
			boolean isActive, String datasetPrefix)
	{
		super(observerName, isActive);

		_recordDetections = recordDetections;
		_recordDecisions = recordDecisions;
		_recordPositions = recordPositions;
		_subjectToTrack = subjectToTrack;
		_datasetPrefix = datasetPrefix;

		_participantIds = new HashMap<String, Integer>();
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	/**
	 * ok. ready to start writing. get on with it
	 * 
	 * @param title
	 *          the title of this run
	 * @param currentDTG
	 *          the current time (not model time)
	 * @throws IOException
	 */
	protected void writeFileHeaderDetails(final String title, long currentDTG)
	{

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
	protected void writeThesePositionDetails(WorldLocation loc, Status stat,
			ParticipantType pt, long newTime)
	{
		try
		{
			// see if we've loaded this participant
			Integer theIndex = _participantIds.get(pt.getName() + POSITION_FORMAT);
			PreparedStatement stP;

			if (theIndex == null)
			{
				theIndex = getDatasetIndexFor(pt.getName(), POSITION_FORMAT);
				_participantIds.put(pt.getName() + POSITION_FORMAT, theIndex);
			}

			stP = _conn
					.prepareStatement("INSERT INTO dataItems(datasetid, dtg, location, contenttype, content) VALUES" + 
							"(?, ?, ?,?,?)");
			stP.setInt(1, theIndex.intValue());
			stP.setTimestamp(2, new Timestamp(stat.getTime()));
			stP.setObject(3, createGeometry(loc));
			stP.setString(4, "application/vstatus+xml");
			stP.setString(5, wrapStatus(stat.getCourse(), stat.getSpeed()));
			stP.executeUpdate();
			stP.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private String wrapStatus(double course, WorldSpeed speed)
	{
		return "<status course=\"" + course + "\" speed=\"" + speed.toString() + "\" />";
	}

	private org.postgis.PGgeometry createGeometry(WorldLocation loc)
	{
		org.postgis.Point theP = new org.postgis.Point(loc.getLong(), loc.getLat(), -loc.getDepth());
		theP.setSrid(4326);
		return  new org.postgis.PGgeometry(theP);
	}
	
	private Integer getDatasetIndexFor(String participantName, String dataFormat)
			throws SQLException
	{
		int thisParticipantIndex = 0;
		ResultSet rs;
		PreparedStatement stP;
		Statement st = _conn.createStatement();

		// does the participant exist in the database?
		rs = st.executeQuery("SELECT platformid from platforms where platformname = '"
				+ participantName + "';");
		if (rs.next())
		{
			thisParticipantIndex = rs.getInt(1);
		}
		else
		{
			// nope, better create it
			stP = _conn.prepareStatement("INSERT INTO platforms(platformname) VALUES (?)");
			stP.setString(1, participantName);
			stP.executeUpdate();
			stP.close();

			// and get the id
			rs = st
					.executeQuery("SELECT Max(platformid) AS MaxOfID FROM platforms;");
			rs.next();
			thisParticipantIndex = rs.getInt(1);
		}

		// does the data format in the database?
		int thisExerciseIndex = 0;
		rs = st.executeQuery("SELECT exerciseid from exercises where exercisename = '"
				+ _scenarioName + "';");
		if (rs.next())
		{
			thisExerciseIndex = rs.getInt(1);
		}
		else
		{
			// nope, better create it
			stP = _conn.prepareStatement("INSERT INTO exercises(exercisename) VALUES (?)");
			stP.setString(1, _scenarioName);
			stP.executeUpdate();
			stP.close();

			// and get the id
			rs = st.executeQuery("SELECT Max(exerciseid) AS MaxOfID FROM exercises;");
			rs.next();
			thisExerciseIndex = rs.getInt(1);
		}		
		
		// does the data format in the database?
		int thisFormatIndex = 0;
		rs = st.executeQuery("SELECT formatid from formats where formatname = '"
				+ dataFormat + "';");
		if (rs.next())
		{
			thisFormatIndex = rs.getInt(1);
		}
		else
		{
			// nope, better create it
			stP = _conn.prepareStatement("INSERT INTO formats(formatname, iconname) VALUES (?,?)");
			stP.setString(1, dataFormat);
			stP.setString(2, dataFormat  +".png");
			stP.executeUpdate();
			stP.close();

			// and get the id
			rs = st.executeQuery("SELECT Max(formatid) AS MaxOfID FROM formats;");
			rs.next();
			thisFormatIndex = rs.getInt(1);
		}

		// ok, now create the dataset
		stP = _conn
				.prepareStatement("INSERT INTO datasets(datasetname, platformid, formatId, exerciseid) VALUES (?,?,?,?)");
		stP.setString(1, _datasetPrefix + " dated:" + new Date().toString());
		stP.setInt(2, thisParticipantIndex);
		stP.setInt(3, thisFormatIndex);
		stP.setInt(4, thisExerciseIndex);
		stP.executeUpdate();
		stP.close();

		// and get the id
		rs = st.executeQuery("SELECT Max(datasetid) AS MaxOfID FROM datasets;");
		rs.next();
		int thisDatasetIndex = rs.getInt(1);

		st.close();

		return new Integer(thisDatasetIndex);
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
		try
		{
			// check there are some decisions
			if (detections.size() == 0)
				return;

			// see if we've loaded this participant
			Integer theIndex = _participantIds.get(pt.getName() + DETECTION_FORMAT);
			PreparedStatement stP;

			if (theIndex == null)
			{
				theIndex = getDatasetIndexFor(pt.getName(), DETECTION_FORMAT);
				_participantIds.put(pt.getName() + DETECTION_FORMAT, theIndex);
			}

			stP = _conn
					.prepareStatement("INSERT INTO dataItems(datasetid, dtg, summary) VALUES (?, ?, ?)");
			stP.setInt(1, theIndex.intValue());
			stP.setTimestamp(2, new Timestamp(dtg));

			Iterator<DetectionEvent> iter = detections.iterator();
			while (iter.hasNext())
			{
				DetectionEvent de = iter.next();
				String detStr = "";
				Float brg = de.getBearing();
				if (brg != null)
					detStr += "Brg:" + brg.floatValue();
				WorldDistance dist = de.getRange();
				if (dist != null)
					detStr += " Rng:" + dist.toString();
				detStr += " " + de.getTargetType().toString();
				stP.setString(3, detStr);
				stP.executeUpdate();
			}

			stP.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
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
	protected void writeThisDecisionDetail(ParticipantType pt, String activity,
			long dtg)
	{
		try
		{
			// see if we've loaded this participant
			Integer theIndex = _participantIds.get(pt.getName() + NARRATIVE_FORMAT);
			PreparedStatement stP;

			if (theIndex == null)
			{
				theIndex = getDatasetIndexFor(pt.getName(), NARRATIVE_FORMAT);
				_participantIds.put(pt.getName() + NARRATIVE_FORMAT, theIndex);
			}

			stP = _conn
					.prepareStatement("INSERT INTO dataItems(datasetid, dtg, summary) VALUES (?, ?, ?)");
			stP.setInt(1, theIndex.intValue());
			stP.setTimestamp(2, new Timestamp(dtg));
			stP.setString(3, activity);
			stP.executeUpdate();
			stP.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * the scenario has stepped forward
	 */
	public void step(ScenarioType scenario, long newTime)
	{
		if (!isActive())
			return;

		// just check that/if we have an output file
		if (_conn == null)
		{
			// ok, better sort out the output files.
			createConnection();
		}

		// still failing?
		if(_conn == null)
			return;
		
		// get the positions of the participants
		final Integer[] lst = _myScenario.getListOfParticipants();
		for (int thisIndex = 0; thisIndex < lst.length; thisIndex++)
		{
			final Integer integer = lst[thisIndex];
			if (integer != null)
			{
				final ASSET.ParticipantType pt = _myScenario.getThisParticipant(integer
						.intValue());

				// is this a target of interest?
				if ((_subjectToTrack == null)
						|| (_subjectToTrack.matches(pt.getCategory())))
				{
					if (getRecordPositions())
					{
						final ASSET.Participants.Status stat = pt.getStatus();
						final MWC.GenericData.WorldLocation loc = stat.getLocation();

						// ok, now output these details in our special format
						writeThesePositionDetails(loc, stat, pt, newTime);
					}

					if (getRecordDetections())
					{
						// get the list of detections
						DetectionList list = pt.getNewDetections();
						writeTheseDetectionDetails(pt, list, newTime);
					}

					if (getRecordDecisions())
					{
						// get the current activity
						String thisActivity = pt.getActivity();
						writeThisDecisionDetail(pt, thisActivity, newTime);
					}

				}

			}
		}

	}

	private void createConnection()
	{
		try
		{
			String url = "jdbc:postgresql://86.134.91.5:5432/gnd";
			_conn = DriverManager.getConnection(url, "dev", DEV_PASSWORD);
		}
		catch (SQLException e)
		{
			System.err.println("failed to create connection");
			e.printStackTrace();
		}
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

	// ////////////////////////////////////////////////
	// member getter/setters
	// ////////////////////////////////////////////////

	public boolean getRecordDetections()
	{
		return _recordDetections;
	}

	public void setRecordDetections(boolean recordDetections)
	{
		this._recordDetections = recordDetections;
	}

	public boolean getRecordDecisions()
	{
		return _recordDecisions;
	}

	public void setRecordDecisions(boolean recordDecisions)
	{
		this._recordDecisions = recordDecisions;
	}

	public boolean getRecordPositions()
	{
		return _recordPositions;
	}

	public void setRecordPositions(boolean recordPositions)
	{
		this._recordPositions = recordPositions;
	}

	public TargetType getSubjectToTrack()
	{
		return _subjectToTrack;
	}

	public void setSubjectToTrack(TargetType subjectToTrack)
	{
		this._subjectToTrack = subjectToTrack;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static final class recToDbTest extends
			ContinuousRecordToFileObserver.RecToFileTest
	{
		String _buildDate;
		String _headerDetails;
		boolean _detectionDetailsWritten;
		boolean _positionDetailsWritten;
		boolean _decisionDetailsWritten;

		public recToDbTest(final String val)
		{
			super(val);
		}

		public void testCombinations()
		{
//			doThisTest(true, true, true, null);
			doThisTest(true, false, false, null);
//			doThisTest(false, true, false, null);
//			doThisTest(false, false, false, null);
		}

		public void doThisTest(boolean testPos, boolean testDecs, boolean testDets,
				TargetType target)
		{
			RecordStatusToDBObserverType observer = new RecordStatusToDBObserverType(
					testDets, testDecs, testPos, target, "trial", true, "monster run");
			assertNotNull("observer wasn't created", observer);

			// and the scenario
			CoreScenario cs = new CoreScenario();
			cs.setName("testing scenario output");

			// add a participant
			final SSN ssn = new SSN(12);
			ssn.setName("SSN");
			ssn.setCategory(new Category(Category.Force.BLUE,
					Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));
			ssn.setDecisionModel(new ASSET.Models.Decision.Tactical.Wait(
					new Duration(12, Duration.HOURS), "do wait"));
			OpticSensor sampleSensor = new OpticSensor(12)
			{
				private static final long serialVersionUID = 1L;

				// what is the detection strength for this target?
				protected DetectionEvent detectThis(EnvironmentType environment,
						ParticipantType host, ParticipantType target1, long time,
						ScenarioType scenario)
				{
					DetectionEvent de = new DetectionEvent(12l, 12, null, this, null,
							null, null, null, null, null, null, null, ssn);
					return de;
				}
				// public void detects(EnvironmentType environment, DetectionList
				// existingDetections, ParticipantType ownship, ScenarioType scenario,
				// long time)
				// {
				// DetectionEvent de = new DetectionEvent(12l, 12, null, this, null,
				// null, null, null, null, null, null, null, ssn);
				// existingDetections.add(de);
				// }
			};
			ssn.addSensor(sampleSensor);
			ssn.setMovementChars(HeloMovementCharacteristics.getSampleChars());
			Status theStat = new Status(12, 12);
			theStat.setLocation(new WorldLocation(12, 12, 12));
			theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			ssn.setStatus(theStat);

			cs.addParticipant(12, ssn);

			// initialise results instances
			_buildDate = null;
			_headerDetails = null;
			_detectionDetailsWritten = false;
			_positionDetailsWritten = false;
			_decisionDetailsWritten = false;

			// and do the setup
			observer.setup(cs);

			// do a step
			cs.step();

			// do lots more steps
			for (int i = 0; i < 3000; i++)
			{
				// do a step
				cs.step();
			}

			// and the close
			observer.tearDown(cs);
			
			System.out.println("complete");
			
			// and close the file
		//	assertNull("stream wasn't closed", observer._conn);

		}

		// ////////////////////////////////////////////////
		// utility method to create an observer - over-ridden in instantiated
		// classes
		// ////////////////////////////////////////////////

	}

	@Override
	protected void performCloseProcessing(ScenarioType scenario)
	{
		try
		{
			_conn.close();
		}
		catch (SQLException e)
		{
			System.err.println("failed to close database connection");
			e.printStackTrace();
		}
	}

	@Override
	protected void performSetupProcessing(ScenarioType scenario)
	{
		// check we have the driver
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("Failed to load database driver");
			e.printStackTrace();
		}

		// store the scenario name, so we can create our exercuse
		_scenarioName = scenario.getName() + " ran on:" + 
		MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(new Date().getTime()); 
		
		try
		{
			String url = "jdbc:postgresql://86.134.91.5:5432/gnd";
			_conn = DriverManager.getConnection(url, "dev", DEV_PASSWORD);
			
			// also tell the connection about our new custom data types
	    ((org.postgresql.PGConnection)_conn).addDataType("geometry",org.postgis.PGgeometry.class);
	    ((org.postgresql.PGConnection)_conn).addDataType("box3d",org.postgis.PGbox3d.class);

	    
			// _conn = DriverManager.getConnection("//localhost:5432/GND", "postgres",
			// "4pfonmr");

		}
		catch (SQLException e)
		{
			System.err.println("failed to create connection");
			e.printStackTrace();
		}
	}
	
  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor =  new RecordToDbObserverInfo(this);

    return _myEditor;
  }
	

	public boolean hasEditor()
	{
		return true;
	}
	
	 //////////////////////////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////////////////////////

  static public class RecordToDbObserverInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param obj the Layers themselves
     */
    public RecordToDbObserverInfo(final RecordStatusToDBObserverType obj)
    {
      super(obj, obj.getName(), "Edit");
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
          prop("Active", "Whether this observer is active"),
          prop("Name", "The name of this observer"),
        };

        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }
}
