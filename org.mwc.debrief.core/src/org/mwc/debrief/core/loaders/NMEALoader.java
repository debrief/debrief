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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.cmap.core.wizards.ImportNMEADialog;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.NMEA.ImportNMEA;
import MWC.GUI.Layers;

/**
 */
public class NMEALoader extends CoreLoader
{

  public NMEALoader(String fileType)
  {
    super("NMEA", null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
   * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
   */
  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      Layers layers, final InputStream inputStream,
      final String fileName)
  {

    // ok, we'll need somewhere to put the data
    final Layers theLayers = (Layers) target.getAdapter(Layers.class);

    return new IRunnableWithProgress()
    {
      public void run(final IProgressMonitor pm)
      {

        try
        {
          final ImportNMEADialog dialog = new ImportNMEADialog();
          if (dialog.open() != Dialog.CANCEL)
          {
            // get the selected values
            final long osFreq = dialog.getOwnshipFreq();
            final long tgtFreq = dialog.getThirdPartyFreq();

            // ok - get loading going
            ImportNMEA importer = new ImportNMEA(theLayers);
            importer.importThis(fileName, inputStream, osFreq, tgtFreq);
          }
          else
          {
            DebriefPlugin.logError(Status.INFO, "User cancelled loading:"
                + fileName, null);
          }
        }
        catch (final Exception e)
        {
          DebriefPlugin.logError(Status.ERROR, "Problem loading AIS datafile:"
              + fileName, e);
        }
      }
    };
  }
}
