/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.NarrativeTypeHelper;

/**
 * @author Ayesha
 *
 */
public class ImportNarrativeTypesHelper implements NarrativeTypeHelper {

	@Override
	public List<String> getSelectedNarrativeTypes(final Map<String,Integer> narrativeTypes) {
		final Display targetDisplay;
		final List<String> retVal = new ArrayList<String>();
		if (Display.getCurrent() == null) {
			targetDisplay = Display.getDefault();
		} else {
			targetDisplay = Display.getCurrent();
		}

		// ok, get the answer
		targetDisplay.syncExec(new Runnable() {
			@Override
			public void run() {
				final SelectNarrativeTypesDialog dialog = new SelectNarrativeTypesDialog(targetDisplay.getActiveShell(),narrativeTypes);
				if (dialog.open() == Window.OK) {
					final List<String> narrativeTypes = dialog.getSelectedTypes();
					retVal.addAll(narrativeTypes);
				}
			}
		});
		return retVal;
	}

}
