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

import java.awt.Cursor;
import java.awt.MenuShortcut;

import javax.swing.JFrame;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.Replay.ImportReplay.ProvidesModeSelector;
import MWC.GUI.Tool;

public class LiteApplication extends Application implements ProvidesModeSelector
{
  
  /** store preferences for import mode, and import frequency
   * 
   */
  private final ImportSettings settings;
  
  private final Long freq;

  private Session _session;

  private JFrame _theFrame;

  public LiteApplication(final String mode, final long freq)
  {
    settings = new ImportSettings(mode, freq);
    this.freq = freq;
  }
  
  @Override
  public String getProperty(final String name)
  {
    if (name.equals(ImportReplay.TRACK_IMPORT_MODE))
    {
      return settings.importMode;
    }
    else if (name.equals(ImportReplay.RESAMPLE_FREQUENCY))
    {
      return "" + settings.sampleFrequency;
    }
    else
    {
      return super.getProperty(name);
    }
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
    throw new IllegalArgumentException("Not implemented");
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
    _theFrame.getContentPane().setCursor(null);
  }

  @Override
  public void setCursor(final int theCursor)
  {
    _theFrame.getContentPane().setCursor(new Cursor(theCursor));
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


  @Override
  public Long getSelectedImportFrequency(final String trackName)
  {
    return this.freq;
  }

  @Override
  public ImportSettings getSelectedImportMode(final String trackName)
  {
    return settings;
  }

  public void setSession(Session sessio)
  {
    _session = sessio;
  }

  public void setFrame(JFrame theFrame)
  {
    _theFrame = theFrame;
  }

  public JFrame getTheFrame()
  {
    return _theFrame;
  }
}