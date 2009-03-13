package org.mwc.cmap.core.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Tools.Chart.WriteMetafile;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class WMFExportPrefsPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public WMFExportPrefsPage() {
		super("WMF Export location", CorePlugin.getImageDescriptor("icons/write_wmf.gif"), GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Destination of WMF Export plots");
	}


	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.WMF_DIRECTORY, 
				"&WMF data file location:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	/**
	 * Constant definitions for plug-in preferences
	 */
	public static class PreferenceConstants {

		public static final String WMF_DIRECTORY = WriteMetafile.PROP_NAME;
	}
	
}