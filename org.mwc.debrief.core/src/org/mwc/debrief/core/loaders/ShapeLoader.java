
package org.mwc.debrief.core.loaders;

import java.io.File;
import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.ChartBoundsWrapper;

/**
 * @author ian.mayo
 */
public class ShapeLoader extends CoreLoader
{

  public ShapeLoader()
  {
    super("Shape (.shp)", "shp");
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers theLayers, final InputStream inputStream,
      final String fileName)
  {
    return new IRunnableWithProgress()
    {
      public void run(final IProgressMonitor pm)
      {
        // create a layer name from the filename
        File tmpFile = new File(fileName);
        String layerName = tmpFile.getName();

        // ok - get loading going
        final ExternallyManagedDataLayer dl = new ExternallyManagedDataLayer(
            ChartBoundsWrapper.SHAPEFILE_TYPE, layerName, fileName);
        theLayers.addThisLayer(dl);
      }
    };
  }
}
