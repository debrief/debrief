package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SteppingBoundedIntegerEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SteppingBoundedIntegerEditor.java,v $
// Revision 1.2  2004/05/25 15:29:09  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:42+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:43+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:44+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:52+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:49+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:21  ianmayo
// initial version
//
// Revision 1.1  2000-10-03 09:39:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  2000-09-26 10:51:56+01  ian_mayo
// Initial revision
//


import java.beans.PropertyEditorSupport;

public abstract class SteppingBoundedIntegerEditor extends PropertyEditorSupport 
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
	/** the value we are editing
	 */
  protected SteppingBoundedInteger _myVal;
	
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /** indicate that we can't just be painted, we've got to be edited
   */
  public boolean isPaintable()
  {
    return false;
  }

	/** build the editor
	 */
  abstract public java.awt.Component getCustomEditor();

	/** store the new value
	 */

  public void setValue(Object p1)
  {
		// try to catch if we are receiving a null (uninitialised) value
		if(p1 == null)
			p1 = new SteppingBoundedInteger(1, 1, 10, 1);
		
    if(p1 instanceof SteppingBoundedInteger)
    {
      SteppingBoundedInteger val = (SteppingBoundedInteger)p1;
			// take duplicate of bounded integer value - so that we are not editing
			// the original one
      _myVal = new SteppingBoundedInteger(val.getCurrent(), 
                                          val.getMin(), 
                                          val.getMax(),
                                          val.getStep());
      
      // also trigger a reset data - to update the GUI
      resetData();
      
    }
    else
      return;
  }


	/** return flag to say that we'd rather use our own (custom) editor
	 */
  public boolean supportsCustomEditor()
  {
    return true;
  }

	/** extract the values currently stored in the text boxes
	 */
  public Object getValue()
  {
		SteppingBoundedInteger res = null;
		res = _myVal;
    return res;
  }

	/** put the data into the text fields, if they have been
	 * created yet
	 */
  abstract public void resetData();
  
}
