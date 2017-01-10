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
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
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

import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;

public class NarrativeViewerPart {

	private boolean wordWrap = true;
	
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

        // property names of the NarrativeEntry class
        String[] propertyNames = { "date", "time", "name", "type", "log" };

        // mapping from property to label, needed for column header labels
        Map<String, String> propertyToLabelMap = new HashMap<String, String>();
        propertyToLabelMap.put("date", "Date");
        propertyToLabelMap.put("time", "Time");
        propertyToLabelMap.put("name", "Source");
        propertyToLabelMap.put("type", "Type");
        propertyToLabelMap.put("log", "Entry");

        final IColumnPropertyAccessor<INatEntry> columnPropertyAccessor =
                new ReflectiveColumnPropertyAccessor<INatEntry>(propertyNames);

        List<INatEntry> input = NarrativeValueParser.getInput();
        BodyLayerStack<INatEntry> bodyLayer =
                new BodyLayerStack<INatEntry>(
                        input,
                        columnPropertyAccessor);
        bodyLayer.addConfigLabelAccumulator(
        		new NarrativeEntryConfigLabelAccumulator(bodyLayer.getBodyDataProvider(), configRegistry));

        IDataProvider columnHeaderDataProvider =
                new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);
        DataLayer columnHeaderDataLayer =
                new DataLayer(columnHeaderDataProvider);
        ColumnHeaderLayer columnHeaderLayer =
                new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayer, bodyLayer.getSelectionLayer());

        SortHeaderLayer<INatEntry> sortHeaderLayer =
                new SortHeaderLayer<INatEntry>(
                        columnHeaderLayer,
                        new GlazedListsSortModel<INatEntry>(
                                bodyLayer.getSortedList(),
                                columnPropertyAccessor,
                                configRegistry,
                                columnHeaderDataLayer));

        GlazedListsFilterRowComboBoxDataProvider<INatEntry> comboBoxDataProvider = 
        		new GlazedListsFilterRowComboBoxDataProvider<INatEntry>(
        				bodyLayer.getGlazedListsEventLayer(),
        				bodyLayer.getSortedList(),
        				columnPropertyAccessor);
        ComboBoxFilterRowHeaderComposite<INatEntry> filterRowHeaderLayer =
                new ComboBoxFilterRowHeaderComposite<INatEntry>(
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

        Button toggleWrapping = new Button(buttonPanel, SWT.PUSH);
        toggleWrapping.setText("Toggle Word Wrap");
        toggleWrapping.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		wordWrap = !wordWrap;
    			configRegistry.registerConfigAttribute(
    					CellConfigAttributes.CELL_PAINTER, 
    					wordWrap ? styleConfig.wrappingAutomaticRowHeightPainter : styleConfig.automaticRowHeightPainter,
    					DisplayMode.NORMAL,
    					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 4);
        		
        		natTable.refresh(false);
        	}
		});
        
        // add filter
        final TextMatcherEditor<INatEntry> textMatcherEditor = new TextMatcherEditor<INatEntry>(new TextFilterator<INatEntry>() {

			@Override
			public void getFilterStrings(List<String> baseList, INatEntry element) {
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
}
