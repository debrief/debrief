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
package org.mwc.debrief.lite;

import java.awt.MenuShortcut;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import MWC.GUI.Tool;

class LiteApplication extends Application
{

  private final Session _session;

  public LiteApplication(final Session session)
  {
    _session = session;
  }

  @Override
  protected void addMenuItem(final String theMenu, final String theLabel,
      final Tool theTool, final MenuShortcut theShortCut)
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  protected void addMenuSeparator(final String theMenu)
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  protected void closeSessionGUI(final Session theSession)
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public Session createSession()
  {
    return _session;
  }

  @Override
  public Session getCurrentSession()
  {
    return _session;
  }

  @Override
  public void logStack(final int status, final String text)
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public void restoreCursor()
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public void setCursor(final int theCursor)
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  protected void setTitleName(final String theStr)
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  protected void showSession(final Session theSession)
  {
    throw new IllegalArgumentException("Not implemented");
  }

}