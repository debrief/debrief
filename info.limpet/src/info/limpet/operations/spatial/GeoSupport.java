package info.limpet.operations.spatial;

import info.limpet.impl.SampleData;

import java.awt.geom.Point2D;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotools.referencing.crs.DefaultGeographicCRS;

public class GeoSupport
{
  private static IGeoCalculator calcWGS84;
  private static IGeoCalculator calc2D;

  /**
   * protected constructor, to prevent inadvertent instantiation
   * 
   */
  protected GeoSupport()
  {
  }

  public static IGeoCalculator getCalculatorWGS84()
  {
    if (calcWGS84 == null)
    {
      calcWGS84 = new GeotoolsCalculator(DefaultGeographicCRS.WGS84);
    }

    return calcWGS84;
  }

  public static IGeoCalculator getCalculatorGeneric2D()
  {
    if (calc2D == null)
    {
      calc2D = new Calculator2D();
    }

    return calc2D;
  }

  private static class Calculator2D implements IGeoCalculator
  {

    @Override
    public Point2D createPoint(double x, double y)
    {
      return new Point2D.Double(x, y);
    }

    @Override
    public double getDistanceBetween(Point2D locA, Point2D locB)
    {
      return locA.distance(locB);
    }

    @Override
    public Point2D calculatePoint(Point2D pos1, double angle, double distance)
    {
      double dx = Math.sin(Math.toRadians(angle)) * distance;
      double dy = Math.cos(Math.toRadians(angle)) * distance;
      return new Point2D.Double(pos1.getX() + dx, pos1.getY() + dy);
    }

    @Override
    public double getAngleBetween(Point2D locA, Point2D locB)
    {
      double dx = locB.getX() - locA.getX();
      double dy = locB.getY() - locA.getY();
      double angle = Math.toDegrees(Math.atan2(dy, dx));
      if(angle < 0)
      {
        angle += 360;
      }
      return angle;
    }

  }

  public static IGeoCalculator calculatorFor(Unit<?> units)
  {
    final IGeoCalculator res;
    if(units.equals(SampleData.DEGREE_ANGLE))
    {
      res = getCalculatorWGS84();
    }
    else if(units.equals(SI.METRE))
    {
      res = getCalculatorGeneric2D();
    }
    else
    {
      throw new IllegalArgumentException("Don't have calculator for:" + units);
    }
    return res;
  }

}
