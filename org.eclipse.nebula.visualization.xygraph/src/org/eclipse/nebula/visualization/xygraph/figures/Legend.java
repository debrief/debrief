/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.nebula.visualization.xygraph.util.Preferences;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * The legend to indicate the style and size of the trace line and point. The
 * border color of the legend is same as the traces' Y-Axis color.
 * 
 * @author Xihui Chen
 *
 */
public class Legend extends RectangleFigure {
	private final static int ICON_WIDTH = 25;
	private final static int INNER_GAP = 2;
	private final static int OUT_GAP = 5;

	// private final static Font LEGEND_FONT =
	// XYGraphMediaFactory.getInstance().getFont(
	// XYGraphMediaFactory.FONT_ARIAL);
	//
	// private final Color WHITE_COLOR =
	// XYGraphMediaFactory.getInstance().getColor(
	// XYGraphMediaFactory.COLOR_WHITE);

	private final Color BLACK_COLOR = XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLACK);

	private final List<Trace> traceList = new ArrayList<Trace>();

	public Legend(XYGraph xyGraph) {
		// setFont(LEGEND_FONT);
		xyGraph.getPlotArea().addPropertyChangeListener(PlotArea.BACKGROUND_COLOR, new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				setBackgroundColor((Color) evt.getNewValue());
			}
		});
		setBackgroundColor(xyGraph.getPlotArea().getBackgroundColor());
		setForegroundColor(BLACK_COLOR);
		setOpaque(false);
		setOutline(true);
	}

	/**
	 * Add a trace to the axis.
	 * 
	 * @param trace
	 *            the trace to be added.
	 */
	public void addTrace(Trace trace) {
		traceList.add(trace);
	}

	/**
	 * Remove a trace from the axis.
	 * 
	 * @param trace
	 * @return true if this axis contained the specified trace
	 */
	public boolean removeTrace(Trace trace) {
		return traceList.remove(trace);
	}

	@Override
	protected void outlineShape(Graphics graphics) {
		graphics.setForegroundColor(traceList.get(0).getYAxis().getForegroundColor());
		super.outlineShape(graphics);

	}

	@Override
	protected void fillShape(Graphics graphics) {
		if (!((XYGraph) getParent()).isTransparent())
			super.fillShape(graphics);
		int hPos = bounds.x + INNER_GAP;
		int vPos = bounds.y + INNER_GAP;
		int i = 0;
		for (Trace trace : traceList) {
			int hwidth = OUT_GAP + ICON_WIDTH + INNER_GAP
					+ +FigureUtilities.getTextExtents(trace.getName(), getFont()).width;
			int hEnd = hPos + hwidth;
			if (hEnd > (bounds.x + bounds.width) && i > 0) {
				hPos = bounds.x + INNER_GAP;
				vPos += ICON_WIDTH + INNER_GAP;
				hEnd = hPos + hwidth;
			}

			// graphics.setForegroundColor(trace.getYAxis().getForegroundColor());
			// Rectangle rect = new Rectangle(hPos, vPos-INNER_GAP/2, hwidth -
			// OUT_GAP,ICON_WIDTH-INNER_GAP);
			// graphics.fillRectangle(rect);
			// graphics.drawRectangle(rect);
			drawTraceLagend(trace, graphics, hPos, vPos);
			hPos = hEnd;
			i++;
		}

	}

	private void drawTraceLagend(Trace trace, Graphics graphics, int hPos, int vPos) {
		graphics.pushState();
		if (Preferences.useAdvancedGraphics())
			graphics.setAntialias(SWT.ON);
		graphics.setForegroundColor(trace.getTraceColor());
		// draw symbol
		switch (trace.getTraceType()) {
		case BAR:
			trace.drawLine(graphics, new Point(hPos + ICON_WIDTH / 2, vPos + trace.getPointSize() / 2), new Point(hPos
					+ ICON_WIDTH / 2, vPos + ICON_WIDTH));
			trace.drawPoint(graphics, new Point(hPos + ICON_WIDTH / 2, vPos + trace.getPointSize() / 2));
			break;
		case LINE_AREA:
			graphics.drawPolyline(new int[] { hPos, vPos + ICON_WIDTH / 2, hPos + ICON_WIDTH / 2,
					vPos + trace.getPointSize() / 2, hPos + ICON_WIDTH - 1, vPos + ICON_WIDTH / 2, });
		case AREA:
			graphics.setBackgroundColor(trace.getTraceColor());
			if (Preferences.useAdvancedGraphics())
				graphics.setAlpha(trace.getAreaAlpha());
			graphics.fillPolygon(new int[] { hPos, vPos + ICON_WIDTH / 2, hPos + ICON_WIDTH / 2,
					vPos + trace.getPointSize() / 2, hPos + ICON_WIDTH, vPos + ICON_WIDTH / 2, hPos + ICON_WIDTH,
					vPos + ICON_WIDTH, hPos, vPos + ICON_WIDTH });
			if (Preferences.useAdvancedGraphics())
				graphics.setAlpha(255);
			trace.drawPoint(graphics, new Point(hPos + ICON_WIDTH / 2, vPos + trace.getPointSize() / 2));
			break;
		default:
			trace.drawLine(graphics, new Point(hPos, vPos + ICON_WIDTH / 2), new Point(hPos + ICON_WIDTH, vPos
					+ ICON_WIDTH / 2));
			trace.drawPoint(graphics, new Point(hPos + ICON_WIDTH / 2, vPos + ICON_WIDTH / 2));
			break;
		}

		// draw text
		graphics.drawText(trace.getName(), hPos + ICON_WIDTH + INNER_GAP,
				vPos + ICON_WIDTH / 2 - FigureUtilities.getTextExtents(trace.getName(), getFont()).height / 2);
		graphics.popState();
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		int maxWidth = 0;
		int hEnd = INNER_GAP;
		int height = ICON_WIDTH + INNER_GAP;
		// int i=0;
		for (Trace trace : traceList) {
			hEnd = hEnd + OUT_GAP + ICON_WIDTH + INNER_GAP
					+ +FigureUtilities.getTextExtents(trace.getName(), getFont()).width;

			if (hEnd > wHint) {
				hEnd = INNER_GAP + OUT_GAP + ICON_WIDTH + INNER_GAP
						+ +FigureUtilities.getTextExtents(trace.getName(), getFont()).width;
				height += ICON_WIDTH + INNER_GAP;
			}
			if (maxWidth < hEnd)
				maxWidth = hEnd;
			// i++;
		}
		return new Dimension(maxWidth, height);
	}

	/**
	 * @return the traceList
	 */
	public List<Trace> getTraceList() {
		return traceList;
	}

}
