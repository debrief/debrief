package Debrief.ReaderWriter.GeoPDF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import junit.framework.TestCase;
import mil.nga.sf.Geometry;
import mil.nga.sf.LineString;
import mil.nga.sf.MultiLineString;
import mil.nga.sf.Point;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Position;

public class GenerateGeoJSON {

	public static void createGeoJSON(final Layers layers, final OutputStream outputStream) {
		/**
		 * Let's write the output using an PrinterWriter
		 */
		final PrintWriter printWriter = new PrintWriter(outputStream);
		
		try {
			/**
			 * Let's iterate over all the layers to find the Tracks to export
			 */
			final Enumeration<Editable> enumeration = layers.elements();
			while (enumeration.hasMoreElements()) {
				final Editable currentEditable = enumeration.nextElement();
				if (currentEditable instanceof TrackWrapper) {
					/**
					 * Ok, at this point we have a TrackWrapper. Now, let's create a Geometry of
					 * the type Simple Features Geotools Library.
					 */
					final TrackWrapper currentTrack = (TrackWrapper) currentEditable;
					
					/**
					 * Let's draw only visible tracks.
					 */
					if (currentTrack.getVisible()) {
						/**
						 * To create the new Geometry, we need to have the points first.
						 */
	
						/**
						 * Let's use a MultiLineString because some tracks have multiples
						 * segments, so we will have 1 line per segment :)
						 */
						final List<LineString> positionForTrackGeometry = new ArrayList<>();
						
						final SegmentList segmentList = currentTrack.getSegments();
						final Enumeration<Editable> segmentEnumeration = segmentList.elements();
						while(segmentEnumeration.hasMoreElements()) {
							final Editable segmentEditable = segmentEnumeration.nextElement();
							if (segmentEditable instanceof TrackSegment) {
								final TrackSegment trackSegment = (TrackSegment) segmentEditable;
								
								/**
								 * Again let's draw only the visible items
								 */
								if (trackSegment.getVisible()) {
									final Enumeration<Editable> fixWrapperEnumeration = trackSegment.elements();
									while(fixWrapperEnumeration.hasMoreElements()) {
										final Editable fixWrapperEditable = fixWrapperEnumeration.nextElement();
	
										/**
										 * We are getting ready to build the new LineString to add it
										 * to the geometry. Let's create an array of positions for the 
										 * LineString then.
										 */
										
										final ArrayList<Point> coordinates = new ArrayList<Point>();
										if (fixWrapperEditable instanceof FixWrapper) {
											final FixWrapper currentFixWrapper = (FixWrapper) fixWrapperEditable;
											
											/**
											 * Only visible objects :)
											 */
											if (currentFixWrapper.getVisible()) {
												// Ok, so now let's add this position to the position list.
												final Point newPosition = new Point(currentFixWrapper.getLocation().getLong(), currentFixWrapper.getLocation().getLat());
												coordinates.add(newPosition);
											}
										}
										
										/**
										 * In case that we have any coordinate to add, let's add it to the geometry.
										 */
										if (!coordinates.isEmpty()) {
											positionForTrackGeometry.add(new LineString(coordinates));
										}
									}
								}
							}
						}
	
						final MultiLineString trackAsGeometry = new MultiLineString(positionForTrackGeometry);
						
						
						
						/**
						 * Let's create the features
						 */
						final Feature feature = FeatureConverter.toFeature(trackAsGeometry);
						final HashMap<String, Object> properties = new HashMap<>();
						
						final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ");
						properties.put("begin", simpleDateFormat.format(currentTrack.getStartDTG().getDate()));
						properties.put("end", simpleDateFormat.format(currentTrack.getEndDTG().getDate()));
						feature.setProperties(properties);
						final FeatureCollection featureCollection = new FeatureCollection(feature);
						
						final String featureCollectionContent = FeatureConverter.toStringValue(featureCollection);
						
						/*
						
						final String featureString = FeatureConverter.toStringValue(feature);*/
						
						/**
						 * Let's write the new feature to the output stream.
						 */
						printWriter.println(featureCollectionContent);
					}
					
				}
			}
		}finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}
		
	}

	public static class GenerateGeoJSONTest extends TestCase {
		
		private final static String sampledpf = "../org.mwc.cmap.combined.feature/root_installs/sample_data/sample.dpf";
		private final static String boat1rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat2.rep";
		
		private static class MockSession extends Session {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public MockSession(Layers theLayers) {
				super(null, theLayers);
			}

			@Override
			public void closeGUI() {
			}

			@Override
			public void initialiseForm(ToolParent theParent) {
			}

			@Override
			public void repaint() {
			}

			@Override
			protected boolean wantsToClose() {
				return false;
			}
		}
		
		
		public void testCreateGeoJSON() throws FileNotFoundException {
			final Layers layers = new Layers();
			Session session = new MockSession(layers);
			final DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(null);
			reader.importThis("sample.dpf", new FileInputStream(sampledpf), session);
		
			final OutputStream toStringOutputStream = new OutputStream() {
				
				private final StringBuilder builder =  new StringBuilder();
				
				@Override
				public void write(int b) throws IOException {
					builder.append((char)b);
				}
				
				@Override
				public String toString() {
					return builder.toString();
				}
			};
			GenerateGeoJSON.createGeoJSON(layers, toStringOutputStream);
			
			// Test the JSON generated.
			
			System.out.println(toStringOutputStream.toString());
		}
		
		public void testCreateGeoJSON2() throws FileNotFoundException {
			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);

			final OutputStream toStringOutputStream = new OutputStream() {
				
				private final StringBuilder builder =  new StringBuilder();
				
				@Override
				public void write(int b) throws IOException {
					builder.append((char)b);
				}
				
				@Override
				public String toString() {
					return builder.toString();
				}
			};
			GenerateGeoJSON.createGeoJSON(layers, toStringOutputStream);
			
			// Test the JSON generated.
			
			System.out.println(toStringOutputStream.toString());
		}
	}
}
