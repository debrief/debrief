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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.interfaces.IPlotLoader.BaseLoader;

import MWC.GUI.Layers;

/**
 * parent class for text-file loaders
 *
 * @author ian
 *
 */
public abstract class CoreLoader extends BaseLoader {
	/**
	 * text description of file-type, used for logging
	 *
	 */
	protected final String _fileType;

	/**
	 * (optional) file suffix. we test against it, if present
	 *
	 */
	private final String _suffix;

	/**
	 *
	 * @param fileType human readable description of file-type, for logging
	 * @param suffix   (optional) file suffix to test file-name against
	 */
	public CoreLoader(final String fileType, final String suffix) {
		_fileType = fileType;
		_suffix = suffix;
	}

	/**
	 * get the importer code
	 *
	 * @param layers the destination for the data
	 * @return a runnable that will perform the import process
	 * @throws Exception
	 */
	abstract protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers layers,
			final InputStream inputStream, final String fileName) throws Exception;

	@Override
	public void loadFile(final IAdaptable target, final InputStream inputStream, final String fileName,
			final CompleteListener listener) {
		// ok, get reading
		if (_suffix == null || fileName.toLowerCase().endsWith(_suffix)) {
			final Layers layers = target.getAdapter(Layers.class);
			try {
				final IRunnableWithProgress runnable = getImporter(target, layers, inputStream, fileName);
				// hmm, is there anything in the file?
				final int numAvailable = inputStream.available();
				if (numAvailable > 0) {
					layers.suspendFiringExtended(false);

					final IWorkbench wb = PlatformUI.getWorkbench();
					final IProgressService ps = wb.getProgressService();
					ps.busyCursorWhile(runnable);
				}
			} catch (final InvocationTargetException e) {
				DebriefPlugin.logError(IStatus.ERROR, "Problem loading datafile:" + fileName, e);
			} catch (final InterruptedException e) {
				DebriefPlugin.logError(IStatus.ERROR, "Problem loading datafile:" + fileName, e);
			} catch (final IOException e) {
				DebriefPlugin.logError(IStatus.ERROR, "Problem loading " + _fileType + ":" + fileName, e);
			} catch (final Exception e) {
				DebriefPlugin.logError(IStatus.ERROR, "Problem loading " + _fileType + ":" + fileName, e);
			} finally {
				listener.complete(this);
				layers.suspendFiringExtended(false);
			}

			// ok, load the data...
			DebriefPlugin.logError(IStatus.INFO, "Successfully loaded " + _fileType + " file", null);
		} else {
			// ok, load the data...
			DebriefPlugin.logError(IStatus.WARNING, "Not loading " + _fileType + ", suffix doesn't match " + _suffix,
					null);

		}
	}

}
