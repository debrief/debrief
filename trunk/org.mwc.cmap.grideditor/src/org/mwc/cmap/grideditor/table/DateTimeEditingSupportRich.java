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

	public DateTimeEditingSupportRich(TableModel tableModel) {
		super(tableModel.getViewer());
		myUndoSupport = tableModel.getUndoSupport();
	}

	public void setTabTraverseTarget(EditableTarget tabTraverseTarget) {
		myTabTraverseTarget = tabTraverseTarget;
	}

	@Override
	protected boolean canEdit(Object element) {
		return element instanceof TimeStampedDataItem;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		DateTimeCellEditor cellEditor = new DateTimeCellEditor(getCellEditorParent());
		if (myTabTraverseTarget != null) {
			cellEditor.getTimeUI().addTraverseListener(new CellEditorTraverseHandler(myTabTraverseTarget, element));
		}
		return cellEditor;
	}

	@Override
	protected Object getValue(Object element) {
		TimeStampedDataItem dataItem = (TimeStampedDataItem) element;
		Date date = dataItem.getDTG().getDate();
		return date;
	}

	@Override
	protected void setValue(Object element, Object value) {
		TimeStampedDataItem dataItem = (TimeStampedDataItem) element;
		Date valueImpl = (Date) value;
		if (valueImpl != null && !valueImpl.equals(dataItem.getDTG().getDate())) {
			GriddableSeries series = (GriddableSeries) getViewer().getInput();
			OperationEnvironment environment = new OperationEnvironment(myUndoSupport.getUndoContext(), series, dataItem);
			SetTimeStampOperation update = new SetTimeStampOperation(environment, new HiResDate(valueImpl));
			try {
				myUndoSupport.getOperationHistory().execute(update, null, null);
			} catch (ExecutionException e) {
				throw new RuntimeException("Can't set the timestamp of :" + valueImpl + //
						" for item " + dataItem, e);
			}
		}
	}

	protected final Composite getCellEditorParent() {
		TableViewer viewer = (TableViewer) getViewer();
		return viewer.getTable();
	}

}
