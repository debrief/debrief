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
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;

public class NarrativeViewerPrefsPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

  final private class MyFontFieldEditor extends FontFieldEditor
  {
    private Font prefFont;

    public MyFontFieldEditor(final String font, final String string,
        final Composite fieldEditorParent)
    {
      super(font, string, fieldEditorParent);
    }

    @Override
    public void dispose()
    {
      super.dispose();
      if (prefFont != null)
      {
        prefFont.dispose();
        prefFont = null;
      }
    }

    @Override
    protected void doLoad()
    {

      super.doLoad();
      updatePreview();
    }

    @Override
    protected void doLoadDefault()
    {

      super.doLoadDefault();
      updatePreview();
    }

    @Override
    public void
        setPropertyChangeListener(final IPropertyChangeListener listener)
    {
      super.setPropertyChangeListener(new IPropertyChangeListener()
      {
        @Override
        public void propertyChange(final PropertyChangeEvent event)
        {
          listener.propertyChange(event);
          if (prefFont != null)
          {
            prefFont.dispose();
            prefFont = null;
          }
          final FontData[] readFontData = new FontData[]
          {(FontData) event.getNewValue()};
          if (readFontData != null)
          {
            prefFont = new Font(Display.getDefault(), readFontData);
            previewText.setFont(prefFont);
          }
        }
      });
    }

    protected void updatePreview()
    {
      if (prefFont != null)
      {
        prefFont.dispose();
        prefFont = null;
      }
      final FontData[] readFontData =
          PreferenceConverter.readFontData(CorePlugin.getDefault()
              .getPreferenceStore().getString(
                  NarrativeViewerPrefsPage.PreferenceConstants.FONT));
      if (readFontData != null)
      {
        prefFont = new Font(Display.getDefault(), readFontData);
        previewText.setFont(prefFont);
      }
    }

  }

  /**
   * Constant definitions for plug-in preferences
   */
  public final static class PreferenceConstants
  {

    public static final String HIGHLIGHT_PHRASES =
        "narrative_viewer_highlight_phrases";
    public static final String FONT = "narrative_viewer_font";
  }

  private Text previewText;

  public NarrativeViewerPrefsPage()
  {
    super("Narrative Viewer", CorePlugin
        .getImageDescriptor("icons/16/narrative_viewer.png"), GRID);
    setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
    setDescription("Provide the phrases to be highlighted (comma delimited) and Fonts for narrative viewer.");
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
   * manipulate various types of preferences. Each field editor knows how to save and restore
   * itself.
   */
  @Override
  public void createFieldEditors()
  {
    addField(new StringFieldEditor(PreferenceConstants.HIGHLIGHT_PHRASES,
        "&Words/phrases:", getFieldEditorParent()));

    final FontFieldEditor fontEditor =
        new MyFontFieldEditor(PreferenceConstants.FONT, "Font:",
            getFieldEditorParent());

    addField(fontEditor);
    final Label label = new Label(getFieldEditorParent(), SWT.NONE);

    label.setText("Font Preview:");
    previewText =
        new Text(getFieldEditorParent(), SWT.BORDER | SWT.MULTI | SWT.WRAP);
    final GridData gd =
        new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
    gd.heightHint = 30;
    gd.horizontalSpan = 4;
    previewText.setLayoutData(gd);
    previewText.setText("Sample text");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  @Override
  public void init(final IWorkbench workbench)
  {
  }

}