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
			res = new SensorWrapper(coll.iterator());
		}

		return res;
	}
	
	public static final class SensorWrapper implements java.util.Enumeration<Editable>
	{
		private final Iterator<SensorType> _val;

		public SensorWrapper(final Iterator<SensorType> iterator)
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
