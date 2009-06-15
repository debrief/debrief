 package MWC.GUI.Tools.Operations;

// Copyright MWC 1999
// $RCSfile: Save.java,v $
// $Author: Ian.Mayo $
// $Log: Save.java,v $
// Revision 1.2  2004/05/25 15:44:08  Ian.Mayo
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
// Revision 1.1  2002-04-11 13:03:38+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:54+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:47  ianmayo
// initial version
//
// Revision 1.10  2000-11-22 15:39:28+00  ian_mayo
// stop ignoring the file suffix we receive
//
// Revision 1.9  2000-09-21 15:26:56+01  ian_mayo
// make lastDirectory visible through children
//
// Revision 1.8  2000-08-07 12:21:50+01  ian_mayo
// tidy icon filename
//
// Revision 1.7  2000-04-03 14:04:36+01  ian_mayo
// allow child implementation to override text/image in constructor
//
// Revision 1.6  2000-03-14 09:54:52+00  ian_mayo
// use icons for these tools
//
// Revision 1.5  2000-02-02 14:23:55+00  ian_mayo
// Workarounds to allow use of original Swing fileChooser, because of problems experienced when using IBM jre (also so that both types of dialog [open/save] return File objects rather  than just pathnames)
//
// Revision 1.4  1999-11-25 16:21:35+00  ian_mayo
// Handle user cancelling save operation
//


import java.io.File;

import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;

abstract public class Save extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  String _theSuffix;

  /** the last directory we opened from
   */
  protected String _lastDirectory;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public Save(ToolParent theParent,
              String theName,
              String theSuffix){
    this(theParent, theName, theSuffix, "images/save.gif");
  }

  public Save(ToolParent theParent,
              String theName,
              String theSuffix,
              String theImage)
  {
    super(theParent, theName, theImage);

    _theSuffix = theSuffix;
    _lastDirectory = "";
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  /** get a filename from the user
   */
  private File getFile(){
    return MWC.GUI.Dialogs.DialogFactory.getNewFile(_theSuffix,
                                                    "Debrief Plots",
                                                    _lastDirectory);
  }

  /** collate the data ready to perform the operations
   */
  public Action getData()
  {
    Action res = null;

    // get the filename of the file to import
    File theFile = getFile();

    if(theFile != null)
    {
      _lastDirectory = theFile.getParent();

      String fn = theFile.getPath();
      res = doSave(fn);
    }

    return res;
  }

  abstract protected Action doSave(String filename);


  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  protected class SavePlotAction implements Action{
    /** store the name of the session we have saved
     */
    protected String _theSessionName;

    public SavePlotAction(String theName){
      _theSessionName = theName;
    }

    public boolean isRedoable(){
      return false;
    }


    public boolean isUndoable(){
      return false;
    }

    public String toString(){
      return "Save " + _theSessionName;
    }

    public void undo(){
      // delete the plottables from the Application object
    }

    public void execute(){
    }

  }

}
