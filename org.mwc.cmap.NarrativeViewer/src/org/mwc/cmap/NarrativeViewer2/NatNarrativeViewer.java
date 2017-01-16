package org.mwc.cmap.NarrativeViewer2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowIdAccessor;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.ComboBoxFilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.GlazedListsFilterRowComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
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
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;
import org.mwc.cmap.NarrativeViewer.preferences.NarrativeViewerPrefsPage;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;

public class NatNarrativeViewer
{

  private ConfigRegistry configRegistry;
  private NatTable natTable;
  private NarrativeViewerStyleConfiguration styleConfig;
  private Composite container;
  private HashMap<String, String> propertyToLabelMap;
  private ReflectiveColumnPropertyAccessor<INatEntry> columnPropertyAccessor;
  private String[] propertyNames;
  private IRollingNarrativeProvider input;
  private TextMatcherEditor<INatEntry> textMatcherEditor;
  private Text filterInput;
  private DateFormatter dateFormatter = new DateFormatter();
  private BodyLayerStack<INatEntry> bodyLayer;
  private NatDoubleClickListener doubleClickListener;

  private Font prefFont;
  private IPreferenceStore preferenceStore;

  public NatNarrativeViewer(final Composite parent,
      final IPreferenceStore preferenceStore)
  {
    this.preferenceStore = preferenceStore;
    container = new Composite(parent, SWT.NONE);
    container.addDisposeListener(new DisposeListener()
    {

      @Override
      public void widgetDisposed(DisposeEvent e)
      {
        if (prefFont != null)
          prefFont.dispose();

      }
    });

    container.setLayout(new GridLayout());

    filterInput = new Text(container, SWT.SEARCH | SWT.ICON_CANCEL);
    filterInput.setMessage("filter text (including * or ?)");
    filterInput.addSelectionListener(new SelectionAdapter()
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * org.eclipse.swt.events.SelectionAdapter#widgetDefaultSelected(org.eclipse.swt.events.
       * SelectionEvent)
       */
      public void widgetDefaultSelected(SelectionEvent e)
      {
        if (e.detail == SWT.ICON_CANCEL)
        {
          clearText();
        }
          
      }
    });
    GridDataFactory.fillDefaults().grab(true, false).applyTo(filterInput);

    // create a new ConfigRegistry which will be needed for GlazedLists
    // handling
    configRegistry = new ConfigRegistry();

    // property names of the NarrativeEntry class
    propertyNames = new String[]
    {"time", "name", "type", "log"};

    // mapping from property to label, needed for column header labels
    propertyToLabelMap = new HashMap<String, String>();

    propertyToLabelMap.put("time", "Time");
    propertyToLabelMap.put("name", "Source");
    propertyToLabelMap.put("type", "Type");
    propertyToLabelMap.put("log", "Entry");

    columnPropertyAccessor =
        new ReflectiveColumnPropertyAccessor<INatEntry>(propertyNames);

    // add filter
    textMatcherEditor =
        new TextMatcherEditor<INatEntry>(new TextFilterator<INatEntry>()
        {

          @Override
          public void
              getFilterStrings(List<String> baseList, INatEntry element)
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
    
    //for font changes 
    preferenceStore.addPropertyChangeListener(new IPropertyChangeListener()
    {
      @Override
      public void propertyChange(PropertyChangeEvent event)
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
    //for phrases changes 
    preferenceStore.addPropertyChangeListener(new IPropertyChangeListener()
    {
      @Override
      public void propertyChange(PropertyChangeEvent event)
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

    // input listener
    filterInput.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR)
        {
          // update highlight
          String input = filterInput.getText();
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
        }
      }
    });

  }
  
  private void loadFont(final IPreferenceStore preferenceStore)
  {
    String fontStr =
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

      FontData[] readFontData = PreferenceConverter.readFontData(fontStr);
      if (readFontData != null)
      {
        prefFont = new Font(Display.getDefault(), readFontData);
      }
    }
    // load font to style
    IStyle defaultStyle =
        configRegistry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
            DisplayMode.NORMAL);
    IStyle selectionStyle =
        configRegistry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
            DisplayMode.SELECT);
    IStyle headerStyle =
        configRegistry.getConfigAttribute(CellConfigAttributes.CELL_STYLE,
            DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);

    defaultStyle.setAttributeValue(CellStyleAttributes.FONT, prefFont);
    selectionStyle.setAttributeValue(CellStyleAttributes.FONT, prefFont);
    headerStyle.setAttributeValue(CellStyleAttributes.FONT, prefFont);

  }

  public void setFilterMode(boolean checked)
  {
    clearText();
    filterInput.setVisible(checked);
    filterInput.setVisible(checked);
    if (checked)
    {
      GridDataFactory.fillDefaults().grab(true, false).applyTo(filterInput);
    }
    else
    {
      GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
      gridData.widthHint = 0;
      gridData.heightHint = 0;
      filterInput.setLayoutData(gridData);
    }
    container.layout(true);

  }

  void clearText()
  {
    if (filterInput != null && !filterInput.isDisposed())
    {
      filterInput.setText("");
      styleConfig.updateSearchHighlight("");

      // update filter
      textMatcherEditor.setFilterText(new String[]
      {""});

      natTable.refresh(false);
    }

  }

  private void buildTable()
  {
    if (natTable != null && !natTable.isDisposed())
    {
      natTable.dispose();
    }

    final CompositeLayer compositeLayer = new CompositeLayer(1, 2);
    List<INatEntry> input = getNatInput();
    bodyLayer = new BodyLayerStack<INatEntry>(input, columnPropertyAccessor);
    bodyLayer
        .addConfigLabelAccumulator(new NarrativeEntryConfigLabelAccumulator(
            bodyLayer.getBodyDataProvider(), configRegistry));

    IDataProvider columnHeaderDataProvider =
        new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);
    DataLayer columnHeaderDataLayer = new DataLayer(columnHeaderDataProvider);
    ColumnHeaderLayer columnHeaderLayer =
        new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayer, bodyLayer
            .getSelectionLayer());

    SortHeaderLayer<INatEntry> sortHeaderLayer =
        new SortHeaderLayer<INatEntry>(columnHeaderLayer,
            new GlazedListsSortModel<INatEntry>(bodyLayer.getSortedList(),
                columnPropertyAccessor, configRegistry, columnHeaderDataLayer));

    GlazedListsFilterRowComboBoxDataProvider<INatEntry> comboBoxDataProvider =
        new GlazedListsFilterRowComboBoxDataProvider<INatEntry>(bodyLayer
            .getGlazedListsEventLayer(), bodyLayer.getSortedList(),
            columnPropertyAccessor);
    ComboBoxFilterRowHeaderComposite<INatEntry> filterRowHeaderLayer =
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

    natTable.getUiBindingRegistry().registerDoubleClickBinding(
        MouseEventMatcher.bodyLeftClick(SWT.NONE), new IMouseAction()
        {

          @Override
          public void run(NatTable natTable, MouseEvent event)
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

              public Serializable getRowId(INatEntry rowObject)
              {
                return rowObject;
              }

            }, false));

    GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
    container.layout(true);

    loadFont(preferenceStore);
    natTable.refresh(false);
  }

  private List<INatEntry> getNatInput()
  {
    if (input != null)
    {
      NarrativeEntry[] narrativeHistory =
          input.getNarrativeHistory(new String[]
          {});
      List<INatEntry> entries =
          new ArrayList<INatEntry>(narrativeHistory.length);
      for (NarrativeEntry narrativeEntry : narrativeHistory)
      {
        entries.add(new NatEntryProxy(dateFormatter, narrativeEntry));
      }
      return entries;
    }
    return Collections.emptyList();
  }

  public void setInput(IRollingNarrativeProvider input)
  {
    this.input = input;
    dateFormatter.clearCache();
    if(!container.isDisposed())
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

  public void setTimeFormatter(TimeFormatter timeFormatter)
  {
    dateFormatter.setFormatter(timeFormatter);
    natTable.refresh(true);
  }

  public StructuredSelection getSelection()
  {
    Set<Range> selectedRowPositions =
        bodyLayer.getSelectionLayer().getSelectedRowPositions();
    for (Range range : selectedRowPositions)
    {
      INatEntry rowObject =
          bodyLayer.getBodyDataProvider().getRowObject(range.start);
      if (rowObject instanceof NatEntryProxy)
        return new StructuredSelection(((NatEntryProxy) rowObject).entry);
    }
    return null;
  }

  public Control getControl()
  {
    return container;
  }

  public void
      addDoubleClickListener(NatDoubleClickListener iDoubleClickListener)
  {
    doubleClickListener = iDoubleClickListener;
  }

  public void refresh()
  {
    natTable.refresh(false);

  }

  public void setEntry(NarrativeEntry entry)
  {
    List<INatEntry> list = bodyLayer.getBodyDataProvider().getList();
    int indexOf = list.indexOf(new NatEntryProxy(dateFormatter, entry));
    if (indexOf > -1)
      bodyLayer.getSelectionLayer().doCommand(
          new SelectRowsCommand(bodyLayer.getSelectionLayer(), 0, indexOf,
              false, false));

  }

  public void setSearchMode(boolean checked)
  {
    setFilterMode(checked);

  }

  public void fillActionBars(IActionBars actionBars)
  {
    // TODO Auto-generated method stub

  }

  public void setWrappingEntries(boolean checked)
  {
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        checked ? styleConfig.wrappingEntryLogPainter
            : styleConfig.automaticRowHeightLogPainter, DisplayMode.NORMAL,
        ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 3);

    natTable.refresh(false);

  }

  public void setDTG(HiResDate dtg)
  {
    // find the table entry immediately after or on this DTG
    NarrativeEntry entry = null;

    // retrieve the list of visible rows
    final List<INatEntry> visEntries =
        bodyLayer.getBodyDataProvider().getList();

    // step through them
    for (final Iterator<INatEntry> entryIterator = visEntries.iterator(); entryIterator
        .hasNext();)
    {
      final NatEntryProxy narrativeEntry = (NatEntryProxy) entryIterator.next();

      // get the date
      final HiResDate dt = narrativeEntry.entry.getDTG();

      // is this what we're looking for?
      if (dt.greaterThanOrEqualTo(dtg))
      {
        entry = narrativeEntry.entry;
        break;
      }

    }

    // ok, try to select this entry
    if (entry != null)
    {
      boolean needsChange = true;

      ISelection curSel = getSelection();
      if (curSel instanceof StructuredSelection)
      {
        StructuredSelection sel = (StructuredSelection) curSel;
        if (sel.size() == 1)
        {
          Object item = sel.getFirstElement();
          if (item instanceof NarrativeEntry)
          {
            NarrativeEntry nw = (NarrativeEntry) item;
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

}
