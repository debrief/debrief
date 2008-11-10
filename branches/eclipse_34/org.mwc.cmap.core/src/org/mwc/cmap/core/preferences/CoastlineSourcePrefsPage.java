package org.mwc.cmap.core.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Chart.Painters.CoastPainter;

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

public class CoastlineSourcePrefsPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public CoastlineSourcePrefsPage() {
		super("Coastline location", CorePlugin.getImageDescriptor("icons/coast.gif"), GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Destination of low-res coastline file");
	}


	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new FileFieldEditor(PreferenceConstants.COASTLINE_FILE, 
				"&Low res file location:", getFieldEditorParent()));
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

		public static final String COASTLINE_FILE =  CoastPainter.PREF_NAME;
	}
	
}