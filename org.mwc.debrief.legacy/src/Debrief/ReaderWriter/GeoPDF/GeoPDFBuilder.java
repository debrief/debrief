package Debrief.ReaderWriter.GeoPDF;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import com.fasterxml.jackson.core.JsonProcessingException;

import Debrief.ReaderWriter.GeoPDF.GenerateGeoJSON.GeoJSONConfiguration;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerBackground;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVector;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVector.LogicalStructure;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFPage;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import junit.framework.TestCase;
import net.n3.nanoxml.XMLWriter;

public class GeoPDFBuilder {

	public static class GeoPDFConfiguration {
		public static final String SUCCESS_GDAL_DONE = "done.";
		public static final String GDALWARP_COMMAND_UNIX = "gdalwarp";
		public static final String GDALWARP_COMMAND_WINDOWS = "gdalwarp.exe";
		public static final String GDAL_CREATE_COMMAND_UNIX = "gdal_create";
		public static final String GDAL_CREATE_COMMAND_WINDOWS = "gdal_create.exe";

		private int markDeltaMinutes;
		private int labelDeltaMinutes;
		private String author;
		private String background;
		private double marginPercent;
		private double pageWidth = 841.698;
		private double pageHeight = 595.14;
		private int pageDpi = 72;
		private String gdalWarpCommand = GDALWARP_COMMAND_UNIX;
		private String[] gdalWarpParams = "-t_srs EPSG:4326 -r cubic -of GTiff".split(" ");
		private String gdalCreateCommand = GDAL_CREATE_COMMAND_UNIX;
		private String[] gdalCreateParams = "-of PDF -co".split(" ");
		private String pdfOutputPath;

		public String getPdfOutputPath() {
			return pdfOutputPath;
		}

		public void setPdfOutputPath(String pdfOutputPath) {
			this.pdfOutputPath = pdfOutputPath;
		}

		public String getGdalCreateCommand() {
			return gdalCreateCommand;
		}

		public void setGdalCreateCommand(String gdalCreateCommand) {
			this.gdalCreateCommand = gdalCreateCommand;
		}

		public String[] getGdalCreateParams() {
			return gdalCreateParams;
		}

		public void setGdalCreateParams(String[] gdalCreateParams) {
			this.gdalCreateParams = gdalCreateParams;
		}

		public String getGdalWarpCommand() {
			return gdalWarpCommand;
		}

		public void setGdalWarpCommand(String gdalWarpCommand) {
			this.gdalWarpCommand = gdalWarpCommand;
		}

		public String[] getGdalWarpParams() {
			return gdalWarpParams;
		}

		public void setGdalWarpParams(String gdalWarpParams) {
			this.gdalWarpParams = gdalWarpParams.split(" ");
		}

		public double getPageWidth() {
			return pageWidth;
		}

		public void setPageWidth(double pageWidth) {
			this.pageWidth = pageWidth;
		}

		public double getPageHeight() {
			return pageHeight;
		}

		public void setPageHeight(double pageHeight) {
			this.pageHeight = pageHeight;
		}

		public int getPageDpi() {
			return pageDpi;
		}

		public void setPageDpi(int pageDpi) {
			this.pageDpi = pageDpi;
		}

		public double getMarginPercent() {
			return marginPercent;
		}

		public void setMarginPercent(double marginPercent) {
			this.marginPercent = marginPercent;
		}

		public String getBackground() {
			return background;
		}

		public void setBackground(String background) {
			this.background = background;
		}

		public int getMarkDeltaMinutes() {
			return markDeltaMinutes;
		}

		public void setMarkDeltaMinutes(int markDeltaMinutes) {
			this.markDeltaMinutes = markDeltaMinutes;
		}

		public int getLabelDeltaMinutes() {
			return labelDeltaMinutes;
		}

		public void setLabelDeltaMinutes(int labelDeltaMinutes) {
			this.labelDeltaMinutes = labelDeltaMinutes;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

	}

	public static File generatePDF(final GeoPDF geoPDF, final GeoPDFConfiguration configuration) throws IOException, InterruptedException {
		final File tmpFile = File.createTempFile("compositionFileDebrief", ".xml");

		final FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
		final XMLWriter xmlWrite = new XMLWriter(fileOutputStream);
		xmlWrite.write(geoPDF.toXML(), true);
		fileOutputStream.close();

		final Runtime runtime = Runtime.getRuntime();
		final ArrayList<String> params = new ArrayList<String>();
		params.add(configuration.getGdalCreateCommand());
		for (String s : configuration.getGdalCreateParams()) {
			params.add(s);
		}
		params.add(tmpFile.getAbsolutePath());
		params.add(configuration.getPdfOutputPath());

		final Process process = runtime.exec(params.toArray(new String[] {}));
		process.waitFor();
		final BufferedReader gdalWarpOutputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		final BufferedReader gdalWarpErrorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		final StringBuilder allOutput = new StringBuilder();
		String line = null;
		while ((line = gdalWarpOutputStream.readLine()) != null) {
			allOutput.append(line + "\n");
		}

		if (allOutput.toString().trim().isEmpty()) {
			// SUCCESS
			return tmpFile;
		}

		allOutput.setLength(0);
		while ((line = gdalWarpErrorStream.readLine()) != null) {
			allOutput.append(line + "\n");
		}
		throw new IOException(allOutput.toString());

		//return null;
	}

	public static GeoPDF build(final Layers layers, final GeoPDFConfiguration configuration)
			throws IOException, InterruptedException {
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
		mainPage.setArea(layers.getBounds());

		/**
		 * Let's create the BackGroundLayer;
		 */
		if (configuration.getBackground() != null) {
			final File backgroundFile = createBackgroundFile(configuration, geoPDF.getFilesToDelete());
			final GeoPDFLayerBackground backgroundLayer = new GeoPDFLayerBackground();
			backgroundLayer.setName("Background chart");
			backgroundLayer.setId("background");
			backgroundLayer.addRaster(backgroundFile.getAbsolutePath());
			mainPage.addLayer(backgroundLayer);
		}

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
					mainPage.addLayer(newTrackLayer);

					newTrackLayer.setId(currentTrack.getName().toLowerCase());
					newTrackLayer.setName(currentTrack.getName());

					/**
					 * Let's create the different parts of the layer
					 */
					/**
					 * TrackLine
					 */
					createTrackLine(geoPDF.getFilesToDelete(), currentTrack, newTrackLayer);

					// Let's create now the point-type vectors
					/**
					 * Minutes difference Layer
					 */
					createMinutesLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer);

					/**
					 * Label Layer
					 */

					createLabelsLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer);

					/**
					 * One point layer
					 */
					createOnePointLayer(configuration, geoPDF.getFilesToDelete(), currentTrack, newTrackLayer);
				}

			}

		}

		return geoPDF;
	}

	private static File createBackgroundFile(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete) throws IOException, InterruptedException {
		final File tmpFile = File.createTempFile("debriefgdalbackground", ".tif");

		tmpFile.delete();

		final Runtime runtime = Runtime.getRuntime();
		final ArrayList<String> params = new ArrayList<String>();
		params.add(configuration.getGdalWarpCommand());
		for (String s : configuration.getGdalWarpParams()) {
			params.add(s);
		}
		params.add(configuration.getBackground());
		params.add(tmpFile.getAbsolutePath());
		// final String[] command = new String[] {"gdalwarp", "--version"};

		final Process process = runtime.exec(params.toArray(new String[] {}));
		process.waitFor();
		final BufferedReader gdalWarpOutputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		final BufferedReader gdalWarpErrorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		final StringBuilder allOutput = new StringBuilder();
		String line = null;
		while ((line = gdalWarpOutputStream.readLine()) != null) {
			allOutput.append(line + "\n");
		}

		if (allOutput.toString().trim().endsWith(GeoPDFConfiguration.SUCCESS_GDAL_DONE)) {
			// SUCCESS
			return tmpFile;
		}

		allOutput.setLength(0);
		while ((line = gdalWarpErrorStream.readLine()) != null) {
			allOutput.append(line + "\n");
		}
		throw new IOException(allOutput.toString());
	}

	protected static void createOnePointLayer(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete, final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer)
			throws FileNotFoundException, JsonProcessingException {

		final String layerName = currentTrack.getName() + "_FirstPoint";
		final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(-1, true, true, layerName);
		final GeoPDFLayerVector deltaMinutesVector = new GeoPDFLayerVector();
		final String vectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack, geoJSONConfiguration);

		final File deltaMinutesFile = createTempFile(layerName + ".geojson", vectorData);
		filesToDelete.add(deltaMinutesFile);

		deltaMinutesVector.setData(deltaMinutesFile.getAbsolutePath());
		deltaMinutesVector.setName(layerName);
		final Color trackColor = currentTrack.getColor();
		final String colorHex = String.format("#%02x%02x%02x", trackColor.getRed(), trackColor.getGreen(),
				trackColor.getBlue());
		deltaMinutesVector
				.setStyle("LABEL(t:\"" + currentTrack.getName() + "\",c: " + colorHex + ",s:24pt,p:2,dy:10mm,bo:1)");

		newTrackLayer.addVector(deltaMinutesVector);
	}

	protected static void createLabelsLayer(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete, final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer)
			throws FileNotFoundException, JsonProcessingException {

		final String layerName = currentTrack.getName() + "_PointsLabels_" + configuration.getLabelDeltaMinutes()
				+ "mins";
		final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(configuration.getLabelDeltaMinutes(),
				true, false, layerName);
		final GeoPDFLayerVector deltaMinutesVector = new GeoPDFLayerVector();
		final String deltaMinutesVectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack,
				geoJSONConfiguration);

		final File deltaMinutesFile = createTempFile(layerName + ".geojson", deltaMinutesVectorData);
		filesToDelete.add(deltaMinutesFile);

		deltaMinutesVector.setData(deltaMinutesFile.getAbsolutePath());
		deltaMinutesVector.setName(layerName);
		final Color trackColor = currentTrack.getColor();
		final String colorHex = String.format("#%02x%02x%02x", trackColor.getRed(), trackColor.getGreen(),
				trackColor.getBlue());
		deltaMinutesVector.setStyle("LABEL(t:{time_str},c:" + colorHex + ",s:24pt,p:4,dx:7mm,bo:1)");

		newTrackLayer.addVector(deltaMinutesVector);
	}

	protected static void createMinutesLayer(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete, final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer)
			throws FileNotFoundException, JsonProcessingException {

		final String layerName = currentTrack.getName() + "_Points_" + configuration.getMarkDeltaMinutes() + "mins";
		final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(configuration.getMarkDeltaMinutes(),
				false, false, layerName);
		final GeoPDFLayerVector deltaMinutesVector = new GeoPDFLayerVector();
		final String deltaMinutesVectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack,
				geoJSONConfiguration);

		final File deltaMinutesFile = createTempFile(layerName + ".geojson", deltaMinutesVectorData);
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

	protected static void createTrackLine(final ArrayList<File> filesToDelete, final TrackWrapper currentTrack,
			final GeoPDFLayerTrack newTrackLayer) throws FileNotFoundException, JsonProcessingException {

		final String layerName = currentTrack.getName() + "_Line";
		final GeoPDFLayerVector trackLineVector = new GeoPDFLayerVector();
		final GeoJSONConfiguration configuration = new GeoJSONConfiguration(0, false, false, layerName);
		final String vectorTrackLineData = GenerateGeoJSON.createGeoJSONTrackLine(currentTrack, configuration);

		final File trackLineFile = createTempFile(layerName + ".geojson", vectorTrackLineData);
		filesToDelete.add(trackLineFile);

		trackLineVector.setData(trackLineFile.getAbsolutePath());
		trackLineVector.setName(layerName);
		final Color trackColor = currentTrack.getColor();
		final String colorHex = String.format("#%02x%02x%02x", trackColor.getRed(), trackColor.getGreen(),
				trackColor.getBlue());
		trackLineVector.setStyle("PEN(c:" + colorHex + ",w:5px)");

		newTrackLayer.addVector(trackLineVector);
	}

	private static File createTempFile(final String fileName, final String data) throws FileNotFoundException {
		final String tempFolder = System.getProperty("java.io.tmpdir");
		final File newFile = new File(tempFolder + File.separatorChar + fileName);

		PrintWriter print = null;
		try {
			print = new PrintWriter(newFile);
			print.println(data);
			print.flush();
		} finally {
			if (print != null) {
				print.close();
			}
		}

		return newFile;
	}

	public static class GeoPDFBuilderTest extends TestCase {

		private final static String boat1rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";
		private final static String boat2rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat2.rep";

		public void testCreateTempFile() {
			// TODO
		}

		public void testBuild() throws IOException, InterruptedException {

			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
			replayImporter.importThis("boat2.rep", new FileInputStream(boat2rep), layers);

			final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			configuration.setLabelDeltaMinutes(60);
			configuration.setMarkDeltaMinutes(10);
			configuration.setMarginPercent(0.1);
			configuration.setBackground("/home/saul/PycharmProjects/GeoPDF/2450_ANVIL_POINT_TO_BEACHY_H.tif");

			configuration.setAuthor("Saul Hidalgo");

			final GeoPDF geoPdf = GeoPDFBuilder.build(layers, configuration);

			System.out.println(geoPdf);
		}
	}
}
