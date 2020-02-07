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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.draw2d.ext.PolygonDecoration;

public class SpeedDepthArrow extends CompositeDecoration {
	public static final double EXPECTED_LENGTH = 36;

	private static PolygonDecoration createPink() {
		final Color LIGHT = new Color(null, 255, 200, 200);
		final Color PINK = new Color(null, 252, 252, 252);

		final PointList pinkTemplate = new PointList();
		pinkTemplate.addPoint(0, -1);
		pinkTemplate.addPoint(-16, -1);
		pinkTemplate.addPoint(-16, 1);
		pinkTemplate.addPoint(0, -1);

		pinkTemplate.translate(30, 0);

		final GradientDecoration pink = new GradientDecoration();
		pink.setTemplate(pinkTemplate);

		pink.setGradient(0, PINK, 2, LIGHT);

		pink.setShadowColor(new Color(null, 100, 100, 100));

		return pink;
	}

	private static PolygonDecoration createRed() {
		final Color DARK = new Color(null, 220, 0, 0);

		final PointList redTemplate = new PointList();
		redTemplate.addPoint(-16, -1);
		redTemplate.addPoint(-41, -2);
		redTemplate.addPoint(-41, 2);
		redTemplate.addPoint(-16, 1);

		redTemplate.translate(30, 0);

		final GradientDecoration red = new GradientDecoration();
		red.setTemplate(redTemplate);
		red.setGradient(0, ColorConstants.red, 2, DARK);

		red.setShadowColor(new Color(null, 100, 100, 100));

		return red;
	}

	public SpeedDepthArrow() {
		super(createRed(), createPink());
	}
}
