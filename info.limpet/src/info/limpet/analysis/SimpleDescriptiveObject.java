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
package info.limpet.analysis;

import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.impl.LocationDocument;
import info.limpet.operations.CollectionComplianceTests;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SimpleDescriptiveObject extends CoreAnalysis
{

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public SimpleDescriptiveObject()
  {
    super("Object Analysis");
  }

  @Override
  public void analyse(List<IStoreItem> selection)
  {
    List<String> titles = new ArrayList<String>();
    List<String> values = new ArrayList<String>();

    // check compatibility
    if (appliesTo(selection) && selection.size() == 1)
    {
      // ok, let's go for it.
      for (Iterator<IStoreItem> iter = selection.iterator(); iter.hasNext();)
      {
        IDocument<?> thisD = (IDocument<?>) iter.next();

        // check it has some data
        if (thisD.size() > 0)
        {
          titles.add("Content Type");
          Object nextObject = thisD.getIterator().next();
          values.add(typeFor(nextObject, nextObject.getClass()));
          
          // is it a location?
          if(thisD instanceof LocationDocument)
          {
            LocationDocument locD = (LocationDocument) thisD;
            titles.add("Spatial units");
            values.add(locD.getUnits().toString());
          }
          
        }
      }
    }

    if (titles.size() > 0)
    {
      presentResults(titles, values);
    }
  }

  public String typeFor(Object subject, Object oClass)
  {
    String res = "un-recognised";

    if (oClass.equals(String.class))
    {
      res = "String";
    }
    else if (subject instanceof Point2D)
    {
      res = "Location";
    }

    return res;
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    return aTests.allCollections(selection) && aTests.allNonQuantity(selection)
        && aTests.allOneDim(selection);
  }

  protected abstract void presentResults(List<String> titles,
      List<String> values);
}
