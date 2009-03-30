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
import java.util.Vector;

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
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 28-Oct-2003 Time: 14:17:34 To
 * change this template use Options | File Templates.
 */
public class RecordStatusToDBObserverType extends CoreObserver implements
		ASSET.Scenario.ScenarioSteppedListener
{
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
	 */
	public RecordStatusToDBObserverType(final boolean recordDetections,
			boolean recordDecisions, final boolean recordPositions,
			final TargetType subjectToTrack, final String observerName,
			boolean isActive)
	{
		super(observerName, isActive);

		_recordDetections = recordDetections;
		_recordDecisions = recordDecisions;
		_recordPositions = recordPositions;
		_subjectToTrack = subjectToTrack;

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
		// start off with the dataset
		try
		{
			// see if we've loaded this participant
			Integer theIndex = _participantIds.get(pt.getName());
			int thisParticipantIndex = 0;
			PreparedStatement stP;
			Statement st = _conn.createStatement();

			if (theIndex == null)
			{
				// does it exist in the database?
				ResultSet rs = st.executeQuery("SELECT datasourceid from datasources where name = '"
								+ pt.getName() + "';");
				if(rs.next())
				{
					thisParticipantIndex = rs.getInt(1);
				}
				else
				{
					// nope, better create it
					stP = _conn
							.prepareStatement("INSERT INTO datasources(name) VALUES (?)");
					stP.setString(1, pt.getName());
					stP.executeUpdate();
					stP.close();

					// and get the id
					rs = st
							.executeQuery("SELECT Max(datasourceid) AS MaxOfID FROM datasources;");
					rs.next();
					thisParticipantIndex = rs.getInt(1);
				}

				// ok, now create the dataset
				stP = _conn
						.prepareStatement("INSERT INTO datasets(name, datasourceid, formatId) VALUES (?,?,1)");
				stP.setString(1, "Position log:" + new Date().toGMTString());
				stP.setInt(2, thisParticipantIndex);
				stP.executeUpdate();
				stP.close();

				// and get the id
				rs = st
						.executeQuery("SELECT Max(datasetid) AS MaxOfID FROM datasets;");
				rs.next();
				int thisDatasetIndex = rs.getInt(1);
				theIndex = new Integer(thisDatasetIndex);
				_participantIds.put(pt.getName(), theIndex);
			}

			stP = _conn
					.prepareStatement("INSERT INTO dataItem(datasetid, datetime, latitude, longitude) VALUES (?, ?, ?, ?)");
			stP.setInt(1, theIndex.intValue());
			stP.setTimestamp(2, new Timestamp(stat.getTime()));
			stP.setDouble(3, loc.getLat());
			stP.setDouble(4, loc.getLong());
			stP.executeUpdate();
			stP.close();
			
			// and close our reuseable statement
			st.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
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
	}

	/**
	 * the scenario has stepped forward
	 */
	public void step(long newTime)
	{
		if (!isActive())
			return;

		// just check that/if we have an output file
		if (_conn == null)
		{
			// ok, better sort out the output files.
			createConnection();
		}

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
			_conn = DriverManager.getConnection("//localhost:5432/GND", "postgres",
					"4pfonmr");
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
	protected void addListeners()
	{
		_myScenario.addScenarioSteppedListener(this);
	}

	/**
	 * remove any listeners
	 */
	protected void removeListeners()
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
	public static final class recToFileTest extends
			ContinuousRecordToFileObserver.RecToFileTest
	{
		String _buildDate;
		String _headerDetails;
		boolean _detectionDetailsWritten;
		boolean _positionDetailsWritten;
		boolean _decisionDetailsWritten;

		public recToFileTest(final String val)
		{
			super(val);
		}

		public void testCombinations()
		{
			doThisTest(true, true, true, null);
			doThisTest(true, false, false, null);
			doThisTest(false, true, false, null);
			doThisTest(false, false, false, null);
		}

		public void doThisTest(boolean testPos, boolean testDecs, boolean testDets,
				TargetType target)
		{
			RecordStatusToDBObserverType observer = new RecordStatusToDBObserverType(
					testDets, testDecs, testPos, target, "trial", true);
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
				/**
				 * 
				 */
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
			for (int i = 0; i < 100; i++)
			{
				// do a step
				cs.step();
			}

			// and the close
			observer.tearDown(cs);

			// and close the file
			assertNull("stream wasn't closed", observer._conn);

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

		try
		{
			String url = "jdbc:postgresql://localhost:5432/GND";
			_conn = DriverManager.getConnection(url, "postgres", "4pfonmr");

			// _conn = DriverManager.getConnection("//localhost:5432/GND", "postgres",
			// "4pfonmr");

		}
		catch (SQLException e)
		{
			System.err.println("failed to create connection");
			e.printStackTrace();
		}
	}

	@Override
	public EditorType getInfo()
	{
		return null;
	}

	@Override
	public boolean hasEditor()
	{
		return false;
	}
}
