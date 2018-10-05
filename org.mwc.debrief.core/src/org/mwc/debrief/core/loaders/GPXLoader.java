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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.gpx.ImportGPX;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import MWC.GUI.Layers;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 */
public class GPXLoader extends CoreLoader
{
  public GPXLoader(String fileType)
  {
    super("GPX");
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target, final InputStream inputStream,
      final String fileName, final CompleteListener listener)
  {
    final Layers theLayers = (Layers) target.getAdapter(Layers.class);
    final IPlotLoader finalLoader = this;

    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)
      {
        // right, better suspend the LayerManager extended updates from
        // firing
        theLayers.suspendFiringExtended(true);

        try
        {
          // ok - get loading going

          // double check, is this a KMZ
          if (fileName.toLowerCase().endsWith(".gpx"))
          {
            // ok - get loading going
            ImportGPX.doImport(theLayers, inputStream, fileName);
          }

          // and inform the plot editor
          listener.complete(finalLoader);
        }
        catch (final RuntimeException e)
        {
          DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
              + fileName, e);
        }
        finally
        {
          // and inform the plot editor
          listener.complete(finalLoader);

          // ok, allow the layers object to inform anybody what's
          // happening
          // again
          theLayers.suspendFiringExtended(false);
        }
      }

    };
  }
}
