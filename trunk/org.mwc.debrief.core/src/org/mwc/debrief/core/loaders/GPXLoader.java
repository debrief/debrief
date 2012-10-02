/**
 * 
 */
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
import org.mwc.debrief.core.gpx.ImportGPX;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import MWC.GUI.Layers;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 */
public class GPXLoader extends IPlotLoader.BaseLoader
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
	 * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void loadFile(final PlotEditor thePlot, final InputStream inputStream,
			final String fileName)
	{

		final Layers theLayers = (Layers) thePlot.getAdapter(Layers.class);

		try
		{
			// hmm, is there anything in the file?
			int numAvailable = inputStream.available();
			if (numAvailable > 0)
			{

				IWorkbench wb = PlatformUI.getWorkbench();
				IProgressService ps = wb.getProgressService();
				ps.busyCursorWhile(new IRunnableWithProgress()
				{
					@Override
					public void run(IProgressMonitor pm)
					{
						// right, better suspend the LayerManager extended updates from
						// firing
						theLayers.suspendFiringExtended(true);

						try
						{
							DebriefPlugin.logError(Status.INFO, "about to start loading:"
									+ fileName, null);

							// ok - get loading going

							// double check, is this a KMZ
							if (fileName.endsWith(".gpx"))
							{
								// ok - get loading going
								ImportGPX.doImport(theLayers, inputStream, fileName);
							}

							DebriefPlugin.logError(Status.INFO, "completed loading:"
									+ fileName, null);

							// and inform the plot editor
							thePlot.loadingComplete(this);

							DebriefPlugin.logError(Status.INFO, "parent plot informed", null);

							DebriefPlugin.logError(Status.INFO, "completed loading:"
									+ fileName, null);

							DebriefPlugin.logError(Status.INFO, "parent plot informed", null);

						}
						catch (RuntimeException e)
						{
							DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
									+ fileName, e);
						}
						finally
						{
							// and inform the plot editor
							thePlot.loadingComplete(this);

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
		catch (InvocationTargetException e)
		{
			DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
					+ fileName, e);
		}
		catch (InterruptedException e)
		{
			DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
					+ fileName, e);
		}
		catch (IOException e)
		{
			DebriefPlugin
					.logError(Status.ERROR, "Problem loading GPX:" + fileName, e);
		}

		// ok, load the data...
		DebriefPlugin.logError(Status.INFO, "Successfully loaded GPX file", null);
	}
}
