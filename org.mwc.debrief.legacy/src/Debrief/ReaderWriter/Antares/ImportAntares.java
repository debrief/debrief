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
import java.util.ArrayList;
import java.util.List;

import Debrief.ReaderWriter.Antares.ImportAntaresImpl.ImportAntaresException;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.Utilities.ReaderWriter.PlainImporterBase;

/**
 * Antares format Importer
 */
public class ImportAntares extends PlainImporterBase {

	private String _trackName;
	private int _month;
	private int _year;
	private List<ImportAntaresException> errors = new ArrayList<>();

	public ImportAntares() {

	}

	public ImportAntares(final String _trackName, final int _month, final int _year, final Layers layers) {
		this._trackName = _trackName;
		this._month = _month;
		this._year = _year;

		this.setLayers(layers);
	}

	/**
	 * Method that returns true if the Stream given contains a valid Antares file.
	 *
	 * @param theFile file that contains the
	 * @return true only if it is a valid Antares file.
	 */
	@Override
	public boolean canImportThisFile(final String theFile) {
		try {
			final FileInputStream inputStream = new FileInputStream(theFile);
			return canImportThisInputStream(inputStream);
		} catch (final FileNotFoundException e) {
			return false;
		}

	}

	public boolean canImportThisInputStream(final InputStream inputStream) {
		return ImportAntaresImpl.canLoadThisStream(inputStream);
	}

	@Override
	public void exportThis(final Plottable item) {
		// Nothing to do here
	}

	@Override
	public void exportThis(final String comment) {
		// Nothing to do here
	}

	@Override
	public void importThis(final String fName, final InputStream is) {
		errors.clear();
		errors.addAll(ImportAntaresImpl.importThis(is, getLayers(), _trackName, _month, _year));
	}

	@Override
	public void importThis(final String fName, final InputStream is, final MonitorProvider provider) {
		// We can use the provider eventually.
		importThis(fName, is);
	}

	public void setMonth(final int _month) {
		this._month = _month;
	}

	public void setTrackName(final String _trackName) {
		this._trackName = _trackName;
	}

	public void setYear(final int _year) {
		this._year = _year;
	}

	public List<ImportAntaresException> getErrors() {
		return errors;
	}
}
