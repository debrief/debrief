package ASSET.GUI.Workbench.Plotters;

import java.util.Enumeration;

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
	
	public Enumeration elements()
	{
		Enumeration res = null;

		// hmm, do we have child behaviours?
		if (getModel() instanceof SensorList)
		{
			SensorList bl = (SensorList) getModel();
			res = new Plottables.IteratorWrapper(bl.getSensors().iterator());
		}

		return res;
	}
}
