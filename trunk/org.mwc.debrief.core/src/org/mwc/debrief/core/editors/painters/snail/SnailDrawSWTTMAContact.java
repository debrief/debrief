package org.mwc.debrief.core.editors.painters.snail;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawSWTTMAContact.java,v $
// @author $Author$
// @version $Revision$
// $Log: SnailDrawSWTTMAContact.java,v $
// Revision 1.2  2005/07/11 11:51:34  Ian.Mayo
// Do tidying as recommended by Eclipse
//
// Revision 1.1  2005/07/04 07:45:52  Ian.Mayo
// Initial snail implementation
//


import Debrief.Wrappers.TMAContactWrapper;
import MWC.GenericData.Watchable;


/**
 * Class to perform custom plotting of tma solution data,
 * when in a Snail-mode.  (this may include Snail-mode or relative-mode).
 */
public final class SnailDrawSWTTMAContact extends SnailDrawSWTTacticalContact
{

  ////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////
  public SnailDrawSWTTMAContact(final SnailDrawSWTFix plotter)
  {
    _fixPlotter = plotter;
  }

  ///////////////////////////////////
  // member functions
  //////////////////////////////////

  public final boolean canPlot(final Watchable wt)
	{
		boolean res = false;

		if(wt instanceof TMAContactWrapper)
		{
			res = true;
		}
		return res;
	}
}

