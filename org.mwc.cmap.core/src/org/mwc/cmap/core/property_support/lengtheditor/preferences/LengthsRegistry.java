/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.property_support.lengtheditor.preferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.lengtheditor.Messages;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


public class LengthsRegistry {

	private static final String DELIMITER = ",";//$NON-NLS-1$

	static final String FILE_NAME = "fileName";//$NON-NLS-1$

	private static LengthsRegistry ourInstance;

	private String myFileName;

	private final List<String> myNames = new ArrayList<String>();

	private final List<Double> myLengths = new ArrayList<Double>();

	public LengthsRegistry() {
		setFileName(CorePlugin.getDefault().getPreferenceStore().getString(FILE_NAME));
	}

	public static LengthsRegistry getRegistry() {
		if (ourInstance == null) {
			ourInstance = new LengthsRegistry();
			ourInstance.load();
		}
		return ourInstance;
	}

	public int getItemsCount() {
		return Math.min(myNames.size(), myLengths.size());
	}

	/**
	 * Clear data from registry
	 */
	private void clear() {
		getNames().clear();
		getLengths().clear();
	}

	public void reload() {
		clear();
		load();
	}

	/**
	 * Load data from file
	 */
	private void load() {
		if (getFileName() == null || getFileName().trim().length() == 0) {
			CorePlugin.logError(Status.WARNING,Messages.LengthsRegistry_EmptyFile, null);
			return;
		}

		try {
			final BufferedReader br = new BufferedReader(new FileReader(getFileName()));
			try {
				String nextLine = null;
				// skip header
				if (isHeader()){
					br.readLine();
				}
				try {
					while ((nextLine = br.readLine()) != null) {
						parseLine(nextLine);
					}
				} finally {
					br.close();
				}
			} catch (final IOException e) {
				CorePlugin.logError(Status.WARNING,Messages.LengthsRegistry_ErrorOnReading, e);
			}
		} catch (final FileNotFoundException e) {
			CorePlugin.logError(Status.WARNING,Messages.LengthsRegistry_FileNotFound, null);
		}

	}

	private boolean isHeader() {
		return true;
	}

	private void parseLine(final String nextLine) {
		final String[] split = nextLine.split(DELIMITER);
		if (split.length != 2) {
			return;
		}

		final String f = split[0];
		final String s = split[1];
		try {
			final Double value = MWCXMLReader.readThisDouble(s);
			myNames.add(f);
			myLengths.add(value);
		} catch (final Exception e) {
			// skip
		}
	}

	public void setFileName(final String fileName) {
		myFileName = fileName;
	}

	public String getFileName() {
		return myFileName;
	}

	public List<String> getNames() {
		return myNames;
	}

	public List<Double> getLengths() {
		return myLengths;
	}
}
