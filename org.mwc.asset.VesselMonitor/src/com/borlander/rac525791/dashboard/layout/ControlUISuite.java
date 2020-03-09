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

package com.borlander.rac525791.dashboard.layout;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public interface ControlUISuite {
	public static interface ControlAccess {
		ControlUIModel selectControl(ControlUISuite suite);
	}

	public static final ControlAccess SPEED = new ControlAccess() {
		@Override
		public ControlUIModel selectControl(final ControlUISuite suite) {
			return suite.getSpeed();
		}
	};

	public static final ControlAccess DEPTH = new ControlAccess() {
		@Override
		public ControlUIModel selectControl(final ControlUISuite suite) {
			return suite.getDepth();
		}
	};

	public static final ControlAccess DIRECTION = new ControlAccess() {
		@Override
		public ControlUIModel selectControl(final ControlUISuite suite) {
			return suite.getDirection();
		}
	};

	public void dispose();

	public Rectangle getCourseValueBounds();

	public ControlUIModel getDepth();

	public ControlUIModel getDirection();

	public DashboardFonts getFonts();

	public DashboardImages getImages();

	public Dimension getPreferredSize();

	/**
	 * The same as <code>getPreferredSize</code> but in contrast to untrusted code
	 * from draw2d this method is called by trusted client that NEVER changes return
	 * value.
	 */
	public Dimension getPreferredSizeRO();

	public ControlUIModel getSpeed();

	public double getTemplatesScale();

	public Rectangle getVesselNameBounds();

	public Rectangle getVesselStatusBounds();

}
