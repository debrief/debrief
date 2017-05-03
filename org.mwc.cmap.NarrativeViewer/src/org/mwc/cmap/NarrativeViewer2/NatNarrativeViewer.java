package org.mwc.cmap.NarrativeViewer2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IRowIdAccessor;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.ComboBoxFilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.GlazedListsFilterRowComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.command.MultiColumnHideCommand;
import org.eclipse.nebula.widgets.nattable.hideshow.command.ShowAllColumnsCommand;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.NatTableBorderOverlayPainter;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRowsCommand;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.util.ArrayUtil;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.mwc.cmap.NarrativeViewer.actions.AbstractDynamicAction;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;
import org.mwc.cmap.NarrativeViewer.preferences.NarrativeViewerPrefsPage;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;

public class NatNarrativeViewer
{

  private static final String TYPE_LBL = "Type";
  private static final String SOURCE_LBL = "Source";
  private ConfigRegistry configRegistry;
  private NatTable natTable;
  private NarrativeViewerStyleConfiguration styleConfig;
  private Composite container;
  private HashMap<String, String> propertyToLabelMap;
  private ReflectiveColumnPropertyAccessor<INatEntry> columnPropertyAccessor;
  private String[] propertyNames;
  private IRollingNarrativeProvider input;
  private TextMatcherEditor<INatEntry> textMatcherEditor;

  private final DateFormatter dateFormatter = new DateFormatter();
  private BodyLayerStack<INatEntry> bodyLayer;
  private NatDoubleClickListener doubleClickListener;

  private Font prefFont;
  private IPreferenceStore preferenceStore;
  private FilteredNatTable filteredNatTable;
  private DefaultColumnHeaderDataProvider columnHeaderDataProvider;

  private List<Integer> hiddenCols = new ArrayList<Integer>();

  public NatNarrativeViewer(final Composite parent,
      final IPreferenceStore preferenceStore)
  {
    this.preferenceStore = preferenceStore;

    filteredNatTable = new FilteredNatTable(parent, SWT.NONE, true)
    {

      @Override
      protected Control createGridControl(final Composite parent,
          final int style)
      {
        container = new Composite(parent, SWT.NONE);
        final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayoutData(data);
        return container;
      }

      @Override
      protected void updateGridData(final String text)
      {

        // update highlight
        final String input = text;
        if (!input.isEmpty())
        {
          styleConfig.updateSearchHighlight("(" + input + ")");
        }
        else
        {
          styleConfig.updateSearchHighlight("");
        }

        // update filter
        textMatcherEditor.setFilterText(new String[]
        {input});

        natTable.refresh(false);

        // update filter
        textMatcherEditor.setFilterText(new String[]
        {text});

        natTable.refresh(false);

      }
    };
    filteredNatTable
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    container.addDisposeListener(new DisposeListener()
    {

      @Override
      public void widgetDisposed(final DisposeEvent e)
      {
        if (prefFont != null)
        {
          prefFont.dispose();
        }

      }
    });

    container.setLayout(new FillLayout());

    // create a new ConfigRegistry which will be needed for GlazedLists
    // handling
    configRegistry = new ConfigRegistry();

    // property names of the NarrativeEntry class
    propertyNames = new String[]
    {"time", "name", "type", "log"};

    // mapping from property to label, needed for column header labels
    propertyToLabelMap = new HashMap<String, String>();

    propertyToLabelMap.put("time", "Time");
    propertyToLabelMap.put("name", SOURCE_LBL);
    propertyToLabelMap.put("type", TYPE_LBL);
    propertyToLabelMap.put("log", "Entry");

    columnPropertyAccessor =
        new ReflectiveColumnPropertyAccessor<INatEntry>(propertyNames);

    // add filter
    textMatcherEditor =
        new TextMatcherEditor<INatEntry>(new TextFilterator<INatEntry>()
        {

          @Override
          public void getFilterStrings(final List<String> baseList,
              final INatEntry element)
          {
            baseList.add(element.getLog());
            baseList.add(element.getName());
            baseList.add(element.getType());
          }
        });
    textMatcherEditor.setMode(TextMatcherEditor.CONTAINS);
    ;
    styleConfig = new NarrativeViewerStyleConfiguration(preferenceStore);

    buildTable();

    // for font changes
    preferenceStore.addPropertyChangeListener(new IPropertyChangeListener()
    {
      @Override
      public void propertyChange(final PropertyChangeEvent event)
      {
        if (container.isDisposed())
        {
          return;
        }

        if (!event.getProperty().equals(
            NarrativeViewerPrefsPage.PreferenceConstants.FONT))
        {
          return;
        }

        try
        {

          loadFont(preferenceStore);

        }
        finally
        {
          natTable.refresh(false);
        }
      }

    });
    // for phrases changes
    preferenceStore.addPropertyChangeListener(new IPropertyChangeListener()
    {
      @Override
      public void propertyChange(final PropertyChangeEvent event)
      {
        if (container.isDisposed())
        {
          return;
        }

        if (!event.getProperty().equals(
            NarrativeViewerPrefsPage.PreferenceConstants.HIGHLIGHT_PHRASES))
        {
          return;
        }

        styleConfig.updatePhrasesStyle();

        natTable.refresh(false);
      }

    });

  }

  public void addDoubleClickListener(
      final NatDoubleClickListener iDoubleClickListener)
  {
    doubleClickListener = iDoubleClickListener;
  }

  private void buildTable()
  {
    if (natTable != null && !natTable.isDisposed())
    {
      natTable.dispose();
    }

    final CompositeLayer compositeLayer = new CompositeLayer(1, 2);
    final List<INatEntry> input = getNatInput();
    bodyLayer = new BodyLayerStack<INatEntry>(input, columnPropertyAccessor);
    bodyLayer
        .addConfigLabelAccumulator(new NarrativeEntryConfigLabelAccumulator(
            bodyLayer.getBodyDataProvider(), configRegistry));

    columnHeaderDataProvider =
        new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);
    final DataLayer columnHeaderDataLayer =
        new DataLayer(columnHeaderDataProvider);
    final ColumnHeaderLayer columnHeaderLayer =
        new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayer, bodyLayer
            .getSelectionLayer());

    final SortHeaderLayer<INatEntry> sortHeaderLayer =
        new SortHeaderLayer<INatEntry>(columnHeaderLayer,
            new GlazedListsSortModel<INatEntry>(bodyLayer.getSortedList(),
                columnPropertyAccessor, configRegistry, columnHeaderDataLayer));

    final GlazedListsFilterRowComboBoxDataProvider<INatEntry> comboBoxDataProvider =
        new GlazedListsFilterRowComboBoxDataProvider<INatEntry>(bodyLayer
            .getGlazedListsEventLayer(), bodyLayer.getSortedList(),
            columnPropertyAccessor);
    final ComboBoxFilterRowHeaderComposite<INatEntry> filterRowHeaderLayer =
        new ComboBoxFilterRowHeaderComposite<INatEntry>(bodyLayer
            .getFilterList(), comboBoxDataProvider, columnPropertyAccessor,
            sortHeaderLayer, columnHeaderDataProvider, configRegistry, false);
    filterRowHeaderLayer
        .addConfiguration(new NarrativeViewerFilterRowConfiguration(
            comboBoxDataProvider));

    compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER,
        filterRowHeaderLayer, 0, 0);
    compositeLayer.setChildLayer(GridRegion.BODY, bodyLayer, 0, 1);
    filterRowHeaderLayer.getFilterStrategy().addStaticFilter(textMatcherEditor);

    natTable = new NatTable(container, compositeLayer, false);
    natTable.setBackground(GUIHelper.COLOR_WHITE);
    natTable.setConfigRegistry(configRegistry);
    natTable.addConfiguration(styleConfig);
    natTable.configure();

    natTable.addOverlayPainter(new NatTableBorderOverlayPainter());

    // natTable.getUiBindingRegistry().registerMouseDownBinding(
    // MouseEventMatcher.columnHeaderLeftClick(SWT.NONE), new IMouseAction()
    // {
    //
    // @Override
    // public void run(final NatTable natTable, final MouseEvent event)
    // {
    // int columnPosition = natTable.getColumnPositionByX(event.x);
    // if (columnPosition == getColumnPositionBylabel(SOURCE_LBL))// source
    // {
    // System.out.println("//click on source");
    // }
    // else if (columnPosition == getColumnPositionBylabel(TYPE_LBL))// type
    // {
    // System.out.println("//click on type");
    // }
    //
    // }
    // });
    natTable.getUiBindingRegistry().registerDoubleClickBinding(
        MouseEventMatcher.bodyLeftClick(SWT.NONE), new IMouseAction()
        {

          @Override
          public void run(final NatTable natTable, final MouseEvent event)
          {
            if (doubleClickListener != null)
            {
              doubleClickListener.doubleClick(getSelection());
            }
          }
        });

    bodyLayer.getSelectionLayer().setSelectionModel(
        new RowSelectionModel<INatEntry>(bodyLayer.getSelectionLayer(),
            bodyLayer.getBodyDataProvider(), new IRowIdAccessor<INatEntry>()
            {

              @Override
              public Serializable getRowId(final INatEntry rowObject)
              {
                return rowObject;
              }

            }, false));

    container.layout(true);

    loadFont(preferenceStore);
    natTable.refresh(false);
  }

  public void fillActionBars(final IActionBars actionBars)
  {
    IMenuManager menu = actionBars.getMenuManager();
    final SwitchColumnVisibilityAction showSource =
        new SwitchColumnVisibilityAction(SOURCE_LBL, "Show source");
    menu.add(showSource);
    final SwitchColumnVisibilityAction showType =
        new SwitchColumnVisibilityAction(TYPE_LBL, "Show type");
    menu.add(showType);
    menu.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(final IMenuManager manager)
      {
        showSource.refresh();
        showType.refresh();
      }
    });

  }

  public Control getControl()
  {
    return container;
  }

  private List<INatEntry> getNatInput()
  {
    if (input != null)
    {
      final NarrativeEntry[] narrativeHistory =
          input.getNarrativeHistory(new String[]
          {});
      final List<INatEntry> entries =
          new ArrayList<INatEntry>(narrativeHistory.length);
      for (final NarrativeEntry narrativeEntry : narrativeHistory)
      {
        entries.add(new NatEntryProxy(dateFormatter, narrativeEntry));
      }
      return entries;
    }
    return Collections.emptyList();
  }

  public StructuredSelection getSelection()
  {
    final Set<Range> selectedRowPositions =
        bodyLayer.getSelectionLayer().getSelectedRowPositions();
    for (final Range range : selectedRowPositions)
    {
      final INatEntry rowObject =
          bodyLayer.getBodyDataProvider().getRowObject(range.start);
      if (rowObject instanceof NatEntryProxy)
      {
        return new StructuredSelection(((NatEntryProxy) rowObject).getEntry());
      }
    }
    return null;
  }

  private void loadFont(final IPreferenceStore preferenceStore)
  {
    final String fontStr =
        preferenceStore
            .getString(NarrativeViewerPrefsPage.PreferenceConstants.FONT);
    if (fontStr == null)
    {
      if (prefFont != null)
      {
        prefFont.dispose();
      }
      prefFont = null;

    }

    else
    {
      if (prefFont != null)
      {
        prefFont.dispose();
        prefFont = null;
      }

      final FontData[] readFontData = PreferenceConverter.readFontData(fontStr);
      if (readFontData != null && readFontData.length > 0)
      {
        prefFont = new Font(Display.getDefault(), readFontData);
      }
    }
    // load font to style
    final IStyle defaultStyle =
        configRegistry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
            DisplayMode.NORMAL);
    final IStyle selectionStyle =
        configRegistry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
            DisplayMode.SELECT);
    final IStyle headerStyle =
        configRegistry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
            DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);

    defaultStyle.setAttributeValue(CellStyleAttributes.FONT, prefFont);
    selectionStyle.setAttributeValue(CellStyleAttributes.FONT, prefFont);
    headerStyle.setAttributeValue(CellStyleAttributes.FONT, prefFont);

  }

  public void refresh()
  {
    natTable.refresh(false);

  }

  public void setDTG(final HiResDate dtg)
  {
    // find the table entry immediately after or on this DTG
    NarrativeEntry entry = null;

    // retrieve the list of visible rows
    final List<INatEntry> visEntries =
        bodyLayer.getBodyDataProvider().getList();

    // step through them
    for (final INatEntry nEntry : visEntries)
    {
      final NatEntryProxy narrativeEntry = (NatEntryProxy) nEntry;

      // get the date
      final HiResDate dt = narrativeEntry.getEntry().getDTG();

      // is this what we're looking for?
      if (dt.greaterThanOrEqualTo(dtg))
      {
        entry = narrativeEntry.getEntry();
        break;
      }
    }

    // ok, try to select this entry
    if (entry != null)
    {
      boolean needsChange = true;

      final ISelection curSel = getSelection();
      if (curSel instanceof StructuredSelection)
      {
        final StructuredSelection sel = (StructuredSelection) curSel;
        if (sel.size() == 1)
        {
          final Object item = sel.getFirstElement();
          if (item instanceof NarrativeEntry)
          {
            final NarrativeEntry nw = (NarrativeEntry) item;
            if (entry.equals(nw))
            {
              needsChange = false;
            }
          }
        }
      }

      if (needsChange)
      {
        setEntry(entry);
      }
    }

  }

  public void setEntry(final NarrativeEntry entry)
  {
    final List<INatEntry> list = bodyLayer.getBodyDataProvider().getList();
    int indexOf = list.indexOf(new NatEntryProxy(dateFormatter, entry));

    // did we find a row?
    if (indexOf > -1)
    {
      // find 1/2 the number of visible rows, to use as offset
      final int offset = bodyLayer.getViewportLayer().getRowCount() / 2;
      
      // find the currently selected row
      final int[] cells = bodyLayer.getSelectionLayer().getFullySelectedRowPositions();
      
      // move the selection back a bit, so we see both sides of it
      int revealRow = indexOf;
      if (cells != null && cells.length == 1)
      {
        // ok, are we moving up or down?
        if (cells[0] < indexOf)
        {
          // we're moving down. Do we have enough rows for an offset?
          if (indexOf < list.size() - offset)
          {
            revealRow = indexOf + offset;
          }
        }
        else
        {
          // we must be moving up. Do we have enough rows for an offset?
          if (indexOf > offset)
          {
            revealRow = indexOf - offset;
          }
        }
      }
      
      bodyLayer.getSelectionLayer().doCommand(
          new SelectRowsCommand(bodyLayer.getSelectionLayer(), 0, ArrayUtil
              .asIntArray(indexOf), false, false, revealRow));
    }

  }

  public void setFilterMode(final boolean checked)
  {
    filteredNatTable.setFilterMode(checked);

  }

  public void setInput(final IRollingNarrativeProvider input)
  {
    this.input = input;
    dateFormatter.clearCache();
    if (!container.isDisposed())
    {
      buildTable();
      Display.getCurrent().asyncExec(new Runnable()
      {

        @Override
        public void run()
        {
          natTable.refresh(true);
        }
      });
    }
  }

  public void setSearchMode(final boolean checked)
  {
    setFilterMode(checked);

  }

  public void setTimeFormatter(final TimeFormatter timeFormatter)
  {
    dateFormatter.setFormatter(timeFormatter);
    natTable.refresh(true);
  }

  public void setWrappingEntries(final boolean checked)
  {
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        checked ? styleConfig.wrappingEntryLogPainter
            : styleConfig.automaticRowHeightLogPainter, DisplayMode.NORMAL,
        ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 3);

    natTable.refresh(false);

  }

  int getColumnPositionBylabel(String label)
  {

    int columnCount = natTable.getColumnCount();
    for (int i = 0; i < columnCount; i++)
    {
      String columnHeaderLabel =
          columnHeaderDataProvider.getColumnHeaderLabel(i);
      if (label.equals(columnHeaderLabel))
      {
        return i;
      }

    }

    return -1;
  }

  private class SwitchColumnVisibilityAction extends AbstractDynamicAction
  {
    private final String colLabel;
    private boolean visible = true;

    public SwitchColumnVisibilityAction(final String colLabel, final String name)
    {
      this.colLabel = colLabel;
      setText(name);

    }

    public void refresh()
    {
      setChecked(visible);
    }

    int[] toIntArray(List<Integer> list)
    {
      int[] ret = new int[list.size()];
      int i = 0;
      for (Integer e : list)
        ret[i++] = e.intValue();
      return ret;
    }

    @Override
    public void run()
    {
      // make sure to do show all to find correct column index
      natTable.doCommand(new ShowAllColumnsCommand());
      int columnPositionBylabel = getColumnPositionBylabel(colLabel);
      visible = !visible;
      if (visible)
      {
        hiddenCols.remove((Object) Integer.valueOf(columnPositionBylabel));
        bodyLayer.getBodyDataLayer().setColumnWidthByPosition(
            columnPositionBylabel, 100);
      }
      else
      {
        hiddenCols.add(columnPositionBylabel);
        bodyLayer.getBodyDataLayer().setColumnWidthByPosition(
            columnPositionBylabel, 0);
      }

      if (hiddenCols.size() > 0)
        natTable.doCommand(new MultiColumnHideCommand(natTable,
            toIntArray(hiddenCols)));

      natTable.layout(true);

    }

  }

}
