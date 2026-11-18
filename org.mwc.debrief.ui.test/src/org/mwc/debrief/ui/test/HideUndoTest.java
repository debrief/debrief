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
package org.mwc.debrief.ui.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Enumeration;

import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;

/**
 * Scenario 6 of docs/testing/rcp-ui-suite-plan.md: edit an item from Outline,
 * then undo it. Hides the NOGO DELTA shape, then Edit, Undo shows it again.
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class HideUndoTest {

	private static final String SHAPE = "NOGO DELTA";

	@BeforeAll
	static void prepareWorkbench() throws Exception {
		DebriefBot.prepareWorkbench();
	}

	@AfterEach
	void closeEditors() {
		DebriefBot.closeAllEditors();
	}

	@Test
	void hideThenUndo() throws Exception {
		final SWTBotEditor editor = DebriefBot.openSample("sample.dpf");
		final Plottable shape = find(DebriefBot.layers(editor), SHAPE);
		assertNotNull(shape, SHAPE + " is in sample.dpf");
		assertTrue(shape.getVisible(), SHAPE + " starts visible");

		DebriefBot.outlineLabels();
		final SWTBotView outline = DebriefBot.outline();
		SWTBotTreeItem item = null;
		for (final SWTBotTreeItem layer : outline.bot().tree().getAllItems()) {
			layer.expand();
			for (final String node : layer.getNodes()) {
				if (node.contains(SHAPE)) {
					item = layer.getNode(node);
				}
			}
		}
		assertNotNull(item, SHAPE + " is listed in Outline");
		item.select();
		outline.getToolbarButtons().stream().filter(b -> "Hide selected items".equals(b.getToolTipText()))
				.findFirst().orElseThrow().click();
		DebriefBot.waitFor(() -> !UIThreadRunnable.syncExec(shape::getVisible), SHAPE + " was not hidden");
		assertFalse(UIThreadRunnable.syncExec(shape::getVisible), SHAPE + " is hidden");

		editor.setFocus();
		DebriefBot.BOT.menu("Edit").menu("Undo Hide item").click();
		DebriefBot.waitFor(() -> UIThreadRunnable.syncExec(shape::getVisible), "undo did not show " + SHAPE);
		assertTrue(UIThreadRunnable.syncExec(shape::getVisible), "undo shows " + SHAPE + " again");
	}

	/** an item with this name, at the top level or inside a layer */
	static Plottable find(final Layers layers, final String name) {
		final Enumeration<Editable> top = layers.elements();
		while (top.hasMoreElements()) {
			final Editable e = top.nextElement();
			if (e instanceof Layer) {
				final Enumeration<Editable> inner = ((Layer) e).elements();
				while (inner.hasMoreElements()) {
					final Editable i = inner.nextElement();
					if (name.equals(i.getName()) && i instanceof Plottable) {
						return (Plottable) i;
					}
				}
			}
		}
		return null;
	}
}
