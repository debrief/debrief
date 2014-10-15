/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI;

import java.util.Enumeration;

public interface PlottablesType {

  /** get the elements in this list of plottables
   *
   * @return enumeration of elements
   */
  Enumeration<Editable> elements();

  /** how big is the list?
   *
   * @return the length of the list
   */
  int size();

  /** paint this list of plottables
   *
   * @param dest the graphics destination
   */
  void paint(CanvasType dest);

  /** what area do we cover?
   *
   * @return the area, or null
   */
  MWC.GenericData.WorldArea getBounds();

  /** the name of this set of plottables
   *
   * @return my name
   */
  String getName();
}
