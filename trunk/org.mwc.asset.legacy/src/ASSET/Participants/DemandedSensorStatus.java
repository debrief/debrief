/*
 * Created by Ian Mayo, PlanetMayo Ltd.
 * User: Ian.Mayo
 * Date: 01-Nov-2002
 * Time: 12:14:36
 */
package ASSET.Participants;

/** class which extends DemandedStatus by adding optional sensor lineup change
 *
 */

public class DemandedSensorStatus
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /** the medium this status should apply to
   *
   */
  private int _medium;

  /** whether to switch on or off
   *
   */
  private boolean _switchOn;

  /** the depth to deploy to (optional)
   *
   */
  private Double _deployDepth = null;

  ////////////////////////////////////////////////////
  // member constructor
  ////////////////////////////////////////////////////

  /** create new demanded status which also changes sensor lineupo
   *
   * @param sensorMedium the medium for this sensor(s)
   * @param switchOn whether to switch on or off
   */
  public DemandedSensorStatus(final int sensorMedium,
                              final boolean switchOn)
  {
    _medium = sensorMedium;
    _switchOn = switchOn;
  }

  ////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////

  public int getMedium()
  {
    return _medium;
  }

  public boolean getSwitchOn()
  {
    return _switchOn;
  }

  public Double getDeployDepth()
  {
    return _deployDepth;
  }

  public void setDeployDepth(Double deployDepth)
  {
    this._deployDepth = deployDepth;
  }


}
