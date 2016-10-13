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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.NarrativeViewer.Column.VisibilityListener;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;
import org.mwc.cmap.NarrativeViewer.preferences.NarrativeViewerPrefsPage;
import org.mwc.cmap.core.CorePlugin;

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
  private final EntryFilter textFilter;

  final LinkedList<NarrativeEntry> myVisibleRows =
      new LinkedList<NarrativeEntry>();
  private NarrativeEntry[] myAllEntries = NO_ENTRIES;

  private IRollingNarrativeProvider myInput;

  public AbstractColumn[] getAllColumns()
  {
    return myAllColumns;
  }

  static final Color MATCH_YELLOW = new Color(Display.getDefault(), 255, 251,
      204);

  static final Color[] PHRASES_COLORS = new Color[]
  {new Color(Display.getDefault(), 178, 180, 255),
      new Color(Display.getDefault(), 147, 232, 207),
      new Color(Display.getDefault(), 232, 205, 167),
      new Color(Display.getDefault(), 255, 192, 215),};

  private final Styler SEARCH_STYLE = new Styler()
  {

    @Override
    public void applyStyles(TextStyle textStyle)
    {
      textStyle.background = MATCH_YELLOW;

    }
  };
  private IPreferenceStore store;

  public NarrativeViewerModel(final IPreferenceStore store,
      EntryFilter textFilter)
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
    this.store = store;

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

  protected String[] getPhrases()
  {
    String phrasesText = store.getString(NarrativeViewerPrefsPage.PreferenceConstants.HIGHLIGHT_PHRASES);

    if(phrasesText!=null && !phrasesText.trim().isEmpty())
    {
      String[] split = phrasesText.split(",");
      String[] phrases = new String[split.length];
      for (int i = 0; i < phrases.length; i++)
      {
        phrases[i] = split[i].trim().toLowerCase();
        
      }
      return phrases;
    }
    
    
    return new String[]{};
  }

  protected Styler[] getPhraseStyles()
  {
    Styler[] stylers = new Styler[getPhrases().length];

    for (int i = 0; i < stylers.length; i++)
    {
      final Color bg = PHRASES_COLORS[i % PHRASES_COLORS.length];
      stylers[i] = new Styler()
      {

        @Override
        public void applyStyles(TextStyle textStyle)
        {
          textStyle.background = bg;

        }
      };

    }

    return stylers;
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
      if (entry.getVisible() && mySourceFilter.accept(entry) && myTypeFilter.accept(entry)
          && textFilter.accept(entry))
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
    private static final Color BLACK = Display.getDefault().getSystemColor(
        SWT.COLOR_BLACK);
    private static final Color WHITE = Display.getDefault().getSystemColor(
        SWT.COLOR_WHITE);

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
        private IPreferenceStore store = CorePlugin.getDefault().getPreferenceStore();

        private Map<java.awt.Color, Color> swtColorMap =
            new HashMap<java.awt.Color, Color>();

        private Font prefFont;

        private String prefFontStr;

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
        public Font getFont(Object element)
        {
          String fontStr = store.getString(NarrativeViewerPrefsPage.PreferenceConstants.FONT);
          if(fontStr==null)
          {
            if(prefFont!=null)
            {
              prefFont.dispose();
            
            }
            prefFont = null;
            prefFontStr = null;
          }
          else if(fontStr.equals(prefFontStr) && prefFont!=null )
          {
            return (prefFont);
          }
          else
          {
            if(prefFont!=null)
            {
              prefFont.dispose();
              prefFont = null;
            }
            prefFontStr =fontStr;
            FontData[] readFontData = PreferenceConverter.readFontData(fontStr);
            if(readFontData!=null)
            {
              prefFont = new Font(Display.getDefault(), readFontData);
              return prefFont;
            }
          }
          return super.getFont(element);
        }
        
      

        @Override
        public Color getForeground(Object element)
        {
          if (element instanceof NarrativeEntry)
          {
            NarrativeEntry entry = (NarrativeEntry) element;
            java.awt.Color color = entry.getColor();
            Color swtColor = swtColorMap.get(color);
            if (swtColor == null || swtColor.isDisposed())
            {
              swtColor =
                  new Color(Display.getCurrent(), color.getRed(), color
                      .getGreen(), color.getBlue());
              swtColorMap.put(color, swtColor);
            }

            if (swtColor.getRGB().equals(WHITE.getRGB()))
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

    // @Override
    // public CellEditor getCellEditor(Grid table)
    // {
    // return new TextCellEditor(table,
    // SWT.WRAP);
    // }

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
      return entry.getEntry() == null ? "" : entry.getEntry();
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

  public void createTable(final NarrativeViewer viewer,
      final GridColumnLayout layout)
  {
    viewer.getViewer().setItemCount(0);
    TableViewerColumnFactory factory =
        new TableViewerColumnFactory(viewer.getViewer());
    viewer.getViewer().setContentProvider(new ILazyContentProvider()
    {

      Object[] elements;
      private GridTableViewer gridTableViewer;

      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
      {

        elements = myVisibleRows == null ? NO_ENTRIES : myVisibleRows.toArray();
        gridTableViewer = (GridTableViewer) viewer;
        gridTableViewer.setItemCount(0);
        gridTableViewer.setItemCount(elements.length);
      }

      @Override
      public void dispose()
      {

      }

      @Override
      public void updateElement(int index)
      {
        gridTableViewer.replace(elements[index], index);

      }
    });

    {

      for (final AbstractColumn column : myAllColumns)
      {
        CellLabelProvider cellRenderer =
            column.getCellRenderer(viewer.getViewer());

        final GridViewerColumn viewerColumn =
            factory.createColumn(column.getColumnName(), column
                .getColumnWidth(), cellRenderer, column.isWrap());

        final GridColumn gridColumn = viewerColumn.getColumn();
        gridColumn.addControlListener(new ControlListener()
        {
          
          @Override
          public void controlResized(ControlEvent e)
          {
            //trigger cells to recalculate heights  
            viewer.refresh();
          }
          
          @Override
          public void controlMoved(ControlEvent e)
          {
            //ignore
            
          }
        });
        TextHighlightCellRenderer styledTextCellRenderer =
            new TextHighlightCellRenderer()
            {

              protected StyledString getStyledString(String text)
              {
                String filterText = getFilterText();
                boolean hasTextFilter =
                    filterText != null && !filterText.trim().isEmpty();

                String[] phrases = getPhrases();
                if (hasTextFilter || phrases.length > 0)
                {

                  boolean found = false;
                  
                  Map<String,Styler> stylerReg = new HashMap<String,Styler>();

                  StringBuilder group = new StringBuilder();

                  boolean addOR = hasTextFilter;
                  if (hasTextFilter)
                  {
                    group.append("(");
                    group.append(Pattern.quote(filterText));
                    group.append(")");
                    stylerReg.put(filterText.toLowerCase(), SEARCH_STYLE);
                  }

                  Styler[] phraseStyles = getPhraseStyles();
                  int index = 0;
                  for (String phrase : phrases)
                  {
                    if(addOR)
                    {
                      group.append("|");
                    }
                    
                    group.append("(");
                    group.append(Pattern.quote(phrase));
                    group.append(")");
                    addOR = true;
                    stylerReg.put(phrase.toLowerCase(), phraseStyles[index]);
                    index++;
                  }
                  StyledString string = new StyledString();
                  Pattern pattern =
                      Pattern.compile(group.toString(), Pattern.CASE_INSENSITIVE);
                  Matcher matcher = pattern.matcher(text);

                  found = matchRanges(text, matcher, string, stylerReg);

                  if (!found)
                  {
                    return null;
                  }
                  else
                  {
                    return string;
                  }

                }
                return null;
              }

              private boolean matchRanges(String text, Matcher matcher,
                  StyledString string, Map<String,Styler> stylerReg)
              {
                boolean found = false;
                int lastindex = 0;
                while (matcher.find())
                {

                  found = true;
                  if (lastindex != matcher.start())
                  {
                    string.append(text.substring(lastindex, matcher.start()));
                  }
                  string.append(text.substring(matcher.start(), matcher.end()),
                      stylerReg.get(matcher.group().toLowerCase()));
                  lastindex = matcher.end();
                }
                if (lastindex < text.length())
                  string.append(text.substring(lastindex));
                return found;
              }

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

            if (column.isVisible())
            {
              layout.setColumnData(gridColumn, new ColumnWeightData(column
                  .getColumnWidth()));
            }
            else
            {
              layout.setColumnData(gridColumn, new ColumnWeightData(0));
            }
          }
        });
        layout.setColumnData(gridColumn, new ColumnWeightData(column
            .getColumnWidth(), column.isColumnWidthExpand()));

        if (!column.isVisible())
        {
          gridColumn.setVisible(column.isVisible());
          layout.setColumnData(gridColumn, new ColumnWeightData(0));
        }

      }

    }

  }

}
