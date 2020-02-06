
package org.mwc.debrief.core.loaders;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.debrief.core.gpx.ImportGPX;

import MWC.GUI.Layers;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 */
public class GPXLoader extends CoreLoader
{
  public GPXLoader()
  {
    super("GPX", "gpx");
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers layers, final InputStream inputStream, final String fileName)
  {
    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)
      {

          // ok - get loading going
          ImportGPX.doImport(layers, inputStream, fileName);

      }
    };
  }
}
