package Debrief.ReaderWriter.GeoPDF;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class GeoPDFBuilder {

	public static class GeoPDFConfiguration {
		private int markDeltaMinutes;
		private int labelDeltaMinutes;
		private String author;
		private String background;
		private double marginPercent;
		private double pageWidth = 841.698;
		private double pageHeight = 595.14;
		private int pageDpi = 72;

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

	public static GeoPDF build(final Layers layers, final GeoPDFConfiguration configuration)
			throws FileNotFoundException, JsonProcessingException {
		final GeoPDF geoPDF = new GeoPDF();

		geoPDF.setAuthor(configuration.getAuthor());

		final ArrayList<File> filesToDelete = new ArrayList<File>();

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
			final GeoPDFLayerBackground backgroundLayer = new GeoPDFLayerBackground();
			backgroundLayer.setName("Background chart");
			backgroundLayer.setId("background");
			// TODO COMPLETE THIS.
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
					createTrackLine(filesToDelete, currentTrack, newTrackLayer);

					// Let's create now the point-type vectors
					/**
					 * Minutes difference Layer
					 */
					createMinutesLayer(configuration, filesToDelete, currentTrack, newTrackLayer);

					/**
					 * Label Layer
					 */

					createLabelsLayer(configuration, filesToDelete, currentTrack, newTrackLayer);

					/**
					 * One point layer
					 */
					createOnePointLayer(configuration, filesToDelete, currentTrack, newTrackLayer);
				}

			}

		}

		return geoPDF;
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

		public void testBuild() throws IOException {

			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);
			replayImporter.importThis("boat2.rep", new FileInputStream(boat2rep), layers);

			final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			configuration.setLabelDeltaMinutes(60);
			configuration.setMarkDeltaMinutes(10);
			configuration.setMarginPercent(0.03);
			configuration.setAuthor("Saul Hidalgo");

			final GeoPDF geoPdf = GeoPDFBuilder.build(layers, configuration);

			System.out.println(geoPdf);
		}
	}
}
