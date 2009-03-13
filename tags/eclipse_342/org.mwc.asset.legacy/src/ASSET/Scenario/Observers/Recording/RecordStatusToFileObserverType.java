package ASSET.Scenario.Observers.Recording;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Sensor.Initial.OpticSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.ScenarioType;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 28-Oct-2003
 * Time: 14:17:34
 * To change this template use Options | File Templates.
 */
abstract public class RecordStatusToFileObserverType extends ContinuousRecordToFileObserver implements ASSET.Scenario.ScenarioSteppedListener
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


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////


  /**
   * an observer which wants to record it's data to file
   *
   * @param directoryName    the directory to output to
   * @param fileName         the filename to output to
   * @param recordDetections whether to record detections
   * @param recordDecisions  whether to record decisions
   * @param recordPositions  whether to record positions
   * @param subjectToTrack   the type of target to track (or null for all targets)
   * @param observerName     what to call this narrative observer
   * @param isActive         whether this observer is active
   */
  public RecordStatusToFileObserverType(final String directoryName,
                                        final String fileName,
                                        final boolean recordDetections,
                                        boolean recordDecisions,
                                        final boolean recordPositions,
                                        final TargetType subjectToTrack,
                                        final String observerName,
                                        boolean isActive)
  {
    super(directoryName, fileName, observerName, isActive);

    _recordDetections = recordDetections;
    _recordDecisions = recordDecisions;
    _recordPositions = recordPositions;
    _subjectToTrack = subjectToTrack;
  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /**
   * ok. ready to start writing. get on with it
   *
   * @param title      the title of this run
   * @param currentDTG the current time (not model time)
   * @throws IOException
   */
  abstract protected void writeFileHeaderDetails(final String title, long currentDTG) throws IOException;


  /**
   * write this set of details to file
   *
   * @param loc  the current location
   * @param stat the current status
   * @param pt   the participant in question
   */
  abstract protected void writeThesePositionDetails(WorldLocation loc, Status stat, ParticipantType pt,
                                                    long newTime);


  /**
   * write these detections to file
   *
   * @param pt         the participant we're on about
   * @param detections the current set of detections
   * @param dtg        the dtg at which the detections were observed
   */
  abstract protected void writeTheseDetectionDetails(ParticipantType pt, DetectionList detections, long dtg);

  /**
   * write the current decision description to file
   *
   * @param pt       the participant we're looking at
   * @param activity a description of the current activity
   * @param dtg      the dtg at which the description was recorded
   */
  abstract protected void writeThisDecisionDetail(ParticipantType pt, String activity, long dtg);

  /**
   * the scenario has stepped forward
   */
  public void step(long newTime)
  {
    if (!isActive())
      return;

    // just check that/if we have an output file
    if (_os == null)
      return;

    // get the positions of the participants
    final Integer[] lst = _myScenario.getListOfParticipants();
    for (int thisIndex = 0; thisIndex < lst.length; thisIndex++)
    {
      final Integer integer = lst[thisIndex];
      if (integer != null)
      {
        final ASSET.ParticipantType pt = _myScenario.getThisParticipant(integer.intValue());

        // is this a target of interest?
        if ((_subjectToTrack == null) || (_subjectToTrack.matches(pt.getCategory())))
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

  /**
   * the file has been opened, write the header details to it
   */
  protected void writeFileHeaderInformation(FileWriter destination, String fileName) throws IOException
  {
    // write out the build date
    writeBuildDate(getBuildDate());

    // ok, write the header details
    long theDTG = new Date().getTime();
    writeFileHeaderDetails(fileName, theDTG);

  }

  /**
   * write the supplied build details to file
   *
   * @param details the build time/date
   * @throws IOException if we have any of that file trouble
   */
  protected abstract void writeBuildDate(String details) throws IOException;

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

  //////////////////////////////////////////////////
  // member getter/setters
  //////////////////////////////////////////////////

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

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static final class recToFileTest extends ContinuousRecordToFileObserver.RecToFileTest
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

    public void doThisTest(boolean testPos, boolean testDecs, boolean testDets, TargetType target)
    {
      RecordStatusToFileObserverType observer = getRecordObserver(true, testDets, testDecs, testPos, target);
      assertNotNull("observer wasn't created", observer);

      // and the scenario
      CoreScenario cs = new CoreScenario();
      cs.setName("testing scenario output");

      // add a participant
      final SSN ssn = new SSN(12);
      ssn.setName("SSN");
      ssn.setCategory(new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));
      ssn.setDecisionModel(new ASSET.Models.Decision.Tactical.Wait(new Duration(12, Duration.HOURS), "do wait"));
      OpticSensor sampleSensor = new OpticSensor(12)
      {
        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				// what is the detection strength for this target?
        protected DetectionEvent detectThis(EnvironmentType environment, ParticipantType host, ParticipantType target1,
                                            long time, ScenarioType scenario)
        {
          DetectionEvent de = new DetectionEvent(12l, 12, null, this, null, null, null, null, null, null, null, null, ssn);
          return de;
        }
        //        public void detects(EnvironmentType environment, DetectionList existingDetections, ParticipantType ownship, ScenarioType scenario,
        //                            long time)
        //        {
        //          DetectionEvent de = new DetectionEvent(12l, 12, null, this, null, null, null, null, null, null, null, null, ssn);
        //          existingDetections.add(de);
        //        }
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

      assertNotNull("build date wasn't called", _buildDate);
      assertNotNull("headerDetails weren't called", _headerDetails);

      // do a step
      cs.step();

      // see if the relevant bits were called
      assertEquals("positions called", testPos, _positionDetailsWritten);
      assertEquals("detections called", testDets, _detectionDetailsWritten);
      assertEquals("decisions called", testDecs, _decisionDetailsWritten);

      // and the close
      observer.tearDown(cs);

      // and close the file
      assertNull("stream wasn't closed", observer._os);


    }

    //////////////////////////////////////////////////
    // utility method to create an observer - over-ridden in instantiated classes
    //////////////////////////////////////////////////
    protected RecordStatusToFileObserverType getRecordObserver(boolean isActive, boolean dets, boolean
      decisions, boolean positions, TargetType subject)
    {
      return new RecordStatusToFileObserverType(super.dir_name, super.file_name, dets, decisions, positions, subject, "rec status", isActive)
      {
        protected void writeBuildDate(String details) throws IOException
        {
          _buildDate = details;
        }

        protected void writeFileHeaderDetails(String title, long currentDTG) throws IOException
        {
          _headerDetails = title;
        }

        protected void writeTheseDetectionDetails(ParticipantType pt, DetectionList detections, long dtg)
        {
          _detectionDetailsWritten = true;
        }

        protected void writeThesePositionDetails(WorldLocation loc, Status stat, ParticipantType pt, long newTime)
        {
          _positionDetailsWritten = true;
        }

        protected void writeThisDecisionDetail(ParticipantType pt, String activity, long dtg)
        {
          _decisionDetailsWritten = true;
        }

        protected String getMySuffix()
        {
          return "rso";  //To change body of implemented methods use File | Settings | File Templates.
        }

        protected String newName(String name)
        {
          return "new_rec_status";  //To change body of implemented methods use File | Settings | File Templates.
        }

        protected EditorType createEditor()
        {
          return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

      };
    }
  }
}
