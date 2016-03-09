package org.mwc.debrief.core.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import Debrief.ReaderWriter.Word.ImportWord;
import MWC.GUI.Layers;

public class MsDocLoader extends IPlotLoader.BaseLoader
{

  @Override
  public void loadFile(final PlotEditor thePlot, final InputStream inputStream,
      final String fileName)
  {
    final Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);

    try
    {

      // hmm, is there anything in the file?
      final int numAvailable = inputStream.available();
      if (numAvailable > 0)
      {

        final IWorkbench wb = PlatformUI.getWorkbench();
        final IProgressService ps = wb.getProgressService();
        ps.busyCursorWhile(new IRunnableWithProgress()
        {
          public void run(final IProgressMonitor pm)
          {
            // right, better suspend the LayerManager extended updates from
            // firing
            theLayers.suspendFiringExtended(true);

            try
            {
              // ok, get reading
              if (fileName.endsWith(".doc"))
              {
                ImportWord iw = new ImportWord(theLayers);
                iw.importThis(fileName, inputStream);
              }

              // and inform the plot editor
              thePlot.loadingComplete(this);

            }
            catch (final RuntimeException e)
            {
              DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
                  + fileName, e);
            }
            finally
            {
              // ok, allow the layers object to inform anybody what's
              // happening
              // again
              theLayers.suspendFiringExtended(false);

              // and trigger an update ourselves
              // theLayers.fireExtended();
            }
          }
        });

      }

    }
    catch (final InvocationTargetException e)
    {
      DebriefPlugin.logError(Status.ERROR, "Problem loading MS Word document:"
          + fileName, e);
    }
    catch (final InterruptedException e)
    {
      DebriefPlugin.logError(Status.ERROR, "Problem loading shapefile:"
          + fileName, e);
    }
    catch (final IOException e)
    {
      DebriefPlugin.logError(Status.ERROR, "Problem loading shapefile:"
          + fileName, e);
    }
    finally
    {
    }
    // }
    // ok, load the data...
    DebriefPlugin.logError(Status.INFO, "Successfully loaded shapefile", null);
  }

}
