package Debrief.ReaderWriter.GeoPDF;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.GeoPDF.GenerateSegmentedGeoJSON.GeometryType;
import Debrief.ReaderWriter.GeoPDF.GenerateSegmentedGeoJSON.SegmentedGeoJSONConfiguration;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerBackground;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVector;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVector.LogicalStructure;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVectorLabel;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFPage;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import junit.framework.TestCase;

public class GeoPDFSegmentedBuilder extends AbstractGeoPDFBuilder {

	public static class GeoPDFSegmentedBuilderTest extends TestCase {
		private final static String boat1rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";
		private final static String boat2rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat2.rep";

		public void testBuild() throws IOException, InterruptedException, NoSuchFieldException, SecurityException,
				IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
			replayImporter.importThis("boat2.rep", new FileInputStream(boat2rep), layers);

			final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			configuration.setDateFormat("ddMMyyyyHHmmss");
			configuration.addBackground("../org.mwc.cmap.combined.feature/root_installs/sample_data/SP27GTIF.tif");

			final AbstractGeoPDFBuilder builder = new GeoPDFSegmentedBuilder();
			final GeoPDF geoPdf = builder.build(layers, configuration);

			configuration.setPdfOutputPath(System.getProperty("java.io.tmpdir") + File.separatorChar + "test.pdf");
			builder.generatePDF(geoPdf, configuration);

			System.out.println("PDF successfully generated at " + configuration.getPdfOutputPath());
			final GeoPDFPage page = geoPdf.getPages().get(0);

			assertEquals("Correct DPI in the GeoPDF", 72, page.getDpi());
			assertEquals("Correct height in the GeoPDF", 595.14, page.getHeight(), 1e-8);
			assertEquals("Correct Margin", .1, page.getMargin(), 1e-8);
			assertEquals("Correct width in the GeoPDF", 841.698, page.getWidth(), 1e-8);
			final GeoPDFLayerBackground backgroundLayer = (GeoPDFLayerBackground) page.getLayers().get(0);
			assertEquals("Correct Background Layer", "Background chart", backgroundLayer.getName());
			assertEquals("Correct ID for background", "background", backgroundLayer.getId());
			assertTrue("Correct Background tif", backgroundLayer.getRasters().get(0).endsWith("SP27GTIF.tif"));
			final GeoPDFLayerTrack nelsonNonInteractıve = (GeoPDFLayerTrack) page.getLayers().get(1);
			assertEquals("Correct NELSON non interactive name", "NELSON (non-interactive)",
					nelsonNonInteractıve.getName());
			assertEquals("Correct NELSON non interactive id", "NELSON", nelsonNonInteractıve.getId());
			assertEquals("Correct amount of layers in the vectors in nelson", 402,
					nelsonNonInteractıve.getVectors().size());
			final GeoPDFLayerVector firstVectorNelsonNonInteractıve = nelsonNonInteractıve.getVectors().get(0);
			assertTrue("Correct first data file",
					firstVectorNelsonNonInteractıve.getData().endsWith("NELSON_LINE_818744400000.geojson"));
			assertEquals("Correct first data name", "NELSON_LINE_818744400000",
					firstVectorNelsonNonInteractıve.getName());
			assertEquals("Correct first data style", "PEN(c:#e01c3e,w:5px)",
					firstVectorNelsonNonInteractıve.getStyle());
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode rootVectorAsJson = mapper
					.readTree(new FileInputStream(firstVectorNelsonNonInteractıve.getData()));
			assertEquals("Correct type in the geojson", "FeatureCollection", rootVectorAsJson.get("type").asText());
			assertEquals("Correct name in the geojson", "NELSON_LINE_818744400000",
					rootVectorAsJson.get("name").asText());

			final ArrayNode features = (ArrayNode) rootVectorAsJson.get("features");
			final JsonNode feature = features.get(0);

			assertEquals("Correct type in the feature geojson", "Feature", feature.get("type").asText());
			final JsonNode properties = feature.get("properties");
			assertEquals("Correct end property", "818744460000", properties.get("end").asText());
			assertEquals("Correct begin property", "818744400000", properties.get("begin").asText());

			final JsonNode geometry = feature.get("geometry");
			assertEquals("Correct geometry type", "MultiLineString", geometry.get("type").asText());
			final ArrayNode coordinates = (ArrayNode) ((ArrayNode) geometry.get("coordinates")).get(0);
			final ArrayNode coordinatesStart = (ArrayNode) coordinates.get(0);
			final ArrayNode coordinatesEnd = (ArrayNode) coordinates.get(1);
			assertEquals("X coordinate of the Start", -21.697880555555557, coordinatesStart.get(0).doubleValue(), 1e-8);
			assertEquals("Y coordinate of the Start", 22.186286111111112, coordinatesStart.get(1).doubleValue(), 1e-8);

			assertEquals("X coordinate of the End", -21.70082777777778, coordinatesEnd.get(0).doubleValue(), 1e-8);
			assertEquals("Y coordinate of the End", 22.18627222222222, coordinatesEnd.get(1).doubleValue(), 1e-8);

			final GeoPDFLayerTrack collinwoodNonInteractıve = (GeoPDFLayerTrack) page.getLayers().get(2);
			assertEquals("Correct NELSON non interactive name", "COLLINGWOOD (non-interactive)",
					collinwoodNonInteractıve.getName());
			assertEquals("Correct NELSON non interactive id", "COLLINGWOOD", collinwoodNonInteractıve.getId());
			assertEquals("Correct amount of layers in the vectors in nelson", 403,
					collinwoodNonInteractıve.getVectors().size());

			final GeoPDFLayerTrack interactiveLayers = (GeoPDFLayerTrack) page.getLayers().get(3);
			assertEquals("Correct interactive name", "Interactive Layers", interactiveLayers.getName());
			assertEquals("Correct interactive id", "Interactive Layers", interactiveLayers.getId());
			assertEquals("Correct amount of interactive layers", 27, interactiveLayers.getChildren().size());

			final GeoPDFLayerTrack firstLayer = interactiveLayers.getChildren().get(0);
			assertEquals("Correct id in interactive", "12121995050000", firstLayer.getId());
			assertEquals("Correct name in interactive", "12121995050000", firstLayer.getName());
			assertEquals("Correct amount of sublayers", 2, firstLayer.getChildren().size());
			final GeoPDFLayerTrack firstLayerNelson = firstLayer.getChildren().get(0);
			assertEquals("Nelson sublayer id", "NELSON 12121995050000", firstLayerNelson.getId());
			assertEquals("Nelson sublayer name", "NELSON", firstLayerNelson.getName());
			final GeoPDFLayerTrack firstLayerCollinwood = firstLayer.getChildren().get(1);
			assertEquals("COLLINGWOOD sublayer id", "COLLINGWOOD 12121995050000", firstLayerCollinwood.getId());
			assertEquals("COLLINGWOOD sublayer name", "COLLINGWOOD", firstLayerCollinwood.getName());

		}
	}

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
		for (final String background : configuration.getBackground()) {
			final File backgroundFile = createBackgroundFile(configuration, background, geoPDF.getFilesToDelete());
			backgroundLayer.addRaster(backgroundFile.getAbsolutePath());
		}
		if (!backgroundLayer.getRasters().isEmpty()) {
			mainPage.addLayer(backgroundLayer);
		}

		/**
		 * Let's add now the non-interactive layers
		 */
		final ObjectMapper mapper = new ObjectMapper();
		final StringBuilder javascriptNonInteractiveLayerIndex = new StringBuilder();
		javascriptNonInteractiveLayerIndex.append("var nonInteractiveLayers = [");

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
							.append('\'' + currentTrack.getName() + NON_INTERACTIVE_SUFFIX + '\'' + ',');

					/**
					 * TrackLine
					 */
					createTrackLine(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer, null);

					// Let's create now the point-type vectors
					/**
					 * Minutes difference Layer
					 */
					createTicksLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer, null);

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
		final ArrayNode jsonTimestamps = mapper.createArrayNode();

		final HiResDate startTime;
		if (configuration.getStartTime() != null) {
			startTime = configuration.getStartTime();
		} else {
			startTime = layers.getTimePeriod().getStartDTG();
		}
		final HiResDate endTime;
		if (configuration.getEndTime() != null) {
			endTime = configuration.getEndTime();
		} else {
			endTime = layers.getTimePeriod().getEndDTG();
		}
		
		HiResDate currentTime = startTime;
		final HashSet<String> usedNames = new HashSet<>();

		while (currentTime.lessThan(endTime)) {
			final HiResDate topCurrentPeriod = HiResDate.min(
					new HiResDate(currentTime.getMicros() / 1000 + configuration.getStepDeltaMilliSeconds()), endTime);
			final TimePeriod period = new TimePeriod.BaseTimePeriod(currentTime, topCurrentPeriod);

			final GeoPDFLayerTrack periodTrack = new GeoPDFLayerTrack();
			String periodName;
			if (configuration.getDateFormat() != null) {
				periodName = new SimpleDateFormat(configuration.getDateFormat()).format(period.getStartDTG().getDate());
			} else {
				periodName = period.getStartDTG().toString();
			}
			if (usedNames.contains(periodName)) {
				int suffix = 2;
				while (usedNames.contains(periodName + "_" + suffix)) {
					++suffix;
				}
				periodName = periodName + "_" + suffix;
			}
			usedNames.add(periodName);

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
						createTicksLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer, period);

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

		final FixWrapper[] fixes = currentTrack.getFixes();
		for (int i = 0; i < fixes.length; i++) {
			if ((period == null || period.contains(fixes[i].getDTG())) && fixes[i].getLabelShowing()) {
				final String vectorName = sanitizeFilename(
						currentTrack.getName() + "_LABEL_" + HiResDateToFileName(fixes[i].getDTG()));
				final SegmentedGeoJSONConfiguration segmentConfiguration = new SegmentedGeoJSONConfiguration(vectorName,
						GeometryType.Point);
				segmentConfiguration.addProperty("elevation", fixes[i].getLocation().getDepth() + "");
				segmentConfiguration.addProperty("longitude", fixes[i].getLocation().getLong() + "");
				segmentConfiguration.addProperty("latitude", fixes[i].getLocation().getLat() + "");
				segmentConfiguration.addProperty("time", HiResDateToFileName(fixes[i].getDTG()));
				segmentConfiguration.addProperty("course", fixes[i].getCourse() + "");
				segmentConfiguration.addProperty("speed", fixes[i].getSpeed() + "");
				segmentConfiguration.addProperty("time_str",
						FormatRNDateTime.toShortString(fixes[i].getTime().getDate().getTime()));
				segmentConfiguration.addCoordinate(
						new double[] { fixes[i].getLocation().getLong(), fixes[i].getLocation().getLat() });

				final String vectorLabelData = GenerateSegmentedGeoJSON.createGeoJsonFixSegment(segmentConfiguration);
				final File labelFile = createTempFile(vectorName + ".geojson", vectorLabelData,
						configuration.getTempFolder());
				filesToDelete.add(labelFile);

				final GeoPDFLayerVectorLabel segmentLabelVector = new GeoPDFLayerVectorLabel();
				segmentLabelVector.setData(labelFile.getAbsolutePath());
				segmentLabelVector.setName(vectorName);

				final Color vectorColor = fixes[i].getColor();
				final String colorHex = String.format("#%02x%02x%02x", vectorColor.getRed(), vectorColor.getGreen(),
						vectorColor.getBlue());
				segmentLabelVector.setStyle("SYMBOL(c:" + colorHex + ",s:2,id:\"ogr-sym-3\")");
				segmentLabelVector.setStyle("LABEL(t:{time_str},c:" + colorHex + ",s:24pt,p:4,dx:7mm,bo:1)");

				newTrackLayer.addVector(segmentLabelVector);
			}
		}
	}

	public void createTicksLayer(final GeoPDFConfiguration configuration, final ArrayList<File> filesToDelete,
			final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer, final TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {

		final FixWrapper[] fixes = currentTrack.getFixes();
		for (int i = 0; i < fixes.length; i++) {
			if ((period == null || period.contains(fixes[i].getDTG())) && fixes[i].getSymbolShowing()) {
				final String vectorName = sanitizeFilename(
						currentTrack.getName() + "_TICKS_" + HiResDateToFileName(fixes[i].getDTG()));
				final SegmentedGeoJSONConfiguration segmentConfiguration = new SegmentedGeoJSONConfiguration(vectorName,
						GeometryType.Point);
				segmentConfiguration.addProperty("elevation", fixes[i].getLocation().getDepth() + "");
				segmentConfiguration.addProperty("longitude", fixes[i].getLocation().getLong() + "");
				segmentConfiguration.addProperty("latitude", fixes[i].getLocation().getLat() + "");
				segmentConfiguration.addProperty("time", HiResDateToFileName(fixes[i].getDTG()));
				segmentConfiguration.addProperty("course", fixes[i].getCourse() + "");
				segmentConfiguration.addProperty("speed", fixes[i].getSpeed() + "");
				segmentConfiguration.addCoordinate(
						new double[] { fixes[i].getLocation().getLong(), fixes[i].getLocation().getLat() });

				final String vectorTickData = GenerateSegmentedGeoJSON.createGeoJsonFixSegment(segmentConfiguration);
				final File tickFile = createTempFile(vectorName + ".geojson", vectorTickData,
						configuration.getTempFolder());
				filesToDelete.add(tickFile);

				final GeoPDFLayerVector segmentTickVector = new GeoPDFLayerVector();
				segmentTickVector.setData(tickFile.getAbsolutePath());
				segmentTickVector.setName(vectorName);

				final Color vectorColor = fixes[i].getColor();
				final String colorHex = String.format("#%02x%02x%02x", vectorColor.getRed(), vectorColor.getGreen(),
						vectorColor.getBlue());
				segmentTickVector.setStyle("SYMBOL(c:" + colorHex + ",s:2,id:\"ogr-sym-3\")");

				segmentTickVector.setLogicalStructure(new LogicalStructure(currentTrack.getName(), "time"));

				newTrackLayer.addVector(segmentTickVector);
			}
		}
	}

	@Override
	protected void createTrackLine(final GeoPDFConfiguration configuration, final ArrayList<File> filesToDelete,
			final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer, final TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {
		final FixWrapper[] fixes = currentTrack.getFixes();
		for (int i = 0; i < fixes.length - 1; i++) {
			if (period == null || period.contains(fixes[i].getDTG())) {
				final String vectorName = sanitizeFilename(
						currentTrack.getName() + "_LINE_" + HiResDateToFileName(fixes[i].getDTG()));
				final SegmentedGeoJSONConfiguration configurationGeojson = new SegmentedGeoJSONConfiguration(vectorName,
						GeometryType.MultiLineString);
				configurationGeojson.addProperty("begin", HiResDateToFileName(fixes[i].getDTG()));
				configurationGeojson.addProperty("end", HiResDateToFileName(fixes[i + 1].getDTG()));
				configurationGeojson.addCoordinate(
						new double[] { fixes[i].getLocation().getLong(), fixes[i].getLocation().getLat() });
				configurationGeojson.addCoordinate(
						new double[] { fixes[i + 1].getLocation().getLong(), fixes[i + 1].getLocation().getLat() });

				final String vectorSegmentTrackLineData = GenerateSegmentedGeoJSON
						.createGeoJsonFixSegment(configurationGeojson);
				final File trackLineFile = createTempFile(vectorName + ".geojson", vectorSegmentTrackLineData,
						configuration.getTempFolder());
				filesToDelete.add(trackLineFile);

				final GeoPDFLayerVector segmentTrackLineVector = new GeoPDFLayerVector();
				segmentTrackLineVector.setData(trackLineFile.getAbsolutePath());
				segmentTrackLineVector.setName(vectorName);

				final Color vectorColor = fixes[i].getColor();
				final String colorHex = String.format("#%02x%02x%02x", vectorColor.getRed(), vectorColor.getGreen(),
						vectorColor.getBlue());
				segmentTrackLineVector.setStyle("PEN(c:" + colorHex + ",w:5px)");

				newTrackLayer.addVector(segmentTrackLineVector);
			}
		}
	}

	@Override
	protected void createTrackNameLayer(final GeoPDFConfiguration configuration, final ArrayList<File> filesToDelete,
			final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer, final TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {
		final FixWrapper fix = currentTrack.getFixes()[0];
		if ((period == null || period.contains(fix.getDTG()))) {
			final String vectorName = sanitizeFilename(currentTrack.getName() + "_FIRST_POINT");
			final SegmentedGeoJSONConfiguration segmentConfiguration = new SegmentedGeoJSONConfiguration(vectorName,
					GeometryType.Point);
			segmentConfiguration.addProperty("elevation", fix.getLocation().getDepth() + "");
			segmentConfiguration.addProperty("longitude", fix.getLocation().getLong() + "");
			segmentConfiguration.addProperty("latitude", fix.getLocation().getLat() + "");
			segmentConfiguration.addProperty("time", HiResDateToFileName(fix.getDTG()));
			segmentConfiguration.addProperty("course", fix.getCourse() + "");
			segmentConfiguration.addProperty("speed", fix.getSpeed() + "");
			segmentConfiguration
					.addCoordinate(new double[] { fix.getLocation().getLong(), fix.getLocation().getLat() });

			final String vectorLabelData = GenerateSegmentedGeoJSON.createGeoJsonFixSegment(segmentConfiguration);
			final File labelFile = createTempFile(vectorName + ".geojson", vectorLabelData,
					configuration.getTempFolder());
			filesToDelete.add(labelFile);

			final GeoPDFLayerVectorLabel segmentLabelVector = new GeoPDFLayerVectorLabel();
			segmentLabelVector.setData(labelFile.getAbsolutePath());
			segmentLabelVector.setName(vectorName);

			final Color vectorColor = fix.getColor();
			final String colorHex = String.format("#%02x%02x%02x", vectorColor.getRed(), vectorColor.getGreen(),
					vectorColor.getBlue());
			segmentLabelVector.setStyle(
					"LABEL(t:\"" + currentTrack.getName() + "\",c: " + colorHex + ",s:28pt,p:2,dy:10mm,bo:1)");

			newTrackLayer.addVector(segmentLabelVector);
		}
	}

}