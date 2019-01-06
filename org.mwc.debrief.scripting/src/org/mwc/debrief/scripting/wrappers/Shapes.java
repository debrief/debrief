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
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GUI.Shapes.RangeRingShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Shapes.TextLabel;
import MWC.GUI.Shapes.WheelShape;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import junit.framework.TestCase;

/**
 * creating shape objects
 * 
 * @author ian
 *
 */
public class Shapes
{

  public static class TestShapes extends TestCase
  {
    static final WorldLocation shapeCenterLocation = new WorldLocation(12.3,
        12.4, 12.5);
    static final WorldLocation shapeBottomRightLocation = new WorldLocation(1.1,
        1.1, 12);
    static final WorldLocation shapeTopLeftLocation = new WorldLocation(12.1,
        12.3, 12);
    static final java.awt.Color shapeColor = Core.createColorRGB(255, 0, 255);
    static final double innerRadius = 3.0;
    static final double outerRadius = 5.0;
    static final String shapeName = "ShapeName";
    static final int numOfRings = 1;
    static final WorldDistance ringsDistance = new WorldDistance(12,
        WorldDistance.METRES);

    static final WorldSpeed speed = new WorldSpeed(12, WorldSpeed.M_sec);

    static final int interval = 4;
    static final int arcCenter = 7;
    static final int arcWidth = 13;

    static final double theOrient = 9.5;
    static final WorldDistance theMaxima = new WorldDistance(32,
        WorldDistance.KM);
    static final WorldDistance theMinima = new WorldDistance(12,
        WorldDistance.KM);

    public void testCreateCircle()
    {
      final ShapeWrapper circle = createCircle(shapeCenterLocation, theMinima,
          shapeName, shapeColor);
      assertEquals("Same name for circle", shapeName, circle.getName());
      assertEquals("Same color for circle", shapeColor, circle.getColor());
      assertEquals("Same get start for line", shapeCenterLocation,
          ((CircleShape) circle.getShape()).getCentre());
      assertEquals("Same get start for line", theMinima, ((CircleShape) circle
          .getShape()).getRadius());
    }

    public void testCreateEllipse()
    {
      final ShapeWrapper ellipse = createEllipse(shapeCenterLocation, theOrient,
          theMaxima, theMinima, shapeName, shapeColor);
      assertEquals("Same name for ellipse", shapeName, ellipse.getName());
      assertEquals("Same color for ellipse", shapeColor, ellipse.getColor());
      assertEquals("Same location (Center) for ellipse", shapeCenterLocation,
          ((EllipseShape) ellipse.getShape()).getCentre());
      assertEquals("Same location (Orient) for ellipse", theOrient,
          ((EllipseShape) ellipse.getShape()).getOrientation(), 1e-5);
      assertEquals("Same location (Maxima) for ellipse", theMaxima,
          ((EllipseShape) ellipse.getShape()).getMaxima());
      assertEquals("Same location (theMinima) for ellipse", theMinima,
          ((EllipseShape) ellipse.getShape()).getMinima());
    }

    public void testCreateFurthestOnCircleShape()
    {

      final ShapeWrapper furthestOnCircleShape = createFurthestOnCircleShape(
          shapeCenterLocation, numOfRings, speed, interval, arcCenter, arcWidth,
          shapeName, shapeColor);

      assertEquals("Same name for furthestOnCircleShape", shapeName,
          furthestOnCircleShape.getName());
      assertEquals("Same color for furthestOnCircleShape", shapeColor,
          furthestOnCircleShape.getColor());
      assertEquals("Same (Number of Rings) for furthestOnCircleShape",
          numOfRings, ((FurthestOnCircleShape) furthestOnCircleShape.getShape())
              .getNumRings().getCurrent());
      assertEquals("Same (interval) for furthestOnCircleShape", interval,
          ((FurthestOnCircleShape) furthestOnCircleShape.getShape())
              .getTimeInterval());
      assertEquals("Same (Arc Centre) for furthestOnCircleShape", arcCenter,
          ((FurthestOnCircleShape) furthestOnCircleShape.getShape())
              .getArcCentre().getCurrent());
      assertEquals("Same (Arc Width) for furthestOnCircleShape", arcWidth,
          ((FurthestOnCircleShape) furthestOnCircleShape.getShape())
              .getArcWidth().getCurrent());
      assertEquals("Same location (Center) for furthestOnCircleShape",
          shapeCenterLocation, ((FurthestOnCircleShape) furthestOnCircleShape
              .getShape()).getCentre());
      assertEquals("Same location (Speed) for furthestOnCircleShape", speed,
          ((FurthestOnCircleShape) furthestOnCircleShape.getShape())
              .getSpeed());
    }

    public void testCreateLine()
    {
      final ShapeWrapper line = createLine(shapeCenterLocation,
          shapeTopLeftLocation, shapeName, shapeColor);
      assertEquals("Same name for line", shapeName, line.getName());
      assertEquals("Same color for line", shapeColor, line.getColor());
      assertEquals("Same get start for line", shapeCenterLocation,
          ((LineShape) line.getShape()).getLine_Start());
      assertEquals("Same get start for line", shapeTopLeftLocation,
          ((LineShape) line.getShape()).getLineEnd());
    }

    public void testCreatePolygonShape()
    {
      final String nodeName1 = "NodeName1";
      final String nodeName2 = "NodeName2";

      final ShapeWrapper polygonShape = createPolygon(shapeName, shapeColor);
      final PolygonShape polygonShapeCasted = (PolygonShape) polygonShape
          .getShape();
      final PolygonNode node1 = addPolygonNode(nodeName1, shapeCenterLocation,
          polygonShapeCasted);
      final PolygonNode node2 = addPolygonNode(nodeName2,
          shapeBottomRightLocation, polygonShapeCasted);
      assertEquals("Same name for PolygoneShape", shapeName, polygonShape
          .getName());
      assertEquals("Same color for PolygoneShape", shapeColor, polygonShape
          .getColor());
      assertTrue("PolygoneShape contains node 1", polygonShapeCasted.getPoints()
          .contains(node1));
      assertTrue("PolygoneShape contains node 2", polygonShapeCasted.getPoints()
          .contains(node2));
    }

    public void testCreateRangeRingShape()
    {
      final ShapeWrapper rangeRing = createRangeRingShape(shapeCenterLocation,
          numOfRings, ringsDistance, shapeName, shapeColor);

      assertEquals("Same location (Center) for RangeRing", shapeCenterLocation,
          ((RangeRingShape) rangeRing.getShape()).getCentre());
      assertEquals("Same (Number of Rings) for RangeRing", numOfRings,
          ((RangeRingShape) rangeRing.getShape()).getNumRings().getCurrent());
      assertEquals("Same (Rings Distance) for RangeRing", ringsDistance,
          ((RangeRingShape) rangeRing.getShape()).getRingWidth());
      assertEquals("Same name for RangeRing", shapeName, rangeRing.getName());
      assertEquals("Same color for RangeRing", shapeColor, rangeRing
          .getColor());
    }

    public void testCreateRectangleShape()
    {
      final ShapeWrapper rectangle = createRectangleShape(shapeTopLeftLocation,
          shapeBottomRightLocation, shapeName, shapeColor);

      final WorldArea area = new WorldArea(shapeTopLeftLocation,
          shapeBottomRightLocation);
      assertEquals("Same location for (Top Left) Rectangle", area.getTopLeft(),
          ((RectangleShape) rectangle.getShape()).getCorner_TopLeft());
      assertEquals("Same location for (Bottom Right) Rectangle", area
          .getBottomRight(), ((RectangleShape) rectangle.getShape())
              .getCornerBottomRight());

      assertEquals("Same name for Rectangle", shapeName, rectangle.getName());
      assertEquals("Same color for Rectangle", shapeColor, rectangle
          .getColor());
    }

    public void testCreateWheelShape()
    {
      final ShapeWrapper wheelShape = createWheelShape(shapeCenterLocation,
          innerRadius, outerRadius, shapeName, shapeColor);

      assertEquals("Same location for WheelShape", shapeCenterLocation,
          ((WheelShape) wheelShape.getShape()).getCentre());
      assertEquals("Same inner radius for WheelShape", innerRadius,
          ((WheelShape) wheelShape.getShape()).getRadiusInner().getValueIn(
              WorldDistance.YARDS));
      assertEquals("Same outer radius for WheelShape", outerRadius,
          ((WheelShape) wheelShape.getShape()).getRadiusOuter().getValueIn(
              WorldDistance.YARDS));
      assertEquals("Same name for WheelShape", shapeName, wheelShape.getName());
      assertEquals("Same color for WheelShape", shapeColor, wheelShape
          .getColor());
    }
  }

  /**
   * Method that adds a PolygoneNode to a PolygonShape
   * 
   * @param name
   *          Name of the new node
   * @param location
   *          Location of the new Node
   * @param parent
   *          PolygonShape to add the new Node
   * @return Reference to the added node. <br />
   *         // @type MWC.GUI.Shapes.PolygonShape.PolygonNode
   * 
   */
  @WrapToScript
  public static PolygonNode addPolygonNode(final String name,
      final WorldLocation location, final PolygonShape parent)
  {
    final PolygonNode newNode = new PolygonNode(name, location, parent);
    parent.add(newNode);
    return newNode;
  }

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
   * @return Circle Shape <br />
   *         // @type Debrief.Wrappers.ShapeWrapper
   * 
   */
  @WrapToScript
  public static ShapeWrapper createCircle(final WorldLocation center,
      final WorldDistance radius, final String name, final Color color)
  {
    return new ShapeWrapper(name, new CircleShape(center, radius, name), color,
        null);
  }

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
   * @return New Eclipse as a ShapeWrapper <br />
   *         // @type Debrief.Wrappers.ShapeWrapper
   * 
   */
  @WrapToScript
  public static ShapeWrapper createEllipse(final WorldLocation theCenter,
      final double theOrient, final WorldDistance theMaxima,
      final WorldDistance theMinima, final String name, final Color color)
  {
    return new ShapeWrapper(name, new EllipseShape(theCenter, theOrient,
        theMaxima, theMinima), color, null);
  }

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
   * @return FurthestOnCircle Shape as a ShapeWrapper. <br />
   *         // @type Debrief.Wrappers.ShapeWrapper
   * 
   */
  @WrapToScript
  public static ShapeWrapper createFurthestOnCircleShape(
      final WorldLocation theCenter, final int numRings, final WorldSpeed speed,
      final long interval, final int arcCenter, final int arcWidth,
      final String name, final Color color)
  {
    return new ShapeWrapper(name, new FurthestOnCircleShape(theCenter, numRings,
        speed, interval, arcCenter, arcWidth), color, null);
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
   * @return New line created with the parameters given <br />
   *         // @type Debrief.Wrappers.ShapeWrapper
   * 
   */
  @WrapToScript
  public static ShapeWrapper createLine(final WorldLocation startPt,
      final WorldLocation endPt, final String name, final Color color)
  {
    return new ShapeWrapper(name, new LineShape(startPt, endPt, name), color,
        null);
  }

  /**
   * Creates a polygon given a vector of PolygonNode
   *
   * @param vector
   *          Nodes of the Polygon
   * @param name
   *          Name of the Shape
   * @param color
   *          Color of the Shape
   * @return Polygon Shape. <br />
   *         // @type Debrief.Wrappers.ShapeWrapper
   * 
   */
  @WrapToScript
  public static ShapeWrapper createPolygon(final String name, final Color color)
  {
    return new ShapeWrapper(name, new PolygonShape(new Vector<PolygonNode>()),
        color, null);
  }

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
   * @return New RangeRingShape created as a ShapeWrapper <br />
   *         // @type Debrief.Wrappers.ShapeWrapper
   * 
   */
  @WrapToScript
  public static ShapeWrapper createRangeRingShape(final WorldLocation theCenter,
      final int numRings, final WorldDistance ringWidth, final String name,
      final Color color)
  {
    return new ShapeWrapper(name, new RangeRingShape(theCenter, numRings,
        ringWidth), color, null);
  }

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
   * @return New RectangleShape created as a ShapeWrapper <br />
   *         // @type Debrief.Wrappers.ShapeWrapper
   * 
   */
  @WrapToScript
  public static ShapeWrapper createRectangleShape(final WorldLocation TL,
      final WorldLocation BR, final String name, final Color color)
  {
    return new ShapeWrapper(name, new RectangleShape(TL, BR), color, null);
  }

  /**
   * Create a wheel shape
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
   * @return New WheelShape created as a ShapeWrapper <br />
   *         // @type Debrief.Wrappers.ShapeWrapper
   * 
   */
  @WrapToScript
  public static ShapeWrapper createWheelShape(final WorldLocation theCenter,
      final double theInnerRadius, final double theOuterRadius,
      final String name, final Color color)
  {
    return new ShapeWrapper(name, new WheelShape(theCenter, theInnerRadius,
        theOuterRadius), color, null);
  }

  @WrapToScript
  public static ShapeWrapper createTextLabel(final WorldLocation theLocation,
      final String name, final Color color)
  {
    return new ShapeWrapper(name, new TextLabel(theLocation, name), color,
        null);
  }
}
