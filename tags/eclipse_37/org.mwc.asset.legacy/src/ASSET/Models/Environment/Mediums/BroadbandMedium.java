/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 12-Jul-02
 * Time: 11:14:55
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Environment.Mediums;

import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class BroadbandMedium implements ASSET.Models.Environment.MediumType
{


  /** the absorption coefficient in this environment
   *
   */
  final private static double ABSORPTION_COEFFICIENT_DB_PER_KY = 0.08;

  /** get the name of this environment
   *
   */
  public String getName()
  {
    return "Broadband";
  }

  /** get the loss in this medium between the indicated points
   * @param origin the source of the energy
   * @param destination the destination we're looking at
   * @return the resultant energy at the destination
   * @see <a href="file:///D:/Dev/Asset/docs/doc_set/html/asset_mod_mediums.htm#mod_medium_bb">ASSET Mod Guide</a>
   *
   */
  public double getLossBetween(final WorldLocation origin,
                               final WorldLocation destination)
  {
    double res = 0;

    final WorldVector separation = destination.subtract(origin);
    final double rng_degs = separation.getRange();
    // convert to yds
    final double rng_yds = MWC.Algorithms.Conversions.Degs2m(rng_degs);

    // produce a transmission loss
    if(rng_yds == 0)
    {
      // zero range is a special occurence, since it doesn't get through our log calcs
      res = 0;
    }
    else
    {
      // first sort out the spreading
      final double spreading = 20 * Math.log(rng_yds) / Math.log(10);

      // convert range to yds
      final double rng_kyds = rng_yds / 1000;

      // now sort out the absorption
      final double absorption = ABSORPTION_COEFFICIENT_DB_PER_KY * rng_kyds;

                          // was 0.453
      res = spreading + absorption;
    }

    return res;
  }

  /** get the loss in this medium between the indicated points
   * @param origin the source of the energy
   * @param destination the destination we're looking at
   * @param sourceLevel the amount of noise radiated from the source
   * @return the resultant energy at the destination
   *
   */
  public double getResultantEnergyAt(final WorldLocation origin,
                                     final WorldLocation destination,
                                     final double sourceLevel)
  {
    // calculate the loss
    final double loss = getLossBetween(origin, destination);

    // calculate the resultant energy
    final double res = sourceLevel - loss;

    return res;
  }

  /** get the background noise in the indicated bearing from the indicated location
   *
   * @param origin where we are at the moment
   * @param brg_degs the direction we're looking at
   * @return the background noise
   */
  public double getBkgndNoise(WorldLocation origin, double brg_degs)
  {
    // hey, let's just return 65dbs
    return 35;
  }

}
