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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Scenario 5 of docs/testing/rcp-ui-suite-plan.md: change the primary and
 * secondary tracks from Outline; the Track Tote follows. sample.dpf opens with
 * NELSON as primary, so this makes COLLINGWOOD primary and NELSON secondary.
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class ToteTest {

	@BeforeAll
	static void prepareWorkbench() throws Exception {
		DebriefBot.prepareWorkbench();
	}

	@AfterEach
	void closeEditors() {
		DebriefBot.closeAllEditors();
	}

	@Test
	void primaryAndSecondary() throws Exception {
		final SWTBotEditor editor = DebriefBot.openSample("sample.dpf");
		DebriefBot.outlineLabels();
		final SWTBotView outline = DebriefBot.outline();
		final SWTBotTreeItem collingwood = outline.bot().tree().getTreeItem("Track:COLLINGWOOD");
		collingwood.select();
		DebriefBot.clickOutlineTool("Make this item the primary ");
		final SWTBotTreeItem nelson = outline.bot().tree().getTreeItem("Track:NELSON");
		nelson.select();
		DebriefBot.clickOutlineTool("Set this item as the secondary track");

		editor.setFocus();
		final SWTBotView tote = DebriefBot.showView("org.mwc.cmap.tote.views.ToteView");
		final SWTBotTable table = tote.bot().table();
		final List<String> columns = table.columns();
		final List<String> rows = new ArrayList<>();
		for (int i = 0; i < table.rowCount(); i++) {
			rows.add(table.cell(i, 0));
		}
		System.out.println("Tote columns " + columns + ", rows " + rows);

		assertEquals(List.of("Attribute", "COLLINGWOOD", "NELSON"), columns, "primary, then secondary");
		assertEquals(List.of("Range", "Bearing", "Rel Brg", "Brg Rate", "ATB", "Speed", "Course", "Depth", "Time"),
				rows, "the tote's calculations");
	}
}
