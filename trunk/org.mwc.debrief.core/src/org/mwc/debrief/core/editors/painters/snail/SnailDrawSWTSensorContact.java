package org.mwc.debrief.core.editors.painters.snail;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawSWTSensorContact.java,v $
// @author $Author$
// @version $Revision$
// $Log: SnailDrawSWTSensorContact.java,v $
// Revision 1.2  2005/07/11 11:51:33  Ian.Mayo
// Do tidying as recommended by Eclipse
//
// Revision 1.1  2005/07/04 07:45:52  Ian.Mayo
// Initial snail implementation
//


import Debrief.Tools.Tote.Watchable;


/**
 * Class to perform custom plotting of Sensor data,
 * when in a Snail-mode.  (this may include Snail-mode or relative-mode).
 */
public final class SnailDrawSWTSensorContact  extends SnailDrawSWTTacticalContact
{


  ////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////
  public SnailDrawSWTSensorContact(final SnailDrawSWTFix plotter)
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

