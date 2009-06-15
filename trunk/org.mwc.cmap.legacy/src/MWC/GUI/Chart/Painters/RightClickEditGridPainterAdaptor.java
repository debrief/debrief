package MWC.GUI.Chart.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: RightClickEditGridPainterAdaptor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: RightClickEditGridPainterAdaptor.java,v $
// Revision 1.4  2005/09/13 09:30:23  Ian.Mayo
// Eclipse tidying
//
// Revision 1.3  2004/09/03 15:13:22  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.2  2004/05/25 14:47:00  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:16  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:12  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:15+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-03-19 11:05:04+00  administrator
// Switch to Swing menus
//
// Revision 1.1  2002-01-24 14:22:32+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.0  2001-07-17 08:46:29+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-21 21:36:15+00  novatech
// pass Layers around properly
//
// Revision 1.1  2001-01-03 13:43:00+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:13  ianmayo
// initial version
//
// Revision 1.4  2000-11-02 16:44:34+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.3  2000-09-26 09:48:31+01  ian_mayo
// make classes children of RightClickEditAdaptor, to provide built-in support for creating boolean and enumerated editors
//
// Revision 1.2  2000-01-20 10:12:51+00  ian_mayo
// changed method signatures
//
// Revision 1.1  1999-10-12 15:37:02+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-17 10:02:08+01  administrator
// Initial revision
//

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Chart.RightClickEdit;

public class RightClickEditGridPainterAdaptor extends RightClickEdit.BaseMenuCreator implements Serializable
{
	
	
  /////////////////////////////////////////////////////////////
  // member data
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void createMenu(javax.swing.JPopupMenu menu,
                         java.awt.Point thePoint,
                         MWC.GUI.CanvasType theCanvas,
                         MWC.GUI.Properties.PropertiesPanel thePanel,
                         Layers theData)
  {
    // also show the editors for any screen decorations
    final Layer decorations = theData.findLayer("Decorations");
    final PropertiesPanel _thePanel = thePanel;
    if (decorations != null)
    {
      Enumeration<Editable> decs = decorations.elements();
      while (decs.hasMoreElements())
      {
        Plottable p = (Plottable) decs.nextElement();
        final Editable.EditorType ped = p.getInfo();
        if (ped != null)
        {
          javax.swing.JMenuItem pmi = new javax.swing.JMenuItem("Edit " + ped.getBeanDescriptor().getDisplayName());
          pmi.addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              _thePanel.addEditor(ped, decorations);
            }
          });

          menu.add(pmi);

          // finally add the other editors
          super.createAdditionalItems(menu, theCanvas, thePanel, (Editable) ped.getData(), theData);

        }
      }
    }

  }
}
