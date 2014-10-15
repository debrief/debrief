/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.gridharness;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;

public class LocationFormatPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String LABEL_DD_MM_MMM = "DD\u00B0MM.MMM\u2032";

	private static final String LABEL_DD_MM_SS_SSS = "DD\u00B0MM\u2032SS.SSS\u2033";
	
  public static final String PREFS_PAGE_ID = "org.mwc.cmap.core.preferences.LocationFormatPreferencePage";


	public LocationFormatPreferencePage() {
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
	}

	public void init(final IWorkbench workbench) {
		//
	}

	@Override
	protected void createFieldEditors() {
		final Composite main = new Composite(getFieldEditorParent(), SWT.NULL);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		createLocationFormatGroup(main);
	}

	private void createLocationFormatGroup(final Composite parent) {
		final Composite locationFormatGroup = new Composite(parent, SWT.NULL);
		locationFormatGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		final GridLayout layout = new GridLayout();
		locationFormatGroup.setLayout(layout);
		final String[][] choices = new String[][] {//
		//
				{ LABEL_DD_MM_MMM, "true" }, //
				{ LABEL_DD_MM_SS_SSS, "false" }, //
		};

		final RadioGroupFieldEditor formarEditor = new RadioGroupFieldEditor(//
				CorePlugin.PREF_BASE60_FORMAT_NO_SECONDS, //
				"Location Format", //
				1, choices, locationFormatGroup, true);

		addField(formarEditor);
	}
}
