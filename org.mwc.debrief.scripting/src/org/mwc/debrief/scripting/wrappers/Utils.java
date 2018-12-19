/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.scripting.wrappers;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ease.modules.WrapToScript;
import org.mwc.debrief.core.DebriefPlugin;

/** Utility methods, copying data to/from the system
 * 
 * @author ian
 *
 */
public class Utils
{
  
  /** copy the current plot onto the clipboard
   * 
   */
  public static void copyPlotToClipboard()
  {

  }

  
  /**
   * Returns the content of the clipboard.
   * 
   * @return content of the clipboard as String.
   * @throws HeadlessException if the application is running headless
   * @throws UnsupportedFlavorException if the system can't handle this type of data
   * @throws IOException other input/output exception
   */
  @WrapToScript
  public static String getClipboard() throws HeadlessException,
      UnsupportedFlavorException, IOException
  {
    return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(
        DataFlavor.stringFlavor);
  }

  
  /**
   * Set the specified string to the clipboard.
   * 
   * @param _clipboardData
   *          String text to be added to the clipboard.
   */
  @WrapToScript
  public static void setClipboard(final String _clipboardData)
  {
    final StringSelection selection = new StringSelection(_clipboardData);
    final java.awt.datatransfer.Clipboard clipboard = Toolkit
        .getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(selection, selection);
  }

  
  /**
   * Write the error message in the log.
   * 
   * @param error
   *          Error message to be written in the log.
   */
  @WrapToScript
  public static void writeError(final String error)
  {
    DebriefPlugin.logError(IStatus.ERROR, error, null);
  }

  
  /**
   * Write the information message in the log
   * 
   * @param info
   *          Information message to be written in the log.
   */
  @WrapToScript
  public static void writeInfo(final String info)
  {
    DebriefPlugin.logError(IStatus.INFO, info, null);
  }

  
  /**
   * Write the warning message in the log
   * 
   * @param warning
   *          Warning message to be written in the log.
   */
  @WrapToScript
  public static void writeWarning(final String warning)
  {
    DebriefPlugin.logError(IStatus.WARNING, warning, null);
  }
}
