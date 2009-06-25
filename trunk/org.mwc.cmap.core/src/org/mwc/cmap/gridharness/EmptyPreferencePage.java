package org.mwc.cmap.gridharness;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;

public class EmptyPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EmptyPreferencePage() {
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		//
	}

	public void init(IWorkbench workbench) {
	}
}
