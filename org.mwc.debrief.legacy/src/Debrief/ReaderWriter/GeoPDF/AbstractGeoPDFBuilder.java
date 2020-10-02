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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import junit.framework.TestCase;

public abstract class AbstractGeoPDFBuilder {

	public static class GeoPDFBuilderTest extends TestCase {

//		private final static String boat1rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";
//		private final static String boat2rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat2.rep";

		public void testBuild() throws IOException, InterruptedException, NoSuchFieldException, SecurityException,
				IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

			/*
			 * final Layers layers = new Layers(); final ImportReplay replayImporter = new
			 * ImportReplay(); replayImporter.importThis("boat1.rep", new
			 * FileInputStream(boat1rep), layers); replayImporter.importThis("boat2.rep",
			 * new FileInputStream(boat2rep), layers);
			 *
			 * final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			 * configuration.addBackground(
			 * "../org.mwc.cmap.combined.feature/root_installs/sample_data/SP27GTIF.tif");
			 *
			 * final GeoPDF geoPdf = GeoPDFBuilder.build(layers, configuration);
			 *
			 * configuration.setPdfOutputPath(System.getProperty("java.io.tmpdir") +
			 * File.separatorChar + "test.pdf"); GeoPDFBuilder.generatePDF(geoPdf,
			 * configuration);
			 *
			 * System.out.println("PDF successfully generated at " +
			 * configuration.getPdfOutputPath()); System.out.println(geoPdf);
			 */
			// We will cover this in the ticket
			// https://github.com/debrief/debrief/issues/4969
		}

		public void testCreateTempFile() throws FileNotFoundException {
			final File test = createTempFile("test.txt", "Test", "test");

			final Scanner scanner = new Scanner(test);
			assertEquals("Correct file writted with data", "Test", scanner.next());
			scanner.close();

		}
	}

	public static class GeoPDFConfiguration {
		public static final String SUCCESS_GDAL_DONE = "done.";
		public static final String GDALWARP_RAW_COMMAND = "gdalwarp";
		public static final String ECLIPSE_GDAL_BIN_NATIVE_PATH = "..\\org.mwc.debrief.legacy\\native\\";
		public static final String GDALWARP_COMMAND_WINDOWS = ECLIPSE_GDAL_BIN_NATIVE_PATH + "windows\\gdalwarp.exe";
		public static final String GDAL_CREATE_RAW_COMMAND = "gdal_create";
		public static final String GDAL_CREATE_COMMAND_WINDOWS = ECLIPSE_GDAL_BIN_NATIVE_PATH
				+ "windows\\gdal_create.exe";
		public static final String GDAL_CREATE_COMPOSITION_KEYWORD = "COMPOSITION_FILE=";
		public static final String PROJ_PATH_TO_REGISTER = "windows\\proj6\\share";
		public static final String PROJ_ENV_VAR = "PROJ_LIB";

		public static void createTemporaryEnvironmentWindows(final String destinationFolder,
				final String resourceFileListPath, final GeoPDFConfiguration configuration) throws IOException {

			Application.logError3(ToolParent.INFO,
					"GeoPDF-We are creating the Gdal binaries folder in " + destinationFolder, null, false);
			final InputStream filesToCopyStream;
			filesToCopyStream = AbstractGeoPDFBuilder.class
					.getResourceAsStream(GDAL_NATIVE_PREFIX_FOLDER + resourceFileListPath);

			final Scanner scanner = new Scanner(filesToCopyStream);
			while (scanner.hasNextLine()) {
				final String line = scanner.nextLine();

				final Path destinationPath = Paths.get(destinationFolder + line);
				Files.createDirectories(destinationPath.getParent());

				Files.copy(GeoPDFConfiguration.class.getResourceAsStream(GDAL_NATIVE_PREFIX_FOLDER + line),
						destinationPath, StandardCopyOption.REPLACE_EXISTING);
				if (line.contains(GDALWARP_RAW_COMMAND)) {
					configuration.setGdalWarpCommand(destinationPath.toString());
				}
				if (line.contains(GDAL_CREATE_RAW_COMMAND)) {
					configuration.setGdalCreateCommand(destinationPath.toString());
				}
			}

			scanner.close();
			Application.logError3(ToolParent.INFO, "GeoPDF-All binaries has been successfully copied.", null, false);
			try {
				filesToCopyStream.close();
			} catch (final IOException e) {
				// Nothing to do, we are just closing the resource....
			}
		}

		private boolean isReady = false;
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
		private String gdalWarpCommand = GDALWARP_RAW_COMMAND;
		private String[] gdalWarpParams = "-t_srs EPSG:4326 -r near -of GTiff".split(" ");
		private String gdalCreateCommand = GDAL_CREATE_RAW_COMMAND;
		private String[] gdalCreateParams = "-of PDF -co".split(" ");
		private String pdfOutputPath;

		private String tempFolder = new Timestamp(System.currentTimeMillis()).getTime() + "";
		private boolean landscape = true;

		private final List<String> envVariables = new ArrayList<String>();

		public void addBackground(final String background) {
			this.background.add(background);
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

		public List<String> getEnvVariables() {
			return envVariables;
		}

		public String getGdalCreateCommand() {
			return gdalCreateCommand;
		}

		public String[] getGdalCreateParams() {
			return gdalCreateParams;
		}

		public String getGdalWarpCommand() {
			return gdalWarpCommand;
		}

		public String[] getGdalWarpParams() {
			return gdalWarpParams;
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

		public boolean isReady() {
			return isReady;
		}

		public void prepareGdalEnvironment() throws IOException, NoSuchFieldException, SecurityException,
				IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
			final String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("win") != -1) {
				Application.logError3(ToolParent.INFO, "GeoPDF-Windows has been detected as the OS.", null, false);
				// We are on Windows
				final File createCommandPath = new File(GeoPDFConfiguration.GDAL_CREATE_COMMAND_WINDOWS);
				Application.logError3(ToolParent.INFO,
						"GeoPDF-We are going to check if the gdal binaries are available from a relative path.", null,
						false);
				if (createCommandPath.exists()) {
					Application.logError3(ToolParent.INFO, "GeoPDF-We are running Gdal binaries from relative path.",
							null, false);
					// We are running from Eclipse, we let's just run the command directly

					this.setGdalCreateCommand(GeoPDFConfiguration.GDAL_CREATE_COMMAND_WINDOWS);
					this.setGdalWarpCommand(GeoPDFConfiguration.GDALWARP_COMMAND_WINDOWS);
					final File projFile = new File(ECLIPSE_GDAL_BIN_NATIVE_PATH + PROJ_PATH_TO_REGISTER);
					registerEnvironmentVar(PROJ_ENV_VAR, projFile.getAbsolutePath());
				} else {
					Application.logError3(ToolParent.INFO,
							"GeoPDF-We didn't find the files. We need to create a temporary environment", null, false);
					// We are inside the .jar file, we need to copy all the files to a Temporary
					// folder.

					createTemporaryEnvironmentWindows(System.getProperty("java.io.tmpdir") + File.separatorChar,
							"/windows-files.txt", this);
					registerEnvironmentVar(PROJ_ENV_VAR,
							System.getProperty("java.io.tmpdir") + File.separatorChar + PROJ_PATH_TO_REGISTER);
				}

			} // we don't do the else, because by default it is Unit command.
			else {
				Application.logError3(ToolParent.INFO,
						"GeoPDF-We have detected an Unix-like system. We will assume gdal is installed.", null, false);
			}

			Application.logError3(ToolParent.INFO, "GeoPDF-Temporary environment is ready.", null, false);
			isReady = true;
		}

		private void registerEnvironmentVar(final String key, final String value) {
			envVariables.add(key + "=" + value);
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

		public void setGdalCreateCommand(final String gdalCreateCommand) {
			this.gdalCreateCommand = gdalCreateCommand;
		}

		public void setGdalCreateParams(final String[] gdalCreateParams) {
			this.gdalCreateParams = gdalCreateParams;
		}

		public void setGdalWarpCommand(final String gdalWarpCommand) {
			this.gdalWarpCommand = gdalWarpCommand;
		}

		public void setGdalWarpParams(final String gdalWarpParams) {
			this.gdalWarpParams = gdalWarpParams.split(" ");
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

	public static final String GDAL_NATIVE_PREFIX_FOLDER = "/native";
	public static final String JAVASCRIPT_TEMPLATE_PATH = "/geopdf_animation.js";
	public static final String JAVASCRIPT_TIMESTAMP_TAG = "!!JS_TIMESTAMPS";
	public static final String JAVASCRIPT_STEP_SPEED = "!!STEPSPEED";
	public static final String JAVASCRIPT_TIMESTAMP_TAG_NON_INTERATIVE = "!!NONINTERACTLAYERS";

	public static final String NON_INTERACTIVE_SUFFIX = " (non-interactive)";

	public static final String INTERACTIVE_LAYER_NAME = "Interactive Layers";

	protected static File createBackgroundFile(final GeoPDFConfiguration configuration, final String background,
			final ArrayList<File> filesToDelete) throws IOException, InterruptedException {
		final File tmpFile = createTempFile("debriefgdalbackground" + UUID.randomUUID().toString().replaceAll("-", ""),
				"", configuration.getTempFolder());

		tmpFile.delete();

		filesToDelete.add(tmpFile);

		final Runtime runtime = Runtime.getRuntime();
		final ArrayList<String> params = new ArrayList<String>();
		params.add(configuration.getGdalWarpCommand());
		for (final String s : configuration.getGdalWarpParams()) {
			params.add(s);
		}
		params.add(background);
		params.add(tmpFile.getAbsolutePath());

		final StringBuilder paramsLog = new StringBuilder();
		for (final String p : params) {
			paramsLog.append(p);
			paramsLog.append(" ");
		}
		Application.logError3(ToolParent.INFO, "GeoPDF-" + paramsLog.toString(), null, false);
		final Process process = runtime.exec(params.toArray(new String[] {}),
				configuration.getEnvVariables().toArray(new String[] {}));
		process.waitFor();
		final BufferedReader gdalWarpOutputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		final BufferedReader gdalWarpErrorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		final StringBuilder allOutput = new StringBuilder();
		String line = null;
		while ((line = gdalWarpOutputStream.readLine()) != null) {
			allOutput.append(line + "\n");
		}

		Application.logError3(ToolParent.INFO, "GeoPDF-Output: " + allOutput.toString(), null, false);
		if (allOutput.toString().trim().endsWith(GeoPDFConfiguration.SUCCESS_GDAL_DONE)) {
			// SUCCESS
			Application.logError3(ToolParent.INFO, "GeoPDF-Reported as a successful background conversion.", null,
					false);
			return tmpFile;
		}
		// SUCCESS
		Application.logError3(ToolParent.INFO, "GeoPDF-Problem detected while converting the background file.", null,
				false);

		allOutput.setLength(0);
		while ((line = gdalWarpErrorStream.readLine()) != null) {
			allOutput.append(line + "\n");
		}
		Application.logError3(ToolParent.INFO, "GeoPDF-" + allOutput.toString(), null, false);
		throw new IOException(allOutput.toString());
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
		final String tempFolder = System.getProperty("java.io.tmpdir");
		// Let's create everything inside a folder.
		final File tempFolderTimestamp = new File(tempFolder + File.separatorChar + folderName);

		tempFolderTimestamp.mkdirs();

		final File newFile = new File(tempFolderTimestamp.getAbsolutePath() + File.separatorChar + fileName);
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

	public File generatePDF(final GeoPDF geoPDF, final GeoPDFConfiguration configuration)
			throws IOException, InterruptedException {
		final File tmpFile = createTempFile("compositionFileDebrief" + configuration.getTempFolder() + ".xml",
				geoPDF.toString(), configuration.getTempFolder());

		final Runtime runtime = Runtime.getRuntime();
		final ArrayList<String> params = new ArrayList<String>();
		params.add(configuration.getGdalCreateCommand());
		for (final String s : configuration.getGdalCreateParams()) {
			params.add(s);
		}
		params.add(GeoPDFConfiguration.GDAL_CREATE_COMPOSITION_KEYWORD + tmpFile.getAbsolutePath());
		params.add(configuration.getPdfOutputPath());

		final StringBuilder paramsLog = new StringBuilder();
		for (final String p : params) {
			paramsLog.append(p);
			paramsLog.append(" ");
		}
		Application.logError3(ToolParent.INFO, "GeoPDF-" + paramsLog.toString(), null, false);
		final Process process = runtime.exec(params.toArray(new String[] {}),
				configuration.getEnvVariables().toArray(new String[] {}));
		process.waitFor();
		final BufferedReader gdalWarpOutputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

		final BufferedReader gdalWarpErrorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		final StringBuilder allOutput = new StringBuilder();
		String line = null;
		while ((line = gdalWarpOutputStream.readLine()) != null) {
			allOutput.append(line + "\n");
		}

		Application.logError3(ToolParent.INFO, "GeoPDF-Output: " + allOutput.toString(), null, false);
		if (!allOutput.toString().trim().isEmpty()) {
			throw new IOException(allOutput.toString());
		}

		allOutput.setLength(0);
		while ((line = gdalWarpErrorStream.readLine()) != null) {
			allOutput.append(line + "\n");
		}

		Application.logError3(ToolParent.INFO, "GeoPDF-Error generating the PDF: " + allOutput.toString(), null, false);
		if (!allOutput.toString().trim().isEmpty()) {
			throw new IOException(allOutput.toString());
		}

		return tmpFile;
	}
}