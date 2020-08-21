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
import Debrief.ReaderWriter.GeoPDF.GenerateGeoJSON.GeoJSONConfiguration;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
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

	public static class GeoJSONConfiguration {
		private final int timeDeltaMinutes;
		private final boolean isLabel;
		private final boolean onlyFirstPoint;

		public GeoJSONConfiguration(int timeDeltaMinutes, boolean isLabel, boolean onlyFirstPoint) {
			this.timeDeltaMinutes = timeDeltaMinutes;
			this.isLabel = isLabel;
			this.onlyFirstPoint = onlyFirstPoint;
		}

		public int getTimeDeltaMinutes() {
			return timeDeltaMinutes;
		}

		public boolean isLabel() {
			return isLabel;
		}

		public boolean isOnlyFirstPoint() {
			return onlyFirstPoint;
		}

	}

	/**
	 * Method that creates the Track Line from the track given
	 * 
	 * @param currentTrack Track to create the GeoJSON.
	 * @return GeoJSON of the TrackLine given
	 */
	public static String createGeoJSONTrackLine(final TrackWrapper currentTrack) {
		/**
		 * Let's use a MultiLineString because some tracks have multiples segments, so
		 * we will have 1 line per segment :)
		 */
		final List<LineString> positionForTrackGeometry = new ArrayList<>();

		final SegmentList segmentList = currentTrack.getSegments();
		final Enumeration<Editable> segmentEnumeration = segmentList.elements();
		while (segmentEnumeration.hasMoreElements()) {
			final Editable segmentEditable = segmentEnumeration.nextElement();
			if (segmentEditable instanceof TrackSegment) {
				final TrackSegment trackSegment = (TrackSegment) segmentEditable;

				/**
				 * Again let's draw only the visible items
				 */
				if (trackSegment.getVisible()) {
					final Enumeration<Editable> fixWrapperEnumeration = trackSegment.elements();
					while (fixWrapperEnumeration.hasMoreElements()) {
						final Editable fixWrapperEditable = fixWrapperEnumeration.nextElement();

						/**
						 * We are getting ready to build the new LineString to add it to the geometry.
						 * Let's create an array of positions for the LineString then.
						 */

						final ArrayList<Point> coordinates = new ArrayList<Point>();
						if (fixWrapperEditable instanceof FixWrapper) {
							final FixWrapper currentFixWrapper = (FixWrapper) fixWrapperEditable;

							/**
							 * Only visible objects :)
							 */
							if (currentFixWrapper.getVisible()) {
								// Ok, so now let's add this position to the position list.
								final Point newPosition = new Point(currentFixWrapper.getLocation().getLong(),
										currentFixWrapper.getLocation().getLat());
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

		return featureCollectionContent;

	}

	/**
	 * Method used for creating a set of TrackPoints, used for the 10/15 mins
	 * sequence. It can be also used for the label Track Points or for the first
	 * point GeoJSON. Output depends on the configuration passed in the 4rd
	 * parameter
	 * 
	 * @param currentTrack
	 * @param configuration
	 */
	public static String createGeoJSONTrackPoints(final TrackWrapper currentTrack,
			final GeoJSONConfiguration configuration) {

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ");
		/**
		 * Let's iterate all the track changing the time given.
		 */
		HiResDate currentTime = currentTrack.getStartDTG();

		final FeatureCollection featuresCollection = new FeatureCollection();
		while (currentTime.lessThanOrEqualTo(currentTrack.getEndDTG())) {
			final Watchable[] points = currentTrack.getNearestTo(currentTime, true);
			if (points.length > 0) {
				// if we have a point.
				final Watchable point = points[0];

				final Point newPoint = new Point(point.getLocation().getLong(), point.getLocation().getLat());
				final HashMap<String, Object> properties = new HashMap<String, Object>();

				properties.put("elevation", point.getDepth());
				properties.put("time", simpleDateFormat.format(point.getTime().getDate()));
				properties.put("heading", "null");
				properties.put("course", point.getCourse());
				properties.put("speed", point.getSpeed());

				/**
				 * In this case, we are creating a label, so, let's add the time_str tag
				 */
				if (configuration.isLabel() && !configuration.isOnlyFirstPoint()) {
					properties.put("time_str", FormatRNDateTime.toShortString(point.getTime().getDate().getTime()));
				}

				final Feature feature = FeatureConverter.toFeature(newPoint);
				feature.setProperties(properties);
				featuresCollection.addFeature(feature);

				if (configuration.isOnlyFirstPoint()) {
					break;
				}
			}

			/**
			 * Let's increase the time by the timeDelta given. Remember we need to pass it
			 * to Milliseconds.
			 */
			currentTime = new HiResDate(
					currentTime.getMicros() / 1000 + configuration.getTimeDeltaMinutes() * 60 * 1000, 0);
		}

		final String featureCollectionContent = FeatureConverter.toStringValue(featuresCollection);

		return featureCollectionContent;

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

			// TODO
			//GenerateGeoJSON.createGeoJSONTrackLine(layers, toStringOutputStream, null);

			// Test the JSON generated.

			//System.out.println(toStringOutputStream.toString());
		}

		public void testCreateGeoJSON2() throws FileNotFoundException {
			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);

			final TrackWrapper track = (TrackWrapper) layers.findLayer("NELSON", true);
			final String geoJsonData = GenerateGeoJSON.createGeoJSONTrackLine(track);

			// Test the JSON generated.

			System.out.println(geoJsonData);
		}

		public void testCreateGeoJSON3() throws FileNotFoundException {
			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
			final TrackWrapper track = (TrackWrapper) layers.findLayer("NELSON", true);

			String geoJsonData = GenerateGeoJSON.createGeoJSONTrackPoints(track,
					new GeoJSONConfiguration(10, false, false));

			System.out.println(geoJsonData);

			geoJsonData = GenerateGeoJSON.createGeoJSONTrackPoints(track,
					new GeoJSONConfiguration(20, true, false));

			// Test the JSON generated.

			System.out.println(geoJsonData);

			geoJsonData = GenerateGeoJSON.createGeoJSONTrackPoints(track,
					new GeoJSONConfiguration(-1, true, true));

			// Test the JSON generated.

			System.out.println(geoJsonData);
		}
	}
}
