/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 25-Sep-2002
 * Time: 09:38:48
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Decision.Conditions;

import ASSET.Participants.Status;
import ASSET.Models.Detection.DetectionList;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;

/**
 * interface for behaviour objects which act as conditions (as part of a composite structure)
 *
 */
public interface Condition extends Editable
{
  /** decide the course of action to take, or return null to no be used
   * @param status the current status of the participant
   * @param detections the current list of detections for this participant
   * @param time the time this decision was made
   * @return an object related to the type of condition reached (on success),
   *       or null to signify failure
   */
   public Object test(ASSET.Participants.Status status,
                                                 ASSET.Models.Detection.DetectionList detections,
                                                 long time,
                                                 ScenarioActivityMonitor monitor);

  /** restart the condition
   *
   */
  public void restart();

  /**
   *
   * @return the name of this condition
   */
  public String getName();

  /**
   *
   * @param name the new name for this condition
   */
  public void setName(String name);


  /** the core content of a condition
   *
   */
  static abstract public class CoreCondition implements Condition, MWC.GUI.Editable
  {
    ////////////////////////////////////////////////////
    // member objects
    ////////////////////////////////////////////////////

    /** a local copy of our editable object
     *
     */
    MWC.GUI.Editable.EditorType _myEditor = null;

    /** the name of this condition
     *
     */
    private String _myName = null;

    ////////////////////////////////////////////////////
    // constructor
    ////////////////////////////////////////////////////
    /**
     *
     * @param name then name of this condition
     */
    CoreCondition(final String name)
    {
      _myName = name;
    }

    ////////////////////////////////////////////////////
    // conditions
    ////////////////////////////////////////////////////

    /** the condition we want to test
     *
     * @param status current ownship status
     * @param detections detections produced by the current sensor fit
     * @param time the current time
     * @param monitor the scenario we are working from
     * @return either null (for failed) or an object containing details of the success
     */
    abstract public Object test(Status status,
                       DetectionList detections,
                       long time, ScenarioActivityMonitor monitor);

    ////////////////////////////////////////////////////
    // accessor/setters
    ////////////////////////////////////////////////////

    final public String getName()
    {
      return _myName;
    }

    final public void setName(final String name)
    {
      _myName = name;
    }
  }

}
