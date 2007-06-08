package org.mwc.cmap.plotViewer.editors.operations;

import java.util.*;

import org.mwc.cmap.core.DataTypes.Temporal.ControllablePeriod;

import Debrief.Tools.Tote.WatchableList;
import MWC.GUI.*;
import MWC.GenericData.TimePeriod;

public class PlotOperations implements ControllablePeriod
{

	/**
	 * store our list of operations
	 */
	private Vector myOperations;

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
		myOperations = new Vector(0, 1);
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

		public void apply(TimePeriod period, Object[] selection)
		{
			// right, pass through the series
			for (int i = 0; i < selection.length; i++)
			{
				Object thisO = selection[i];
				// is it a watchable?
				if (thisO instanceof WatchableList)
				{
					WatchableList wa = (WatchableList) thisO;
					wa.filterListTo(period.getStartDTG(), period.getEndDTG());
				}
				else if(thisO instanceof Layer)
				{
					// hey, pass through this layer
					Layer thisL = (Layer) thisO;
					Enumeration enumer = thisL.elements();
					while(enumer.hasMoreElements())
					{
						Plottable thisP = (Plottable) enumer.nextElement();
						if(thisP instanceof WatchableList)
						{
							WatchableList wl = (WatchableList) thisP;
							wl.filterListTo(period.getStartDTG(), period.getEndDTG());
						}
					}
				}

			}
		}

	}

	/**
	 * @return
	 */
	public Vector getOperations()
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
	public void performOperation(AnOperation operationName)
	{
		// right, find the operation with the correct name

		operationName.apply(getPeriod(), getTargets());
	}

	/**
	 * @param period
	 */
	public void setPeriod(TimePeriod period)
	{
		_myPeriod = period;
	}

	public void performOperation(String operationName)
	{
		// right, run through the ops to find a matching one
		for (Iterator iter = myOperations.iterator(); iter.hasNext();)
		{
			AnOperation op = (AnOperation) iter.next();
			if (op.getName().equals(operationName))
			{
				performOperation(op);
			}
		}

	}

	public Object[] getTargets()
	{
		return null;
	}

}
