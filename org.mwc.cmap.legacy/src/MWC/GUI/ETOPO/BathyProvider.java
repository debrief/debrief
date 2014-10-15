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
package MWC.GUI.ETOPO;

import MWC.GenericData.WorldLocation;

public interface BathyProvider
{
  /** provide the delta for the data
   *
   */
  public double getGridDelta();

  /** provide the depth in metres at the indicated location
   *
   */
  public double getDepthAt(WorldLocation loc);

  /** whether the data has been loaded yet
   *
   */
  public boolean isDataLoaded();
}
