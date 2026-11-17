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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;

public abstract class AbstractGeoPDFBuilder {

	public static class GeoPDFConfiguration {
		/**
		 * turns a background chart file into an image in geographic coordinates
		 * (WGS84 lat/long) for the GeoPDF. The export sets one up (it needs GeoTools);
		 * without one, backgrounds are left out.
		 */
		public interface BackgroundProvider {
			GeoPDF.GeoPDFRaster prepare(String backgroundFile, File tempFolder) throws IOException;
		}

		private BackgroundProvider backgroundProvider;
		private int markDeltaMinutes = 10;
		private int labelDeltaMinutes = 60;
		private String author = "DebriefNG";
		private final List<String> background = new ArrayList<String>();
		private double marginPercent = 0.1;
		private double pageWidth = 841.698;
		private double pageHeight = 595.14;
		private long stepDeltaMilliSeconds = 15 * 60 * 1000;
		private long stepSpeedMilliSeconds = 1000; // Default 1 second.
		private String dateFormat;
		private HiResDate startTime;
		private HiResDate endTime;
		private WorldArea viewportArea;
		private int pageDpi = 72;
		private String pdfOutputPath;

		private String tempFolder = new Timestamp(System.currentTimeMillis()).getTime() + "";
		private boolean landscape = true;

		public void addBackground(final String background) {
			this.background.add(background);
		}

		public BackgroundProvider getBackgroundProvider() {
			return backgroundProvider;
		}

		public String getAuthor() {
			return author;
		}

		public List<String> getBackground() {
			return background;
		}

		public String getDateFormat() {
			return dateFormat;
		}

		public HiResDate getEndTime() {
			return endTime;
		}

		public int getLabelDeltaMinutes() {
			return labelDeltaMinutes;
		}

		public double getMarginPercent() {
			return marginPercent;
		}

		public int getMarkDeltaMinutes() {
			return markDeltaMinutes;
		}

		public int getPageDpi() {
			return pageDpi;
		}

		public double getPageHeight() {
			return pageHeight;
		}

		public double getPageWidth() {
			return pageWidth;
		}

		public String getPdfOutputPath() {
			return pdfOutputPath;
		}

		public HiResDate getStartTime() {
			return startTime;
		}

		public long getStepDeltaMilliSeconds() {
			return stepDeltaMilliSeconds;
		}

		public long getStepSpeedMilliSeconds() {
			return stepSpeedMilliSeconds;
		}

		public String getTempFolder() {
			return tempFolder;
		}

		public WorldArea getViewportArea() {
			return viewportArea;
		}

		public boolean isLandscape() {
			return landscape;
		}

		public void setBackgroundProvider(final BackgroundProvider backgroundProvider) {
			this.backgroundProvider = backgroundProvider;
		}

		public void setAuthor(final String author) {
			this.author = author;
		}

		public void setDateFormat(final String dateFormat) {
			this.dateFormat = dateFormat;
		}

		public void setEndTime(final HiResDate endTime) {
			this.endTime = endTime;
		}

		public void setLabelDeltaMinutes(final int labelDeltaMinutes) {
			this.labelDeltaMinutes = labelDeltaMinutes;
		}

		public void setLandscape(final boolean landscape) {
			if (this.landscape != landscape) {
				this.landscape = landscape;
				final double tmp = pageWidth;
				pageWidth = pageHeight;
				pageHeight = tmp;
			}
		}

		public void setMarginPercent(final double marginPercent) {
			this.marginPercent = marginPercent;
		}

		public void setMarkDeltaMinutes(final int markDeltaMinutes) {
			this.markDeltaMinutes = markDeltaMinutes;
		}

		public void setPageDpi(final int pageDpi) {
			this.pageDpi = pageDpi;
		}

		public void setPageHeight(final double pageHeight) {
			this.pageHeight = pageHeight;
		}

		public void setPageWidth(final double pageWidth) {
			this.pageWidth = pageWidth;
		}

		public void setPdfOutputPath(final String pdfOutputPath) {
			this.pdfOutputPath = pdfOutputPath;
		}

		public void setStartTime(final HiResDate startTime) {
			this.startTime = startTime;
		}

		public void setStepDeltaMilliSeconds(final long stepDeltaMilliSeconds) {
			this.stepDeltaMilliSeconds = stepDeltaMilliSeconds;
		}

		public void setStepSpeedMilliSeconds(final long stepSpeedMilliSeconds) {
			this.stepSpeedMilliSeconds = stepSpeedMilliSeconds;
		}

		public void setTempFolder(final String tempFolder) {
			this.tempFolder = tempFolder;
		}

		public void setViewportArea(final WorldArea viewportArea) {
			this.viewportArea = viewportArea;
		}
	}

	public static final String JAVASCRIPT_TEMPLATE_PATH = "/geopdf_animation.js";
	public static final String JAVASCRIPT_TIMESTAMP_TAG = "!!JS_TIMESTAMPS";
	public static final String JAVASCRIPT_STEP_SPEED = "!!STEPSPEED";
	public static final String JAVASCRIPT_TIMESTAMP_TAG_NON_INTERATIVE = "!!NONINTERACTLAYERS";

	public static final String NON_INTERACTIVE_SUFFIX = " (non-interactive)";

	public static final String INTERACTIVE_LAYER_NAME = "Interactive Layers";

	/**
	 * the background chart as an image in lat/long, or null if there is no
	 * provider to prepare it
	 */
	protected static GeoPDF.GeoPDFRaster createBackground(final GeoPDFConfiguration configuration,
			final String background) throws IOException {
		final GeoPDFConfiguration.BackgroundProvider provider = configuration.getBackgroundProvider();
		if (provider == null) {
			Application.logError3(ToolParent.INFO,
					"GeoPDF-No background provider, so leaving out background " + background, null, false);
			return null;
		}
		return provider.prepare(background, getFile("", configuration.getTempFolder()));
	}

	protected static String createJavascriptContent(final String javaScriptTS, final String javaScriptTSNONInteractive,
			final String javaScriptStepSpeed, final String filePath) throws IOException {
		final InputStream javascriptContentInputStream = AbstractGeoPDFBuilder.class.getResourceAsStream(filePath);

		BufferedReader javascriptBufferReader = null;
		try {
			javascriptBufferReader = new BufferedReader(new InputStreamReader(javascriptContentInputStream));

			final StringBuilder content = new StringBuilder();

			String line = null;
			while ((line = javascriptBufferReader.readLine()) != null) {
				content.append(line + "\n");
			}

			return content.toString().replaceAll(JAVASCRIPT_TIMESTAMP_TAG, javaScriptTS)
					.replaceAll(JAVASCRIPT_TIMESTAMP_TAG_NON_INTERATIVE, javaScriptTSNONInteractive)
					.replaceAll(JAVASCRIPT_STEP_SPEED, javaScriptStepSpeed);
		} finally {
			if (javascriptBufferReader != null) {
				javascriptBufferReader.close();
			}
		}
	}

	protected static File createTempFile(final String fileName, final String data, final String folderName)
			throws FileNotFoundException {

		final File newFile = getFile(fileName, folderName);
		Application.logError3(ToolParent.INFO, "GeoPDF-Creating temporary file in " + newFile.getAbsolutePath(), null,
				false);

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

	protected static File getFile(final String fileName, final String folderName) {
		final String tempFolder = System.getProperty("java.io.tmpdir");
		// Let's create everything inside a folder.
		final File tempFolderTimestamp = new File(tempFolder + File.separatorChar + folderName);

		tempFolderTimestamp.mkdirs();

		final File newFile = new File(tempFolderTimestamp.getAbsolutePath() + File.separatorChar + fileName);
		return newFile;
	}

	/**
	 * Use millis for filename, to overcome problem when sparse data lead to filenames
	 * being re-used.
	 *
	 * @param date Date to convert to filename
	 * @return filename
	 */
	protected static String HiResDateToFileName(final HiResDate date) {
		return date.getMicros() / 1000 + "";
	}

	protected static String sanitizeFilename(final String fileName) {
		return fileName.replaceAll("[\\\\/:*?\"<>|]", "");
	}

	public abstract GeoPDF build(final Layers layers, final GeoPDFConfiguration configuration)
			throws IOException, InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, ClassNotFoundException;

	protected abstract void createLabelsLayer(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete, final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer,
			final TimePeriod period) throws FileNotFoundException, JsonProcessingException;

	protected abstract void createTrackLine(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete, final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer,
			final TimePeriod period) throws FileNotFoundException, JsonProcessingException;

	protected abstract void createTrackNameLayer(final GeoPDFConfiguration configuration,
			final ArrayList<File> filesToDelete, final TrackWrapper currentTrack, final GeoPDFLayerTrack newTrackLayer,
			final TimePeriod period) throws FileNotFoundException, JsonProcessingException;

	/**
	 * write the GeoPDF to the configured output path
	 *
	 * @return the PDF file
	 */
	public File generatePDF(final GeoPDF geoPDF, final GeoPDFConfiguration configuration) throws IOException {
		final File output = new File(configuration.getPdfOutputPath());
		Application.logError3(ToolParent.INFO, "GeoPDF-Writing " + output.getAbsolutePath(), null, false);
		new GeoPdfWriter().write(geoPDF, output);
		return output;
	}
}