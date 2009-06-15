package MWC.GUI.Properties;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: WorldLocationPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: WorldLocationPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:29:15  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:25  Ian.Mayo
// Initial import
//
// Revision 1.5  2002-07-08 11:53:00+01  ian_mayo
// <>
//
// Revision 1.4  2002-06-05 12:56:24+01  ian_mayo
// unnecessarily loaded
//
// Revision 1.3  2002-05-31 16:24:20+01  ian_mayo
// Implement doClose method
//
// Revision 1.2  2002-05-28 09:25:41+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:46+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:54+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:50+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:22  ianmayo
// initial version
//
// Revision 1.4  1999-11-18 11:10:02+00  ian_mayo
// move AWT/Swing specific behaviour into separate classes
//
// Revision 1.3  1999-11-15 15:43:18+00  ian_mayo
// implementing dialog editor for locations
//
// Revision 1.2  1999-11-11 18:18:00+00  ian_mayo
// extensions to allow pop-up editor to be used
//
// Revision 1.1  1999-10-12 15:36:51+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:05:58+01  administrator
// Initial revision
//

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;

import MWC.GUI.PlainChart;
import MWC.GenericData.WorldLocation;

abstract public class WorldLocationPropertyEditor extends 
           PropertyEditorSupport implements ActionListener, 
                                            PlainPropertyEditor.EditorUsesChart,
                                            PlainChart.ChartDoubleClickListener
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
	/** the value we are editing
	 */
  protected WorldLocation _myVal;

	/** the chart object which we are letting the user select from
	 */
  protected PlainChart _theChart;
  
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

  abstract public java.awt.Component getCustomEditor();
  abstract protected void resetData();
  abstract public void actionPerformed(ActionEvent p1);

  

  public void setValue(Object p1)
  {
    if(p1 instanceof WorldLocation)
    {
      _myVal = (WorldLocation) p1;
      resetData();
    }
    else
      return;
  }

  public boolean supportsCustomEditor()
  {
    return true;
  }

  public Object getValue()
  {
    return _myVal;
  }

	public void setChart(MWC.GUI.PlainChart theChart)
  {
    _theChart = theChart;
  }


  abstract public void cursorDblClicked(PlainChart theChart,
                               WorldLocation theLocation, 
                               Point thePoint);
}
