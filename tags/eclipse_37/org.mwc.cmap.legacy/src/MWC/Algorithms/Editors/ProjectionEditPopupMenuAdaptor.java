package MWC.Algorithms.Editors;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ProjectionEditPopupMenuAdaptor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: ProjectionEditPopupMenuAdaptor.java,v $
// Revision 1.4  2006/02/09 15:31:40  Ian.Mayo
// Minor tidying
//
// Revision 1.3  2006/02/09 15:25:18  Ian.Mayo
// Minor tidying
//
// Revision 1.2  2004/05/24 16:29:00  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:13  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:06:59  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:33+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:37+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-03-19 11:05:05+00  administrator
// Switch to Swing menus
//
// Revision 1.2  2002-01-24 14:22:30+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.1  2001-08-21 12:08:25+01  administrator
// Replace anonymous listener with named class
//
// Revision 1.0  2001-07-17 08:47:00+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-21 21:36:15+00  novatech
// pass Layers around properly
//
// Revision 1.1  2001-01-03 13:43:12+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:28  ianmayo
// initial version
//
// Revision 1.3  2000-09-26 09:48:34+01  ian_mayo
// make classes children of RightClickEditAdaptor, to provide built-in support for creating boolean and enumerated editors
//
// Revision 1.2  2000-01-20 10:12:51+00  ian_mayo
// changed method signatures
//
// Revision 1.1  1999-10-12 15:37:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:04:55+01  administrator
// Initial revision
//

import java.awt.Point;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Chart.RightClickEdit;

public class ProjectionEditPopupMenuAdaptor extends RightClickEdit.BaseMenuCreator
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////


	public void createMenu(javax.swing.JPopupMenu menu,
												 Point thePoint,
												 CanvasType theCanvas,
												 PropertiesPanel thePanel,
												 Layers theData)
	{
    final Editable.EditorType et = theCanvas.getProjection().getInfo();
    // is this projection editable?
    if(et != null)
    {
      javax.swing.JMenuItem mi = new javax.swing.JMenuItem("Edit " + et.getBeanDescriptor().getDisplayName());
      mi.addActionListener(new RightClickEdit.EditThisActionListener(thePanel, et, null));
      menu.add(mi);

      // finally add the other editors
      super.createAdditionalItems(menu, theCanvas, thePanel, (Editable)et.getData(), theData);
    }
	}
}
