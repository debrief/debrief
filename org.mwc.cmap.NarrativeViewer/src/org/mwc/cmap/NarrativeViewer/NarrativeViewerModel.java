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
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
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
  private final TextFilter textFilter;

  // private Map<Color, KTableCellRenderer> renderers = new HashMap<Color, KTableCellRenderer>();
  // private List<org.eclipse.swt.graphics.Color> swtColors = new
  // ArrayList<org.eclipse.swt.graphics.Color>();

  final LinkedList<NarrativeEntry> myVisibleRows =
      new LinkedList<NarrativeEntry>();
  private NarrativeEntry[] myAllEntries = NO_ENTRIES;

  private IRollingNarrativeProvider myInput;

  public AbstractColumn[] getAllColumns()
  {
    return myAllColumns;
  }

  public NarrativeViewerModel(final IPreferenceStore store,TextFilter textFilter)
  {

    this.textFilter = textFilter;
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
      if (mySourceFilter.accept(entry) && myTypeFilter.accept(entry) && textFilter.accept(entry))
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
    private static final Color BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
    private static final Color WHITE = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

    public AbstractTextColumn(final int index, final String name,
        final IPreferenceStore store)
    {
      super(index, name, store);
    }

    @Override
    protected CellLabelProvider createRenderer(ColumnViewer viewer)
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
                      .getGreen(), color.getBlue());
              swtColorMap.put(color, swtColor);
            }

            if(swtColor.getRGB().equals(WHITE.getRGB()))
            {
              return BLACK;
            }
            return swtColor;
          }
          return BLACK;
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

    public ColumnVisible(final IPreferenceStore store)
    {
      super(0, "Visible", store);

    }

    public Object getProperty(final NarrativeEntry entry)
    {
      return entry.getVisible();
    }

    @Override
    public void setProperty(NarrativeEntry entry, Object obj)
    {
      entry.setVisible((Boolean) obj);
    }

    @Override
    public int getColumnWidth()
    {
      return 20;
    }

    @Override
    public CellEditor getCellEditor(Grid table)
    {
      CheckboxCellEditor checkboxCellEditor = new CheckboxCellEditor(table);

      return checkboxCellEditor;
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
    protected ColumnLabelProvider createRenderer(ColumnViewer viewer)
    {
      return new ColumnLabelProvider()
      {

        @Override
        public String getText(Object element)
        {
          return getProperty((NarrativeEntry) element).toString();
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

    @Override
    public int getColumnWidth()
    {
      return 50;
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

    @Override
    public int getColumnWidth()
    {
      return 40;
    }

    @Override
    protected void columnSelection(NarrativeViewer viewer)
    {
      viewer.showFilterDialog(this);
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

    @Override
    public int getColumnWidth()
    {
      return 40;
    }

    @Override
    protected void columnSelection(NarrativeViewer viewer)
    {
      viewer.showFilterDialog(this);
    }
  }

  private static class ColumnEntry extends AbstractTextColumn
  {
    private boolean myIsWrapping = true;

    public ColumnEntry(final IPreferenceStore store)
    {
      super(4, "Entry", store);
    }

    @Override
    public int getColumnWidth()
    {
      return 250;
    }
    
    @Override
    public boolean isColumnWidthExpand()
    {
      return true;
    }

    @Override
    public boolean isWrap()
    {
      return true;
    }
    
    public boolean isWrapSupport()
    {
      return true;
    }
    
    
//    @Override
//    public CellEditor getCellEditor(Grid table)
//    {
//      return  new TextCellEditor(table,
//          SWT.WRAP);
//    }
    
    
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
      return entry.getEntry()==null?"":entry.getEntry();
    }

//    @Override
//    protected CellLabelProvider createRenderer(final ColumnViewer viewer)
//    {
//      return new OwnerDrawLabelProvider()
//      {
//
//        private int defultSize = 0;
//        private static final int TEXT_MARGIN = 3;
//        private GC m_LastGCFromExtend;
//        private Map<String, Point> m_StringExtentCache =
//            new HashMap<String, Point>();
//
//        public synchronized Point getCachedStringExtent(GC gc, String text)
//        {
//          if (m_LastGCFromExtend != gc)
//          {
//            m_StringExtentCache.clear();
//            m_LastGCFromExtend = gc;
//          }
//          Point p = (Point) m_StringExtentCache.get(text);
//          if (p == null)
//          {
//            if (text == null)
//              return new Point(0, 0);
//            p = gc.textExtent(text);
//            m_StringExtentCache.put(text, p);
//          }
//          return new Point(p.x, p.y);
//        }
//
//        public String wrapText(GC gc, String text, int width)
//        {
//          Point textSize = getCachedStringExtent(gc, text);
//          if (textSize.x > width)
//          {
//            StringBuffer wrappedText = new StringBuffer();
//            String[] lines = text.split("\n");
//            int cutoffLength =
//                width / gc.getFontMetrics().getAverageCharWidth();
//            if (cutoffLength < 3)
//              return text;
//            for (int i = 0; i < lines.length; i++)
//            {
//              int breakOffset = 0;
//              while (breakOffset < lines[i].length())
//              {
//                String lPart =
//                    lines[i].substring(breakOffset, Math.min(breakOffset
//                        + cutoffLength, lines[i].length()));
//                Point lineSize = getCachedStringExtent(gc, lPart);
//                while ((lPart.length() > 0) && (lineSize.x >= width))
//                {
//                  lPart = lPart.substring(0, Math.max(lPart.length() - 1, 0));
//                  lineSize = getCachedStringExtent(gc, lPart);
//                }
//                wrappedText.append(lPart);
//                breakOffset += lPart.length();
//                wrappedText.append('\n');
//              }
//            }
//            return wrappedText.substring(0, Math.max(wrappedText.length() - 1,
//                0));
//          }
//          else
//            return text;
//
//        }
//
//        @Override
//        protected void measure(final Event event, final Object element)
//        {
//          defultSize = Math.min(defultSize, event.y);
//          String property = getProperty((NarrativeEntry) element).toString();
//          event.width =
//              ((TableViewer) viewer).getTable().getColumn(event.index).getWidth();
//          if (event.width == 0 || !isWrapping())
//            return;
//          
//          if(isWrapping())
//          {
//          String text = isWrapping()? wrapText(event.gc, property, event.width):property;
//          final Point size =
//              event.gc.textExtent(text);
//          event.height = Math.max(event.height, size.y );
//          }
//          else
//          {
//            event.y = defultSize;
//          }
//
//          
//        }
//
//        
//        private Map<java.awt.Color, Color> swtColorMap =
//            new HashMap<java.awt.Color, Color>();
//
//        @Override
//        public void dispose()
//        {
//          for (Color color : swtColorMap.values())
//          {
//            color.dispose();
//          }
//          super.dispose();
//        }
//
//      
//        public Color getForeground(Object element)
//        {
//          if (element instanceof NarrativeEntry)
//          {
//            NarrativeEntry entry = (NarrativeEntry) element;
//            java.awt.Color color = entry.getColor();
//            Color swtColor = swtColorMap.get(color);
//            if (swtColor == null || !swtColor.isDisposed())
//            {
//              swtColor =
//                  new Color(Display.getCurrent(), color.getRed(), color
//                      .getGreen(), color.getBlue());
//              swtColorMap.put(color, swtColor);
//            }
//
//            return swtColor;
//          }
//          return null;
//        }
//        
//        @Override
//        protected void paint(final Event event, final Object element)
//        {
//          if((event.detail & SWT.SELECTED) == 0)
//          {
//            Color foreground = getForeground(element);
//            if(foreground!=null)
//            {
//              event.gc.setForeground(foreground);
//            }
//          }
//          
//          String property = getProperty((NarrativeEntry) element).toString();
//          int width =
//              ((TableViewer) viewer).getTable().getColumn(event.index).getWidth();
//          String text = isWrapping() ?wrapText(event.gc, property, width):property;
//          event.gc.drawText(text, event.x
//              + TEXT_MARGIN, event.y, true);
//        }
//
//        @Override
//        protected void erase(Event event, Object element)
//        {
//
//        }
//      };
//    }

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


  
  
  public void createTable(final NarrativeViewer viewer,
      final GridColumnLayout layout)
  {
    viewer.getViewer().setItemCount(0);
    TableViewerColumnFactory factory =
        new TableViewerColumnFactory(viewer.getViewer());
    viewer.getViewer().setContentProvider(new ILazyContentProvider()
    {

      Object [] elements ;
      private GridTableViewer gridTableViewer;
      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
      {

        elements =  myVisibleRows == null ? NO_ENTRIES : myVisibleRows.toArray();
        gridTableViewer = (GridTableViewer) viewer;
        gridTableViewer.setItemCount(0);
        gridTableViewer.setItemCount(elements.length);
      }

      @Override
      public void dispose()
      {

      }

//      @Override
//      public Object[] getElements(Object inputElement)
//      {
//        return ;
//      }
//
//      @Override
//      public Object[] getChildren(Object parentElement)
//      {
//
//        return new Object[0];
//      }
//
//      @Override
//      public Object getParent(Object element)
//      {
//
//        return null;
//      }
//
//      @Override
//      public boolean hasChildren(Object element)
//      {
//        return false;
//      }

      @Override
      public void updateElement(int index)
      {
        gridTableViewer.replace(elements[index], index);
        
      }
    });

    {
      
      
      for (final AbstractColumn column : myAllColumns)
      {
        CellLabelProvider cellRenderer = column.getCellRenderer(viewer.getViewer());
        
       
        final GridViewerColumn viewerColumn =
            factory.createColumn(column.getColumnName(), column
                .getColumnWidth(), cellRenderer,column.isWrap());

        
        final GridColumn gridColumn = viewerColumn.getColumn();
        FilterTextCellRenderer styledTextCellRenderer =
            new FilterTextCellRenderer()
            {

              @Override
              protected String getFilterText()
              {
                return viewer.getFilterGrid().getFilterString();
              }
            };
        styledTextCellRenderer.setWordWrap(column.isWrap());
        gridColumn.setCellRenderer(styledTextCellRenderer);
       
        gridColumn.addSelectionListener(new SelectionAdapter()
        {
          @Override
          public void widgetSelected(SelectionEvent e)
          {
            column.columnSelection(viewer);
          }
        });
        final CellEditor cellEditor =
            column.getCellEditor(viewer.getViewer().getGrid());
        if (cellEditor != null)
          viewerColumn.setEditingSupport(new EditingSupport(viewer.getViewer())
          {

            @Override
            protected void setValue(Object element, Object value)
            {
              column.setProperty((NarrativeEntry) element, value);
            }

            @Override
            protected Object getValue(Object element)
            {
              return column.getProperty((NarrativeEntry) element);
            }

            @Override
            protected CellEditor getCellEditor(Object element)
            {
              return cellEditor;
            }

            @Override
            protected boolean canEdit(Object element)
            {
              return cellEditor != null;
            }
          });
        column.addVisibilityListener(new VisibilityListener()
        {

          @Override
          public void columnVisibilityChanged(Column column,
              boolean actualIsVisible)
          {
            gridColumn.setVisible(column.isVisible());

            if(column.isVisible())
            {
              layout.setColumnData(gridColumn, new ColumnWeightData(
                  column.getColumnWidth()));
            }
            else
            {
              layout.setColumnData(gridColumn, new ColumnWeightData(
                  0));
            }
          }
        });
        layout.setColumnData(gridColumn, new ColumnWeightData(
            column.getColumnWidth(),column.isColumnWidthExpand()));

        if (!column.isVisible())
        {
          gridColumn.setVisible(column.isVisible());
          layout.setColumnData(gridColumn, new ColumnWeightData(
              0));
        }

      }

    }

  }

}
