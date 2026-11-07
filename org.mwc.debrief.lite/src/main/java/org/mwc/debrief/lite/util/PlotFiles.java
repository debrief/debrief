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

package org.mwc.debrief.lite.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.NMEA.ImportNMEA;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import MWC.GUI.Layers;

/**
 * The file reading and writing that Debrief Lite does, kept free of any UI so
 * that it can be tested headless. The GUI code calls these, then updates its
 * time controls, outline and map.
 */
public final class PlotFiles {

	/**
	 * Lite samples ownship and AIS (third-party) NMEA tracks at one fix a minute.
	 */
	public static final long NMEA_SAMPLE_MILLIS = 60000;

	/**
	 * read an NMEA log into the layers, as Lite's NMEA import does
	 */
	public static void importNmea(final File file, final Layers layers) throws IOException {
		final ImportNMEA importer = new ImportNMEA(layers);
		try (InputStream is = new FileInputStream(file)) {
			importer.importThis(file.getName(), is, NMEA_SAMPLE_MILLIS, NMEA_SAMPLE_MILLIS, false);
		}
	}

	/**
	 * read a Debrief plot (DPF/XML) into the session, as Lite's Open Plot does
	 */
	public static void importPlot(final File file, final Session session, final Application parent)
			throws IOException {
		final DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(parent);
		try (InputStream is = new FileInputStream(file)) {
			reader.importThis(file.getName(), is, session);
		}
	}

	/**
	 * write the session as a Debrief plot (DPF), as Lite's Save does
	 */
	public static void writePlot(final Session session, final File target) throws IOException {
		try (OutputStream os = new FileOutputStream(target)) {
			DebriefXMLReaderWriter.exportThis(session, os);
		}
	}

	private PlotFiles() {
	}
}
