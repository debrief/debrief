/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
