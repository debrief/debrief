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
    private static final String DEFAULT_TIME_FORMAT = "HH:mm";

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

    private final LinkedList<NarrativeEntry> myVisibleRows = new LinkedList<NarrativeEntry>();
    private NarrativeEntry[] myAllEntries = NO_ENTRIES;
    private ColumnSizeCalculator myColumnSizeCalculator;
    private int myEntryCellContentWidth = -1;
    private IRollingNarrativeProvider myInput;

    public NarrativeViewerModel(IPreferenceStore store,
            ColumnSizeCalculator columnSizeCalculator)
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
            public String getFilterValue(NarrativeEntry entry)
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
            public String getFilterValue(NarrativeEntry entry)
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
            public void columnVisibilityChanged(Column column,
                    boolean actualIsVisible)
            {
                updateColumnsVisibility();
            }
        });
        updateColumnsVisibility();
    }

    public void setInput(IRollingNarrativeProvider entryWrapper)
    {
        myInput = entryWrapper;
        myAllEntries = null;

        if (entryWrapper != null)
        {
            // check it has some data.
            NarrativeEntry[] entries = entryWrapper
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

    public void setTimeFormatter(TimeFormatter timeFormatter)
    {
        myColumnTime.setTimeFormatter(timeFormatter);
    }

    public boolean isWrappingEntries()
    {
        return myColumnEntry.isWrapping();
    }

    public boolean setWrappingEntries(boolean shouldWrap)
    {
        return myColumnEntry.setWrapping(shouldWrap);
    }

    private void updateFilters()
    {
        myVisibleRows.clear();
        if (!hasInput())
        {
            return;
        }
        for (NarrativeEntry entry : myAllEntries)
        {
            if (mySourceFilter.accept(entry) && myTypeFilter.accept(entry))
            {
                myVisibleRows.add(entry);
            }
        }
    }

    private void updateColumnsVisibility()
    {
        myVisibleColumns.clear();
        for (AbstractColumn next : myAllColumns)
        {
            if (next.isVisible())
            {
                myVisibleColumns.add(next);
            }
        }
    }

    public Column getVisibleColumn(int col)
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
    public KTableCellEditor doGetCellEditor(int col, int row)
    {
        return row == 0 ? null : getVisibleColumn(col).getCellEditor();
    }

    @Override
    public KTableCellRenderer doGetCellRenderer(int col, int row)
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
    public Object doGetContentAt(int col, int row)
    {
        Column column = getVisibleColumn(col);
        return row == 0 ? column.getColumnName() : column
                .getProperty(myVisibleRows.get(row - 1));
    }

    @Override
    public int doGetRowCount()
    {
        return 1 + (hasInput() ? myVisibleRows.size() : 0);
    }

    @Override
    public void doSetContentAt(int col, int row, Object value)
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
    public int getInitialRowHeight(int row)
    {
        return ROW_HEIGHT;
    }

    public boolean isRowResizable(int row)
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
    public int getInitialColumnWidth(int column)
    {
        return getVisibleColumn(column).getColumnWidth();
    }

    public int getRowHeightMinimum()
    {
        return ROW_HEIGHT;
    }

    public boolean isColumnResizable(int col)
    {
        return true;
    }

    public void addColumnVisibilityListener(
            AbstractColumn.VisibilityListener listener)
    {
        myColumnSource.addVisibilityListener(listener);
        myColumnTime.addVisibilityListener(listener);
        myColumnEntry.addVisibilityListener(listener);
        myColumnVisible.addVisibilityListener(listener);
        myColumnType.addVisibilityListener(listener);
    }

    public void onColumnsResized(GC gc, boolean force)
    {
        int newEntryCellContentWidth = myColumnSizeCalculator
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
            int newRowHeight = gc.textExtent(entry).y
                    + KTABLE_CELL_CONTENT_MARGIN_HEIGHT * 2;
            setRowHeight(row + 1, newRowHeight);
        }
    }

    private static abstract class AbstractTextColumn extends AbstractColumn
    {
        public AbstractTextColumn(int index, String name, IPreferenceStore store)
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
        public ColumnVisible(IPreferenceStore store)
        {
            super(0, "Visible", store);
        }

        public Object getProperty(NarrativeEntry entry)
        {
            return entry.getVisible();
        }

        public KTableCellEditor getCellEditor()
        {
            Rectangle imgBounds = CheckableCellRenderer.IMAGE_CHECKED
                    .getBounds();
            Point sensible = new Point(imgBounds.width, imgBounds.height);
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

        public ColumnTime(IPreferenceStore store)
        {
            super(1, "Time", store);
        }

        public Object getProperty(NarrativeEntry entry)
        {
            return myTimeFormatter.format(entry.getDTG());
        }

        public void setTimeFormatter(TimeFormatter formatter)
        {
            myTimeFormatter = formatter;
        }
    }

    private static class ColumnSource extends AbstractTextColumn
    {
        public ColumnSource(IPreferenceStore store)
        {
            super(2, "Source", store);
        }

        public Object getProperty(NarrativeEntry entry)
        {
            return entry.getTrackName();
        }
    }

    private static class ColumnType extends AbstractTextColumn
    {
        public ColumnType(IPreferenceStore store)
        {
            super(3, "Type", store);
        }

        public Object getProperty(NarrativeEntry entry)
        {
            return entry.getType();
        }
    }

    private static class ColumnEntry extends AbstractTextColumn
    {
        private boolean myIsWrapping;
        private TextCellRenderer myWrappingRenderer;

        public ColumnEntry(IPreferenceStore store)
        {
            super(4, "Entry", store);
        }

        public boolean setWrapping(boolean shouldWrap)
        {
            boolean changed = myIsWrapping ^ shouldWrap;
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

        public Object getProperty(NarrativeEntry entry)
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

    private static TimeFormatter DEFAULT_TIME = new TimeFormatter()
    {
        public String format(HiResDate time)
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
