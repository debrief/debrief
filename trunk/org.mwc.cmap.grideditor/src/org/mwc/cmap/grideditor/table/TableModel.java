package org.mwc.cmap.grideditor.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TableColumn;
import org.mwc.cmap.grideditor.GridEditorUndoSupport;
import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.gridharness.data.FormatDateTime;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptorExtension;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;


public class TableModel {

	/**
	 * This constant is used to determine the padding (both left and right) for
	 * the table column title, while determining the default column width.
	 */
	private static final int COLUMN_WIDTH_PADDING = 15;

	private GriddableSeries mySeries;

	private ColumnBase myDateTimeColumn;

	private ColumnBase myRowSelectorColumn;

	private List<ColumnBase> myAllColumns = new ArrayList<ColumnBase>(10);

	private final TableViewer myViewer;

	private final GridEditorUndoSupport myUndoSupport;

	public TableModel(TableViewer viewer, GridEditorUndoSupport undoSupport) {
		myViewer = viewer;
		myUndoSupport = undoSupport;
	}

	public TableViewer getViewer() {
		return myViewer;
	}

	public GridEditorUndoSupport getUndoSupport() {
		return myUndoSupport;
	}

	public void setInputSeries(GriddableSeries newSeries) {
		removeColumns(newSeries == null);
		mySeries = newSeries;
		if (newSeries != null) {
			createColumns(myDateTimeColumn == null);
		}
	}

	public ColumnBase getColumnData(int columnIndex) {
		if (columnIndex >= myAllColumns.size()) {
			return null;
		}
		return myAllColumns.get(columnIndex);
	}

	public ColumnBase findColumnData(TableColumn tableColumn) {
		for (ColumnBase next : myAllColumns) {
			if (next.getTableViewerColumn().getColumn() == tableColumn) {
				return next;
			}
		}
		return null;
	}

	private void removeColumns(boolean removeFixedColumns) {
		getViewer().getTable().setRedraw(false);
		try {
			for (Iterator<ColumnBase> columns = myAllColumns.iterator(); columns.hasNext();) {
				ColumnBase next = columns.next();
				if (!removeFixedColumns && next.isFixed()) {
					continue;
				}
				next.getTableViewerColumn().getColumn().dispose();
				if (next == myDateTimeColumn) {
					myDateTimeColumn = null;
				}
				if (next == myRowSelectorColumn) {
					myRowSelectorColumn = null;
				}
				columns.remove();
			}
		} finally {
			getViewer().getTable().setRedraw(true);
		}
	}

	private void createColumns(boolean createFixedCOlumns) {
		getViewer().getTable().setRedraw(false);
		try {
			if (createFixedCOlumns) {
				myAllColumns.add(myRowSelectorColumn = new RowSelectorColumn(this));
				myAllColumns.add(myDateTimeColumn = new DateTimeColumn(this, false));
			}
			GriddableItemDescriptor[] allAttributes = mySeries.getAttributes();
			for (int i = 0; i < allAttributes.length; i++) {
				boolean isLast = (i == allAttributes.length - 1);
				myAllColumns.add(new DescriptorBasedColumn(this, allAttributes[i], myAllColumns.size(), isLast));
			}
		} finally {
			getViewer().getTable().setRedraw(true);
		}
	}

	public abstract static class ColumnBase {

		private final TableModel myModel;

		public ColumnBase(TableModel model) {
			myModel = model;
		}

		protected TableViewer getViewer() {
			return myModel.getViewer();
		}

		protected TableModel getModel() {
			return myModel;
		}

		public abstract boolean isFixed();

		public abstract TableViewerColumn getTableViewerColumn();

		public abstract ILabelProvider getLabelProvider(Object element);

		/**
		 * @return associated {@link GriddableItemDescriptor} for this column or
		 * 	<code>null</code> if there are no (i.p., for an implicit date/time
		 * 	column)
		 */
		public abstract GriddableItemDescriptor getDescriptor();

		protected TableViewerColumn createColumn(String title) {
			return createColumn(title, title);
		}

		protected TableViewerColumn createColumn(String title, String sampleTextHint) {
			TableViewerColumn result = new TableViewerColumn(getViewer(), SWT.NONE);
			TableColumn column = result.getColumn();
			column.setText(title);
			Point hintWidth = hintMeasureText(sampleTextHint);
			hintWidth.x += 2 * COLUMN_WIDTH_PADDING;
			column.setWidth(hintWidth.x);
			column.setResizable(true);
			column.setMoveable(false);
			return result;
		}

		protected final Point hintMeasureText(String text) {
			GC gc = new GC(getViewer().getControl().getDisplay());
			gc.setFont(getViewer().getControl().getFont());
			try {
				return gc.textExtent(text);
			} finally {
				gc.dispose();
			}
		}
	}

	/**
	 * Couples the {@link TableViewerColumn} with underlying {@link
	 * GriddableItemDescriptor}.
	 */
	private static class DescriptorBasedColumn extends ColumnBase {

		/**
		 * Label text for any object that is not a {@link TimeStampedDataItem}
		 */
		private static final String UNKNOWN = "<unknown>";

		private final GriddableItemDescriptor myDescriptor;

		private final TableViewerColumn myTableViewerColumn;

		private LabelProvider myLabelProvider;

		public DescriptorBasedColumn(TableModel model, GriddableItemDescriptor descriptor, int columnIndex, boolean isLastColumn) {
			super(model);
			myDescriptor = descriptor;
			String widthEstimator = descriptor.getTitle();
			if (descriptor instanceof GriddableItemDescriptorExtension) {
				String dataSample = ((GriddableItemDescriptorExtension) descriptor).getSampleString();
				if (dataSample != null && dataSample.length() > widthEstimator.length()) {
					widthEstimator = dataSample;
				}
			}
			myTableViewerColumn = createColumn(descriptor.getTitle(), widthEstimator);
			DescriptorEditingSupport editingSupport = new DescriptorEditingSupport(model, myDescriptor);
			if (isLastColumn) {
				editingSupport.setTabTraverseTarget(new NextElementFirstColumnTarget(getViewer(), true));
			} else {
				editingSupport.setTabTraverseTarget(new SameElementDifferentColumnTarget(getViewer(), columnIndex + 1));
			}
			myTableViewerColumn.setEditingSupport(editingSupport);
		}

		@Override
		public boolean isFixed() {
			return false;
		}

		@Override
		public TableViewerColumn getTableViewerColumn() {
			return myTableViewerColumn;
		}

		public GriddableItemDescriptor getDescriptor() {
			return myDescriptor;
		}

		@Override
		public ILabelProvider getLabelProvider(Object element) {
			//XXX: the line below does not work -- there are no meaningful 
			//implementation of the label providers in actual descriptors
			//return getDescriptor().getEditor().getLabelFor(element);

			//So, for now we will use the double value which is already used for charting
			if (myLabelProvider == null) {
				myLabelProvider = createChartableLabelProvider();
			}

			//if chart data is not available, just give up
			return myLabelProvider != null ? myLabelProvider : getDescriptor().getEditor().getLabelFor(element);
		}

		private LabelProvider createChartableLabelProvider() {
			return new LabelProvider() {

				@Override
				public String getText(Object element) {
					if (false == element instanceof TimeStampedDataItem) {
						return UNKNOWN;
					}
					TimeStampedDataItem dataItem = (TimeStampedDataItem) element;
					Object value = BeanUtil.getItemValue(dataItem, getDescriptor());
					return String.valueOf(value);
				}
			};
		}

	}

	private static class RowSelectorColumn extends ColumnBase {

		/**
		 * Row selector is a fake empty column that allows to select rows
		 */
		public static final int SELECTOR_COLUMN_INDEX = 0;

		private final TableViewerColumn myEmptyColumn;

		private final LabelProvider myLabelProvider;

		public RowSelectorColumn(TableModel tableModel) {
			super(tableModel);
			myEmptyColumn = createColumn("");
			myLabelProvider = new LabelProvider() {

				@Override
				public String getText(Object element) {
					return "a";
				}
			};
		}

		@Override
		public boolean isFixed() {
			return true;
		}

		@Override
		public GriddableItemDescriptor getDescriptor() {
			return null;
		}

		@Override
		public TableViewerColumn getTableViewerColumn() {
			return myEmptyColumn;
		}

		@Override
		public ILabelProvider getLabelProvider(Object arg0) {
			return myLabelProvider;
		}
	}

	private static class DateTimeColumn extends ColumnBase {

		/**
		 * The first column is a table row selector, so the first meaningful
		 * index is 1
		 */
		public static final int DATE_TIME_COLUMN_INDEX = 1;

		/**
		 * Name of the implicit first column (that shows the {@link HiResDate})
		 */
		private static final String DATE_TIME_COLUMN_TITLE = "Date Time";

		/**
		 * Label text for any object that is not a {@link HiResDate}
		 */
		private static final String NOT_A_DATE = "<not a date>";

		private LabelProvider myDateTimeLabelProvider;

		private final TableViewerColumn myTableViewerColumn;

		private final boolean myAppendMilliseconds;

		public DateTimeColumn(TableModel model, boolean appendMilliseconds) {
			super(model);
			myAppendMilliseconds = appendMilliseconds;
			final String SAMPLE_DATE = getSampleDateTimeLabel();
			myTableViewerColumn = createColumn(DATE_TIME_COLUMN_TITLE, SAMPLE_DATE);
			DateTimeEditingSupportRich editingSupport = new DateTimeEditingSupportRich(model);
			editingSupport.setTabTraverseTarget(new SameElementDifferentColumnTarget(getViewer(), DATE_TIME_COLUMN_INDEX + 1));
			myTableViewerColumn.setEditingSupport(editingSupport);

			DateTimeCellEditor shouldFit = new DateTimeCellEditor(getViewer().getTable());
			shouldFit.setValue(new Date());
			Point shouldFitSize = shouldFit.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			myTableViewerColumn.getColumn().setWidth(Math.max(myTableViewerColumn.getColumn().getWidth(), shouldFitSize.x));
			shouldFit.dispose();
		}

		@Override
		public boolean isFixed() {
			return true;
		}

		/**
		 * @return <code>null</code> because this column had no associated
		 * 	descriptor.
		 */
		@Override
		public GriddableItemDescriptor getDescriptor() {
			return null;
		}

		@Override
		public TableViewerColumn getTableViewerColumn() {
			return myTableViewerColumn;
		}

		@Override
		public ILabelProvider getLabelProvider(Object element) {
			if (myDateTimeLabelProvider == null) {
				myDateTimeLabelProvider = new LabelProvider() {

					@Override
					public String getText(Object element) {
						HiResDate date = null;
						if (element instanceof HiResDate) {
							date = (HiResDate) date;
						}
						if (element instanceof TimeStampedDataItem) {
							date = ((TimeStampedDataItem) element).getDTG();
						}
						if (date == null) {
							return NOT_A_DATE;
						}
						StringBuffer result = new StringBuffer();
						result.append(FormatDateTime.toString(date.getDate().getTime()));
						if (myAppendMilliseconds) {
							result.append(" (");
							result.append(date.getDate().getTime());
							result.append(" ms)");
						}
						return result.toString();
					}
				};
			}
			return myDateTimeLabelProvider;
		}

		private String getSampleDateTimeLabel() {
			HiResDate now = new HiResDate(new Date());
			return getLabelProvider(now).getText(now);
		}

	}

	private static class SameElementDifferentColumnTarget implements EditableTarget {

		private final int myColumnIndex;

		private final ColumnViewer myViewer;

		public SameElementDifferentColumnTarget(ColumnViewer viewer, int columnIndex) {
			myViewer = viewer;
			myColumnIndex = columnIndex;
		}

		@Override
		public int getColumnIndex() {
			return myColumnIndex;
		}

		@Override
		public ColumnViewer getColumnViewer() {
			return myViewer;
		}

		@Override
		public Object getElementToEdit(Object actuallyEdited) {
			return actuallyEdited;
		}
	}

	private static class NextElementFirstColumnTarget implements EditableTarget {

		private final ColumnViewer myViewer;

		private final boolean myCreateOnDemand;

		public NextElementFirstColumnTarget(ColumnViewer viewer, boolean createOnDemand) {
			myViewer = viewer;
			myCreateOnDemand = createOnDemand;
		}

		@Override
		public int getColumnIndex() {
			return DateTimeColumn.DATE_TIME_COLUMN_INDEX;
		}

		@Override
		public ColumnViewer getColumnViewer() {
			return myViewer;
		}

		@Override
		public Object getElementToEdit(Object actuallyEdited) {
			if (getColumnViewer().getInput() instanceof GriddableSeries && actuallyEdited instanceof TimeStampedDataItem) {
				GriddableSeries series = (GriddableSeries) getColumnViewer().getInput();
				TimeStampedDataItem actualItem = (TimeStampedDataItem) actuallyEdited;
				if (isLastItem(series, actualItem)) {
					return myCreateOnDemand ? cloneActualItem(series, actualItem) : null;
				} else {
					ListIterator<TimeStampedDataItem> items = series.getItems().listIterator();
					while (items.hasNext()) {
						TimeStampedDataItem next = items.next();
						if (next == actualItem) {
							break;
						}
					}
					return items.hasNext() ? items.next() : null;
				}
			}
			return null;
		}

		private Object cloneActualItem(GriddableSeries series, TimeStampedDataItem lastItem) {
			TimeStampedDataItem copy = series.makeCopy(lastItem);
			series.insertItem(copy);
			return copy;
		}

		private boolean isLastItem(GriddableSeries series, TimeStampedDataItem item) {
			if (series.getItems().isEmpty()) {
				return false;
			}
			List<TimeStampedDataItem> allItems = series.getItems();
			return allItems.get(allItems.size() - 1) == item;
		}

	}

}
