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

package org.mwc.debrief.core.operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.TimeControllerOperation;
import org.mwc.cmap.core.ui_support.wizards.SimplePageListWizard;
import org.mwc.cmap.core.wizards.DirectorySelectorWizardPage;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.FlatFile.DopplerShift.DopplerShiftExporter;
import Debrief.ReaderWriter.FlatFile.DopplerShift.DopplerShiftExporter.ExportException;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

/**
 * embedded class to generate menu-items for creating a new sensor
 */
public class ExportDopplerShift extends TimeControllerOperation {
	private static final String ACTION_NAME = "Export Doppler Shift data";
	public static final String FILE_SUFFIX = "csv";
	private static final String HELP_CONTEXT = "org.mwc.debrief.help.ExportDoppler";

	public ExportDopplerShift() {
		super(ACTION_NAME, true, false, false, true);
	}

	@Override
	public void executeExport(final WatchableList primaryTrack, final WatchableList[] secondaryTracks,
			final TimePeriod period) {

		final DopplerShiftExporter ff = new DopplerShiftExporter();
		String theData = null;
		try {
			// sort out the base frequency
			double baseFreq = -1;

			final TrackWrapper prim = (TrackWrapper) primaryTrack;
			final BaseLayer sensors = prim.getSensors();
			final Enumeration<Editable> numer = sensors.elements();
			while (numer.hasMoreElements()) {
				final SensorWrapper sensor = (SensorWrapper) numer.nextElement();
				if (sensor.getVisible()) {
					final double hisBase = sensor.getBaseFrequency();
					if (baseFreq == -1 && hisBase != -1) {
						baseFreq = hisBase;
					} else if (baseFreq != -1 && hisBase != -1) {
						throw new ExportException("Target track has multiple recorded base frequencies");
					}
				}
			}

			theData = ff.export(primaryTrack, secondaryTracks, period, baseFreq);
		} catch (final ExportException e1) {
			CorePlugin.logError(IStatus.ERROR, "Whilst exporting doppler shift data", e1);
			CorePlugin.showMessage("Export to doppler", e1.getMessage());
			return;
		}

		// did it work?
		if (theData != null) {

			// sort out the destination file name
			String filePath = null;

			final SimplePageListWizard wizard = new SimplePageListWizard();
			final DirectorySelectorWizardPage exportPage = new DirectorySelectorWizardPage("ExportDoppler", ACTION_NAME,
					"Please select the directory where Debrief will \nplace the exported Doppler shift file.",
					"org.mwc.debrief.core", "images/DopplerEffect.png", HELP_CONTEXT);
			wizard.addWizard(exportPage);
			wizard.setHelpAvailable(true);
			final WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
			dialog.create();
			dialog.open();

			// did it work?
			if (dialog.getReturnCode() == Window.OK) {
				if (exportPage.isPageComplete()) {
					filePath = exportPage.getFileName();
				}

				// now write the data to file
				final String HOST_NAME = primaryTrack.getName();
				final String HOST_DATE = MWC.Utilities.TextFormatting.FormatRNDateTime
						.toMediumString(period.getStartDTG().getDate().getTime());

				final String fileName = filePath + File.separator + HOST_NAME + "_" + HOST_DATE + "." + FILE_SUFFIX;

				BufferedWriter out = null;
				try {
					final FileWriter fOut = new FileWriter(fileName);
					out = new BufferedWriter(fOut);
					out.write(theData);
					out.flush();
				} catch (final FileNotFoundException e) {
					DebriefPlugin.logError(IStatus.ERROR, "Unable to find output file:" + fileName, e);
				} catch (final IOException e) {
					DebriefPlugin.logError(IStatus.ERROR, "Whilst writing to output file:" + fileName, e);
				} finally {
					try {
						if (out != null) {
							out.close();
						}
					} catch (final IOException e) {
						DebriefPlugin.logError(IStatus.ERROR, "Whilst closing output file:" + fileName, e);
					}
				}
			}
		}

	}

	@Override
	public ImageDescriptor getDescriptor() {
		return DebriefPlugin.getImageDescriptor("icons/export_doppler.png");
	}

}