package org.mwc.cmap.NarrativeViewer2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.swt.graphics.Color;

/**
 * {@link IConfigLabelAccumulator} that is used to attach labels according to the source value of a narrative entry.
 * Needs to be set to the body DataLayer in order to avoid index-position transformations.
 */
public class NarrativeEntryConfigLabelAccumulator implements IConfigLabelAccumulator {

	private IRowDataProvider<INatEntry> dataProvider;
	private IConfigRegistry configRegistry;

	// track which sources we have registered
  private List<String> processedSources = new ArrayList<String>();


	public NarrativeEntryConfigLabelAccumulator(IRowDataProvider<INatEntry> dataProvider, IConfigRegistry configRegistry) {
		this.dataProvider = dataProvider;
		this.configRegistry = configRegistry;
	}

	@Override
	public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
		INatEntry entry = dataProvider.getRowObject(rowPosition);
    String uName = entry.getName().toUpperCase();
    
    // tell this cell that it has a style label to apply
    configLabels.addLabel(uName);
    
    // have we already processed it?
    if(!processedSources.contains(uName))
    {
      // generate a style with this name
      registerStylesForSource(configRegistry, uName, entry.getColor());
      
      // remember the fact that we've created it
      processedSources.add(uName);
    }
	}

	private void registerStylesForSource(IConfigRegistry configRegistry, String source, Color color) {
	    // ok, generate it
	    Style style = new Style();
	    style.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, color);
	    configRegistry.registerConfigAttribute(
	        CellConfigAttributes.CELL_STYLE, 
	        style,
	        DisplayMode.NORMAL,
	        source);
	}
}
