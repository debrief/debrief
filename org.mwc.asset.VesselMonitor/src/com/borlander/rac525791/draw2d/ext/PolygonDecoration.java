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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Transform;
import org.eclipse.swt.graphics.Color;

/**
 * A rotatable, polygon shaped decoration most commonly used for decorating the
 * ends of {@link com.borlander.rac525791.draw2d.ext.Polyline polylines}.
 */
public class PolygonDecoration extends Polygon implements RotatableDecorationExt {

	/**
	 * Template for a triangle that points to the right when the rotation angle is 0
	 */
	public static final PointList TRIANGLE_TIP = new PointList();

	/**
	 * Template for a triangle that points to the left when the rotation angle is 0
	 */
	public static final PointList INVERTED_TRIANGLE_TIP = new PointList();

	static {
		TRIANGLE_TIP.addPoint(0, 0);
		TRIANGLE_TIP.addPoint(-1, 1);
		TRIANGLE_TIP.addPoint(-1, -1);

		INVERTED_TRIANGLE_TIP.addPoint(0, 1);
		INVERTED_TRIANGLE_TIP.addPoint(0, -1);
		INVERTED_TRIANGLE_TIP.addPoint(-1, 0);
	}

	private final Point location = new Point();

	private PointList template = TRIANGLE_TIP;

	private final Transform transform = new Transform();

	/**
	 * Constructs a PolygonDecoration. Defaults the PolygonDecoration to fill its
	 * region with black.
	 *
	 * @since 2.0
	 */
	public PolygonDecoration() {
		setFill(true);
		setScale(1, 1);
	}

	/**
	 * @see org.eclipse.draw2d.IFigure#getBackgroundColor()
	 */
	@Override
	public Color getLocalBackgroundColor() {
		if (super.getLocalBackgroundColor() == null)
			return getForegroundColor();
		return super.getLocalBackgroundColor();
	}

	/**
	 * Returns the points in the PolygonDecoration as a PointList.
	 *
	 * @return the points in this PolygonDecoration
	 * @since 2.0
	 */
	@Override
	public PointList getPoints() {
		if (points == null) {
			points = new PointList();
			for (int i = 0; i < template.size(); i++)
				points.addPoint(transform.getTransformed(template.getPoint(i)));
		}
		return points;
	}

	public PointList getTemplateCopy() {
		return template.getCopy();
	}

	public Point getTemplatePoint(final int index, final Point output) {
		return template.getPoint(output, index);
	}

	/**
	 * Sets the location of this PolygonDecoration.
	 *
	 * @param p the new location
	 */
	@Override
	public void setLocation(final Point p) {
		points = null;
		bounds = null;
		location.setLocation(p);
		transform.setTranslation(p.x, p.y);
	}

	/**
	 * Sets the rotation of this decoration so that the decoration points toward the
	 * given reference point.
	 *
	 * @param ref the reference point
	 */
	@Override
	public void setReferencePoint(final Point ref) {
		final Point pt = Point.SINGLETON;
		pt.setLocation(ref);
		pt.negate().translate(location);
		setRotation(Math.atan2(pt.y, pt.x));
	}

	/**
	 * Sets the angle by which rotation is to be done on the PolygonDecoration.
	 *
	 * @param angle Angle of rotation
	 * @since 2.0
	 */
	@Override
	public void setRotation(final double angle) {
		points = null;
		bounds = null;
		transform.setRotation(angle);
	}

	/**
	 * Sets the amount of scaling to be done along X and Y axes on the
	 * PolygonDecoration's template.
	 *
	 * @param x X scaling
	 * @param y Y scaling
	 * @since 2.0
	 */
	@Override
	public void setScale(final double x, final double y) {
		points = null;
		bounds = null;
		transform.setScale(x, y);
	}

	/**
	 * Sets the PolygonDecorations point template to the passed PointList. This
	 * template is an outline of the PolygonDecoration's region. (The default value
	 * is TRIANGLE_TIP which is a triangle whose tip is pointing to the right).
	 *
	 * @param pl the PointList outline to use as the PolygonDecoration's region
	 * @since 2.0
	 */
	public void setTemplate(final PointList pl) {
		erase();
		template = pl;
		points = null;
		bounds = null;
		repaint();
	}

	public Point transformPoint(final Point pt) {
		return transform.getTransformed(pt);
	}

}
