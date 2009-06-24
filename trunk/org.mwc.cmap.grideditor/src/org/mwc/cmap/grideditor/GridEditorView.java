package org.mwc.cmap.grideditor;

import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
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
import org.mwc.cmap.core.property_support.DebriefProperty;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.grideditor.table.actons.GridEditorActionGroup;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.Editable;
import MWC.GUI.Griddable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Editable.EditorType;

public class GridEditorView extends ViewPart
{

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
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		ISelectionService selectionService = site.getWorkbenchWindow()
				.getSelectionService();
		selectionService.addSelectionListener(getSelectionListener());

		initUndoSupport();
	}

	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(getSelectionListener());
		super.dispose();
	}

	@Override
	public void setFocus()
	{
		myUI.forceTableFocus();
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
		GriddableSeries input = extractGriddableSeries(actualSelection);

		if (input != null)
		{
			myUI.inputSeriesChanged(input);
		}
	}

	private GriddableSeries extractGriddableSeries(ISelection selection)
	{
		GriddableSeries res = null;

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

		// right, see if this is a series object already. if it is, we've got our
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

	protected class GriddableWrapper implements GriddableSeries
	{
		final EditableWrapper _item;

		public GriddableWrapper(EditableWrapper item)
		{
			_item = item;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener)
		{
			if (_item.getEditable() instanceof SupportsPropertyListeners)
			{
				SupportsPropertyListeners pw = (SupportsPropertyListeners) _item.getEditable();
				pw.addPropertyChangeListener(listener);
			}
		}

		@Override
		public void deleteItem(TimeStampedDataItem subject)
		{
			// TODO Auto-generated method stub
			throw new RuntimeException("NOT YET IMPLEMENTED!");
		}

		@Override
		public void fireModified(TimeStampedDataItem subject)
		{
//			modified event isn't working for position data. find out why!

			if (_item.getEditable() instanceof SupportsPropertyListeners)
			{
				SupportsPropertyListeners pw = (SupportsPropertyListeners) _item.getEditable();
				// ok, inform any listeners
				pw.firePropertyChange(GriddableSeries.PROPERTY_CHANGED,
						null, subject);
			}
		}

		private GriddableItemDescriptor[] _myAttributes;

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

						GriddableItemDescriptor gd = new GriddableItemDescriptor(desc
								.getDisplayName(), desc.getDisplayName(), dataClass, desc
								.getHelper());
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
					list.add((TimeStampedDataItem) ed);
				}
			}
			return list;
		}

		@Override
		public String getName()
		{
			return _item.getEditable().getName();
		}

		@Override
		public void insertItem(TimeStampedDataItem subject)
		{
			// TODO Auto-generated method stub
			throw new RuntimeException("NOT YET IMPLEMENTED!");

		}

		@Override
		public void insertItemAt(TimeStampedDataItem subject, int index)
		{
			// TODO Auto-generated method stub
			throw new RuntimeException("NOT YET IMPLEMENTED!");
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
				SupportsPropertyListeners pw = (SupportsPropertyListeners) _item.getEditable();
				pw.removePropertyChangeListener(listener);
			}
		}

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
						// ignore, we are going to handle our own selection ourselves
						return;
					}
					handleWorkspaceSelectionChanged(selection);
				}
			};
		}
		return mySelectionListener;
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

}
