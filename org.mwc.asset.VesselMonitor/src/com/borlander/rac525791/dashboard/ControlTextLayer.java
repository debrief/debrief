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

package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardFonts;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.text.AbstractTextLayer;
import com.borlander.rac525791.dashboard.text.CenteredText;
import com.borlander.rac525791.dashboard.text.TextDrawer;

public class ControlTextLayer extends AbstractTextLayer {

	private static class GradientRoundedRectangle extends RoundedRectangle {
		private Color myTopColor = ColorConstants.red;
		private Color myBottomColor = ColorConstants.red;
		private Pattern myPattern;
		private final Rectangle CACHED_BOUNDS = new Rectangle();
		private final Rectangle TEMP = new Rectangle();

		public GradientRoundedRectangle() {
			setForegroundColor(ColorConstants.black);
			setFill(true);
			setOutline(true);
		}

		private Pattern createPattern(final Rectangle localBounds) {
			final int left = localBounds.x;
			final int right = localBounds.x + localBounds.width;
			final int top = localBounds.y;
			final int bottom = localBounds.y + localBounds.height;
			return new Pattern(null, right, top, left, bottom, myTopColor, myBottomColor);
		}

		@Override
		protected void fillShape(final Graphics g) {
			g.pushState();
			g.setBackgroundPattern(getPattern(getBounds()));
			TEMP.setBounds(getBounds());
			TEMP.shrink(1, 1);
			g.fillRoundRectangle(TEMP, corner.width, corner.height);
			g.popState();
		}

		private Pattern getPattern(final Rectangle localBounds) {
			if (myPattern != null && !myPattern.isDisposed() && CACHED_BOUNDS.equals(localBounds)) {
				return myPattern;
			}
			CACHED_BOUNDS.setBounds(localBounds);
			if (myPattern != null) {
				myPattern.dispose();
			}
			myPattern = createPattern(localBounds);
			return myPattern;
		}

		public void setBottomColor(final Color bottomColor) {
			myBottomColor = bottomColor;
			if (myPattern != null) {
				myPattern.dispose();
			}
		}

		public void setTopColor(final Color topColor) {
			myTopColor = topColor;
			if (myPattern != null) {
				myPattern.dispose();
			}
		}

	}

	private class Layout extends BaseDashboardLayout {
		private final Rectangle RECT = new Rectangle();

		public Layout(final DashboardUIModel uiModel) {
			super(uiModel);
		}

		@Override
		public void layout(final IFigure container) {
			assert container == ControlTextLayer.this;
			// System.out.println("ControlTextLayer.Layout.layout()");

			final ControlUISuite suite = getSuite(container);
			final DashboardFonts fonts = suite.getFonts();
			mySpeedText.setFont(fonts.getValueFont());
			myDepthText.setFont(fonts.getValueFont());

			layoutControlValue(suite.getSpeed(), mySpeed, mySpeedText, container);
			layoutControlValue(suite.getDepth(), myDepth, myDepthText, container);
		}

		private void layoutControlValue(final ControlUIModel positions, final GradientRoundedRectangle back,
				final CenteredText text, final IFigure container) {
			placeAtTopLeft(container, RECT);
			RECT.translate(positions.getControlCenter());
			RECT.translate(positions.getValueTextPosition());
			RECT.setSize(positions.getValueTextSize());
			back.setBounds(RECT);

			RECT.shrink(1, 1);
			text.setBounds(RECT);
		}
	}

	private static final Color LIGHT_PINK = new Color(null, 255, 190, 190);
	private static final Color DARK_PINK = new Color(null, 255, 92, 92);

	private static final Color LIGHT_GREEN = new Color(null, 191, 255, 180);
	private static final Color DARK_GREEN = new Color(null, 92, 255, 64);

	private static String formatValue(final int value) {
		if (value < 0) {
			throw new IllegalArgumentException("Expected not negative integer: " + value);
		}
		if (value < 10) {
			return "." + value;
		}
		return String.valueOf(value / 10);
	}

	private static void setGradient(final GradientRoundedRectangle rect, final boolean isOK) {
		if (isOK) {
			rect.setTopColor(LIGHT_GREEN);
			rect.setBottomColor(DARK_GREEN);
		} else {
			rect.setTopColor(LIGHT_PINK);
			rect.setBottomColor(DARK_PINK);
		}
		rect.invalidate();
	}

	GradientRoundedRectangle mySpeed;

	GradientRoundedRectangle myDepth;

	final CenteredText mySpeedText;

	final CenteredText myDepthText;

	private final TextDrawer[] myTextDrawers;

	public ControlTextLayer(final DashboardUIModel uiModel) {
		setLayoutManager(new Layout(uiModel));
		setForegroundColor(ColorConstants.black);

		mySpeed = new GradientRoundedRectangle();
		setGradient(mySpeed, true);

		myDepth = new GradientRoundedRectangle();
		setGradient(myDepth, true);

		this.add(mySpeed);
		this.add(myDepth);

		mySpeedText = new CenteredText();
		myDepthText = new CenteredText();

		myTextDrawers = new TextDrawer[] { mySpeedText, myDepthText };
	}

	@Override
	protected TextDrawer[] getTextDrawers() {
		return myTextDrawers;
	}

	public void setDepth(final int depth) {
		myDepthText.setText(formatValue(depth));
		invalidate();
	}

	public void setSpeed(final int speed) {
		mySpeedText.setText(formatValue(speed));
		invalidate();
	}

	public void updateDepthGradient(final boolean isOnThreshold) {
		setGradient(myDepth, isOnThreshold);
	}

	public void updateSpeedGradient(final boolean isOnThreshold) {
		setGradient(mySpeed, isOnThreshold);
	}

}
