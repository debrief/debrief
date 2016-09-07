/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.NarrativeViewer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.NarrativeViewer.Column.VisibilityListener;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;

public class NarrativeViewerModel
{
  private static final NarrativeEntry[] NO_ENTRIES = new NarrativeEntry[0];
  protected static final org.eclipse.swt.graphics.Color SWT_WHITE =
      new org.eclipse.swt.graphics.Color(Display.getCurrent(), 255, 255, 254);

  private final ColumnVisible myColumnVisible;
  private final ColumnTime myColumnTime;
  private final ColumnSource myColumnSource;
  private final ColumnType myColumnType;
  private final ColumnEntry myColumnEntry;
  private final AbstractColumn[] myAllColumns;

  private final ColumnFilter mySourceFilter;
  private final ColumnFilter myTypeFilter;

  // private Map<Color, KTableCellRenderer> renderers = new HashMap<Color, KTableCellRenderer>();
  // private List<org.eclipse.swt.graphics.Color> swtColors = new
  // ArrayList<org.eclipse.swt.graphics.Color>();

  final LinkedList<NarrativeEntry> myVisibleRows =
      new LinkedList<NarrativeEntry>();
  private NarrativeEntry[] myAllEntries = NO_ENTRIES;

  private IRollingNarrativeProvider myInput;

  public NarrativeViewerModel(final IPreferenceStore store)
  {

    myColumnVisible = new ColumnVisible(store);
    myColumnVisible.setVisible(false);
    myColumnTime = new ColumnTime(store);
    myColumnSource = new ColumnSource(store);
    myColumnType = new ColumnType(store);
    myColumnEntry = new ColumnEntry(store);
    myAllColumns = new AbstractColumn[]
    {
        //
        myColumnVisible, //
        myColumnTime, //
        myColumnSource, //
        myColumnType, //
        myColumnEntry, //
    };

    mySourceFilter = new ColumnFilter()
    {
      public String getFilterValue(final NarrativeEntry entry)
      {
        return entry.getTrackName();
      }

      protected void valuesSetChanged()
      {
        updateFilters();
      }
    };
    myTypeFilter = new ColumnFilter()
    {
      public String getFilterValue(final NarrativeEntry entry)
      {
        return entry.getType();
      }

      protected void valuesSetChanged()
      {
        updateFilters();
      }
    };

    myColumnSource.setFilter(mySourceFilter);
    myColumnType.setFilter(myTypeFilter);

    
  }

  public void setInput(final IRollingNarrativeProvider entryWrapper)
  {
    myInput = entryWrapper;
    myAllEntries = null;

    if (entryWrapper != null)
    {
      // check it has some data.
      final NarrativeEntry[] entries =
          entryWrapper.getNarrativeHistory(new String[]
          {});
      if (entries != null)
        myAllEntries = entries;
      else
        myAllEntries = null;
    }
    updateFilters();
  }

  public IRollingNarrativeProvider getInput()
  {
    return myInput;
  }

  public void setTimeFormatter(final TimeFormatter timeFormatter)
  {
    myColumnTime.setTimeFormatter(timeFormatter);
  }

  public boolean isWrappingEntries()
  {
    return myColumnEntry.isWrapping();
  }

  public boolean setWrappingEntries(final boolean shouldWrap)
  {
    return myColumnEntry.setWrapping(shouldWrap);
  }

  void updateFilters()
  {
    myVisibleRows.clear();
    if (!hasInput())
    {
      return;
    }
    for (final NarrativeEntry entry : myAllEntries)
    {
      if (mySourceFilter.accept(entry) && myTypeFilter.accept(entry))
      {
        myVisibleRows.add(entry);
      }
    }
  }

 

  

  public Column getColumnSource()
  {
    return myColumnSource;
  }

  public Column getColumnType()
  {
    return myColumnType;
  }

  public ColumnEntry getColumnEntry()
  {
    return myColumnEntry;
  }

  public ColumnTime getColumnTime()
  {
    return myColumnTime;
  }

  public ColumnVisible getColumnVisible()
  {
    return myColumnVisible;
  }

  public boolean hasInput()
  {
    return myAllEntries != null;
  }

  public boolean isColumnResizable(final int col)
  {
    return true;
  }

 

  private static abstract class AbstractTextColumn extends AbstractColumn
  {
    public AbstractTextColumn(final int index, final String name,
        final IPreferenceStore store)
    {
      super(index, name, store);
    }

    @Override
    protected ColumnLabelProvider createRenderer()
    {
      return new ColumnLabelProvider()
      {

        private Map<java.awt.Color, Color> swtColorMap =
            new HashMap<java.awt.Color, Color>();

        @Override
        public void dispose()
        {
          for (Color color : swtColorMap.values())
          {
            color.dispose();
          }
          super.dispose();
        }

        @Override
        public Color getForeground(Object element)
        {
          if (element instanceof NarrativeEntry)
          {
            NarrativeEntry entry = (NarrativeEntry) element;
            java.awt.Color color = entry.getColor();
            Color swtColor = swtColorMap.get(color);
            if (swtColor == null || !swtColor.isDisposed())
            {
              swtColor =
                  new Color(Display.getCurrent(), color.getRed(), color
                      .getBlue(), color.getGreen());
              swtColorMap.put(color, swtColor);
            }

            return swtColor;
          }
          return super.getForeground(element);
        }

        @Override
        public String getText(Object element)
        {
          if (element instanceof NarrativeEntry)
          {
            NarrativeEntry entry = (NarrativeEntry) element;
            return (String) getProperty(entry);
          }
          return super.getText(element);
        }

      };
    }
  }

  private static class ColumnVisible extends AbstractColumn
  {

    private static final String CHECKED_KEY = "CHECKED";
    private static final String UNCHECK_KEY = "UNCHECKED";

    private Image makeShot( boolean type)
    {
      Shell s = new Shell(Display.getDefault(), SWT.NO_TRIM);
      Button b = new Button(s, SWT.CHECK);
      b.setSelection(type);
      Point bsize = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
      b.setSize(bsize);
      b.setLocation(0, 0);
      s.setSize(bsize);
      s.open();

      GC gc = new GC(b);
      Image image = new Image(Display.getDefault(), bsize.x, bsize.y);
      gc.copyArea(image, 0, 0);
      gc.dispose();

      s.close();

      return image;
    }

    public ColumnVisible(final IPreferenceStore store)
    {
      super(0, "Visible", store);
      if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null)
      {
        JFaceResources.getImageRegistry().put(UNCHECK_KEY,
            makeShot( false));
        JFaceResources.getImageRegistry().put(CHECKED_KEY,
            makeShot( true));
      }
    }

    public Object getProperty(final NarrativeEntry entry)
    {
      return entry.getVisible();
    }

    // public KTableCellEditor getCellEditor()
    // {
    // final Rectangle imgBounds = CheckableCellRenderer.IMAGE_CHECKED
    // .getBounds();
    // final Point sensible = new Point(imgBounds.width, imgBounds.height);
    // return new KTableCellEditorCheckbox2(sensible,
    // SWTX.ALIGN_HORIZONTAL_CENTER, SWTX.ALIGN_VERTICAL_CENTER);
    // }

    @Override
    protected ColumnLabelProvider createRenderer()
    {
      return new ColumnLabelProvider()
      {

        @Override
        public Image getImage(Object element)
        {
          if (element instanceof NarrativeEntry)
          {
            NarrativeEntry entry = (NarrativeEntry) element;

            if (entry.getVisible())
            {
              return JFaceResources.getImageRegistry().getDescriptor(
                  CHECKED_KEY).createImage();
            }
            else
            {
              return JFaceResources.getImageRegistry().getDescriptor(
                  UNCHECK_KEY).createImage();
            }
          }
          return super.getImage(element);
        }

      };
    }
  }

  private static class ColumnTime extends AbstractTextColumn
  {
    private TimeFormatter myTimeFormatter = DEFAULT_TIME;

    public ColumnTime(final IPreferenceStore store)
    {
      super(1, "Time", store);
    }

    public Object getProperty(final NarrativeEntry entry)
    {
      return myTimeFormatter.format(entry.getDTG());
    }

    public void setTimeFormatter(final TimeFormatter formatter)
    {
      myTimeFormatter = formatter;
    }
  }

  private static class ColumnSource extends AbstractTextColumn
  {
    public ColumnSource(final IPreferenceStore store)
    {
      super(2, "Source", store);
    }

    public Object getProperty(final NarrativeEntry entry)
    {
      return entry.getTrackName();
    }
  }

  private static class ColumnType extends AbstractTextColumn
  {
    public ColumnType(final IPreferenceStore store)
    {
      super(3, "Type", store);
    }

    public Object getProperty(final NarrativeEntry entry)
    {
      return entry.getType();
    }
  }

  private static class ColumnEntry extends AbstractTextColumn
  {
    private boolean myIsWrapping = true;

    public ColumnEntry(final IPreferenceStore store)
    {
      super(4, "Entry", store);
    }

    public boolean setWrapping(final boolean shouldWrap)
    {
      final boolean changed = myIsWrapping ^ shouldWrap;
      if (changed)
      {
        myIsWrapping = shouldWrap;

      }
      return changed;
    }

    public boolean isWrapping()
    {
      return myIsWrapping;
    }

    public Object getProperty(final NarrativeEntry entry)
    {
      return entry.getEntry();
    }

  }

  static TimeFormatter DEFAULT_TIME = new TimeFormatter()
  {
    public String format(final HiResDate time)
    {
      return time.toString();
      // Calendar calendar = Calendar.getInstance();
      // calendar.setTimeInMillis(time);
      // SimpleDateFormat simpleDateFormat = new
      // SimpleDateFormat(DEFAULT_TIME_FORMAT);
      // return simpleDateFormat.format(calendar.getTime());
    }
  };

  public void createTable(TableViewer viewer)
  {
    TableViewerColumnFactory factory = new TableViewerColumnFactory(viewer);
    viewer.setContentProvider(new IStructuredContentProvider()
    {
      
      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
      {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void dispose()
      {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public Object[] getElements(Object inputElement)
      {
        return myAllEntries;
      }
    });
    
    {
      
      for (AbstractColumn column : myAllColumns)
      {
        final TableViewerColumn viewerColumn = factory.createColumn(column.getColumnName(), column.getColumnWidth(), column.createRenderer());
       
        column.addVisibilityListener(new VisibilityListener()
        {
          
          @Override
          public void columnVisibilityChanged(Column column, boolean actualIsVisible)
          {
            // TODO Auto-generated method stub
            
          }
        });
      }
      
    }
    
    
    
  }

}
