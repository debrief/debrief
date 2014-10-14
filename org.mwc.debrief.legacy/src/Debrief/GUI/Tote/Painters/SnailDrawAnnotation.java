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
package Debrief.GUI.Tote.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawAnnotation.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: SnailDrawAnnotation.java,v $
// Revision 1.5  2005/12/13 09:04:24  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2005/06/13 11:01:19  Ian.Mayo
// Override dest line width: since the annotation parent never gets consulted anyway
//
// Revision 1.3  2005/06/13 10:20:43  Ian.Mayo
// Correct how we record the changed coordinates for plotting an annotation
//
// Revision 1.2  2004/11/25 10:24:01  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:19  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:38:01+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:56+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:57+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:38+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-24 11:35:52+00  novatech
// recognise optimised toScreen handling which reduces object creation
//
// Revision 1.2  2001-01-15 11:20:41+00  novatech
// use the new method signature
//
// Revision 1.1  2001-01-03 13:40:52+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:33  ianmayo
// initial import of files
//
// Revision 1.2  2000-10-10 14:07:33+01  ian_mayo
// reflect name change
//
// Revision 1.1  2000-09-26 09:55:32+01  ian_mayo
// Initial revision
//

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import MWC.GUI.PlainWrapper;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;


public final class SnailDrawAnnotation implements SnailPainter.drawHighLight
{


  ///////////////////////////////////
  // member functions
  //////////////////////////////////
	public final java.awt.Rectangle drawMe(final MWC.Algorithms.PlainProjection proj,
																	 final java.awt.Graphics dest,
																	 final WatchableList list,
																	 final Watchable watch,
																	 final SnailPainter parent,
																	 final HiResDate dtg,
                                   final java.awt.Color backColor)
	{
    dest.setXORMode(backColor);

    if(dest instanceof Graphics2D)
    {
    	final Graphics2D g2 = (Graphics2D) dest;
    	g2.setStroke(new BasicStroke(1));
    }
    
    final PlainWrapper swa = (PlainWrapper)watch;

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

