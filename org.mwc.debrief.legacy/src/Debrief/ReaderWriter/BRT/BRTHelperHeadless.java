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
package Debrief.ReaderWriter.BRT;

import java.awt.Color;

import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.WorldDistance;

public class BRTHelperHeadless implements BRTHelper
{

  final private boolean towed;
  final private WorldDistance offset;
  final private Color color;
  final private WorldDistance length;
  final private TrackWrapper trackToSelect;

  public BRTHelperHeadless(final boolean towed, final WorldDistance offset,
      final Color color, final WorldDistance length,
      final TrackWrapper trackToSelect)
  {
    super();
    this.towed = towed;
    this.offset = offset;
    this.color = color;
    this.length = length;
    this.trackToSelect = trackToSelect;
  }

  @Override
  public WorldDistance arrayOffset()
  {
    return offset;
  }

  @Override
  public WorldDistance defaultLength()
  {
    return length;
  }

  @Override
  public Color getColor()
  {
    return color;
  }

  @Override
  public Boolean isTowed()
  {
    return towed;
  }

  @Override
  /**
   * User will always pick the option given at trackToSelect
   */
  public TrackWrapper select()
  {
    return trackToSelect;
  }

}
