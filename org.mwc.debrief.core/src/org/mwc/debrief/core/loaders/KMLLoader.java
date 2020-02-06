
package org.mwc.debrief.core.loaders;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

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
