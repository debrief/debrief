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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Scenario 1 of docs/testing/rcp-ui-suite-plan.md: open sample.dpf in the plot
 * editor and check what the Outline view lists.
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class OpenPlotTest {

	@BeforeAll
	static void prepareWorkbench() throws Exception {
		DebriefBot.prepareWorkbench();
	}

	@AfterEach
	void closeEditors() {
		DebriefBot.closeAllEditors();
	}

	@Test
	void openSamplePlot() throws Exception {
		final SWTBotEditor editor = DebriefBot.openSample("sample.dpf");
		assertEquals("org.mwc.debrief.PlotEditor", editor.getReference().getId(), "opened in the plot editor");

		final SWTBotView outline = DebriefBot.outline();
		final List<String> labels = new ArrayList<>();
		for (final SWTBotTreeItem item : outline.bot().tree().getAllItems()) {
			labels.add(item.getText());
		}
		System.out.println("Outline of sample.dpf: " + labels);

		assertEquals(List.of("Annotations (0 items)", "Track:COLLINGWOOD", "Chart Features (3 items)",
				"Misc (5 items)", "Track:NELSON", "Narratives (19 items)"), labels, "the layers of sample.dpf");
	}
}
