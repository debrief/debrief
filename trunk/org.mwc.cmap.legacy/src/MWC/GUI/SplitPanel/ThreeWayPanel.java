package MWC.GUI.SplitPanel;

// Copyright MWC 1999
// $RCSfile: ThreeWayPanel.java,v $
// $Author: Ian.Mayo $
// $Log: ThreeWayPanel.java,v $
// Revision 1.2  2004/05/25 15:36:59  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:24  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:38  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:56+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:48+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:06+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:12+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:46  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:34+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-04 09:43:06+01  administrator
// make tools serializable
//
// Revision 1.1  1999-07-27 10:50:36+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-27 09:24:59+01  administrator
// set RightHand pane to be border layout
//
// Revision 1.1  1999-07-08 13:09:14+01  administrator
// Initial revision
//

import java.awt.*;

public class ThreeWayPanel extends SplitPanel
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Panel _right;
  Panel _topLeft;
  Panel _bottomLeft;

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public ThreeWayPanel(){
    super();
    setGap(1);
    setDividerColor(Color.red);
    
    // set the background colour
    setBackground(Color.lightGray);
    
    // create the panels
    _right = new Panel();
    _right.setLayout(new BorderLayout());
    _topLeft = new Panel();
    _bottomLeft = new Panel();
    
    // and now add them to the interface
    add(_right, 
        new PaneConstraints("Right", 
                            "Right", 
                            PaneConstraints.ROOT, 
                            1.0f));
    add(_topLeft, 
        new PaneConstraints("TopLeft", 
                            "Right", 
                            PaneConstraints.LEFT, 
                            0.4f));
    add(_bottomLeft, 
        new PaneConstraints("BottomLeft", 
                            "TopLeft", 
                            PaneConstraints.BOTTOM, 
                            0.7f));
  }

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////
  public Panel getRight(){
    return _right;
  }
  
  public Panel getTopLeft(){
    return _topLeft;
  }
  
  public Panel getBottomLeft(){
    return _bottomLeft;
  }
}
