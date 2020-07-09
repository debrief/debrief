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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import Debrief.ReaderWriter.Word.ImportRiderNarrativeDocument;
import MWC.GUI.Layers;
import MWC.TacticalData.TrackDataProvider;

public class MsDocXLoader extends CoreLoader {

	public MsDocXLoader() {
		super(".docx", ".docx");
	}

	@Override
	protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers theLayers,
			final InputStream inputStream, final String fileName) {
		final TrackDataProvider trackData = target.getAdapter(TrackDataProvider.class);

		return new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor pm) {
				// ok. we'll pass it to the rider import. If that fails, we can offer it to the
				// plain importer
				final ImportRiderNarrativeDocument iw = new ImportRiderNarrativeDocument(theLayers, trackData);
				iw.handleImportX(fileName, inputStream);
			}
		};
	}
}
