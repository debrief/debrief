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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class LogTrackLoader extends IPlotLoader.BaseLoader
{


	public LogTrackLoader()
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

								// quick check, is this a .log file
								if(fileName.endsWith(".log"))
								{
									// ok, go for it.
									importThis(theLayers, fileName, inputStream);
//									ImportKML.doZipImport(theLayers, inputStream, fileName);
								}
								
								DebriefPlugin.logError(Status.INFO,
										"completed loading:" + fileName, null);

								// and inform the plot editor
								thePlot.loadingComplete(this);

								DebriefPlugin.logError(Status.INFO, "parent plot informed", null);

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
							}
						}
					});

				}

			}
			catch (final InvocationTargetException e)
			{
				DebriefPlugin.logError(Status.ERROR, "Problem loading log:"
						+ fileName, e);
			}
			catch (final InterruptedException e)
			{
				DebriefPlugin.logError(Status.ERROR, "Problem loading log:"
						+ fileName, e);
			}
			catch (final IOException e)
			{
				DebriefPlugin.logError(Status.ERROR, "Problem loading log:"
						+ fileName, e);
			}
			finally
			{
			}
		// ok, load the data...
		DebriefPlugin.logError(Status.INFO, "Successfully loaded .LOG file", null);
	}
	

  /** import data from this stream
   * @param theLayers 
   */
  public final void importThis(final Layers theLayers, final String fName,
                         final java.io.InputStream is){
    // declare linecounter
    @SuppressWarnings("unused")
		int lineCounter = 0;

    TrackWrapper tw = null;
    
    final Reader reader = new InputStreamReader(is);
    final BufferedReader br = new BufferedReader(reader);
    String thisLine=null;
  
      // check stream is valid
      try
			{
				if(is.available() > 0){

					// ok, we know we have a header line, so skip it.
				  @SuppressWarnings("unused")
					String ingoreMe = br.readLine();
				  
				  // ok, now the first real line
				  thisLine = br.readLine();

				  final long start = System.currentTimeMillis();

				  // loop through the lines
				  while(thisLine != null){

				    // keep line counter (use it for error reporting)
				    lineCounter ++;

				    // catch import problems
				    FixWrapper fw = readLine(thisLine);
				    
				    if(fw != null)
				    {
				    	// ok, add the fix.
				    	if(tw == null)
				    	{
				    		// ok, create the track
				    	}
				    	
				    	// now add the fix
				    	tw.addFix(fw);
				    }

				    // read another line
				    thisLine = br.readLine();
				  }

				  final long end = System.currentTimeMillis();
				  System.out.print(" |Elapsed:" + (end - start) + " ");

				}
			}
      
			catch (IOException fe)
			{
				DebriefPlugin
				.logError(
						Status.INFO,
						"Trouble creating input stream for " + fName,
						fe);
			}
  }

	private FixWrapper readLine(String thisLine)
	{
		// TODO parse the line
		return null;
	}
	
}
