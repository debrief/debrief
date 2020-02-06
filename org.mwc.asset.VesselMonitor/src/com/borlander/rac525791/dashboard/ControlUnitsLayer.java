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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUIModel;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.text.AbstractTextLayer;
import com.borlander.rac525791.dashboard.text.CenteredText;
import com.borlander.rac525791.dashboard.text.TextDrawer;

public class ControlUnitsLayer extends AbstractTextLayer {
	private class Layout extends BaseDashboardLayout {
		private final Rectangle RECT = new Rectangle();

		public Layout(final DashboardUIModel uiModel) {
			super(uiModel);
		}

		@Override
		public void layout(final IFigure container) {
			assert container == ControlUnitsLayer.this;
			// System.out.println("ControlUnitsLayer.Layout.layout()");

			final ControlUISuite suite = getSuite(container);
			final Font unitsFont = suite.getFonts().getUnitsFont();
			for (final TextDrawer next : getTextDrawers()) {
				next.setFont(unitsFont);
			}

			layoutUnitsAndMultipliers(suite.getSpeed(), mySpeedUnits, mySpeedMultiplier, container);
			layoutUnitsAndMultipliers(suite.getDepth(), myDepthUnits, myDepthMultiplier, container);

			// now depth is layouted, we can place "x" using just set block positions
			final ControlUIModel depth = suite.getDepth();
			final int X_WIDTH = 4;
			RECT.setSize(X_WIDTH, suite.getDepth().getUnitsAndMultipliersSize().height);

			placeAtTopLeft(container, RECT);
			RECT.translate(depth.getControlCenter());
			RECT.translate(depth.getUnitsPosition()); // at the top-left corner of "units"
			RECT.translate(-X_WIDTH, depth.getUnitsAndMultipliersSize().height / 2);
			myDepthXOnly.setBounds(RECT);
		}

		private void layoutUnitsAndMultipliers(final ControlUIModel positions, final CenteredText units,
				final CenteredText multiplier, final IFigure container) {
			placeAtTopLeft(container, RECT);
			RECT.translate(positions.getControlCenter());
			RECT.translate(positions.getUnitsPosition());
			RECT.setSize(positions.getUnitsAndMultipliersSize());
			units.setBounds(RECT);

			RECT.translate(0, positions.getUnitsAndMultipliersSize().height);
			multiplier.setBounds(RECT);
		}
	}

	private static final Color GRAY = new Color(null, 196, 196, 196);

	private final TextDrawer[] myDrawers;
	CenteredText mySpeedUnits;

	CenteredText mySpeedMultiplier;
	CenteredText myDepthUnits;
	CenteredText myDepthMultiplier;

	CenteredText myDepthXOnly;

	public ControlUnitsLayer(final DashboardUIModel uiModel) {
		setLayoutManager(new Layout(uiModel));
		setForegroundColor(GRAY);

		mySpeedUnits = new CenteredText();
		mySpeedMultiplier = new CenteredText();
		myDepthUnits = new CenteredText();
		myDepthMultiplier = new CenteredText();
		myDepthXOnly = new CenteredText();
		myDrawers = new TextDrawer[] { mySpeedUnits, mySpeedMultiplier, myDepthUnits, myDepthMultiplier, myDepthXOnly };
	}

	@Override
	protected TextDrawer[] getTextDrawers() {
		return myDrawers;
	}

	public void setDepthMultiplier(final int multiplier) {
		myDepthXOnly.setText("x");
		myDepthMultiplier.setText(String.valueOf(multiplier));
	}

	public void setDepthUnits(final String units) {
		myDepthUnits.setText(units);
	}

	public void setSpeedMultiplier(final int multiplier) {
		mySpeedMultiplier.setText("x" + String.valueOf(multiplier));
	}

	public void setSpeedUnits(final String units) {
		mySpeedUnits.setText(units);
	}
}
