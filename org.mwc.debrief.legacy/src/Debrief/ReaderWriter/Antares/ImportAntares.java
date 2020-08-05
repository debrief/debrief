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
package Debrief.ReaderWriter.Antares;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.PlainImporterBase;

/**
 * Antares format Importer
 */
public class ImportAntares extends PlainImporterBase {

	String _trackName;
	int _month;
	int _year;

	public ImportAntares(final String _trackName, final int _month, final int _year, final Layers layers) {
		this._trackName = _trackName;
		this._month = _month;
		this._year = _year;

		this.setLayers(layers);
	}
	
	public ImportAntares() {
		
	}

	public void setMonth(int _month) {
		this._month = _month;
	}

	public void setYear(int _year) {
		this._year = _year;
	}
	
	public void setTrackName(String _trackName) {
		this._trackName = _trackName;
	}

	/**
	 * Method that returns true if the Stream given contains a valid Antares file.
	 * 
	 * @param theFile file that contains the
	 * @return true only if it is a valid Antares file.
	 */
	@Override
	public boolean canImportThisFile(String theFile) {
		try {
			final FileInputStream inputStream = new FileInputStream(theFile);
			return ImportAntaresImpl.canLoadThisStream(inputStream);
		} catch (FileNotFoundException e) {
			return false;
		}

	}

	@Override
	public void exportThis(Plottable item) {
		// Nothing to do here
	}

	@Override
	public void exportThis(String comment) {
		// Nothing to do here
	}

	@Override
	public void importThis(String fName, InputStream is) {
		ImportAntaresImpl.importThis(is, getLayers(), _trackName, _month, _year);
	}

	@Override
	public void importThis(String fName, InputStream is, MonitorProvider provider) {
		// We can use the provider eventually.
		importThis(fName, is);
	}
}
