package org.mwc.cmap.grideditor;

import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.DebriefProperty;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.grideditor.table.actons.GridEditorActionGroup;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptorExtension;
import org.mwc.cmap.gridharness.data.GriddableSeries;
import org.mwc.cmap.gridharness.views.WorldLocationHelper;

import MWC.GUI.Editable;
import MWC.GUI.Griddable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Editable.EditorType;
import MWC.GenericData.WorldLocation;

public class GridEditorView extends ViewPart
{

	/**
	 * class that makes one of our tactical wrapper objects look like a griddable
	 * series We aren't aaplying this functionality directly to the wrapper data
	 * objects, since it introduces Eclipse-related objects
	 * 
	 * @author Ian Mayo
	 * 
	 */
	public static class GriddableWrapper implements GriddableSeries
	{
		/**
		 * the thing we're wrapping
		 * 
		 */
		final EditableWrapper _item;

		private boolean _onlyVisItems = false;

		private static EventStack myStack = new EventStack(50);

		/**
		 * a cached set of attributes - we have to get them quite frequently
		 * 
		 */
		private GriddableItemDescriptor[] _myAttributes;

		public boolean isOnlyShowVisibleItems()
		{
			return _onlyVisItems;
		}

		public void setOnlyShowVisibleItems(boolean onlyVisItems)
		{
			_onlyVisItems = onlyVisItems;
		}

		public GriddableWrapper(EditableWrapper item)
		{
			_item = item;
		}

		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener)
		{
			if (_item.getEditable() instanceof SupportsPropertyListeners)
			{
				SupportsPropertyListeners pw = (SupportsPropertyListeners) _item
						.getEditable();
				pw.addPropertyChangeListener(listener);
			}
		}

		@Override
		public void deleteItem(TimeStampedDataItem subject)
		{
			GriddableSeriesMarker gs = (GriddableSeriesMarker) _item.getEditable();
			gs.removeElement((Editable) subject);

			// tell everybody something's changed
			fireExtended(PROPERTY_DELETED, subject);
		}

		/**
		 * broadcast the fact that something in this layer has changed
		 * 
		 * @param propertyName
		 * @param subject
		 * 
		 */
		public void fireExtended(String propertyName, TimeStampedDataItem subject)
		{
			// start off with a plain modified event (to inform ourselves)
			SupportsPropertyListeners pw = (SupportsPropertyListeners) _item
					.getEditable();
			// ok, inform any listeners
			pw.firePropertyChange(propertyName, null, subject);

			// now tell the world.
			_item.getLayers().fireExtended(null, _item.getTopLevelLayer());
		}

		@Override
		public void fireModified(TimeStampedDataItem subject)
		{
			if (_item.getEditable() instanceof SupportsPropertyListeners)
			{
				SupportsPropertyListeners pw = (SupportsPropertyListeners) _item
						.getEditable();
				// ok, inform any listeners
				pw.firePropertyChange(GriddableSeries.PROPERTY_CHANGED, null, subject);
			}
			else
				CorePlugin.logError(IStatus.ERROR,
						"Item in grid doesn't let us watch it's properties", null);

			// also tell the layers object that we've changed
			cachedFireModified(_item.getLayers(), _item.getTopLevelLayer());
			// _item.getLayers().fireModified(_item.getTopLevelLayer());
		}

		private void cachedFireModified(final Layers layers, final Layer topLayer)
		{
			// create the runnable
			Runnable runme = new Runnable()
			{
				public void run()
				{
					layers.fireModified(topLayer);
				}
			};

			// add it to the cache
			myStack.addEvent(runme);

		}

		@Override
		public GriddableItemDescriptor[] getAttributes()
		{
			if (_myAttributes == null)
			{
				Vector<GriddableItemDescriptor> items = new Vector<GriddableItemDescriptor>();
				GriddableSeriesMarker series = (GriddableSeriesMarker) _item
						.getEditable();

				Editable sampleItem = series.getSampleGriddable();
				EditorType info = sampleItem.getInfo();
				if (info instanceof Griddable)
				{
					IPropertyDescriptor[] props = _item.getGriddablePropertyDescriptors();

					// wrap them
					for (int i = 0; i < props.length; i++)
					{
						DebriefProperty desc = (DebriefProperty) props[i];

						Object dataObject = desc.getRawValue();
						Class<?> dataClass = dataObject.getClass();
						GriddableItemDescriptor gd;

						// aah, is this a 'special' class?
						if (dataClass == WorldLocation.class)
						{
							WorldLocationHelper worldLocationHelper = new WorldLocationHelper();
							WorldLocation sample = new WorldLocation(1, 1, 1);
							String sampleLocationText = worldLocationHelper.getLabelFor(
									sample).getText(sample);

							gd = new GriddableItemDescriptorExtension("Location", "Location",
									WorldLocation.class, new WorldLocationHelper(),
									sampleLocationText);
						}
						else
						{
							gd = new GriddableItemDescriptor(desc.getDisplayName(), desc
									.getDisplayName(), dataClass, desc.getHelper());
						}

						items.add(gd);
					}
				}

				if (items.size() > 0)
				{
					_myAttributes = items.toArray(new GriddableItemDescriptor[]
					{ null });
				}
			}
			return _myAttributes;
		}

		@Override
		public List<TimeStampedDataItem> getItems()
		{
			List<TimeStampedDataItem> list = null;
			Editable obj = _item.getEditable();
			if (obj instanceof Layer)
			{
				list = new Vector<TimeStampedDataItem>();
				Layer layer = (Layer) obj;
				Enumeration<Editable> enumer = layer.elements();
				while (enumer.hasMoreElements())
				{
					Editable ed = enumer.nextElement();

					if (_onlyVisItems)
					{
						// right, this should be a plottable - just check
						if (ed instanceof Plottable)
						{
							Plottable pl = (Plottable) ed;
							if (pl.getVisible())
							{
								list.add(0, (TimeStampedDataItem) ed);
							}
						}
					}
					else
					{
						// just show all of them
						list.add(0, (TimeStampedDataItem) ed);
					}
				}
			}
			return list;
		}

		@Override
		public String getName()
		{
			return _item.getEditable().getName();
		}

		/**
		 * return the thing being edited
		 * 
		 * @return
		 */
		public EditableWrapper getWrapper()
		{
			return _item;
		}

		@Override
		public void insertItem(TimeStampedDataItem subject)
		{
			GriddableSeriesMarker gs = (GriddableSeriesMarker) _item.getEditable();
			gs.add((Editable) subject);

			// tell everybody something's changed
			fireExtended(GriddableSeries.PROPERTY_ADDED, subject);
		}

		@Override
		public void insertItemAt(TimeStampedDataItem subject, int index)
		{
			// we don't need to worry about the order of the item,
			// since they're chronologically ordered anyway
			insertItem(subject);
		}

		@Override
		public TimeStampedDataItem makeCopy(TimeStampedDataItem item)
		{
			GriddableSeriesMarker gs = (GriddableSeriesMarker) _item.getEditable();
			return gs.makeCopy(item);
		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener listener)
		{
			if (_item.getEditable() instanceof SupportsPropertyListeners)
			{
				SupportsPropertyListeners pw = (SupportsPropertyListeners) _item
						.getEditable();
				pw.removePropertyChangeListener(listener);
			}
		}

		private static class EventStack
		{

			public EventStack(int delay)
			{
				this.delay = delay;
				if (delay <= 0)
				{
					throw new RuntimeException("Delay has to be positive");
				}
			}

			public void addEvent(Runnable event)
			{
				if (event == null)
				{
					throw new RuntimeException("Event cannot be null");
				}
				getEventRunner().setNextEvent(event);
			}

			// implementation details

			private final int delay;

			private DelayedEventRunner eventRunner = null;

			private DelayedEventRunner getEventRunner()
			{
				if (eventRunner == null)
				{
					eventRunner = new DelayedEventRunner(delay);
					eventRunner.start();
				}
				return eventRunner;
			}

		}

		static private class DelayedEventRunner extends Thread
		{

			private final int delay;
			private Runnable nextEvent = null;
			private Long eventReceivedTime = null;

			public DelayedEventRunner(int delay)
			{
				this.delay = delay;
				setDaemon(true);
			}

			synchronized public void setNextEvent(Runnable runnable)
			{
				// store the time we received this
				eventReceivedTime = new Date().getTime();

				// remember this event (over-writing any previous events)
				nextEvent = runnable;
			}

			public void run()
			{
				while (true)
				{
					synchronized (this)
					{
						if (nextEvent != null)
						{
							boolean isDelayUp = false;

							final long currentTime = new Date().getTime();

							// how long since we last received an event?
							long elapsedTime = currentTime - eventReceivedTime;

							// have we passed the required waiting time?
							isDelayUp = elapsedTime > delay;

							if (isDelayUp)
							{
								Display.getDefault().asyncExec(nextEvent);
								// and clear the queue
								nextEvent = null;
							}
						}
					}

					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
						// we can not do anything about it, let's ignore
					}
				}
			}

		}

	}

	private ISelectionListener mySelectionListener;

	private GridEditorActionGroup myActions;

	private GridEditorUI myUI;

	private GridEditorUndoSupport myUndoSupport;

	private UndoActionHandler myUndoAction;

	private RedoActionHandler myRedoAction;

	@Override
	public void createPartControl(Composite parent)
	{
		GridEditorActionContext actionContext = new GridEditorActionContext(
				myUndoSupport);
		myActions = new GridEditorActionGroup(this, actionContext);
		myUI = new GridEditorUI(parent, myActions);
		ISelectionService selectionService = getSite().getWorkbenchWindow()
				.getSelectionService();
		handleWorkspaceSelectionChanged(selectionService.getSelection());

		IActionBars actionBars = getViewSite().getActionBars();
		myActions.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), myUndoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), myRedoAction);
	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(getSelectionListener());
		super.dispose();
	}

	private GriddableWrapper extractGriddableSeries(ISelection selection)
	{
		GriddableWrapper res = null;

		if (false == selection instanceof IStructuredSelection)
		{
			return null;
		}
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if (structuredSelection.isEmpty())
		{
			return null;
		}
		Object firstElement = structuredSelection.getFirstElement();

		// right, see if this is a series object already. if it is, we've got
		// our
		// data. if it isn't, see
		// if it's a candidate for editing and collate a series of elements
		if (firstElement instanceof EditableWrapper)
		{
			EditableWrapper wrapped = (EditableWrapper) firstElement;
			if (wrapped.getEditableValue() instanceof GriddableSeriesMarker)
			{
				res = new GriddableWrapper(wrapped);
			}
		}

		return res;
	}

	private ISelectionListener getSelectionListener()
	{
		if (mySelectionListener == null)
		{
			mySelectionListener = new ISelectionListener()
			{

				@Override
				public void selectionChanged(IWorkbenchPart part, ISelection selection)
				{
					if (part == GridEditorView.this)
					{
						// ignore, we are going to handle our own selection
						// ourselves
						return;
					}
					handleWorkspaceSelectionChanged(selection);
				}
			};
		}
		return mySelectionListener;
	}

	public GridEditorUI getUI()
	{
		return myUI;
	}

	private void handleWorkspaceSelectionChanged(ISelection actualSelection)
	{
		if (myUI.isDisposed())
		{
			return;
		}
		GriddableWrapper input = extractGriddableSeries(actualSelection);

		if (input != null)
		{

			// yes, but what are we currently looking at?
			GriddableWrapper existingInput = (GriddableWrapper) myUI.getTable()
					.getTableViewer().getInput();

			// see if we're currently looking at something
			EditableWrapper editable = null;
			if (existingInput != null)
			{
				editable = existingInput.getWrapper();
			}

			// are they the same?
			if (input.getWrapper() == editable)
			{
				// ignore, we're already looking at it
			}
			else
			{
				myUI.inputSeriesChanged(input);
			}
		}
	}

	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		ISelectionService selectionService = site.getWorkbenchWindow()
				.getSelectionService();
		selectionService.addSelectionListener(getSelectionListener());

		initUndoSupport();
	}

	private void initUndoSupport()
	{
		myUndoSupport = new GridEditorUndoSupport(PlatformUI.getWorkbench()
				.getOperationSupport().getOperationHistory());
		// set up action handlers that operate on the current context
		myUndoAction = new UndoActionHandler(this.getSite(), myUndoSupport
				.getUndoContext());
		myRedoAction = new RedoActionHandler(this.getSite(), myUndoSupport
				.getUndoContext());
	}

	public void refreshUndoContext()
	{
		if (myUndoAction != null)
		{
			myUndoAction.dispose();
			myUndoAction = null;
		}
		if (myRedoAction != null)
		{
			myRedoAction.dispose();
			myRedoAction = null;
		}

		myUndoAction = new UndoActionHandler(this.getSite(), myUndoSupport
				.getUndoContext());
		myRedoAction = new RedoActionHandler(this.getSite(), myUndoSupport
				.getUndoContext());
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), myUndoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), myRedoAction);
		actionBars.updateActionBars();
	}

	@Override
	public void setFocus()
	{
		myUI.forceTableFocus();
	}

}
