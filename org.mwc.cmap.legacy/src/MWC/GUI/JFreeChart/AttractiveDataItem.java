/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.JFreeChart;

import java.awt.Color;

/**
 * Created by IntelliJ IDEA.
 * User: ian.mayo
 * Date: 30-Nov-2004
 * Time: 12:05:18
 * To change this template use File | Settings | File Templates.
 */
public interface AttractiveDataItem
{
  Color getColor();

  boolean connectToPrevious();
}
