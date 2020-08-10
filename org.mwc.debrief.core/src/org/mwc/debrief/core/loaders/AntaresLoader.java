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
package org.mwc.debrief.core.loaders;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import Debrief.ReaderWriter.Antares.ImportAntares;
import Debrief.ReaderWriter.Antares.ImportAntaresImpl.ImportAntaresException;
import MWC.GUI.Layers;

public class AntaresLoader extends CoreLoader {

	public AntaresLoader() {
		super("Antares", "txt");
	}

	public boolean canLoad(final InputStream inputStream) {
		final ImportAntares antaresImporter = new ImportAntares();
		return antaresImporter.canImportThisInputStream(inputStream);
	}

	@Override
	public boolean canLoad(final String fileName) {
		if (super.canLoad(fileName)) {
			final ImportAntares antaresImporter = new ImportAntares();
			return antaresImporter.canImportThisFile(fileName);
		} else {
			return false;
		}
	}

	@Override
	protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers layers,
			final InputStream inputStream, final String fileName) throws Exception {

		System.out.println("A");
		return new IRunnableWithProgress() {

			@Override
			public void run(final IProgressMonitor arg0) throws InvocationTargetException, InterruptedException {

				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {

						final Shell active = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
						final AntaresSWTDialog antaresDialog = new AntaresSWTDialog(active);

						antaresDialog.create();

						if (antaresDialog.open() == Window.OK) {
							final ImportAntares antaresImporter = new ImportAntares();
							antaresImporter.setMonth(antaresDialog.getMonth());
							antaresImporter.setYear(antaresDialog.getYear());
							antaresImporter.setTrackName(antaresDialog.getTrackName());
							antaresImporter.setLayers(layers);

							antaresImporter.importThis(fileName, inputStream);

							if (!antaresImporter.getErrors().isEmpty()) {
								final List<Status> status = new ArrayList<>();
								for (final ImportAntaresException error : antaresImporter.getErrors()) {
									status.add(new Status(IStatus.ERROR, "Antares Import", error.getMessage()));
								}

								final MultiStatus multiStatus = new MultiStatus("Antares Import", IStatus.ERROR,
										status.toArray(new Status[] {}), "Some lines didn't have the Antares format",
										null);

								ErrorDialog.openError(active, "Error", "File Imported with errors", multiStatus);
							}
						}
					}
				});
			}
		};
	}

}
