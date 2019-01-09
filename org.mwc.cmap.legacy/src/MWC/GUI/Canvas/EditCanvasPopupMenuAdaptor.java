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
package MWC.GUI.Canvas;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: EditCanvasPopupMenuAdaptor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: EditCanvasPopupMenuAdaptor.java,v $
// Revision 1.3  2005/12/09 14:53:49  Ian.Mayo
// Minor tidying
//
// Revision 1.2  2004/05/25 14:43:53  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:06  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-10-28 09:23:31+00  ian_mayo
// support line widths
//
// Revision 1.2  2002-05-28 09:25:37+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:09+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:20+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-03-19 11:05:05+00  administrator
// Switch to Swing menus
//
// Revision 1.2  2002-01-24 14:22:30+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.1  2001-08-21 12:08:18+01  administrator
// Replace anonymous listener with named class
//
// Revision 1.0  2001-07-17 08:46:32+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-21 21:36:16+00  novatech
// pass Layers around properly
//
// Revision 1.1  2001-01-03 13:43:03+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:05  ianmayo
// initial version
//
// Revision 1.4  2000-09-26 09:48:32+01  ian_mayo
// make classes children of RightClickEditAdaptor, to provide built-in support for creating boolean and enumerated editors
//
// Revision 1.3  2000-08-14 15:49:05+01  ian_mayo
// name change
//
// Revision 1.2  2000-01-20 10:12:52+00  ian_mayo
// changed method signatures
//
// Revision 1.1  2000-01-12 15:37:10+00  ian_mayo
// Initial revision
//


import java.io.Serializable;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Chart.RightClickEdit;

public class EditCanvasPopupMenuAdaptor extends RightClickEdit.BaseMenuCreator implements Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
	protected Editable	_theCanvasEditor;


  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
	public EditCanvasPopupMenuAdaptor(final CanvasType theCanvas)
	{
		if(theCanvas instanceof Editable)
		{
			_theCanvasEditor = (Editable) theCanvas;
		}
		else
			_theCanvasEditor = null;
	}

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public void createMenu(final javax.swing.JPopupMenu menu,
                         final java.awt.Point thePoint,
                         final MWC.GUI.CanvasType theCanvas,
                         final PropertiesPanel thePanel,
                         final Layers theData)
  {
		// see if our canvas is editable
		if(_theCanvasEditor != null)
		{
			final Editable.EditorType et = _theCanvasEditor.getInfo();

			if(et != null)
			{
			  final javax.swing.JMenuItem mi = new javax.swing.JMenuItem("Edit Appearance");
        mi.addActionListener(new RightClickEdit.EditThisActionListener(thePanel, et, null));
			  menu.add(mi);

        // finally add the other editors
        super.createAdditionalItems(menu, thePanel, (Editable)et.getData(), theData);

			}
		}
  }
}
