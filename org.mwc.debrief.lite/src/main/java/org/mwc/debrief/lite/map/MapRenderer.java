package org.mwc.debrief.lite.map;


import javax.swing.JSplitPane;

/**
 * 
 * @author Unni Mana <unnivm@gmail.com>
 *
 */
public abstract class MapRenderer implements BaseMap {

	protected JSplitPane splitPane;
	
	protected JSplitPane getPane() {
		return splitPane;
	}
	
}
