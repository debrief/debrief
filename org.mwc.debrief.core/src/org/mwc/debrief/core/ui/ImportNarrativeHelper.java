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
package org.mwc.debrief.core.ui;

import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.ImportNarrativeEnum;
import Debrief.ReaderWriter.Word.ImportNarrativeDocument.NarrativeHelperRetVal;
import Debrief.ReaderWriter.Word.ImportNarrativeDocument.TrimNarrativeHelper;

/**
 * Helper class to pop up dialog to offer choice to analyst to import all data
 * or loaded tracks
 *
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class ImportNarrativeHelper implements TrimNarrativeHelper {

	public static final String PREF_DEF_NARRATIVE_CHOICE = "defaultNarrativeEntryChoice";

	@Override
	public NarrativeHelperRetVal findWhatToImport(final Map<String,Integer> narrativeTypes) {
		final Display targetDisplay;
		final NarrativeHelperRetVal retVal = new NarrativeHelperRetVal();
//		final boolean reuseChoice = CorePlugin.getDefault().getPreferenceStore()
//				.getBoolean(PreferenceConstants.REUSE_TRIM_NARRATIVES_DIALOG_CHOICE);
//		final String defaultChoice = CorePlugin.getDefault().getPreference(PREF_DEF_NARRATIVE_CHOICE);
//		if (reuseChoice && defaultChoice != null && !defaultChoice.isEmpty()) {
//			retVal.narrativeEnum = ImportNarrativeEnum.getByName(defaultChoice);
//			
//		} else {
//			final StringBuilder retVal = new StringBuilder();
			if (Display.getCurrent() == null) {
				targetDisplay = Display.getDefault();
			} else {
				targetDisplay = Display.getCurrent();
			}

			// ok, get the answer
			targetDisplay.syncExec(new Runnable() {
				@Override
				public void run() {
					final ImportNarrativeDialog dialog = new ImportNarrativeDialog(targetDisplay.getActiveShell(),narrativeTypes);
					if (dialog.open() == Window.OK) {
						final ImportNarrativeEnum userChoice = dialog.getUserChoice();
						retVal.narrativeEnum = userChoice;
						if (dialog.getPreference()) {
							CorePlugin.getDefault().getPreferenceStore().setValue(PREF_DEF_NARRATIVE_CHOICE,
									userChoice.getName());
						}
						retVal.selectedNarrativeTypes = dialog.getSelectedNarrativeTypes();
					} 
					else {
						retVal.narrativeEnum = ImportNarrativeEnum.CANCEL;
					}

				}
			});
			return retVal;
//		}
	}

}
