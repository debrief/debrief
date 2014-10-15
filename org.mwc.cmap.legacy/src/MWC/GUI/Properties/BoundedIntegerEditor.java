/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: BoundedIntegerEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: BoundedIntegerEditor.java,v $
// Revision 1.2  2004/05/25 15:28:43  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:23  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:44+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:35+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:47+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:45+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:08  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:51:56+01  ian_mayo
// Initial revision
//


import java.beans.PropertyEditorSupport;

abstract public class BoundedIntegerEditor extends 
           PropertyEditorSupport 
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
	/** the value we are editing
	 */
  protected BoundedInteger _myVal;
	
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

  public void setValue(final Object p1)
  {
	  Object obj = p1;
	  
		// try to catch if we are receiving a null (uninitialised) value
		if(obj == null)
			obj = new BoundedInteger(1, 1, 10);
		
    if(obj instanceof BoundedInteger)
    {
      final BoundedInteger val = (BoundedInteger)obj;
			// take duplicate of bounded integer value - so that we are not editing
			// the original one
      _myVal = new BoundedInteger(val.getCurrent(), val.getMin(), val.getMax());
      
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
		BoundedInteger res = null;
		res = _myVal;
    return res;
  }

	/** put the data into the text fields, if they have been
	 * created yet
	 */
  abstract public void resetData();
  
}
