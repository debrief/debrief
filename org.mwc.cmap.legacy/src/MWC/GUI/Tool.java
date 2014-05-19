// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Tool.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: Tool.java,v $
// Revision 1.2  2004/05/25 15:45:52  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:04  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:34+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:32+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:38+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:09+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:03  ianmayo
// initial version
//
// Revision 1.4  2000-04-12 10:45:22+01  ian_mayo
// provide better support for garbage collection (close method)
//
// Revision 1.3  2000-03-14 09:55:31+00  ian_mayo
// process image names
//
// Revision 1.2  1999-11-23 10:36:55+00  ian_mayo
// Made into ActionListener, may prove useful one day
//
// Revision 1.1  1999-10-12 15:37:12+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:52+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:10+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:01+01  sm11td
// Initial revision
//
// Revision 1.3  1999-06-01 16:49:22+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.2  1999-02-04 08:02:27+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-02-01 14:25:07+00  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:33:10+00  sm11td
// Initial revision
//

package MWC.GUI;

/** definition of things to be implemented by a command
 */
public interface Tool extends java.awt.event.ActionListener
{
  /** do your operation
   */
  public void execute();
  /** prepare the data for your operation
   */
  public MWC.GUI.Tools.Action getData();
  /** what text do we display
   */
  public String getLabel();
  /** what image do we display
   */
  public String getImage();

	/** also allow us to be defined as an action listener
	 */
	public void actionPerformed(java.awt.event.ActionEvent p1);
	
	/** provide a method which will allow us to close (finalise) the tool
	 */
	public void close();
}
