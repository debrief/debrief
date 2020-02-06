/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package org.mwc.cmap.NarrativeViewer2;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.combobox.ComboBoxFilterRowConfiguration;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
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

	public NarrativeViewerFilterRowConfiguration(final IComboBoxDataProvider comboBoxDataProvider) {
		super(comboBoxDataProvider);
	}

	@Override
	public void configureRegistry(final IConfigRegistry configRegistry) {

		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, this.cellEditor, DisplayMode.NORMAL,
				FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, this.cellEditor, DisplayMode.NORMAL,
				FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);

		// by default, prevent the columns from being editable
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE,
				DisplayMode.NORMAL, GridRegion.FILTER_ROW);

		// let the source column be editable
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
				DisplayMode.NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);

		// let the type column be editable
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
				DisplayMode.NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);

		configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
				TextMatchingMode.REGULAR_EXPRESSION);

		final ICellPainter cellPainter = new CellPainterDecorator(new TextPainter() {
			{
				this.paintFg = false;
			}

			// override the preferred width and height to be 0, as otherwise
			// the String that is generated in the background for multiple
			// selection will be taken into account for auto resizing

			@Override
			public int getPreferredHeight(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
				return 0;
			}

			@Override
			public int getPreferredWidth(final ILayerCell cell, final GC gc, final IConfigRegistry configRegistry) {
				return 0;
			}
		}, CellEdgeEnum.RIGHT, this.filterIconPainter);

		// by default, use plain painter for all columns
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new BackgroundPainter(),
				DisplayMode.NORMAL, GridRegion.FILTER_ROW);
		// use special painter for source column
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, cellPainter, DisplayMode.NORMAL,
				FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);

		// use special painter for type column
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, cellPainter, DisplayMode.NORMAL,
				FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
	}

}
