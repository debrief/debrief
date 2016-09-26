/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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
