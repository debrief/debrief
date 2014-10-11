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

