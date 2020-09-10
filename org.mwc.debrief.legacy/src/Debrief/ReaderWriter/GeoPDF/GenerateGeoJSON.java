/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package Debrief.ReaderWriter.GeoPDF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import junit.framework.TestCase;

public class GenerateGeoJSON {

	public static class GeoJSONConfiguration {
		private final int timeDeltaMinutes;
		private final boolean isLabel;
		private final boolean onlyFirstPoint;
		private final String layerName;

		public GeoJSONConfiguration(int timeDeltaMinutes, boolean isLabel, boolean onlyFirstPoint, String layerName) {
			super();
			this.timeDeltaMinutes = timeDeltaMinutes;
			this.isLabel = isLabel;
			this.onlyFirstPoint = onlyFirstPoint;
			this.layerName = layerName;
		}

		public String getLayerName() {
			return layerName;
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

	public static class Point {
		private double x;
		private double y;

		public Point(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
	}

	/**
	 * Method that creates the Track Line from the track given
	 * 
	 * @param currentTrack Track to create the GeoJSON.
	 * @return GeoJSON of the TrackLine given
	 * @throws JsonProcessingException
	 */
	public static String createGeoJSONTrackLine(final TrackWrapper currentTrack,
			final GeoJSONConfiguration configuration) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode jsonRoot = mapper.createObjectNode();
		jsonRoot.put("type", "FeatureCollection");
		if (configuration.getLayerName() != null) {
			jsonRoot.put("name", configuration.getLayerName());
		} else {
			jsonRoot.put("name", currentTrack.getName());
		}

		final ObjectNode crcJson = mapper.createObjectNode();
		crcJson.put("type", "name");

		final ObjectNode crcPropertiesJson = mapper.createObjectNode();
		crcPropertiesJson.put("name", "urn:ogc:def:crs:OGC:1.3:CRS84");
		crcJson.set("properties", crcPropertiesJson);

		jsonRoot.set("crs", crcJson);

		final ArrayNode featuresJson = mapper.createArrayNode();
		final ObjectNode featureJson = mapper.createObjectNode();

		featuresJson.add(featureJson);
		jsonRoot.set("features", featuresJson);

		featureJson.put("type", "Feature");
		final ObjectNode featuresProperty = mapper.createObjectNode();

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ");
		featuresProperty.put("begin", simpleDateFormat.format(currentTrack.getStartDTG().getDate()));
		featuresProperty.put("end", simpleDateFormat.format(currentTrack.getEndDTG().getDate()));

		featureJson.set("properties", featuresProperty);

		final ObjectNode geometryJson = mapper.createObjectNode();
		featureJson.set("geometry", geometryJson);

		geometryJson.put("type", "MultiLineString");
		final ArrayNode coordinatesJson = mapper.createArrayNode();
		geometryJson.set("coordinates", coordinatesJson);

		final ArrayNode subcoordinatesArray = mapper.createArrayNode();
		coordinatesJson.add(subcoordinatesArray);

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
						for (Point coord : coordinates) {
							final ArrayNode coordinateJson = mapper.createArrayNode();
							coordinateJson.add(coord.getX());
							coordinateJson.add(coord.getY());
							subcoordinatesArray.add(coordinateJson);
						}
					}
				}
			}
		}

		final String featureCollectionContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRoot);

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
	 * @throws JsonProcessingException
	 */
	public static String createGeoJSONTrackPoints(final TrackWrapper currentTrack,
			final GeoJSONConfiguration configuration) throws JsonProcessingException {

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ");
		/**
		 * Let's iterate all the track changing the time given.
		 */
		HiResDate currentTime = currentTrack.getStartDTG();

		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode jsonRoot = mapper.createObjectNode();
		jsonRoot.put("type", "FeatureCollection");
		if (configuration.getLayerName() != null) {
			jsonRoot.put("name", configuration.getLayerName());
		} else {
			jsonRoot.put("name", currentTrack.getName());
		}

		final ObjectNode crcJson = mapper.createObjectNode();
		crcJson.put("type", "name");
		final ObjectNode crcPropertiesJson = mapper.createObjectNode();
		crcPropertiesJson.put("name", "urn:ogc:def:crs:OGC:1.3:CRS84");
		crcJson.set("properties", crcPropertiesJson);

		jsonRoot.set("crs", crcJson);

		final ArrayNode featuresArray = mapper.createArrayNode();
		jsonRoot.set("features", featuresArray);

		while (currentTime.lessThanOrEqualTo(currentTrack.getEndDTG())) {
			final Watchable[] points = currentTrack.getNearestTo(currentTime, true);
			if (points.length > 0) {
				final ObjectNode featureNode = mapper.createObjectNode();
				featuresArray.add(featureNode);
				featureNode.put("type", "Feature");
				final ObjectNode properties = mapper.createObjectNode();
				featureNode.set("properties", properties);

				// if we have a point.
				final Watchable point = points[0];

				final Point newPoint = new Point(point.getLocation().getLong(), point.getLocation().getLat());

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

				final ObjectNode geometryJson = mapper.createObjectNode();
				featureNode.set("geometry", geometryJson);

				geometryJson.put("type", "Point");
				final ArrayNode coordinatesNode = mapper.createArrayNode();
				geometryJson.set("coordinates", coordinatesNode);
				coordinatesNode.add(newPoint.getX());
				coordinatesNode.add(newPoint.getY());

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

		final String featureCollectionContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRoot);

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

		public void testCreateGeoJSON() throws FileNotFoundException, JsonProcessingException {
			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);

			final TrackWrapper track = (TrackWrapper) layers.findLayer("COLLINGWOOD", true);
			final GeoJSONConfiguration config = new GeoJSONConfiguration(-1, false, false, null);
			final String jsonTrackLineString = GenerateGeoJSON.createGeoJSONTrackLine(track, config);

			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode collingwood = mapper.readValue(jsonTrackLineString, ObjectNode.class);

			final JsonNode collingWoodName = collingwood.get("name");
			assertEquals("Correct name", "COLLINGWOOD", collingWoodName.textValue());
			assertEquals("Correct type", "FeatureCollection", collingwood.get("type").textValue());

			final JsonNode crsNode = collingwood.get("crs");

			final JsonNode crsType = crsNode.get("type");

			assertEquals("Correct crs name", "name", crsType.textValue());

			final JsonNode crsProperty = crsNode.get("properties");

			final JsonNode crsPropertyName = crsProperty.get("name");

			assertEquals("Correct crs property name", "urn:ogc:def:crs:OGC:1.3:CRS84", crsPropertyName.textValue());

			final JsonNode featuresNode = collingwood.get("features");
			final JsonNode featureNodeChild = featuresNode.get(0);
			final JsonNode typeFeature = featureNodeChild.get("type");
			assertEquals("Correct type features", "Feature", typeFeature.textValue());

			final JsonNode beginNode = featureNodeChild.get("properties").get("begin");
			// assertEquals("Correct beginning", "1995/12/12 06:03:00+0100", beginNode.textValue());

			final JsonNode endNode = featureNodeChild.get("properties").get("end");
			// assertEquals("Correct beginning", "1995/12/12 12:45:00+0100", endNode.textValue());

			final JsonNode geometryTypeJsonNode = featureNodeChild.get("geometry").get("type");
			assertEquals("Correct Geometry Type", "MultiLineString", geometryTypeJsonNode.textValue());

			final JsonNode coordinatesJsonNode = featureNodeChild.get("geometry").get("coordinates").get(0);

			assertEquals("Amount of coordinates ", 403, coordinatesJsonNode.size());

			final JsonNode firstCoordinate = coordinatesJsonNode.get(0);

			assertEquals("Correct First X coordinate", -21.593774999999997, firstCoordinate.get(0).doubleValue(), 1e-8);
			assertEquals("Correct First Y coordinate", 21.894219444444445, firstCoordinate.get(1).doubleValue(), 1e-8);

			final JsonNode lastCoordinate = coordinatesJsonNode.get(coordinatesJsonNode.size() - 1);

			assertEquals("Correct First X coordinate", -21.733580555555555, lastCoordinate.get(0).doubleValue(), 1e-8);
			assertEquals("Correct First Y coordinate", 22.142258333333334, lastCoordinate.get(1).doubleValue(), 1e-8);
		}

		public void testCreateGeoJSON3() throws FileNotFoundException, JsonProcessingException {
			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
			final TrackWrapper track = (TrackWrapper) layers.findLayer("COLLINGWOOD", true);

			String geoJsonData = GenerateGeoJSON.createGeoJSONTrackPoints(track,
					new GeoJSONConfiguration(10, false, false, null));
			
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode collingwood = mapper.readValue(geoJsonData, ObjectNode.class);

			final JsonNode collingWoodName = collingwood.get("name");
			assertEquals("Correct name", "COLLINGWOOD", collingWoodName.textValue());
			assertEquals("Correct type", "FeatureCollection", collingwood.get("type").textValue());

			final JsonNode crsNode = collingwood.get("crs");

			final JsonNode crsType = crsNode.get("type");

			assertEquals("Correct crs name", "name", crsType.textValue());

			final JsonNode crsProperty = crsNode.get("properties");

			final JsonNode crsPropertyName = crsProperty.get("name");

			assertEquals("Correct crs property name", "urn:ogc:def:crs:OGC:1.3:CRS84", crsPropertyName.textValue());

			final JsonNode featuresNode = collingwood.get("features");
			
			assertEquals("Correct amount of child", 41, featuresNode.size());
			
			final JsonNode firstNodeChild = featuresNode.get(0);
			final JsonNode typeFeature = firstNodeChild.get("type");
			assertEquals("Correct type features", "Feature", typeFeature.textValue());

			final JsonNode propertiesFirstNodeChild = firstNodeChild.get("properties");
			assertEquals("Correct course ", 0.005235987755982988, propertiesFirstNodeChild.get("course").doubleValue(), 1e-8);
			assertEquals("Correct speed ", 3.5, propertiesFirstNodeChild.get("speed").doubleValue(), 1e-8);
			// assertEquals("Correct time ", "1995/12/12 06:03:00+0100", propertiesFirstNodeChild.get("time").textValue());
			
			final JsonNode geometryFirstNodeChild = firstNodeChild.get("geometry");
			assertEquals("Correct type for geometry", "Point", geometryFirstNodeChild.get("type").textValue());
			final JsonNode coordinates = geometryFirstNodeChild.get("coordinates");
			
			assertEquals("Correct X coordinate ", -21.593774999999997, coordinates.get(0).asDouble(), 1e-8);
			assertEquals("Correct Y coordinate ", 21.894219444444445, coordinates.get(1).asDouble(), 1e-8);
			
			final JsonNode lastNodeChild = featuresNode.get(featuresNode.size() - 1);
			final JsonNode lastTypeFeature = lastNodeChild.get("type");
			assertEquals("Correct type features", "Feature", lastTypeFeature.textValue());

			final JsonNode propertiesLastNodeChild = lastNodeChild.get("properties");
			assertEquals("Correct course ", 0.35255650890285456, propertiesLastNodeChild.get("course").doubleValue(), 1e-8);
			assertEquals("Correct speed ", 3.5, propertiesLastNodeChild.get("speed").doubleValue(), 1e-8);
			// assertEquals("Correct time ", "1995/12/12 12:43:00+0100", propertiesLastNodeChild.get("time").textValue());
			
			final JsonNode geometryLastNodeChild = firstNodeChild.get("geometry");
			assertEquals("Correct type for geometry", "Point", geometryLastNodeChild.get("type").textValue());
			final JsonNode lastCoordinates = geometryLastNodeChild.get("coordinates");
			
			assertEquals("Correct X coordinate ", -21.593774999999997, lastCoordinates.get(0).asDouble(), 1e-8);
			assertEquals("Correct Y coordinate ", 21.894219444444445, lastCoordinates.get(1).asDouble(), 1e-8);
		}
		
		public void testCreateGeoJSON4() throws FileNotFoundException, JsonProcessingException {
			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
			final TrackWrapper track = (TrackWrapper) layers.findLayer("COLLINGWOOD", true);

			String geoJsonData = GenerateGeoJSON.createGeoJSONTrackPoints(track,
					new GeoJSONConfiguration(20, true, false, null));
			
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode collingwood = mapper.readValue(geoJsonData, ObjectNode.class);

			final JsonNode collingWoodName = collingwood.get("name");
			assertEquals("Correct name", "COLLINGWOOD", collingWoodName.textValue());
			assertEquals("Correct type", "FeatureCollection", collingwood.get("type").textValue());

			final JsonNode crsNode = collingwood.get("crs");

			final JsonNode crsType = crsNode.get("type");

			assertEquals("Correct crs name", "name", crsType.textValue());

			final JsonNode crsProperty = crsNode.get("properties");

			final JsonNode crsPropertyName = crsProperty.get("name");

			assertEquals("Correct crs property name", "urn:ogc:def:crs:OGC:1.3:CRS84", crsPropertyName.textValue());

			final JsonNode featuresNode = collingwood.get("features");
			
			assertEquals("Correct amount of child", 21, featuresNode.size());
			
			final JsonNode firstNodeChild = featuresNode.get(0);
			final JsonNode typeFeature = firstNodeChild.get("type");
			assertEquals("Correct type features", "Feature", typeFeature.textValue());

			final JsonNode propertiesFirstNodeChild = firstNodeChild.get("properties");
			assertEquals("Correct course ", 0.005235987755982988, propertiesFirstNodeChild.get("course").doubleValue(), 1e-8);
			assertEquals("Correct speed ", 3.5, propertiesFirstNodeChild.get("speed").doubleValue(), 1e-8);
			// assertEquals("Correct time ", "1995/12/12 06:03:00+0100", propertiesFirstNodeChild.get("time").textValue());
			
			final JsonNode geometryFirstNodeChild = firstNodeChild.get("geometry");
			assertEquals("Correct type for geometry", "Point", geometryFirstNodeChild.get("type").textValue());
			final JsonNode coordinates = geometryFirstNodeChild.get("coordinates");
			
			assertEquals("Correct X coordinate ", -21.593774999999997, coordinates.get(0).asDouble(), 1e-8);
			assertEquals("Correct Y coordinate ", 21.894219444444445, coordinates.get(1).asDouble(), 1e-8);
			
			final JsonNode lastNodeChild = featuresNode.get(featuresNode.size() - 1);
			final JsonNode lastTypeFeature = lastNodeChild.get("type");
			assertEquals("Correct type features", "Feature", lastTypeFeature.textValue());

			final JsonNode propertiesLastNodeChild = lastNodeChild.get("properties");
			assertEquals("Correct course ", 0.35255650890285456, propertiesLastNodeChild.get("course").doubleValue(), 1e-8);
			assertEquals("Correct speed ", 3.5, propertiesLastNodeChild.get("speed").doubleValue(), 1e-8);
			// assertEquals("Correct time ", "1995/12/12 12:43:00+0100", propertiesLastNodeChild.get("time").textValue());
			
			final JsonNode geometryLastNodeChild = firstNodeChild.get("geometry");
			assertEquals("Correct type for geometry", "Point", geometryLastNodeChild.get("type").textValue());
			final JsonNode lastCoordinates = geometryLastNodeChild.get("coordinates");
			
			assertEquals("Correct X coordinate ", -21.593774999999997, lastCoordinates.get(0).asDouble(), 1e-8);
			assertEquals("Correct Y coordinate ", 21.894219444444445, lastCoordinates.get(1).asDouble(), 1e-8);
		}
		
		public void testCreateGeoJSON2() throws FileNotFoundException, JsonProcessingException {
			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
			final TrackWrapper track = (TrackWrapper) layers.findLayer("COLLINGWOOD", true);

			String geoJsonData = GenerateGeoJSON.createGeoJSONTrackLine(track,
					new GeoJSONConfiguration(-1, true, false, null));
			
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode collingwood = mapper.readValue(geoJsonData, ObjectNode.class);

			final JsonNode collingWoodName = collingwood.get("name");
			assertEquals("Correct name", "COLLINGWOOD", collingWoodName.textValue());
			assertEquals("Correct type", "FeatureCollection", collingwood.get("type").textValue());

			final JsonNode crsNode = collingwood.get("crs");

			final JsonNode crsType = crsNode.get("type");

			assertEquals("Correct crs name", "name", crsType.textValue());

			final JsonNode crsProperty = crsNode.get("properties");

			final JsonNode crsPropertyName = crsProperty.get("name");

			assertEquals("Correct crs property name", "urn:ogc:def:crs:OGC:1.3:CRS84", crsPropertyName.textValue());

			final JsonNode featuresNode = collingwood.get("features");
			final JsonNode featureNodeChild = featuresNode.get(0);
			final JsonNode typeFeature = featureNodeChild.get("type");
			assertEquals("Correct type features", "Feature", typeFeature.textValue());

			final JsonNode beginNode = featureNodeChild.get("properties").get("begin");
			// assertEquals("Correct beginning", "1995/12/12 06:03:00+0100", beginNode.textValue());

			final JsonNode endNode = featureNodeChild.get("properties").get("end");
			// assertEquals("Correct beginning", "1995/12/12 12:45:00+0100", endNode.textValue());

			final JsonNode geometryTypeJsonNode = featureNodeChild.get("geometry").get("type");
			assertEquals("Correct Geometry Type", "MultiLineString", geometryTypeJsonNode.textValue());

			final JsonNode coordinatesJsonNode = featureNodeChild.get("geometry").get("coordinates").get(0);

			assertEquals("Amount of coordinates ", 403, coordinatesJsonNode.size());

			final JsonNode firstCoordinate = coordinatesJsonNode.get(0);

			assertEquals("Correct First X coordinate", -21.593774999999997, firstCoordinate.get(0).doubleValue(), 1e-8);
			assertEquals("Correct First Y coordinate", 21.894219444444445, firstCoordinate.get(1).doubleValue(), 1e-8);

			final JsonNode lastCoordinate = coordinatesJsonNode.get(coordinatesJsonNode.size() - 1);

			assertEquals("Correct First X coordinate", -21.733580555555555, lastCoordinate.get(0).doubleValue(), 1e-8);
			assertEquals("Correct First Y coordinate", 22.142258333333334, lastCoordinate.get(1).doubleValue(), 1e-8);
		}
	}
}
