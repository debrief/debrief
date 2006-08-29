package ASSET.GUI.Workbench.Plotters;

import java.util.Enumeration;

import ASSET.Models.Sensor.SensorList;
import MWC.GUI.Plottables;

public class SensorsPlottable extends BasePlottable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SensorsPlottable(SensorList sensorFit)
	{
		super(sensorFit);
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
