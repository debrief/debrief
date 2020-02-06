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

package com.borlander.rac525791.dashboard.rotatable;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;

public abstract class AbstractGradientDecoration extends PolygonDecoration {
	private final Rectangle CACHED_BOUNDS = new Rectangle();
	private Pattern myPattern;
	private Color myShadowColor;
	private Quadrant myQuadrant = Quadrant.LEFT_TOP;

	public AbstractGradientDecoration() {
		setFill(true);
	}

	protected abstract Pattern createPattern();

	public void dispose() {
		if (myPattern != null) {
			myPattern.dispose();
		}
	}

	protected void fillShadow(final Graphics g) {
		final Color shadowColor = getShadowColor();
		if (shadowColor != null) {
			final int DX = 2;
			final int DY = getShadowDY();
			g.pushState();
			g.setBackgroundColor(shadowColor);
			final PointList pointListRef = getPoints();
			pointListRef.performTranslate(DX, DY);
			g.fillPolygon(pointListRef);
			pointListRef.performTranslate(-DX, -DY);
			g.popState();
		}
	}

	@Override
	protected void fillShape(final Graphics g) {
		fillShadow(g);

		final Pattern pattern = getPattern(getBounds());
		g.pushState();
		g.setBackgroundPattern(pattern);
		super.fillShape(g);
		g.popState();
	}

	protected final Pattern getPattern(final Rectangle loacalBounds) {
		if (myPattern != null && !myPattern.isDisposed() && CACHED_BOUNDS.equals(loacalBounds)) {
			return myPattern;
		}
		CACHED_BOUNDS.setBounds(loacalBounds);
		if (myPattern != null) {
			myPattern.dispose();
		}
		myPattern = createPattern();
		return myPattern;
	}

	protected final Quadrant getQuadrant() {
		return myQuadrant;
	}

	private Color getShadowColor() {
		return myShadowColor;
	}

	private int getShadowDY() {
		final Quadrant quadrant = getQuadrant();
		return (quadrant == Quadrant.RIGHT_BOTTOM || quadrant == Quadrant.LEFT_TOP) ? -2 : 2;
	}

	@Override
	protected void outlineShape(final Graphics g) {
		g.pushState();
		g.setForegroundPattern(getPattern(getBounds()));
		super.outlineShape(g);
		g.popState();
	}

	@Override
	public void paint(final Graphics graphics) {
		final int old = graphics.getAntialias();
		graphics.setAntialias(SWT.ON);
		super.paint(graphics);
		graphics.setAntialias(old);
	}

	@Override
	public void setRotation(final double angle) {
		super.setRotation(angle);
		myQuadrant = Quadrant.valueOfAngle(angle);
	}

	public void setShadowColor(final Color color) {
		myShadowColor = color;
	}

}