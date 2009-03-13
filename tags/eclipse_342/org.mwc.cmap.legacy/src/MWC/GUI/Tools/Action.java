// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Action.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: Action.java,v $
// Revision 1.3  2006/05/02 13:25:44  Ian.Mayo
// Minor tidying
//
// Revision 1.2  2004/05/25 15:43:33  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:24  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:41  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:58+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:47+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:00+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:55+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:03  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:25+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:32+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:43+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:02+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:56+01  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:33:09+00  sm11td
// Initial revision
//

package MWC.GUI.Tools;



/**
 * parent class defining behavior of a tool, either on a toolbar or menu
 */
public interface Action {
	/**
	 * @return boolean flag to describe whether this operation may be undone
	 */
  boolean isUndoable();
  /**
	 * @return boolean flag to indicate whether this action may be redone
	 */
  boolean isRedoable();
  /**
	 * method to produce string describing the activity waiting on the buffer
	 */
  String toString();
  /**
	 * this method calls the 'undo' event in the parent tool, passing the
	 * necessary data to it
	 */
  void undo();
  /**
	 * this method calls the 'do' event in the parent tool, passing the necessary
	 * data to it
	 */
  void execute();

}







