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

package MWC.TacticalData.temporal;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeWrapper;

public class PlotOperations implements ControllablePeriod {

	protected static class FilterToTimePeriod implements ControllablePeriod.AnOperation {

		@Override
		public Vector<Layer> apply(final TimePeriod period, final Object[] selection) {

			final Vector<Layer> res = new Vector<Layer>(0, 1);

			// right, pass through the series
			for (int i = 0; i < selection.length; i++) {
				Layer useThis = null;

				final Object thisO = selection[i];
				// is it a watchable?
				if (thisO instanceof WatchableList) {
					final WatchableList wa = (WatchableList) thisO;
					wa.filterListTo(period.getStartDTG(), period.getEndDTG());
					useThis = (Layer) wa;
				} else if (thisO instanceof NarrativeWrapper) {
					// hey, pass through this layer
					final NarrativeWrapper thisL = (NarrativeWrapper) thisO;
					thisL.filterListTo(period);
					useThis = thisL;
				} else if (thisO instanceof Layer) {
					// hey, pass through this layer
					final Layer thisL = (Layer) thisO;
					final Enumeration<Editable> enumer = thisL.elements();
					while (enumer.hasMoreElements()) {
						final Plottable thisP = (Plottable) enumer.nextElement();
						if (thisP instanceof WatchableList) {
							final WatchableList wl = (WatchableList) thisP;
							wl.filterListTo(period.getStartDTG(), period.getEndDTG());

							if (useThis == null)
								useThis = thisL;
						}
					}
				}
				if (useThis != null) {
					res.add(useThis);
				}
			}

			return res;
		}

		@Override
		public String getDescription() {
			return "Hide all time-related data points outside time period";
		}

		@Override
		public String getName() {
			return ControllablePeriod.FILTER_TO_TIME_PERIOD;
		}

	}

	/**
	 * store our list of operations
	 */
	private final Vector<ControllablePeriod.AnOperation> myOperations;

	/**
	 * store the time period
	 */
	protected TimePeriod _myPeriod;

	/**
	 * constructor - get the ball rolling
	 */
	public PlotOperations() {
		super();
		myOperations = new Vector<ControllablePeriod.AnOperation>(0, 1);
		myOperations.add(new FilterToTimePeriod());
	}

	/**
	 * @return
	 */
	@Override
	public Vector<ControllablePeriod.AnOperation> getOperations() {
		return myOperations;
	}

	/**
	 * @return
	 */
	@Override
	public TimePeriod getPeriod() {
		return _myPeriod;
	}

	public Object[] getTargets() {
		return null;
	}

	/**
	 * @param operationName
	 */
	@Override
	public Vector<Layer> performOperation(final AnOperation operationName) {
		// right, find the operation with the correct name
		final Vector<Layer> res = operationName.apply(getPeriod(), getTargets());
		return res;
	}

	@Override
	public Vector<Layer> performOperation(final String operationName) {
		Vector<Layer> res = null;

		// right, run through the ops to find a matching one
		for (final Iterator<AnOperation> iter = myOperations.iterator(); iter.hasNext();) {
			final AnOperation op = iter.next();
			if (op.getName().equals(operationName)) {
				res = performOperation(op);
				break;
			}
		}

		return res;

	}

	/**
	 * @param period
	 */
	@Override
	public void setPeriod(final TimePeriod period) {
		_myPeriod = period;
	}

}
