/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: TimeFrequencyPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: TimeFrequencyPropertyEditor.java,v $
// Revision 1.4  2004/11/25 11:05:15  Ian.Mayo
// Switch to HiResDate internally
//
// Revision 1.3  2004/11/24 16:05:28  Ian.Mayo
// Switch to hi-res timers
//
// Revision 1.2  2004/05/25 15:29:10  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-12-16 15:17:59+00  ian_mayo
// Tidy comments, extend frequency
//
// Revision 1.2  2002-05-28 09:25:42+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:43+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:45+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-10-01 12:48:59+01  administrator
// Use accessor methods to get at tags & labels (so that we can over-ride this class more easily)
//
// Revision 1.0  2001-07-17 08:43:52+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:49+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:21  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:53:02+01  ian_mayo
// Initial revision
//


package Debrief.Tools.Properties;

import java.beans.PropertyEditorSupport;
import java.util.List;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.Track.TrackColorModeHelper;
import Debrief.Wrappers.Track.TrackColorModeHelper.TrackColorMode;

/**
 * class to provide list of time frequencies, together with ALL value
 */
public class TrackColorModePropertyEditor extends PropertyEditorSupport
{

  /** the array centres for the current object
   * 
   */
  private static List<TrackColorMode> _colorModes;
  
  /**
   * the currently color modes
   */
  protected TrackColorMode _myMode = TrackColorModeHelper.LegacyTrackColorModes.PER_FIX;

  @Override
  public String[] getTags()
  {
    final String[] allTags;
    if(_colorModes != null)
    {
      // ok, build up the list
      allTags = new String[_colorModes.size()];
      for(int i=0;i<_colorModes.size();i++)
      {
        allTags[i] = _colorModes.get(i).asString();
      }
    }
    else
    {
      Application.logStack2(Application.ERROR,
          "The color modes have not been set. We can't produce a proper list");
      allTags = new String[]
      {"Broken: color modes not found"};
    }
    
    return allTags;
  }

  @Override
  public Object getValue()
  {
    return _myMode;
  }

  @Override
  public void setValue(final Object p1)
  {
    if (p1 instanceof String)
    {
      final String val = (String) p1;
      setAsText(val);
    }
    else if(p1 instanceof TrackColorMode)
    {
      TrackColorMode td = (TrackColorMode) p1;
      _myMode = td;
    }
    else if(p1 instanceof Integer)
    {
      _myMode = _colorModes.get((int) p1);
    }
  }

  @Override
  public void setAsText(final String val)
  {
    // ok, loop though modes, to fine match
    for(final TrackColorMode t: _colorModes)
    {
      if(t.asString().equals(val))
      {
        _myMode = t;
        break;
      }
    }
  }

  @Override
  public String getAsText()
  {
    return _myMode.asString();
  }

  /** we wish to allow some modes that have been taken from the
   * subject sensor. They get set here
   * @param datasets
   */
  public static void setCustomModes(List<TrackColorMode> datasets)
  {
    _colorModes = datasets;
  }
}

