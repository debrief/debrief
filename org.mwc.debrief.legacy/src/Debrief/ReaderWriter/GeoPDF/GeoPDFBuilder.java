package Debrief.ReaderWriter.GeoPDF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

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
		private int markDeltaMinutes;
		private int labelDeltaMinutes;
		private String author;

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
			throws FileNotFoundException {
		final GeoPDF geoPDF = new GeoPDF();

		geoPDF.setAuthor(configuration.getAuthor());

		final ArrayList<File> filesToDelete = new ArrayList<File>();

		/**
		 * For now let's work using only one page, but we will fix it eventually to have
		 * several pages.
		 */
		final GeoPDFPage mainPage = geoPDF.createNewPage();

		mainPage.setDpi(72);
		mainPage.setWidth(841.698);
		mainPage.setHeight(595.14);

		// TODO
		// Set Area
		mainPage.setArea(null);

		/**
		 * Let's create the BackGroundLayer;
		 */
		final GeoPDFLayerBackground backgroundLayer = new GeoPDFLayerBackground();
		backgroundLayer.setName("Background chart");
		backgroundLayer.setId("background");
		// TODO COMPLETE THIS.
		mainPage.addLayer(backgroundLayer);

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
			throws FileNotFoundException {

		final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(-1, true, true);
		final GeoPDFLayerVector deltaMinutesVector = new GeoPDFLayerVector();
		final String vectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack, geoJSONConfiguration);

		final File deltaMinutesFile = createTempFile(currentTrack.getName() + "_FirstPoint.geojson", vectorData);
		filesToDelete.add(deltaMinutesFile);

		deltaMinutesVector.setData(deltaMinutesFile.getAbsolutePath());
		deltaMinutesVector.setName(currentTrack.getName() + "_FirstPoint");
		// TODO
		deltaMinutesVector.setStyle("");

		newTrackLayer.addVector(deltaMinutesVector);
	}

	protected static void createLabelsLayer(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete, final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer)
			throws FileNotFoundException {

		final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(configuration.getLabelDeltaMinutes(),
				true, false);
		final GeoPDFLayerVector deltaMinutesVector = new GeoPDFLayerVector();
		final String deltaMinutesVectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack,
				geoJSONConfiguration);

		final File deltaMinutesFile = createTempFile(
				currentTrack.getName() + "_PointsLabels_" + configuration.getLabelDeltaMinutes() + "mins.geojson",
				deltaMinutesVectorData);
		filesToDelete.add(deltaMinutesFile);

		deltaMinutesVector.setData(deltaMinutesFile.getAbsolutePath());
		deltaMinutesVector
				.setName(currentTrack.getName() + "_PointsLabels_" + configuration.getLabelDeltaMinutes() + "mins");
		// TODO
		deltaMinutesVector.setStyle("");

		newTrackLayer.addVector(deltaMinutesVector);
	}

	protected static void createMinutesLayer(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete, final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer)
			throws FileNotFoundException {

		final GeoJSONConfiguration geoJSONConfiguration = new GeoJSONConfiguration(configuration.getMarkDeltaMinutes(),
				false, false);
		final GeoPDFLayerVector deltaMinutesVector = new GeoPDFLayerVector();
		final String deltaMinutesVectorData = GenerateGeoJSON.createGeoJSONTrackPoints(currentTrack,
				geoJSONConfiguration);

		final File deltaMinutesFile = createTempFile(
				currentTrack.getName() + "_Points_" + configuration.getMarkDeltaMinutes() + "mins.geojson",
				deltaMinutesVectorData);
		filesToDelete.add(deltaMinutesFile);

		deltaMinutesVector.setData(deltaMinutesFile.getAbsolutePath());
		deltaMinutesVector.setName(currentTrack.getName() + "_Points_" + configuration.getMarkDeltaMinutes() + "mins");
		// TODO
		deltaMinutesVector.setStyle("");
		deltaMinutesVector.setLogicalStructure(new LogicalStructure(currentTrack.getName(), "time"));

		newTrackLayer.addVector(deltaMinutesVector);
	}

	protected static void createTrackLine(final ArrayList<File> filesToDelete, final TrackWrapper currentTrack,
			final GeoPDFLayerTrack newTrackLayer) throws FileNotFoundException {

		final GeoPDFLayerVector trackLineVector = new GeoPDFLayerVector();
		final String vectorTrackLineData = GenerateGeoJSON.createGeoJSONTrackLine(currentTrack);

		final File trackLineFile = createTempFile(currentTrack.getName() + "_Line.geojson", vectorTrackLineData);
		filesToDelete.add(trackLineFile);

		trackLineVector.setData(trackLineFile.getAbsolutePath());
		trackLineVector.setName(currentTrack.getName() + "_Line");
		// TODO
		trackLineVector.setStyle("");

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

		private final static String boat1rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat2.rep";

		public void testCreateTempFile() {
			// TODO
		}

		public void testBuild() throws IOException {

			final Layers layers = new Layers();
			final ImportReplay replayImporter = new ImportReplay();
			replayImporter.importThis("boat1.rep", new FileInputStream(boat1rep), layers);

			final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			configuration.setLabelDeltaMinutes(60);
			configuration.setMarkDeltaMinutes(10);
			configuration.setAuthor("Saul Hidalgo");

			final GeoPDF geoPdf = GeoPDFBuilder.build(layers, configuration);

			final XMLWriter xmlWrite = new XMLWriter(new FileOutputStream(new File("composition.xml")));

			xmlWrite.write(geoPdf.toXML(), true);
		}
	}
}
