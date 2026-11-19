/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package org.mwc.debrief.lite.gui.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePane.Direction;

/** the collapsible side panels' size calculations, headless */
public class JXCollapsiblePaneTest {

	@Test
	public void testStepTowardsTarget() {
		// expanding: up by the step, never past the target
		assertEquals(18, JXCollapsiblePane.step(10, 8, 100, false));
		assertEquals(100, JXCollapsiblePane.step(95, 8, 100, false));
		// contracting: down by the step, never past the target
		assertEquals(92, JXCollapsiblePane.step(100, 8, 20, true));
		assertEquals(20, JXCollapsiblePane.step(25, 8, 20, true));
	}

	@Test
	public void testClampAlpha() {
		// contracting fades from 1 (start) to 0.01 (end)
		assertEquals(0.5f, JXCollapsiblePane.clampAlpha(0.5f, 1f, 0.01f, true));
		assertEquals(0.01f, JXCollapsiblePane.clampAlpha(-0.2f, 1f, 0.01f, true));
		assertEquals(1f, JXCollapsiblePane.clampAlpha(1.3f, 1f, 0.01f, true));
		// expanding fades from 0.01 (start) to 1 (end)
		assertEquals(0.5f, JXCollapsiblePane.clampAlpha(0.5f, 0.01f, 1f, false));
		assertEquals(1f, JXCollapsiblePane.clampAlpha(1.3f, 0.01f, 1f, false));
		assertEquals(0.01f, JXCollapsiblePane.clampAlpha(0f, 0.01f, 1f, false));
	}

	@Test
	public void testDragDelta() {
		final Point from = new Point(100, 100);
		final Point to = new Point(130, 90);
		// a pane on the left grows when its title (on its right edge) moves right
		assertEquals(30, JXCollapsiblePaneWithTitle.dragDelta(Direction.LEFT, from, to));
		// a pane on the right grows when its title (on its left edge) moves left
		assertEquals(-30, JXCollapsiblePaneWithTitle.dragDelta(Direction.RIGHT, from, to));
		// a pane at the bottom grows when its title (on its top edge) moves up
		assertEquals(10, JXCollapsiblePaneWithTitle.dragDelta(Direction.DOWN, from, to));
		assertEquals(-10, JXCollapsiblePaneWithTitle.dragDelta(Direction.UP, from, to));
	}

	@Test
	public void testStackLayout() {
		final JPanel parent = new JPanel(new JXCollapsiblePane.StackLayout(true, 2));
		final JPanel a = new JPanel();
		a.setPreferredSize(new Dimension(40, 10));
		final JPanel b = new JPanel();
		b.setPreferredSize(new Dimension(60, 20));
		parent.add(a);
		parent.add(b);

		// the widest child, and both heights plus one gap
		assertEquals(new Dimension(60, 32), parent.getPreferredSize());

		parent.setSize(100, 200);
		parent.doLayout();
		// each child stretched to the width, at its own height
		assertEquals(new Rectangle(0, 0, 100, 10), a.getBounds());
		assertEquals(new Rectangle(0, 12, 100, 20), b.getBounds());
	}
}
