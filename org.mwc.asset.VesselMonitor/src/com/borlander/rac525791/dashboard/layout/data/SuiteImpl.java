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

package com.borlander.rac525791.dashboard.layout.data;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.borlander.rac525791.dashboard.layout.ControlUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardFonts;
import com.borlander.rac525791.dashboard.layout.DashboardImages;

public class SuiteImpl implements ControlUISuite {
	private static class TextPanesConfig {
		private final int myPadding;
		private final int myTop;
		private final int myBottom;

		public TextPanesConfig(final int padding, final int top, final int bottom) {
			myPadding = padding;
			myTop = top;
			myBottom = bottom;
		}

		public int getBottom() {
			return myBottom;
		}

		public int getPadding() {
			return myPadding;
		}

		public int getTop() {
			return myTop;
		}
	}

	public static ControlUISuite create280x160() {
		return new SuiteImpl(//
				280, 160, //
				new Rectangle(114, 17, 52, 12), //
				panes(4, 17, 45), //
				"images/280x160", //
				new Speed280x160(), //
				new Depth280x160(), //
				new Direction280x160(), //
				fonts(6, 8, 7));
	}

	public static ControlUISuite create320x183() {
		return new SuiteImpl(//
				320, 183, //
				new Rectangle(130, 19, 59, 16), //
				panes(4, 19, 52), //
				"images/320x183", //
				new Speed320x183(), //
				new Depth320x183(), //
				new Direction320x183(), //
				fonts(7, 9, 8));
	}

	public static ControlUISuite create360x206() {
		return new SuiteImpl(//
				360, 206, //
				new Rectangle(146, 22, 67, 16), //
				panes(4, 22, 58), //
				"images/360x206", //
				new Speed360x206(), //
				new Depth360x206(), //
				new Direction360x206(), //
				fonts(7, 10, 9));
	}

	public static ControlUISuite create400x229() {
		return new SuiteImpl(//
				400, 229, //
				new Rectangle(163, 25, 73, 20), //
				panes(6, 26, 65), "images/400x229", //
				new Speed400x229(), //
				new Depth400x229(), //
				new Direction400x229(), //
				fonts(8, 12, 10));
	}

	private static DashboardFontsImpl fonts(final int text, final int value, final int units) {
		return new DashboardFontsImpl(text, value, units);
	}

	private static TextPanesConfig panes(final int pad, final int top, final int bottom) {
		return new TextPanesConfig(pad, top, bottom);
	}

	private final ControlUIModel myDepth;

	private final ControlUIModel mySpeed;

	private final ControlUIModel myDirection;

	private final Dimension myPrefSize;

	private final DashboardImages myImages;

	private final DashboardFonts myFonts;

	private final Rectangle myCourseValueBounds;

	private final Rectangle myNameBounds;

	private final Rectangle myStatusBounds;

	private SuiteImpl(final int width, final int height, final Rectangle courseValueBounds, final TextPanesConfig panes,
			final String path, final ControlUIModel speed, final ControlUIModel depth, final ControlUIModel direction,
			final DashboardFonts fonts) {
		myCourseValueBounds = courseValueBounds;
		myFonts = fonts;
		myPrefSize = new Dimension(width, height);
		myImages = new DashboardImagesImpl(Display.getCurrent(), path, true);

		myDepth = depth;
		mySpeed = speed;
		myDirection = direction;

		// position panes symmetrically against control center
		final int pad = panes.getPadding();
		final int top = panes.getTop();
		final int bottom = panes.getBottom();
		final int speedCenter = mySpeed.getControlCenter().x;
		final int depthCenter = myDepth.getControlCenter().x;
		myNameBounds = new Rectangle(pad, top, (speedCenter - pad) * 2, bottom - top);
		myStatusBounds = new Rectangle(width - pad - (width - pad - depthCenter) * 2, top,
				(width - pad - depthCenter) * 2, bottom - top);
	}

	@Override
	public void dispose() {
		myImages.dispose();
		myFonts.dispose();
	}

	@Override
	public Rectangle getCourseValueBounds() {
		return myCourseValueBounds;
	}

	@Override
	public ControlUIModel getDepth() {
		return myDepth;
	}

	@Override
	public ControlUIModel getDirection() {
		return myDirection;
	}

	@Override
	public DashboardFonts getFonts() {
		return myFonts;
	}

	@Override
	public DashboardImages getImages() {
		return myImages;
	}

	@Override
	public Dimension getPreferredSize() {
		// XXX: can we cache it? does draw2d layouting change it?
		return myPrefSize.getCopy();
	}

	@Override
	public Dimension getPreferredSizeRO() {
		return myPrefSize;
	}

	@Override
	public ControlUIModel getSpeed() {
		return mySpeed;
	}

	@Override
	public double getTemplatesScale() {
		return myPrefSize.width / 280.0;
	}

	@Override
	public Rectangle getVesselNameBounds() {
		return myNameBounds;
	}

	@Override
	public Rectangle getVesselStatusBounds() {
		return myStatusBounds;
	}

}
