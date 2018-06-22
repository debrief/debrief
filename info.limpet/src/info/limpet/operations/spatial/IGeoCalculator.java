package info.limpet.operations.spatial;

import java.awt.geom.Point2D;

public interface IGeoCalculator
{
 
  /**
   * 
   * @param dLong
   * @param dLat
   * @return
   */
  Point2D createPoint(double dLong, double dLat);

  /**
   * 
   * @param locA origin
   * @param locB designation
   * @return metres
   */
  double getDistanceBetween(Point2D locA, Point2D locB);

  /**
   * 
   * @param pos1 origin
   * @param angle radians
   * @param distance metres
   * @return
   */
  Point2D calculatePoint(Point2D pos1, double angle, double distance);

  /**
   * 
   * @param locA
   * @param locB
   * @return
   */
  double getAngleBetween(Point2D locA, Point2D locB);
}
