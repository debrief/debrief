package org.mwc.debrief.gndmanager.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.debrief.gndmanager.Activator;

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

public class GNDPrefsPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	public GNDPrefsPage() {
		super("GND Server locations", Activator.getImageDescriptor("icons/couchdb.png"), GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Web-URLs for the track datastore, and the index");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.DB_URL, 
				"Datastore URL:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.INDEX_URL, 
				"Index URL", getFieldEditorParent()));
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

		public static final String DB_URL = "CouchDb_URL";
		public static final String INDEX_URL = "ElasticSearch_URL";
	}	
	
}