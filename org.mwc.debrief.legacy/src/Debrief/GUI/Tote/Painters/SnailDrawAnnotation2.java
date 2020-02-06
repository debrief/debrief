/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/

package Debrief.GUI.Tote.Painters;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import Debrief.GUI.Tote.Painters.SnailPainter2.ColorFadeCalculator;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;

public final class SnailDrawAnnotation2 implements SnailPainter2.drawHighLight2
{

  @Override
  public final boolean canPlot(final Watchable wt)
  {
    boolean res = false;

    if ((wt instanceof Debrief.Wrappers.ShapeWrapper)
        || (wt instanceof Debrief.Wrappers.LabelWrapper))
    {
      res = true;
    }
    return res;
  }

  ///////////////////////////////////
  // member functions
  //////////////////////////////////
  @Override
  public final java.awt.Rectangle drawMe(
      final MWC.Algorithms.PlainProjection proj, final java.awt.Graphics dest,
      final WatchableList list, final Watchable watch, final TotePainter parent,
      final HiResDate dtg, final ColorFadeCalculator fader)
  {
    if (dest instanceof Graphics2D)
    {
      final Graphics2D g2 = (Graphics2D) dest;
      g2.setStroke(new BasicStroke(1));
    }

    final PlainWrapper swa = (PlainWrapper) watch;

    final CanvasAdaptor cad = new CanvasAdaptor(proj, dest);

    swa.paint(cad);

    // get the current area of the watchable
    final WorldArea wa = watch.getBounds();

    // convert to screen coordinates
    final Point tl = proj.toScreen(wa.getTopLeft());

    final int tlx = tl.x;
    final int tly = tl.y;

    final Point br = proj.toScreen(wa.getBottomRight());

    // get the width
    final int wid = (br.x - tlx);
    final int ht = (br.y - tly);

    // represent this area as a rectangle
    final java.awt.Rectangle thisR = new Rectangle(tlx, br.y, wid, ht);

    return thisR;
  }

}
