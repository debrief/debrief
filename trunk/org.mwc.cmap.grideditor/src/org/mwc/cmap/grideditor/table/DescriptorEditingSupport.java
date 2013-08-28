package org.mwc.cmap.grideditor.table;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.grideditor.command.OperationEnvironment;
import org.mwc.cmap.grideditor.command.SetDescriptorValueOperation;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;
import org.mwc.cmap.gridharness.views.MultiControlCellEditor;

import MWC.GUI.TimeStampedDataItem;


public class DescriptorEditingSupport extends EditingSupport {

	private final GriddableItemDescriptor myDescriptor;

	private final TableModel myTableModel;

	private boolean myNeedCastValueToStringForCellEditor;

	private EditableTarget myTabTraverseTarget;

	public DescriptorEditingSupport(final TableModel tableModel, final GriddableItemDescriptor descriptor) {
		super(tableModel.getViewer());
		myDescriptor = descriptor;
		myTableModel = tableModel;
	}

	public GriddableItemDescriptor getDescriptor() {
		return myDescriptor;
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
		final CellEditor cellEditor = getDescriptor().getEditor().getCellEditorFor(getCellEditorParent());
		if (myTabTraverseTarget != null) {
			Control traverseSubject = null;
			if (cellEditor instanceof MultiControlCellEditor) {
				traverseSubject = ((MultiControlCellEditor) cellEditor).getLastControl();
			}
			if (traverseSubject == null) {
				traverseSubject = cellEditor.getControl();
			}
			if(traverseSubject != null)
				traverseSubject.addTraverseListener(new CellEditorTraverseHandler(myTabTraverseTarget, element));
		}
		myNeedCastValueToStringForCellEditor = cellEditor instanceof TextCellEditor;
		return cellEditor;
	}

	@Override
	protected Object getValue(final Object element) {
		if (false == element instanceof TimeStampedDataItem) {
			return null;
		}
		final TimeStampedDataItem item = (TimeStampedDataItem) element;
		final Object rawValue = BeanUtil.getItemValue(item, getDescriptor());
		return transformToCellEditor(rawValue);
	}

	@Override
	protected void setValue(final Object element, final Object value) {
		Object theValue = value;
		if (element instanceof TimeStampedDataItem) {
			final TimeStampedDataItem item = (TimeStampedDataItem) element;
			theValue = transformFromCellEditor(theValue);
			if (theValue != null && !theValue.equals(BeanUtil.getItemValue(item, myDescriptor))) {
				final GriddableSeries series = (GriddableSeries) getViewer().getInput();
				final OperationEnvironment environment = new OperationEnvironment(getUndoContext(), series, item, myDescriptor);
				final SetDescriptorValueOperation update = new SetDescriptorValueOperation(environment, theValue);
				try {
					getOperationHistory().execute(update, null, null);
				} catch (final ExecutionException e) {
					throw new RuntimeException("Can't set the value of :" + theValue + //
							" for descriptor " + myDescriptor.getTitle() + //
							" of item: " + item, e);
				}
			}
		}
	}

	protected final Composite getCellEditorParent() {
		final TableViewer viewer = (TableViewer) getViewer();
		return viewer.getTable();
	}

	protected Object transformToCellEditor(final Object value) {
		return (myNeedCastValueToStringForCellEditor) ? String.valueOf(value) : value;
	}

	protected Object transformFromCellEditor(final Object cellEditorValue) {
		if (!myNeedCastValueToStringForCellEditor) {
			return cellEditorValue;
		}
		if (false == cellEditorValue instanceof String) {
			return cellEditorValue;
		}
		if (!getDescriptor().getType().isPrimitive()) {
			return cellEditorValue;
		}
		final String stringValue = (String) cellEditorValue;
		final Class<?> descriptorType = getDescriptor().getType();
		try {
			if (double.class.equals(descriptorType)) {
				return Double.valueOf(stringValue);
			}
			if (int.class.equals(descriptorType)) {
				return Integer.valueOf(stringValue);
			}
			if (long.class.equals(descriptorType)) {
				return Long.valueOf(stringValue);
			}
			if (float.class.equals(descriptorType)) {
				return Float.valueOf(stringValue);
			}
			if (short.class.equals(descriptorType)) {
				return Short.valueOf(stringValue);
			}
			if (byte.class.equals(descriptorType)) {
				return Byte.valueOf(stringValue);
			}
			throw new UnsupportedOperationException("Primitive type is not suported: " + descriptorType);
		} catch (final NumberFormatException e) {
			return null;
		}
	}

	private IUndoContext getUndoContext() {
		return myTableModel.getUndoSupport().getUndoContext();
	}

	private IOperationHistory getOperationHistory() {
		return myTableModel.getUndoSupport().getOperationHistory();
	}
}
