package org.mwc.cmap.grideditor.table;

import java.util.Date;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.grideditor.GridEditorUndoSupport;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.grideditor.command.SetTimeStampOperation;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;


public class DateTimeEditingSupportRich extends EditingSupport {

	private EditableTarget myTabTraverseTarget;

	private final GridEditorUndoSupport myUndoSupport;

	public DateTimeEditingSupportRich(final TableModel tableModel) {
		super(tableModel.getViewer());
		myUndoSupport = tableModel.getUndoSupport();
	}

	public void setTabTraverseTarget(final EditableTarget tabTraverseTarget) {
		myTabTraverseTarget = tabTraverseTarget;
	}

	@Override
	protected boolean canEdit(final Object element) {
		return element instanceof TimeStampedDataItem;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		final DateTimeCellEditor cellEditor = new DateTimeCellEditor(getCellEditorParent());
		if (myTabTraverseTarget != null) {
			cellEditor.getTimeUI().addTraverseListener(new CellEditorTraverseHandler(myTabTraverseTarget, element));
		}
		return cellEditor;
	}

	@Override
	protected Object getValue(final Object element) {
		final TimeStampedDataItem dataItem = (TimeStampedDataItem) element;
		final Date date = dataItem.getDTG().getDate();
		return date;
	}

	@Override
	protected void setValue(final Object element, final Object value) {
		final TimeStampedDataItem dataItem = (TimeStampedDataItem) element;
		final Date valueImpl = (Date) value;
		if (valueImpl != null && !valueImpl.equals(dataItem.getDTG().getDate())) {
			final GriddableSeries series = (GriddableSeries) getViewer().getInput();
			final OperationEnvironment environment = new OperationEnvironment(myUndoSupport.getUndoContext(), series, dataItem);
			final SetTimeStampOperation update = new SetTimeStampOperation(environment, new HiResDate(valueImpl));
			try {
				myUndoSupport.getOperationHistory().execute(update, null, null);
			} catch (final ExecutionException e) {
				throw new RuntimeException("[Table]Can't set the timestamp of :" + valueImpl + //
						" for item " + dataItem, e);
			}
		}
	}

	protected final Composite getCellEditorParent() {
		final TableViewer viewer = (TableViewer) getViewer();
		return viewer.getTable();
	}

}
