package Debrief.Wrappers.DynamicTrackShapes;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.Shapes.CircleShape;
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
		private Vector<WorldLocation> calcDataPoints(final WorldLocation origin,
				final double orient)
		{
			// get ready to store the list
			final Vector<WorldLocation> res = new Vector<WorldLocation>(0, 1);

			final double minDegs = new WorldDistance(minYds, WorldDistance.YARDS)
					.getValueIn(WorldDistance.DEGS);
			final double maxDegs = new WorldDistance(maxYds, WorldDistance.YARDS)
					.getValueIn(WorldDistance.DEGS);

			// draw the outer ring
			createCircle(origin, orient, res, maxDegs);

			// and now the inner ring
			createCircle(origin, orient, res, minDegs);

			return res;

		}

		private void createCircle(final WorldLocation origin, final double orient,
				final Vector<WorldLocation> res, final double maxDegs)
		{
			for (int i = 0; i <= CircleShape.NUM_SEGMENTS; i++)
			{
				// produce the current bearing
				final double this_brg = (360.0 / CircleShape.NUM_SEGMENTS * i) / 180.0
						* Math.PI;

				// first produce a standard ellipse of the correct size
				final double x1 = Math.sin(this_brg) * maxDegs;
				double y1 = Math.cos(this_brg) * maxDegs;

				// now produce the range out to the edge of the ellipse at
				// this point
				final double r = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));

				// to prevent div/0 error in atan, make y1 small if zero
				if (y1 == 0)
					y1 = 0.0000001;

				// and the new bearing to the correct point on the ellipse
				final double tr = Math.atan2(y1, x1) + (orient - Math.PI / 2);

				// use our "add" function to add a vector, rather than the
				// x-y components as we did, so that the ellipse stays correctly
				// shaped as it travels further from the equator.
				final WorldLocation wl = origin.add(new WorldVector(tr, r, 0));

				res.add(wl);
			}
		}

		public void paint(CanvasType dest, Watchable hostState, Color color)
		{
			// get the polygon at this location
			Vector<WorldLocation> _theDataPoints = calcDataPoints(
					hostState.getLocation(), hostState.getCourse());

			// update the color
			dest.setColor(color);

			// create a polygon to represent the ellipse (so that we can fill or draw
			// it)
			final int len = _theDataPoints.size();
			final int[] xPoints = new int[len];
			final int[] yPoints = new int[len];

			// work through the list to create the list of screen coordinates
			for (int i = 0; i < _theDataPoints.size(); i++)
			{
				final WorldLocation location = (WorldLocation) _theDataPoints
						.elementAt(i);

				final Point p2 = dest.toScreen(location);

				xPoints[i] = p2.x;
				yPoints[i] = p2.y;
			}

			// if (getSemiTransparent() && dest instanceof ExtendedCanvasType)
			// {
			ExtendedCanvasType ext = (ExtendedCanvasType) dest;
			ext.semiFillPolygon(xPoints, yPoints, len);
			// }
			// else
			// dest.fillPolygon(xPoints, yPoints, len);
		}

		public String toString()
		{
			return minAngleDegs + " " + maxAngleDegs + " " + minYds + " " + maxYds
					+ " ";
		}
	}
}
