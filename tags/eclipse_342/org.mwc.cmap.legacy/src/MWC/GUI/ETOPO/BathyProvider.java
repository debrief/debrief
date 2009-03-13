/*
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 23-May-02
 * Time: 14:27:51
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
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
