package ASSET.Server.MonteCarlo.Components;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 22-Sep-2003
 * Time: 14:59:10
 * Log:  
 *  $Log: ParticipantVariance.java,v $
 *  Revision 1.1  2006/08/08 14:22:20  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:26  Ian.Mayo
 *  First versions
 *
 *  Revision 1.2  2004/05/24 16:21:16  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:56  ian
 *  no message
 *
 *  Revision 1.1  2003/09/22 15:50:35  Ian.Mayo
 *  New implementations
 *
 *
 */


/** the variance to apply to a particular participant
 *
 */
public class ParticipantVariance
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /** the variances to apply
   *
   */
  private VarianceList _theVariance;

  /** the particpant to vary
   *
   */
  private String _theIdentifier;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /** create this participant variance
   *
   * @param theIdentifier the name of the participant to edit
   */
  public ParticipantVariance(String theIdentifier)
  {
    this._theIdentifier = theIdentifier;
  }

  /////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////


  /** produce a mutation of the existing scenario, returning as a string
   *
   * @param existingScenario a String containing the existing scenario
   * @return a new permutation of the file
   */
  public String getNewPermutation(String existingScenario)
  {
    String res = null;

    return res;
  }

  /** retrieve list of variances
   *
   * @return
   */
  public VarianceList getVariances()
  {
    return _theVariance;
  }

  /** set list of variances
   *
   * @param theVariances
   */
  public void setVariances(VarianceList theVariances)
  {
    this._theVariance = theVariances;
  }

  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
}
