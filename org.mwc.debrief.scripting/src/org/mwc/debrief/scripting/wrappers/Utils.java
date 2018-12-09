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

import org.eclipse.core.runtime.Status;
import org.eclipse.ease.modules.WrapToScript;
import org.mwc.debrief.core.DebriefPlugin;

public class Utils
{
  @WrapToScript
  public static String getClipboard() throws HeadlessException,
      UnsupportedFlavorException, IOException
  {
    return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(
        DataFlavor.stringFlavor);
  }

  @WrapToScript
  public static void setClipboard(String _clipboardData)
  {
    StringSelection selection = new StringSelection(_clipboardData);
    java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(selection, selection);
  }

  @WrapToScript
  public static void copyPlotToClipboard()
  {

  }

  @WrapToScript
  public static void writeError(String error)
  {
    DebriefPlugin.logError(Status.ERROR, error, null);
  }

  @WrapToScript
  public static void writeWarning(String warning)
  {
    DebriefPlugin.logError(Status.WARNING, warning, null);
  }
  
  @WrapToScript
  public static void writeInfo(String info)
  {
    DebriefPlugin.logError(Status.INFO, info, null);
  }
}
