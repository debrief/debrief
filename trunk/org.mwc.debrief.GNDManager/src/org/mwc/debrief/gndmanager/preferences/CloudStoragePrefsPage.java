package org.mwc.debrief.gndmanager.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.TrackStoreWrapper;

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

public class CloudStoragePrefsPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{
	public CloudStoragePrefsPage()
	{
		super("Cloud Storage", CorePlugin.getImageDescriptor("icons/coast.gif"),
				GRID);
		setDescription("Locations of cloud-based storage");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		addField(new StringFieldEditor(PreferenceConstants.COUCH_URL,
				"&CouchDb Database URL:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.ES_URL,
				"&ElasticSearch index URL:", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(DebriefPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Constant definitions for plug-in preferences
	 */
	public static class PreferenceConstants
	{
		public static final String COUCH_URL = TrackStoreWrapper.COUCHDB_LOCATION;
		public static final String ES_URL = TrackStoreWrapper.ES_LOCATION;
	}

}