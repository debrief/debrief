package org.mwc.cmap.core.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;

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

public class VPFPrefsPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public VPFPrefsPage() {
		super("VPF Library locations", CorePlugin.getImageDescriptor("icons/large_vpf.gif"), GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Location for VPF library data");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH_1, 
				"&VPF Directory preference (1):", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH_2, 
				"&VPF Directory preference (2):", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH_3, 
				"&VPF Directory preference (3):", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH_4, 
				"&VPF Directory preference (4):", getFieldEditorParent()));
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

		public static final String P_PATH_1 = "VPF_DATABASE_DIR.1";
		public static final String P_PATH_2 = "VPF_DATABASE_DIR.2";
		public static final String P_PATH_3 = "VPF_DATABASE_DIR.3";
		public static final String P_PATH_4 = "VPF_DATABASE_DIR.4";	
	}	
	
}