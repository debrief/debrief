/**
 * 
 */
package org.mwc.debrief.core.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllablePeriod;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.mwc.debrief.core.loaders.LoaderManager;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;
import org.mwc.debrief.core.operations.PlotOperations;
import org.osgi.framework.Bundle;

import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.PlainProjection.RelativeProjectionParent;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.IRollingNarrativeProvider;

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

	// private static final String EXTENSION_TAG_CLASS_ATTRIB = "class";

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.debrief.core";

	/**
	 * helper object which loads plugin file-loaders
	 */
	LoaderManager _loader;

	/**
	 * we keep the reference to our track-type adapter
	 */
	TrackDataProvider _trackDataProvider;

	/**
	 * something to look after our layer painters
	 */
	LayerPainterManager _layerPainterManager;

	/**
	 * and how we view the time
	 * 
	 */
	protected TimeControlPreferences _timePreferences;

	private PlotOperations _myOperations;

	/**
	 * support tool that provides a relative plot
	 */
	private RelativeProjectionParent _myRelativeWrapper;

	/**
	 * handle narrative management
	 */
	protected IRollingNarrativeProvider _theNarrativeProvider;

	/**
	 * an object to look after all of the time bits
	 */
	private TimeManager _timeManager;

	/**
	 * constructor - quite simple really.
	 */
	public PlotEditor()
	{
		super();

		// create the track manager to manage the primary & secondary tracks
		_trackDataProvider = new TrackManager(_myLayers);

		// and listen out form modifications, because we want to mark ourselves
		// as
		// dirty once they've updated
		_trackDataProvider.addTrackDataListener(new TrackDataListener()
		{
			public void tracksUpdated(WatchableList primary,
					WatchableList[] secondaries)
			{
				fireDirty();
			}
		});

		_layerPainterManager = new LayerPainterManager(_trackDataProvider);
		_layerPainterManager.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent arg0)
			{
				// ok, trigger repaint of plot
				if (getChart() != null)
					getChart().update();
			}
		});

		// create the time manager. cool
		_timeManager = new TimeManager();
		_timeManager.addListener(_timeListener,
				TimeProvider.TIME_CHANGED_PROPERTY_NAME);

		// and how time is managed
		_timePreferences = new TimeControlProperties();

		// listen out for when our input changes, since we will change the
		// editor
		// window title
		this.addPropertyListener(new IPropertyListener()
		{

			@SuppressWarnings("synthetic-access")
			public void propertyChanged(Object source, int propId)
			{
				if (propId == PROP_INPUT)
				{
					IFileEditorInput inp = (IFileEditorInput) getEditorInput();
					setPartName(inp.getName());
				}
			}
		});

		_myOperations = new PlotOperations()
		{
			// just provide with our complete set of layers
			@SuppressWarnings("synthetic-access")
			public Object[] getTargets()
			{
				// ok, return our top level layers as objects
				Vector<Layer> res = new Vector<Layer>(0, 1);
				for (int i = 0; i < _myLayers.size(); i++)
				{
					res.add(_myLayers.elementAt(i));
				}
				return res.toArray();
			}

			/**
			 * override performing the operation, since we'll do a screen update on
			 * completion
			 */
			@SuppressWarnings("synthetic-access")
			public Vector<Layer> performOperation(AnOperation operationName)
			{
				// make the actual change
				Vector<Layer> res = super.performOperation(operationName);

				if (res != null)
				{
					if (res.size() != 0)
					{
						for (Iterator<Layer> iter = res.iterator(); iter.hasNext();)
						{
							Layer thisL = (Layer) iter.next();
							// and update the screen
							_myLayers.fireReformatted(thisL);

						}
					}
				}

				return res;

			}
		};

		// do we have some time preferences?
		if (_timePreferences != null)
		{
			HiResDate startDTG = _timePreferences.getSliderStartTime();
			HiResDate endDTG = _timePreferences.getSliderEndTime();
			// and were there any times in it?
			if ((startDTG != null) && (endDTG != null))
			{
				// yup, store the time data.
				_myOperations
						.setPeriod(new TimePeriod.BaseTimePeriod(startDTG, endDTG));
			}
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();

		// stop listening to the time manager
		_timeManager.removeListener(_timeListener,
				TimeProvider.TIME_CHANGED_PROPERTY_NAME);

	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		setSite(site);
		setInputWithNotify(input);

		// ok - declare and load the supplemental plugins which can load
		// datafiles
		initialiseFileLoaders();

		// and start the load
		loadThisFile(input);

		// lastly, set the title (if we have one)
		this.setPartName(input.getName());
	}

	/**
	 * @param input
	 *          the file to insert
	 */
	private void loadThisFile(IEditorInput input)
	{
		try
		{
			InputStream is = null;
			IPersistableElement persist = input.getPersistable();
			if (input instanceof IFileEditorInput)
			{
				IFileEditorInput ife = (IFileEditorInput) input;
				IFile iff = ife.getFile();
				iff.refreshLocal(IResource.DEPTH_ONE, null);
				is = iff.getContents();
			}
			else if (persist instanceof IFileEditorInput)
			{
				IFileEditorInput iff = (IFileEditorInput) persist;
				is = iff.getFile().getContents();
			}
			else if (input instanceof FileStoreEditorInput)
			{
				FileStoreEditorInput _input = (FileStoreEditorInput) input;
				URI _uri = _input.getURI();
				Path _p = new Path(_uri.getPath());
				IFileStore _ifs = EFS.getLocalFileSystem().getStore(_p);
				is = _ifs.openInputStream(EFS.NONE, null);
			}

			if (is != null)
				loadThisStream(is, input.getName());
			else
			{
				CorePlugin.logError(Status.INFO, "Failed to load file from:" + input,
						null);
			}

		}
		catch (ResourceException e)
		{
			CorePlugin.logError(Status.ERROR,
					"Resource out of sync, REFRESH the workspace", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"File out of sync",
					"Please right-click on your navigator project and press Refresh");
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param input
	 *          the file to insert
	 */
	private void loadThisFile(String filePath)
	{
		try
		{
			FileInputStream ifs = new FileInputStream(filePath);
			loadThisStream(ifs, filePath);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadThisStream(InputStream is, String fileName)
	{
		// right, see if any of them will do our edit
		IPlotLoader[] loaders = _loader.findLoadersFor(fileName);
		// did we find any?
		if (loaders.length > 0)
		{
			// cool, give them a go...
			try
			{
				for (int i = 0; i < loaders.length; i++)
				{
					IPlotLoader thisLoader = loaders[i];

					// get it to load. Just in case it's an asychronous load
					// operation, we
					// rely on it calling us back (loadingComplete)
					thisLoader.loadFile(this, is, fileName);
				}
			}
			catch (RuntimeException e)
			{
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

		for (Enumeration<Editable> iter = theData.elements(); iter
				.hasMoreElements();)
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
				Enumeration<Editable> elements = thisLayer.elements();
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
		// have we received a date?
		if (date != null)
		{
			if (period == null)
			{
				period = new TimePeriod.BaseTimePeriod(date, date);
			}
			else
				period.extend(date);
		}

		return period;
	}

	/**
	 * method called when a helper object has completed a plot-load operation
	 * 
	 * @param source
	 */
	public void loadingComplete(Object source)
	{

		// ok, stop listening for dirty calls - since there will be so many and
		// we
		// don't want
		// to start off with a dirty plot
		startIgnoringDirtyCalls();

		DebriefPlugin.logError(Status.INFO, "File load received", null);

		// and update the time management bits
		TimePeriod timePeriod = getPeriodFor(_myLayers);

		if (timePeriod != null)
		{
			_timeManager.setPeriod(this, timePeriod);

			// also give it a current DTG (if it doesn't have one)
			if (_timeManager.getTime() == null)
				_timeManager.setTime(this, timePeriod.getStartDTG(), false);
		}

		// done - now we can process dirty calls again
		stopIgnoringDirtyCalls();

	}

	protected void filesDropped(String[] fileNames)
	{
		super.filesDropped(fileNames);

		// ok, iterate through the files
		for (int i = 0; i < fileNames.length; i++)
		{
			final String thisFilename = fileNames[i];
			loadThisFile(thisFilename);
		}

		// ok, we're probably done - fire the update
		this._myLayers.fireExtended();

		// and resize to make sure we're showing all the data
		this._myChart.rescale();

		// hmm, we may have loaded more track data - but we don't track
		// loading of individual tracks - just fire a "modified" flag
		_trackDataProvider.fireTracksChanged();

	}

	private static boolean _updatingPlot = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#timeChanged()
	 */
	protected void timeChanged(HiResDate newDTG)
	{
		super.timeChanged(newDTG);

		// just check we're ready for plotting.
		if (getChart() == null)
			return;

		if (_updatingPlot)
		{
			// skip the update - we're already at it
		}
		else
		{
			_updatingPlot = true;

			try
			{
				// note, we've learn't to use the default display instead of the
				// current one, we were get a null returned since this thread may not
				// have a display
				Display myDis = Display.getDefault();
				if (myDis != null)
				{
					myDis.asyncExec(new Runnable()
					{
						public void run()
						{
							// ok - update our painter
							getChart().getCanvas().updateMe();
						}
					});
				}

			}
			finally
			{
				_updatingPlot = false;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mwc.cmap.plotViewer.editors.CorePlotEditor#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == Layers.class)
		{
			if (_myLayers != null)
				res = _myLayers;
		}
		else if (adapter == TrackManager.class)
		{
			res = _trackDataProvider;
		}
		else if (adapter == TrackDataProvider.class)
		{
			res = _trackDataProvider;
		}
		else if (adapter == PlainProjection.class)
		{
			res = super.getChart().getCanvas().getProjection();
		}
		else if (adapter == LayerPainterManager.class)
		{
			res = _layerPainterManager;
		}
		else if (adapter == ControllablePeriod.class)
		{
			res = _myOperations;
		}
		else if (adapter == TimeControlPreferences.class)
		{
			res = _timePreferences;
		}
		else if (adapter == ControllableTime.class)
		{
			res = _timeManager;
		}
		else if (adapter == TimeProvider.class)
		{
			res = _timeManager;
		}
		else if (adapter == IRollingNarrativeProvider.class)
		{
			res = _theNarrativeProvider;
		}
		else if (adapter == IGotoMarker.class)
		{
			return new IGotoMarker()
			{
				public void gotoMarker(IMarker marker)
				{
					String lineNum = marker.getAttribute(IMarker.LINE_NUMBER, "na");
					if (lineNum != "na")
					{
						// right, convert to DTG
						HiResDate tNow = new HiResDate(0, Long.parseLong(lineNum));
						_timeManager.setTime(this, tNow, true);
					}
				}

			};
		}

		else if (adapter == IRollingNarrativeProvider.class)
		{
			// so, do we have any narrative data?
			Layer narr = _myLayers.findLayer(ImportReplay.NARRATIVE_LAYER);

			if (narr != null)
			{
				// did we find it?
				// cool, cast to object
				final NarrativeWrapper wrapper = (NarrativeWrapper) narr;

				res = wrapper;
			}
			else
			{
				// create an empty narrative warpper
				res = new NarrativeWrapper("Empty", null);
			}
		}
		else if (adapter == RelativeProjectionParent.class)
		{
			if (_myRelativeWrapper == null)
			{
				_myRelativeWrapper = new RelativeProjectionParent()
				{
					public double getHeading()
					{
						double res1 = 0.0;
						// do we have a primary?
						Watchable[] thePositions = _trackDataProvider.getPrimaryTrack()
								.getNearestTo(_timeManager.getTime());
						if (thePositions != null)
						{
							// yup, get the centre point
							res1 = thePositions[0].getCourse();
						}
						return res1;
					}

					public WorldLocation getLocation()
					{
						MWC.GenericData.WorldLocation res1 = null;
						// do we have a primary?
						Watchable[] thePositions = _trackDataProvider.getPrimaryTrack()
								.getNearestTo(_timeManager.getTime());
						if (thePositions != null)
						{
							if (thePositions.length > 0)
							{
								// yup, get the centre point
								res1 = thePositions[0].getBounds().getCentre();
							}
						}
						return res1;
					}

				};
			}
			res = _myRelativeWrapper;
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

	/**
	 * @param parent
	 */
	protected SWTChart createTheChart(Composite parent)
	{
		// TODO Auto-generated method stub
		SWTChart res = new SWTChart(_myLayers, parent)
		{

			public SWTCanvas createCanvas(Composite parent1)
			{
				return new CustomisedSWTCanvas(parent1)
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void parentFireSelectionChanged(ISelection selected)
					{
						chartFireSelectionChanged(selected);
					}

					public void doSupplementalRightClickProcessing(
							MenuManager menuManager, Plottable selected, Layer theParentLayer)
					{
						// hmm, is it a fix. if it is, also flash up the track
						if (selected instanceof FixWrapper)
						{
							// get the parent track
							FixWrapper fix = (FixWrapper) selected;
							TrackWrapper parent11 = fix.getTrackWrapper();
							RightClickSupport.getDropdownListFor(menuManager, new Editable[]
							{ parent11 }, new Layer[]
							{ theParentLayer }, new Layer[]
							{ theParentLayer }, getLayers(), true);
						}
					}
				};
			}

			public void chartFireSelectionChanged(ISelection sel)
			{
				// TODO Auto-generated method stub
				fireSelectionChanged(sel);
			};

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @param thisLayer
			 * @param dest
			 */
			protected void paintThisLayer(Layer thisLayer, CanvasType dest)
			{
				try
				{
					// get the current time
					HiResDate tNow = _timeManager.getTime();

					// do we know the time?
					// if (tNow != null)
					if (true)
					{
						// yes. cool, get plotting
						_layerPainterManager.getCurrentPainter().paintThisLayer(thisLayer,
								dest, tNow);

						// ok, now sort out the highlight

						// right, what are the watchables
						final Vector<Plottable> watchables = SnailPainter
								.getWatchables(thisLayer);

						// cycle through them
						final Enumeration<Plottable> watches = watchables.elements();
						while (watches.hasMoreElements())
						{
							final WatchableList list = (WatchableList) watches.nextElement();
							// is the primary an instance of layer (with it's
							// own line
							// thickness?)
							if (list instanceof Layer)
							{
								final Layer ly = (Layer) list;
								int thickness = ly.getLineThickness();
								dest.setLineWidth(thickness);
							}

							// ok, clear the nearest items
							Watchable[] wList = list.getNearestTo(tNow);
							Watchable watch = null;
							if (wList.length > 0)
								watch = wList[0];

							if (watch != null)
							{
								// aah, is this the primary?
								boolean isPrimary = (list == _trackDataProvider
										.getPrimaryTrack());

								// plot it
								_layerPainterManager.getCurrentHighlighter().highlightIt(
										dest.getProjection(), dest, list, watch, isPrimary);
							}
						}
					}
				}
				catch (Exception e)
				{
					CorePlugin
							.logError(Status.ERROR, "Whilst repainting:" + thisLayer, e);
				}
			}

		};
		return res;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		IEditorInput input = getEditorInput();
		String ext = null;

		// do we have an input
		if (input.exists())
		{
			// get the file suffix
			if (input instanceof IFileEditorInput)
			{
				IFile file = null;
				file = ((IFileEditorInput) getEditorInput()).getFile();
				IPath path = file.getFullPath();
				ext = path.getFileExtension();
			}
			else if (input instanceof FileStoreEditorInput)
			{
				FileStoreEditorInput fi = (FileStoreEditorInput) input;
				URI uri = fi.getURI();
				Path path = new Path(uri.getPath());
				ext = path.getFileExtension();
			}

			// right, have a look at it.
			if (ext == null || ext.equalsIgnoreCase("rep"))
			{
				// not, we have to do a save-as
				doSaveAs();
			}
			else
			{

				OutputStream tmpOS = null;

				try
				{
					// NEW STRATEGY. Save to tmp first, then overwrite existing on
					// success.

					// 1. create the temp file
					File tmpFile = File.createTempFile("DebNG_tmp", ".xml");
					tmpFile.createNewFile();
					tmpFile.deleteOnExit();

					// 1a. record the name of the tmp file in the log
					String filePath = tmpFile.getAbsolutePath();
					CorePlugin.logError(Status.INFO, "Created temp save file at:"
							+ filePath, null);

					// 2. open the file as a stream
					tmpOS = new FileOutputStream(tmpFile);

					// 3. save to this stream
					doSaveTo(tmpOS, monitor);

					tmpOS.close();
					tmpOS = null;

					// 4. overwrite the existing file with the saved file
					// - note we will only reach this point if the save succeeded.
					if (tmpOS == null)
					{
						// sort out where we're saving to
						if (input instanceof IFileEditorInput)
						{
							IFile file = ((IFileEditorInput) getEditorInput()).getFile();

							// get the current path (since we're going to be moving the temp
							// one to it
							IPath thePath = file.getLocation();

							// create a backup path
							IPath bakPath = file.getFullPath().addFileExtension("bak");

							// delete any existing backup file
							File existingBackupFile = new File(file.getLocation()
									.addFileExtension("bak").toOSString());
							if (existingBackupFile.exists())
							{
								CorePlugin.logError(Status.INFO,
										"Existing back file still there, having to delete"
												+ existingBackupFile.getAbsolutePath(), null);
								existingBackupFile.delete();
							}

							// now rename the existing file as the backup
							file.move(bakPath, true, monitor);

							// move the temp file to be our real working file
							tmpFile.renameTo(thePath.toFile().getAbsoluteFile());

							// finally, delete the backup file
							if (existingBackupFile.exists())
							{
								CorePlugin.logError(Status.INFO,
										"Save operation completed successfully, deleting backup file"
												+ existingBackupFile.getAbsolutePath(), null);
								existingBackupFile.delete();
							}

							// throw in a refresh - since we've done the save outside Eclipse
							file.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);

						}
						else if (input instanceof FileStoreEditorInput)
						{
							// get the data-file
							FileStoreEditorInput fi = (FileStoreEditorInput) input;
							URI _uri = fi.getURI();
							Path _p = new Path(_uri.getPath());

							// create pointers to the existing file, and the backup file
							IFileStore existingFile = EFS.getLocalFileSystem().getStore(_p);
							IFileStore backupFile = EFS.getLocalFileSystem().getStore(
									_p.addFileExtension("bak"));

							// delete any existing backup file
							IFileInfo backupStatus = backupFile.fetchInfo();
							if (backupStatus.exists())
							{
								CorePlugin.logError(Status.INFO,
										"Existing back file still there, having to delete"
												+ backupFile.toURI().getRawPath(), null);
								backupFile.delete(EFS.NONE, monitor);
							}

							// now rename the existing file as the backup
							existingFile.move(backupFile, EFS.OVERWRITE, monitor);

							// and rename the temp file as the working file
							tmpFile.renameTo(existingFile.toLocalFile(EFS.NONE, monitor));

							if (backupStatus.exists())
							{
								CorePlugin.logError(Status.INFO,
										"Save operation successful, deleting backup file"
												+ backupFile.toURI().getRawPath(), null);
								backupFile.delete(EFS.NONE, monitor);
							}

						}
					}
				}
				catch (CoreException e)
				{
					CorePlugin.logError(Status.ERROR,
							"Failed whilst saving external file", e);
				}
				catch (FileNotFoundException e)
				{
					CorePlugin.logError(Status.ERROR,
							"Failed to find local file to save to", e);
				}
				catch (Exception e)
				{
					CorePlugin.logError(Status.ERROR, "Unknown file-save error occurred",
							e);

				}
				finally
				{
					try
					{
						if (tmpOS != null)
							tmpOS.close();
					}
					catch (IOException e)
					{
						CorePlugin.logError(Status.ERROR, "Whilst performing save", e);
					}
				}
			}
		}
	}

	/**
	 * save our plot to the indicated location
	 * 
	 * @param destination
	 *          where to save plot to
	 * @param monitor
	 *          somebody/something to be informed about progress
	 */
	private void doSaveTo(OutputStream os, IProgressMonitor monitor)
	{
		if (os != null)
		{

			IProduct prod = Platform.getProduct();
			Bundle bund = prod.getDefiningBundle();
			String version = "" + new Date(bund.getLastModified());

			// ok, now write to the file
			DebriefEclipseXMLReaderWriter.exportThis(this, os, version);

			// ok, lastly indicate that the save worked (if it did!)
			_plotIsDirty = false;
			firePropertyChange(PROP_DIRTY);
		}
		else
		{
			DebriefPlugin.logError(Status.ERROR,
					"Unable to identify source file for plot", null);
		}

	}

	public void doSaveAs()
	{
		String message = "Save as";
		SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
		dialog.setTitle("Save Plot As");
		if (getEditorInput() instanceof FileEditorInput)
		{
			IFile oldFile = ((FileEditorInput) getEditorInput()).getFile();
			// dialog.setOriginalFile(oldFile);

			IPath oldPath = oldFile.getFullPath();
			IPath newStart = oldPath.removeFileExtension();
			IPath newPath = newStart.addFileExtension("xml");
			File asFile = newPath.toFile();
			String newName = asFile.getName();
			// dialog.setOriginalFile(newName);
			dialog.setOriginalName(newName);
		}
		dialog.create();
		if (message != null)
			dialog.setMessage(message, IMessageProvider.WARNING);
		else
			dialog.setMessage("Save file to another location.");
		dialog.open();
		IPath path = dialog.getResult();

		if (path == null)
		{
			return;
		}
		else
		{
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (!file.exists())
				try
				{
					System.out.println("creating:" + file.getName());
					file.create(new ByteArrayInputStream(new byte[]
					{}), false, null);
				}
				catch (CoreException e)
				{
					DebriefPlugin.logError(IStatus.ERROR,
							"Failed trying to create new file for save-as", e);
					return;
				}

			OutputStream os = null;
			try
			{
				os = new FileOutputStream(file.getLocation().toFile(), false);
				// ok, write to the file
				doSaveTo(os, new NullProgressMonitor());

				// also make this new file our input
				IFileEditorInput newInput = new FileEditorInput(file);
				setInputWithNotify(newInput);
			}
			catch (FileNotFoundException e)
			{
				CorePlugin
						.logError(Status.ERROR, "Failed whilst performing Save As", e);
			}
			finally
			{
				// and close it
				try
				{
					if (os != null)
						os.close();
				}
				catch (IOException e)
				{
					CorePlugin.logError(Status.ERROR, "Whilst performaing save-as", e);
				}

			}

		}

		_plotIsDirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * 
	 */
	@Override
	protected void layersExtended()
	{
		// inform our parent
		super.layersExtended();

		// and tell the track data manager that something's happened. One of
		// it's
		// tracks may have been
		// deleted!
		_trackDataProvider.fireTracksChanged();
	}

	public boolean isSaveAsAllowed()
	{
		return true;
	}
}
