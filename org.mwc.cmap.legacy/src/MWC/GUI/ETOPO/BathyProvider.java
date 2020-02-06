
package MWC.GUI.ETOPO;

import MWC.GenericData.WorldLocation;

public interface BathyProvider
{
  /** provide the delta for the data
   *
   */
  public double getGridDelta();

  /** provide the depth in metres at the indicated location
   *
   */
  public double getDepthAt(WorldLocation loc);

  /** whether the data has been loaded yet
   *
   */
  public boolean isDataLoaded();
}
