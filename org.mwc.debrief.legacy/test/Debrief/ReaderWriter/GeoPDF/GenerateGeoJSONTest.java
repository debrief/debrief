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

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.GeoPDF.GenerateGeoJSON.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Debrief.ReaderWriter.GeoPDF.GenerateGeoJSON.*;
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
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.Watchable;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import org.junit.jupiter.api.Test;

public class GenerateGeoJSONTest {

	private static class MockSession extends Session {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public MockSession(final Layers theLayers) {
			super(null, theLayers);
		}

		@Override
		public void closeGUI() {
		}

		@Override
		public void initialiseForm(final ToolParent theParent) {
		}

		@Override
		public void repaint() {
		}

		@Override
		protected boolean wantsToClose() {
			return false;
		}
	}

	private final static String sampledpf = "../org.mwc.cmap.combined.feature/root_installs/sample_data/sample.dpf";

	private final static String boat1rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat2.rep";

	/**
	 * the GeoJSON for each track in sample.dpf (the line, and points every 10
	 * minutes) matches the committed golden file. Written as canonical JSON
	 * (keys sorted, indented) so the comparison doesn't depend on how a Jackson
	 * version orders or spaces its output. Times are formatted in GMT, so the
	 * result doesn't depend on the machine's time zone.
	 */
	@Test
	public void testGeoJSONGolden() throws Exception {
		final java.util.TimeZone oldZone = java.util.TimeZone.getDefault();
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("GMT"));
		try {
			// importing into a set of layers reads a <layers> element, so hand it
			// just that part of the plot file (the whole plot needs a GUI session)
			final String plot = new String(java.nio.file.Files.readAllBytes(new java.io.File(sampledpf).toPath()),
					java.nio.charset.StandardCharsets.UTF_8);
			final String layersXml = plot.substring(plot.indexOf("<layers"),
					plot.indexOf("</layers>") + "</layers>".length());
			final Layers layers = new Layers();
			new Debrief.ReaderWriter.XML.DebriefXMLReaderWriter(null).importThis("sample.dpf",
					new java.io.ByteArrayInputStream(layersXml.getBytes(java.nio.charset.StandardCharsets.UTF_8)),
					layers);

			final ObjectMapper mapper = new ObjectMapper();
			mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
			mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT, true);
			final ObjectNode root = mapper.createObjectNode();
			int tracks = 0;
			for (final String name : new String[] { "NELSON", "COLLINGWOOD" }) {
				final TrackWrapper track = (TrackWrapper) layers.findLayer(name, true);
				assertNotNull(track, name + " loaded from sample.dpf");
				final ObjectNode trackNode = root.putObject(name);
				trackNode.set("line", mapper.readTree(GenerateGeoJSON.createGeoJSONTrackLine(track,
						new GeoJSONConfiguration(-1, false, false, null, null, null))));
				trackNode.set("points", mapper.readTree(GenerateGeoJSON.createGeoJSONTrackPoints(track,
						new GeoJSONConfiguration(10, true, false, null, null, null))));
				tracks++;
			}
			assertEquals(2, tracks, "both tracks exported");

			// a tree read back from text sorts its keys only through a Map, so go via one
			final Object canonical = mapper.treeToValue(root, java.util.Map.class);
			Debrief.Testing.GoldenFile.assertMatches("geojson/sample_dpf.json",
					mapper.writeValueAsString(canonical) + "\n");
		} finally {
			java.util.TimeZone.setDefault(oldZone);
		}
	}

	@Test
	public void testCreateGeoJSON() throws FileNotFoundException, JsonProcessingException {
		final Layers layers = new Layers();
		final ImportReplay replayImporter = new ImportReplay();
		replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);

		final TrackWrapper track = (TrackWrapper) layers.findLayer("COLLINGWOOD", true);
		final GeoJSONConfiguration config = new GeoJSONConfiguration(-1, false, false, null, null, null);
		final String jsonTrackLineString = GenerateGeoJSON.createGeoJSONTrackLine(track, config);

		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode collingwood = mapper.readValue(jsonTrackLineString, ObjectNode.class);

		final JsonNode collingWoodName = collingwood.get("name");
		assertEquals("COLLINGWOOD", collingWoodName.textValue(), "Correct name");
		assertEquals("FeatureCollection", collingwood.get("type").textValue(), "Correct type");

		final JsonNode crsNode = collingwood.get("crs");

		final JsonNode crsType = crsNode.get("type");

		assertEquals("name", crsType.textValue(), "Correct crs name");

		final JsonNode crsProperty = crsNode.get("properties");

		final JsonNode crsPropertyName = crsProperty.get("name");

		assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", crsPropertyName.textValue(), "Correct crs property name");

		final JsonNode featuresNode = collingwood.get("features");
		final JsonNode featureNodeChild = featuresNode.get(0);
		final JsonNode typeFeature = featureNodeChild.get("type");
		assertEquals("Feature", typeFeature.textValue(), "Correct type features");

		final JsonNode beginNode = featureNodeChild.get("properties").get("begin");
		// assertEquals("Correct beginning", "1995/12/12 06:03:00+0100",
		// beginNode.textValue());

		final JsonNode endNode = featureNodeChild.get("properties").get("end");
		// assertEquals("Correct beginning", "1995/12/12 12:45:00+0100",
		// endNode.textValue());

		final JsonNode geometryTypeJsonNode = featureNodeChild.get("geometry").get("type");
		assertEquals("MultiLineString", geometryTypeJsonNode.textValue(), "Correct Geometry Type");

		final JsonNode coordinatesJsonNode = featureNodeChild.get("geometry").get("coordinates").get(0);

		assertEquals(403, coordinatesJsonNode.size(), "Amount of coordinates ");

		final JsonNode firstCoordinate = coordinatesJsonNode.get(0);

		assertEquals(-21.593774999999997, firstCoordinate.get(0).doubleValue(), 1e-8, "Correct First X coordinate");
		assertEquals(21.894219444444445, firstCoordinate.get(1).doubleValue(), 1e-8, "Correct First Y coordinate");

		final JsonNode lastCoordinate = coordinatesJsonNode.get(coordinatesJsonNode.size() - 1);

		assertEquals(-21.733580555555555, lastCoordinate.get(0).doubleValue(), 1e-8, "Correct First X coordinate");
		assertEquals(22.142258333333334, lastCoordinate.get(1).doubleValue(), 1e-8, "Correct First Y coordinate");
	}

	@Test
	public void testCreateGeoJSON2() throws FileNotFoundException, JsonProcessingException {
		final Layers layers = new Layers();
		final ImportReplay replayImporter = new ImportReplay();
		replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
		final TrackWrapper track = (TrackWrapper) layers.findLayer("COLLINGWOOD", true);

		final String geoJsonData = GenerateGeoJSON.createGeoJSONTrackLine(track,
				new GeoJSONConfiguration(-1, true, false, null, null, null));

		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode collingwood = mapper.readValue(geoJsonData, ObjectNode.class);

		final JsonNode collingWoodName = collingwood.get("name");
		assertEquals("COLLINGWOOD", collingWoodName.textValue(), "Correct name");
		assertEquals("FeatureCollection", collingwood.get("type").textValue(), "Correct type");

		final JsonNode crsNode = collingwood.get("crs");

		final JsonNode crsType = crsNode.get("type");

		assertEquals("name", crsType.textValue(), "Correct crs name");

		final JsonNode crsProperty = crsNode.get("properties");

		final JsonNode crsPropertyName = crsProperty.get("name");

		assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", crsPropertyName.textValue(), "Correct crs property name");

		final JsonNode featuresNode = collingwood.get("features");
		final JsonNode featureNodeChild = featuresNode.get(0);
		final JsonNode typeFeature = featureNodeChild.get("type");
		assertEquals("Feature", typeFeature.textValue(), "Correct type features");

		final JsonNode beginNode = featureNodeChild.get("properties").get("begin");
		// assertEquals("Correct beginning", "1995/12/12 06:03:00+0100",
		// beginNode.textValue());

		final JsonNode endNode = featureNodeChild.get("properties").get("end");
		// assertEquals("Correct beginning", "1995/12/12 12:45:00+0100",
		// endNode.textValue());

		final JsonNode geometryTypeJsonNode = featureNodeChild.get("geometry").get("type");
		assertEquals("MultiLineString", geometryTypeJsonNode.textValue(), "Correct Geometry Type");

		final JsonNode coordinatesJsonNode = featureNodeChild.get("geometry").get("coordinates").get(0);

		assertEquals(403, coordinatesJsonNode.size(), "Amount of coordinates ");

		final JsonNode firstCoordinate = coordinatesJsonNode.get(0);

		assertEquals(-21.593774999999997, firstCoordinate.get(0).doubleValue(), 1e-8, "Correct First X coordinate");
		assertEquals(21.894219444444445, firstCoordinate.get(1).doubleValue(), 1e-8, "Correct First Y coordinate");

		final JsonNode lastCoordinate = coordinatesJsonNode.get(coordinatesJsonNode.size() - 1);

		assertEquals(-21.733580555555555, lastCoordinate.get(0).doubleValue(), 1e-8, "Correct First X coordinate");
		assertEquals(22.142258333333334, lastCoordinate.get(1).doubleValue(), 1e-8, "Correct First Y coordinate");
	}

	@Test
	public void testCreateGeoJSON3() throws FileNotFoundException, JsonProcessingException {
		final Layers layers = new Layers();
		final ImportReplay replayImporter = new ImportReplay();
		replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
		final TrackWrapper track = (TrackWrapper) layers.findLayer("COLLINGWOOD", true);

		final String geoJsonData = GenerateGeoJSON.createGeoJSONTrackPoints(track,
				new GeoJSONConfiguration(10, false, false, null, null, null));

		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode collingwood = mapper.readValue(geoJsonData, ObjectNode.class);

		final JsonNode collingWoodName = collingwood.get("name");
		assertEquals("COLLINGWOOD", collingWoodName.textValue(), "Correct name");
		assertEquals("FeatureCollection", collingwood.get("type").textValue(), "Correct type");

		final JsonNode crsNode = collingwood.get("crs");

		final JsonNode crsType = crsNode.get("type");

		assertEquals("name", crsType.textValue(), "Correct crs name");

		final JsonNode crsProperty = crsNode.get("properties");

		final JsonNode crsPropertyName = crsProperty.get("name");

		assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", crsPropertyName.textValue(), "Correct crs property name");

		final JsonNode featuresNode = collingwood.get("features");

		assertEquals(41, featuresNode.size(), "Correct amount of child");

		final JsonNode firstNodeChild = featuresNode.get(0);
		final JsonNode typeFeature = firstNodeChild.get("type");
		assertEquals("Feature", typeFeature.textValue(), "Correct type features");

		final JsonNode propertiesFirstNodeChild = firstNodeChild.get("properties");
		assertEquals(0.005235987755982988, propertiesFirstNodeChild.get("course").doubleValue(), 1e-8,
				"Correct course ");
		assertEquals(3.5, propertiesFirstNodeChild.get("speed").doubleValue(), 1e-8, "Correct speed ");
		// assertEquals("Correct time ", "1995/12/12 06:03:00+0100",
		// propertiesFirstNodeChild.get("time").textValue());

		final JsonNode geometryFirstNodeChild = firstNodeChild.get("geometry");
		assertEquals("Point", geometryFirstNodeChild.get("type").textValue(), "Correct type for geometry");
		final JsonNode coordinates = geometryFirstNodeChild.get("coordinates");

		assertEquals(-21.593774999999997, coordinates.get(0).asDouble(), 1e-8, "Correct X coordinate ");
		assertEquals(21.894219444444445, coordinates.get(1).asDouble(), 1e-8, "Correct Y coordinate ");

		final JsonNode lastNodeChild = featuresNode.get(featuresNode.size() - 1);
		final JsonNode lastTypeFeature = lastNodeChild.get("type");
		assertEquals("Feature", lastTypeFeature.textValue(), "Correct type features");

		final JsonNode propertiesLastNodeChild = lastNodeChild.get("properties");
		assertEquals(0.35255650890285456, propertiesLastNodeChild.get("course").doubleValue(), 1e-8,
				"Correct course ");
		assertEquals(3.5, propertiesLastNodeChild.get("speed").doubleValue(), 1e-8, "Correct speed ");
		// assertEquals("Correct time ", "1995/12/12 12:43:00+0100",
		// propertiesLastNodeChild.get("time").textValue());

		final JsonNode geometryLastNodeChild = firstNodeChild.get("geometry");
		assertEquals("Point", geometryLastNodeChild.get("type").textValue(), "Correct type for geometry");
		final JsonNode lastCoordinates = geometryLastNodeChild.get("coordinates");

		assertEquals(-21.593774999999997, lastCoordinates.get(0).asDouble(), 1e-8, "Correct X coordinate ");
		assertEquals(21.894219444444445, lastCoordinates.get(1).asDouble(), 1e-8, "Correct Y coordinate ");
	}

	@Test
	public void testCreateGeoJSON4() throws FileNotFoundException, JsonProcessingException {
		final Layers layers = new Layers();
		final ImportReplay replayImporter = new ImportReplay();
		replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
		final TrackWrapper track = (TrackWrapper) layers.findLayer("COLLINGWOOD", true);

		final String geoJsonData = GenerateGeoJSON.createGeoJSONTrackPoints(track,
				new GeoJSONConfiguration(20, true, false, null, null, null));

		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode collingwood = mapper.readValue(geoJsonData, ObjectNode.class);

		final JsonNode collingWoodName = collingwood.get("name");
		assertEquals("COLLINGWOOD", collingWoodName.textValue(), "Correct name");
		assertEquals("FeatureCollection", collingwood.get("type").textValue(), "Correct type");

		final JsonNode crsNode = collingwood.get("crs");

		final JsonNode crsType = crsNode.get("type");

		assertEquals("name", crsType.textValue(), "Correct crs name");

		final JsonNode crsProperty = crsNode.get("properties");

		final JsonNode crsPropertyName = crsProperty.get("name");

		assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", crsPropertyName.textValue(), "Correct crs property name");

		final JsonNode featuresNode = collingwood.get("features");

		assertEquals(21, featuresNode.size(), "Correct amount of child");

		final JsonNode firstNodeChild = featuresNode.get(0);
		final JsonNode typeFeature = firstNodeChild.get("type");
		assertEquals("Feature", typeFeature.textValue(), "Correct type features");

		final JsonNode propertiesFirstNodeChild = firstNodeChild.get("properties");
		assertEquals(0.005235987755982988, propertiesFirstNodeChild.get("course").doubleValue(), 1e-8,
				"Correct course ");
		assertEquals(3.5, propertiesFirstNodeChild.get("speed").doubleValue(), 1e-8, "Correct speed ");
		// assertEquals("Correct time ", "1995/12/12 06:03:00+0100",
		// propertiesFirstNodeChild.get("time").textValue());

		final JsonNode geometryFirstNodeChild = firstNodeChild.get("geometry");
		assertEquals("Point", geometryFirstNodeChild.get("type").textValue(), "Correct type for geometry");
		final JsonNode coordinates = geometryFirstNodeChild.get("coordinates");

		assertEquals(-21.593774999999997, coordinates.get(0).asDouble(), 1e-8, "Correct X coordinate ");
		assertEquals(21.894219444444445, coordinates.get(1).asDouble(), 1e-8, "Correct Y coordinate ");

		final JsonNode lastNodeChild = featuresNode.get(featuresNode.size() - 1);
		final JsonNode lastTypeFeature = lastNodeChild.get("type");
		assertEquals("Feature", lastTypeFeature.textValue(), "Correct type features");

		final JsonNode propertiesLastNodeChild = lastNodeChild.get("properties");
		assertEquals(0.35255650890285456, propertiesLastNodeChild.get("course").doubleValue(), 1e-8,
				"Correct course ");
		assertEquals(3.5, propertiesLastNodeChild.get("speed").doubleValue(), 1e-8, "Correct speed ");
		// assertEquals("Correct time ", "1995/12/12 12:43:00+0100",
		// propertiesLastNodeChild.get("time").textValue());

		final JsonNode geometryLastNodeChild = firstNodeChild.get("geometry");
		assertEquals("Point", geometryLastNodeChild.get("type").textValue(), "Correct type for geometry");
		final JsonNode lastCoordinates = geometryLastNodeChild.get("coordinates");

		assertEquals(-21.593774999999997, lastCoordinates.get(0).asDouble(), 1e-8, "Correct X coordinate ");
		assertEquals(21.894219444444445, lastCoordinates.get(1).asDouble(), 1e-8, "Correct Y coordinate ");
	}
}
