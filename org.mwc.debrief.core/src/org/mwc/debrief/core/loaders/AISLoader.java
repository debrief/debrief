
package org.mwc.debrief.core.loaders;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.ais.ImportAIS;
import MWC.GUI.Layers;

/**
 */
public class AISLoader extends CoreLoader
{

  public AISLoader()
  {
    super("AIS", null);
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
        try
        {
          // ok - get loading going
          ImportAIS importer = new ImportAIS(layers);
          importer.importThis(fileName, inputStream);
        }
        catch (final RuntimeException e)
        {
          DebriefPlugin.logError(Status.ERROR, "Problem loading AIS datafile:"
              + fileName, e);
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
