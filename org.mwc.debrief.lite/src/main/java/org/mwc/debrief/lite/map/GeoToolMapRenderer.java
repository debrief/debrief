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

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JSplitPane;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
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
import org.opengis.feature.simple.SimpleFeatureType;
//import org.geotools.swing.tool.ScrollWheelTool;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import MWC.GUI.Tools.Swing.SwingToolbar;

/**
 *
 * @author Unni Mana <unnivm@gmail.com>
 *
 */
public class GeoToolMapRenderer implements BaseMap {

  protected JSplitPane splitPane;
  
  protected JSplitPane getPane() {
    return splitPane;
  }
  

	private JMapPane mapPane;
	private MapContent mapComponent;

	private Graphics graphics;

	private SimpleFeatureSource featureSource;

  private List<MapRenderer> _myRenderers = new ArrayList<MapRenderer>();

  
	@Override
	public void addMapTool(final SwingToolbar theToolbar) {

		JButton btn;
		final ButtonGroup cursorToolGrp = new ButtonGroup();

		// mapPane.addMouseListener(new ScrollWheelTool(mapPane));

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

	public static interface MapRenderer
	{
	  public void paint(final Graphics gc);
	}
	
	public void addRenderer(MapRenderer renderer)
	{
	  _myRenderers.add(renderer);
	}
	
	private void paintEvent(Graphics arg0)
	{
	  for(MapRenderer r: _myRenderers)
	  {
	    r.paint(arg0);
	  }
	}

	@Override
	public void createMapLayout() {
		mapPane = new JMapPane() {

			/**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
			protected void paintComponent(Graphics arg0) {
				super.paintComponent(arg0);
				
				paintEvent(arg0);
			}
		};

		StreamingRenderer streamer = new StreamingRenderer();
		mapPane.setRenderer(streamer);
		mapPane.setMapContent(mapComponent);

		final MapLayerTable mapLayerTable = new MapLayerTable(mapPane);
		mapLayerTable.setVisible(false);
		mapLayerTable.setPreferredSize(new Dimension(200, 400));
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, mapLayerTable, mapPane);
	}

	/**
	 * return map component
	 * 
	 * @return
	 */
	public MapContent getMapComponent() {
		return mapComponent;
	}

	@Override
	public void loadMapContent() {
	  final String shape_path =
        "../org.mwc.cmap.NaturalEarth/data/ne_110m_admin_0_countries_89S/ne_110m_admin_0_countries_89S.shp";
	  File file = new File(shape_path);
	  if(!file.exists())
	  {
	    file = JFileDataStoreChooser.showOpenFile("shp", null);
	  }
		if (file == null) {
			return;
		}

		FileDataStore store;
		featureSource = null;
		try {
			store = FileDataStoreFinder.getDataStore(file);
			featureSource = store.getFeatureSource();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// Create a map content and add our shape file to it
		mapComponent = new MapContent();
		mapComponent.setTitle("Debrief Lite");

		final Style style = SLD.createSimpleStyle(featureSource.getSchema());
		final Layer layer = new FeatureLayer(featureSource, style);
		mapComponent.addLayer(layer);

	}

	/**
	 * returns java.awt.Graphics object
	 * 
	 * @return
	 */
	public Graphics getGraphicsContext() {
		return graphics;
	}

	/**
	 * gets a MathTransform object
	 * 
	 * @return MathTransform
	 */
	public MathTransform getTransformObject() {
		SimpleFeatureType schema = featureSource.getSchema();
		CoordinateReferenceSystem dataCRS = (CoordinateReferenceSystem) schema.getCoordinateReferenceSystem();
		CoordinateReferenceSystem worldCRS = (CoordinateReferenceSystem) mapComponent.getCoordinateReferenceSystem();
		MathTransform transform = null;
		try {
			transform = CRS.findMathTransform(dataCRS, worldCRS);
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		return transform;
	}
}
