/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.borlander.rac525791.draw2d.ext;

import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Renders a {@link PointList} as a series of line segments. A Polyline figure
 * should be positioned by manipulating its points, <EM>NOT</EM> by calling
 * {@link Figure#setBounds(Rectangle)}.
 * <P>
 * A polyline's bounds will be calculated automatically based on its PointList.
 * The bounds will be the smallest Rectangle large enough to render the line
 * properly. Children should not be added to a Polyline and will not affect the
 * bounds calculation.
 */
public class Polyline extends Shape {

	private static final int TOLERANCE = 2;
	private static final Rectangle LINEBOUNDS = Rectangle.SINGLETON;
	PointList points = new PointList();

	{
		setFill(false);
		bounds = null;
	}

	/**
	 * Adds the passed point to the Polyline.
	 *
	 * @param pt the Point to be added to the Polyline
	 * @since 2.0
	 */
	public void addPoint(final Point pt) {
		points.addPoint(pt);
		bounds = null;
		repaint();
	}

	/**
	 * @see org.eclipse.draw2d.IFigure#containsPoint(int, int)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean containsPoint(final int x, final int y) {
		final int tolerance = lineWidth / 2 + TOLERANCE;
		LINEBOUNDS.setBounds(getBounds());
		LINEBOUNDS.expand(tolerance, tolerance);
		if (!LINEBOUNDS.contains(x, y))
			return false;
		final int ints[] = points.toIntArray();
		for (int index = 0; index < ints.length - 3; index += 2) {
			if (lineContainsPoint(ints[index], ints[index + 1], ints[index + 2], ints[index + 3], x, y, tolerance))
				return true;
		}
		final List children = getChildren();
		for (int i = 0; i < children.size(); i++) {
			if (((IFigure) children.get(i)).containsPoint(x, y))
				return true;
		}
		return false;
	}

	/**
	 * Null implementation for a line.
	 *
	 * @see org.eclipse.draw2d.Shape#fillShape(Graphics)
	 */
	@Override
	protected void fillShape(final Graphics g) {
		//
	}

	/**
	 * @see org.eclipse.draw2d.IFigure#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		if (bounds == null) {
			bounds = getPoints().getBounds().getExpanded(lineWidth / 2, lineWidth / 2);
		}
		return bounds;
	}

	/**
	 * Returns the last point in the Polyline.
	 *
	 * @since 2.0
	 * @return the last point
	 */
	public Point getEnd() {
		return points.getLastPoint();
	}

	/**
	 * Returns the points in this Polyline <B>by reference</B>. If the returned list
	 * is modified, this Polyline must be informed by calling
	 * {@link #setPoints(PointList)}. Failure to do so will result in layout and
	 * paint problems.
	 *
	 * @return this Polyline's points
	 * @since 2.0
	 */
	public PointList getPoints() {
		return points;
	}

	/**
	 * @return the first point in the Polyline
	 * @since 2.0
	 */
	public Point getStart() {
		return points.getFirstPoint();
	}

	/**
	 * Inserts a given point at a specified index in the Polyline.
	 *
	 * @param pt    the point to be added
	 * @param index the position in the Polyline where the point is to be added
	 *
	 * @since 2.0
	 */
	public void insertPoint(final Point pt, final int index) {
		bounds = null;
		points.insertPoint(pt, index);
		repaint();
	}

	/**
	 * @return <code>false</code> because Polyline's aren't filled
	 */
	@Override
	public boolean isOpaque() {
		return false;
	}

	private boolean lineContainsPoint(final int x1, final int y1, final int x2, final int y2, final int px,
			final int py, final int tolerance) {
		LINEBOUNDS.setSize(0, 0);
		LINEBOUNDS.setLocation(x1, y1);
		LINEBOUNDS.union(x2, y2);
		LINEBOUNDS.expand(tolerance, tolerance);
		if (!LINEBOUNDS.contains(px, py))
			return false;

		int v1x, v1y, v2x, v2y;
		int numerator, denominator;
		int result = 0;

		/**
		 * calculates the length squared of the cross product of two vectors, v1 & v2.
		 */
		if (x1 != x2 && y1 != y2) {
			v1x = x2 - x1;
			v1y = y2 - y1;
			v2x = px - x1;
			v2y = py - y1;

			numerator = v2x * v1y - v1x * v2y;

			denominator = v1x * v1x + v1y * v1y;

			result = (int) ((long) numerator * numerator / denominator);
		}

		// if it is the same point, and it passes the bounding box test,
		// the result is always true.
		return result <= tolerance * tolerance;

	}

	/**
	 * @see Shape#outlineShape(Graphics)
	 */
	@Override
	protected void outlineShape(final Graphics g) {
		g.drawPolyline(points);
	}

	/**
	 * @see Figure#primTranslate(int, int)
	 */
	@Override
	public void primTranslate(final int x, final int y) {
		//
	}

	/**
	 * Erases the Polyline and removes all of its {@link Point Points}.
	 *
	 * @since 2.0
	 */
	public void removeAllPoints() {
		erase();
		bounds = null;
		points.removeAllPoints();
	}

	/**
	 * Removes a point from the Polyline.
	 *
	 * @param index the position of the point to be removed
	 * @since 2.0
	 */
	public void removePoint(final int index) {
		erase();
		bounds = null;
		points.removePoint(index);
	}

	/**
	 * Sets the end point of the Polyline
	 *
	 * @param end the point that will become the last point in the Polyline
	 * @since 2.0
	 */
	public void setEnd(final Point end) {
		if (points.size() < 2)
			addPoint(end);
		else
			setPoint(end, points.size() - 1);
	}

	/**
	 * Sets the points at both extremes of the Polyline
	 *
	 * @param start the point to become the first point in the Polyline
	 * @param end   the point to become the last point in the Polyline
	 * @since 2.0
	 */
	public void setEndpoints(final Point start, final Point end) {
		setStart(start);
		setEnd(end);
	}

	/**
	 * @see org.eclipse.draw2d.Shape#setLineWidth(int)
	 */
	@Override
	public void setLineWidth(final int w) {
		if (lineWidth == w)
			return;
		if (w < lineWidth) // The bounds will become smaller, so erase must occur first.
			erase();
		bounds = null;
		super.setLineWidth(w);
	}

	/**
	 * Sets the point at <code>index</code> to the Point <code>pt</code>. Calling
	 * this method results in a recalculation of the polyline's bounding box. If
	 * you're going to set multiple Points, use {@link #setPoints(PointList)}.
	 *
	 * @param pt    the point
	 * @param index the index
	 */
	public void setPoint(final Point pt, final int index) {
		erase();
		points.setPoint(pt, index);
		bounds = null;
		repaint();
	}

	/**
	 * Sets the list of points to be used by this polyline connection. Removes any
	 * previously existing points. The polyline will hold onto the given list by
	 * reference.
	 *
	 * @param newPoints new set of points
	 * @since 2.0
	 */
	public void setPoints(final PointList newPoints) {
		erase();
		this.points = newPoints;
		bounds = null;
		firePropertyChange(Connection.PROPERTY_POINTS, null, newPoints);
		repaint();
	}

	/**
	 * Sets the start point of the Polyline
	 *
	 * @param start the point that will become the first point in the Polyline
	 * @since 2.0
	 */
	public void setStart(final Point start) {
		if (points.size() == 0)
			addPoint(start);
		else
			setPoint(start, 0);
	}

}
