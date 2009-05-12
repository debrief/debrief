package org.mwc.cmap.core.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Tools.Tote.Calculations.relBearingCalc;
import MWC.GUI.Properties.UnitsPropertyEditor;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 * we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class CMAPPrefsPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

  public static final String PREFS_PAGE_ID = "org.mwc.cmap.core.preferences.CMAPPrefsPage";
  
	public CMAPPrefsPage()
	{
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Settings applicable to MWC's Core Maritime Analysis Platform");
	}

	/** the tags and labels to use in the range units editor
	 * 
	 */
	private static String[][] _distanceUnitTags;
	
	/** the tags and labels to use in the relative bearing format selector
	 * 
	 */
	private static String[][] _relBearingTags;
	
	/** the options for what to do when importing a track
	 * 
	 */
	private static String[][] _trackModeTags;
	

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		// initialise the units tags, if we have to
		if (_distanceUnitTags == null)
		{
			// get the unit types
			UnitsPropertyEditor units = new UnitsPropertyEditor();
			String[] tags = units.getTags();

			_distanceUnitTags = new String[tags.length][2];
			for (int i = 0; i < tags.length; i++)
			{
				_distanceUnitTags[i][0] = tags[i];
				_distanceUnitTags[i][1] = tags[i];
			}
		}

		// initialise the units tags, if we have to
		if (_relBearingTags == null)
		{
			_relBearingTags = new String[2][2];
			_relBearingTags[0][0] = "UK format (R180..G180)";
			_relBearingTags[0][1] = relBearingCalc.UK_REL_BEARING_FORMAT;
			_relBearingTags[1][0] = "US format (0..360)";
			_relBearingTags[1][1] = relBearingCalc.US_REL_BEARING_FORMAT;
		}
		
		// initialise the import choice tags, if we have to
		if (_trackModeTags == null)
		{
			_trackModeTags = new String[3][2];
			_trackModeTags[0][0] = "DR Track";
			_trackModeTags[0][1] =  ImportReplay.IMPORT_AS_DR;
			_trackModeTags[1][0] = "ATG Track";
			_trackModeTags[1][1] = ImportReplay.IMPORT_AS_ATG;
			_trackModeTags[2][0] = "Ask user";
			_trackModeTags[2][1] = ImportReplay.ASK_THE_AUDIENCE;
		}

		
		addField(new RadioGroupFieldEditor(PreferenceConstants.IMPORT_MODE,
				"Default &track import mode:", 1, _trackModeTags, getFieldEditorParent()));
		
		addField(new RadioGroupFieldEditor(PreferenceConstants.RNG_UNITS,
				"Default &range units:", 1, _distanceUnitTags, getFieldEditorParent()));
		
		addField(new RadioGroupFieldEditor(PreferenceConstants.REL_BEARING_FORMAT,
				"Relative &bearing format:", 1, _relBearingTags, getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * Constant definitions for plug-in preferences
	 */
	public static class PreferenceConstants
	{
		public static final String RNG_UNITS = MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY;
		public static final String REL_BEARING_FORMAT = relBearingCalc.REL_BEARING_FORMAT;
		public static final String IMPORT_MODE = ImportReplay.TRACK_IMPORT_MODE;
	}

}