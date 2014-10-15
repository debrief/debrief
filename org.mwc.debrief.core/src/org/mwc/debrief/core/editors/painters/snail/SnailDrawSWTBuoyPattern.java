/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.editors.painters.snail;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawSWTBuoyPattern.java,v $
// @author $Author$
// @version $Revision$
// $Log: SnailDrawSWTBuoyPattern.java,v $
// Revision 1.1  2005/07/04 07:45:50  Ian.Mayo
// Initial snail implementation
//


import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import org.mwc.debrief.core.editors.painters.SnailHighlighter;
import org.mwc.debrief.core.editors.painters.SnailHighlighter.drawSWTHighLight;

import Debrief.Wrappers.BuoyPatternWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;


/** class to handle drawing a buoy pattern when we're in snail mode
 *
 */
public final class SnailDrawSWTBuoyPattern implements drawSWTHighLight, Editable
{

  ///////////////////////////////////
  // member functions
  //////////////////////////////////
	public final java.awt.Rectangle drawMe(final MWC.Algorithms.PlainProjection proj,
			final CanvasType dest, final WatchableList list, final Watchable watch,
			final SnailHighlighter parent, final HiResDate dtg, final Color backColor)
	{
    Rectangle thisR = null;

    // get a pointer to the fix
		final BuoyPatternWrapper.BuoyPatternAdaptor adaptor = (BuoyPatternWrapper.BuoyPatternAdaptor)watch;

    final BuoyPatternWrapper pattern = adaptor.getPattern();

    // get the current area of the watchable
    final WorldArea wa = watch.getBounds();
    // convert to screen coordinates
    final Point tl = new Point(proj.toScreen(wa.getTopLeft()));
    final Point br = new Point(proj.toScreen(wa.getBottomRight()));

    // and do the paint
    pattern.paint(dest);

    // extend the area covered to include the stick
    thisR =  new Rectangle(tl);
    thisR.add(br);

		return thisR;
	}

	public final boolean canPlot(final Watchable wt)
	{
		boolean res = false;

		if(wt instanceof Debrief.Wrappers.BuoyPatternWrapper.BuoyPatternAdaptor)
		{
			res = true;
		}
		return res;
	}



  public final String getName()
  {
    return "Snail Buoypattern Plotter";
  }

  public final String toString()
  {
    return getName();
  }

  public final boolean hasEditor()
  {
    return false;
  }

  public final Editable.EditorType getInfo()
  {
    return null;
  }

  //////////////////////////////////////////////////////////
  // accessors for editable parameters
  /////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////
  // nested editable class
  /////////////////////////////////////////////////////////


}

