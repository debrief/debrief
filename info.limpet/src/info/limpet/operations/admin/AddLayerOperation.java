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
import info.limpet.impl.StoreGroup;
import info.limpet.operations.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class AddLayerOperation implements IOperation
{

  @Override
  public List<ICommand> actionsFor(
      List<IStoreItem> selection, IStoreGroup destination, IContext context)
  {
    List<ICommand> res =
        new ArrayList<ICommand>();

    // note: we don't do "applies to" - we apply to everything

    final String thisTitle = "Add new folder";
    // hmm, see if a group has been selected
    ICommand newC = null;
    if (selection.size() == 1)
    {
      IStoreItem first = selection.get(0);
      if (first instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) first;
        newC = new AddLayerCommand(thisTitle, group, destination, context);
      }
    }

    if (newC == null)
    {
      newC = new AddLayerCommand(thisTitle, destination, context);
    }

    if (newC != null)
    {
      res.add(newC);
    }

    return res;
  }

  protected static class AddLayerCommand extends AbstractCommand
  {
    private IStoreGroup _group;

    public AddLayerCommand(String title, IStoreGroup store, IContext context)
    {
      super(title, "Add a new layer", store, false, false, null, context);
    }

    public AddLayerCommand(String title, IStoreGroup group, IStoreGroup store,
        IContext context)
    {
      this(title, store, context);
      _group = group;
    }

    @Override
    public void execute()
    {
      // get the String
      String string = getOutputName();

      if (string != null)
      {
        IStoreGroup newGroup = new StoreGroup(string);

        if (_group != null)
        {
          _group.add(newGroup);
        }
        else
        {
          getStore().add(newGroup);

        }
      }
      else
      {
    	  super.execute();
      }
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // don't worry
    }

    protected String getOutputName()
    {
      return getContext().getInput("Add layer", "Provide name for new folder",
          "");
    }

  }

}
