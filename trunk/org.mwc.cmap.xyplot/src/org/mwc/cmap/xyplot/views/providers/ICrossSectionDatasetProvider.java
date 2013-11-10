package org.mwc.cmap.xyplot.views.providers;

import org.jfree.data.xy.XYSeries;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;

public interface ICrossSectionDatasetProvider 
{
	//TODO: javadoc
	XYSeries getSeries(final LineShape line, final TrackWrapper wlist, 
			final HiResDate startT, final HiResDate endT);
	XYSeries getSeries(final LineShape line, final TrackWrapper wlist, 
			final HiResDate timeT);
}
