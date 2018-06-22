/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations.spatial;

import java.awt.geom.Point2D;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.crs.SingleCRS;

public class GeotoolsCalculator implements IGeoCalculator
{
  private GeodeticCalculator calc;

  /**
   * protected constructor - to prevent declaration of GeoSupport
   * 
   */
  public GeotoolsCalculator(SingleCRS system)
  {
    calc =
        new GeodeticCalculator(system);
  }

  @Override
  public Point2D createPoint(double dLong, double dLat)
  {
    return new Point2D.Double(dLong, dLat);
  }

  @Override
  public Point2D calculatePoint(Point2D pos1, double angle, double distance)
  {
    calc.setStartingGeographicPoint(pos1);
    calc.setDirection(angle, distance);
    return calc.getDestinationGeographicPoint();
  }

  @Override
  public double getDistanceBetween(Point2D locA, Point2D locB)
  {
    calc.setStartingGeographicPoint(locA);
    calc.setDestinationGeographicPoint(locB);
    return calc.getOrthodromicDistance();
  }

  @Override
  public double getAngleBetween(Point2D txLoc, Point2D rxLoc)
  {
    calc.setStartingGeographicPoint(txLoc);
    calc.setDestinationGeographicPoint(rxLoc);
    return calc.getAzimuth();
  }
}
