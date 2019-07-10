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
package Debrief.GUI.Tote.Painters;

import java.awt.Graphics;


import java.awt.Point;
import java.awt.Rectangle;

import Debrief.GUI.Tote.Painters.SnailPainter2.ColorFadeCalculator;
import Debrief.Wrappers.BuoyPatternWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;


/** class to handle drawing a buoy pattern when we're in snail mode
 *
 */
public final class SnailDrawBuoyPattern2 implements SnailPainter2.drawHighLight2, Editable
{

  ///////////////////////////////////
  // member functions
  //////////////////////////////////
  public final Rectangle drawMe(final MWC.Algorithms.PlainProjection proj,
      final Graphics dest,
      final WatchableList list,
      final Watchable watch,
      final TotePainter parent,
      final HiResDate dtg,
      final ColorFadeCalculator fader)
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

    // create our own canvas object
    final CanvasAdaptor cad = new CanvasAdaptor(proj, dest);

    // and do the paint
    pattern.paint(cad);

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
    return "Snail Bouypattern Plotter";
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
}

