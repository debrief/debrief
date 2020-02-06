
package Debrief.GUI.Tote.Painters;

import MWC.GenericData.Watchable;


/**
 * Class to perform custom plotting of Sensor data,
 * when in a Snail-mode.  (this may include Snail-mode or relative-mode).
 */
public final class SnailDrawSensorContact  extends SnailDrawTacticalContact
{


  ////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////
  public SnailDrawSensorContact(final SnailDrawFix plotter)
  {
    _fixPlotter = plotter;
  }


	public final boolean canPlot(final Watchable wt)
	{
		boolean res = false;

		if(wt instanceof Debrief.Wrappers.SensorContactWrapper)
		{
			res = true;
		}
		return res;
	}


}

