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
package org.mwc.debrief.core.loaders;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.wizards.ImportNMEADialog;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.FlatFile.OTH_Helper;
import Debrief.ReaderWriter.FlatFile.OTH_Helper_Headless;
import Debrief.ReaderWriter.FlatFile.OTH_Importer;
import MWC.GUI.Layers;

/**
 */
public class OTH_Gold_Loader extends CoreLoader
{

  public OTH_Gold_Loader()
  {
    super("OTH Gold", ".txt");
  }

  @Override
  public boolean canLoad(String fileName)
  {
    boolean res = false;

    if (super.canLoad(fileName))
    {
      res = OTH_Importer.canLoad(fileName, CorePlugin.getToolParent());
    }
    return res;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
   * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
   */
  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers layers, final InputStream inputStream, final String fileName)
  {
    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)
      {
        // create way of passing reference back from dialog
        final AtomicReference<ImportNMEADialog> dialogO =
            new AtomicReference<ImportNMEADialog>();
        Display.getDefault().syncExec(new Runnable()
        {
          @Override
          public void run()
          {
            final ImportNMEADialog dialog = new ImportNMEADialog();
            if (dialog.open() != Window.CANCEL)
            {
              dialogO.set(dialog);
            }
          }
        });
        try
        {
          // did user press finish?
          if (dialogO.get() != null)
          {
            @SuppressWarnings("unused")
            final ImportNMEADialog dialog = dialogO.get();
            // get the selected values

            OTH_Importer importer = new OTH_Importer();

            // TODO: replace with "real" dialog
            OTH_Helper brtHelper = new OTH_Helper_Headless(true);

            // ok - get loading going
            importer.importThis(brtHelper, inputStream, layers);
          }
          else
          {
            DebriefPlugin.logError(IStatus.INFO, "User cancelled loading:"
                + fileName, null);
          }
        }
        catch (final Exception e)
        {
          DebriefPlugin.logError(IStatus.ERROR, "Problem loading AIS datafile:"
              + fileName, e);
        }
      }
    };
  }
}
