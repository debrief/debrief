package org.mwc.cmap.xyplot.views.providers;

import java.awt.Color;
import java.util.Map;

import org.jfree.data.xy.XYSeriesCollection;

import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;

public interface ICrossSectionDatasetProvider 
{
	//TODO: javadoc
	XYSeriesCollection getDataset(final LineShape line, final Layers layers, 
			final HiResDate startT, final HiResDate endT);
	XYSeriesCollection getDataset(final LineShape line, final Layers layers, 
			final HiResDate timeT);
	
	Map<Integer, Color> getSeriesColors();
}
