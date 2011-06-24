package ASSET.Models.Movement;

import MWC.GenericData.WorldSpeed;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 11-Sep-2003
 * Time: 15:10:50
 * Log:  
 *  $Log: ClimbRateCharacteristics.java,v $
 *  Revision 1.1  2006/08/08 14:21:46  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:54  Ian.Mayo
 *  First versions
 *
 *  Revision 1.3  2004/05/24 15:08:58  Ian.Mayo
 *  Commit changes conducted at home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:53  ian
 *  no message
 *
 *  Revision 1.2  2003/09/19 07:39:39  Ian.Mayo
 *  New manoeuvering characteristics
 *
 *  Revision 1.1  2003/09/12 07:39:51  Ian.Mayo
 *  Initial implementation
 *
 *
 */

/** interface describing sets of characteristics which provide climb and dive rates
 *
 */
public interface ClimbRateCharacteristics
{
  /** get the default speed at which this vehicle travels when climbing (m/sec)
   *
   * @return
   */
  WorldSpeed getDefaultClimbSpeed();

  /** get the default speed at which this vehicle travels when climbing (m/sec)
   *
   * @param defaultClimbSpeed_m_sec climb speed (m/sec)
   */
  void setDefaultClimbSpeed(WorldSpeed defaultClimbSpeed_m_sec);

  /** get the default speed at which this vehicle travels when diving (m/sec)
   *
   * @return
   */
  WorldSpeed getDefaultDiveSpeed();

  /** get the default speed at which this vehicle travels when diving (m/sec)
   *
   * @param defaultDiveSpeed_m_sec default dive speed (m/sec)
   */
  void setDefaultDiveSpeed(WorldSpeed defaultDiveSpeed_m_sec);
}
