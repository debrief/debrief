
package Debrief.Wrappers;

import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GenericData.WatchableList;

public interface ISecondaryTrack extends WatchableList, HasEditables {

	/** get the legs
	 * 
	 * @return
	 */
	Enumeration<Editable> segments();

	/** specify if this track should interpolate points when
	 * receiving a "getNEarestTo" call
	 * 
	 * @param val yes/no
	 */
	public abstract void setInterpolatePoints(final boolean val);

	/** is this track configured to interpolate points?
	 * 
	 * @return yes/no
	 */
	public abstract boolean getInterpolatePoints();

}
