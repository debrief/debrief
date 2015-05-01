package Debrief.Wrappers.DynamicTrackShapes;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import MWC.GUI.CanvasType;
import MWC.GUI.ExtendedCanvasType;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class DynamicTrackCoverageWrapper extends DynamicTrackShapeWrapper
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * build a new sensorarc contact wrapper
	 * 
	 */

	public DynamicTrackCoverageWrapper(final String theTrack,
			final HiResDate startDTG, final HiResDate endDTG,
			List<DynamicShape> values, final Color theColor, final int theStyle,
			final String coverageName)
	{
		super(theTrack, startDTG, endDTG, values, theColor, theStyle, coverageName);
	}

	public DynamicTrackCoverageWrapper()
	{
		super();
	}

	@Override
	public String getConstraints()
	{
		StringBuilder builder = new StringBuilder();
		for (DynamicShape valueD : _values)
		{
			DynamicCoverageShape value = (DynamicCoverageShape) valueD;
			builder.append(value.minAngleDegs);
			builder.append(" ");
			builder.append(value.maxAngleDegs);
			builder.append(" ");
			builder.append(value.minYds);
			builder.append(" ");
			builder.append(value.maxYds);
			builder.append(" ");
		}
		return builder.toString().trim();
	}

	public void setConstraints(String arcs)
	{
		if (arcs == null)
		{
			throw new RuntimeException("Error parsing arcs");
		}
		arcs = arcs.trim();
		String[] elements = arcs.split(" ");
		if (elements.length % 4 != 0)
		{
			throw new RuntimeException("Error parsing arcs");
		}
		List<DynamicShape> values = new ArrayList<DynamicShape>();
		int index = 0;
		while (index < elements.length)
		{
			int minAngleDegs = getValue(elements, index++);
			int maxAngleDegs = getValue(elements, index++);
			int minYds = getValue(elements, index++);
			int maxYds = getValue(elements, index++);
			DynamicCoverageShape value = new DynamicCoverageShape(minAngleDegs,
					maxAngleDegs, minYds, maxYds);
			values.add(value);
		}
		this._values = values;
	}

	/**
	 * utility class used to store a single sensor coverage arc
	 * 
	 * @author ian
	 * 
	 */
	public static class DynamicCoverageShape implements DynamicShape
	{

		final public int minYds, maxYds, minAngleDegs, maxAngleDegs;

		public DynamicCoverageShape(int MinAngleDegs, int MaxAngleDegs, int minYds,
				int maxYds)
		{
			this.minAngleDegs = MinAngleDegs;
			this.maxAngleDegs = MaxAngleDegs;
			this.minYds = minYds;
			this.maxYds = maxYds;
		}

		/**
		 * calculate the shape as a series of WorldLocation points. Joined up, these
		 * form a representation of the shape
		 * 
		 * @param trackCourseDegs
		 */
//		private Vector<WorldLocation> calcDataPoints(final WorldLocation origin,
//				final double orient)
//		{
//			// get ready to store the list
//			final Vector<WorldLocation> res = new Vector<WorldLocation>(0, 1);
//
//			final double minDegs = new WorldDistance(minYds, WorldDistance.YARDS)
//					.getValueIn(WorldDistance.DEGS);
//			final double maxDegs = new WorldDistance(maxYds, WorldDistance.YARDS)
//					.getValueIn(WorldDistance.DEGS);
//
//			// draw the outer ring
//			createCircle(origin, orient, res, maxDegs);
//
//			// and now the inner ring
//			createCircle(origin, orient, res, minDegs);
//
//			return res;
//
//		}

//		private void createCircle(final WorldLocation origin, final double orient,
//				final Vector<WorldLocation> res, final double maxDegs)
//		{
//			for (int i = 0; i <= CircleShape.NUM_SEGMENTS; i++)
//			{
//				// produce the current bearing
//				final double this_brg = (360.0 / CircleShape.NUM_SEGMENTS * i) / 180.0
//						* Math.PI;
//
//				// first produce a standard ellipse of the correct size
//				final double x1 = Math.sin(this_brg) * maxDegs;
//				double y1 = Math.cos(this_brg) * maxDegs;
//
//				// now produce the range out to the edge of the ellipse at
//				// this point
//				final double r = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));
//
//				// to prevent div/0 error in atan, make y1 small if zero
//				if (y1 == 0)
//					y1 = 0.0000001;
//
//				// and the new bearing to the correct point on the ellipse
//				final double tr = Math.atan2(y1, x1) + (orient - Math.PI / 2);
//
//				// use our "add" function to add a vector, rather than the
//				// x-y components as we did, so that the ellipse stays correctly
//				// shaped as it travels further from the equator.
//				final WorldLocation wl = origin.add(new WorldVector(tr, r, 0));
//
//				res.add(wl);
//			}
//		}

		/**
	   * Create a donut or donut section.  A full donut results if the
	   * start and end mod 360 are within 0.1 degree, so don't call this 
	   * if the difference between minAngle and maxAngle should be 0.
	   *
	   * @param innerRadius of donut section
	   * @param outerRadius of donut section
	   * @param minAngle compass angle of section start
	   * @param maxAngle compass angle of section end
	   */
	  public static Area makeDonutSectionArea(double innerRadius,
	                                          double outerRadius,
	                                          double minAngle,
	                                          double maxAngle,
	                                          double course) {
	  	
	  	// convenience variables, for simpler coding
	    double dO = outerRadius * 2, dI = innerRadius * 2;
	    
	    // angles from degrees clockwise from the positive y axis.
	    // convert to degress counter-clockwise from positive x axis.
	    double aBeg =  90 - (maxAngle + course), aExt = maxAngle - minAngle;
	    
	    // x and y are upper left corner of bounding rectangle of full circle
	    if (Math.abs(minAngle % 360 - maxAngle % 360) < 0.1) 
	    {
	    	// ok - it's a plain donut
	      Area outer = new Area(new Ellipse2D.Double(-dO/2, -dO/2, dO, dO));
	      Area inner = new Area(new Ellipse2D.Double(-dI/2, -dI/2, dI, dI));
	      outer.subtract(inner);
	      return outer;
	    } else 
	    {
	    	// this is a pie slice of a donut
	      Area outer = new Area(new Arc2D.Double(-dO/2, -dO/2, dO, dO, aBeg, aExt,
	                                             Arc2D.PIE));
	      Area inner = new Area(new Arc2D.Double(-dI/2, -dI/2, dI, dI, aBeg, aExt,
	                                             Arc2D.PIE));
	      outer.subtract(inner);
	      return outer;
	    }
	  }

		public void paint(CanvasType dest, Watchable hostState, Color color, boolean semiTransparent)
		{
			// update the color
			dest.setColor(color);

			WorldLocation originWd = hostState.getLocation();
			// get the host origin in screen coords
			final Point originPt = dest.toScreen(originWd);

			// get the polygon at this location
			//Vector<WorldLocation> _theDataPoints = calcDataPoints(
			//		hostState.getLocation(), hostState.getCourse());

			final WorldDistance minDist = new WorldDistance(minYds, WorldDistance.YARDS);
			final WorldDistance maxDist = new WorldDistance(maxYds, WorldDistance.YARDS);

			// sort out the sizes in pixels
			WorldVector minOffset = new WorldVector(0d, minDist, null);
			WorldVector maxOffset = new WorldVector(0d, maxDist, null);
			Point minPt = dest.toScreen(originWd.add(minOffset));
			Point maxPt = dest.toScreen(originWd.add(maxOffset));
			
			long minOffsetPt = originPt.y - minPt.y;
			long maxOffsetPt = originPt.y - maxPt.y;
			
			double course = Math.toDegrees(hostState.getCourse());
			Area area = makeDonutSectionArea(minOffsetPt, maxOffsetPt, minAngleDegs, maxAngleDegs, course);
			
			final AffineTransform af = AffineTransform.getTranslateInstance(originPt.x, originPt.y);
	    Shape shape = af.createTransformedShape(area);

			if (dest instanceof ExtendedCanvasType)
			{
				if(semiTransparent)
				{
				((ExtendedCanvasType) dest).semiFillShape(shape);
				}
				else
				{
					((ExtendedCanvasType) dest).fillShape(shape);
				}
			}			
		}

		public String toString()
		{
			return minAngleDegs + " " + maxAngleDegs + " " + minYds + " " + maxYds
					+ " ";
		}
	}
}
