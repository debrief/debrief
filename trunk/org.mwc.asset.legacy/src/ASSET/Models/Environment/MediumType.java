/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 12-Jul-02
 * Time: 11:18:51
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Environment;

import MWC.GenericData.WorldLocation;

public interface MediumType
{
  /** return the name of this medium
   *
   */
  public String getName();


  /** get the loss in this medium between the indicated points
   * @param origin the source of the energy
   * @param destination the destination we're looking at
   * @return the resultant energy at the destination
   *
   */
  public double getLossBetween(WorldLocation origin,
                               WorldLocation destination);

  /** get the loss in this medium between the indicated points
   * @param origin the source of the energy
   * @param destination the destination we're looking at
   * @param sourceLevel the amount of noise radiated from the source
   * @return the resultant energy at the destination
   *
   */
  public double getResultantEnergyAt(WorldLocation origin,
                                     WorldLocation destination,
                                     double sourceLevel);



  /** get the background noise in the indicated bearing from the indicated location
   *
   * @param origin where we are at the moment
   * @param brg_degs the direction we're looking at
   * @return the background noise
   */
  public double getBkgndNoise(WorldLocation origin, double brg_degs);

}
