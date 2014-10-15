/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.operations;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.mwc.cmap.core.DataTypes.Temporal.ControllablePeriod;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

public class PlotOperations implements ControllablePeriod
{

	/**
	 * store our list of operations
	 */
	private final Vector<ControllablePeriod.AnOperation> myOperations;

	/**
	 * store the time period
	 */
	private TimePeriod _myPeriod;

	/**
	 * constructor - get the ball rolling
	 */
	public PlotOperations()
	{
		super();
		myOperations = new Vector<ControllablePeriod.AnOperation>(0, 1);
		myOperations.add(new FilterToTimePeriod());
	}

	protected static class FilterToTimePeriod implements ControllablePeriod.AnOperation
	{

		public String getName()
		{
			return ControllablePeriod.FILTER_TO_TIME_PERIOD;
		}

		public String getDescription()
		{
			return "Hide all time-related data points outside time period";
		}

		public Vector<Layer> apply(final TimePeriod period, final Object[] selection)
		{
			
			final Vector<Layer> res = new Vector<Layer>(0,1);
			
			// right, pass through the series
			for (int i = 0; i < selection.length; i++)
			{
				Layer useThis = null;
				
				final Object thisO = selection[i];
				// is it a watchable?
				if (thisO instanceof WatchableList)
				{
					final WatchableList wa = (WatchableList) thisO;
					wa.filterListTo(period.getStartDTG(), period.getEndDTG());
					useThis = (Layer) wa;
				}
				else if(thisO instanceof Layer)
				{
					// hey, pass through this layer
					final Layer thisL = (Layer) thisO;
					final Enumeration<Editable> enumer = thisL.elements();
					while(enumer.hasMoreElements())
					{
						final Plottable thisP = (Plottable) enumer.nextElement();
						if(thisP instanceof WatchableList)
						{
							final WatchableList wl = (WatchableList) thisP;
							wl.filterListTo(period.getStartDTG(), period.getEndDTG());
							
							if(useThis == null)
								useThis = thisL;
							
						}
					}
				}
				if(useThis != null)
				{
					res.add(useThis);
				}
			}
			
			return res;
		}

	}

	/**
	 * @return
	 */
	public Vector<ControllablePeriod.AnOperation>  getOperations()
	{
		return myOperations;
	}

	/**
	 * @return
	 */
	public TimePeriod getPeriod()
	{
		return _myPeriod;
	}

	/**
	 * @param operationName
	 */
	public Vector<Layer> performOperation(final AnOperation operationName)
	{
		// right, find the operation with the correct name
		final Vector<Layer> res = operationName.apply(getPeriod(), getTargets());
		return res;
	}

	/**
	 * @param period
	 */
	public void setPeriod(final TimePeriod period)
	{
		_myPeriod = period;
	}

	public Vector<Layer> performOperation(final String operationName)
	{
		Vector<Layer> res = null;
		
		// right, run through the ops to find a matching one
		for (final Iterator<AnOperation> iter = myOperations.iterator(); iter.hasNext();)
		{
			final AnOperation op = (AnOperation) iter.next();
			if (op.getName().equals(operationName))
			{
				res = performOperation(op);
				break;
			}
		}
		
		return res;

	}

	public Object[] getTargets()
	{
		return null;
	}

}
