package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

public class Main2 {

	private static int dx = 5;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		if (file == null) {
			return;
		}

		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		SimpleFeatureSource featureSource = store.getFeatureSource();

		final MapContent map = new MapContent();
		map.setTitle("Quickstart");

		Style style = SLD.createSimpleStyle(featureSource.getSchema());
		Layer layer = new FeatureLayer(featureSource, style);
		map.addLayer(layer);

		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				try {
					showMap(map);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	/**
	 * 
	 * @param map
	 * @throws IOException
	 */
	private static void showMap(final MapContent map) throws IOException {
		final DbriefJMapPane1 mapPane = new DbriefJMapPane1();
		mapPane.setRenderer(new StreamingRenderer());
		
		mapPane.setMapContent(map);
		mapPane.setSize(500, 400);
		
		
		JFrame frame = new JFrame("ImageLab2");
		frame.setLayout(new BorderLayout());
		frame.add(mapPane, BorderLayout.CENTER);
		frame.add(new JButton("add"), BorderLayout.EAST);
		frame.addComponentListener(new ComponentAdapter() 
		{  
		        public void componentResized(ComponentEvent evt) {
		            Component c = (Component)evt.getSource();
		            System.out.println("raaaaaa " + mapPane.bounds());
		            MapViewport mvp = new MapViewport();
		            mvp.setScreenArea(mapPane.bounds());
		    		map.setViewport(mvp);
		            dx+=5;
		        }
		});
		
		JPanel buttons = new JPanel();
		JButton zoomInButton = new JButton("Zoom In");
		zoomInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// mapPane.setState(JMapPane.ZoomIn);
			}
		});
		buttons.add(zoomInButton);

		JButton zoomOutButton = new JButton("Zoom Out");
		zoomOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// mapPane.setState(JMapPane.ZoomOut);
			}
		});
		buttons.add(zoomOutButton);

		JButton panButton = new JButton("Move");
		panButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// mapPane.setState(JMapPane.Pan);
			}
		});
		buttons.add(panButton);

		frame.add(buttons, BorderLayout.NORTH);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(900, 400);
		frame.setVisible(true);
	}
	
}
