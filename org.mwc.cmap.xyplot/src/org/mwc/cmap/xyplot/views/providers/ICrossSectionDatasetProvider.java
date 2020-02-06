
package org.mwc.cmap.xyplot.views.providers;

import java.awt.Color;
import java.util.Map;

import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.xyplot.views.CrossSectionViewer.SnailRenderer;

import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;

public interface ICrossSectionDatasetProvider 
{
	//TODO: javadoc
	XYSeriesCollection getDataset(final LineShape line, final Layers layers, 
			final HiResDate startT);
	XYSeriesCollection getDataset(final LineShape line, final Layers layers, 
			final HiResDate timeT, final HiResDate endT, final SnailRenderer _snailRenderer);
	
	Map<Integer, Color> getSeriesColors();
}
