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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;

import Debrief.Wrappers.Track.DynamicInfillSegment;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace
 * that allows us to create a page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */

public class PrefsPage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage
{

  public PrefsPage()
  {
    super("Debrief Preferences", CorePlugin.getImageDescriptor("icons/24/debrief_icon.png"), GRID);
    setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
    setDescription("Settings applicable to Debrief analysis tool");
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
   * manipulate various types of preferences. Each field editor knows how to save and restore
   * itself.
   */
  public void createFieldEditors()
  {
    addField(new BooleanFieldEditor(PreferenceConstants.AUTO_SELECT,
        "Select newly created items in Properties View", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.CALC_SLANT_RANGE,
        "Use Slant range in Tote range calculations", getFieldEditorParent()));
    addField(new BooleanFieldEditor(
        PreferenceConstants.DONT_SHOW_DRAG_IN_PROPS,
        "Don't Show current details in properties window when dragging TMA solution",
        getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.ASK_ABOUT_PROJECT,
        "Invite user to create Debrief project if none present",
        getFieldEditorParent()));
    
    // insert a separator
    Label label = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
    label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));    

    // initialise the import choice tags, if we have to
    String[][] _trackModeTags = new String[3][2];
    _trackModeTags[0][0] = "Darker version of previous leg color";
    _trackModeTags[0][1] = DynamicInfillSegment.DARKER_INFILL;
    _trackModeTags[1][0] = "Random color";
    _trackModeTags[1][1] = DynamicInfillSegment.RANDOM_INFILL;
    _trackModeTags[2][0] = "Green";
    _trackModeTags[2][1] = DynamicInfillSegment.GREEN_INFILL;

    addField(new RadioGroupFieldEditor(
        PreferenceConstants.INFILL_COLOR_STRATEGY,
        "Policy for dynamic infill colors:", 1, _trackModeTags,
        getFieldEditorParent()));

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
    public static final String DONT_SHOW_DRAG_IN_PROPS =
        "DONT_SHOW_DRAG_IN_PROPS";
    public static final String ASK_ABOUT_PROJECT = "createProject";
    public static final String INFILL_COLOR_STRATEGY =
        DynamicInfillSegment.INFILL_COLOR_STRATEGY;
  }

}