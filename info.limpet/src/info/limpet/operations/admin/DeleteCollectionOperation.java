/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.operations.AbstractCommand;

public class DeleteCollectionOperation implements IOperation {
	public static class DeleteCollection extends AbstractCommand {

		public DeleteCollection(final String title, final List<IStoreItem> selection, final IStoreGroup store,
				final IContext context) {
			super(title, "Delete specific collections", store, false, false, selection, context);
		}

		@Override
		public void execute() {
			// tell each series that we're a dependent
			final Iterator<IStoreItem> iter = getInputs().iterator();
			while (iter.hasNext()) {
				final IStoreItem iCollection = iter.next();
				iCollection.beingDeleted();
			}

			// and trigger a refresh
			getStore().fireDataChanged();
		}

		protected String getOutputName() {
			// special case, don't worry
			return null;
		}

		@Override
		protected void recalculate(final IStoreItem subject) {
			// don't worry
		}

	}

	@Override
	public List<ICommand> actionsFor(final List<IStoreItem> selection, final IStoreGroup destination,
			final IContext context) {
		final List<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection)) {
			final String commandTitle;
			if (selection.size() == 1) {
				commandTitle = "Delete collection";
			} else {
				commandTitle = "Delete collections";
			}
			final ICommand newC = new DeleteCollection(commandTitle, selection, destination, context);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(final List<IStoreItem> selection) {
		return selection.size() > 0;
	}

}
