package MWC.GUI.Tools.Operations;

// Copyright MWC 1999
// $RCSfile: ShowVideo.java,v $
// $Author: Ian.Mayo $
// $Log: ShowVideo.java,v $
// Revision 1.2  2004/05/25 15:44:11  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:45  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:01+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:06+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:58+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 13:03:38+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-07-30 15:38:00+01  administrator
// update, to pass in the address of the properties window, and to trigger creation of some JMF-related data just to check that we have JMF loaded (so the GUI can elect to not display this data - if it wishes)
//
// Revision 1.1  2001-07-27 17:08:45+01  administrator
// add as floating toolbar if possible
//
// Revision 1.0  2001-07-27 14:08:05+01  administrator
// Initial revision
//


import MWC.GUI.Tools.*;
import MWC.GUI.*;
import java.io.*;


 public class ShowVideo extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  MWC.GUI.Properties.PropertiesPanel _theProperties;

  java.awt.Component _theSubject;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  /** ShowVideo an existing data file
   * @param theParent parent application, where we can show the busy cursor
   * @param theLabel the label to put on the button
   * @param theSubject the GUI component we are going to watch
   */
  public ShowVideo(ToolParent theParent,
              MWC.GUI.Properties.PropertiesPanel thePanel,
              java.awt.Component theSubject){

    super(theParent, "Record video", "images/camera.gif");

    // store the properties window, it's the destination we have to use
    _theProperties = thePanel;

    // and remember the object we're listing to
    _theSubject = theSubject;

    // hey, just have a go at creating a JMF-dependent object
    // to check that the classes are loaded
    MWC.GUI.Video.SwingGrabControl ls = new MWC.GUI.Video.SwingGrabControl(_theSubject, theParent, thePanel);

    // ok, if we hadn't got the classes loaded an exception would have been thrown by now, so we
    // can drop out if we want to
    ls = null;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** collate the data ready to perform the operations
   */
  public Action getData()
  {

    Action res = null;

    java.awt.Component comp = new MWC.GUI.Video.SwingGrabControl(_theSubject, getParent(), _theProperties);

    // see if this is a swing editor - if so we will add our panel as a floatable toolbar
    if(_theProperties instanceof MWC.GUI.Properties.Swing.SwingPropertiesPanel)
    {
      MWC.GUI.Properties.Swing.SwingPropertiesPanel swPanel =
        (MWC.GUI.Properties.Swing.SwingPropertiesPanel)_theProperties;
      swPanel.addThisPanel(comp);
    }
    else
      _theProperties.add(comp);

    // return the product
    return res;
  }

}
