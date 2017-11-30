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
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegmentFromCuts;

import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;

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

  private SelectionListener freqListener;
  private ScaleFieldEditor freqEdit;

  public PrefsPage()
  {
    super("Debrief Preferences", CorePlugin
        .getImageDescriptor("icons/24/debrief_icon.png"), GRID);
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
    addField(new BooleanFieldEditor(
        PreferenceConstants.USE_IMPORT_SENSOR_WIZARD,
        "Show the wizard when importing sensor data from REP",
        getFieldEditorParent()));

    // insert a separator
    Label label =
        new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
    label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

    addField(new BooleanFieldEditor(PreferenceConstants.USE_CUT_COLOR,
        "Use sensor cut colors for new TMA leg positions",
        getFieldEditorParent()));

    addField(new IntegerFieldEditor(PreferenceConstants.CUT_OFF_VALUE_DEGS,
        "Cut-off value for acceptable bearing errors in stacked dots (degs)",
        getFieldEditorParent()));
    
    final String freqLabelStr = "Cut-off value for acceptable frequency errors in stacked dots";
    freqEdit = new ScaleFieldEditor(PreferenceConstants.CUT_OFF_VALUE_HZ, "Cut-off", getFieldEditorParent());
    freqEdit.setMinimum(0);
    freqEdit.setMaximum(100);
    freqEdit.setIncrement(5);
    freqEdit.setLabelText(freqLabelStr + " (x.xx Hz) ");
    freqListener = new SelectionListener()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        int curInt = freqEdit.getScaleControl().getSelection();
        double curVal = (double)curInt / 100d;
        freqEdit.setLabelText(freqLabelStr + " (" + curVal + " Hz)");
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e)
      {
        // TODO Auto-generated method stub
        
      }
    };
    freqEdit.getScaleControl().addSelectionListener(freqListener);
    addField(freqEdit);

    // insert a separator
    Label label2 =
        new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
    label2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));

    // initialise the import choice tags, if we have to
    String[][] _trackModeTags = new String[3][2];
    _trackModeTags[0][0] = "Darker version of previous leg color";
    _trackModeTags[0][1] = DynamicInfillSegment.DARKER_INFILL;
    _trackModeTags[1][0] = "Random color";
    _trackModeTags[1][1] = DynamicInfillSegment.RANDOM_INFILL;
    _trackModeTags[2][0] = "Single shade (green)";
    _trackModeTags[2][1] = DynamicInfillSegment.GREEN_INFILL;

    addField(new RadioGroupFieldEditor(
        PreferenceConstants.INFILL_COLOR_STRATEGY,
        "Policy for dynamic infill colors:", 1, _trackModeTags,
        getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.MERGED_TRACK_COLOR,
        "Default color for merged track:", getFieldEditorParent()));
    addField(new ColorFieldEditor(PreferenceConstants.MERGED_INFILL_COLOR,
        "Color for infill segments in merged track:", getFieldEditorParent()));

  }
  
  

  @Override
  public void dispose()
  {
    // drop the manually generated listener
    freqEdit.getScaleControl().removeSelectionListener(freqListener);

    // let the parent carry on ditching
    super.dispose();
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
    public static final String USE_IMPORT_SENSOR_WIZARD =
        "USE_IMPORT_SENSOR_WIZARD";
    public static final String ASK_ABOUT_PROJECT = "createProject";
    public static final String INFILL_COLOR_STRATEGY =
        DynamicInfillSegment.INFILL_COLOR_STRATEGY;
    public static final String MERGED_INFILL_COLOR = "MERGED_INFILL_COLOR";
    public static final String MERGED_TRACK_COLOR = "MERGED_TRACK_COLOR";
    public static final String CUT_OFF_VALUE_DEGS =
        RelativeTMASegment.CUT_OFF_VALUE_DEGS;
    public static final String CUT_OFF_VALUE_HZ =
        RelativeTMASegment.CUT_OFF_VALUE_HZ;
    public static final String USE_CUT_COLOR =
        GenerateTMASegmentFromCuts.USE_CUT_COLOR;
  }

}