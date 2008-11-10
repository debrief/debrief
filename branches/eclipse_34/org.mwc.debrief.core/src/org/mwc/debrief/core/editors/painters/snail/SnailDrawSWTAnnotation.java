package org.mwc.debrief.core.editors.painters.snail;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SnailDrawSWTAnnotation.java,v $
// @author $Author$
// @version $Revision$
// $Log: SnailDrawSWTAnnotation.java,v $
// Revision 1.1  2005/07/04 07:45:49  Ian.Mayo
// Initial snail implementation
//


import java.awt.*;

import org.mwc.debrief.core.editors.painters.SnailHighlighter;
import org.mwc.debrief.core.editors.painters.SnailHighlighter.drawSWTHighLight;

import Debrief.Tools.Tote.*;
import MWC.GUI.*;
import MWC.GenericData.*;


public final class SnailDrawSWTAnnotation implements drawSWTHighLight
{


  ///////////////////////////////////
  // member functions
  //////////////////////////////////
	public final java.awt.Rectangle drawMe(MWC.Algorithms.PlainProjection proj,
																	 CanvasType dest,
																	 WatchableList list,
																	 Watchable watch,
																	 SnailHighlighter parent,
																	 HiResDate dtg,
                                   java.awt.Color backColor)
	{
    if(dest instanceof Graphics2D)
    {
    	Graphics2D g2 = (Graphics2D) dest;
    	g2.setStroke(new BasicStroke(1));
    }
    
    PlainWrapper swa = (PlainWrapper)watch;

    swa.paint(dest);

		// get the current area of the watchable
		WorldArea wa = watch.getBounds();

		// convert to screen coordinates
		Point tl = proj.toScreen(wa.getTopLeft());

    int tlx = tl.x;
    int tly = tl.y;

		Point br = proj.toScreen(wa.getBottomRight());

		// get the width
		int wid = (br.x - tlx);
		int ht = (br.y - tly);

		// represent this area as a rectangle
		java.awt.Rectangle thisR = new Rectangle(tlx, br.y, wid, ht);

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

