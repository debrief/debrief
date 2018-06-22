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
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.operations.AbstractCommand;
import info.limpet.persistence.csv.CsvGenerator;

import java.util.ArrayList;
import java.util.List;

public class CopyCsvToClipboardAction implements IOperation
{

  /**
   * encapsulate command
   * 
   * @author ian
   * 
   */
  public static class CopyCsvToClipboardCommand extends
      AbstractCommand
  {
    private List<IStoreItem> _selection;

    public static String getCsvString(List<IStoreItem> selection)
    {
      if (selection.size() == 1 && selection.get(0) instanceof IDocument)
      {
        return CsvGenerator.generate(selection.get(0));
      }
      return null;
    }

    public CopyCsvToClipboardCommand(String title, List<IStoreItem> selection,
        IStoreGroup store, IContext context)
    {
      super(title, "Export selection to clipboard as CSV", store, false, false,
          null, context);
      _selection = selection;
    }

    @Override
    public void execute()
    {
      String csv = getCsvString(_selection);
      if (csv != null && !csv.isEmpty())
      {
        getContext().placeOnClipboard(csv);
      }
      else
      {
        getContext().openInformation("Data Manager Editor",
            "Cannot copy current selection");
      }
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // don't worry
    }
  }

  public List<ICommand> actionsFor(
      List<IStoreItem> selection, IStoreGroup destination, IContext context)
  {
    List<ICommand> res =
        new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      // hmm, see if we have a single collection selected
      ICommand newC = null;
      if (selection.size() == 1)
      {
        newC =
            new CopyCsvToClipboardCommand("Copy CSV to clipboard", selection,
                destination, context);
        res.add(newC);
      }
    }

    return res;
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    return selection.size() == 1 && selection.get(0) instanceof IDocument;
  }

}
