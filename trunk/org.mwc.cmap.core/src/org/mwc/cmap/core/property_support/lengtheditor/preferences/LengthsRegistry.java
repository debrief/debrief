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


public class LengthsRegistry {

	private static final String DELIMITER = ",";//$NON-NLS-1$

	static final String FILE_NAME = "fileName";//$NON-NLS-1$

	private static LengthsRegistry ourInstance;

	private String myFileName;

	private List<String> myNames = new ArrayList<String>();

	private List<Double> myLengths = new ArrayList<Double>();

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
			BufferedReader br = new BufferedReader(new FileReader(getFileName()));
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
			} catch (IOException e) {
				CorePlugin.logError(Status.WARNING,Messages.LengthsRegistry_ErrorOnReading, e);
			}
		} catch (FileNotFoundException e) {
			CorePlugin.logError(Status.WARNING,Messages.LengthsRegistry_FileNotFound, e);
		}

	}

	private boolean isHeader() {
		return true;
	}

	private void parseLine(String nextLine) {
		String[] split = nextLine.split(DELIMITER);
		if (split.length != 2) {
			return;
		}

		String f = split[0];
		String s = split[1];
		try {
			Double value = Double.valueOf(s);
			myNames.add(f);
			myLengths.add(value);
		} catch (Exception e) {
			// skip
		}
	}

	public void setFileName(String fileName) {
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
