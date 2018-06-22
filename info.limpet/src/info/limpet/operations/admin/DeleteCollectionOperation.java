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

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.operations.AbstractCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeleteCollectionOperation implements IOperation
{
	public List<ICommand> actionsFor(
			List<IStoreItem> selection, IStoreGroup destination, IContext context)
	{
	  List<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection))
		{
			final String commandTitle;
			if (selection.size() == 1)
			{
				commandTitle = "Delete collection";
			}
			else
			{
				commandTitle = "Delete collections";
			}
			ICommand newC = new DeleteCollection(commandTitle,
					selection, destination, context);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<IStoreItem> selection)
	{
		return selection.size() > 0;
	}

	public static class DeleteCollection extends AbstractCommand
	{

		public DeleteCollection(String title, List<IStoreItem> selection,
				IStoreGroup store, IContext context)
		{
			super(title, "Delete specific collections", store, false, false, selection,
					context);
		}

		@Override
		public void execute()
		{
			// tell each series that we're a dependent
			Iterator<IStoreItem> iter = getInputs().iterator();
			while (iter.hasNext())
			{
				IStoreItem iCollection = iter.next();
				iCollection.beingDeleted();
			}
			
			// and trigger a refresh
			getStore().fireDataChanged();
		}

		@Override
		protected void recalculate(IStoreItem subject)
		{
			// don't worry
		}

		protected String getOutputName()
		{
			// special case, don't worry
			return null;
		}

	}

}
