// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainTool.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: PlainTool.java,v $
// Revision 1.2  2004/05/25 15:43:39  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:41  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:57+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:12+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:49+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-10-29 12:57:34+00  administrator
// Check we have our parent
//
// Revision 1.0  2001-07-17 08:43:01+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:58+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:09  ianmayo
// initial version
//
// Revision 1.6  2000-04-19 11:41:20+01  ian_mayo
// white space only
//
// Revision 1.5  2000-04-12 10:45:23+01  ian_mayo
// provide better support for garbage collection (close method)
//
// Revision 1.4  2000-03-14 09:55:15+00  ian_mayo
// process icon names
//
// Revision 1.3  1999-12-03 14:33:05+00  ian_mayo
// added method to set label parameter
//
// Revision 1.2  1999-11-23 10:37:10+00  ian_mayo
// implemented functionality for it to be an action listener
//
// Revision 1.1  1999-10-12 15:36:27+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-17 08:14:41+01  administrator
// changes to way layers data is passed, and how rubberbands are handled
//
// Revision 1.1  1999-07-27 10:50:33+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-23 14:03:45+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.2  1999-07-16 10:01:43+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:03+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:56+01  sm11td
// Initial revision
//
// Revision 1.4  1999-06-01 16:49:23+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.3  1999-02-04 08:02:27+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 16:08:50+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:10+00  sm11td
// Initial revision
//

package MWC.GUI.Tools;

import MWC.GUI.Tool;
import MWC.GUI.ToolParent;


/** a GUI-independent tool implementation, mostly for commands
 * which are 'operations', ie do a single GUI-independent process
 * immediately the button is pressed
 */
abstract public class PlainTool implements Tool {
  /////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////
  /** the parent class, where we have control over the cursor shape
   */
  ToolParent _theParent;
  /** the image to show to represent this command on a toolbar
   */
  private String image = null;
  /** the label to use to represent this command
   */
  private String _label;

  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
  public PlainTool(ToolParent theParent, String theLabel, String theImage){
    _theParent = theParent;
    if(theLabel == null){
      // check we have a label, if so use it, else
      _label = "blank";
    }
    else
    {
      _label = theLabel;
    }

    // check we have an image, if not then
    // create one
    if(theImage == null){
      // create new image
      image = null;
    }
    else{
      image = theImage;
    }
  }

  public PlainTool()
  {
    // default constructor, for serialisation
  }

  /////////////////////////////////////////////////////////
  // member function
  /////////////////////////////////////////////////////////

	/** allow us to be defined as an action listener
	 */
	public void actionPerformed(java.awt.event.ActionEvent p1)
	{
		execute();
	}


/** the job of the execute function is to collate
  * the data necessary for the command to take place, then
  * call the function specific command in the 'Action'
  * object
  */
  public void execute(){

    // start busy
    setBusy(true);

    // create the memento
    Action newAction = getData();

    // do the action
    if(newAction != null)
    {
      newAction.execute();

      // check that the action is undoable, before we stick it on the buffer
      if(newAction.isUndoable()){
        // store the event
        // put it on the buffer
        if(_theParent != null)
          _theParent.addActionToBuffer(newAction);
      }
    }
    // end busy
    setBusy(false);
  }


  public void setBusy(boolean isBusy){
    if(_theParent != null){
      if(isBusy)
        _theParent.setCursor(java.awt.Cursor.WAIT_CURSOR);
      else
        _theParent.restoreCursor();
    }
  }

  /** abstract definition - this is where the class retrieves the data
   * necessary for the operation
   */
  public abstract Action getData();

  /** get the label (name) for this command*/
  public String getLabel(){
    return _label;
  }

	public void setLabel(String val)
	{
		_label = val;
	}

  /** get the image represented by this command*/
  public String getImage(){
    return image;
  }


  public ToolParent getParent(){
    return _theParent;
  }


	/** provide a method which will allow us to close (finalise) the tool
	 */
	public void close()
	{
		_theParent = null;
		image = null;
		_label = null;
	}

}








