/**
 * 
 */
package org.mwc.debrief.core.editors;

import java.awt.Dimension;
import java.io.File;
import java.util.Enumeration;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.debrief.core.CorePlugin;
import org.mwc.debrief.core.editors.painters.PlainHighlighter;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.mwc.debrief.core.loaders.LoaderManager;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Tools.Tote.*;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Layers.DataListener;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;

/**
 * @author ian.mayo
 */
public class PlotEditor extends org.mwc.cmap.plotViewer.editors.CorePlotEditor
{
	// Extension point tag and attributes in plugin.xml
	private static final String EXTENSION_POINT_ID = "DebriefPlotLoader";

	private static final String EXTENSION_TAG = "loader";

	private static final String EXTENSION_TAG_LABEL_ATTRIB = "name";

	private static final String EXTENSION_TAG_EXTENSIONS_ATTRIB = "extensions";

	private static final String EXTENSION_TAG_ICON_ATTRIB = "icon";

	private static final String EXTENSION_TAG_CLASS_ATTRIB = "class";

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.debrief.core";

	private PlainHighlighter _myTimeHighlighter;

	/**
	 * helper object which loads plugin file-loaders
	 */
	private LoaderManager _loader;

	/** we keep the reference to our track-type adapter
	 * 
	 */
	private TrackDataProvider _trackDataProvider;

	/**
	 * constructor - quite simple really.
	 */
	public PlotEditor()
	{
		super();

		_myLayers = new Layers();

		_myLayers.addDataExtendedListener(new DataListener()
		{

			public void dataModified(Layers theData, Layer changedLayer)
			{
			}

			public void dataExtended(Layers theData)
			{
				layersExtended();
			}

			public void dataReformatted(Layers theData, Layer changedLayer)
			{
			}

		});

	}

	/**
	 * new data has been added - have a look at the times
	 */
	private void layersExtended()
	{

	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		// TODO Auto-generated method stub
		setSite(site);
		setInput(input);

		// ok - declare and load the supplemental plugins which can load datafiles
		initialiseFileLoaders();

		// and start the load
		loadThisFile(input);

		// lastly, set the title (if we have one)
		this.setPartName(input.getName());
		this.setContentDescription("Includes imported Replay data");

	}

	/**
	 * @param input
	 *          the file to insert
	 */
	private void loadThisFile(IEditorInput input)
	{
		// right, see if any of them will do our edit
		IPlotLoader[] loaders = _loader.findLoadersFor(input.getName());
		// did we find any?
		if (loaders.length > 0)
		{
			// cool, give them a go...
			try
			{
				for (int i = 0; i < loaders.length; i++)
				{
					IPlotLoader thisLoader = loaders[i];

					// get it to load. Just in case it's an asychronous load operation, we
					// rely on it calling us back (loadingComplete)
					thisLoader.loadFile(this, input);
				}
			} catch (RuntimeException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	private void initialiseFileLoaders()
	{
		// hey - sort out our plot readers
		_loader = new LoaderManager(EXTENSION_POINT_ID, EXTENSION_TAG, PLUGIN_ID)
		{

			public INamedItem createInstance(IConfigurationElement configElement,
					String label)
			{
				// get the attributes
				label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);
				String icon = configElement.getAttribute(EXTENSION_TAG_ICON_ATTRIB);
				String fileTypes = configElement
						.getAttribute(EXTENSION_TAG_EXTENSIONS_ATTRIB);

				// create the instance
				INamedItem res = new IPlotLoader.DeferredPlotLoader(configElement,
						label, icon, fileTypes);

				// and return it.
				return res;
			}

		};
	}

	private static TimePeriod getPeriodFor(Layers theData)
	{
		TimePeriod res = null;

		for (Enumeration iter = theData.elements(); iter.hasMoreElements();)
		{
			Layer thisLayer = (Layer) iter.nextElement();

			// and through this layer
			if (thisLayer instanceof TrackWrapper)
			{
				TrackWrapper thisT = (TrackWrapper) thisLayer;
				res = extend(res, thisT.getStartDTG());
				res = extend(res, thisT.getEndDTG());
			}
			else if (thisLayer instanceof BaseLayer)
			{
				Enumeration elements = thisLayer.elements();
				while (elements.hasMoreElements())
				{
					Plottable nextP = (Plottable) elements.nextElement();
					if (nextP instanceof Watchable)
					{
						Watchable wrapped = (Watchable) nextP;
						HiResDate dtg = wrapped.getTime();
						if (dtg != null)
							res = extend(res, dtg);
					}
				}
			}
		}

		return res;
	}

	private static TimePeriod extend(TimePeriod period, HiResDate date)
	{
		if (period == null)
		{
			period = new TimePeriod.BaseTimePeriod(date, date);
		}
		else
			period.extend(date);

		return period;
	}

	/**
	 * method called when a helper object has completed a plot-load operation
	 * 
	 * @param source
	 */
	public void loadingComplete(Object source)
	{
		CorePlugin.logError(Status.INFO, "File load received", null);

		// and update the time management bits
		TimePeriod timePeriod = getPeriodFor(_myLayers);

		if (timePeriod != null)
		{
			super._timeManager.setPeriod(this, timePeriod);

			// also give it a current DTG
			super._timeManager.setTime(this, timePeriod.getStartDTG());
		}

		// so, do we have any narrative data?
		Layer narr = _myLayers.findLayer(ImportReplay.NARRATIVE_LAYER);

		// did we find it?
		if (narr != null)
		{
			// cool, cast to object
			final NarrativeWrapper wrapper = (NarrativeWrapper) narr;

			// and put it into our narrative provider
			_theNarrativeProvider = new NarrativeProvider()
			{
				public NarrativeWrapper getNarrative()
				{
					return wrapper;
				}
			};
		}

	}

	protected void filesDropped(String[] fileNames)
	{
		super.filesDropped(fileNames);

		// ok, iterate through the files
		for (int i = 0; i < fileNames.length; i++)
		{
			final String thisFilename = fileNames[i];
			File thisFile = new File(thisFilename);
			// org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage localF
			// = new
			// org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage(thisFile);
			// IFile theFile = (IFile) localF.getAdapter(IFile.class);
			// FileEditorInput theInput = new FileEditorInput(theFile);
			// this.loadThisFile(theInput);

		}

		// ok, get loading.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#timeChanged()
	 */
	protected void timeChanged(HiResDate newDTG)
	{
		// TODO Auto-generated method stub
		super.timeChanged(newDTG);

		// ok - update our painter
		if (getChart() != null)
		{
			getChart().getCanvas().updateMe();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == TrackDataProvider.class)
		{
			if (_trackDataProvider == null)
			{
				_trackDataProvider = new TrackDataProvider()
				{

					public WatchableList[] getSecondaryTracks()
					{
						// TODO Auto-generated method stub
						TrackWrapper sec = (TrackWrapper) _myLayers.findLayer("Tomato");
						WatchableList[] res = null;
						if (sec != null)
							res = new WatchableList[] { sec };
						return res;
					}

					public WatchableList getPrimaryTrack()
					{
						TrackWrapper pri = (TrackWrapper) _myLayers.findLayer("Carpet");
						return pri;
					}

					public void addTrackDataListener(TrackDataListener listener)
					{
						// TODO Auto-generated method stub
					}
				};
			}

			res = _trackDataProvider;
		}

		// did we find anything?
		if (res == null)
		{
			// nope, don't bother.
			res = super.getAdapter(adapter);
		}

		// ok, done
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		// TODO Auto-generated method stub
		super.createPartControl(parent);

		super.getChart().getCanvas().addPainter(new CanvasType.PaintListener()
		{

			public void paintMe(CanvasType dest)
			{
				// ok - get the highlighter to draw itself
				PlainHighlighter.update(_timeManager.getTime(), _myLayers, dest);
			}

			public WorldArea getDataArea()
			{
				// TODO Auto-generated method stub
				return null;
			}

			public void resizedEvent(PlainProjection theProj, Dimension newScreenArea)
			{
				// TODO Auto-generated method stub

			}

			public String getName()
			{
				// TODO Auto-generated method stub
				return null;
			}
		});

	}

}
