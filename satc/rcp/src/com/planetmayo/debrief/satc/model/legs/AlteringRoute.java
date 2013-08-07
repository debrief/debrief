package com.planetmayo.debrief.satc.model.legs;

import java.util.Date;
import java.util.List;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.math.Vector2D;

public class AlteringRoute extends CoreRoute
{

	private volatile double _directDistance;	
	
	private volatile AlteringRouteType _routeType = AlteringRouteType.UNDEFINED;
	/**
	 * array of control points for bezier curve: one point in 
	 * case of quad curve and two points in case of cubic curve   
	 */	
	private volatile Point _controlPoints[] = null;

	public AlteringRoute(String name, Point startP, Date startTime, Point endP,
			Date endTime)
	{
		super(startP, endP, startTime, endTime, name, LegType.ALTERING);
		
		// store the straight line distance
		Vector2D vector = new Vector2D(_startP.getCoordinate(),
				_endP.getCoordinate());

		// find the speed
		double lengthDeg = vector.length();
		_directDistance = GeoSupport.deg2m(lengthDeg);
	}

	/**
	 * break the line down into a series of points, at the indicated times
	 * 
	 */
	@Override	
	public void generateSegments(final List<BoundedState> states)
	{
		 // TODO sort out how to produce a curve through from start to end. actually, we can produce loads!
	}

	public AlteringRouteType getAlteringRouteType()
	{
		return _routeType;
	}
	
	/**
   * constructs altering route as bezier curve:
   *   in case when intersection point is placed between before and after straight segments 
   *   use quadratic bezier curve with control point = intersection point of these straight lines
   *   
   *   in case when intersection point is placed somewhere else: extend before and after
   *   straight segments and place control points for cubic bezier curve on before and
   *   after extensions correspondingly     
	 * 
	 * @param before
	 * @param after
	 */
	public void constructRoute(StraightRoute before, StraightRoute after) 
	{
		_routeType = AlteringRouteType.UNDEFINED;
		double[] beforeCoeffs = findStraightLineCoef(before);
		double[] afterCoeffs = findStraightLineCoef(after);
		
		Point intersection = findIntersection(beforeCoeffs, afterCoeffs);
		
		double beforeStartDistance = GeoSupport.calcDistance(before.getStartPoint(), intersection);
		double beforeEndDistance = GeoSupport.calcDistance(before.getEndPoint(), intersection);
		double beforeDistance = GeoSupport.calcDistance(before.getStartPoint(), before.getEndPoint());
		
		double afterStartDistance = GeoSupport.calcDistance(after.getStartPoint(), intersection);
		double afterEndDistance = GeoSupport.calcDistance(after.getEndPoint(), intersection);
		double afterDistance = GeoSupport.calcDistance(after.getStartPoint(), after.getEndPoint());		
		
		if (beforeEndDistance < beforeStartDistance && afterStartDistance < afterEndDistance &&
				beforeStartDistance > beforeDistance && afterEndDistance > afterDistance)
		{
			_controlPoints = new Point[] { intersection };
			_routeType = AlteringRouteType.QUAD_BEZIER;
		}
		else
		{
			double distance = GeoSupport.calcDistance(_startP, _endP);
			
			_controlPoints = new Point[2];
			_controlPoints[0] = findExtendPoint(beforeCoeffs, before.getEndPoint(), distance / 2, before.getStartPoint());
			_controlPoints[1] = findExtendPoint(afterCoeffs, after.getStartPoint(), distance / 2, after.getEndPoint());
			_routeType = AlteringRouteType.CUBIC_BEZIER;
		}
	}
	
	/** 
	 * defines straight route in y(x) = k * x + b shape and returns [k, b]. 
	 * @param route
	 * @return [k, b]
	 */
	private double[] findStraightLineCoef(StraightRoute route)
	{
		Point startPoint = route.getStartPoint();
		Point endPoint = route.getEndPoint();
		double k = (startPoint.getY() - endPoint.getY()) / (startPoint.getX() - endPoint.getX());
		double b = startPoint.getY() - k * startPoint.getX();
		return new double[] {k, b};
	}
	
	/**
	 * 
	 * @param line1 - first straight line coeffs: y(x) = line1[0] * x + line1[1]
	 * @param line2 - second straight line coeffs: y(x) = line2[0] * x + line2[1]
	 * @return intersection point between two line1 and line2
	 */	
	private Point findIntersection(double[] line1, double[] line2)
	{
		if (Math.abs(line1[0] - line2[0]) < 0.0001) 
		{
			return null;
		}
		double x = (line1[1] - line2[1]) / (line2[0] - line1[0]);
		double y = line1[0] * x + line1[1];
		return GeoSupport.getFactory().createPoint(new Coordinate(x, y));
	}
	
	/**
	 * finds point which is placed on line y(x) = lineCoeffs[0] * x + lineCoeffs[1] after 
	 * from point (fromPoint parameter) on specified distance (distance parameter) 
	 * in checkPoint->fromPoint direction     
	 */
	private Point findExtendPoint(double[] lineCoeffs, Point fromPoint, double distance, Point checkPoint)
	{
		double aux1 = fromPoint.getY() - lineCoeffs[1];
		
		double a = lineCoeffs[0] * lineCoeffs[0] + 1;
		double b = -2 * (aux1 * lineCoeffs[0] + fromPoint.getX());
		double c = aux1 * aux1 + fromPoint.getX() * fromPoint.getX() - distance * distance;
		
		double sqrtD = Math.sqrt(b * b - 4 * a * c);
		double x1 = (-b + sqrtD) / (2 * a);
		double x2 = (-b - sqrtD) / (2 * a);
		Point p1 = GeoSupport.getFactory().createPoint(new Coordinate(x1, lineCoeffs[0] * x1 + lineCoeffs[1]));
		Point p2 = GeoSupport.getFactory().createPoint(new Coordinate(x2, lineCoeffs[0] * x2 + lineCoeffs[1]));
		return GeoSupport.calcDistance(checkPoint, p1) > GeoSupport.calcDistance(checkPoint, p2) ? p1 : p2;
	}
	
	/**
	 * get the straight line between the endsd
	 * 
	 * @return
	 */
	public double getDirectDistance()
	{
		return _directDistance;
	}
	
	public Point[] getBezierControlPoints() 
	{
		return _controlPoints;
	}
}
