/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.loaders;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.wizards.ImportBRTDialog;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.BRT.BRTImporter;
import MWC.GUI.Layers;

public class BRTLoader extends CoreLoader
{

  public BRTLoader()
  {
    super("BRT", ".brt");
  }

  @Override
  protected IRunnableWithProgress getImporter(IAdaptable target, Layers layers,
      final InputStream inputStream, final String fileName) throws Exception
  {
    final Layers theLayers = (Layers) target.getAdapter(Layers.class);

    return new IRunnableWithProgress()
    {
      public void run(final IProgressMonitor pm)
      {
        try
        {
          final BRTImporter importer = new BRTImporter(theLayers);
          final ImportBRTDialog wizard = new ImportBRTDialog(importer
              .findTrack(), importer.getTracks());


          final WizardDialog dialog = new WizardDialog(null, wizard);
          Display.getDefault().syncExec(new Runnable() {
            public void run() {
              

              dialog.create();
              dialog.open();
              }
          });
          if (dialog.getReturnCode() == WizardDialog.OK)
          {
            importer.importThis(wizard, fileName, inputStream);
          }
          else
          {
            DebriefPlugin.logError(Status.INFO, "User cancelled loading:"
                + fileName, null);
          }
        }
        catch (final Exception e)
        {
          DebriefPlugin.logError(Status.ERROR, "Problem loading BRT datafile:"
              + fileName, e);
        }
      }
    };
  }

}
