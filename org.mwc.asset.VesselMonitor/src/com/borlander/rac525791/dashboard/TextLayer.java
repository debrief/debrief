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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardFonts;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.text.AbstractTextLayer;
import com.borlander.rac525791.dashboard.text.CenteredText;
import com.borlander.rac525791.dashboard.text.TextDrawer;
import com.borlander.rac525791.dashboard.text.TwoLinesText;
import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;

public class TextLayer extends AbstractTextLayer {
	private class Layout extends BaseDashboardLayout {
		private final Rectangle TOP = new Rectangle();
		private final Rectangle BOTTOM = new Rectangle();
		private final Rectangle UNION = new Rectangle();

		public Layout(final DashboardUIModel uiModel) {
			super(uiModel);
		}

		@Override
		public void layout(final IFigure container) {
			final ControlUISuite suite = getSuite(container);
			final DashboardFonts fonts = suite.getFonts();

			myLeftText.setFont(fonts.getTextFont());
			myRightText.setFont(fonts.getTextFont());
			myCenterText.setFont(fonts.getValueFont());

			layoutTwoLinesText(container, myLeftText, myLeftPanel, suite.getVesselNameBounds());
			layoutTwoLinesText(container, myRightText, myRightPanel, suite.getVesselStatusBounds());

			placeAtTopLeft(container, TOP);
			final Rectangle courseBounds = suite.getCourseValueBounds();
			TOP.setSize(courseBounds.width, courseBounds.height);
			TOP.translate(courseBounds.x, courseBounds.y);

			myCenterText.setBounds(TOP);
		}

		private void layoutTwoLinesText(final IFigure container, final TwoLinesText twoLinesText, final IFigure panel,
				final Rectangle name) {
			placeAtTopLeft(container, UNION);

			UNION.translate(name.x, name.y);
			UNION.setSize(name.width, name.height);
			UNION.shrink(2, 2);

			TOP.setLocation(UNION.x, UNION.y);
			TOP.setSize(UNION.width, UNION.height / 2);

			BOTTOM.setLocation(UNION.x, UNION.y);
			BOTTOM.setSize(UNION.width, UNION.height - UNION.height / 2);
			BOTTOM.translate(0, UNION.height / 2);

			twoLinesText.setBounds(TOP, BOTTOM);
			if (panel != null) {
				panel.setBounds(UNION);
			}
		}
	}

	private static class ShadowRectangle extends InvisibleRectangle {
		private static final Color SHADOW = new Color(null, 196, 196, 196);
		private static final Color FORE = ColorConstants.black;
		private static final Color TOP = new Color(null, 51, 51, 51);
		private static final Color BOTTOM = new Color(null, 115, 115, 115);

		private final Color myShadowColor;
		private final Color myForeColor;
		private final Color myTopColor;
		private final Color myBottomColor;
		private final int myShadowX;
		private final int myShadowY;

		public ShadowRectangle() {
			this(1, 1, FORE, SHADOW, TOP, BOTTOM);
		}

		public ShadowRectangle(final int shadowX, final int shadowY, final Color foreColor, final Color shadowColor,
				final Color topColor, final Color bottomColor) {
			myShadowX = shadowX;
			myShadowY = shadowY;
			myForeColor = foreColor;
			myShadowColor = shadowColor;
			myTopColor = topColor;
			myBottomColor = bottomColor;
		}

		@Override
		protected void fillShape(final Graphics g) {
			final Rectangle b = getBounds();

			final int x = b.x;
			final int y = b.y;
			final int width = b.width;
			final int height = b.height;

			g.pushState();
			g.setForegroundColor(myTopColor);
			g.setBackgroundColor(myBottomColor);
			g.fillGradient(x + 1, y + 1, width - myShadowX - 2, height - myShadowY - 2, true);
			g.popState();
		}

		@Override
		protected void outlineShape(final Graphics g) {
			final Rectangle b = getBounds();

			final int x = b.x;
			final int y = b.y;
			final int width = b.width;
			final int height = b.height;

			g.pushState();
			g.setForegroundColor(myShadowColor);
			g.drawRectangle(x + myShadowX, y + myShadowY, width - myShadowX, height - myShadowY);

			g.setForegroundColor(myForeColor);
			g.drawRectangle(x, y, width - myShadowX, height - myShadowY);
			g.popState();
		}
	}

	private static final Color TEXT_COLOR = new Color(null, 33, 255, 22);

	private static String safeText(final String text) {
		return text == null ? "" : text.trim();
	}

	private final TextDrawer[] myTextDrawers;

	TwoLinesText myLeftText;
	TwoLinesText myRightText;

	CenteredText myCenterText;

	ShadowRectangle myLeftPanel;

	ShadowRectangle myRightPanel;

	public TextLayer(final DashboardUIModel uiModel) {
		setForegroundColor(TEXT_COLOR);
		setLayoutManager(new Layout(uiModel));

		myLeftText = new TwoLinesText();
		myRightText = new TwoLinesText();
		myCenterText = new CenteredText();

		myTextDrawers = new TextDrawer[] { myRightText, myLeftText, myCenterText };

		myLeftPanel = new ShadowRectangle();
		myRightPanel = new ShadowRectangle();

		this.add(myLeftPanel);
		this.add(myRightPanel);

		setLeftText("");
		setRightText("");
		setCenterText("");
	}

	@Override
	protected TextDrawer[] getTextDrawers() {
		return myTextDrawers;
	}

	public void setCenterText(final String text) {
		myCenterText.setText(safeText(text));
		invalidate();
	}

	public void setLeftText(final String text) {
		myLeftText.setText(safeText(text));
		invalidate();
	}

	public void setRightText(final String text) {
		myRightText.setText(safeText(text));
		invalidate();
	}
}
