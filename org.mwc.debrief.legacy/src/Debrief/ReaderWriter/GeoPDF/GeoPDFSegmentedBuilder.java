package Debrief.ReaderWriter.GeoPDF;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class GeoPDFSegmentedBuilder extends AbstractGeoPDFBuilder {

	/** the background charts, as one layer, if there are any */
	private static void addBackgroundLayer(final GeoPDFConfiguration configuration, final GeoPDF geoPDF,
			final GeoPDFPage page) throws IOException {
		final GeoPDFLayerBackground backgroundLayer = new GeoPDFLayerBackground();
		backgroundLayer.setName("Background chart");
		backgroundLayer.setId("background");
		for (final String background : configuration.getBackground()) {
			final GeoPDF.GeoPDFRaster raster = createBackground(configuration, background);
			if (raster != null) {
				geoPDF.getFilesToDelete().add(raster.getImage());
				backgroundLayer.addRaster(raster);
			}
		}
		if (!backgroundLayer.getRasters().isEmpty()) {
			page.addLayer(backgroundLayer);
		}
	}

	/** a unique layer name for a time step, from its start time */
	private static String periodName(final GeoPDFConfiguration configuration, final TimePeriod period,
			final HashSet<String> usedNames) {
		// without a configured format, use the time controller's default (ddHHmm).
		// HiResDate.toString() isn't for display: it includes the Java object's
		// identity, which then showed in the GeoPDF's time label.
		final String format = configuration.getDateFormat() != null ? configuration.getDateFormat()
				: MWC.GUI.Properties.DateFormatPropertyEditor.DATE_FORMAT;
		String periodName = new SimpleDateFormat(format).format(period.getStartDTG().getDate());
		if (usedNames.contains(periodName)) {
			int suffix = 2;
			while (usedNames.contains(periodName + "_" + suffix)) {
				++suffix;
			}
			periodName = periodName + "_" + suffix;
		}
		usedNames.add(periodName);
		return periodName;
	}

	/** the tracks to export: the visible ones */
	private static List<TrackWrapper> visibleTracks(final Layers layers) {
		final List<TrackWrapper> tracks = new ArrayList<>();
		final Enumeration<Editable> enumeration = layers.elements();
		while (enumeration.hasMoreElements()) {
			final Editable currentEditable = enumeration.nextElement();
			if (currentEditable instanceof TrackWrapper && ((TrackWrapper) currentEditable).getVisible()) {
				tracks.add((TrackWrapper) currentEditable);
			}
		}
		return tracks;
	}

	/**
	 * a track's line, time ticks, time labels and name, for the whole track (null
	 * period) or one time step
	 */
	private void addTrackContent(final GeoPDFConfiguration configuration, final GeoPDF geoPDF,
			final TrackWrapper currentTrack, final GeoPDFLayerTrack trackLayer, final TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {
		createTrackLine(configuration, geoPDF.getFilesToDelete(), currentTrack, trackLayer, period);
		createTicksLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, trackLayer, period);
		createLabelsLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, trackLayer, period);
		createTrackNameLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, trackLayer, period);
	}

	@Override
	public GeoPDF build(final Layers layers, final GeoPDFConfiguration configuration)
			throws IOException, InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, ClassNotFoundException {

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

		addBackgroundLayer(configuration, geoPDF, mainPage);

		final List<TrackWrapper> tracks = visibleTracks(layers);

		/**
		 * the non-interactive layers: each whole track
		 */
		final StringBuilder javascriptNonInteractiveLayerIndex = new StringBuilder();
		javascriptNonInteractiveLayerIndex.append("var nonInteractiveLayers = [");
		for (final TrackWrapper currentTrack : tracks) {
			final GeoPDFLayerTrack newTrackLayer = new GeoPDFLayerTrack();
			mainPage.addLayer(newTrackLayer);
			newTrackLayer.setId(currentTrack.getName());
			newTrackLayer.setName(currentTrack.getName() + NON_INTERACTIVE_SUFFIX);
			javascriptNonInteractiveLayerIndex.append('\'' + currentTrack.getName() + NON_INTERACTIVE_SUFFIX + '\'' + ',');
			addTrackContent(configuration, geoPDF, currentTrack, newTrackLayer, null);
		}
		javascriptNonInteractiveLayerIndex.append("];");

		/**
		 * the interactive layers: each track within each time step
		 */
		final GeoPDFLayerTrack interactiveLayer = new GeoPDFLayerTrack();
		interactiveLayer.setId(INTERACTIVE_LAYER_NAME);
		interactiveLayer.setName(INTERACTIVE_LAYER_NAME);
		mainPage.addLayer(interactiveLayer);

		final ObjectMapper mapper = new ObjectMapper();
		final ArrayNode jsonTimestamps = mapper.createArrayNode();
		final HashSet<String> usedNames = new HashSet<>();

		HiResDate currentTime = configuration.getStartTime();
		while (currentTime.lessThan(configuration.getEndTime())) {
			final HiResDate topCurrentPeriod = HiResDate.min(
					new HiResDate(currentTime.getMicros() / 1000 + configuration.getStepDeltaMilliSeconds()),
					configuration.getEndTime());
			final TimePeriod period = new TimePeriod.BaseTimePeriod(currentTime, topCurrentPeriod);
			final String periodName = periodName(configuration, period, usedNames);

			final GeoPDFLayerTrack periodTrack = new GeoPDFLayerTrack();
			interactiveLayer.addChild(periodTrack);
			periodTrack.setId(periodName);
			periodTrack.setName(periodName);

			final ObjectNode timeStampNode = mapper.createObjectNode();
			timeStampNode.put("name", periodName);
			timeStampNode.put("ocg_name", periodName);
			jsonTimestamps.add(timeStampNode);

			for (final TrackWrapper currentTrack : tracks) {
				final GeoPDFLayerTrack newTrackLayer = new GeoPDFLayerTrack();
				periodTrack.addChild(newTrackLayer);
				newTrackLayer.setId(currentTrack.getName() + " " + periodName);
				newTrackLayer.setName(currentTrack.getName());
				addTrackContent(configuration, geoPDF, currentTrack, newTrackLayer, period);
			}
			currentTime = topCurrentPeriod;
		}

		final String javaScriptReplacementJsTimestamps = "var timestamps = "
				+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonTimestamps) + ";";

		geoPDF.setJavascript(createJavascriptContent(javaScriptReplacementJsTimestamps,
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