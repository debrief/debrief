package org.mwc.debrief.lite.map;


import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import MWC.GUI.Tools.Swing.SwingToolbar;

public class MapBuilder {

	private MapRenderer mapRenderer;
	
	/** enable map tool bar **/
	private boolean enable = true;
	
	private SwingToolbar theToolbar;
	
	/**
	 * sets a map renderer object based on the map API
	 * 
	 * @param renderer
	 * @return
	 */
	public MapBuilder setMapRenderer(MapRenderer mapRenderer) {
		this.mapRenderer = mapRenderer;
		
		return this;
	}
	
	/**
	 * enable or disable toolbar
	 * 
	 * @param enable
	 * @return
	 */
	public MapBuilder enableToolbar(boolean enable) {
		this.enable = enable;
		return this;
	}
	
	/**
	 * 
	 * @param theoolbar
	 */
	public MapBuilder setToolbar(SwingToolbar theoolbar) {
		this.theToolbar = theToolbar;
		return this;
	}
	
	
	/**
	 * builds the map pane with map content in it.
	 */
	public JScrollPane build() {
		JPanel panel = new JPanel();
	    panel.setLayout(new BorderLayout());
	    panel.add(mapRenderer.getPane(), BorderLayout.NORTH);
	    JScrollPane scrPane = new JScrollPane(panel);
	    
	 //   mapRenderer.addMapTool(theToolbar);
	    return scrPane;	
	}
	
}
