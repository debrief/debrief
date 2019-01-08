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
package org.mwc.debrief.lite.map;

import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;

/**
 *
 * @author Unni Mana <unnivm@gmail.com>
 *
 *         This class is used to load different type of map implementation in the real world. The
 *         current implementation the map is based on GeoTools API. Need to implement
 *         createMapPane() method. This method ideally accepts a map content object and creates a
 *         ScrollPane object with map content so that it can be embedded in any layout.
 */
public interface BaseMap
{

  /**
   * adds a map control tool
   */
  public void addMapTool(JRibbonBand toolbar,JRibbon ribbon);

  /**
   * creates a JSplitPane from the given map content.
   *
   * @return
   */
  public void createMapLayout();

  /**
   * loads map content
   */
  public void loadMapContent();

}
