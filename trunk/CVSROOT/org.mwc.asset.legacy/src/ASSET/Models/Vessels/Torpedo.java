/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 14-Jun-02
 * Time: 15:24:24
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Vessels;

import ASSET.Models.Environment.EnvironmentType;

public class Torpedo extends SSN
{
  public Torpedo(final int id)
  {
    super(id);
  }

  public Torpedo(final int id, final ASSET.Participants.Status status, final ASSET.Participants.DemandedStatus demStatus, final String name)
  {
    super(id, status, demStatus, name);
  }

  public void initialise()
  {
    _radiatedChars.add(EnvironmentType.BROADBAND_PASSIVE, new ASSET.Models.Mediums.BroadbandRadNoise(170.78));
    if(getStatus() != null)
      this.getStatus().setFuelLevel(100);
  }
}
