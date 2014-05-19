package ASSET.Models.Sensor;

import MWC.GenericData.WorldLocation;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:
 * $Log: DeployableSensor.java,v $
 * Revision 1.1  2006/08/08 14:21:59  Ian.Mayo
 * Second import
 *
 * Revision 1.1  2006/08/07 12:26:08  Ian.Mayo
 * First versions
 *
 * Revision 1.2  2004/05/24 15:08:37  Ian.Mayo
 * Commit changes conducted at home
 *
 * Revision 1.1.1.1  2004/03/04 20:30:54  ian
 * no message
 *
 * Revision 1.1  2003/09/03 14:01:12  Ian.Mayo
 * Initial implementation
 *
 *
 */
public interface DeployableSensor
{
  /** retrieve the current location of the sensor
   *
   */
  public WorldLocation getLocation(WorldLocation hostLocation);

  /** retrieve the host id
   *
   */
  public int getHostId();
}
