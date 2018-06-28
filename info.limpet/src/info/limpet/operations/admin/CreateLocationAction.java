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

import info.limpet.IContext;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.StoreGroup;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

import java.awt.geom.Point2D;

public class CreateLocationAction extends CreateSingletonGenerator
{
  public CreateLocationAction()
  {
    super("location", null);
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  /**
   * encapsulate creating a location into a command
   * 
   * @author ian
   * 
   */
  public static class CreateLocationCommand extends AbstractCommand
  {
    private StoreGroup _targetGroup;

    public CreateLocationCommand(String title, StoreGroup group,
        IStoreGroup store, IContext context)
    {
      super(title, "Create single location", store, false, false, null, context);
      _targetGroup = group;
    }

    @Override
    public void execute()
    {
      // get the name
      String seriesName =
          getContext().getInput("New fixed location",
              "Enter name for location", "");

      if (seriesName == null || seriesName.isEmpty())
      {
        return;
      }
      String strLat =
          getContext().getInput("New location",
              "Enter initial value for latitude", "");
      if (strLat == null || strLat.isEmpty())
      {
        return;
      }
      String strLong =
          getContext().getInput("New location",
              "Enter initial value for longitude", "");
      if (strLong == null || strLong.isEmpty())
      {
        return;
      }
      try
      {

        // add the new value
        double dblLat = Double.parseDouble(strLat);
        double dblLong = Double.parseDouble(strLong);


        LocationDocumentBuilder builder =
            new LocationDocumentBuilder(seriesName, this, null);
        Point2D newLoc =
            builder.getCalculator().createPoint(dblLong, dblLat);
        
        builder.add(newLoc);
        LocationDocument newData = builder.toDocument();

        // put the new collection in to the selected folder, or into root
        if (_targetGroup != null)
        {
          _targetGroup.add(newData);
        }
        else
        {
          // just store it at the top level
          IStoreGroup store = getStore();
          if (store != null)
          {
            store.add(newData);
          }
        }

      }
      catch (NumberFormatException e)
      {
        getContext().logError(IContext.Status.WARNING,
            "Failed to parse initial value", e);
        return;
      }
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // don't worry
    }

  }

  @Override
  protected AbstractCommand getCommand(IStoreGroup destination,
      IContext context, String thisTitle, StoreGroup group)
  {
    return new CreateLocationCommand(thisTitle, group, destination, context);
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

}
