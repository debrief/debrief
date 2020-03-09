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
package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.ui.editor.Activator;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class ChartsetFigure extends DirectionalShape {
	private static volatile Font boldFont;
	private final DirectionalLabel chartsetHeader;

	public ChartsetFigure(final ActionListener addChartHandler) {
		add(new Label(StackedchartsImages.getImage(StackedchartsImages.DESC_CHARTSET)));
		chartsetHeader = new DirectionalLabel(Activator.FONT_12);
		chartsetHeader.setText("Chart Set");
		chartsetHeader.setTextAlignment(PositionConstants.TOP);
		add(chartsetHeader);

		final Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_ADD));
		button.setToolTip(new Label("Add new chart"));
		button.addActionListener(addChartHandler);
		add(button);

	}

	@Override
	public void paint(final Graphics graphics) {

		if (boldFont == null) {
			final FontData fontData = Display.getDefault().getActiveShell().getFont().getFontData()[0];
			boldFont = new Font(Display.getDefault(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		}
		chartsetHeader.setFont(boldFont);

		super.paint(graphics);
	}

	@Override
	public void setVertical(final boolean vertical) {
		super.setVertical(vertical);
		chartsetHeader.setVertical(vertical);
	}

}