package org.mwc.cmap.NarrativeViewer2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
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
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;
import org.mwc.cmap.core.CorePlugin;

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

  public NatNarrativeViewer(final Composite parent,
      final IPreferenceStore preferenceStore)
  {

    container = new Composite(parent, SWT.NONE);
    container.setLayout(new GridLayout());

    Composite buttonPanel = new Composite(container, SWT.NONE);
    buttonPanel.setLayout(new RowLayout());
    GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    final Text filterInput = new Text(container, SWT.NONE);
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

  private void buildTable()
  {
    if (natTable != null && !natTable.isDisposed())
    {
      natTable.dispose();
    }

    final CompositeLayer compositeLayer = new CompositeLayer(1, 2);
    List<INatEntry> input = getNatInput();
    BodyLayerStack<INatEntry> bodyLayer =
        new BodyLayerStack<INatEntry>(input, columnPropertyAccessor);
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

    GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
    container.layout(true);
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
        entries.add(new NatEntryProxy(narrativeEntry));
      }
      return entries;
    }
    return Collections.emptyList();
  }

  public void setInput(IRollingNarrativeProvider input)
  {
    this.input = input;

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

  public void setTimeFormatter(TimeFormatter timeFormatter)
  {
    // TODO

  }

  public StructuredSelection getSelection()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Control getControl()
  {
    return container;
  }

  public void addDoubleClickListener(IDoubleClickListener iDoubleClickListener)
  {
    // TODO Auto-generated method stub

  }

  public void refresh()
  {
    natTable.refresh(false);

  }

  public void setEntry(NarrativeEntry entry)
  {
    // TODO Auto-generated method stub

  }

  public void setSearchMode(boolean checked)
  {
    // TODO Auto-generated method stub

  }

  public void fillActionBars(IActionBars actionBars)
  {
    // TODO Auto-generated method stub

  }

  public void setWrappingEntries(boolean checked)
  {
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        checked ? styleConfig.wrappingAutomaticRowHeightPainter
            : styleConfig.automaticRowHeightPainter, DisplayMode.NORMAL,
        ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 3);

    natTable.refresh(false);

  }

  public void setDTG(HiResDate theDTG)
  {
    // TODO Auto-generated method stub

  }

}
