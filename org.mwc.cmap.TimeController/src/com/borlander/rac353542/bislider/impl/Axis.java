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

package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

abstract class Axis {
	private static final Axis AXIS_X = new Axis() {
		@Override
		public void advance(final Point p, final int delta) {
			p.x += delta;
		}

		@Override
		public void advanceNormal(final Point p, final int delta) {
			p.y += delta;
		}

		@Override
		public Rectangle createRectangle(final int x, final int y, final int size, final int normalSize) {
			return new Rectangle(x, y, size, normalSize);
		}

		@Override
		public int get(final Point point) {
			return point.x;
		}

		@Override
		public double getAsDouble(final Point point) {
			return get(point);
		}

		@Override
		public int getCenter(final Rectangle rectangle) {
			return rectangle.x + rectangle.width / 2;
		}

		@Override
		public int getMax(final Rectangle rectangle) {
			return rectangle.x + rectangle.width;
		}

		@Override
		public int getMin(final Rectangle rectangle) {
			return rectangle.x;
		}

		@Override
		public int getNormal(final Point point) {
			return point.y;
		}

		@Override
		public int getNormalCenter(final Rectangle rectangle) {
			return rectangle.y + rectangle.height / 2;
		}

		@Override
		public int getNormalDelta(final Rectangle rectangle) {
			return rectangle.height;
		}

		@Override
		public int getNormalMax(final Rectangle rectangle) {
			return rectangle.y + rectangle.height;
		}

		@Override
		public int getNormalMin(final Rectangle rectangle) {
			return rectangle.y;
		}

		@Override
		public void set(final Point point, final int value) {
			point.x = value;
		}

		@Override
		public void setNormal(final Point point, final int value) {
			point.y = value;
		}
	};
	private static final Axis AXIS_Y = new Axis() {
		@Override
		public void advance(final Point p, final int delta) {
			p.y += delta;
		}

		@Override
		public void advanceNormal(final Point p, final int delta) {
			p.x += delta;
		}

		@Override
		public Rectangle createRectangle(final int x, final int y, final int size, final int normalSize) {
			return new Rectangle(x, y, normalSize, size);
		}

		@Override
		public int get(final Point point) {
			return point.y;
		}

		@Override
		public double getAsDouble(final Point point) {
			return get(point);
		}

		@Override
		public int getCenter(final Rectangle rectangle) {
			return rectangle.y + rectangle.height / 2;
		}

		@Override
		public int getMax(final Rectangle rectangle) {
			return rectangle.y + rectangle.height;
		}

		@Override
		public int getMin(final Rectangle rectangle) {
			return rectangle.y;
		}

		@Override
		public int getNormal(final Point point) {
			return point.x;
		}

		@Override
		public int getNormalCenter(final Rectangle rectangle) {
			return rectangle.x + rectangle.width / 2;
		}

		@Override
		public int getNormalDelta(final Rectangle rectangle) {
			return rectangle.width;
		}

		@Override
		public int getNormalMax(final Rectangle rectangle) {
			return rectangle.x + rectangle.width;
		}

		@Override
		public int getNormalMin(final Rectangle rectangle) {
			return rectangle.x;
		}

		@Override
		public void set(final Point point, final int value) {
			point.y = value;
		}

		@Override
		public void setNormal(final Point point, final int value) {
			point.x = value;
		}
	};

	public static Axis getAxis(final boolean verticalNotHorizontal) {
		return verticalNotHorizontal ? AXIS_Y : AXIS_X;
	}

	public abstract void advance(Point p, int delta);

	public abstract void advanceNormal(Point p, int delta);

	public abstract Rectangle createRectangle(int x, int y, int size, int normalSize);

	public final Rectangle createRectangle(final Point point, final int size, final int normalSize) {
		return createRectangle(point.x, point.y, size, normalSize);
	}

	public abstract int get(Point point);

	public abstract double getAsDouble(Point point);

	public abstract int getCenter(Rectangle rectangle);

	public final int getDelta(final Rectangle rectangle) {
		return getMax(rectangle) - getMin(rectangle);
	}

	public abstract int getMax(Rectangle rectangle);

	public abstract int getMin(Rectangle rectangle);

	public abstract int getNormal(Point point);

	public abstract int getNormalCenter(Rectangle rectangle);

	public abstract int getNormalDelta(Rectangle rectangle);

	// [MG]public abstract Point set(Point point, int value);

	public abstract int getNormalMax(Rectangle rectangle);

	public abstract int getNormalMin(Rectangle rectangle);

	public abstract void set(Point point, int value);

	public abstract void setNormal(Point point, int value);

}