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

	@Override
	public void init(IWorkbench workbench) {
		//
	}

	@Override
	protected void createFieldEditors() {
		Composite main = new Composite(getFieldEditorParent(), SWT.NULL);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		createLocationFormatGroup(main);
	}

	private void createLocationFormatGroup(Composite parent) {
		Composite locationFormatGroup = new Composite(parent, SWT.NULL);
		locationFormatGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		GridLayout layout = new GridLayout();
		locationFormatGroup.setLayout(layout);
		String[][] choices = new String[][] {//
		//
				{ LABEL_DD_MM_MMM, "true" }, //
				{ LABEL_DD_MM_SS_SSS, "false" }, //
		};

		RadioGroupFieldEditor formarEditor = new RadioGroupFieldEditor(//
				CorePlugin.PREF_BASE60_FORMAT_NO_SECONDS, //
				"Location Format", //
				1, choices, locationFormatGroup, true);

		addField(formarEditor);
	}
}
