package org.mwc.debrief.core.loaders;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import Debrief.ReaderWriter.Word.ImportRiderNarrativeDocument;
import MWC.GUI.Layers;
import MWC.TacticalData.TrackDataProvider;

public class MsDocXLoader extends CoreLoader
{

  public MsDocXLoader(String fileType)
  {
    super(".docx", ".docx");
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
        final TrackDataProvider trackData = (TrackDataProvider) target
            .getAdapter(TrackDataProvider.class);

        // ok. we'll pass it to the rider import. If that fails, we can offer it to the
        // plain importer
        ImportRiderNarrativeDocument iw = new ImportRiderNarrativeDocument(
            theLayers, trackData);
        iw.handleImportX(fileName, inputStream);
      }
    };
  }
}
