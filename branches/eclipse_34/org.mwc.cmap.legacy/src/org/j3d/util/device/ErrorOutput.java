/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.device;

// Standard imports
// none

// Application specific imports
import org.j3d.util.ErrorHandler;

/**
 * A local implementation of the {@link org.j3d.util.ErrorHandler} used
 * internally by this package.
 * <P>
 *
 * The default handler just prints to <CODE>System.err</CODE>
 *
 * @version $Revision: 1.1.1.1 $
 */
class ErrorOutput implements ErrorHandler
{
  /**
   * Write a status message to the handler. This is a generic informational
   * message rather than a deadly problem.
   *
   * @param msg The message to be written
   */
  public void writeMessage(String msg)
  {
    System.err.println(msg);
  }

  /**
   * Write an error to the handler. This error has generated a corresponding
   * exception that we really need to log to the output.
   *
   * @param msg An informational message
   * @param th The exception that occurred
   */
  public void writeError(String msg, Throwable th)
  {
    System.err.println("Error: " + msg);
    th.printStackTrace();
  }
}

