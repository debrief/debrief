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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public enum Quadrant {
	LEFT_TOP {
		@Override
		public Quadrant flip() {
			return RIGHT_TOP;
		}

		@Override
		public Quadrant opposite() {
			return RIGHT_BOTTOM;
		}

		@Override
		public void point(final Rectangle r, final Point p) {
			p.setLocation(r.x, r.y);
		}
	},
	LEFT_BOTTOM {
		@Override
		public Quadrant flip() {
			return RIGHT_BOTTOM;
		}

		@Override
		public Quadrant opposite() {
			return RIGHT_TOP;
		}

		@Override
		public void point(final Rectangle r, final Point p) {
			p.setLocation(r.x, r.y + r.width);
		}
	},
	RIGHT_TOP {
		@Override
		public Quadrant flip() {
			return LEFT_TOP;
		}

		@Override
		public Quadrant opposite() {
			return LEFT_BOTTOM;
		}

		@Override
		public void point(final Rectangle r, final Point p) {
			p.setLocation(r.x + r.width, r.y);
		}
	},
	RIGHT_BOTTOM {
		@Override
		public Quadrant flip() {
			return RIGHT_TOP;
		}

		@Override
		public Quadrant opposite() {
			return RIGHT_TOP;
		}

		@Override
		public void point(final Rectangle r, final Point p) {
			p.setLocation(r.x + r.width, r.y + r.height);
		}
	};

	public static Quadrant valueOfAngle(final double angle) {
		if (angle > 0) {
			return angle >= Math.PI / 2 ? LEFT_BOTTOM : RIGHT_BOTTOM;
		}
		return angle >= -Math.PI / 2 ? RIGHT_TOP : LEFT_TOP;
	}

	public Quadrant flip() {
		throw notSupported();
	}

	private UnsupportedOperationException notSupported() {
		return new UnsupportedOperationException("Implementation should override");
	}

	public Quadrant opposite() {
		throw notSupported();
	}

	public void oppositePoint(final Rectangle r, final Point p) {
		opposite().point(r, p);
	}

	public void point(final Rectangle r, final Point p) {
		throw notSupported();
	}

}