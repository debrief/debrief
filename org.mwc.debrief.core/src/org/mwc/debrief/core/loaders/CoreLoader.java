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

/** parent class for text-file loaders
 * 
 * @author ian
 *
 */
public abstract class CoreLoader extends BaseLoader
{
  /** text description of file-type, used for logging
   * 
   */
  private final String _fileType;

  /**
   * 
   * @param fileType human readable description of file-type, for logging
   */
  public CoreLoader(final String fileType)
  {
    _fileType = fileType;
  }
  
  @Override
  public void loadFile(final IAdaptable target, final InputStream inputStream,
      final String fileName, final CompleteListener listener)
  {
    final IRunnableWithProgress runnable = getImporter(target, inputStream,
        fileName, listener);
    try
    {
      // hmm, is there anything in the file?
      final int numAvailable = inputStream.available();
      if (numAvailable > 0)
      {
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
      DebriefPlugin.logError(Status.ERROR, "Problem loading " + _fileType + ":"
          + fileName, e);
    }

    // ok, load the data...
    DebriefPlugin.logError(Status.INFO, "Successfully loaded " + _fileType + " file", null);
  
  }
  
  /** get the importer code
   * 
   * @return a runnable that will perform the import process
   */
  abstract protected IRunnableWithProgress getImporter(final IAdaptable target, final InputStream inputStream,
      final String fileName, final CompleteListener listener);

}
