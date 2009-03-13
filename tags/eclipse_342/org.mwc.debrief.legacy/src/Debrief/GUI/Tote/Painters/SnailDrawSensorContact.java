package Debrief.GUI.Tote.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawSensorContact.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: SnailDrawSensorContact.java,v $
// Revision 1.5  2005/12/13 09:04:25  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2005/02/22 09:31:56  Ian.Mayo
// Refactor snail plotting sensor & tma data - so that getting & managing valid data points are handled in generic fashion.  We did have two very similar implementations, tracking errors introduced after hi-res-date changes was proving expensive/unreliable.  All fine now though.
//
// Revision 1.3  2005/01/24 10:29:00  Ian.Mayo
// Pass the parent track to the item when painting in snail mode.  We were just passing null before for sensor data
//
// Revision 1.2  2004/11/25 10:24:03  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:21  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:38:00+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:00+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:17+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:50+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:59+01  ian_mayo
// Initial revision
//
// Revision 1.8  2002-03-18 20:39:07+00  administrator
// Add flag to indicate whether to show line styles (although we still don't show styles in snail mode)
//
// Revision 1.7  2002-03-12 11:07:49+00  administrator
// We already get value in millis - we don't need to perform conversion
//
// Revision 1.6  2002-01-22 09:10:47+00  administrator
// Correctly handle introduction of new Duration object
//
// Revision 1.5  2001-10-24 09:07:58+01  administrator
// Fix for sensor wrapper which returns null sensors, also ensure that sensor lines fade with time (not with number of contacts returned).
//
// Revision 1.4  2001-10-22 11:26:37+01  administrator
// Handle instance where no contacts get returned
//
// Revision 1.3  2001-10-03 16:06:11+01  administrator
// Rename cursor to display
//
// Revision 1.2  2001-08-21 15:19:27+01  administrator
// Reset color if null, since this is a valid colour after all
//
// Revision 1.1  2001-08-17 08:07:11+01  administrator
// Remove unnecessary items
//
// Revision 1.0  2001-08-14 14:01:43+01  administrator
// Initial revision
//

import Debrief.Tools.Tote.Watchable;


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

