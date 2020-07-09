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
package MWC.GUI.RubberBanding;

import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import MWC.GUI.Rubberband;

class ColorChoice extends Choice {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final String colorNames[] = { "black", "blue", "cyan", "darkGray", "gray", "green", "lightgray", "magenta",
			"orange", "pink", "red", "white", "yellow" };

	private final Color colors[] = { Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green,
			Color.lightGray, Color.magenta, Color.orange, Color.pink, Color.red, Color.white, Color.yellow };

	public ColorChoice() {
		for (int i = 0; i < colors.length; ++i) {
			add(colorNames[i]);
		}
	}

	public Color getColor() {
		return colors[getSelectedIndex()];
	}

	public void setColor(final Color color) {
		for (int i = 0; i < colors.length; ++i) {
			if (colors[i].equals(color)) {
				select(i);
				break;
			}
		}
	}
}

class RubberbandTestPanel extends RubberbandPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	ColorChoice colorChoice = new ColorChoice();
	Choice rbChoice = new Choice();
	Rubberband linerb = new RubberbandLine(), ellipserb = new RubberbandEllipse(),
			rectanglerb = new RubberbandRectangle();

	public RubberbandTestPanel() {
		setRubberband(linerb);
		setForeground(Color.black);

		rbChoice.add("line");
		rbChoice.add("ellipse");
		rbChoice.add("rectangle");

		setLayout(new FlowLayout());
		add(rbChoice);
		add(colorChoice);

		rbChoice.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent event) {
				final int index = rbChoice.getSelectedIndex();
				switch (index) {
				case 0:
					setRubberband(linerb);
					break;
				case 1:
					setRubberband(ellipserb);
					break;
				case 2:
					setRubberband(rectanglerb);
					break;
				}
			}
		});
		colorChoice.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent event) {
				setForeground(colorChoice.getColor());
			}
		});
	}

	@Override
	public void rubberbandEnded(final Rubberband rb) {
		final Graphics g = getGraphics();

		if (g != null) {
			final Point anchor = rb.getAnchor(), end = rb.getEnd();
			final int w = Math.abs(anchor.x - end.x);
			final int h = Math.abs(anchor.y - end.y);

			final Rectangle rt = new Rectangle(anchor);
			rt.add(end);

			g.setColor(getForeground());

			if (rb == linerb)
				g.drawLine(anchor.x, anchor.y, end.x, end.y);
			else if (rb == ellipserb)
				g.drawOval(anchor.x, anchor.y, w, h);
			else
				g.drawRect(rt.x, rt.y, rt.width, rt.height);
			// g.drawRect(anchor.x, anchor.y, w, h);

			g.dispose();
		}
	}

	@Override
	public void update(final Graphics g) {
		paint(g);
	}
}
