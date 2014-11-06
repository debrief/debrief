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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;

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

public class PrefsPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	public PrefsPage()
	{
		super(GRID);
		setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
		setDescription("Settings applicable to MWC's Debrief analysis tool");
	}


	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		addField(new BooleanFieldEditor(PreferenceConstants.AUTO_SELECT, "Select newly created items in Properties View", getFieldEditorParent()));		
		addField(new BooleanFieldEditor(PreferenceConstants.CALC_SLANT_RANGE, "Use Slant range in Tote range calculations", getFieldEditorParent()));		
		addField(new BooleanFieldEditor(PreferenceConstants.DONT_SHOW_DRAG_IN_PROPS, "Don't Show current details in properties window when dragging TMA solution", getFieldEditorParent()));		
		addField(new BooleanFieldEditor(PreferenceConstants.ASK_ABOUT_PROJECT, "Invite user to create Debrief project if none present", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench)
	{
	}

	/**
	 * Constant definitions for plug-in preferences
	 */
	public static class PreferenceConstants
	{
		public static final String AUTO_SELECT = "AUTO_SELECT";
		public static final String CALC_SLANT_RANGE = "CALC_SLANT_RANGE";
		public static final String DONT_SHOW_DRAG_IN_PROPS = "DONT_SHOW_DRAG_IN_PROPS";
		public static final String ASK_ABOUT_PROJECT = "createProject";
	}

}