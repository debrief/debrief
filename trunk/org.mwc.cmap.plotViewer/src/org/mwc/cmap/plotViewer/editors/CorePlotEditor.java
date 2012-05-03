package org.mwc.cmap.plotViewer.editors;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.SubActionBars2;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.cmap.core.interfaces.IPlotGUI;
import org.mwc.cmap.core.interfaces.IResourceProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.gt2plot.data.GeoToolsLayer;
import org.mwc.cmap.gt2plot.data.ShapeFileLayer;
import org.mwc.cmap.gt2plot.data.WorldImageLayer;
import org.mwc.cmap.gt2plot.proj.GtProjection;
import org.mwc.cmap.plotViewer.PlotViewerPlugin;
import org.mwc.cmap.plotViewer.actions.ExportWMF;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;
import org.mwc.cmap.plotViewer.editors.chart.CursorTracker;
import org.mwc.cmap.plotViewer.editors.chart.RangeTracker;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.GeoToolsHandler;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener2;
import MWC.GUI.Plottable;
import MWC.GUI.Tools.Chart.DblClickEdit;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;

public abstract class CorePlotEditor extends EditorPart implements
		IResourceProvider, IControllableViewport, ISelectionProvider, IPlotGUI,
		IChartBasedEditor
{

	// //////////////////////////////
	// member data
	// //////////////////////////////

	/**
	 * the chart we store/manager
	 */
	protected SWTChart _myChart = null;

	/**
	 * we may learn the background color of the canvas before it has loaded.
	 * temporarily store the color here, and set the background color when we load
	 * the canvas
	 */
	private Color _pendingCanvasBackgroundColor;

	/**
	 * the graphic data we know about
	 */
	protected Layers _myLayers;

	/**
	 * the object which listens to time-change events. we remember it so that it
	 * can be deleted when we close
	 */
	protected PropertyChangeListener _timeListener;

	/**
	 * store a pending projection. we do this because sometimes we may learn about
	 * the projection before we create our child components, you see.
	 */
	protected PlainProjection _pendingProjection;

	// drag-drop bits

	protected DropTarget target;

	Vector<ISelectionChangedListener> _selectionListeners;

	ISelection _currentSelection;

	/**
	 * keep track of whether the current plot is dirty...
	 */
	public boolean _plotIsDirty = false;

	// ///////////////////////////////////////////////
	// dummy bits applicable for our dummy interface
	// ///////////////////////////////////////////////
	Button _myButton;

	Label _myLabel;

	protected DataListener2 _listenForMods;

	private boolean _ignoreDirtyCalls;

	protected PartMonitor _myPartMonitor;

	protected GeoToolsHandler _myGeoHandler;

	// //////////////////////////////
	// constructor
	// //////////////////////////////

	public CorePlotEditor()
	{
		super();
		
		// create the projection, we're going to need it to load the data, before we have the chart created
		_myGeoHandler = new GtProjection();

		_myLayers = new Layers(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void addThisLayer(Layer theLayer)
			{
				Layer wrappedLayer = null;

				// ok, if this is an externally managed layer (and we're doing
				// GT-plotting, we will wrap it, and actually add the wrapped layer
				if (theLayer instanceof ExternallyManagedDataLayer)
				{			  
				  ExternallyManagedDataLayer dl = (ExternallyManagedDataLayer) theLayer;
					if (dl.getDataType().equals(
							MWC.GUI.Shapes.ChartBoundsWrapper.WORLDIMAGE_TYPE))
					{
						GeoToolsLayer gt = new WorldImageLayer(dl.getName(),
								dl.getFilename());
						gt.setVisible(dl.getVisible());
						_myGeoHandler.addGeoToolsLayer(gt);
						wrappedLayer = gt;
					}
					else if (dl.getDataType().equals(
							MWC.GUI.Shapes.ChartBoundsWrapper.SHAPEFILE_TYPE))
					{
						// just see if it's a raster extent layer (special processing)
						if (dl.getName().equals(WorldImageLayer.RASTER_FILE))
						{
							// special processing - wrap it.
							wrappedLayer = WorldImageLayer.RasterExtentHelper.loadRasters(
									dl.getFilename(), this);
						}
						else
						{
							// ok, it's a normal shapefile: load it.
							GeoToolsLayer gt = new ShapeFileLayer(dl.getName(),
									dl.getFilename());
							gt.setVisible(dl.getVisible());
							_myGeoHandler.addGeoToolsLayer(gt);
							wrappedLayer = gt;
						}
					}
					if (wrappedLayer != null)
						super.addThisLayer(wrappedLayer);
				}
				else
					super.addThisLayer(theLayer);
			}

			@Override
			public void removeThisLayer(Layer theLayer)
			{
				if (theLayer instanceof GeoToolsLayer)
				{
					// get the content
					GtProjection gp = (GtProjection) _myChart.getCanvas().getProjection();
					GeoToolsLayer gt = (GeoToolsLayer) theLayer;
					gt.clearMap();

					if(gp.numLayers() == 0)
					{
						// ok - we've got to force the data rea
						WorldArea area = _myChart.getCanvas().getProjection().getDataArea();
						_myChart.getCanvas().getProjection().setDataArea(area);
					}

				}

				// and remove from the actual list
				super.removeThisLayer(theLayer);

			}

		};

		_listenForMods = new DataListener2()
		{

			public void dataModified(Layers theData, Layer changedLayer)
			{
				fireDirty();
			}

			public void dataExtended(Layers theData)
			{
				layersExtended();
				fireDirty();
			}

			public void dataReformatted(Layers theData, Layer changedLayer)
			{
				fireDirty();
			}

			@Override
			public void dataExtended(Layers theData, Plottable newItem, Layer parent)
			{
				layersExtended();
				fireDirty();
			}

		};
		_myLayers.addDataExtendedListener(_listenForMods);
		_myLayers.addDataModifiedListener(_listenForMods);
		_myLayers.addDataReformattedListener(_listenForMods);

		// and listen for new times
		_timeListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent arg0)
			{

				// right, retrieve the time
				HiResDate newDTG = (HiResDate) arg0.getNewValue();
				timeChanged(newDTG);

				// now make a note that the current DTG has changed
				fireDirty();
			}
		};

	}

	public void dispose()
	{
		// ok, tell the chart to self-destruct (And dispose/release of any objects)
		_myChart.close();
		_myChart = null;
		
		super.dispose();

		// empty the part monitor
		if (_myPartMonitor != null)
		{
			_myPartMonitor.ditch();
			_myPartMonitor = null;
		}
		
		// and the layers
		_myLayers.close();
		_myLayers = null;
		
		// some other items
		_timeListener = null;
	}

	public void createPartControl(Composite parent)
	{
		// hey, create the chart
		_myChart = createTheChart(parent);

		// set the chart color, if we have one
		if (_pendingCanvasBackgroundColor != null)
		{
			_myChart.getCanvas().setBackgroundColor(_pendingCanvasBackgroundColor);
			// and promptly forget it
			_pendingCanvasBackgroundColor = null;
		}

		// and update the projection, if we have one
		if (_pendingProjection != null)
		{
			_myChart.getCanvas().setProjection(_pendingProjection);
			_pendingProjection = null;
		}

		// and the drop support
		configureFileDropSupport();

		// and add our dbl click listener
		// and add our dbl click listener
		getChart().addCursorDblClickedListener(new DblClickEdit(null)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void addEditor(Plottable res, EditorType e, Layer parentLayer)
			{
				selectPlottable(res, parentLayer);
			}

			protected void handleItemNotFound(PlainProjection projection)
			{
				putBackdropIntoProperties();
			}
		});

		getSite().setSelectionProvider(this);

		// and over-ride the undo button
		IAction undoAction = new UndoActionHandler(getEditorSite(),
				CorePlugin.CMAP_CONTEXT);
		IAction redoAction = new RedoActionHandler(getEditorSite(),
				CorePlugin.CMAP_CONTEXT);

		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.UNDO.getId(), undoAction);
		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REDO.getId(), redoAction);

		// put in the plot-copy support

		IAction _copyClipboardAction = new Action()
		{
			public void runWithEvent(Event event)
			{
				ExportWMF ew = new ExportWMF(true, false);
				ew.run(null);
			}
		};

		IActionBars actionBars = getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				_copyClipboardAction);

		// listen out for us losing focus - so we can drop the selection
		listenForMeLosingFocus();

		// listen out for us gaining focus - so we can set the cursort tracker
		listenForMeGainingFocus();
	}

	private void listenForMeLosingFocus()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(CorePlotEditor.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						boolean isMe = checkIfImTheSameAs(instance);
						if (isMe)
							_currentSelection = null;
					}
				});
	}

	private void listenForMeGainingFocus()
	{
		final EditorPart linkToMe = this;
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(CorePlotEditor.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						if (type == PartMonitor.ACTIVATED)
						{
							boolean isMe = checkIfImTheSameAs(instance);
							if (isMe)
							{
								// tell the cursor track that we're it's bitch.
								RangeTracker.displayResultsIn(linkToMe);
								CursorTracker.trackThisChart(_myChart, linkToMe);
							}
						}
					}
				});
	}

	boolean checkIfImTheSameAs(Object target1)
	{
		boolean res = false;
		// is it me?
		if (target1 == this)
			res = true;
		else
		{
			res = false;
		}
		return res;
	}

	/**
	 * ok - let somebody else select an item on the plot. The initial reason for
	 * making this public was so that when a new item is created, we can select it
	 * on the plot. The plot then fires a 'selected' event, and the new item is
	 * shown in the properties window. Cool.
	 * 
	 * @param target1
	 *          - the item to select
	 * @param parentLayer
	 *          - the item's parent layer. Used to decide which layers to update.
	 */
	public void selectPlottable(Plottable target1, Layer parentLayer)
	{
		CorePlugin.logError(Status.INFO,
				"Double-click processed, opening property editor for:" + target1, null);
		EditableWrapper parentP = new EditableWrapper(parentLayer, null, getChart()
				.getLayers());
		EditableWrapper wrapped = new EditableWrapper(target1, parentP, getChart()
				.getLayers());
		ISelection selected = new StructuredSelection(wrapped);
		fireSelectionChanged(selected);
	}

	/**
	 * place the chart in the properties window
	 * 
	 */
	final void putBackdropIntoProperties()
	{
		SWTCanvas can = (SWTCanvas) getChart().getCanvas();
		EditableWrapper wrapped = new EditableWrapper(can, getChart().getLayers());
		ISelection sel = new StructuredSelection(wrapped);
		fireSelectionChanged(sel);

	}

	/**
	 * create the chart we're after
	 * 
	 * @param parent
	 *          the parent object to stick it into
	 */
	protected SWTChart createTheChart(Composite parent)
	{
		SWTChart res = new SWTChart(_myLayers, parent, _myGeoHandler)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void chartFireSelectionChanged(ISelection sel)
			{
				fireSelectionChanged(sel);
			}
		};
		return res;
	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport()
	{
		int dropOperation = DND.DROP_COPY;
		Transfer[] dropTypes =
		{ FileTransfer.getInstance() };

		target = new DropTarget(_myChart.getCanvasControl(), dropOperation);
		target.setTransfer(dropTypes);
		target.addDropListener(new DropTargetListener()
		{
			public void dragEnter(DropTargetEvent event)
			{
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					if (event.detail != DND.DROP_COPY)
					{
						event.detail = DND.DROP_COPY;
					}
				}
			}

			public void dragLeave(DropTargetEvent event)
			{
			}

			public void dragOperationChanged(DropTargetEvent event)
			{
			}

			public void dragOver(DropTargetEvent event)
			{
			}

			public void dropAccept(DropTargetEvent event)
			{
			}

			public void drop(DropTargetEvent event)
			{
				String[] fileNames = null;
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					fileNames = (String[]) event.data;
				}
				if (fileNames != null)
				{
					filesDropped(fileNames);
				}
			}

		});

	}

	/**
	 * process the files dropped onto this panel
	 * 
	 * @param fileNames
	 *          list of filenames
	 */
	protected void filesDropped(String[] fileNames)
	{
		System.out.println("Files dropped");
	}

	public void setFocus()
	{
		// just put some kind of blank object into the properties window
		// putBackdropIntoProperties();

		// ok, set the drag mode to whatever our common "mode" is.
		// - start off by getting the current mode
		PlotMouseDragger curMode = PlotViewerPlugin.getCurrentMode();

		// has one been set?
		if (curMode != null)
		{
			// yup, better observe it then
			_myChart.setDragMode(curMode);
		}

	}

	@SuppressWarnings(
	{ "rawtypes" })
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		// so, is he looking for the layers?
		if (adapter == CorePlotEditor.class)
		{
			res = this;
		}
		else if (adapter == ISelectionProvider.class)
		{
			res = this;
		}
		else if (adapter == IControllableViewport.class)
		{
			res = this;
		}
		else if (adapter == CanvasType.class)
		{
			res = _myChart.getCanvas();
		}

		return res;
	}

	protected void timeChanged(HiResDate newDTG)
	{
	}

	/**
	 * return the file representing where this plot is stored
	 * 
	 * @return the file location
	 */
	public IResource getResource()
	{
		// have we been saved yet?
		return null;
	}

	public WorldArea getViewport()
	{
		return getChart().getCanvas().getProjection().getDataArea();
	}

	public void setViewport(WorldArea target)
	{
		getChart().getCanvas().getProjection().setDataArea(target);
	}

	public PlainProjection getProjection()
	{
		return getChart().getCanvas().getProjection();
	}

	public void setProjection(PlainProjection proj)
	{
		// do we have a chart yet?
		if (_myChart == null)
		{
			// nope, better remember it
			_pendingProjection = proj;
		}
		else
		{
			// yes, just update it.
			_myChart.getCanvas().setProjection(proj);
		}

	}

	public SWTChart getChart()
	{
		return _myChart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	/**
	 * Returns the ActionbarContributor for the Editor.
	 * 
	 * @return the ActionbarContributor for the Editor.
	 */
	public SubActionBars2 getActionbar()
	{
		return (SubActionBars2) getEditorSite().getActionBars();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		return _currentSelection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface
	 * .viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{
		_currentSelection = selection;
	}

	public void fireSelectionChanged(ISelection sel)
	{
		// just double-check that we're not already processing this
		if (sel != _currentSelection)
		{
			_currentSelection = sel;
			if (_selectionListeners != null)
			{
				SelectionChangedEvent sEvent = new SelectionChangedEvent(this, sel);
				for (Iterator<ISelectionChangedListener> stepper = _selectionListeners
						.iterator(); stepper.hasNext();)
				{
					ISelectionChangedListener thisL = stepper.next();
					if (thisL != null)
					{
						thisL.selectionChanged(sEvent);
					}
				}
			}
		}
	}

	/**
	 * hmm, are we dirty?
	 * 
	 * @return
	 */
	public boolean isDirty()
	{
		return _plotIsDirty;
	}

	/**
	 * make a note that the data is now dirty, and needs saving.
	 */
	public void fireDirty()
	{
		if (!_ignoreDirtyCalls)
		{
			_plotIsDirty = true;
			Display.getDefault().asyncExec(new Runnable()
			{

				@SuppressWarnings("synthetic-access")
				public void run()
				{
					firePropertyChange(PROP_DIRTY);
				}
			});
		}
	}

	/**
	 * new data has been added - have a look at the times
	 */
	protected void layersExtended()
	{

	}

	/**
	 * start ignoring dirty calls, since we're loading the initial data (for
	 * instance)
	 */
	public void startIgnoringDirtyCalls()
	{
		_ignoreDirtyCalls = true;
	}

	/**
	 * start ignoring dirty calls, since we're loading the initial data (for
	 * instance)
	 */
	public void stopIgnoringDirtyCalls()
	{
		_ignoreDirtyCalls = false;
	}

	/**
	 * @return
	 */
	public Color getBackgroundColor()
	{
		return _myChart.getCanvas().getBackgroundColor();
	}

	/**
	 * @param theColor
	 */
	public void setBackgroundColor(Color theColor)
	{
		if (_myChart == null)
			_pendingCanvasBackgroundColor = theColor;
		else
			_myChart.getCanvas().setBackgroundColor(theColor);
	}

	public void update()
	{
		_myChart.update();
	}

	/**
	 * get the chart to fit to window
	 * 
	 */
	public void rescale()
	{
		_myChart.rescale();
	}
}
