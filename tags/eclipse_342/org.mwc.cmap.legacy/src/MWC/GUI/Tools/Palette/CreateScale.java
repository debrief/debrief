// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CreateScale.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: CreateScale.java,v $
// Revision 1.2  2004/05/25 15:44:21  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:46  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:02+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:04+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:35+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-23 13:27:55+01  administrator
// Reflect new signature for PlainCreate class, to allow it to fireExtended()
//
// Revision 1.0  2001-07-17 08:42:52+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:40+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:58  ianmayo
// initial version
//

package MWC.GUI.Tools.Palette;

import MWC.GUI.Chart.Painters.ScalePainter;

public class CreateScale extends PlainCreate
{
	public CreateScale(MWC.GUI.ToolParent theParent,
										MWC.GUI.Properties.PropertiesPanel thePanel,
										MWC.GUI.Layer theLayer,
										MWC.GUI.Layers theData,
										MWC.GUI.PlainChart theChart)
	{
		super(theParent, thePanel, theLayer, theData, theChart, "Scale", "images/scale.gif");
	}

	protected MWC.GUI.Plottable createItem(MWC.GUI.PlainChart theChart)
	{
		return new ScalePainter();
	}
}
