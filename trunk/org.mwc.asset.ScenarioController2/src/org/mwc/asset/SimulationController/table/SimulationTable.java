package org.mwc.asset.SimulationController.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.mwc.asset.scenariocontroller2.Activator;
import org.mwc.asset.scenariocontroller2.CoreControllerPresenter;
import org.mwc.asset.scenariocontroller2.views.ScenarioWrapper;
import org.mwc.cmap.core.property_support.EditableWrapper;

import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Scenario.LiveScenario.ISimulationQue;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;
import MWC.Algorithms.LiveData.IAttribute.IndexedAttribute;
import MWC.GUI.Layer;

public class SimulationTable
{

	private abstract class ColumnData extends ColumnSizeData
	{

		public ColumnData(String title, int weight)
		{
			super(new TableColumn(getTable(), SWT.LEFT), weight);
			getTableColumn().setText(title);
			getTableColumn().setData(this);
		}

		public abstract Object getSelection(ISimulation simulation,
				Vector<IAttribute> attrs);

		public abstract Object getValue(ISimulation simulation,
				Vector<IAttribute> attrs);
	}

	private class SimulationLabelProvider extends BaseLabelProvider implements
			ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (!hasInput())
			{
				return ""; //$NON-NLS-1$
			}
			Object value = getCellValue(element, getTable().getColumn(columnIndex));
			return value == null ? "" : value.toString(); //$NON-NLS-1$
		}
	}

	private class SimulationRow
	{

		private final ISimulation mySimulation;

		private boolean myIsRunning;

		private PropertyChangeListener myStateListener;

		private PropertyChangeListener myAttributeListener;

		private HashSet<IAttribute> myListenedAttributes;

		private boolean myDisposed = false;

		private Vector<IAttribute> myAttributeList;
		private final IAttribute _state;

		public SimulationRow(ISimulation simulation, Vector<IAttribute> attributes,
				IAttribute state)
		{
			mySimulation = simulation;
			myListenedAttributes = new HashSet<IAttribute>();
			myIsRunning = false;
			myAttributeList = attributes;
			_state = state;
			myAttributeListener = new PropertyChangeListener()
			{

				public void propertyChange(PropertyChangeEvent evt)
				{
					Display.getDefault().asyncExec(new Runnable()
					{

						public void run()
						{
							if (isDisposed())
							{
								return;
							}
							onAttributeChanged();
						}
					});
				}
			};
			myStateListener = new PropertyChangeListener()
			{

				public void propertyChange(PropertyChangeEvent evt)
				{
					// is it one of ours?
					if (evt.getSource() == mySimulation)
					{
						if (isDisposed())
						{
							System.out.println("disposed!!");
							return;
						}
						onStateChanged();
					}
				}
			};

			_state.addPropertyChangeListener(myStateListener);
			onStateChanged();
		}

		public void dispose()
		{
			myDisposed = true;
			_state.removePropertyChangeListener(myStateListener);
			removeAttributeListeners();
		}

		private ISimulation getSimulation()
		{
			return mySimulation;
		}

		private boolean isDisposed()
		{
			return myDisposed || getControl().isDisposed();
		}

		private void onAttributeChanged()
		{
			updateRow();
		}

		private void onStateChanged()
		{
			Object theState = _state.getCurrent(getSimulation()).getValue();
			if (ISimulation.RUNNING.equals(theState))
			{
				if (!myIsRunning)
				{
					myIsRunning = true;
					for (ColumnDescriptor columnDescriptor : myVisibleAttributeColumns)
					{
						IAttribute attribute = myAttributeList.get(columnDescriptor
								.getIndex());
						attribute.addPropertyChangeListener(myAttributeListener);
						myListenedAttributes.add(attribute);
					}
				}
			}
			else
			{
				if (myIsRunning)
				{
					myIsRunning = false;
					removeAttributeListeners();
				}
			}
			Display.getDefault().asyncExec(new Runnable()
			{

				public void run()
				{
					updateRow();
				}
			});
		}

		public void refreshListenedAttributes()
		{
			if (!myIsRunning)
			{
				return;
			}

			HashSet<IAttribute> newListenedAttributes = new HashSet<IAttribute>();
			for (ColumnDescriptor columnDescriptor : myVisibleAttributeColumns)
			{
				IAttribute newListenedAttribute = myAttributeList
						.elementAt(columnDescriptor.getIndex());
				if (myListenedAttributes.contains(newListenedAttribute))
				{
					myListenedAttributes.remove(newListenedAttribute);
				}
				else
				{
					newListenedAttribute.addPropertyChangeListener(myAttributeListener);
				}
				newListenedAttributes.add(newListenedAttribute);
			}
			for (IAttribute oldListenedAttribute : myListenedAttributes)
			{
				oldListenedAttribute.removePropertyChangeListener(myAttributeListener);
			}
			myListenedAttributes = newListenedAttributes;
		}

		private void removeAttributeListeners()
		{
			for (IAttribute attribute : myListenedAttributes)
			{
				attribute.removePropertyChangeListener(myAttributeListener);
			}
			myListenedAttributes.clear();
		}

		private void updateRow()
		{
			myTableViewer.update(getSimulation(), null);
			if (myTableCursor.getRow() != null
					&& myTableCursor.getRow().getData() == mySimulation)
			{
				myTableCursor.redraw();
			}
		}
	}

	private class SimulationViewerSorter extends ViewerSorter
	{

		private TableColumn myTableColumn;

		private boolean myIsAscending = false;

		@SuppressWarnings("unchecked")
		@Override
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			if (myTableColumn == null)
			{
				return 0;
			}

			Object value1 = getCellValue(e1, myTableColumn);
			Object value2 = getCellValue(e2, myTableColumn);
			boolean value1empty = value1 == null || "".equals(value1.toString()); //$NON-NLS-1$
			boolean value2empty = value2 == null || "".equals(value2.toString()); //$NON-NLS-1$

			int result = 0;
			if (value1empty && value2empty)
			{
				result = 0;
			}
			else if (value1empty)
			{
				result = 1;
			}
			else if (value2empty)
			{
				result = -1;
			}
			else
			{
				if (value1 instanceof Comparable<?>
						&& value1.getClass().isInstance(value2))
				{
					result = ((Comparable<Object>) value1).compareTo(value2);
				}
				else if (value2 instanceof Comparable<?>
						&& value2.getClass().isInstance(value1))
				{
					result = -((Comparable<Object>) value2).compareTo(value1);
				}
				else
				{
					if ((value1 != null) && (value2 != null))
						result = value1.toString().compareTo(value2.toString());
				}
				if (!myIsAscending)
				{
					result = -result;
				}
			}

			return result;
		}

		public void setColumn(TableColumn tableColumn)
		{
			if (tableColumn == null)
			{
				if (myTableColumn != null)
				{
					myTableColumn.setImage(null);
				}
				myIsAscending = false;
				myTableColumn = tableColumn;
				return;
			}

			if (tableColumn == myTableColumn)
			{
				myIsAscending = !myIsAscending;
			}
			else
			{
				if (myTableColumn != null)
				{
					myTableColumn.setImage(null);
				}
				myTableColumn = tableColumn;
				myIsAscending = true;
			}
			myTableColumn.setImage(Activator.getImageDescriptor(
					myIsAscending ? Activator.IMG_ASCEND : Activator.IMG_DESCEND)
					.createImage());
			boolean isCellSelected = myTableCursor.getRow() != null;
			myTableViewer.refresh(true, true);
			if (isCellSelected && getTable().getSelectionIndex() != -1)
			{
				myTableCursor.setSelection(getTable().getSelectionIndex(),
						myTableCursor.getColumn());
			}
		}
	}

	private abstract class SortableColumnData extends ColumnData
	{

		public SortableColumnData(String title, int weight)
		{
			super(title, weight);
			getTableColumn().addListener(SWT.Selection, new Listener()
			{

				public void handleEvent(Event event)
				{
					myViewerSorter.setColumn(getTableColumn());
				}
			});
		}
	}

	private static final int ROW_MARKER_COLUMN_WIDTH = 16;

	private static final int NAME_COLUMN_WEIGHT = 2;

	private static final int ATTRIBUTE_COLUMN_WEIGHT = 1;

	private static final String NAME_COLUMN_TITLE = Messages.SimulationTable_1;

	private TableViewer myTableViewer;

	private ISimulationQue myInput;

	private ArrayList<SimulationRow> myRows;

	private ArrayList<ColumnDescriptor> myAttributeColumns;

	private ArrayList<ColumnDescriptor> myVisibleAttributeColumns;

	private Menu myContextMenu;

	private SimulationViewerSorter myViewerSorter;

	private ISelectionProvider mySelectionProvider;

	private ISelection mySelection;

	private TableCursor myTableCursor;

	private ColumnsResizer myColumnsResizer;

	private CoreControllerPresenter _myPresenter;

	private HashMap<ScenarioType, ScenarioWrapper> _wrappedScenarios = new HashMap<ScenarioType, ScenarioWrapper>();

	public SimulationTable(Composite parent, CoreControllerPresenter contPresenter)
	{
		myTableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);

		_wrappedScenarios = new HashMap<ScenarioType, ScenarioWrapper>();

		_myPresenter = contPresenter;

		getTable().setHeaderVisible(true);
		getTable().setLinesVisible(true);
		getTable().setDragDetect(false);

		myTableViewer.setContentProvider(new ArrayContentProvider());
		myTableViewer.setLabelProvider(new SimulationLabelProvider());
		myViewerSorter = new SimulationViewerSorter();
		myTableViewer.setSorter(myViewerSorter);

		myContextMenu = new Menu(getTable().getShell(), SWT.POP_UP);
		getTable().setMenu(myContextMenu);

		myAttributeColumns = new ArrayList<ColumnDescriptor>();
		myVisibleAttributeColumns = new ArrayList<ColumnDescriptor>();

		myRows = new ArrayList<SimulationRow>();

		myColumnsResizer = new ColumnsResizer(getTable(), ROW_MARKER_COLUMN_WIDTH,
				false);

		myTableCursor = new TableCursor(getTable(), SWT.NONE);
		myTableCursor.addListener(SWT.Selection, new Listener()
		{

			public void handleEvent(Event event)
			{
				getTable().setSelection(myTableCursor.getRow());
				ISimulation theSim = (ISimulation) myTableCursor.getRow().getData();

				// hmm, is this a data column?
				Object newSel = getColumnData(
						getTable().getColumn(myTableCursor.getColumn())).getSelection(
						theSim, myInput.getAttributes());

				StructuredSelection strSel = null;

				if (newSel instanceof CoreScenario)
				{
					ScenarioType theScenario = (ScenarioType) newSel;

					// better wrap it
					ScenarioLayer sl = new ScenarioLayer();
					sl.setScenario(theScenario);

					// right, do we have a wrapper for this object. we cache them so we
					// aren't always changing
					ScenarioWrapper sw = _wrappedScenarios.get(theScenario);
					if (sw == null)
					{
						sw = new ScenarioWrapper(_myPresenter, sl);

						// tell it about any backdrop data
						Layer theBackdrop = theScenario.getBackdrop();
						if (theBackdrop != null)
							sw.addThisLayer(theBackdrop);

						_wrappedScenarios.put((ScenarioType) theSim, sw);

						// also tell it about any observers
						sw.fireNewController();

					}

					// ok, now wrap it as an editable
					EditableWrapper ew = new EditableWrapper(sw);

					// and as a selection
					strSel = new StructuredSelection(ew);
				}
				else if (newSel instanceof IAttribute)
				{
					IAttribute attr = (IAttribute) newSel;

					// ok, create indexed attributed
					IndexedAttribute indexed = new IndexedAttribute(theSim, attr);

					// better wrap it
					strSel = new StructuredSelection(indexed);
				}

				if (strSel != null)
				{
					setSelection(strSel);
				}
			}
		});
	}

	private void doSetSelection()
	{
		if (mySelectionProvider == null)
		{
			return;
		}
		mySelectionProvider.setSelection(mySelection);
	}

	private Object getCellValue(Object element, TableColumn tableColumn)
	{
		return getColumnData(tableColumn).getValue((ISimulation) element,
				myInput.getAttributes());
	}

	private ColumnData getColumnData(TableColumn tableColumn)
	{
		return (ColumnData) tableColumn.getData();
	}

	public Control getControl()
	{
		return getTable();
	}

	public ISelection getSelection()
	{
		return mySelection;
	}

	private Table getTable()
	{
		return myTableViewer.getTable();
	}

	private boolean hasInput()
	{
		return myInput != null;
	}

	private void refreshColumns()
	{
		getTable().setRedraw(false);
		try
		{

			myVisibleAttributeColumns.clear();
			for (ColumnDescriptor columnDescriptor : myAttributeColumns)
			{
				if (columnDescriptor.isVisible())
				{
					myVisibleAttributeColumns.add(columnDescriptor);
				}
			}

			for (TableColumn column : myTableViewer.getTable().getColumns())
			{
				column.dispose();
			}

			if (!hasInput())
			{
				return;
			}

			ArrayList<ColumnSizeData> columnSizeDatas = new ArrayList<ColumnSizeData>();
			ColumnData rowMarkerColumnData = new ColumnData("", -1) { //$NON-NLS-1$

				@Override
				public Object getSelection(ISimulation simulation,
						Vector<IAttribute> attrs)
				{
					return simulation;
				}

				@Override
				public Object getValue(ISimulation simulation, Vector<IAttribute> attrs)
				{
					return ""; //$NON-NLS-1$
				}
			};
			rowMarkerColumnData.getTableColumn().setResizable(false);
			rowMarkerColumnData.setWidth(ROW_MARKER_COLUMN_WIDTH);
			rowMarkerColumnData.getTableColumn().addListener(SWT.Selection,
					new Listener()
					{

						public void handleEvent(Event event)
						{
							myColumnsResizer.fitTableWidth();
						}
					});
			columnSizeDatas.add(new SortableColumnData(NAME_COLUMN_TITLE,
					NAME_COLUMN_WEIGHT)
			{

				@Override
				public Object getSelection(ISimulation simulation,
						Vector<IAttribute> attrs)
				{
					return simulation;
				}

				@Override
				public Object getValue(ISimulation simulation, Vector<IAttribute> attrs)
				{
					return simulation.getName();
				}
			});
			for (final ColumnDescriptor columnDescriptor : myVisibleAttributeColumns)
			{
				columnSizeDatas.add(new SortableColumnData(columnDescriptor.getName(),
						ATTRIBUTE_COLUMN_WEIGHT)
				{

					@Override
					public Object getSelection(ISimulation simulation,
							Vector<IAttribute> attrs)
					{
						return attrs.get(columnDescriptor.getIndex());
					}

					@Override
					public Object getValue(ISimulation simulation,
							Vector<IAttribute> attrs)
					{
						// return new Integer(15);
						final int colIndex = columnDescriptor.getIndex();
						DataDoublet dataDoublet = attrs.get(colIndex)
								.getCurrent(simulation);
						return dataDoublet == null ? null : dataDoublet.getValue();
					}
				});
			}

			myColumnsResizer.setSizeDatas(columnSizeDatas);
			myColumnsResizer.fitTableWidth();

			for (SimulationRow simulationRow : myRows)
			{
				simulationRow.refreshListenedAttributes();
			}

		}
		finally
		{
			getTable().setRedraw(true);
			myTableViewer.refresh();
		}
	}

	private void refreshMenu()
	{
		for (MenuItem menuItem : myContextMenu.getItems())
		{
			menuItem.dispose();
		}
		for (final ColumnDescriptor columnDescriptor : myAttributeColumns)
		{
			final MenuItem menuItem = new MenuItem(myContextMenu, SWT.CHECK);
			menuItem.setText(columnDescriptor.getName());
			menuItem.setSelection(columnDescriptor.isVisible());
			menuItem.addListener(SWT.Selection, new Listener()
			{

				public void handleEvent(Event event)
				{
					columnDescriptor.setVisible(!columnDescriptor.isVisible());
					menuItem.setSelection(columnDescriptor.isVisible());
					refreshColumns();
				}
			});
		}
	}

	public void setInput(ISimulationQue input)
	{
		for (SimulationRow row : myRows)
		{
			row.dispose();
		}

		myInput = input;

		myAttributeColumns.clear();

		if (hasInput())
		{
			int i = 0;
			Vector<IAttribute> theAttrs = myInput.getAttributes();
			for (IAttribute attribute : theAttrs)
			{
				myAttributeColumns.add(new ColumnDescriptor(attribute.getName(), i,
						attribute.isSignificant()));
				i++;
			}
		}

		myViewerSorter.setColumn(null);

		refreshColumns();

		myTableViewer.setInput(myInput == null ? new ISimulation[0] : myInput
				.getSimulations().toArray());

		myRows.clear();
		if (hasInput())
		{
			for (ISimulation simulation : myInput.getSimulations())
			{
				myRows.add(new SimulationRow(simulation, myInput.getAttributes(),
						myInput.getState()));
			}
		}

		refreshMenu();
	}

	private void setSelection(ISelection selection)
	{
		mySelection = selection;
		doSetSelection();
	}

	public void setSelectionProvider(ISelectionProvider selectionProvider)
	{
		mySelectionProvider = selectionProvider;
		doSetSelection();
	}
}
