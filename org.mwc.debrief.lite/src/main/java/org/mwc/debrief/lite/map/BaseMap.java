package org.mwc.debrief.lite.map;


import MWC.GUI.Tools.Swing.SwingToolbar;

/**
 * 
 * @author Unni Mana <unnivm@gmail.com>
 *
 * This class is used to load different type of map implementation in the real world.
 * The current implementation the map is based on GeoTools API. Need to implement createMapPane()
 * method. This method ideally accepts a map content object and creates a ScrollPane object with map content so that it 
 * can be embedded in any layout.
 */
public interface BaseMap {
	
	/**
	 * loads map content
	 */
	public void loadMapContent();
	
	/**
	 * creates a JSplitPane from the given map content.
	 * 
	 * @return
	 */
	public void createMapLayout();
	
	/**
	 * adds a map control tool 
	 */
	public void addMapTool(SwingToolbar toolbar);
	
	
}
