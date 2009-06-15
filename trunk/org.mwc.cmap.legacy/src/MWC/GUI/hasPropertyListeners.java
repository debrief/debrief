package MWC.GUI;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: hasPropertyListeners.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: hasPropertyListeners.java,v $
// Revision 1.2  2004/05/25 15:45:33  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:03  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:36+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:12+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:27+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:35+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:07+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:47  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:37:07+01  ian_mayo
// Initial revision
//
import java.beans.PropertyChangeListener;

public interface hasPropertyListeners
{
  /** the property changing data (add)
   */
  public void addPropertyChangeListener(PropertyChangeListener l);
  public void removePropertyChangeListener(PropertyChangeListener l);
  
}
