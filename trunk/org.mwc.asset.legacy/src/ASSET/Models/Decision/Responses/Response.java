/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 25-Sep-2002
 * Time: 11:25:55
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Decision.Responses;

import ASSET.Participants.*;
import ASSET.Models.Detection.DetectionList;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;

public interface Response extends Editable
{
  /**
   *
   * @param conditionResult the object returned from the condition object
   * @param status the current status of the participant
   * @param detections the current set of detections
   * @param monitor the monitor object listening out for significant activity
   * @param time the current time step
   * @return the DemandedStatus for this vessel
   */
  public DemandedStatus direct(Object conditionResult,
                               ASSET.Participants.Status status,
                               DemandedStatus demStat, ASSET.Models.Detection.DetectionList detections,
                               ASSET.Scenario.ScenarioActivityMonitor monitor,
                               long time);

  /** reset the local data
   *
   */
  public void restart();

  ////////////////////////////////////////////////////
  // accessors
  ////////////////////////////////////////////////////

  /** set the name of this response
   *
   * @return the name
   */
  String getName();

  /** get the name of this response
   *
   * @param myName the name
   */
  void setName(String myName);

  /** get the description of what we're doing
   *
   */
  public String getActivity();


  ////////////////////////////////////////////////////
  // basic implementation of response
  ////////////////////////////////////////////////////
  abstract static public class CoreResponse implements Response, MWC.GUI.Editable
  {
    /** the name of this response object
     *
     */
    private String _myName = null;


    /** a local copy of our editable object
     *
     */
    MWC.GUI.Editable.EditorType _myEditor = null;

    ////////////////////////////////////////////////////
    // response object
    ////////////////////////////////////////////////////

    /** produce the required response
     *
     * @param conditionResult the result from the condition test
     * @param status the current status
     * @param detections the current set of detections
     * @param monitor the object monitoring us(for add/remove participants, detonations, etc)
     * @param time the current time
     * @return
     * @see ASSET.Models.Decision.Conditions.Condition
     */
    abstract public DemandedStatus direct(Object conditionResult,
                                          Status status,
                                          DemandedStatus demStat, DetectionList detections,
                                          ScenarioActivityMonitor monitor,
                                          long time);


    ////////////////////////////////////////////////////
    // accessors
    ////////////////////////////////////////////////////
    public String getName()
    {
      return _myName;
    }

    public void setName(final String myName)
    {
      this._myName = myName;
    }

    ////////////////////////////////////////////////////
    // editor support
    ////////////////////////////////////////////////////

  }

}
