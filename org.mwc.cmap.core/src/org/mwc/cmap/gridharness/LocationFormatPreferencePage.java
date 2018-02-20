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
package org.mwc.cmap.gridharness;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;

import sun.awt.SunHints.LCDContrastKey;

public class LocationFormatPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

  public static final String LABEL_DD_DDD = "DD.DDDDD\u00B0";

  public static final String LABEL_DD_MM_MMM = "DD\u00B0MM.MMM\u2032";

  public static final String LABEL_DD_MM_SS_SSS =
      "DD\u00B0MM\u2032SS.SSS\u2033";

  public static final String PREFS_PAGE_ID =
      "org.mwc.cmap.core.preferences.LocationFormatPreferencePage";

  public LocationFormatPreferencePage()
  {
    super(GRID);
    setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
  }

  public void init(final IWorkbench workbench)
  {
    //
  }

  @Override
  protected void createFieldEditors()
  {
    final Composite main = new Composite(getFieldEditorParent(), SWT.NULL);
    main.setLayout(new GridLayout());
    main.setData("main");
    main.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
    createLocationFormatGroup(main);
  }

  private void createLocationFormatGroup(final Composite parent)
  {
    final Composite locationFormatGroup = new Composite(parent, SWT.NULL);
    locationFormatGroup.setData("group");

    final String[][] choices = new String[][]
    {//
     //
        {LABEL_DD_MM_MMM, LABEL_DD_MM_MMM}, //
        {LABEL_DD_DDD, LABEL_DD_DDD}, //
        {LABEL_DD_MM_SS_SSS, LABEL_DD_MM_SS_SSS} //
    };

    Label lbl = new Label(locationFormatGroup, SWT.NONE);
    lbl.setText("Location format:");

    lbl = new Label(locationFormatGroup, SWT.NONE);
    lbl.setText("bbb cccc   dddd");

    final RadioGroupFieldEditor formarEditor = new RadioGroupFieldEditor(//
        CorePlugin.PREF_BASE60_FORMAT_NO_SECONDS, //
        "", //
        1, choices, locationFormatGroup, true);

    addField(formarEditor);

    lbl = new Label(locationFormatGroup, SWT.NONE);
    lbl.setText("aaa");

    final FontFieldEditor fontEditor = new FontFieldEditor(
        CorePlugin.DEFAULT_FONT, "Default font:", "0133:44",
        locationFormatGroup)
    {
      @Override
      protected void doStore()
      {
        // let the parent store it
        super.doStore();

        // clear the cached value
        CorePlugin.getDefault().clearDefaultFont();
      }
    };
    fontEditor.setPreferenceStore(CorePlugin.getDefault().getPreferenceStore());
    fontEditor.load();
    fontEditor.setPage(this);

    addField(fontEditor);

    // the field editors mangle the layout, so we do it last.

    locationFormatGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
        true));
    final GridLayout layout = new GridLayout(4, false);
    locationFormatGroup.setLayout(layout);

    parent.pack(true);

  }

  protected void adjustGridLayout()
  {
    // DON'T BOTHER - WE HAVE OUR GRID
  }

}
