package org.mwc.cmap.NarrativeViewer2;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;

/**
 * {@link IConfigLabelAccumulator} that is used to attach labels according to the source value of a narrative entry.
 * Needs to be set to the body DataLayer in order to avoid index-position transformations.
 */
public class NarrativeEntryConfigLabelAccumulator implements IConfigLabelAccumulator {

	private IRowDataProvider<INatEntry> dataProvider;
	private IConfigRegistry configRegistry;
	
	public NarrativeEntryConfigLabelAccumulator(IRowDataProvider<INatEntry> dataProvider, IConfigRegistry configRegistry) {
		this.dataProvider = dataProvider;
		this.configRegistry = configRegistry;
	}

	@Override
	public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
		INatEntry entry = dataProvider.getRowObject(rowPosition);
		if (entry != null) {
			configLabels.addLabel(entry.getName().toUpperCase());
			registerStylesForSource(configRegistry, entry.getName().toUpperCase());
		}
	}

	// TODO this could be optimized so the styles get only registered once with the known values
	// this approach is very dynamic as it reacts dynamically on occuring values
	public void registerStylesForSource(IConfigRegistry configRegistry, String source) {
		Style style = new Style();
		style.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, colorFor(source));
		configRegistry.registerConfigAttribute(
				CellConfigAttributes.CELL_STYLE, 
				style,
				DisplayMode.NORMAL,
				source);
	}
	
	public Color colorFor(String source) {
		if (source.equalsIgnoreCase("NELSON")) {
			return GUIHelper.COLOR_RED;
		} else {
			return GUIHelper.COLOR_BLUE;
		}
	}

}
