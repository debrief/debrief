package ASSET.Models.Sensor;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 14-Oct-2004
 * Time: 13:51:35
 * To change this template use File | Settings | File Templates.
 */
//////////////////////////////////////////////////
// interface used for objects for whom detections can be listened to
//////////////////////////////////////////////////
public interface SensorDataProvider
{

  /** somebody wants to stop listening to us
   *
   * @param listener
   */
  public void addParticipantDetectedListener(ASSET.Participants.ParticipantDetectedListener listener);

  /** somebody wants to start listening to us
   *
   * @param listener
   */
  public void removeParticipantDetectedListener(ASSET.Participants.ParticipantDetectedListener listener);


  /** find the list of all detections for this participant since the start of the scenario
   *
   */
  ASSET.Models.Detection.DetectionList getAllDetections();
}
