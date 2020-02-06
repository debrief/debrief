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
package Debrief.Wrappers.DynamicTrackShapes;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 * A simple demo that draws a full donut and a section of a donut on two
 * canvasses.
 */
public class PlotDonutFrame extends Frame {

	/**
	 * A simple canvas that draws a single area with a border color and fill. The
	 * origin is the center of the canvas.
	 */
	private static class AreaCanvas extends Canvas {
		/**
			 *
			 */
		private static final long serialVersionUID = 1L;
		Area area;
		Color areaBorderColor = Color.decode("#95b770");
		Paint areaFillPaint = Color.decode("#cce9ad");

//    public Color getAreaBorderColor() { return this.areaBorderColor; }
//    public void setAreaBorerColor(Color c) { this.areaBorderColor = c; }
//    public Paint getAreaFillPaint() { return this.areaFillPaint; }
//    public void setAreaFillPaint(Paint p) { this.areaFillPaint = p; }
		@Override
		public void paint(final Graphics g) {
			super.paint(g);
			final Graphics2D g2 = (Graphics2D) g;

			// translate origin to center
			g2.translate(this.getWidth() / 2, this.getHeight() / 2);

			g2.setPaint(this.areaFillPaint);
			g2.fill(area);
			g2.setColor(this.areaBorderColor);
			g2.draw(area);
		}

//    public Area getArea() { return this.area; }
		public void setArea(final Area a) {
			this.area = a;
		}
	}

	/**
		 *
		 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] ignore) {
		final PlotDonutFrame frame = new PlotDonutFrame("Plot Donut", makeDonutSectionArea(50, 100, 0, 360),
				makeDonutSectionArea(50, 100, -45, 45));
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				frame.setVisible(false);
				System.exit(0);
			}
		});
		frame.pack();
		frame.setLocationRelativeTo(null);// center on screen
		frame.setVisible(true);
	}

	/**
	 * Create a donut or donut section. A full donut results if the start and end
	 * mod 360 are within 0.1 degree, so don't call this if the difference between
	 * minAngle and maxAngle should be 0.
	 *
	 * @param innerRadius of donut section
	 * @param outerRadius of donut section
	 * @param minAngle    compass angle of section start
	 * @param maxAngle    compass angle of section end
	 */
	public static Area makeDonutSectionArea(final double innerRadius, final double outerRadius, final double minAngle,
			final double maxAngle) {
		final double dO = 2 * outerRadius, dI = 2 * innerRadius;
		// Angles: From degrees clockwise from the positive y axis,
		// convert to degress counter-clockwise from positive x axis.
		final double aBeg = 90 - maxAngle, aExt = maxAngle - minAngle;
		// X and y are upper left corner of bounding rectangle of full circle.
		// Subtract 0.5 so that center is between pixels and drawn width is dO
		// (rather than dO + 1).
		final double xO = -dO / 2 - 0.5, yO = -dO / 2 - .5;
		final double xI = -dI / 2 - 0.5, yI = -dI / 2 - .5;
		if (Math.abs(minAngle % 360 - maxAngle % 360) < 0.1) {
			final Area outer = new Area(new Ellipse2D.Double(xO, yO, dO, dO));
			final Area inner = new Area(new Ellipse2D.Double(xI, yI, dI, dI));
			outer.subtract(inner);
			return outer;
		} else {
			final Area outer = new Area(new Arc2D.Double(xO, yO, dO, dO, aBeg, aExt, Arc2D.PIE));
			final Area inner = new Area(new Arc2D.Double(xI, yI, dI, dI, aBeg, aExt, Arc2D.PIE));
			outer.subtract(inner);
			return outer;
		}
	}

	/**
	 * Create a PlotDonutFrame with the given title and areas to be displayed. The
	 * areas may be created by {@link #makeDonutSectionArea}. Each area is drawn in
	 * a separate canvas. The layout is FlowLayout.
	 */
	PlotDonutFrame(final String title, final Area... areas) {
		super(title);
		setLayout(new FlowLayout());
		for (final Area area : areas) {
			final AreaCanvas canvas = new AreaCanvas();
			canvas.setArea(area); // what to draw
			canvas.setPreferredSize(new Dimension(200, 200));
			add(canvas);
		}
	}

}