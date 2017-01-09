package org.mwc.cmap.NarrativeViewer2;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.EditableRule;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.combobox.ComboBoxFilterRowConfiguration;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.swt.graphics.GC;

/**
 * Custom implementation of {@link ComboBoxFilterRowConfiguration} that only
 * applies the filter editors to the source and the type column.
 */
public class NarrativeViewerFilterRowConfiguration extends ComboBoxFilterRowConfiguration {

	public NarrativeViewerFilterRowConfiguration(IComboBoxDataProvider comboBoxDataProvider) {
		super(comboBoxDataProvider);
	}
	
    @Override
    public void configureRegistry(IConfigRegistry configRegistry) {

        configRegistry.registerConfigAttribute(
                EditConfigAttributes.CELL_EDITOR, 
                this.cellEditor,
                DisplayMode.NORMAL, 
                FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
        configRegistry.registerConfigAttribute(
        		EditConfigAttributes.CELL_EDITOR, 
        		this.cellEditor,
        		DisplayMode.NORMAL, 
        		FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);

        configRegistry.registerConfigAttribute(
        		EditConfigAttributes.CELL_EDITABLE_RULE, 
        		EditableRule.ALWAYS_EDITABLE,
        		DisplayMode.NORMAL,
        		FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
        configRegistry.registerConfigAttribute(
        		EditConfigAttributes.CELL_EDITABLE_RULE, 
        		EditableRule.ALWAYS_EDITABLE,
        		DisplayMode.NORMAL,
        		FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);
        
        configRegistry.registerConfigAttribute(
                FilterRowConfigAttributes.TEXT_MATCHING_MODE,
                TextMatchingMode.REGULAR_EXPRESSION);

        ICellPainter cellPainter = new CellPainterDecorator(new TextPainter() {
            {
                this.paintFg = false;
            }

            // override the preferred width and height to be 0, as otherwise
            // the String that is generated in the background for multiple
            // selection will be taken into account for auto resizing

            @Override
            public int getPreferredWidth(ILayerCell cell, GC gc,
                    IConfigRegistry configRegistry) {
                return 0;
            }

            @Override
            public int getPreferredHeight(ILayerCell cell, GC gc,
                    IConfigRegistry configRegistry) {
                return 0;
            }
        }, CellEdgeEnum.RIGHT, this.filterIconPainter);

        configRegistry.registerConfigAttribute(
                CellConfigAttributes.CELL_PAINTER, 
                new BackgroundPainter(),
                DisplayMode.NORMAL, 
                FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
        configRegistry.registerConfigAttribute(
        		CellConfigAttributes.CELL_PAINTER, 
        		new BackgroundPainter(),
        		DisplayMode.NORMAL, 
        		FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
        configRegistry.registerConfigAttribute(
        		CellConfigAttributes.CELL_PAINTER, 
        		new BackgroundPainter(),
        		DisplayMode.NORMAL, 
        		FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);

        configRegistry.registerConfigAttribute(
                CellConfigAttributes.CELL_PAINTER, 
                cellPainter,
                DisplayMode.NORMAL, 
                FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
        configRegistry.registerConfigAttribute(
        		CellConfigAttributes.CELL_PAINTER, 
        		cellPainter,
        		DisplayMode.NORMAL, 
        		FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);
    }

}
