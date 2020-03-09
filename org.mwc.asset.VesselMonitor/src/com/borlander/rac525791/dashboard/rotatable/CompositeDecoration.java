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

import java.util.LinkedList;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;
import com.borlander.rac525791.draw2d.ext.RotatableDecorationExt;

public class CompositeDecoration extends Figure implements RotatableDecorationExt {
	private final LinkedList<PolygonDecoration> myDecorations;

	public CompositeDecoration(final Color shadowColor, final PolygonDecoration... decorations) {
		myDecorations = new LinkedList<PolygonDecoration>();
		if (shadowColor != null) {
			for (final PolygonDecoration next : decorations) {
				final FilledDecoration nextShadow = new FilledDecoration();
				nextShadow.setTemplate(next.getTemplateCopy());
				nextShadow.setFillColor(shadowColor);
				nextShadow.setFill(true);

				addDecoration(nextShadow);
			}
		}

		for (final PolygonDecoration next : decorations) {
			addDecoration(next);
		}
	}

	public CompositeDecoration(final PolygonDecoration... decorations) {
		this(null, decorations);
	}

	private void addDecoration(final PolygonDecoration decoration) {
		myDecorations.add(decoration);
		this.add(decoration);
	}

	@Override
	public Rectangle getBounds() {
		Rectangle max = null;
		if (myDecorations != null) {
			for (final PolygonDecoration next : myDecorations) {
				if (max == null) {
					max = new Rectangle(next.getBounds());
				} else {
					max.union(next.getBounds());
				}
			}
		}
		return max == null ? super.getBounds() : max;
	}

	@Override
	public void paint(final Graphics graphics) {
		for (final PolygonDecoration next : myDecorations) {
			((Figure) next).paint(graphics);
		}
	}

	@Override
	public void setBackgroundColor(final Color bg) {
		for (final PolygonDecoration next : myDecorations) {
			next.setBackgroundColor(bg);
		}
	}

	@Override
	public void setBounds(final Rectangle rect) {
		super.setBounds(rect);
		for (final PolygonDecoration next : myDecorations) {
			next.setBounds(rect);
		}
	}

	@Override
	public void setForegroundColor(final Color fg) {
		for (final PolygonDecoration next : myDecorations) {
			next.setForegroundColor(fg);
		}
	}

	@Override
	public void setLocation(final Point p) {
		for (final PolygonDecoration next : myDecorations) {
			next.setLocation(p);
		}
	}

	@Override
	public void setReferencePoint(final Point p) {
		for (final PolygonDecoration next : myDecorations) {
			next.setReferencePoint(p);
		}
	}

	@Override
	public void setRotation(final double angle) {
		for (final PolygonDecoration next : myDecorations) {
			next.setRotation(angle);
		}
	}

	@Override
	public void setScale(final double xScale, final double yScale) {
		for (final PolygonDecoration next : myDecorations) {
			next.setScale(xScale, yScale);
		}
	}

}