package MWC.GUI;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Moveable.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: Moveable.java,v $
// Revision 1.2  2004/05/25 15:45:36  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:03  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:35+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:13+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:29+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:36+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:07+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:52  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:37:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:04:23+01  administrator
// Initial revision
//

import MWC.GenericData.*;

public interface Moveable
{
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  
  /** find the data area occupied by this item
   */
  public WorldArea getBounds();

  /** apply the necessary movement (during drag)
   */
  public void dragBy(WorldVector wv);
  
  /** and apply the final movement
   */
  public void doMove(WorldLocation start, WorldLocation end);
  
  /** get the current origin of this item (to support undo operation)
   */
  public WorldLocation getLocation();
  
}
