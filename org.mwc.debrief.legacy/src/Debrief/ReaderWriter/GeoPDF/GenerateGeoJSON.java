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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.Watchable;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class GenerateGeoJSON {

	public static class GeoJSONConfiguration {
		private final int timeDeltaMinutes;
		private final boolean isLabel;
		private final boolean onlyFirstPoint;
		private final String layerName;
		private final TimePeriod period;
		private final HiResDate step;

		public GeoJSONConfiguration(final int timeDeltaMinutes, final boolean isLabel, final boolean onlyFirstPoint,
				final String layerName, final TimePeriod period, final HiResDate step) {
			super();
			this.timeDeltaMinutes = timeDeltaMinutes;
			this.isLabel = isLabel;
			this.onlyFirstPoint = onlyFirstPoint;
			this.layerName = layerName;
			this.period = period;
			this.step = step;
		}

		public String getLayerName() {
			return layerName;
		}

		public TimePeriod getPeriod() {
			return period;
		}

		public HiResDate getStep() {
			return step;
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
		private final double x;
		private final double y;

		public Point(final double x, final double y) {
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

		final TimePeriod periodToUse;
		if (configuration.getPeriod() != null) {
			periodToUse = configuration.getPeriod();
		} else {
			periodToUse = new BaseTimePeriod(currentTrack.getStartDTG(), currentTrack.getEndDTG());
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
		featuresProperty.put("begin", simpleDateFormat.format(periodToUse.getStartDTG().getDate()));
		featuresProperty.put("end", simpleDateFormat.format(periodToUse.getEndDTG().getDate()));

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
							if (currentFixWrapper.getVisible() && periodToUse.contains(currentFixWrapper.getDTG())) {
								// Ok, so now let's add this position to the position list.
								final Point newPosition = new Point(currentFixWrapper.getLocation().getLong(),
										currentFixWrapper.getLocation().getLat());
								coordinates.add(newPosition);
							}
						}

						/**
						 * In case that we have any coordinate to add, let's add it to the geometry.
						 */
						for (final Point coord : coordinates) {
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
		final TimePeriod periodToUse;
		if (configuration.getPeriod() != null) {
			periodToUse = configuration.getPeriod();
		} else {
			periodToUse = new BaseTimePeriod(currentTrack.getStartDTG(), currentTrack.getEndDTG());
		}
		HiResDate currentTime = HiResDate.max(currentTrack.getStartDTG(), periodToUse.getStartDTG());

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

		final HiResDate topPeriodEnd = HiResDate.min(periodToUse.getEndDTG(), currentTrack.getEndDTG());
		while (currentTime.lessThan(topPeriodEnd)) {
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
}
