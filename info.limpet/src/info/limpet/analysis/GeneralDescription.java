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
import info.limpet.impl.SampleData;
import info.limpet.operations.CollectionComplianceTests;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;

public abstract class GeneralDescription extends CoreAnalysis
{

  public GeneralDescription()
  {
    super("General Description");
  }

  private interface INumberFormatter
  {
    String format(double value);
  }

  private class MilliFormatter implements INumberFormatter
  {
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

    @Override
    public String format(double value)
    {
      return sdf.format(new Date((long) value));
    }
  }

  private class SecondFormatter implements INumberFormatter
  {
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

    @Override
    public String format(double value)
    {
      return sdf.format(new Date((long) (value / 1000d)));
    }
  }

  private class NumberFormatter implements INumberFormatter
  {
    @Override
    public String format(double value)
    {
      return "" + (int) value;
    }
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
        IDocument<?> thisC = (IDocument<?>) iter.next();

        // ok, avoid threading issues by extracting some items
        final DoubleDataset index =
            thisC.isIndexed() ? thisC.getIndexValues().clone() : null;

        titles.add("Collection");
        values.add(thisC.getName());
        titles.add("Size");
        values.add("" + thisC.size());
        titles.add("Quantity");
        values.add("" + thisC.isQuantity());
        titles.add("Indexed");
        values.add("" + thisC.isIndexed());
        if (thisC.isIndexed())
        {
          titles.add("Index units");
          final Unit<?> indexUnits = thisC.getIndexUnits();
          if (indexUnits == null)
          {
            System.err.println(thisC + " is missing index units");
            values.add("MISSING");
          }
          else
          {
            values.add("" + thisC.getIndexUnits().toString());
          }

          // check it has data
          if (thisC.size() > 0)
          {
            if (aTests.allOneDim(selection))
            {
              final double lower = index.get(0);
              final double upper = index.get(thisC.size() - 1);
              final INumberFormatter formatter;
              if (indexUnits != null)
              {
                // have a go at the index range
                if (indexUnits.equals(SI.SECOND))
                {
                  formatter = new SecondFormatter();
                }
                else if (indexUnits.equals(SampleData.MILLIS))
                {
                  formatter = new MilliFormatter();
                }
                else
                {
                  formatter = new NumberFormatter();
                }
              }
              else
              {
                formatter = new NumberFormatter();
              }

              titles.add("Index range");
              values.add(formatter.format(lower) + "-"
                  + formatter.format(upper));
            }
            else if (aTests.allTwoDim(selection))
            {
              // ok, ouput the index ranges for the two dimensions
            }
            else
            {
              // TODO: we need to output the index ranges for multi-dim datasets
            }
          }
        }
      }
    }

    if (titles.size() > 0)
    {
      presentResults(titles, values);
    }

  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    return aTests.allCollections(selection);
  }

  protected abstract void presentResults(List<String> titles,
      List<String> values);
}
