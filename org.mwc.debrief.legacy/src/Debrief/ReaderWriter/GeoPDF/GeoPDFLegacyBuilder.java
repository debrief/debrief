package Debrief.ReaderWriter.GeoPDF;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.GeoPDF.GenerateGeoJSON.GeoJSONConfiguration;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerBackground;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVector;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVector.LogicalStructure;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVectorLabel;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFPage;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

public class GeoPDFLegacyBuilder extends AbstractGeoPDFBuilder {

	@Override
	public GeoPDF build(final Layers layers, final GeoPDFConfiguration configuration)
			throws IOException, InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, ClassNotFoundException {
		if (!configuration.isReady()) {
			Application.logError3(ToolParent.INFO, "GeoPDF-GDAL Temporary environment is about to be prepared.", null,
					false);
			configuration.prepareGdalEnvironment();
		}
		final GeoPDF geoPDF = new GeoPDF();

		geoPDF.setAuthor(configuration.getAuthor());

		/**
		 * For now let's work using only one page, but we will fix it eventually to have
		 * several pages.
		 */
		final GeoPDFPage mainPage = geoPDF.createNewPage();

		mainPage.setDpi(configuration.getPageDpi());
		mainPage.setWidth(configuration.getPageWidth());
		mainPage.setHeight(configuration.getPageHeight());
		mainPage.setMargin(configuration.getMarginPercent());
		if (configuration.getViewportArea() == null) {
			mainPage.setArea(layers.getBounds());
		} else {
			mainPage.setArea(configuration.getViewportArea());
		}

		/**
		 * Let's create the BackGroundLayer;
		 */
		final GeoPDFLayerBackground backgroundLayer = new GeoPDFLayerBackground();
		backgroundLayer.setName("Background chart");
		backgroundLayer.setId("background");
		for (String background : configuration.getBackground()) {
			final File backgroundFile = createBackgroundFile(configuration, background, geoPDF.getFilesToDelete());
			backgroundLayer.addRaster(backgroundFile.getAbsolutePath());
		}
		if (!backgroundLayer.getRasters().isEmpty()) {
			mainPage.addLayer(backgroundLayer);
		}

		final StringBuilder javascriptNonInteractiveLayerIndex = new StringBuilder();
		javascriptNonInteractiveLayerIndex.append("var nonInteractiveLayers = [");

		/**
		 * Let's add now the non-interactive layers
		 */

		/**
		 * Let's iterate over all the layers to find the Tracks to export
		 */
		final Enumeration<Editable> enumerationNonInteractive = layers.elements();
		while (enumerationNonInteractive.hasMoreElements()) {
			final Editable currentEditable = enumerationNonInteractive.nextElement();
			if (currentEditable instanceof TrackWrapper) {
				/**
				 * Ok, at this point we have a TrackWrapper. Now, let's create a Geometry of the
				 * type Simple Features Geotools Library.
				 */
				final TrackWrapper currentTrack = (TrackWrapper) currentEditable;

				/**
				 * Let's draw only visible tracks.
				 */
				if (currentTrack.getVisible()) {

					final GeoPDFLayerTrack newTrackLayer = new GeoPDFLayerTrack();
					mainPage.addLayer(newTrackLayer);

					newTrackLayer.setId(currentTrack.getName());
					newTrackLayer.setName(currentTrack.getName() + NON_INTERACTIVE_SUFFIX);
					javascriptNonInteractiveLayerIndex
							.append('\'' + currentTrack.getName() + NON_INTERACTIVE_SUFFIX + '\'' + ",");

					/**
					 * TrackLine
					 */
					createTrackLine(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer, null);

					// Let's create now the point-type vectors
					/**
					 * Minutes difference Layer
					 */
					createMinutesLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer, null);

					/**
					 * Label Layer
					 */

					createLabelsLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer, null);

					/**
					 * One point layer
					 */
					createTrackNameLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer, null);

				}

			}

		}
		javascriptNonInteractiveLayerIndex.append("];");

		/**
		 * Now the interactive Layers
		 */
		final GeoPDFLayerTrack interactiveLayer = new GeoPDFLayerTrack();
		interactiveLayer.setId(INTERACTIVE_LAYER_NAME);
		interactiveLayer.setName(INTERACTIVE_LAYER_NAME);
		mainPage.addLayer(interactiveLayer);

		final StringBuilder javaScriptReplacementJsTimestamps = new StringBuilder();
		javaScriptReplacementJsTimestamps.append("var timestamps = ");
		final ObjectMapper mapper = new ObjectMapper();
		final ArrayNode jsonTimestamps = mapper.createArrayNode();

		HiResDate currentTime = configuration.getStartTime();

		while (currentTime.lessThan(configuration.getEndTime())) {
			final HiResDate topCurrentPeriod = HiResDate.min(
					new HiResDate(currentTime.getMicros() / 1000 + configuration.getStepDeltaMilliSeconds()),
					configuration.getEndTime());
			final TimePeriod period = new TimePeriod.BaseTimePeriod(currentTime, topCurrentPeriod);

			final GeoPDFLayerTrack periodTrack = new GeoPDFLayerTrack();
			final String periodName;
			if (configuration.getDateFormat() != null) {
				periodName = new SimpleDateFormat(configuration.getDateFormat()).format(period.getStartDTG().getDate());
			} else {
				periodName = period.getStartDTG().toString();
			}

			interactiveLayer.addChild(periodTrack);
			periodTrack.setId(periodName);
			periodTrack.setName(periodName);

			final ObjectNode timeStampNode = mapper.createObjectNode();
			timeStampNode.put("name", periodName);
			timeStampNode.put("ocg_name", periodName);
			jsonTimestamps.add(timeStampNode);

			/**
			 * Let's iterate over all the layers to find the Tracks to export
			 */
			final Enumeration<Editable> enumeration = layers.elements();
			while (enumeration.hasMoreElements()) {
				final Editable currentEditable = enumeration.nextElement();
				if (currentEditable instanceof TrackWrapper) {
					/**
					 * Ok, at this point we have a TrackWrapper. Now, let's create a Geometry of the
					 * type Simple Features Geotools Library.
					 */
					final TrackWrapper currentTrack = (TrackWrapper) currentEditable;

					/**
					 * Let's draw only visible tracks.
					 */
					if (currentTrack.getVisible()) {

						final GeoPDFLayerTrack newTrackLayer = new GeoPDFLayerTrack();
						periodTrack.addChild(newTrackLayer);

						newTrackLayer.setId(currentTrack.getName() + " " + periodName);
						newTrackLayer.setName(currentTrack.getName());

						/**
						 * Let's create the different parts of the layer
						 */
						/**
						 * TrackLine
						 */
						createTrackLine(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer, period);

						// Let's create now the point-type vectors
						/**
						 * Minutes difference Layer
						 */
						createMinutesLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer,
								period);

						/**
						 * Label Layer
						 */

						createLabelsLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer,
								period);

						/**
						 * One point layer
						 */
						createTrackNameLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer,
								period);

					}

				}

			}
			currentTime = topCurrentPeriod;
		}

		final String jsonTimestampsContent = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonTimestamps);
		javaScriptReplacementJsTimestamps.append(jsonTimestampsContent);
		javaScriptReplacementJsTimestamps.append(";");

		geoPDF.setJavascript(createJavascriptContent(javaScriptReplacementJsTimestamps.toString(),
				javascriptNonInteractiveLayerIndex.toString(), configuration.getStepSpeedMilliSeconds() + "",
				JAVASCRIPT_TEMPLATE_PATH));

		return geoPDF;
	}

	@Override
	protected void createLabelsLayer(final GeoPDFConfiguration configuration, final ArrayList<File> filesToDelete,
			final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer, final TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {

		final String fileNameSuffix;
		if (period != null) {
			fileNameSuffix = HiResDateToFileName(period.getStartDTG(), configuration.getDateFormat());
		} else {
			fileNameSuffix = "COMPLETE_PERIOD";
		}
		final String layerName = sanitizeFilename(currentTrack.getName() + "_PointsLabels_"
				+ configuration.getLabelDeltaMinutes() + "mins" + "_" + fileNameSuffix);

		final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(configuration.getLabelDeltaMinutes(),
				true, false, layerName, period, new HiResDate(configuration.getStepDeltaMilliSeconds()));
		final GeoPDFLayerVectorLabel deltaMinutesVector = new GeoPDFLayerVectorLabel();
		final String deltaMinutesVectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack,
				geoJSONConfiguration);

		final File deltaMinutesFile = createTempFile(layerName + ".geojson", deltaMinutesVectorData,
				configuration.getTempFolder());
		filesToDelete.add(deltaMinutesFile);

		deltaMinutesVector.setData(deltaMinutesFile.getAbsolutePath());
		deltaMinutesVector.setName(layerName);
		final Color trackColor = currentTrack.getColor();
		final String colorHex = String.format("#%02x%02x%02x", trackColor.getRed(), trackColor.getGreen(),
				trackColor.getBlue());
		deltaMinutesVector.setStyle("LABEL(t:{time_str},c:" + colorHex + ",s:24pt,p:4,dx:7mm,bo:1)");

		newTrackLayer.addVector(deltaMinutesVector);
	}

	public void createMinutesLayer(final GeoPDFConfiguration configuration, final ArrayList<File> filesToDelete,
			final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer, final TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {
		final String fileNameSuffix;
		if (period != null) {
			fileNameSuffix = HiResDateToFileName(period.getStartDTG(), configuration.getDateFormat());
		} else {
			fileNameSuffix = "COMPLETE_PERIOD";
		}
		final String layerName = sanitizeFilename(currentTrack.getName() + "_Points_"
				+ configuration.getMarkDeltaMinutes() + "mins" + "_" + fileNameSuffix);
		final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(configuration.getMarkDeltaMinutes(),
				false, false, layerName, period, new HiResDate(configuration.getStepDeltaMilliSeconds()));
		final GeoPDFLayerVector deltaMinutesVector = new GeoPDFLayerVector();
		final String deltaMinutesVectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack,
				geoJSONConfiguration);

		final File deltaMinutesFile = createTempFile(layerName + ".geojson", deltaMinutesVectorData,
				configuration.getTempFolder());
		filesToDelete.add(deltaMinutesFile);

		deltaMinutesVector.setData(deltaMinutesFile.getAbsolutePath());
		deltaMinutesVector.setName(layerName);

		final Color trackColor = currentTrack.getColor();
		final String colorHex = String.format("#%02x%02x%02x", trackColor.getRed(), trackColor.getGreen(),
				trackColor.getBlue());
		deltaMinutesVector.setStyle("SYMBOL(c:" + colorHex + ",s:2,id:\"ogr-sym-3\")");

		deltaMinutesVector.setLogicalStructure(new LogicalStructure(currentTrack.getName(), "time"));

		newTrackLayer.addVector(deltaMinutesVector);
	}

	@Override
	protected void createTrackLine(final GeoPDFConfiguration configuration, final ArrayList<File> filesToDelete,
			final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer, final TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {
		final String fileNameSuffix;
		if (period != null) {
			fileNameSuffix = HiResDateToFileName(period.getStartDTG(), configuration.getDateFormat());
		} else {
			fileNameSuffix = "COMPLETE_PERIOD";
		}
		final String layerName = sanitizeFilename(currentTrack.getName() + "_Line_" + fileNameSuffix);
		final GeoPDFLayerVector trackLineVector = new GeoPDFLayerVector();
		final GeoJSONConfiguration configurationJson = new GeoJSONConfiguration(0, false, false, layerName, period,
				null);
		final String vectorTrackLineData = GenerateGeoJSON.createGeoJSONTrackLine(currentTrack, configurationJson);

		final File trackLineFile = createTempFile(layerName + ".geojson", vectorTrackLineData,
				configuration.getTempFolder());
		filesToDelete.add(trackLineFile);

		trackLineVector.setData(trackLineFile.getAbsolutePath());
		trackLineVector.setName(layerName);
		final Color trackColor = currentTrack.getColor();
		final String colorHex = String.format("#%02x%02x%02x", trackColor.getRed(), trackColor.getGreen(),
				trackColor.getBlue());
		trackLineVector.setStyle("PEN(c:" + colorHex + ",w:5px)");

		newTrackLayer.addVector(trackLineVector);
	}

	@Override
	protected void createTrackNameLayer(final GeoPDFConfiguration configuration, final ArrayList<File> filesToDelete,
			final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer, final TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {

		if (period == null || period.contains(currentTrack.getStartDTG())) {
			final String layerName = sanitizeFilename(currentTrack.getName() + "_FirstPoint");
			final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(-1, true, true, layerName, null,
					null);
			final GeoPDFLayerVectorLabel deltaMinutesVector = new GeoPDFLayerVectorLabel();
			final String vectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack, geoJSONConfiguration);

			final File deltaMinutesFile = createTempFile(layerName + ".geojson", vectorData,
					configuration.getTempFolder());
			filesToDelete.add(deltaMinutesFile);

			deltaMinutesVector.setData(deltaMinutesFile.getAbsolutePath());
			deltaMinutesVector.setName(layerName);
			final Color trackColor = currentTrack.getColor();
			final String colorHex = String.format("#%02x%02x%02x", trackColor.getRed(), trackColor.getGreen(),
					trackColor.getBlue());
			deltaMinutesVector.setStyle(
					"LABEL(t:\"" + currentTrack.getName() + "\",c: " + colorHex + ",s:24pt,p:2,dy:10mm,bo:1)");

			newTrackLayer.addVector(deltaMinutesVector);
		}
	}
}