package Debrief.ReaderWriter.GeoPDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import Debrief.ReaderWriter.GeoPDF.GenerateGeoJSON.GeoJSONConfiguration;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerBackground;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVector;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFPage;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;

public class GeoPDFBuilder {

	public static class GeoPDFConfiguration {
		private int markDeltaMinutes;
		private int labelDeltaMinutes;

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

	}

	public static void build(final Layers layers, final GeoPDFConfiguration configuration)
			throws FileNotFoundException {
		final GeoPDF geoPDF = new GeoPDF();

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
		// TODO
		deltaMinutesVector.setStyle(null);

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
		// TODO
		deltaMinutesVector.setStyle(null);

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
		// TODO
		deltaMinutesVector.setStyle(null);

		newTrackLayer.addVector(deltaMinutesVector);
	}

	protected static void createTrackLine(final ArrayList<File> filesToDelete, final TrackWrapper currentTrack,
			final GeoPDFLayerTrack newTrackLayer) throws FileNotFoundException {

		final GeoPDFLayerVector trackLineVector = new GeoPDFLayerVector();
		final String vectorTrackLineData = GenerateGeoJSON.createGeoJSONTrackLine(currentTrack);

		final File trackLineFile = createTempFile(currentTrack.getName() + "_Line.geojson", vectorTrackLineData);
		filesToDelete.add(trackLineFile);

		trackLineVector.setData(trackLineFile.getAbsolutePath());

		// TODO
		trackLineVector.setStyle(null);

		newTrackLayer.addVector(trackLineVector);
	}

	private static File createTempFile(final String fileName, final String data) throws FileNotFoundException {
		final File newFile = new File(fileName);

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
}
