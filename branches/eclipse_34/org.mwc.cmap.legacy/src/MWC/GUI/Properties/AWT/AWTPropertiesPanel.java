package MWC.GUI.Properties.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTPropertiesPanel.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTPropertiesPanel.java,v $
// Revision 1.2  2004/05/25 15:29:24  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:25  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:45+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:33+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:22:31+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.0  2001-07-17 08:43:47+01  administrator
// Initial revision
//
// Revision 1.4  2001-07-12 12:11:53+01  novatech
// pass the tool editor to the child classes
//
// Revision 1.3  2001-07-05 11:58:32+01  novatech
// add mock method to meet requirements of Interface
//
// Revision 1.2  2001-01-05 09:09:26+00  novatech
// Create type of editor called "Constructor", which requires particular processing from the properties panel (renaming "Apply" to "Build").  Also provide button renaming method
//
// Revision 1.1  2001-01-03 13:42:43+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:26  ianmayo
// initial version
//
// Revision 1.6  2000-10-09 13:35:50+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.5  2000-08-23 09:36:25+01  ian_mayo
// tidied up
//
// Revision 1.4  2000-04-12 10:44:27+01  ian_mayo
// white space only
//
// Revision 1.3  2000-01-14 11:57:59+00  ian_mayo
// added method to return the UndoBuffer
//
// Revision 1.2  1999-11-18 11:10:45+00  ian_mayo
// AWTTabPanel name changed
//
// Revision 1.1  1999-10-12 15:36:47+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:45:29+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:50:41+01  administrator
// Initial revision
//
// Revision 1.5  1999-07-27 09:26:13+01  administrator
// switching to bean-based editing
//
// Revision 1.4  1999-07-23 14:03:50+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.3  1999-07-16 10:01:45+01  administrator
// Nearing end of phase 2
//
// Revision 1.2  1999-07-12 08:09:18+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-08 13:09:15+01  administrator
// Initial revision
//

import MWC.GUI.TabPanel.*;
import MWC.GUI.Properties.*;
import MWC.GUI.*;
import MWC.GUI.Undo.*;
import java.awt.*;
import java.beans.*;

public class AWTPropertiesPanel extends AWTTabPanel implements PropertiesPanel
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  PlainChart _myChart;
  UndoBuffer _theBuffer;

  /** the toolparent we supply to any new panels
   *
   */
  MWC.GUI.ToolParent _theToolParent;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public AWTPropertiesPanel(PlainChart theChart,
                            UndoBuffer theUndoBuffer,
                            MWC.GUI.ToolParent theToolParent){
    super();
    _myChart = theChart;
    _theBuffer = theUndoBuffer;
    _theToolParent = theToolParent;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////


  public void remove(Object theObject)
  {
    // ignore it!
  }

  public void removeMe(Panel thePage){
    // find the index of the panel
    super.removeTabPanel(super.getCurrentPanelNdx());
  }


  /** put the specified panel to the top of the stack
   */
  public void show(java.awt.Panel thePanel){
    int idx = super.getPanelTabIndex(thePanel);
    try{
    super.setCurrentPanelNdx(idx);
    }catch(Exception e){
      // we don't really expect anything to happen here.
      MWC.Utilities.Errors.Trace.trace(e);
    }
  }

  public void addConstructor(Editable.EditorType theInfo, Layer parentLayer){
    AWTPropertyEditor ap = new AWTPropertyEditor(theInfo,
                                                 this,
                                                 _myChart,
                                                 _theToolParent,
                                                 parentLayer);
    ap.setNames("Build", null, null);
    int index = addTabPanel(theInfo.getName(), true, ap.getPanel());
    try{
      setCurrentTab(index);
    }catch(Exception e){
      // don't bother, we don't expect there to be a problem
      MWC.Utilities.Errors.Trace.trace(e);
    }
  }

  public void addEditor(Editable.EditorType theInfo, Layer parentLayer){
    AWTPropertyEditor ap = new AWTPropertyEditor(theInfo,
                                                 this,
                                                 _myChart,
                                                 _theToolParent, parentLayer);
    int index = addTabPanel(theInfo.getName(), true, ap.getPanel());
    try{
      setCurrentTab(index);
    }catch(Exception e){
      // don't bother, we don't expect there to be a problem
      MWC.Utilities.Errors.Trace.trace(e);
    }
  }

  public void doApply(){
    _myChart.update();
  }

  public UndoBuffer getBuffer(){
    return _theBuffer;
  }

}
