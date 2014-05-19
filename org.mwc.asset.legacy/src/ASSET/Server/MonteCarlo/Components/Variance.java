package ASSET.Server.MonteCarlo.Components;

import org.w3c.dom.Document;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 22-Sep-2003
 * Time: 14:57:59
 * Log:  
 *  $Log: Variance.java,v $
 *  Revision 1.1  2006/08/08 14:22:20  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:26  Ian.Mayo
 *  First versions
 *
 *  Revision 1.2  2004/05/24 16:21:17  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:56  ian
 *  no message
 *
 *  Revision 1.1  2003/09/22 15:50:37  Ian.Mayo
 *  New implementations
 *
 *
 */

/** an individual variance to apply
 *
 */
abstract public class Variance
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /** the description of this attribute variance
   *
   */
  private String _myName;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /** create a new permutation of the supplied scenario
   *
   * @param existingScenario
   * @return the new scenario
   */
  abstract public String getNewPermutation(Document existingScenario);

  /** create a new permutation of the indicated participant within the supplied scenario
   *
   * @param participantName
   * @param existingScenario
   * @return the new scenaroio
   */
  abstract public String getNewPermutation(String participantName,
                                           Document existingScenario);


  /** return a description of this variance
   *
   */
  public String getDescription()
  {
    return "Attribute variance:" + _myName;
  }

  //////////////////////////////////////////////////
  // getter/setters
  //////////////////////////////////////////////////

  public String getName()
  {
    return _myName;
  }

  public void setName(String myName)
  {
    this._myName = myName;
  }

}
