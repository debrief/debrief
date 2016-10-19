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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;

public class ImportNMEADialog extends CoreFreqImportDialog
{

  /**
   * @param args
   */
  public static void main(final String[] args)
  {

    Display display = Display.getDefault();
    if (display.isDisposed())
      display = new Display();
    final Shell shell = new Shell(display, SWT.NO_TRIM);
    final ImportNMEADialog dialog = new ImportNMEADialog(shell);
    dialog.open();
  }

  private long ownshipFreq;

  private long thirdPartyFreq;

  public final String OS_FREQ = "NMEA_OwnshipFreq";
  public final String TGT_FREQ = "NMEA_TargetFreq";

  
  public ImportNMEADialog()
  {
    this(Display.getDefault().getActiveShell());
  }

  public ImportNMEADialog(final Shell parentShell)
  {
    super(parentShell);
    
    // retrieve the sample frequency
    final String freqO = CorePlugin.getToolParent().getProperty(OS_FREQ);
    if(freqO != null && freqO.length() > 0)
    {
      ownshipFreq = Long.valueOf(freqO);
    }
    
    String freq = CorePlugin.getToolParent().getProperty(TGT_FREQ);
    if(freq != null && freq.length() > 0)
    {
      thirdPartyFreq = Long.valueOf(freq);
    }

  }

  @Override
  public void create()
  {
    super.create();
    setTitle("Import NMEA Data");
    getButton(IDialogConstants.OK_ID).setText("Import");
    setTitleImage(CorePlugin.extendedGetImageFromRegistry("icons/48/NMEA.png"));
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
    {
      new Label(composite, SWT.NONE).setText("Ownship position frequency:");
      final ComboViewer comboViewer = new ComboViewer(composite);
      comboViewer.setContentProvider(new ArrayContentProvider());
      comboViewer.setLabelProvider(newLabelProvider());
      comboViewer.setInput(getDataSet());

      comboViewer.getCombo().setLayoutData(
          new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
      comboViewer.setSelection(new StructuredSelection(Long
          .valueOf(ownshipFreq)));
      comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {

        @Override
        public void selectionChanged(final SelectionChangedEvent event)
        {
          final IStructuredSelection selection =
              (IStructuredSelection) event.getSelection();
          if (selection.getFirstElement() instanceof Long)
          {
            ownshipFreq = (Long) selection.getFirstElement();
            
            // ok, remember this value
            CorePlugin.getToolParent().setProperty(OS_FREQ, "" + ownshipFreq);
          }
        }
      });
    }
    {
      new Label(composite, SWT.NONE).setText("AIS position frequency:");
      final ComboViewer comboViewer = new ComboViewer(composite);
      comboViewer.setContentProvider(new ArrayContentProvider());
      comboViewer.setLabelProvider(newLabelProvider());
      comboViewer.setInput(getDataSet());
      comboViewer.getCombo().setLayoutData(
          new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
      comboViewer.setSelection(new StructuredSelection(Long
          .valueOf(thirdPartyFreq)));
      comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
      {

        @Override
        public void selectionChanged(final SelectionChangedEvent event)
        {
          final IStructuredSelection selection =
              (IStructuredSelection) event.getSelection();
          if (selection.getFirstElement() instanceof Long)
          {
            thirdPartyFreq = (Long) selection.getFirstElement();
            
            // ok, remember this value
            CorePlugin.getToolParent().setProperty(TGT_FREQ, "" + thirdPartyFreq);
          }
        }
      });
    }
    return composite;
  }

  public long getOwnshipFreq()
  {
    return ownshipFreq;
  }

  public long getThirdPartyFreq()
  {
    return thirdPartyFreq;
  }

}
