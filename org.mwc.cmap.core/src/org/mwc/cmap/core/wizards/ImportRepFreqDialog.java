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
package org.mwc.cmap.core.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;

public class ImportRepFreqDialog extends CoreFreqImportDialog
{

  private long sampleFreq;
  private final String _trackName;
  public final String IMPORT_FREQ = "RepImportFreq";

  public ImportRepFreqDialog(final Shell parentShell, final String trackName)
  {
    super(parentShell);
    _trackName = trackName;

    // retrieve the sample frequency
    // ok, remember this value
    String freq = CorePlugin.getToolParent().getProperty(IMPORT_FREQ);
    if(freq != null && freq.length() > 0)
    {
      sampleFreq = Long.valueOf(freq);
    }
  }

  @Override
  public void create()
  {
    super.create();
    setTitle("Add REP data to existing track");
    getButton(IDialogConstants.OK_ID).setText("Import");
    setMessage("Please choose the frequency at which data will be imported (or 'none' to not import that type).");
  }

  @Override
  protected Control createDialogArea(final Composite parent)
  {
    final Composite base = (Composite) super.createDialogArea(parent);
    final Composite composite = new Composite(base, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
    composite.setLayout(new GridLayout(2, false));
    new Label(composite, SWT.NONE).setText("Sampling frequency for "
        + _trackName + ":");
    final ComboViewer comboViewer = new ComboViewer(composite);
    comboViewer.setContentProvider(new ArrayContentProvider());
    comboViewer.setLabelProvider(newLabelProvider());
    comboViewer.setInput(getDataSet());
    comboViewer.getCombo().setLayoutData(
        new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
    comboViewer.setSelection(new StructuredSelection(Long.valueOf(sampleFreq)));
    comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(final SelectionChangedEvent event)
      {
        final IStructuredSelection selection =
            (IStructuredSelection) event.getSelection();
        if (selection.getFirstElement() instanceof Long)
        {
          sampleFreq = (Long) selection.getFirstElement();

          // ok, remember this value
          CorePlugin.getToolParent().setProperty(IMPORT_FREQ, "" + sampleFreq);

        }
      }
    });
    return composite;
  }

  public long getSampleFreq()
  {
    return sampleFreq;
  }

}
