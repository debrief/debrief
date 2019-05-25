package org.mwc.debrief.core.loaders;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.Word.ImportRiderNarrativeDocument;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.TrackDataProvider;

public class MsDocLoader extends CoreLoader
{

  public MsDocLoader()
  {
    super(".doc", ".doc");
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers theLayers, final InputStream inputStream,
      final String fileName)
  {
    return new IRunnableWithProgress()
    {
      @Override
      public void run(final IProgressMonitor pm)
      {
        final TrackDataProvider trackData = target.getAdapter(
            TrackDataProvider.class);

        // ok. we'll pass it to the rider import. If that fails, we can offer it to the
        // plain importer
        final ImportRiderNarrativeDocument iw =
            new ImportRiderNarrativeDocument(theLayers, trackData);
        iw.handleImport(fileName, inputStream);

        // hey, it worked. now open the narrative viewer
        // hmm, just double-check we've got some narrative data
        final Layer narratives = theLayers.findLayer(
            NarrativeEntry.NARRATIVE_LAYER);
        if (narratives != null)
        {
          Display.getDefault().asyncExec(new Runnable()
          {

            @Override
            public void run()
            {
              try
              {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().showView(CorePlugin.BULK_NARRATIVE_VIEWER);
              }
              catch (final PartInitException e)
              {
                CorePlugin.logError(IStatus.ERROR,
                    "Failed opening narrative viewer", e);
                e.printStackTrace();
              }
            }
          });
        }
      }
    };
  }
}
