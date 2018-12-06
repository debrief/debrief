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
package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.mwc.debrief.lite.custom.JPanelWithTitleBar;
import org.mwc.debrief.lite.gui.DebriefLiteApplication;
import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.MapBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Toolbar;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.Canvas.Swing.SwingCanvas;
import MWC.GUI.Tools.Swing.SwingToolbar;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 * @author Unni Mana <unnivm@gmail.com>
 */
public class DebriefLiteApp {

	public static final String appName = "Debrief Lite";
	public static final String NOTES_ICON = "images/16/note.png";
	private static MapContent mapComponent;

	private static JScrollPane createScrollPane(final JPanelWithTitleBar jTitleBar) {
		final JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(jTitleBar, BorderLayout.NORTH);
		final JScrollPane scrPane1 = new JScrollPane(panel);
		return scrPane1;
	}

	public static void main(final String[] args) {
		new DebriefLiteApp();
	}

	private final JFrame theFrame;
	private JMenuBar theMenuBar;
	private JMenu theMenu;
	private JLabel statusBar;
	private JLabel _notesIconLabel;
	private boolean notesPaneExpanded = false;

	private MWC.GUI.Tools.Swing.SwingToolbar theToolbar;

	private final GeoToolMapRenderer geoMapRenderer;

	public DebriefLiteApp() {

		geoMapRenderer = new GeoToolMapRenderer();
		geoMapRenderer.loadMapContent();
		mapComponent = geoMapRenderer.getMapComponent();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		theFrame = new JFrame(appName + " (" + Debrief.GUI.VersionInfo.getVersion() + ")");

		initForm();
		createAppPanels();

		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.setVisible(true);

		/// start application
		startDebriefLiteApplication();

	}

	private void addMenus() {
		theMenu = new JMenu("File");
		theMenu.add(new JMenuItem("New"));
		theMenu.add(new JMenuItem("Open"));
		theMenu.add(new JMenuItem("Save"));
		theMenuBar.add(theMenu);
	}

	private void addStatusBar() {
		statusBar = new JLabel("Status bar for displaying statuses");
		theFrame.add(statusBar, BorderLayout.SOUTH);
	}

	private void addTools(final SwingToolbar theToolbar) {
		final URL iconURL = getClass().getClassLoader().getResource("images/16/new.png");
		final JButton newFile = new JButton("New");
		newFile.setIcon(new ImageIcon(iconURL));
		theToolbar.add(newFile);
	}

	private void createAppPanels() {
		final Dimension frameSize = theFrame.getSize();
		final int width = (int) frameSize.getWidth();
		final int height = (int) frameSize.getHeight();
		final JPanelWithTitleBar timeControllerPanel = new JPanelWithTitleBar("Time Controller");
		final JPanelWithTitleBar outlinePanel = new JPanelWithTitleBar("Outline");
		final JPanelWithTitleBar editorPanel = new JPanelWithTitleBar("Plot Editor");
		final JPanelWithTitleBar graphPanel = new JPanelWithTitleBar("Graph");
		final JScrollPane timeControllerPane = createScrollPane(timeControllerPanel);
		final JScrollPane outlinePane = createScrollPane(outlinePanel);
		final JScrollPane editorPane = createMapPane(mapComponent);// createScrollPane(editorPanel);
		geoMapRenderer.addMapTool(theToolbar);
		final JScrollPane graphPane = createScrollPane(graphPanel);
		final JScrollPane notesPane = createNotesPane();
		final JSplitPane controlPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, timeControllerPane,
				outlinePane);
		final JSplitPane graphSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, editorPane, graphPane);
		final JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanelSplit, graphSplit);
		final JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, notesPane);
		rightSplit.setOneTouchExpandable(true);

		controlPanelSplit.setOneTouchExpandable(true);
		graphSplit.setOneTouchExpandable(true);
		rightSplit.setDividerLocation(width - 50);
		graphSplit.setDividerLocation(height / 2 + height / 5);
		controlPanelSplit.setDividerLocation(height / 2);
		leftSplit.setDividerLocation(width / 3);
		rightSplit.setResizeWeight(0.9);
		graphSplit.setResizeWeight(0.5);
		editorPanel.addMaxListenerFor(leftSplit, graphSplit);
		graphPanel.addMinListenerFor(graphSplit);
		leftSplit.setOneTouchExpandable(true);
		_notesIconLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {

				rightSplit.getRightComponent().setMinimumSize(new Dimension());
				if (notesPaneExpanded) {
					rightSplit.setDividerLocation(0.97d);
				} else {
					rightSplit.setDividerLocation(0.7d);
				}
				// toggle the state
				notesPaneExpanded = !notesPaneExpanded;
			}
		});
		theFrame.add(rightSplit, BorderLayout.CENTER);
		addStatusBar();
		// dummy placeholder
		addMenus();

	}

	/**
	 * creates a scroll pane with map
	 *
	 * @param mapContent
	 * @return
	 */
	private JScrollPane createMapPane(final MapContent mapContent) {
		geoMapRenderer.createMapLayout();
		final MapBuilder builder = new MapBuilder();
		return builder.setMapRenderer(geoMapRenderer).enableToolbar(true).setToolbar(theToolbar).build();
	}

	private JScrollPane createNotesPane() {
		final JPanel notesPanel = new JPanel();
		notesPanel.setLayout(new FlowLayout());
		final JScrollPane notesPane = new JScrollPane(notesPanel);
		final URL url = getClass().getClassLoader().getResource(NOTES_ICON);
		_notesIconLabel = new JLabel();
		_notesIconLabel.setIcon(new ImageIcon(url));
		notesPanel.add(_notesIconLabel);
		return notesPane;
	}

	/**
	 * fill in the UI details
	 */
	private void initForm() {
		theFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final java.awt.event.WindowEvent e) {
				System.exit(0);
			}
		});

		// try to give the application an icon
		final URL iconURL = getClass().getClassLoader().getResource("images/icon.png");
		if (iconURL != null) {
			final ImageIcon myIcon = new ImageIcon(iconURL);
			if (myIcon != null)
				theFrame.setIconImage(myIcon.getImage());
		}

		// create the components
		theToolbar = new MWC.GUI.Tools.Swing.SwingToolbar(Toolbar.HORIZONTAL, "Application", null);
		addTools(theToolbar);

		// and the panel
		final JPanel topSection = new JPanel();
		topSection.setLayout(new BorderLayout());
		theMenuBar = new JMenuBar();
		theFrame.setJMenuBar(theMenuBar);

		// add them
		theFrame.getContentPane().add("North", theToolbar);

		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		theFrame.setSize((int) (dim.width * 0.6), (int) (dim.height * 0.6));
		final Dimension sz = theFrame.getSize();
		theFrame.setLocation((dim.width - sz.width) / 2, (dim.height - sz.height) / 2);

		// do any final re-arranging
		theFrame.doLayout();
	}

	public void setStatus(final String message) {
		statusBar.setText(message);
	}

	private void startDebriefLiteApplication() {

		DebriefLiteApplication application = new DebriefLiteApplication();
    final String boat_file =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/shapes.rep";
		application.openFile(new java.io.File(boat_file));
		File testFile = new File(boat_file);
		final MWC.GUI.Layers _theLayers = new MWC.GUI.Layers();
		final File[] _theFiles = new File[] { testFile };

		ImportReplay.initialise(new DebriefLiteToolParent(ImportReplay.IMPORT_AS_OTG, 0L));

		MWC.Utilities.ReaderWriter.ImportManager.addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());

		// get our thread to import this
		final MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller reader = new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(
				_theFiles, _theLayers) {
			// handle completion of the full import process
			@Override
			public void allFilesFinished(final File[] fNames, final Layers newData) {
				System.out.println("1...all files finished reading....");
			}

			// handle the completion of each file
			@Override
			public void fileFinished(final File fName, final Layers newData) {
				System.out.println("2...files finished reading...." + newData.size());
			}
		};

		// and start it running
		reader.start();

		// wait for the results
		while (reader.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (final java.lang.InterruptedException e) {
			}
		}
		
		System.out.println("num layers:" + _theLayers.size());

		TrackWrapper track = (TrackWrapper) _theLayers.findLayer("NELSON");
		if(track != null)
		{
	    Enumeration<Editable> enumerations = track.getPositionIterator();
	    int cnt = 0;
	    while (enumerations.hasMoreElements()) {
	      @SuppressWarnings("unused")
        final FixWrapper fix = (FixWrapper) enumerations.nextElement();
	      cnt++;
	    }

	    JOptionPane.showMessageDialog(theFrame, "Total Number of records Read from Replay file " + cnt);
		}

		final MapContent map = geoMapRenderer.getMapComponent();

		//// now start plotting the tracks

		final int len = _theLayers.size();
		CanvasType dest = new SwingCanvas();
		GeoToolMapProjection projection = new GeoToolMapProjection(map, _theLayers);
		Graphics g = geoMapRenderer.getGraphicsContext();
		CanvasAdaptor adaptor = new CanvasAdaptor(projection, g);
		dest.setProjection(projection);
		dest.startDraw(g);
		for (int i = 0; i < len; i++) {
			final Layer thisLayer = _theLayers.elementAt(i);
			thisLayer.paint(adaptor);
		}
      
		// first approach
		paintTest(g, projection);

		// second approach
		drawLine1();
	}
	
	private void paintTest(Graphics g, GeoToolMapProjection projection) {

		// 60N 30W to 10N 10W
		WorldLocation loc1 = new WorldLocation(50d, 40d, 0);
		WorldLocation loc2 = new WorldLocation(10d, 10d, 0);

		Point p1 = projection.toScreen(loc1);
		Point p2 = projection.toScreen(loc2);
		
		
		System.out.println(projection.toWorld(p1));

		geoMapRenderer.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
		theFrame.repaint();
	}

	public void drawLine1() {

		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
		b.setName("myFeatureType");
		b.setCRS(DefaultGeographicCRS.WGS84);
		b.add("location", com.vividsolutions.jts.geom.LineString.class);

		final SimpleFeatureType type = b.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);
		com.vividsolutions.jts.geom.GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

		Coordinate[] coords = new Coordinate[2];
		coords[0] = new Coordinate(60, 30);
		coords[1] = new Coordinate(10, 10);

		com.vividsolutions.jts.geom.LineString lineString = geometryFactory.createLineString(coords);
		featureBuilder.add(lineString);

		SimpleFeature feature = featureBuilder.buildFeature("feature1");
		/////////// create feature collection
		DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", type);
		featureCollection.add(feature);

		float lineWidth = 2.0f;
		Style lineStyle = SLD.createLineStyle(Color.red, lineWidth);
		org.geotools.map.Layer layer = new FeatureLayer(featureCollection, lineStyle);

		mapComponent.addLayer(layer);
		theFrame.repaint();
	}

}
