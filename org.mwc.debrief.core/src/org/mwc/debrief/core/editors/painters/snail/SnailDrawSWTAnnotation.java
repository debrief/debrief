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
// $RCSfile: SnailDrawSWTAnnotation.java,v $
// @author $Author$
// @version $Revision$
// $Log: SnailDrawSWTAnnotation.java,v $
// Revision 1.1  2005/07/04 07:45:49  Ian.Mayo
// Initial snail implementation
//


import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.mwc.debrief.core.editors.painters.SnailHighlighter;
import org.mwc.debrief.core.editors.painters.SnailHighlighter.drawSWTHighLight;

import MWC.GUI.CanvasType;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;


public final class SnailDrawSWTAnnotation implements drawSWTHighLight
{


  ///////////////////////////////////
  // member functions
  //////////////////////////////////
	public final java.awt.Rectangle drawMe(final MWC.Algorithms.PlainProjection proj,
																	 final CanvasType dest,
																	 final WatchableList list,
																	 final Watchable watch,
																	 final SnailHighlighter parent,
																	 final HiResDate dtg,
                                   final java.awt.Color backColor)
	{
    if(dest instanceof Graphics2D)
    {
    	final Graphics2D g2 = (Graphics2D) dest;
    	g2.setStroke(new BasicStroke(1));
    }
    
    final PlainWrapper swa = (PlainWrapper)watch;

    swa.paint(dest);

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

	public final boolean canPlot(final Watchable wt)
	{
		boolean res = false;

		if((wt instanceof Debrief.Wrappers.ShapeWrapper) || (wt instanceof Debrief.Wrappers.LabelWrapper))
		{
			res = true;
		}
		return res;
	}



}

