package org.mwc.cmap.grideditor.command;

import org.eclipse.core.commands.ExecutionException;

import MWC.GUI.TimeStampedDataItem;


/**
 * Represents the state of the {@link OperationEnvironment} at the some point of
 * time, typically, before or after some command have been executed at the first
 * time.
 * 
 * When operation calculates canUndo() or canRedo() state, it can check that
 * important aspects of the environment are compatible to the state before (for
 * redo) or after (for undo) the original execution.
 * 
 * Say, the precondition state for change-value operation may consist of the
 * subject {@link TimeStampedDataItem}, and the old value for some {@link
 * GriddableItemDescriptor}. If the command is being redone when the actual
 * value in environment is not the same as expected, than something goes wrong
 * in undo subsystem, and it make sense to block the redo() execution.
 */
public interface EnvironmentState {

	public boolean isCompatible(OperationEnvironment environment);

	public static final EnvironmentState ALWAYS_COMPATIBLE = new EnvironmentState() {

		public boolean isCompatible(OperationEnvironment environment) {
			return true;
		}
	};

	/**
	 * Simple state that consists of (and checks for) the size of series only.
	 */
	public static class SeriesOfKnownSize implements EnvironmentState {

		private final int myItemsCountBefore;

		public SeriesOfKnownSize(OperationEnvironment environment) {
			myItemsCountBefore = environment.getSeries().getItems().size();
		}

		public boolean isCompatible(OperationEnvironment environment) {
			return myItemsCountBefore == environment.getSeries().getItems().size();
		}
	}

	/**
	 * Helper state that consists of (and checks for) an item at some known
	 * position.
	 */
	public class ItemAtKnownPosition implements EnvironmentState {

		private final int myActualPosition;

		private final TimeStampedDataItem myItem;

		public ItemAtKnownPosition(OperationEnvironment environment, int expectedPosition) throws ExecutionException {
			this(environment.getSeries().getItems().get(expectedPosition), expectedPosition);
			if (myItem != environment.getSubject()) {
				throw new ExecutionException("Can't find subject element at position: " + myActualPosition);
			}
		}

		public ItemAtKnownPosition(OperationEnvironment environment) {
			this(environment.getSubject(), environment.getSeries().getItems().indexOf(environment.getSubject()));
		}

		private ItemAtKnownPosition(TimeStampedDataItem item, int position) {
			myItem = item;
			myActualPosition = position;
		}

		public boolean isCompatible(OperationEnvironment environment) {
			return myActualPosition == environment.getSeries().getItems().indexOf(myItem);
		}
		
		public int getPosition() {
			return myActualPosition;
		}
	}
}
