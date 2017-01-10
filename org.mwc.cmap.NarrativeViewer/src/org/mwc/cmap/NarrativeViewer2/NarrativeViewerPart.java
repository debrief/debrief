package org.mwc.cmap.NarrativeViewer2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.ComboBoxFilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.GlazedListsFilterRowComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.NatTableBorderOverlayPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.matchers.TextMatcherEditor;

public class NarrativeViewerPart extends ViewPart{
  
  public NarrativeViewerPart()
  {
    
  }

	public Control createExampleControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        Composite buttonPanel = new Composite(container, SWT.NONE);
        buttonPanel.setLayout(new RowLayout());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);
        
        final Text filterInput = new Text(container, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(filterInput);
        
        // create a new ConfigRegistry which will be needed for GlazedLists
        // handling
        final ConfigRegistry configRegistry = new ConfigRegistry();

        // property names of the ExtendedPersonWithAddress class
        String[] propertyNames = { "date", "time", "name", "type", "log" };

        // mapping from property to label, needed for column header labels
        Map<String, String> propertyToLabelMap = new HashMap<String, String>();
        propertyToLabelMap.put("date", "Date");
        propertyToLabelMap.put("time", "Time");
        propertyToLabelMap.put("name", "Source");
        propertyToLabelMap.put("type", "Type");
        propertyToLabelMap.put("log", "Entry");

        final IColumnPropertyAccessor<NATNarrativeEntry> columnPropertyAccessor =
                new ReflectiveColumnPropertyAccessor<NATNarrativeEntry>(propertyNames);

        BodyLayerStack<NATNarrativeEntry> bodyLayer =
                new BodyLayerStack<NATNarrativeEntry>(
                        NarrativeValueParser.getInput(),
                        columnPropertyAccessor);

        IDataProvider columnHeaderDataProvider =
                new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);
        DataLayer columnHeaderDataLayer =
                new DataLayer(columnHeaderDataProvider);
        ColumnHeaderLayer columnHeaderLayer =
                new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayer, bodyLayer.getSelectionLayer());

        SortHeaderLayer<NATNarrativeEntry> sortHeaderLayer =
                new SortHeaderLayer<NATNarrativeEntry>(
                        columnHeaderLayer,
                        new GlazedListsSortModel<NATNarrativeEntry>(
                                bodyLayer.getSortedList(),
                                columnPropertyAccessor,
                                configRegistry,
                                columnHeaderDataLayer));

        GlazedListsFilterRowComboBoxDataProvider<NATNarrativeEntry> comboBoxDataProvider = 
        		new GlazedListsFilterRowComboBoxDataProvider<NATNarrativeEntry>(
        				bodyLayer.getGlazedListsEventLayer(),
        				bodyLayer.getSortedList(),
        				columnPropertyAccessor);
        ComboBoxFilterRowHeaderComposite<NATNarrativeEntry> filterRowHeaderLayer =
                new ComboBoxFilterRowHeaderComposite<NATNarrativeEntry>(
                		bodyLayer.getFilterList(),
                		comboBoxDataProvider,
                        columnPropertyAccessor,
                        sortHeaderLayer,
                        columnHeaderDataProvider,
                        configRegistry,
                        false);
        filterRowHeaderLayer.addConfiguration(new NarrativeViewerFilterRowConfiguration(comboBoxDataProvider));

        CompositeLayer compositeLayer = new CompositeLayer(1, 2);
        compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, filterRowHeaderLayer, 0, 0);
        compositeLayer.setChildLayer(GridRegion.BODY, bodyLayer, 0, 1);

        final NarrativeViewerStyleConfiguration styleConfig = new NarrativeViewerStyleConfiguration();
        
        final NatTable natTable = new NatTable(container, compositeLayer, false);
        natTable.setBackground(GUIHelper.COLOR_WHITE);
        natTable.setConfigRegistry(configRegistry);
        natTable.addConfiguration(styleConfig);
        natTable.configure();
        
        natTable.addOverlayPainter(new NatTableBorderOverlayPainter());
        
        GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
        
        Button increaseFont = new Button(buttonPanel, SWT.PUSH);
        increaseFont.setText("Increase Font");
        increaseFont.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		IStyle defaultStyle = configRegistry.getConfigAttribute(
        				CellConfigAttributes.CELL_STYLE, 
        				DisplayMode.NORMAL);
        		IStyle selectionStyle = configRegistry.getConfigAttribute(
        				CellConfigAttributes.CELL_STYLE, 
        				DisplayMode.SELECT);
        		IStyle headerStyle = configRegistry.getConfigAttribute(
        				CellConfigAttributes.CELL_STYLE, 
        				DisplayMode.NORMAL,
        				GridRegion.COLUMN_HEADER);
        		
        		changeFontSize(defaultStyle, 1);
        		changeFontSize(selectionStyle, 1);
        		changeFontSize(headerStyle, 1);
        		
        		natTable.refresh(false);
        	}
		});

        Button decreaseFont = new Button(buttonPanel, SWT.PUSH);
        decreaseFont.setText("Decrease Font");
        decreaseFont.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		IStyle defaultStyle = configRegistry.getConfigAttribute(
        				CellConfigAttributes.CELL_STYLE, 
        				DisplayMode.NORMAL);
        		IStyle selectionStyle = configRegistry.getConfigAttribute(
        				CellConfigAttributes.CELL_STYLE, 
        				DisplayMode.SELECT);
        		IStyle headerStyle = configRegistry.getConfigAttribute(
        				CellConfigAttributes.CELL_STYLE, 
        				DisplayMode.NORMAL,
        				GridRegion.COLUMN_HEADER);
        		
        		changeFontSize(defaultStyle, -1);
        		changeFontSize(selectionStyle, -1);
        		changeFontSize(headerStyle, -1);
        		
        		natTable.refresh(false);
        	}
		});

        
        // add filter
        final TextMatcherEditor<NATNarrativeEntry> textMatcherEditor = new TextMatcherEditor<NATNarrativeEntry>(new TextFilterator<NATNarrativeEntry>() {

			@Override
			public void getFilterStrings(List<String> baseList, NATNarrativeEntry element) {
				baseList.add(element.getLog());
				baseList.add(element.getName());
				baseList.add(element.getType());
			}
		});
        textMatcherEditor.setMode(TextMatcherEditor.CONTAINS);
        filterRowHeaderLayer.getFilterStrategy().addStaticFilter(textMatcherEditor);
        
        // input listener
        filterInput.addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyPressed(KeyEvent e) {
        		if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
        			// update highlight
        			String input = filterInput.getText();
        			if (!input.isEmpty()) {
        				styleConfig.updateSearchHighlight("(" + input + ")");
        			} else {
        				styleConfig.updateSearchHighlight("");
        			}
        			
        			// update filter
        			textMatcherEditor.setFilterText(new String[] { input });
        			
            		natTable.refresh(false);
        		}
        	}
		});

        
        return container;
	}
	
	private void changeFontSize(IStyle style, int value) {
		Font font = style.getAttributeValue(CellStyleAttributes.FONT);
		FontData fd = font.getFontData()[0];
		fd.setHeight(fd.getHeight() + value);
		style.setAttributeValue(
				CellStyleAttributes.FONT,
				GUIHelper.getFont(fd));
	}
	
	/**
	 * The body layer stack for the viewer.
	 * 
	 * @param <T>
	 */
    class BodyLayerStack<T> extends AbstractLayerTransform {

        private final SortedList<T> sortedList;
        private final FilterList<T> filterList;

        private final IDataProvider bodyDataProvider;

        private GlazedListsEventLayer<T> glazedListsEventLayer;
        private ColumnReorderLayer columnReorderLayer;
        private ColumnHideShowLayer columnHideShowLayer;
        private SelectionLayer selectionLayer;
        private ViewportLayer viewportLayer;

        public BodyLayerStack(List<T> values,
                IColumnPropertyAccessor<T> columnPropertyAccessor) {
            // wrapping of the list to show into GlazedLists
            // see http://publicobject.com/glazedlists/ for further information
            EventList<T> eventList = GlazedLists.eventList(values);
            TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

            // use the SortedList constructor with 'null' for the Comparator
            // because the Comparator will be set by configuration
            this.sortedList = new SortedList<T>(rowObjectsGlazedList, null);
            // wrap the SortedList with the FilterList
            this.filterList = new FilterList<T>(getSortedList());

            this.bodyDataProvider = new ListDataProvider<T>(this.filterList, columnPropertyAccessor);
            DataLayer bodyDataLayer = new DataLayer(this.bodyDataProvider);
            
            bodyDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator(bodyDataProvider));
            
            // width configuration - last column should take remaining space
            bodyDataLayer.setColumnWidthByPosition(0, 100);
            bodyDataLayer.setColumnWidthByPosition(1, 100);
            bodyDataLayer.setColumnWidthByPosition(2, 100);
            bodyDataLayer.setColumnWidthByPosition(3, 100);
            bodyDataLayer.setColumnPercentageSizing(4, true);

            // layer for event handling of GlazedLists and PropertyChanges
            glazedListsEventLayer = new GlazedListsEventLayer<T>(bodyDataLayer, this.filterList);

            this.columnReorderLayer = new ColumnReorderLayer(glazedListsEventLayer);
            this.columnHideShowLayer = new ColumnHideShowLayer(this.columnReorderLayer);
            this.selectionLayer = new SelectionLayer(this.columnHideShowLayer);
            this.viewportLayer = new ViewportLayer(this.selectionLayer);

            addConfiguration(new DefaultEditBindings());
            addConfiguration(new DefaultEditConfiguration());

            setUnderlyingLayer(viewportLayer);
        }

        public SortedList<T> getSortedList() {
            return this.sortedList;
        }

        public FilterList<T> getFilterList() {
            return this.filterList;
        }

        public IDataProvider getBodyDataProvider() {
            return this.bodyDataProvider;
        }

        public GlazedListsEventLayer<T> getGlazedListsEventLayer() {
        	return this.glazedListsEventLayer;
        }
        
        public ColumnReorderLayer getColumnReorderLayer() {
            return this.columnReorderLayer;
        }

        public ColumnHideShowLayer getColumnHideShowLayer() {
            return this.columnHideShowLayer;
        }

        public SelectionLayer getSelectionLayer() {
            return this.selectionLayer;
        }

        public ViewportLayer getViewportLayer() {
            return this.viewportLayer;
        }

    }
	
	
	public static void main(String[] args) {
        // Setup
        Display display = Display.getDefault();
        Shell shell = new Shell(display, SWT.SHELL_TRIM);
        shell.setLayout(new FillLayout());
        shell.setSize(800, 600);
        shell.setText("Narrative Viewer");

        // Create example control
        Control exampleControl = new NarrativeViewerPart().createExampleControl(shell);

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        exampleControl.dispose();

        shell.dispose();
        display.dispose();
	}

  @Override
  public void createPartControl(Composite parent)
  {
    parent.setLayout(new FillLayout());
    createExampleControl(parent);
    
  }

  @Override
  public void setFocus()
  {
    // TODO Auto-generated method stub
    
  }
}
