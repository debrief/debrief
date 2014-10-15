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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package MWC.GUI.SplitPanel;



public class PaneConstraints {
  public float  proportion = 0.5f;             //NORES
  public String position = TOP;
  public String splitComponentName;
  public String name;
  public static final String TOP    = "Top";       //NORES
  public static final String BOTTOM = "Bottom";    //NORES
  public static final String LEFT   = "Left";      //NORES
  public static final String RIGHT  = "Right";     //NORES
  public static final String ROOT   = "Root";      //NORES

  public PaneConstraints() {
  }

  public PaneConstraints(final String name,final String splitComponentName, final String position, final float proportion) {
    this.name = name;
    this.splitComponentName = splitComponentName;
    this.position = position;
    this.proportion = proportion;
  }
  
  public String toString() {
    return name + ": " + splitComponentName + "," + position + " proportion:" + proportion;    //NORES
  }
}
