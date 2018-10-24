package org.mwc.debrief.scripting.wrappers;

import java.awt.Color;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public class Shapes
{

  public static ShapeWrapper createLine(WorldLocation startPt,
      WorldLocation endPt, String name, Color color)
  {
    return new ShapeWrapper(name, new LineShape(startPt, endPt, name),
        color, null);
  }

  public static ShapeWrapper createCircle(WorldLocation centre,
      WorldDistance radius, String name, Color color)
  {
    return new ShapeWrapper(name, new CircleShape(centre, radius, name),
        color, null);
  }

}
