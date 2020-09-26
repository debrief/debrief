package Debrief.ReaderWriter.GeoPDF;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GenerateSegmentedGeoJSON {

	public static enum GeometryType{MultiLineString, Point};
	
	public static class SegmentedGeoJSONConfiguration {
		private String name;
		private HashMap<String, String> properties = new HashMap<>();
		private GeometryType type;
		private ArrayList< double[] > coordinates = new ArrayList<>();
		
		public SegmentedGeoJSONConfiguration(String name, GeometryType type) {
			this.name = name;
			this.type = type;
		}
		
		public void addCoordinate(final double[] xy) {
			coordinates.add(xy);
		}
		
		public void addProperty(final String key, final String value) {
			properties.put(key, value);
		}

		public String getName() {
			return name;
		}

		public HashMap<String, String> getProperties() {
			return properties;
		}

		public GeometryType getType() {
			return type;
		}

		public ArrayList<double[]> getCoordinates() {
			return coordinates;
		}
		
		
	}

	public static String createGeoJsonFixSegment(final SegmentedGeoJSONConfiguration configuration) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode jsonRoot = mapper.createObjectNode();
		jsonRoot.put("type", "FeatureCollection");
		jsonRoot.put("name", configuration.getName());
		
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

		for (String propertyName : configuration.getProperties().keySet()) {
			featuresProperty.put(propertyName, configuration.getProperties().get(propertyName));
		}

		featureJson.set("properties", featuresProperty);

		final ObjectNode geometryJson = mapper.createObjectNode();
		featureJson.set("geometry", geometryJson);

		geometryJson.put("type", configuration.getType().name());
		
		final ArrayNode coordinatesJson = mapper.createArrayNode();
		geometryJson.set("coordinates", coordinatesJson);
		
		if (configuration.getType().equals(GeometryType.MultiLineString)) {
			final ArrayNode subcoordinatesArray = mapper.createArrayNode();
			coordinatesJson.add(subcoordinatesArray);
			
			for (double[] fix : configuration.getCoordinates()) {
				final ArrayNode coordinateJson = mapper.createArrayNode();
				coordinateJson.add(fix[0]);
				coordinateJson.add(fix[1]);
				subcoordinatesArray.add(coordinateJson);
			}
		}else {
			for (double[] fix : configuration.getCoordinates()) {
				coordinatesJson.add(fix[0]);
				coordinatesJson.add(fix[1]);
			}
		}
		
		final String featureCollectionContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRoot);

		return featureCollectionContent;
	}

}
