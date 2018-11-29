package org.mwc.debrief.lite.map;


import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JSplitPane;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.geotools.swing.MapLayerTable;
import org.geotools.swing.action.InfoAction;
import org.geotools.swing.action.NoToolAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.geotools.swing.data.JFileDataStoreChooser;
//import org.geotools.swing.tool.ScrollWheelTool;

import MWC.GUI.Tools.Swing.SwingToolbar;

/**
 * 
 * @author Unni Mana <unnivm@gmail.com>
 *
 */
public class GeoToolMapRenderer extends MapRenderer {

	private JMapPane mapPane;
	private MapContent mapComponent;
	
	@Override
	public void loadMapContent() {
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
	      if (file == null) {
	          return;
	      }

	      FileDataStore store;
	      SimpleFeatureSource featureSource = null;
		try {
			store = FileDataStoreFinder.getDataStore(file);
			featureSource = store.getFeatureSource();
		} catch (IOException e) {
			e.printStackTrace();
		}

	      // Create a map content and add our shape file to it
	      mapComponent = new MapContent();
	      mapComponent.setTitle("Debrief Lite");

	      Style style = SLD.createSimpleStyle(featureSource.getSchema());
	      Layer layer = new FeatureLayer(featureSource, style);
	      mapComponent.addLayer(layer);

	}

	@Override
	public void createMapLayout() {
	    mapPane = new JMapPane();
		mapPane.setRenderer(new StreamingRenderer());
		mapPane.setMapContent(mapComponent);
	    
		MapLayerTable mapLayerTable = new MapLayerTable(mapPane);
		mapLayerTable.setVisible(false);
		mapLayerTable.setPreferredSize(new Dimension(200, 400));
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, mapLayerTable, mapPane);
	}

	@Override
	public void addMapTool(SwingToolbar theToolbar) {
		
  	      JButton btn;
	      ButtonGroup cursorToolGrp = new ButtonGroup();
	   
	      System.out.println("tool bar " + theToolbar);
	   //   mapPane.addMouseListener(new ScrollWheelTool(mapPane));

	      ///// no action
	      btn = new JButton(new NoToolAction(mapPane));
	      cursorToolGrp.add(btn);
	      theToolbar.add(btn);
	      
	      ////// zoom in
	      btn = new JButton(new ZoomInAction(mapPane));
	      cursorToolGrp.add(btn);
	      theToolbar.add(btn);
	      
	      ////// zoom out
	      btn = new JButton(new ZoomOutAction(mapPane));
	      cursorToolGrp.add(btn);
	      theToolbar.add(btn);
	      
	      theToolbar.addSeparator();
	      
	      //// pan action
	      btn = new JButton(new PanAction(mapPane));
	      cursorToolGrp.add(btn);
	      theToolbar.add(btn);
	      
	      //// info action
	      btn = new JButton(new InfoAction(mapPane));
	      cursorToolGrp.add(btn);
	      theToolbar.add(btn);
	      
	      //// reset action
	      btn = new JButton(new ResetAction(mapPane));
	      cursorToolGrp.add(btn);
	      theToolbar.add(btn);
	}

	/**
	 * return map component
	 * @return
	 */
	public MapContent getMapComponent() {
		return mapComponent;
	}
	
}
