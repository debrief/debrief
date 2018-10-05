package org.mwc.debrief.core.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.interfaces.IPlotLoader.BaseLoader;

import MWC.GUI.Layers;

/**
 * parent class for text-file loaders
 * 
 * @author ian
 *
 */
public abstract class CoreLoader extends BaseLoader
{
  /**
   * text description of file-type, used for logging
   * 
   */
  protected final String _fileType;

  /**
   * (optional) file suffix. we test against it, if present
   * 
   */
  private final String _suffix;

  /**
   * 
   * @param fileType
   *          human readable description of file-type, for logging
   * @param suffix
   *          (optional) file suffix to test file-name against
   */
  public CoreLoader(final String fileType, String suffix)
  {
    _fileType = fileType;
    _suffix = suffix;
  }

  @Override
  public void loadFile(final IAdaptable target, final InputStream inputStream,
      final String fileName, final CompleteListener listener)
  {

    // ok, get reading
    if (_suffix == null || fileName.toLowerCase().endsWith(_suffix))
    {
      final Layers layers = (Layers) target.getAdapter(Layers.class);
      try
      {
        final IRunnableWithProgress runnable = getImporter(target, layers,
            inputStream, fileName);
        // hmm, is there anything in the file?
        final int numAvailable = inputStream.available();
        if (numAvailable > 0)
        {
          layers.suspendFiringExtended(false);

          final IWorkbench wb = PlatformUI.getWorkbench();
          final IProgressService ps = wb.getProgressService();
          ps.busyCursorWhile(runnable);
        }
      }
      catch (final InvocationTargetException e)
      {
        DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
            + fileName, e);
      }
      catch (final InterruptedException e)
      {
        DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
            + fileName, e);
      }
      catch (final IOException e)
      {
        DebriefPlugin.logError(Status.ERROR, "Problem loading " + _fileType
            + ":" + fileName, e);
      }
      catch (Exception e)
      {
        DebriefPlugin.logError(Status.ERROR, "Problem loading " + _fileType
            + ":" + fileName, e);
      }
      finally
      {
        listener.complete(this);
        layers.suspendFiringExtended(false);
      }

      // ok, load the data...
      DebriefPlugin.logError(Status.INFO, "Successfully loaded " + _fileType
          + " file", null);
    }
    else
    {
      // ok, load the data...
      DebriefPlugin.logError(Status.WARNING, "Not loading " + _fileType
          + ", suffix doesn't match " + _suffix, null);

    }
  }

  /**
   * get the importer code
   * 
   * @param layers
   *          TODO
   * @return a runnable that will perform the import process
   * @throws Exception
   */
  abstract protected IRunnableWithProgress getImporter(final IAdaptable target,
      Layers layers, final InputStream inputStream, final String fileName)
      throws Exception;

}
