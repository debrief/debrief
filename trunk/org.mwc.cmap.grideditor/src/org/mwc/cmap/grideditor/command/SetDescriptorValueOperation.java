package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.UnitsSet;
import org.mwc.cmap.gridharness.data.ValueInUnits;

import MWC.GUI.TimeStampedDataItem;


public class SetDescriptorValueOperation extends AbstractGridEditorOperation {

	private final boolean myFireRefresh;

	private final Object myNewValue;

	public SetDescriptorValueOperation(OperationEnvironment environment, Object newValue) {
		this(environment, newValue, true);
	}

	public SetDescriptorValueOperation(OperationEnvironment environment, Object newValue, boolean fireRefresh) {
		super(formatLabel(environment.getDescriptor()), environment);
		myNewValue = adjustValue(environment, newValue);
		myFireRefresh = fireRefresh;
		if (environment.getSubject() == null) {
			throw new IllegalArgumentException("I need a subject item");
		}
		if (environment.getDescriptor() == null) {
			throw new IllegalArgumentException("I need a descriptor to set value");
		}
	}

	@Override
	protected EnvironmentState computeBeforeExecutionState() {
		return new ElementHasValueState(getOperationEnvironment());
	}

	@Override
	protected EnvironmentState doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		TimeStampedDataItem subject = getOperationEnvironment().getSubject();
		BeanUtil.setItemValue(subject, getOperationEnvironment().getDescriptor(), myNewValue);
		EnvironmentState resultState = new ElementHasValueState(subject, getOperationEnvironment().getDescriptor());
		if (myFireRefresh) {
			getOperationEnvironment().getSeries().fireModified(subject);
		}
		return resultState;
	}

	@Override
	protected void doUndo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		TimeStampedDataItem subject = getOperationEnvironment().getSubject();
		Object oldValue = getStateBeforeFirstRun().getValue();
		BeanUtil.setItemValue(subject, getOperationEnvironment().getDescriptor(), oldValue);
		if (myFireRefresh) {
			getOperationEnvironment().getSeries().fireModified(subject);
		}
	}

	@Override
	protected ElementHasValueState getStateBeforeFirstRun() {
		return (ElementHasValueState) super.getStateBeforeFirstRun();
	}

	protected static class ElementHasValueState implements EnvironmentState {

		private final Object myValue;

		private final TimeStampedDataItem myItem;

		public ElementHasValueState(OperationEnvironment context) {
			this(context.getSubject(), context.getDescriptor());
		}

		public ElementHasValueState(TimeStampedDataItem item, GriddableItemDescriptor descriptor) {
			myItem = item;
			myValue = BeanUtil.getItemValue(item, descriptor);
		}

		@Override
		public boolean isCompatible(OperationEnvironment environment) {
			return safeEquals(myValue, BeanUtil.getItemValue(environment.getSubject(), environment.getDescriptor()));
		}

		private boolean safeEquals(Object o1, Object o2) {
			return o1 == null ? o2 == null : o1.equals(o2);
		}

		public Object getValue() {
			return myValue;
		}

		public TimeStampedDataItem getItem() {
			return myItem;
		}

	}

	private static String formatLabel(GriddableItemDescriptor descriptor) {
		return NLS.bind("Setting {0} value", descriptor == null ? "" : descriptor.getTitle());
	}

	private static Object adjustValue(OperationEnvironment environment, Object newValue) {
		if (ValueInUnits.class.isAssignableFrom(environment.getDescriptor().getType()) && newValue instanceof Number) {
			ValueInUnits currentValue = BeanUtil.getItemValue(environment.getSubject(), environment.getDescriptor(), ValueInUnits.class);
			if (currentValue != null) {
				ValueInUnits copy = currentValue.makeCopy();
				UnitsSet.Unit chartUnit = copy.getUnitsSet().getMainUnit();
				copy.setValues(((Number) newValue).doubleValue(), chartUnit);
				return copy;
			}
		}
		return newValue;
	}

}
