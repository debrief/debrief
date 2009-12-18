/**
 * 
 */
package org.mwc.debrief.core.loaders;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.*;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;

import Debrief.ReaderWriter.XML.KML.ImportKML;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class KMLLoader extends IPlotLoader.BaseLoader
{


	public KMLLoader()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer.editors.CorePlotEditor,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void loadFile(final PlotEditor thePlot, final InputStream inputStream, final String fileName)
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

								ImportKML.doImport(theLayers, inputStream, fileName);

								DebriefPlugin.logError(Status.INFO,
										"completed loading:" + fileName, null);

								// and inform the plot editor
								thePlot.loadingComplete(this);

								DebriefPlugin.logError(Status.INFO, "parent plot informed", null);

							}
							catch (RuntimeException e)
							{
								e.printStackTrace();
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
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
			}
	//	}
		// ok, load the data...
		DebriefPlugin.logError(Status.INFO, "Successfully loaded XML file", null);
	}
}
