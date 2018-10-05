package org.mwc.debrief.core.loaders;

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument;
import MWC.GUI.Layers;

public class PdfLoader extends CoreLoader
{

  public PdfLoader(String fileType)
  {
    super("pdf");
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final InputStream inputStream, final String fileName,
      final CompleteListener listener)
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
          // ok, get reading
          if (fileName.toLowerCase().endsWith(".pdf"))
          {
            ImportNarrativeDocument iw = new ImportNarrativeDocument(theLayers);                
            ArrayList<String> strings = iw.importFromPdf(fileName, inputStream);
            iw.processThese(strings);
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
          // ok, allow the layers object to inform anybody what's
          // happening
          // again
          theLayers.suspendFiringExtended(false);

          // and trigger an update ourselves
          // theLayers.fireExtended();
        }
      }
    };
  }

}
