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

package ASSET.GUI.Workbench.Plotters;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.SensorList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;

public class SensorsPlottable extends BasePlottable {

	public static final class SensorPlottableWrapper implements java.util.Enumeration<Editable> {
		private final Iterator<SensorType> _val;

		public SensorPlottableWrapper(final Iterator<SensorType> iterator) {
			_val = iterator;
		}

		@Override
		public final boolean hasMoreElements() {
			return _val.hasNext();

		}

		@Override
		public final Editable nextElement() {
			return _val.next();
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SensorsPlottable(final SensorList sensorFit, final Layer parentLayer) {
		super(sensorFit, parentLayer);
	}

	@Override
	public Enumeration<Editable> elements() {
		Enumeration<Editable> res = null;

		// hmm, do we have child behaviours?
		if (getModel() instanceof SensorList) {
			final SensorList bl = (SensorList) getModel();
			final Collection<SensorType> coll = bl.getSensors();
			res = new SensorPlottableWrapper(coll.iterator());
		}

		return res;
	}

	public SensorList getSensorFit() {
		return (SensorList) getModel();
	}
}
