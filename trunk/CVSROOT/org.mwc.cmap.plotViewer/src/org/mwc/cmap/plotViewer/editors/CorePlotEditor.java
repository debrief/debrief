package org.mwc.cmap.plotViewer.editors;

import java.beans.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.SubActionBars2;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.operations.*;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeProvider;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.interfaces.*;
import org.mwc.cmap.core.property_support.PlottableWrapper;
import org.mwc.cmap.core.ui_support.LineItem;
import org.mwc.cmap.plotViewer.editors.chart.*;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.*;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.Tools.Chart.DblClickEdit;
import MWC.GenericData.*;

public abstract class CorePlotEditor extends EditorPart implements
		IResourceProvider, IControllableViewport, ISelectionProvider
{

	// //////////////////////////////
	// member data
	// //////////////////////////////

	/**
	 * the chart we store/manager
	 */
	protected SWTChart _myChart = null;

	/**
	 * the graphic data we know about
	 */
	final protected Layers _myLayers;

	/**
	 * handle narrative management
	 */
	protected NarrativeProvider _theNarrativeProvider;

	/**
	 * an object to look after all of the time bits
	 */
	protected TimeManager _timeManager;

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

	Vector _selectionListeners;

	ISelection _currentSelection;

	/**
	 * keep track of whether the current plot is dirty...
	 */
	protected boolean _plotIsDirty = false;

	/**
	 * whether to ignore firing dirty events for the time being (such as when
	 * we're loading data)
	 */
	protected boolean _ignoreDirtyCalls = false;

	// ///////////////////////////////////////////////
	// dummy bits applicable for our dummy interface
	// ///////////////////////////////////////////////
	Button _myButton;

	Label _myLabel;

	private CursorTracker _myTracker;

	// //////////////////////////////
	// constructor
	// //////////////////////////////

	public CorePlotEditor()
	{
		super();

		_myLayers = new Layers();

		DataListener listenForMods = new DataListener()
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

		};
		_myLayers.addDataExtendedListener(listenForMods);
		_myLayers.addDataModifiedListener(listenForMods);
		_myLayers.addDataReformattedListener(listenForMods);

		// create the time manager. cool
		_timeManager = new TimeManager();

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

		_timeManager.addListener(_timeListener,
				TimeProvider.TIME_CHANGED_PROPERTY_NAME);
	}

	public void dispose()
	{
		super.dispose();

		// stop listening to the time manager
		_timeManager.removeListener(_timeListener,
				TimeProvider.TIME_CHANGED_PROPERTY_NAME);
	}

	public void createPartControl(Composite parent)
	{
		// hey, create the chart
		_myChart = createTheChart(parent);

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
		getChart().addCursorDblClickedListener(new DblClickEdit(_myLayers, null)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void addEditor(Plottable res, EditorType e, Layer parentLayer)
			{
				System.out.println("opening editor for:" + res);
				PlottableWrapper parentP = new PlottableWrapper(parentLayer, null,
						getChart().getLayers());
				PlottableWrapper wrapped = new PlottableWrapper(res, parentP,
						getChart().getLayers());
				ISelection selected = new StructuredSelection(wrapped);
				fireSelectionChanged(selected);
			}

			protected void handleItemNotFound(PlainProjection projection)
			{
			}
		});

		getSite().setSelectionProvider(this);

		LineItem lineItem = CorePlugin.getStatusLine(this);
		_myTracker = new CursorTracker(_myChart, lineItem);

		//		
		// Display.getDefault().asyncExec(new Runnable()
		// {
		// public void run()
		// {
		// IStatusLineManager mgr = getActionbar().getStatusLineManager();
		// CursorTracker.LineItem item = new CursorTracker.LineItem("vv aa");
		// mgr.add(item);
		// mgr.update(true);
		// item.update("bbb ccc");
		// mgr.update(true);
		// }
		// });

		// and over-ride the undo button
		IAction undoAction = new UndoActionHandler(getEditorSite(), CorePlugin.CMAP_CONTEXT);
		IAction redoAction = new RedoActionHandler(getEditorSite(), CorePlugin.CMAP_CONTEXT);


		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.UNDO.getId(), undoAction);
		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REDO.getId(), redoAction);
	}

	/** create the chart we're after
	 * @param parent the parent object to stick it into
	 */
	protected SWTChart createTheChart(Composite parent)
	{
		SWTChart res  = new SWTChart(_myLayers, parent);
		return res;
	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport()
	{
		int dropOperation = DND.DROP_COPY;
		Transfer[] dropTypes = { FileTransfer.getInstance() };

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
		// TODO Auto-generated method stub

	}

	public Object getAdapter(Class adapter)
	{
		Object res = null;

		// so, is he looking for the layers?
		if (adapter == Layers.class)
		{
			if (_myLayers != null)
				res = _myLayers;
		}
		else if (adapter == NarrativeProvider.class)
		{
			return _theNarrativeProvider;
		}
		else if (adapter == TimeProvider.class)
		{
			return _timeManager;
		}
		else if (adapter == ISelectionProvider.class)
		{
			return this;
		}
		else if (adapter == ControllableTime.class)
		{
			return _timeManager;
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
						_timeManager.setTime(this, tNow);
					}
				}

			};
		}

		return res;
	}

	protected void timeChanged(HiResDate newDTG)
	{
	}

	/**
	 * method called when a helper object has completed a plot-load operation
	 * 
	 * @param source
	 */
	abstract public void loadingComplete(Object source);

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
		// TODO Auto-generated method stub
		return null;
	}

	public void setViewport(WorldArea target)
	{
		// TODO Auto-generated method stub

	}

	public PlainProjection getProjection()
	{
		// TODO Auto-generated method stub
		return null;
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
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector(0, 1);

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
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{
		// TODO Auto-generated method stub
		_currentSelection = selection;
	}

	protected void fireSelectionChanged(ISelection sel)
	{
		_currentSelection = sel;
		if (_selectionListeners != null)
		{
			SelectionChangedEvent sEvent = new SelectionChangedEvent(this, sel);
			for (Iterator stepper = _selectionListeners.iterator(); stepper.hasNext();)
			{
				ISelectionChangedListener thisL = (ISelectionChangedListener) stepper
						.next();
				if (thisL != null)
				{
					thisL.selectionChanged(sEvent);
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
	protected void fireDirty()
	{
		if (!_ignoreDirtyCalls)
		{
			_plotIsDirty = true;
			firePropertyChange(PROP_DIRTY);
		}
	}

	/**
	 * new data has been added - have a look at the times
	 */
	private void layersExtended()
	{

	}

	/**
	 * start ignoring dirty calls, since we're loading the initial data (for
	 * instance)
	 */
	protected void startIgnoringDirtyCalls()
	{
		_ignoreDirtyCalls = true;
	}

	/**
	 * start ignoring dirty calls, since we're loading the initial data (for
	 * instance)
	 */
	protected void stopIgnoringDirtyCalls()
	{
		_ignoreDirtyCalls = false;
	}

}
