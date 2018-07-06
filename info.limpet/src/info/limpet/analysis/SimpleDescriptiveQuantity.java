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

import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.Range;
import info.limpet.operations.CollectionComplianceTests;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.eclipse.january.dataset.DoubleDataset;

public abstract class SimpleDescriptiveQuantity extends CoreAnalysis
{

  public SimpleDescriptiveQuantity()
  {
    super("Quantity Analysis");
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

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
        NumberDocument o = (NumberDocument) iter.next();

        // output some high level data
        if (o.getUnits() != null)
        {
          titles.add("Units");
          values.add(o.getUnits().toString());
        }
        if (o.getUnits() != null && o.getUnits().getDimension() != null)
        {
          titles.add("Dimension");
          values.add(o.getUnits().getDimension().toString());
        }

        // if it's a singleton, show the value
        if (o.size() == 1)
        {
          titles.add("Value");
          values.add("" + o.getValueAt(0));
        }

        Range range = o.getRange();
        if (range != null)
        {
          titles.add("Range");
          values.add(range.getMinimum() + " - " + range.getMaximum() + " "
              + o.getUnits());

        }

        // we only bother with the stats if there are more than 1 item
        if (o.size() > 1)
        {
          // collate the values into an array
          DoubleDataset dd = (DoubleDataset) o.getDataset();;
          double[] data = dd.getData();
          
          // Get a DescriptiveStatistics instance
          DescriptiveStatistics stats = new DescriptiveStatistics(data);

          // output some basic overview stats
          titles.add("Min");
          values.add("" + format(stats.getMin()));
          titles.add("Max");
          values.add("" + format(stats.getMax()));
          titles.add("Mean");
          values.add("" + format(stats.getMean()));
          titles.add("Std");
          values.add("" + format(stats.getStandardDeviation()));
          titles.add("Median");
          values.add("" + format(stats.getPercentile(50)));
        }
      }
    }

    if (titles.size() > 0)
    {
      presentResults(titles, values);
    }

  }

  private String format(double val)
  {
    return new DecimalFormat("0.####").format(val);
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    return aTests.allCollections(selection) && aTests.allQuantity(selection);
  }

  protected abstract void presentResults(List<String> titles,
      List<String> values);
}
