/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util;

// Standard imports
// none

// Application specific imports
// none

/**
 * A generic handler for dealing errors within the code.
 * <P>
 *
 * The idea of this class is to provide an abstracted and common way of getting
 * errors back from low level code and writing it in a consistent manner that
 * is suitable for the environment. For example, in servlet code, you want the
 * servlet to implement this and write the error messages to the log file.
 * Normal <CODE>System.err</CODE> messages don't get written out in many
 * servlet environments so you really want to handle everything consistently.
 * <P>
 *
 * All low level libraries should take a reference to this. Anywhere that the
 * normal catch clause would write to <CODE>System.err</CODE> it should be
 * replaced with a call to one of these methods.
 *
 * @version 0.1
 */
public interface ErrorHandler
{
  /**
   * Write a status message to the handler. This is a generic informational
   * message rather than a deadly problem.
   *
   * @param msg The message to be written
   */
  public void writeMessage(String msg);

  /**
   * Write an error to the handler. This error has generated a corresponding
   * exception that we really need to log to the output.
   *
   * @param msg An informational message
   * @param th The exception that occurred
   */
  public void writeError(String msg, Throwable th);
}

