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

import Debrief.ReaderWriter.XML.KML.ImportKML;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class KMLLoader extends CoreLoader
{

  public KMLLoader()
  {
    super("KML", null);
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
        if (fileName.toLowerCase().endsWith(".kmz"))
        {
          // ok - get loading going
          ImportKML.doZipImport(layers, inputStream, fileName);

        }
        else if (fileName.toLowerCase().endsWith(".kml"))
        {
          // ok - get loading going
          ImportKML.doImport(layers, inputStream, fileName);
        }
      }
    };
  }
}
