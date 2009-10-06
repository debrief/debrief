/*
 * Desciption:
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 8:45:34 AM
 */
package ASSET.Scenario.Observers;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Movement.RectangleWander;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.BatchCollatorHelper;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * listener which keeps tally of how close a particular type of vessel
 * gets to another indicated type of vessel
 */
public class ProximityObserver extends CoreObserver implements
  ScenarioObserver.ScenarioReferee,
  ASSET.Scenario.ScenarioSteppedListener,
  BatchCollator,
  IAttribute
{
  //////////////////////////////////////////////////
  // property listings
  //////////////////////////////////////////////////
  static public class ProximityObserverInfo extends EditorType
  {


    /**
     * constructor for editable details
     *
     * @param data the object we're going to edit
     */
    public ProximityObserverInfo(final ProximityObserver data)
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
        final PropertyDescriptor[] res = {
          prop("Name", "the name of this observer"),
          prop("TargetType", "the participant we're measuring range from"),
          prop("WatchType", "the type of participant to monitor"),
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

  //////////////////////////////////////////////////
  // and now general test
  //////////////////////////////////////////////////
  public static final class ProxObserverTest extends SupportTesting.EditableTesting
  {
    boolean hasStopped = false;

    public ProxObserverTest(final String val)
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
      return new ProximityObserver(null, null, "here", true);
    }

    public final void testNoDemCourseSpeed()
    {
      final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
      final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
      final WorldArea theArea = new WorldArea(topLeft, bottomRight);
      final RectangleWander tw = new RectangleWander(theArea, "rect wander");
      final RectangleWander tw2 = new RectangleWander(theArea, "rect wander 2");


      final Status stat = new Status(1, 0);
      stat.setLocation(SupportTesting.createLocation(2000, 4000));
      stat.setCourse(270);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      final Status stat2 = new Status(1, 0);
      stat2.setLocation(SupportTesting.createLocation(4000, 2000));
      stat2.setCourse(155);
      stat2.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      final DemandedStatus dem = null;

      final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat, null, dem, null, null, 100);

      assertNotNull("dem returned", ds);
      //      assertEquals("still on old course", 22, ds.getCourse(), 0);
      //      assertEquals("still on old speed", MWC.Algorithms.Conversions.Kts2Mps(12), ds.getSpeed(), 0);

      Surface fisher = new Surface(23);
      fisher.setName("Fisher");
      fisher.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
      fisher.setStatus(stat);
      fisher.setDecisionModel(tw);
      fisher.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

      Surface fisher2 = new Surface(25);
      fisher2.setName("Fisher2");
      fisher2.setCategory(new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
      fisher2.setStatus(stat2);
      fisher2.setDecisionModel(tw2);
      fisher2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

      CoreScenario cs = new CoreScenario();
      cs.setScenarioStepTime(10000);
      cs.addParticipant(fisher.getId(), fisher);
      cs.addParticipant(fisher2.getId(), fisher2);

      hasStopped = false;

      // and the stop on proximity observer
      StopOnProximityObserver stopper = new StopOnProximityObserver(new TargetType(Category.Force.RED),
                                                                    new TargetType(Category.Force.BLUE), new WorldDistance(0.6, WorldDistance.KM), "stop on proxim", true)
      {
        protected void stopScenario()
        {
          hasStopped = true;
          super.stopScenario();
          System.out.println("stopped at:" + super._myScenario.getTime());
        }
      };

      stopper.setup(cs);
      System.out.println("started at:" + cs.getTime());

      //     DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp", "rect_wander_search.rep", false, "plotter", true);
      //     TrackPlotObserver tpo = new TrackPlotObserver("c:/temp", 300, 300, "rect_wander_search.png", null, false, true, "tester", true);
      //     dro.setup(cs);
      //    tpo.setup(cs);
      //
      //   dro.outputThisArea(theArea);

      // now run through to completion
      while ((cs.getTime() < 120000000) && (!hasStopped))
      {
        cs.step();
      }


      stopper.tearDown(cs);

      //   dro.tearDown(cs);
      //   tpo.tearDown(cs);

      // check that we stopped
      assertTrue("Proximity sensor failed to notice vessels getting too close to each other", hasStopped);


    }

  }

  //////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////
  public static class StopOnProximityObserver extends ProximityObserver
  {
    //////////////////////////////////////////////////
    // editable properties
    //////////////////////////////////////////////////
    static public class StopOnProximityObserverInfo extends EditorType
    {


      /**
       * constructor for editable details
       *
       * @param data the object we're going to edit
       */
      public StopOnProximityObserverInfo(final StopOnProximityObserver data)
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
          final PropertyDescriptor[] res = {
            prop("Name", "the name of this observer"),
            prop("Range", "the range at which to stop"),
            prop("TargetType", "the participant we're measuring range from"),
            prop("WatchType", "the type of participant to monitor"),
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

    /**
     * the range within which we stop the scenario
     */
    private WorldDistance _cutOffRange;


    private EditorType _myEditor11;

    /**
     * constructor - get going.
     *
     * @param watchType   the type of vessel to monitor
     * @param targetType  the type of vessel it's looking for
     * @param cutOffRange the range at which we stop the scenario
     */
    public StopOnProximityObserver(final TargetType watchType,
                                   final TargetType targetType,
                                   final WorldDistance cutOffRange,
                                   final String myName,
                                   final boolean isActive)
    {
      super(watchType, targetType, myName, isActive);

      _cutOffRange = cutOffRange;
    }


    /**
     * get the editor for this item
     *
     * @return the BeanInfo data for this editable object
     */
    public EditorType getInfo()
    {
      if (_myEditor11 == null)
        _myEditor11 = new StopOnProximityObserverInfo(this);

      return _myEditor11;
    }

    public WorldDistance getRange()
    {
      return _cutOffRange;
    }

    //////////////////////////////////////////////////
    // property editing
    //////////////////////////////////////////////////

    /**
     * ok, we know the range from this target. handle it
     *
     * @param rng thje current range (in degrees)
     */
    protected void handleThisRange(double rng)
    {
      double distDegs = _cutOffRange.getValueIn(WorldDistance.DEGS);

      if (rng <= distDegs)
      {
        // ok, call a stop
        stopScenario();
      }
    }

    /**
     * whether there is any edit information for this item
     * this is a convenience function to save creating the EditorType data
     * first
     *
     * @return yes/no
     */
    public boolean hasEditor()
    {
      return true;
    }

    public void setRange(WorldDistance cutOffRange)
    {
      this._cutOffRange = cutOffRange;
    }

    /**
     * stop the scenario - we've peaked.
     */
    protected void stopScenario()
    {
      _myScenario.stop("Stopped on proximity:" + getName());
    }


  }

  //////////////////////////////////////////////////
  // first stop on proximity tset
  //////////////////////////////////////////////////
  public static final class StopOnProxObserverTest extends SupportTesting.EditableTesting
  {
    public StopOnProxObserverTest(final String val)
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
      return new StopOnProximityObserver(null, null, new WorldDistance(12, WorldDistance.NM), "here", true);
    }
  }

  /**
   * running tally of fitness score
   */
  private double _myScore = -1;


  /**
   * the vessels we're watching
   */
  private Vector<ParticipantType> _watchVessels = new Vector<ParticipantType>(0, 1);

  /**
   * the targets we're concerned about proximity to
   */
  private Vector<ParticipantType> _targetVessels = new Vector<ParticipantType>(0, 1);


  /**
   * the target type for vessels we're watching
   */
  private TargetType _watchType = null;


  //////////////////////////////////////////////////
  // inter-scenario observers
  //////////////////////////////////////////////////

  /**
   * the target type for target vessels for the target we're watching
   */
  private TargetType _targetType = null;

  /**
   * our batch collator
   */
  private BatchCollatorHelper _batcher = null;

  /**
   * whether to override (cancel) writing per-scenario results to file
   */
  private boolean _onlyBatch = true;

  private EditorType _myEditor1;

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public ProximityObserver(final TargetType watchType,
                           final TargetType targetType,
                           String myType,
                           final boolean isActive)
  {
    super(myType, isActive);
    // remember the target types
    _watchType = watchType;
    _targetType = targetType;
   }

  /**
   * add any applicable listeners
   */
  protected void addListeners()
  {

    // listen to the scenario stepping
    _myScenario.addScenarioSteppedListener(this);
  }

  //////////////////////////////////////////////////
  // inter-scenario observer methods
  //////////////////////////////////////////////////
  public void finish()
  {
    if (_batcher != null)
    {
      // ok, get the batch thingy to do it's stuff
      _batcher.writeOutput(getHeaderInfo());
    }
  }


  /***************************************************************
   *  member methods
   ***************************************************************/


  /**
   * accessor to retrieve batch processing settings
   */
  public BatchCollatorHelper getBatchHelper()
  {
    return _batcher;
  }


  /**
   * whether to override (cancel) writing per-scenario results to file
   *
   * @return whether to override batch processing
   */
  public boolean getBatchOnly()
  {
    return _onlyBatch;
  }

  @Override
	public DataDoublet getCurrent()
	{
		return getHelper().getCurrent();
	}

  @Override
	public Vector<DataDoublet> getHistoricValues()
	{
		return getHelper().getHistoricValues();
	}

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public EditorType getInfo()
  {
    if (_myEditor1 == null)
      _myEditor1 = new ProximityObserverInfo(this);

    return _myEditor1;
  }

  /**
   * define the filename for the batch output
   *
   * @return
   */
  private String getMySuffix()
  {
    return "csv";
  }

  /**
   * return how well this scenario performed, according to this referee
   */
  public ScenarioRunner.ScenarioOutcome getOutcome()
  {
    ScenarioRunner.ScenarioOutcome res = new ScenarioRunner.ScenarioOutcome();
    res.score = MWC.Algorithms.Conversions.Degs2Yds(_myScore);
    res.summary = getSummary();
    return res;
  }

  /**
   * get a text description of the outcome
   */
  public String getSummary()
  {
    return "Distance:" + (int) MWC.Algorithms.Conversions.Degs2Yds(_myScore);
  }

  /**
   * get the types of vessel whose proximity we are checking for (targets)
   */
  public TargetType getTargetType()
  {
    return _targetType;
  }


  /**
   * get the types of vessel we are monitoring
   */
  public TargetType getWatchType()
  {
    return _watchType;
  }

  /**
   * ok, we know the range from this target. handle it
   *
   * @param rng thje current range (in degrees)
   * @param rng2 
   */
  protected void handleThisRange(final long time, double rng)
  {
    // is this the first range?
    if (_myScore == -1)
    {
      _myScore = rng;
    }
    else
      _myScore = Math.min(_myScore, rng);
    
    // tell the attribute helper
    getHelper().newData(time, _myScore);
  }

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public boolean hasEditor()
  {
    return true;
  }

  public void initialise(File outputDirectory)
  {
    // set the output directory for the batch collator
    if (_batcher != null)
      _batcher.setDirectory(outputDirectory);
  }


  @Override
	public boolean isSignificant()
	{
		return true;
	}

  /**
   * right, the scenario is about to close.  We haven't removed the listeners
   * or forgotten the scenario (yet).
   *
   * @param scenario the scenario we're closing from
   */
  protected void performCloseProcessing(ScenarioType scenario)
  {
    // do we have a batcher?
    // are we recording to batch?
    if (_batcher != null)
    {
      _batcher.submitResult(_myScenario.getName(), _myScenario.getCaseId(), (int) MWC.Algorithms.Conversions.Degs2m(_myScore));
    }
    // clear out lists
    _targetVessels.removeAllElements();
    _watchVessels.removeAllElements();

    // reset the score
    _myScore = -1;
  }

  /**
   * we're getting up and running.  The observers have been created and we've remembered
   * the scenario
   *
   * @param scenario the new scenario we're looking at
   */
  protected void performSetupProcessing(ScenarioType scenario)
  {
    // find any vessels we're interested in which are already in the scenario
    final Integer[] lst = scenario.getListOfParticipants();
    for (int thisI = 0; thisI < lst.length; thisI++)
    {
      final Integer thisIndex = lst[thisI];
      if (thisIndex != null)
      {
        final ASSET.ParticipantType thisP = scenario.getThisParticipant(thisIndex.intValue());

        // is this of our target category?
        if (_targetType.matches(thisP.getCategory()))
        {
          _targetVessels.add(thisP);
        }

        // is this of our watched category?
        if (_watchType.matches(thisP.getCategory()))
        {
          _watchVessels.add(thisP);
        }
      }
    }
  }

  /**
   * remove any listeners
   */
  protected void removeListeners()
  {

    // stop listening to the scenario
    _myScenario.removeScenarioSteppedListener(this);
  }

  /**
   * the scenario has restarted
   */
  public void restart()
  {
    super.restart();

    _myScore = -1;
  }


  /**
   * configure the batch processing
   *
   * @param fileName          the filename to write to
   * @param collationMethod   how to collate the data
   * @param perCaseProcessing whether to collate the stats on a per-case basis
   * @param isActive          whether this collator is active
   */
  public void setBatchCollationProcessing(String fileName, String collationMethod,
                                          boolean perCaseProcessing, boolean isActive)
  {
    _batcher = new BatchCollatorHelper(getName(), perCaseProcessing, collationMethod, isActive, "range (metres)");

    // do we have a filename?
    if (fileName == null)
      fileName = getName() + "." + getMySuffix();

    _batcher.setFileName(fileName);
  }

  /**
   * whether to override (cancel) writing per-scenario results to file
   *
   * @param override
   */
  public void setBatchOnly(boolean override)
  {
    _onlyBatch = override;
  }

	/**
   * set the types of vessel we are looking for
   *
   * @param targetType
   */
  public void setTargetType(TargetType targetType)
  {
    this._targetType = targetType;
  }


	/**
   * set the types of vessel we are monitoring
   */
  public void setWatchType(TargetType watchType)
  {
    this._watchType = watchType;
  }


	/**
   * the scenario has stepped forward
   */
  public void step(long newTime)
  {

    // step through our watch vessels
    final Iterator<ParticipantType> thisV = _watchVessels.iterator();
    while (thisV.hasNext())
    {
      final ASSET.ParticipantType thisWatch = (ASSET.ParticipantType) thisV.next();

      // and through our target vessels
      final Iterator<ParticipantType> thisW = _targetVessels.iterator();
      while (thisW.hasNext())
      {
        final ASSET.ParticipantType thisTarget = (ASSET.ParticipantType) thisW.next();

        // check they're not the same vessel
        if (thisTarget != thisWatch)
        {
          // find the range
          final double rng = thisTarget.getStatus().getLocation().rangeFrom(thisWatch.getStatus().getLocation());

          // ok. we know this range.  handle it
          handleThisRange(newTime, rng);
        }
      }
    }
  }



}
