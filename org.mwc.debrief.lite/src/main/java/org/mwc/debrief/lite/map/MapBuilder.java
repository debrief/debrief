/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.map;


import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import MWC.GUI.Tools.Swing.SwingToolbar;

public class MapBuilder {

	private MapRenderer mapRenderer;
	
	/** enable map tool bar **/
	@SuppressWarnings("unused")
  private boolean enable = true;
	
	@SuppressWarnings("unused")
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
	public MapBuilder setToolbar(SwingToolbar theToolbar) {
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
