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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Scenario 3 of docs/testing/rcp-ui-suite-plan.md: import a REP file. Opening
 * boat1.rep asks for the track mode; the track then appears in Outline.
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class ImportRepTest {

	@BeforeAll
	static void prepareWorkbench() throws Exception {
		DebriefBot.prepareWorkbench();
	}

	@AfterEach
	void closeEditors() {
		DebriefBot.closeAllEditors();
	}

	@Test
	void importOverTheGround() throws Exception {
		DebriefBot.openSampleAsync("boat1.rep");
		DebriefBot.answerTrackMode(true, 1);
		DebriefBot.BOT.editorByTitle("boat1.rep").setFocus();

		final List<String> labels = DebriefBot.outlineLabels();
		System.out.println("Outline of boat1.rep: " + labels);
		assertTrue(labels.stream().anyMatch(l -> l.startsWith("Track:")), "Outline lists the track: " + labels);
	}
}
