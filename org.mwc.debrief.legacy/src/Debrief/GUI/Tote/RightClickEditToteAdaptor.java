package Debrief.GUI.Tote;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: RightClickEditToteAdaptor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: RightClickEditToteAdaptor.java,v $
// Revision 1.3  2005/12/13 09:04:30  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/09/09 10:51:52  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.1.1.2  2003/07/21 14:47:13  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:38:02+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:53+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:05+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-03-19 11:10:19+00  administrator
// Reflect switch to Swing menu items
// Only show separators if we have added menu items
//
// Revision 1.2  2002-01-25 13:32:04+00  administrator
// Reflect new signature for CreateMenu
//
// Revision 1.1  2001-08-21 12:15:15+01  administrator
// Replace anonymous listeners with named class (to remove final objects)
//
// Revision 1.0  2001-07-17 08:41:41+01  administrator
// Initial revision
//
// Revision 1.4  2001-01-17 13:22:31+00  novatech
// reflect name change from Field to Pattern
//
// Revision 1.3  2001-01-05 09:16:30+00  novatech
// Handle right-clicking on BuoyField
//
// Revision 1.2  2001-01-04 14:01:39+00  novatech
// handle right-clicks on buoyfields (where we may wish to add the buoy or the field
//
// Revision 1.1  2001-01-03 13:40:54+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:18  ianmayo
// initial import of files
//
// Revision 1.1  2000-07-12 08:35:23+01  ian_mayo
// Initial revision
//
// Revision 1.2  2000-01-20 10:11:12+00  ian_mayo
// changes to signatures
//
// Revision 1.1  1999-10-12 15:34:23+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-04 10:53:01+01  administrator
// Initial revision
//


import Debrief.Wrappers.BuoyPatternWrapper;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Chart.RightClickEdit;
import MWC.GenericData.WatchableList;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class RightClickEditToteAdaptor implements RightClickEdit.PlottableMenuCreator
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private final AnalysisTote _theTote;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public RightClickEditToteAdaptor(final AnalysisTote theTote)
  {
    _theTote = theTote;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public final void createMenu(final javax.swing.JPopupMenu menu,
                               final Editable data,
                               final Point thePoint,
                               final CanvasType theCanvas,
                               final PropertiesPanel thePanel,
                               final Layer theParent,
                               final Layers theData, final Layer updateLayer)
  {
    WatchableList wl = null;
    WatchableList buoy = null;
    WatchableList buoyF = null;

    boolean addedMenuItem = false;


    // see what kind of object has been clicked on
    if (data instanceof FixWrapper)
    {
      final FixWrapper fw = (FixWrapper) data;
      wl = fw.getTrackWrapper();
    }
    else if (data instanceof LabelWrapper)
    {
      // this may be a buoyfield, have a look at the parent
      final LabelWrapper lw = (LabelWrapper) data;
      final Editable ed = lw.getParent();
      if (ed == null)
      {
        // this is a plain symbol, without a parent
        wl = lw;
      }
      else
      {
        if (ed instanceof BuoyPatternWrapper)
        {
          // so, yes it is a buoyfield, better remember this!
          final BuoyPatternWrapper bw = (BuoyPatternWrapper) ed;
          buoyF = bw;
          buoy = lw;
        }
      }
    }
    else if (data instanceof BuoyPatternWrapper)
    {
      buoyF = (BuoyPatternWrapper) data;
    }
    else if (data instanceof WatchableList)
    {
      wl = (WatchableList) data;
    }

    // sort out the plain items first
    if (wl != null)
    {
      // and add our new wonder item
      final javax.swing.JMenuItem setPri = new javax.swing.JMenuItem("Set Primary Track");
      setPri.addActionListener(new DoSetPrimary(_theTote, wl));

      final javax.swing.JMenuItem setSec = new javax.swing.JMenuItem("Set Secondary Track");
      setSec.addActionListener(new DoSetSecondary(_theTote, wl));

      menu.add(setPri);
      menu.add(setSec);

      addedMenuItem = true;
    }

    // now sort out the buoyfield related items
    // first the individual buoy
    if (buoy != null)
    {
      // and add our new wonder item
      final javax.swing.JMenuItem setPri2 = new javax.swing.JMenuItem("Set Buoy as Primary Track");
      setPri2.addActionListener(new DoSetPrimary(_theTote, wl));

      final javax.swing.JMenuItem setSec2 = new javax.swing.JMenuItem("Set Buoy as Secondary Track");
      setSec2.addActionListener(new DoSetSecondary(_theTote, wl));

      menu.add(setPri2);
      menu.add(setSec2);

      addedMenuItem = true;
    }

    // now the field itself
    if (buoyF != null)
    {
      // and add our new wonder item
      final javax.swing.JMenuItem setPri3 = new javax.swing.JMenuItem("Set BuoyField as Primary Track");
      setPri3.addActionListener(new DoSetPrimary(_theTote, wl));

      final javax.swing.JMenuItem setSec3 = new javax.swing.JMenuItem("Set BuoyField as Secondary Track");
      setSec3.addActionListener(new DoSetSecondary(_theTote, wl));

      menu.add(setPri3);
      menu.add(setSec3);

      addedMenuItem = true;

    }

    // add a separator if we've added any items
    if (addedMenuItem)
      menu.addSeparator();

  }

  static final class DoSetPrimary implements ActionListener
  {
    private AnalysisTote _myTote;
    private WatchableList _myList;

    public DoSetPrimary(final AnalysisTote tote, final WatchableList list)
    {
      _myTote = tote;
      _myList = list;
    }

    public final void actionPerformed(final ActionEvent e)
    {
      _myTote.setPrimary(_myList);
      _myTote = null;
      _myList = null;
    }
  }

  static final class DoSetSecondary implements ActionListener
  {
    private AnalysisTote _myTote;
    private WatchableList _myList;

    public DoSetSecondary(final AnalysisTote tote, final WatchableList list)
    {
      _myTote = tote;
      _myList = list;
    }

    public final void actionPerformed(final ActionEvent e)
    {
      _myTote.setSecondary(_myList);
      _myTote = null;
      _myList = null;
    }
  }


}