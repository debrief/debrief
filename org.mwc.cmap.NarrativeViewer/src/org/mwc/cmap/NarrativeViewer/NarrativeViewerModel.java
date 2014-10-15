/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.NarrativeViewer;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;


import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableDefaultModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.editors.KTableCellEditorCheckbox2;
import de.kupzog.ktable.renderers.CheckableCellRenderer;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;

public class NarrativeViewerModel extends KTableDefaultModel
{
    private static final NarrativeEntry[] NO_ENTRIES = new NarrativeEntry[0];
    private static final int ROW_HEIGHT = 20;
    private static final int KTABLE_CELL_CONTENT_MARGIN_WIDTH = 4;
    private static final int KTABLE_CELL_CONTENT_MARGIN_HEIGHT = 3;

    private final ColumnVisible myColumnVisible;
    private final ColumnTime myColumnTime;
    private final ColumnSource myColumnSource;
    private final ColumnType myColumnType;
    private final ColumnEntry myColumnEntry;
    private final AbstractColumn[] myAllColumns;
    private final ArrayList<AbstractColumn> myVisibleColumns = new ArrayList<AbstractColumn>();

    private final ColumnFilter mySourceFilter;
    private final ColumnFilter myTypeFilter;

    private final FixedCellRenderer myHeaderCellRenderer = new FixedCellRenderer(
            FixedCellRenderer.STYLE_PUSH | SWT.BOLD
                    | TextCellRenderer.INDICATION_FOCUS_ROW)
    {
        {
            setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER
                    | SWTX.ALIGN_VERTICAL_CENTER);
        }
    };

    final LinkedList<NarrativeEntry> myVisibleRows = new LinkedList<NarrativeEntry>();
    private NarrativeEntry[] myAllEntries = NO_ENTRIES;
    private ColumnSizeCalculator myColumnSizeCalculator;
    private int myEntryCellContentWidth = -1;
    private IRollingNarrativeProvider myInput;

    public NarrativeViewerModel(final IPreferenceStore store,
            final ColumnSizeCalculator columnSizeCalculator)
    {
        myColumnSizeCalculator = columnSizeCalculator;

        myColumnVisible = new ColumnVisible(store);
        myColumnVisible.setVisible(false);
        myColumnTime = new ColumnTime(store);
        myColumnSource = new ColumnSource(store);
        myColumnType = new ColumnType(store);
        myColumnEntry = new ColumnEntry(store);
        myAllColumns = new AbstractColumn[] {
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

        addColumnVisibilityListener(new Column.VisibilityListener()
        {
            public void columnVisibilityChanged(final Column column,
                    final boolean actualIsVisible)
            {
                updateColumnsVisibility();
            }
        });
        updateColumnsVisibility();
    }

    public void setInput(final IRollingNarrativeProvider entryWrapper)
    {
        myInput = entryWrapper;
        myAllEntries = null;

        if (entryWrapper != null)
        {
            // check it has some data.
            final NarrativeEntry[] entries = entryWrapper
                    .getNarrativeHistory(new String[] {});
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

    void updateColumnsVisibility()
    {
        myVisibleColumns.clear();
        for (final AbstractColumn next : myAllColumns)
        {
            if (next.isVisible())
            {
                myVisibleColumns.add(next);
            }
        }
    }

    public Column getVisibleColumn(final int col)
    {
        return myVisibleColumns.get(col);
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

    @Override
    public KTableCellEditor doGetCellEditor(final int col, final int row)
    {
        return row == 0 ? null : getVisibleColumn(col).getCellEditor();
    }

    @Override
    public KTableCellRenderer doGetCellRenderer(final int col, final int row)
    {
        return row == 0 ? myHeaderCellRenderer : getVisibleColumn(col)
                .getCellRenderer();
    }

    @Override
    public int doGetColumnCount()
    {
        return myVisibleColumns.size();
    }

    @Override
    public Object doGetContentAt(final int col, final int row)
    {
        final Column column = getVisibleColumn(col);
        return row == 0 ? column.getColumnName() : column
                .getProperty(myVisibleRows.get(row - 1));
    }
    
    public NarrativeEntry getEntryAt(final int col, final int row)
    {
        return myVisibleRows.get(row - 1);
    }

    @Override
    public int doGetRowCount()
    {
        return 1 + (hasInput() ? myVisibleRows.size() : 0);
    }

    @Override
    public void doSetContentAt(final int col, final int row, final Object value)
    {
        if (row == 0)
        {
            return; // header
        }
        // TODO: if more columns are writable, move tyhis code to Column class
        if (myColumnVisible == getVisibleColumn(col))
        {
            myVisibleRows.get(row - 1).setVisible((Boolean) value);
        }
    }

    @Override
    public int getInitialRowHeight(final int row)
    {
        return ROW_HEIGHT;
    }

    public boolean isRowResizable(final int row)
    {
        return false;
    }

    public int getFixedHeaderColumnCount()
    {
        return 0;
    }

    public int getFixedHeaderRowCount()
    {
        return 1;
    }

    public int getFixedSelectableColumnCount()
    {
        return 0;
    }

    public int getFixedSelectableRowCount()
    {
        return 0;
    }

    @Override
    public int getInitialColumnWidth(final int column)
    {
        return getVisibleColumn(column).getColumnWidth();
    }

    public int getRowHeightMinimum()
    {
        return ROW_HEIGHT;
    }

    public boolean isColumnResizable(final int col)
    {
        return true;
    }

    public void addColumnVisibilityListener(
            final AbstractColumn.VisibilityListener listener)
    {
        myColumnSource.addVisibilityListener(listener);
        myColumnTime.addVisibilityListener(listener);
        myColumnEntry.addVisibilityListener(listener);
        myColumnVisible.addVisibilityListener(listener);
        myColumnType.addVisibilityListener(listener);
    }

    public void onColumnsResized(final GC gc, final boolean force)
    {
        final int newEntryCellContentWidth = myColumnSizeCalculator
                .getColumnWidth(myVisibleColumns.indexOf(myColumnEntry))
                - KTABLE_CELL_CONTENT_MARGIN_WIDTH * 2;
        if (newEntryCellContentWidth == myEntryCellContentWidth && !force)
        {
            return;
        }
        myEntryCellContentWidth = newEntryCellContentWidth;

        for (int row = 0; row < myVisibleRows.size(); row++)
        {
            String entry = myVisibleRows.get(row).getEntry();
            if (myColumnEntry.isWrapping())
            {
                entry = SWTX.wrapText(gc, entry, myEntryCellContentWidth);
            }
            final int newRowHeight = gc.textExtent(entry).y
                    + KTABLE_CELL_CONTENT_MARGIN_HEIGHT * 2;
            setRowHeight(row + 1, newRowHeight);
        }
    }

    private static abstract class AbstractTextColumn extends AbstractColumn
    {
        public AbstractTextColumn(final int index, final String name, final IPreferenceStore store)
        {
            super(index, name, store);
        }

        @Override
        protected KTableCellRenderer createRenderer()
        {
            return new TextCellRenderer(TextCellRenderer.INDICATION_FOCUS_ROW);
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

        public KTableCellEditor getCellEditor()
        {
            final Rectangle imgBounds = CheckableCellRenderer.IMAGE_CHECKED
                    .getBounds();
            final Point sensible = new Point(imgBounds.width, imgBounds.height);
            return new KTableCellEditorCheckbox2(sensible,
                    SWTX.ALIGN_HORIZONTAL_CENTER, SWTX.ALIGN_VERTICAL_CENTER);
        }

        @Override
        protected KTableCellRenderer createRenderer()
        {
            return new CheckableCellRenderer(
                    CheckableCellRenderer.INDICATION_CLICKED
                            | CheckableCellRenderer.INDICATION_FOCUS);
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
        private boolean myIsWrapping;
        private TextCellRenderer myWrappingRenderer;

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
                updateWrapping();
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

        @Override
        protected KTableCellRenderer createRenderer()
        {
            myWrappingRenderer = new TextCellRenderer(
                    TextCellRenderer.INDICATION_FOCUS_ROW);
            updateWrapping();
            return myWrappingRenderer;
        }

        private void updateWrapping()
        {
            if (myWrappingRenderer == null)
            {
                return;
            }
            int alignment = SWTX.ALIGN_HORIZONTAL_LEFT
                    | SWTX.ALIGN_VERTICAL_TOP;
            if (myIsWrapping)
            {
                alignment |= SWTX.WRAP;
            }
            myWrappingRenderer.setAlignment(alignment);
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
}
