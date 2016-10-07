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
package org.mwc.cmap.NarrativeViewer.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;

public class NarrativeViewerPrefsPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

  public NarrativeViewerPrefsPage()
  {
    super("Narrative Viewer", CorePlugin
        .getImageDescriptor("icons/16/narrative_viewer.png"), GRID);
    setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
    setDescription("Provide the phrases to be highlighted (comma delimeted) and Fonts for narrative viewer.");
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
   * manipulate various types of preferences. Each field editor knows how to save and restore
   * itself.
   */
  public void createFieldEditors()
  {
    addField(new StringFieldEditor(PreferenceConstants.HIGHLIGHT_PHRASES,
        "&Words/phrases:", getFieldEditorParent()));
    addField(new FontFieldEditor(PreferenceConstants.FONT, "Font",getFieldEditorParent()));
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

    public static final String HIGHLIGHT_PHRASES =
        "narrative_viewer_highlight_phrases";
    public static final String FONT = "narrative_viewer_font";
  }

}