package org.mwc.cmap.xyplot.views.providers;

import java.util.Collection;
import java.util.Iterator;

import org.jfree.data.xy.XYSeries;
import org.mwc.cmap.xyplot.views.ILocationCalculator;
import org.mwc.cmap.xyplot.views.LocationCalculator;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public class CrossSectionDatasetProvider implements ICrossSectionDatasetProvider
{
	private ILocationCalculator _calc;
	
	public CrossSectionDatasetProvider(final int units)
	{
		_calc = new LocationCalculator(units);
	}

	@Override
	public XYSeries getSeries(final LineShape line, final TrackWrapper wlist, 
			final HiResDate startT, final 	HiResDate endT) 
	{
		final Collection<Editable> wbs  = wlist.getItemsBetween(startT, endT);
	    final Iterator<Editable> itr = wbs.iterator();
	    final XYSeries series = new XYSeries(wlist.getName());
        while (itr.hasNext()) 
        {
        	final Editable ed = itr.next();
        	if (ed instanceof Watchable) 
        	{
        		final Double x_coord = new Double(_calc.getDistance(line, (Watchable) ed));
        		final Double y_coord = new Double(((Watchable) ed).getDepth());
        		series.add(x_coord, y_coord);
        	}		    			
        }	 
		return series;
	}

	@Override
	public XYSeries getSeries(final LineShape line, final TrackWrapper wlist, 
			final HiResDate timeT) 
	{
		final Watchable[] wbs = wlist.getNearestTo(timeT);
		final XYSeries series = new XYSeries(wlist.getName());
		for(final Watchable wb: wbs)
		{		    					
			final Double x_coord = new Double(_calc.getDistance(line, wb));
        	final Double y_coord = new Double(wb.getDepth());
			series.add(x_coord, y_coord);    		        		
		}
		return series;
	}

}
