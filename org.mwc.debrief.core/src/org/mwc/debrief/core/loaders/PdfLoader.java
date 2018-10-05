package org.mwc.debrief.core.loaders;

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument;
import MWC.GUI.Layers;

public class PdfLoader extends CoreLoader
{

  public PdfLoader(String fileType)
  {
    super("pdf", "pdf");
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
        ImportNarrativeDocument iw = new ImportNarrativeDocument(layers);
        ArrayList<String> strings = iw.importFromPdf(fileName, inputStream);
        iw.processThese(strings);
      }
    };
  }

}
