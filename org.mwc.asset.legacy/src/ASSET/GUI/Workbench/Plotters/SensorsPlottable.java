/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.GUI.Workbench.Plotters;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.SensorList;
import MWC.GUI.*;

public class SensorsPlottable extends BasePlottable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SensorsPlottable(SensorList sensorFit, Layer parentLayer)
	{
		super(sensorFit, parentLayer);
	}

	public SensorList getSensorFit()
	{
		return (SensorList) getModel();
	}
	
	public Enumeration<Editable> elements()
	{
		Enumeration<Editable> res = null;

		// hmm, do we have child behaviours?
		if (getModel() instanceof SensorList)
		{
			SensorList bl = (SensorList) getModel();
			Collection<SensorType> coll = bl.getSensors();
			res = new SensorPlottableWrapper(coll.iterator());
		}

		return res;
	}
	
	public static final class SensorPlottableWrapper implements java.util.Enumeration<Editable>
	{
		private final Iterator<SensorType> _val;

		public SensorPlottableWrapper(final Iterator<SensorType> iterator)
		{
			_val = iterator;
		}

		public final boolean hasMoreElements()
		{
			return _val.hasNext();

		}

		public final Editable nextElement()
		{
			return _val.next();
		}
	}
}
