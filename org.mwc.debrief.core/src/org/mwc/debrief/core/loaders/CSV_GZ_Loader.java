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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.XML.csv_gz.Import_CSV_GZ;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class CSV_GZ_Loader extends CoreLoader
{

  public CSV_GZ_Loader()
  {
    super("Compressed C-Log", ".gz");
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers layers, final InputStream inputStream, final String fileName)
  {
    return new IRunnableWithProgress()
    {
      public void run(final IProgressMonitor pm)
      {
        // quick check, is this a KMZ
        if (fileName.toLowerCase().endsWith("csv.gz"))
        {
          // get a logger
          ErrorLogger logger = CorePlugin.getToolParent();
          // ok - get loading going
          try
          {
            new Import_CSV_GZ().doZipImport(layers, inputStream, fileName, logger);
          }
          catch(RuntimeException re)
          {
            CorePlugin.showMessage("Import CSV.GZ", "Failed to load file:" + re
                .getMessage());
          }
        }
      }
    };
  }
}
