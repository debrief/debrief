/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/

package MWC.GUI.Properties.Swing;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import MWC.GUI.Editable.CategorisedPropertyDescriptor;

public class TableSortDecorator extends TableModelDecorator implements
    TableModelListener
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  int indexes[];

  public TableSortDecorator(final TableModel m)
  {
    super(m);

    if (m == null || !(m instanceof AbstractTableModel))
      throw new IllegalArgumentException("bad model");

    m.addTableModelListener(this);
    allocate();
  }

  private void allocate()
  {
    indexes = new int[model.getRowCount()];

    for (int i = 0; i < indexes.length; ++i)
    {
      indexes[i] = i;
    }
  }

  public int compare(final int i, final int j, final int column)
  {
    final Object io = model.getValueAt(i, column);
    final Object jo = model.getValueAt(j, column);

    String iStr = null;
    String jStr = null;
    // if these are property descriptors, use the featureDescriptor as the
    // comparison string
    if (io instanceof java.beans.PropertyDescriptor)
    {
      final java.beans.PropertyDescriptor iPd =
          (java.beans.PropertyDescriptor) io;
      iStr = iPd.getDisplayName();
      final java.beans.PropertyDescriptor jPd =
          (java.beans.PropertyDescriptor) jo;
      jStr = jPd.getDisplayName();

      if (io instanceof CategorisedPropertyDescriptor)
      {
        final CategorisedPropertyDescriptor catI =
            (CategorisedPropertyDescriptor) io;
        iStr = catI.getCategory() + iStr;
      }
      if (jo instanceof CategorisedPropertyDescriptor)
      {
        final CategorisedPropertyDescriptor catJ =
            (CategorisedPropertyDescriptor) jo;
        jStr = catJ.getCategory() + jStr;
      }
    }
    else
    {
      iStr = io.toString();
      jStr = jo.toString();
    }

    // int c = jo.toString().compareTo(io.toString());
    final int c = jStr.compareTo(iStr);
    return (c < 0) ? -1 : ((c > 0) ? 1 : 0);
  }

  @Override
  public Object getValueAt(final int row, final int column)
  {
    return super.getValueAt(indexes[row], column);
  }

  public void sort(final int column)
  {
    final int rowCount = model.getRowCount();

    for (int i = 0; i < rowCount; i++)
    {
      for (int j = i + 1; j < rowCount; j++)
      {
        if (compare(indexes[i], indexes[j], column) < 0)
        {
          swap(i, j);
        }
      }
    }
    fireTableStructureChanged();
  }

  public void swap(final int i, final int j)
  {
    final int tmp = indexes[i];
    indexes[i] = indexes[j];
    indexes[j] = tmp;
  }

  @Override
  public void tableChanged(final TableModelEvent e)
  {
    allocate();
  }
}
