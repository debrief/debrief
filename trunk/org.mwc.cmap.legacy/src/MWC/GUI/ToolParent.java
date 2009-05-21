package MWC.GUI;

// Copyright MWC 1999
// $RCSfile: ToolParent.java,v $
// $Author: Ian.Mayo $
// $Log: ToolParent.java,v $
// Revision 1.3  2005/09/07 15:35:42  Ian.Mayo
// Refactor ToolParent so we can log errors through it
//
// Revision 1.2  2004/05/25 15:45:54  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:05  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:34+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:15+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:33+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-07-18 16:00:46+01  administrator
// add method to give pattern matching from properties
//
// Revision 1.0  2001-07-17 08:46:39+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:10+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:03  ianmayo
// initial version
//
// Revision 1.3  2000-09-21 12:21:47+01  ian_mayo
// provide "setProperty" method in interface
//
// Revision 1.2  2000-08-30 14:48:02+01  ian_mayo
// added getProperty method
//
// Revision 1.1  1999-10-12 15:37:13+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:52+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-16 10:01:47+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:10+01  administrator
// Initial revision
//
import MWC.GUI.Tools.*;

/** interface of methods to be provided by a
 * parent of a tool, mostly providing functionality
 * necessary setting a busy cursor
 */
public interface ToolParent extends ErrorLogger
{
  public void setCursor(int theCursor);
  public void restoreCursor();
  public void addActionToBuffer(Action theAction);
  public String getProperty(String name);
  public java.util.Map<String, String> getPropertiesLike(String pattern);
  public void setProperty(String name, String value);

  
	/** Status severity constant (value 0) indicating this status represents the nominal case.
	 * This constant is also used as the status code representing the nominal case.
	 * @see #getSeverity()
	 * @see #isOK()
	 */
	public static final int OK = 0;

	/** Status type severity (bit mask, value 1) indicating this status is informational only.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int INFO = 0x01;

	/** Status type severity (bit mask, value 2) indicating this status represents a warning.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int WARNING = 0x02;

	/** Status type severity (bit mask, value 4) indicating this status represents an error.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int ERROR = 0x04;

	/** Status type severity (bit mask, value 8) indicating this status represents a
	 * cancelation
	 * @see #getSeverity()
	 * @see #matches(int)
	 * @since 3.0
	 */
	public static final int CANCEL = 0x08;  
}