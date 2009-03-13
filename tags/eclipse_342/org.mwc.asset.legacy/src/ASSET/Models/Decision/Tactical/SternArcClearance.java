package ASSET.Models.Decision.Tactical;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.ThreeDimMovementCharacteristics;
import ASSET.Models.Movement.TurnAlgorithm;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import java.beans.PropertyEditorSupport;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author Ian Mayo
 * @version 1.0
 */

public class SternArcClearance extends CoreDecision implements java.io.Serializable
{

  ////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the frequency at which we perform stern arc clearance (millis)
   */
  private Duration _frequency;

  /**
   * whether we apply a random factor to these clearances
   */
  private boolean _randomClearances = false;

  /**
   * the time of the last clearance (or -1 for the last one)
   */
  private long _lastClearance;

  /**
   * the currently required bearing (or null if we're not in a clearance
   */
  private Double _demandedBearing;

  /** the course change to use for the clearance
   *
   */
  private double _courseChange;

  /**
   * whether we are in leg one, straightening back up (two), or leg three
   */
  private int _thisLeg;

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;
  
  /** do a single, long course change
   * 
   */ 
  static final public String SINGLE_LEG = "SingleLeg";
  
  /** do a combination of two course changes
   * 
   */
  static final public String DOUBLE_LEG = "DoubleLeg";
  
  /** the type of course change specified
   */
  private String _myStyle;

  //////////////////////////////////////////////////
  // default behaviorus
  //////////////////////////////////////////////////

  /** the default style of course change to use
   *
   */
  private final static String DEFAULT_STYLE = SINGLE_LEG;

  /** the default manoeuvre to use
   *
   */
  private final static double DEFAULT_TURN = 120;

  ////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /** create a new stern arc clearance behaviour
   * 
   * @param freq
   * @param random
   * @param style
   */ 
  public SternArcClearance(final Duration freq, 
                           final boolean random,
                           final String style,
                           final Double courseChange)
  {
    this();
    _frequency = freq;
    _randomClearances = random;
    if(style == null)
       setStyle(DEFAULT_STYLE);
    else
       setStyle(style);

    if(courseChange == null)
      setCourseChange(DEFAULT_TURN);
    else
      setCourseChange(courseChange.doubleValue());


  }

  public SternArcClearance()
  {
    super("Stern Arc Clearance");
    _lastClearance = -1;
    _demandedBearing = null;
  }

  ////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////
  /**
   * performed the waterfalled decision, if a model does not return
   * a demanded status, we move on to the next one
   */
  public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
                                                  ASSET.Models.Movement.MovementCharacteristics chars, ASSET.Participants.DemandedStatus demStatus, ASSET.Models.Detection.DetectionList detections,
                                                  ASSET.Scenario.ScenarioActivityMonitor monitor,
                                                  final long time)
  {
    SimpleDemandedStatus res = null;

    // are we in a clearance?
    if (_demandedBearing != null)
    {
      // so, we're still executing one.
      // calculate the demanded course
      res = performSAC(status, time, res);

      if(res != null)
        super.setLastActivity("Continuing Stern arc clearance");
      else
        super.setLastActivity("Stern arc clearance now complete");
    }
    else
    {
      // remember if we decide to do a clearance now
      boolean startSAC = false;

      // has a SAC frequency been set?
      if (_frequency == null)
      {
        // no frequency has been set - the analyst only wants us to fire once

        // have we already fired?
        if (_lastClearance == -1)
        {
          // nope, let's fire
          startSAC = true;

          // and remember that we've fired
          _lastClearance = time;

        }
        else
        {
          // we've already run. don't run again
          startSAC = false;
        }
      }
      else
      {

        // no we're not in a clearance.  is it time for one?
        if (_lastClearance == -1)
        {
          // we haven't done one yet. make now the time of the last clearance
          _lastClearance = time;
        }


        long myFreq = _frequency.getMillis();

        // how long since the last one?
        long elapsed = time - _lastClearance;

        // are we in random mode?
        if (!_randomClearances)
        {
          // make a decision
          if ((double) elapsed * ASSET.Util.RandomGenerator.nextRandom() >= (double) (myFreq) / 2d)
          {
            // yes, make it look like we are ready
            elapsed = myFreq;
          }
        }

        // no we're not in random mode, wait until the time is passed
        if (elapsed >= myFreq)
        {
          startSAC = true;
        }
      }

      if (startSAC)
      {

        super.setLastActivity("Commencing Stern arc clearance");

        // time for one, lets for for it!
        res = new SimpleDemandedStatus(time, status);

        // initiate manoeuvre
        final double hdg = status.getCourse();

        // specify first leg
        final double firstLeg = hdg + _courseChange;

        // go for it
        res.setCourse(firstLeg);

        // indicate we are on first leg
        _thisLeg = 1;

        // remember the beraing
        _demandedBearing = new Double(res.getCourse());

        // remember the time of this manoeuvre
        _lastClearance = time;
      }
    }


    // whether we are in manoeuvre already

    return res;
  }

  private SimpleDemandedStatus performSAC(final ASSET.Participants.Status status, final long time,
                                          SimpleDemandedStatus res)
  {
    // have we achieved the demanded course?
    final double crse = status.getCourse();

    final double delta = Math.abs(crse - _demandedBearing.doubleValue());

    if (delta > 0)
    {
      // we are still trying to get on the bearing, reproduce the command
      res = new SimpleDemandedStatus(time, status);
      res.setCourse(_demandedBearing.doubleValue());
    }
    else
    {
      Double demBrg = null;
      // yes we are practically on course,
      switch (_thisLeg)
      {
        case 1:
          {
            // so, we've finished turning to the right
            demBrg = new Double(crse - _courseChange);
            break;
          }
        case 2:
          {
            // aaah, are we in a double course
            if(_myStyle.equals(DOUBLE_LEG))
            {
              // now turn to the left
              demBrg = new Double(crse - _courseChange);
            }
            else
            {
              // hey, we're complete.  move along.
            }
            break;
          }
        case 3:
          {
            // we must be on a double course to get here.

            // we've just finished the second leg, put us back on our original course
            demBrg = new Double(crse + _courseChange);
            break;
          }
        case 4:
          {
            // hey, surely we're finished by now
            break;

          }
      }

      if (demBrg != null)
      {
        // now do the next leg
        res = new SimpleDemandedStatus(time, status);

        res.setCourse(demBrg.doubleValue());

        // remember the course
        _demandedBearing = new Double(res.getCourse());

        // indicate we are now on the second one
        _thisLeg++;

      }
      else
      {
        // we have finished the manoeuvre, reset ourselves and return null,
        // so that another handler can look after it
        _demandedBearing = null;

        // leave res as-is
      }
    }
    return res;
  }


  /**
   * reset this decision model
   */
  public void restart()
  {
    _demandedBearing = null;
    _lastClearance = -1;
  }


  /**
   * indicate to this model that its execution has been interrupted by another (prob higher priority) model
   *
   * @param currentStatus
   */
  public void interrupted(Status currentStatus)
  {
    // ignore.
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

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new SternArcClearanceInfo(this);

    return _myEditor;
  }

  /**
   * the frequency at which we snort (millis)
   */
  public void setFrequency(final Duration newFrequency)
  {
    // check for void frequency
    if(newFrequency.getMillis() == 0)
      _frequency = null;
    else
      _frequency = newFrequency;
  }

  /**
   * the frequency at which we snort (millis)
   */
  public Duration getFrequency()
  {
    return _frequency;
  }

  /**
   * whether we apply a random factor to the time frequency
   */
  public void setRandomClearances(final boolean newRandomClearances)
  {
    _randomClearances = newRandomClearances;
  }

  /**
   * whether we apply a random factor to the time frequency
   */
  public boolean isRandomClearances()
  {
    return _randomClearances;
  }

  /** get the style of course change required
   * @see SternArcClearance.DOUBLE_LEG
   * @return String representing the style
   */ 
  public String getStyle()
  {
    return _myStyle;
  }

  /** set the style of course change required
   * @see SternArcClearance.DOUBLE_LEG    
   * @param myStyle the style to use
   */ 
  public void setStyle(String myStyle)
  {
    this._myStyle = myStyle;
  }

  /** get the coruse change to use
   *
   * @return
   */
  public double getCourseChange()
  {
    return _courseChange;
  }

  /** set the course change in the manouevres
   *
   * @param courseChange
   */
  public void setCourseChange(double courseChange)
  {
    this._courseChange = courseChange;
  }
  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: SternArcClearance.java,v $
   * Revision 1.1  2006/08/08 14:21:38  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:46  Ian.Mayo
   * First versions
   *
   * Revision 1.14  2004/10/07 12:50:26  Ian.Mayo
   * Wait until we're exactly on course before acknowledging course change complete, correct tests.
   *
   * Revision 1.13  2004/10/07 10:08:59  Ian.Mayo
   * Allow different types of SAC, put in some testing
   *
   * Revision 1.12  2004/10/06 15:32:39  Ian.Mayo
   * Insert tests, Handle instant SAC request not dependent on frequency
   *
   * Revision 1.11  2004/09/02 13:17:38  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   * <p/>
   * Revision 1.10  2004/08/31 15:28:03  Ian.Mayo
   * Polish off test refactoring, start Intercept behaviour
   * <p/>
   * Revision 1.9  2004/08/31 09:36:29  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.8  2004/08/26 16:27:10  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.7  2004/08/20 13:32:36  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.6  2004/08/17 14:22:12  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.5  2004/08/06 12:52:09  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.4  2004/08/06 11:14:31  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.3  2004/05/24 15:57:19  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.2  2003/11/05 09:19:58  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  static public class SternArcClearanceInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public SternArcClearanceInfo(final SternArcClearance data)
    {
      super(data, data.getName(), "SternArcClearance");
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
          prop("Frequency", "the frequency at which we perform clearance"),
          prop("RandomClearances", "whether we perform random clearances"),
          prop("Name", "the name of this Stern Arc Clearance model"),
          prop("CourseChange", "the amount of course change to conduct"),
          prop("Style", "the type of stern arc clearance to conduct"),
        };

        res[4].setPropertyEditorClass(SACStyleEditor.class);
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }


  public static class SACStyleEditor extends PropertyEditorSupport
  {
    ////////////////////////////////////////////////////
    // member objects
    ////////////////////////////////////////////////////
    private static String _stringTags[] =
      {
        SINGLE_LEG,
        DOUBLE_LEG
      };

    ////////////////////////////////////////////////////
    // member methods
    ////////////////////////////////////////////////////
    public String[] getTags()
    {
      return _stringTags;
    }

  }

  //////////////////////////////////////////////////
  // support testing
  //////////////////////////////////////////////////
  public static class SternArcClearanceTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new SternArcClearance();
    }


    public void testSingleShot()
    {
      SternArcClearance sac = new SternArcClearance(null, false, SternArcClearance.SINGLE_LEG, null);
      Status theStat = new Status(12, 1000);
      theStat.setLocation(new WorldLocation(0,0,0));
      theStat.setCourse(10);
      theStat.setSpeed(new  WorldSpeed(12,WorldSpeed.Kts));
      MovementCharacteristics theChars = ThreeDimMovementCharacteristics.getSampleChars(400);
      DemandedStatus theDemStat = new SimpleDemandedStatus(12, theStat);
      DetectionList theDets = null;
      ScenarioActivityMonitor theMonitor = null;
      long time = 3000;
      SimpleDemandedStatus res = (SimpleDemandedStatus) sac.decide(theStat, theChars, theDemStat, theDets, theMonitor, time += 5000);
      assertNotNull("Didn't return demanded status", res);

      // check we;re moving onto the correct course
      assertEquals("haven't adopted correct course", 10 + SternArcClearance.DEFAULT_TURN, res.getCourse(), 0.01);

      TurnAlgorithm alg = new TurnAlgorithm();

      while(res.getCourse() == 10 + SternArcClearance.DEFAULT_TURN)
      {
        theStat = alg.doTurn(theStat, res, theChars, time);
        res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
      }

      // check we're heading fwd again
      assertEquals("not back on straight course", 10, res.getCourse(), 0.01);

      while(res != null)
      {
        theStat = alg.doTurn(theStat, res, theChars, time);
        res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
      }

      assertNull("should have finished", res);
    }

    public void testDoubleTurn()
    {
      SternArcClearance sac = new SternArcClearance(null, false, SternArcClearance.DOUBLE_LEG, null);
      Status theStat = new Status(12, 1000);
      theStat.setLocation(new WorldLocation(0,0,0));
      theStat.setCourse(10);
      theStat.setSpeed(new  WorldSpeed(12,WorldSpeed.Kts));
      MovementCharacteristics theChars = ThreeDimMovementCharacteristics.getSampleChars(400);
      DemandedStatus theDemStat = new SimpleDemandedStatus(12, theStat);
      DetectionList theDets = null;
      ScenarioActivityMonitor theMonitor = null;
      long time = 3000;
      SimpleDemandedStatus res = (SimpleDemandedStatus) sac.decide(theStat, theChars, theDemStat, theDets, theMonitor, time += 5000);
      assertNotNull("Didn't return demanded status", res);

      // check we;re moving onto the correct course
      assertEquals("haven't adopted correct course", 10 + SternArcClearance.DEFAULT_TURN, res.getCourse(), 0.01);

      TurnAlgorithm alg = new TurnAlgorithm();

      while(res.getCourse() == 10 + SternArcClearance.DEFAULT_TURN)
      {
        theStat = alg.doTurn(theStat, res, theChars, time);
        res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
      }

      assertEquals("not back on original course", 10, res.getCourse(), 0.01);

      while(res.getCourse() == 10)
      {
        theStat = alg.doTurn(theStat, res, theChars, time);
        res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
      }

      assertEquals("not on second leg",  250, res.getCourse(), 0.01);

      while(res.getCourse() == 250)
      {
        theStat = alg.doTurn(theStat, res, theChars, time);
        res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
      }

      assertEquals("not back on course",  10, res.getCourse(), 0.01);

      while(res != null)
      {
        theStat = alg.doTurn(theStat, res, theChars, time);
        res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
      }

      assertNull("should have finished", res);
    }
  }

}