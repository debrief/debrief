package org.mwc.debrief.scripting.wrappers;

import java.awt.Color;
import java.util.Vector;

import org.eclipse.ease.modules.WrapToScript;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.FurthestOnCircleShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.RangeRingShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Shapes.WheelShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class Shapes
{

  @WrapToScript
  /**
   * Creates a circle given the center, radius and name
   *
   * @param center
   *          Center of the circle.
   * @param radius
   *          Radius of the circle.
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return Circle Shape
   */
  public static ShapeWrapper createCircle(final WorldLocation center,
      final WorldDistance radius, final String name, final Color color)
  {
    return new ShapeWrapper(name, new CircleShape(center, radius, name), color,
        null);
  }

  /**
   * Creates a line.
   * 
   * @param startPt
   *          Start point of the line
   * @param endPt
   *          End point of the line
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return
   */
  public static ShapeWrapper createLine(final WorldLocation startPt,
      final WorldLocation endPt, final String name, final Color color)
  {
    return new ShapeWrapper(name, new LineShape(startPt, endPt, name), color,
        null);
  }

  @WrapToScript
  /**
   * Creates an EllipseShape
   *
   * @param theCenter
   *          WorldLocation marking the center of the Ellipse
   * @param theOrient
   *          Length of the maximal (in degs)
   * @param theMaxima
   *          Length of the minimal of the ellipse (in degs)
   * @param theMinima
   *          Orientation of the ellipse (in degs)
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return
   */
  public static ShapeWrapper createEllipse(final WorldLocation theCenter,
      final double theOrient, final WorldDistance theMaxima,
      final WorldDistance theMinima, final String name, final Color color)
  {
    return new ShapeWrapper(name, new EllipseShape(theCenter, theOrient,
        theMaxima, theMinima), color, null);
  }

  @WrapToScript
  /**
   * Creates a FurthestOnCircleShape
   * 
   * @param theCenter
   *          Center of the FurthestOnCircleShape
   * @param numRings
   *          Number of rings
   * @param speed
   *          Speed of the FurthestOnCircleShape
   * @param interval
   *          Interval of the FurthestOnCircleShape
   * @param arcCenter
   *          Arc Center of the FurthestOnCircleShape
   * @param arcWidth
   *          Arc Width of the FurthestOnCircleShape
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return
   */
  public static ShapeWrapper createFurthestOnCircleShape(
      final WorldLocation theCenter, final int numRings, final WorldSpeed speed,
      final long interval, final int arcCenter, final int arcWidth,
      final String name, final Color color)
  {
    return new ShapeWrapper(name, new FurthestOnCircleShape(theCenter, numRings,
        speed, interval, arcCenter, arcWidth), color, null);
  }

  @WrapToScript
  /**
   * Creates a polygon given a vector of PolygonNode
   *
   * @param vector
   *          Nodes of the Polygon
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return Polygon Shape.
   */
  public static ShapeWrapper createPolygon(final Vector<PolygonNode> vector,
      final String name, final Color color)
  {
    return new ShapeWrapper(name, new PolygonShape(vector), color, null);
  }

  @WrapToScript
  /**
   * Create a RangeRingShape.
   * 
   * @param theCenter
   *          Center of the RangeRingShape
   * @param numRings
   *          Number of Rings of the RangeRingShape
   * @param ringWidth
   *          Width of the ring of the RangeRingShape
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return
   */
  public static ShapeWrapper createRangeRingShape(final WorldLocation theCenter,
      final int numRings, final WorldDistance ringWidth, final String name,
      final Color color)
  {
    return new ShapeWrapper(name, new RangeRingShape(theCenter, numRings,
        ringWidth), color, null);
  }

  @WrapToScript
  /**
   * Creates a Rectangle Shape
   * 
   * @param TL
   *          Top left of the Rectangle
   * @param BR
   *          Bottom Right of the Rectangle
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return
   */
  public static ShapeWrapper createRectangleShape(final WorldLocation TL,
      final WorldLocation BR, final String name, final Color color)
  {
    return new ShapeWrapper(name, new RectangleShape(TL, BR), color, null);
  }

  @WrapToScript

  /**
   * 
   * @param theCenter
   *          the center of the wheel
   * @param theInnerRadius
   *          the inner radius of the wheel, in yds
   * @param theOuterRadius
   *          the outer radius of the wheel, in yds
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return
   */
  public static ShapeWrapper createWheelShape(final WorldLocation theCenter,
      final double theInnerRadius, final double theOuterRadius,
      final String name, final Color color)
  {
    return new ShapeWrapper(name, new WheelShape(theCenter, theInnerRadius,
        theOuterRadius), color, null);
  }
}
