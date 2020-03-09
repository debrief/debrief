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
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

public class GenerateDummyDataOperation implements IOperation {
	public static class GenerateDummyDataCommand extends AbstractCommand {
		private final long _count;

		public GenerateDummyDataCommand(final String title, final IStoreGroup store, final long count,
				final IContext context) {
			super(title, "Create some sample data", store, false, false, null, context);
			_count = count;
		}

		@Override
		public void execute() {
			final StoreGroup newData = new SampleData().getData(_count);
			final Iterator<IStoreItem> iter = newData.iterator();
			while (iter.hasNext()) {
				final IStoreItem iCollection = iter.next();
				getStore().add(iCollection);
			}
		}

		@Override
		protected void recalculate(final IStoreItem subject) {
			// don't worry
		}

	}

	private final CollectionComplianceTests aTests = new CollectionComplianceTests();

	private final String _title;

	private final long _count;

	public GenerateDummyDataOperation(final String title, final long count) {
		_title = title;
		_count = count;
	}

	@Override
	public List<ICommand> actionsFor(final List<IStoreItem> selection, final IStoreGroup destination,
			final IContext context) {
		final List<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection)) {
			final String thisTitle = "Generate " + _title + " dataset (" + _count + ")";
			final ICommand newC = new GenerateDummyDataCommand(thisTitle, destination, _count, context);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(final List<IStoreItem> selection) {
		final boolean emptySelection = aTests.exactNumber(selection, 0);
		return emptySelection;
	}

}
