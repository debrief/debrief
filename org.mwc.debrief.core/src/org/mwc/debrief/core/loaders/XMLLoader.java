/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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

import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class XMLLoader extends IPlotLoader.BaseLoader
{

	/**
	 * the static object we use for data-file load/open
	 */
	private static DebriefEclipseXMLReaderWriter _myReader;

	public XMLLoader()
	{
		if (_myReader == null)
		{
			_myReader = new DebriefEclipseXMLReaderWriter();
		}
	}

	/**
	 * load the data-file
	 * 
	 * @param destination
	 * @param source
	 * @param fileName
	 */
	public void doTheLoad(final Layers destination, final InputStream source,
			final String fileName, final IControllableViewport view, final PlotEditor plot)
	{
		_myReader.importThis(fileName, source, destination, view, plot);
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
								DebriefPlugin.logError(Status.INFO, "about to start loading:"
										+ fileName, null);

								// ok - get loading going

								doTheLoad(theLayers, inputStream, fileName, thePlot, thePlot);

								DebriefPlugin.logError(Status.INFO,
										"completed loading:" + fileName, null);


								DebriefPlugin.logError(Status.INFO, "parent plot informed", null);

							}
							catch (final RuntimeException e)
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
				DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:"
						+ fileName, e);
			}
			finally
			{
			}
	//	}
		// ok, load the data...
		DebriefPlugin.logError(Status.INFO, "Successfully loaded XML file", null);
	}
}
