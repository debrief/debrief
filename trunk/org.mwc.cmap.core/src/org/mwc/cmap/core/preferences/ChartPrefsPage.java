package org.mwc.cmap.core.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ChartPrefsPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ChartPrefsPage() {
		super("Chart Options", CorePlugin
				.getImageDescriptor("icons/chart_map.png"), GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Options related to display of chart backdrop imagery");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new ScaleFieldEditor(PreferenceConstants.CHART_TRANSPARENCY,
				"Chart backdrop transparency (0=transparent, 255=solid):",
				getFieldEditorParent(), 0,255,1,10));
		
		addField(new DirectoryFieldEditor(PreferenceConstants.CHART_FOLDER,
				"Parent folder for chart libraries",
				getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Constant definitions for plug-in preferences
	 */
	public static class PreferenceConstants {
		public static final String CHART_TRANSPARENCY = "CHART_TRANSPARENCY";
		public static final String CHART_FOLDER = "CHART_FOLDER";
	}

}