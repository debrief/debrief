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

package org.mwc.debrief.core.ContextOperations.ExportCSVPrefs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.mwc.cmap.core.CorePlugin;

public class CSVExportDropdownRegistry implements DropdownProvider {

	private static CSVExportDropdownRegistry ourInstance;

	public synchronized static CSVExportDropdownRegistry getRegistry() {
		if (ourInstance == null) {
			ourInstance = new CSVExportDropdownRegistry();
			try {
				ourInstance.load();
			} catch (final FileNotFoundException e) {
				CorePlugin.logError(IStatus.WARNING, Messages.ExportCSVRegistry_FileNotFound, null);
				CorePlugin.showMessage("Export to CSV", "CSV wizard settings file missing:\n"
						+ ourInstance.getFileName() + "\n\nPlease provide file using Preferences page");
				ourInstance = null;
			}
		}
		return ourInstance;
	}

	static void processLines(final List<String> lines, final Map<String, ArrayList<String>> myFields) {
		String thisGroup = null;
		for (final String nextLine : lines) {
			// check it's not a comment marker
			if (!nextLine.startsWith("////")) {
				if (nextLine.startsWith("//")) {
					// new section, store it

					// trim the comment marker
					thisGroup = nextLine.substring(2, nextLine.length());

					// ditch whitespace
					thisGroup = thisGroup.trim();

					// and create the new list
					final ArrayList<String> newGroup = new ArrayList<String>();
					myFields.put(thisGroup, newGroup);
				} else {
					// do we know our group?
					if (thisGroup != null) {
						// must be part of the current group
						final ArrayList<String> group = myFields.get(thisGroup);
						group.add(nextLine);
					}
				}
			}
		}
	}

	private String myFileName;

	final Map<String, ArrayList<String>> myFields = new HashMap<String, ArrayList<String>>();

	public CSVExportDropdownRegistry() {
		this(CorePlugin.getDefault().getPreferenceStore()
				.getString(ExportCSVPreferencesPage.PreferenceConstants.PATH_TO_CSV));
	}

	public CSVExportDropdownRegistry(final String filename) {
		setFileName(filename);
	}

	/**
	 * Clear data from registry
	 */
	private void clear() {
		myFields.clear();
	}

	public String getFileName() {
		return myFileName;
	}

	@Override
	public List<String> getValuesFor(final String title) {
		return myFields.get(title);
	}

	/**
	 * Load data from file
	 *
	 * @throws FileNotFoundException
	 */
	void load() throws FileNotFoundException {
		if (getFileName() == null || getFileName().trim().length() == 0) {
			CorePlugin.logError(IStatus.WARNING, Messages.ExportCSVRegistry_EmptyFile, null);
			return;
		}

		final BufferedReader br = new BufferedReader(new FileReader(getFileName()));
		try {
			// build up list of lines in this file
			final List<String> lines = new ArrayList<String>();

			try {
				String nextLine = null;
				while ((nextLine = br.readLine()) != null) {
					lines.add(nextLine);
				}
			} finally {
				br.close();
			}

			// and now process those lines
			processLines(lines, myFields);
		} catch (final IOException e) {
			CorePlugin.logError(IStatus.WARNING, Messages.ExportCSVRegistry_ErrorOnReading, e);
		}

	}

	public void reload() throws FileNotFoundException {
		clear();
		load();
	}

	public void setFileName(final String fileName) {
		myFileName = fileName;
	}
}
