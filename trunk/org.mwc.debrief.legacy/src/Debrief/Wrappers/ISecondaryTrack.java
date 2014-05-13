package Debrief.Wrappers;

import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;

public interface ISecondaryTrack extends WatchableList {

	/** get the legs
	 * 
	 * @return
	 */
	Enumeration<Editable> segments();

}
