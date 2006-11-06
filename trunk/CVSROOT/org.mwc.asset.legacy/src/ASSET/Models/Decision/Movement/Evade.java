package ASSET.Models.Decision.Movement;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * Our implementation of evasion behaviour.  Our vessel will attempt to evade any contacts
 * with the specified behaviour
 */

public class Evade extends CoreDecision implements ASSET.Models.DecisionType, MWC.GUI.Editable, java.io.Serializable
{
  /**
   * ASSET.Models.Decision.Movement.Evade.UserControlInfo
   */

  /**
   * period to remain on evasion course for, before we drop back to last activity
   */
  private Duration _fleePeriod;

  /**
   * the speed to flee at
   */
  private double _fleeSpeed;

  /**
   * the Height to flee at
   */
  private double _fleeHeight;

  /**
   * indicator for when we don't have a finish fleeing time
   */
  private int INVALID_TIME = -1;
  /**
   * the time we will have finished fleeing at
   */
  private long _finishedFleeing = INVALID_TIME;

  /**
   * the course we want to flee on
   */
  private double _fleeCourse;

  /**
   * the type of target we are hunting
   */
  private TargetType _myTargetType = new TargetType();

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * a target we are currently evading
   */
  private Integer _myTarget;

  /**
   * <init>
   *
   * @param fleePeriod the period we evade for (millis)
   * @param fleeSpeed  the speed we evade at (kts)
   * @param fleeHeight the Height we evade at
   * @param target
   */
  public Evade(final Duration fleePeriod, final WorldSpeed fleeSpeed, final WorldDistance fleeHeight,
               TargetType target)
  {
    super("Evade");
    _fleePeriod = fleePeriod;
    _fleeSpeed = fleeSpeed.getValueIn(WorldSpeed.Kts);
    _fleeHeight = fleeHeight.getValueIn(WorldDistance.METRES);
    _myTargetType = target;
  }

  /**
   * decide
   *
   * @param status parameter for decide
   * @param time   parameter for decide
   * @return the returned ASSET.Participants.DemandedStatus
   */
  public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
                                                  ASSET.Models.Movement.MovementCharacteristics chars, ASSET.Participants.DemandedStatus demStatus, final ASSET.Models.Detection.DetectionList detections,
                                                  ASSET.Scenario.ScenarioActivityMonitor monitor,
                                                  final long time)
  {

    SimpleDemandedStatus res = null;

    // are we still fleeing?
    if (time <= _finishedFleeing)
    {
      // repeat instructions to head off on flee course
      res = new SimpleDemandedStatus(time, status);
      res.setCourse(_fleeCourse);
      res.setHeight(_fleeHeight);
      res.setSpeed(_fleeSpeed);
    }
    else
    {
      // back to normal handling
      // do we have any detections?
      if (detections != null)
      {
        // are there any matching detections
        DetectionList matches = detections.getDetectionsOf(_myTargetType);

        if (matches != null)
        {

          // find the nearest detection (which may not be the one we are currently tracking
          DetectionEvent nearestDet = matches.getNearestDetection();

          // are we already evading?
          if (_myTarget != null)
          {
            if (nearestDet.getTarget() == _myTarget.intValue())
            {
              // no, we're off evading some other target now
              _finishedFleeing = INVALID_TIME;
            }

          }

          // get bearing to first detection
          final int len = detections.size();
          if (len > 0)
          {
            THROUGH_DETECTIONS: for (int i = 0; i < len; i++)
            {

              final ASSET.Models.Detection.DetectionEvent de = detections.getDetection(i);
              final Float brg = de.getBearing();
              if (brg != null)
              {
                // is this of our target type
                final ASSET.Participants.Category thisTarget = de.getTargetType();
                if (_myTargetType.matches(thisTarget))
                {
                  // yes, continue with this loop
                }
                else
                {
                  // drop out to the next detections
                  continue THROUGH_DETECTIONS;
                }

                res = new SimpleDemandedStatus(time, status);
                res.setCourse(brg.doubleValue() + 180);
                res.setHeight(_fleeHeight);
                res.setSpeed(_fleeSpeed);
                _fleeCourse = res.getCourse();

                super.setLastActivity(de.toString());

                // calculate the flee time
                _finishedFleeing = time + (long) _fleePeriod.getValueIn(Duration.MILLISECONDS);
              } // whether there is a bearing
            }  // loop through the detections
          } // if there are any detections
        }
      }

      else
      {
        // no detections, reset our variables
        _finishedFleeing = -1;
        _fleeCourse = -1;
      }
    }

    return res;
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    // no detections, reset our variables
    _finishedFleeing = -1;
    _fleeCourse = -1;
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
   * the period to flee for (millis)
   */
  public void setFleePeriod(final Duration newFleePeriod)
  {
    _fleePeriod = newFleePeriod;
  }

  /**
   * the period to flee for (millis)
   */
  public Duration getFleePeriod()
  {
    return _fleePeriod;
  }

  /**
   * the Height to flee at
   */
  public void setFleeHeight(final WorldDistance newFleeHeight)
  {
    _fleeHeight = newFleeHeight.getValueIn(WorldDistance.METRES);
  }

  /**
   * the Height to flee at
   */
  public WorldDistance getFleeHeight()
  {
    return new WorldDistance(_fleeHeight, WorldDistance.METRES);
  }

  /**
   * the speed to flee at (m/sec)
   */
  public void setFleeSpeed(final WorldSpeed newFleeSpeed)
  {
    _fleeSpeed = newFleeSpeed.getValueIn(WorldSpeed.M_sec);
  }

  /**
   * the speed to flee at (m.sec)
   */
  public WorldSpeed getFleeSpeed()
  {
    return new WorldSpeed(_fleeSpeed, WorldSpeed.M_sec);
  }


  /**
   * setTargetToEvade
   *
   * @param target parameter for setTargetToEvade
   */
  public void setTargetType(final TargetType target)
  {
    _myTargetType = target;
  }

  /**
   * getTargetToEvade
   *
   * @return the returned TargetType
   */
  public TargetType getTargetType()
  {
    return _myTargetType;
  }


  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log$
   * Revision 1.3  2006-11-06 16:12:15  Ian.Mayo
   * Re-instate pre-CQD version
   *
   * Revision 1.1  2006/08/08 14:21:27  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:35  Ian.Mayo
   * First versions
   *
   * Revision 1.16  2004/10/21 10:27:53  Ian.Mayo
   * Reinstate fleeing target for a set period
   *
   * Revision 1.15  2004/09/07 15:14:16  Ian.Mayo
   * Part way through refresh to incorporate avoid behaviour
   * <p/>
   * Revision 1.14  2004/09/02 13:17:23  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   * <p/>
   * Revision 1.13  2004/08/31 15:27:57  Ian.Mayo
   * Polish off test refactoring, start Intercept behaviour
   * <p/>
   * Revision 1.12  2004/08/31 09:36:02  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.11  2004/08/26 16:26:49  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.10  2004/08/20 13:32:14  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.9  2004/08/17 14:21:54  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.8  2004/08/06 12:51:52  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.7  2004/08/06 11:14:14  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.6  2004/05/24 15:46:31  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.5  2004/02/18 08:47:12  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.3  2003/11/05 09:20:08  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }

  //////////////////////////////////////////////////////////////////////
  // editable data
  //////////////////////////////////////////////////////////////////////
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
      _myEditor = new EvadeInfo(this);

    return _myEditor;
  }

  static public class EvadeInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public EvadeInfo(final Evade data)
    {
      super(data, data.getName(), "Evade");
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
          prop("TargetType", "the type of vessel this model is evading"),
          prop("FleeSpeed", "the speed this participant flees at (kts)"),
          prop("FleeHeight", "the Height this participant flees at(m)"),
          prop("FleePeriod", "the period this participant flees for (minutes)"),
          prop("Name", "the name of this evasion model"),
        };
        //        res[0].setPropertyEditorClass(ASSET.GUI.Editors.TargetTypeEditor.class);
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
  public static class EvadeTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new Evade(null, new WorldSpeed(12, WorldSpeed.M_sec), new WorldDistance(12, WorldDistance.METRES), null);
    }


  }
}