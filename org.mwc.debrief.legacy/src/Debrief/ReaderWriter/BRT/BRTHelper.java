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

public interface BRTHelper
{
  Boolean isTowed();

  WorldDistance arrayOffset();

  TrackWrapper select();

  Color getColor();

  WorldDistance defaultLength();
  
  Boolean isVisible();
  
  String getName();
}
