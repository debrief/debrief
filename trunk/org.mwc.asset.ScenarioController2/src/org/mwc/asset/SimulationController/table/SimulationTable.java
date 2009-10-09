package org.mwc.asset.SimulationController.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;

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
import org.mwc.asset.SimulationController.SimControllerPlugin;


import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Scenario.LiveScenario.ISimulationQue;
import MWC.Algorithms.LiveData.DataDoublet;
import MWC.Algorithms.LiveData.IAttribute;


public class SimulationTable {

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

	public SimulationTable(Composite parent) {
		myTableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);

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

		myColumnsResizer = new ColumnsResizer(getTable(), ROW_MARKER_COLUMN_WIDTH, false);

		myTableCursor = new TableCursor(getTable(), SWT.NONE);
		myTableCursor.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				getTable().setSelection(myTableCursor.getRow());
				setSelection(new StructuredSelection(getColumnData(getTable().getColumn(myTableCursor.getColumn())).getSelection((ISimulation) myTableCursor.getRow().getData())));
			}
		});
	}

	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		mySelectionProvider = selectionProvider;
		doSetSelection();
	}

	private void doSetSelection() {
		if (mySelectionProvider == null) {
			return;
		}
		mySelectionProvider.setSelection(mySelection);
	}

	private void setSelection(ISelection selection) {
		mySelection = selection;
		doSetSelection();
	}

	private Table getTable() {
		return myTableViewer.getTable();
	}

	public Control getControl() {
		return getTable();
	}

	public void setInput(ISimulationQue input) {
		for (SimulationRow row : myRows) {
			row.dispose();
		}

		myInput = input;

		myAttributeColumns.clear();

		if (hasInput()) {
			int i = 0;
			for (IAttribute attribute : myInput.getAttributes()) {
				myAttributeColumns.add(new ColumnDescriptor(attribute.getName(), i, attribute.isSignificant()));
				i++;
			}
		}

		myViewerSorter.setColumn(null);

		refreshColumns();

		myTableViewer.setInput(myInput == null ? new ISimulation[0] : myInput.getSimulations().toArray());

		myRows.clear();
		if (hasInput()) {
			for (ISimulation simulation : myInput.getSimulations()) {
				myRows.add(new SimulationRow(simulation));
			}
		}

		refreshMenu();
	}

	private boolean hasInput() {
		return myInput != null;
	}

	private void refreshMenu() {
		for (MenuItem menuItem : myContextMenu.getItems()) {
			menuItem.dispose();
		}
		for (final ColumnDescriptor columnDescriptor : myAttributeColumns) {
			final MenuItem menuItem = new MenuItem(myContextMenu, SWT.CHECK);
			menuItem.setText(columnDescriptor.getName());
			menuItem.setSelection(columnDescriptor.isVisible());
			menuItem.addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event event) {
					columnDescriptor.setVisible(!columnDescriptor.isVisible());
					menuItem.setSelection(columnDescriptor.isVisible());
					refreshColumns();
				}
			});
		}
	}

	private void refreshColumns() {
		getTable().setRedraw(false);
		try {

			myVisibleAttributeColumns.clear();
			for (ColumnDescriptor columnDescriptor : myAttributeColumns) {
				if (columnDescriptor.isVisible()) {
					myVisibleAttributeColumns.add(columnDescriptor);
				}
			}

			for (TableColumn column : myTableViewer.getTable().getColumns()) {
				column.dispose();
			}

			if (!hasInput()) {
				return;
			}

			ArrayList<ColumnSizeData> columnSizeDatas = new ArrayList<ColumnSizeData>();
			ColumnData rowMarkerColumnData = new ColumnData("", -1) { //$NON-NLS-1$

				@Override
				public Object getSelection(ISimulation simulation) {
					return simulation;
				}

				@Override
				public Object getValue(ISimulation simulation) {
					return ""; //$NON-NLS-1$
				}
			};
			rowMarkerColumnData.getTableColumn().setResizable(false);
			rowMarkerColumnData.setWidth(ROW_MARKER_COLUMN_WIDTH);
			rowMarkerColumnData.getTableColumn().addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event event) {
					myColumnsResizer.fitTableWidth();
				}
			});
			columnSizeDatas.add(new SortableColumnData(NAME_COLUMN_TITLE, NAME_COLUMN_WEIGHT) {

				@Override
				public Object getSelection(ISimulation simulation) {
					return simulation;
				}

				@Override
				public Object getValue(ISimulation simulation) {
					return simulation.getName();
				}
			});
			for (final ColumnDescriptor columnDescriptor : myVisibleAttributeColumns) {
				columnSizeDatas.add(new SortableColumnData(columnDescriptor.getName(), ATTRIBUTE_COLUMN_WEIGHT) {

					@Override
					public Object getSelection(ISimulation simulation) {
						return simulation.getAttributes().get(columnDescriptor.getIndex());
					}

					@Override
					public Object getValue(ISimulation simulation) {
//						return new Integer(15);
						final int colIndex = columnDescriptor.getIndex();
						DataDoublet dataDoublet = simulation.getAttributes().get(colIndex).getCurrent(simulation);
						return dataDoublet == null ? null : dataDoublet.getValue();
					}
				});
			}

			myColumnsResizer.setSizeDatas(columnSizeDatas);
			myColumnsResizer.fitTableWidth();

			for (SimulationRow simulationRow : myRows) {
				simulationRow.refreshListenedAttributes();
			}

		} finally {
			getTable().setRedraw(true);
			myTableViewer.refresh();
		}
	}

	private ColumnData getColumnData(TableColumn tableColumn) {
		return (ColumnData) tableColumn.getData();
	}

	private Object getCellValue(Object element, TableColumn tableColumn) {
		return getColumnData(tableColumn).getValue((ISimulation) element);
	}

	private abstract class ColumnData extends ColumnSizeData {

		public ColumnData(String title, int weight) {
			super(new TableColumn(getTable(), SWT.LEFT), weight);
			getTableColumn().setText(title);
			getTableColumn().setData(this);
		}

		public abstract Object getSelection(ISimulation simulation);

		public abstract Object getValue(ISimulation simulation);
	}

	private abstract class SortableColumnData extends ColumnData {

		public SortableColumnData(String title, int weight) {
			super(title, weight);
			getTableColumn().addListener(SWT.Selection, new Listener() {

				@Override
				public void handleEvent(Event event) {
					myViewerSorter.setColumn(getTableColumn());
				}
			});
		}
	}

	private class SimulationLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			if (!hasInput()) {
				return ""; //$NON-NLS-1$
			}
			Object value = getCellValue(element, getTable().getColumn(columnIndex));
			return value == null ? "" : value.toString(); //$NON-NLS-1$
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	private class SimulationRow {

		private final ISimulation mySimulation;

		private boolean myIsRunning;

		private PropertyChangeListener myStateListener;

		private PropertyChangeListener myAttributeListener;

		private HashSet<IAttribute> myListenedAttributes;

		private boolean myDisposed = false;

		public SimulationRow(ISimulation simulation) {
			mySimulation = simulation;
			myListenedAttributes = new HashSet<IAttribute>();
			myIsRunning = false;
			myAttributeListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							if (isDisposed()) {
								return;
							}
							onAttributeChanged();
						}
					});
				}
			};
			myStateListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							if (isDisposed()) {
								return;
							}
							onStateChanged();
						}
					});
				}
			};
			getSimulation().getState().addPropertyChangeListener(myStateListener);
			onStateChanged();
		}

		private boolean isDisposed() {
			return myDisposed || getControl().isDisposed();
		}

		private ISimulation getSimulation() {
			return mySimulation;
		}

		private void onStateChanged() {
			if (ISimulation.RUNNING.equals(getSimulation().getState().getCurrent(getSimulation()).getValue())) {
				if (!myIsRunning) {
					myIsRunning = true;
					for (ColumnDescriptor columnDescriptor : myVisibleAttributeColumns) {
						IAttribute attribute = getSimulation().getAttributes().get(columnDescriptor.getIndex());
						attribute.addPropertyChangeListener(myAttributeListener);
						myListenedAttributes.add(attribute);
					}
				}
			} else {
				if (myIsRunning) {
					myIsRunning = false;
					removeAttributeListeners();
				}
			}
			updateRow();
		}

		private void removeAttributeListeners() {
			for (IAttribute attribute : myListenedAttributes) {
				attribute.removePropertyChangeListener(myAttributeListener);
			}
			myListenedAttributes.clear();
		}

		public void refreshListenedAttributes() {
			if (!myIsRunning) {
				return;
			}

			HashSet<IAttribute> newListenedAttributes = new HashSet<IAttribute>();
			for (ColumnDescriptor columnDescriptor : myVisibleAttributeColumns) {
				IAttribute newListenedAttribute = getSimulation().getAttributes().elementAt(columnDescriptor.getIndex());
				if (myListenedAttributes.contains(newListenedAttribute)) {
					myListenedAttributes.remove(newListenedAttribute);
				} else {
					newListenedAttribute.addPropertyChangeListener(myAttributeListener);
				}
				newListenedAttributes.add(newListenedAttribute);
			}
			for (IAttribute oldListenedAttribute : myListenedAttributes) {
				oldListenedAttribute.removePropertyChangeListener(myAttributeListener);
			}
			myListenedAttributes = newListenedAttributes;
		}

		private void onAttributeChanged() {
			updateRow();
		}

		private void updateRow() {
			myTableViewer.update(getSimulation(), null);
			if (myTableCursor.getRow() != null && myTableCursor.getRow().getData() == mySimulation) {
				myTableCursor.redraw();
			}
		}

		public void dispose() {
			myDisposed = true;
			getSimulation().getState().removePropertyChangeListener(myStateListener);
			removeAttributeListeners();
		}
	}

	private class SimulationViewerSorter extends ViewerSorter {

		private TableColumn myTableColumn;

		private boolean myIsAscending = false;

		public void setColumn(TableColumn tableColumn) {
			if (tableColumn == null) {
				if (myTableColumn != null) {
					myTableColumn.setImage(null);
				}
				myIsAscending = false;
				myTableColumn = tableColumn;
				return;
			}

			if (tableColumn == myTableColumn) {
				myIsAscending = !myIsAscending;
			} else {
				if (myTableColumn != null) {
					myTableColumn.setImage(null);
				}
				myTableColumn = tableColumn;
				myIsAscending = true;
			}
			myTableColumn.setImage(SimControllerPlugin.getInstance().getImageRegistry().get(myIsAscending ? SimControllerPlugin.IMG_ASCEND : SimControllerPlugin.IMG_DESCEND));
			boolean isCellSelected = myTableCursor.getRow() != null;
			myTableViewer.refresh(true, true);
			if (isCellSelected && getTable().getSelectionIndex() != -1) {
				myTableCursor.setSelection(getTable().getSelectionIndex(), myTableCursor.getColumn());
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (myTableColumn == null) {
				return 0;
			}

			Object value1 = getCellValue(e1, myTableColumn);
			Object value2 = getCellValue(e2, myTableColumn);
			boolean value1empty = value1 == null || "".equals(value1.toString()); //$NON-NLS-1$
			boolean value2empty = value2 == null || "".equals(value2.toString()); //$NON-NLS-1$

			int result;
			if (value1empty && value2empty) {
				result = 0;
			} else if (value1empty) {
				result = 1;
			} else if (value2empty) {
				result = -1;
			} else {
				if (value1 instanceof Comparable<?> && value1.getClass().isInstance(value2)) {
					result = ((Comparable) value1).compareTo(value2);
				} else if (value2 instanceof Comparable<?> && value2.getClass().isInstance(value1)) {
					result = -((Comparable) value2).compareTo(value1);
				} else {
					result = value1.toString().compareTo(value2.toString());
				}
				if (!myIsAscending) {
					result = -result;
				}
			}

			return result;
		}
	}
}
