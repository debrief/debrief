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
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import MWC.GUI.Layers;

/**
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 */
public class GPXLoader extends IPlotLoader.BaseLoader
{

	public static void main(String a[])
	{
		GPXLoader l = new GPXLoader();
		l.doTheLoad(null, null, null, null, null);
	}

	public GPXLoader()
	{
	}

	/**
	 * load the data-file
	 * 
	 * @param destination
	 * @param source
	 * @param fileName
	 */
	public void doTheLoad(Layers destination, InputStream source, String fileName, IControllableViewport view, PlotEditor plot)
	{

		/*
		 * External meta-data sample
		 * 
		 * Map<String, Object> props = new HashMap<String, Object>();
		 * List<InputStream> bindings = new ArrayList<InputStream>();
		 * bindings.add(this
		 * .getClass().getResourceAsStream("Debrief.Wrappers.bindings.xml"));
		 * bindings
		 * .add(this.getClass().getResourceAsStream("MWC.GUI.bindings.xml"));
		 * props.put(JAXBContextProperties.OXM_METADATA_SOURCE, bindings);
		 * 
		 * try { jaxbContext =
		 * JAXBContext.newInstance("Debrief.Wrappers:Debrief.Wrappers.Track",
		 * TrackWrapper.class.getClassLoader(), props);
		 * 
		 * Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		 * TrackWrapper tw = (TrackWrapper)
		 * unmarshaller.unmarshal(this.getClass().getResourceAsStream
		 * ("gpx-data.xml")); System.out.println(tw.getName());
		 * 
		 * TrackWrapper mtw = new TrackWrapper(); mtw.setName("ttt");
		 * 
		 * jaxbContext.createMarshaller().marshal(mtw, System.out); } catch
		 * (JAXBException e) { e.printStackTrace(); }
		 */

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
	 * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
	 */
	@Override
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
					@Override
					public void run(IProgressMonitor pm)
					{
						// right, better suspend the LayerManager extended updates from
						// firing
						theLayers.suspendFiringExtended(true);

						try
						{
							DebriefPlugin.logError(Status.INFO, "about to start loading:" + fileName, null);

							// ok - get loading going

							doTheLoad(theLayers, inputStream, fileName, thePlot, thePlot);

							DebriefPlugin.logError(Status.INFO, "completed loading:" + fileName, null);

							DebriefPlugin.logError(Status.INFO, "parent plot informed", null);

						}
						catch (RuntimeException e)
						{
							DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:" + fileName, e);
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
			DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:" + fileName, e);
		}
		catch (InterruptedException e)
		{
			DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:" + fileName, e);
		}
		catch (IOException e)
		{
			DebriefPlugin.logError(Status.ERROR, "Problem loading datafile:" + fileName, e);
		}
		finally
		{
		}
		// }
		// ok, load the data...
		DebriefPlugin.logError(Status.INFO, "Successfully loaded GPX file", null);
	}
}
