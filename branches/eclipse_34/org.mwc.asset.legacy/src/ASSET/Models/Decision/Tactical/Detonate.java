package ASSET.Models.Decision.Tactical;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

/**
 * Our implementation of detonation.  A weapon (or vessel) may detonate at an indicate range
 * from an indicated type of target
 */

public class Detonate extends CoreDecision implements MWC.GUI.Editable, java.io.Serializable
{
  /**
   * the range at which we detonate (yds)
   */
  private WorldDistance _myRange;

  /**
   * the power of the detonation
   */
  private double _myPower;

  /**
   * the type of target detonate next to
   */
  private TargetType _myTarget = new TargetType();

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /****************************************************
   * constructor
   ***************************************************/

  /**
   * <init>
   *
   * @param myTarget      the type of target we detonate next to
   * @param detonateRange the range at which we detonate (yds)
   * @param thePower      the strength of explosive in the detonation
   */
  public Detonate(final TargetType myTarget,
                  final WorldDistance detonateRange,
                  final double thePower)
  {
    super("Detonate");
    _myTarget = myTarget;
    _myRange = detonateRange;
    _myPower = thePower;
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
                                                  final ScenarioActivityMonitor monitor,
                                                  long time)
  {

    final ASSET.Participants.DemandedStatus res = null;

    DetectionEvent theTarget = null;

    String activity = "";

    // do we have any detections?
    if (detections != null)
    {
      // get bearing to first detection
      final int len = detections.size();
      if (len > 0)
      {
        THROUGH_DETECTIONS: for (int i = 0; i < len; i++)
        {

          final ASSET.Models.Detection.DetectionEvent de = detections.getDetection(i);
          // do we know the range
          final WorldDistance rng = de.getRange();
          if (rng != null)
          {
            // is this of our target type
            final ASSET.Participants.Category thisTarget = de.getTargetType();
            if (_myTarget.matches(thisTarget))
            {
              // ok, is it close enough
              final double theRng = rng.getValueIn(WorldDistance.YARDS);
              final double myRngYds = _myRange.getValueIn(WorldDistance.YARDS);
              if (theRng < myRngYds)
              {
                // ok, detonate
                // remember the details of the detection
                activity = "Detonation following:" + de.toString();

                // and calculate what we need to do
                theTarget = de;

                // drop out of this loop, now that we have a target
                break;
              }
            }
            else
            {
              // drop out to the next detections
              continue THROUGH_DETECTIONS;
            }

          } // if we know the bearing
        } // looping through the detections
      } // if we have any detections
    } // if the detections object was received

    // did we find a target?
    if (theTarget != null)
    {
      // do the detonation!!!
      if (monitor != null)
      {
        monitor.detonationAt(status.getId(), status.getLocation(), _myPower);
      }
    }

    super.setLastActivity(activity);

    return res;
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    // no detections, reset our variables
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
   * the range at which to explode
   */
  public void setDetonationRange(final WorldDistance newRange)
  {
    _myRange = newRange;
  }

  /**
   * the range at which to explode
   */
  public WorldDistance getDetonationRange()
  {
    return _myRange;
  }

  /**
   * the range at which to explode
   */
  public void setPower(final double newPower)
  {
    _myPower = newPower;
  }

  /**
   * the range at which to explode
   */
  public double getPower()
  {
    return _myPower;
  }


  /**
   * setTargetToEvade
   *
   * @param target parameter for setTargetToEvade
   */
  public void setTargetType(final TargetType target)
  {
    _myTarget = target;
  }

  /**
   * getTargetToEvade
   *
   * @return the returned TargetType
   */
  public TargetType getTargetType()
  {
    return _myTarget;
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
      _myEditor = new DetonateInfo(this);

    return _myEditor;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Detonate.java,v $
   * Revision 1.1  2006/08/08 14:21:32  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:41  Ian.Mayo
   * First versions
   *
   * Revision 1.13  2004/09/02 13:17:32  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   *
   * Revision 1.12  2004/08/31 15:28:00  Ian.Mayo
   * Polish off test refactoring, start Intercept behaviour
   * <p/>
   * Revision 1.11  2004/08/31 09:36:18  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.10  2004/08/26 16:27:01  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.9  2004/08/20 13:32:25  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.8  2004/08/17 14:22:02  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.7  2004/08/06 12:51:59  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.6  2004/08/06 11:14:21  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.5  2004/05/24 15:57:06  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.4  2004/02/18 08:48:08  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.2  2003/11/05 09:19:53  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  static public class DetonateInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public DetonateInfo(final Detonate data)
    {
      super(data, data.getName(), "Detonate");
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
          prop("DetonationRange", "the range at which we detonate(yds)"),
          prop("Name", "the name of this detonation model"),
          prop("Power", "the power of this detonation"),
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
  // testing support
  //////////////////////////////////////////////////
  public static class DetonateTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new Detonate(null, null, 0);
    }
  }
}