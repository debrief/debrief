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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Scenario 4 of docs/testing/rcp-ui-suite-plan.md: step through time with the
 * Time Controller's buttons, and check the time it shows.
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class TimeStepTest {

	static final String VIEW_ID = "org.mwc.cmap.TimeController.views.TimeController";

	@BeforeAll
	static void prepareWorkbench() throws Exception {
		DebriefBot.prepareWorkbench();
	}

	@AfterEach
	void closeEditors() {
		DebriefBot.closeAllEditors();
	}

	@Test
	void stepForwardAndBack() throws Exception {
		final SWTBotEditor editor = DebriefBot.openSample("sample.dpf");
		final SWTBotView view = DebriefBot.showView(VIEW_ID);
		editor.setFocus();
		view.show();

		view.bot().buttonWithTooltip("Move to start of dataset").click();
		final String start = timeShown(view);
		view.bot().buttonWithTooltip("Move forward small step (hold to repeat)").click();
		final String next = timeShown(view);
		view.bot().buttonWithTooltip("Move backward small step (hold to repeat)").click();
		final String back = timeShown(view);
		view.bot().buttonWithTooltip("Move to end of dataset").click();
		final String end = timeShown(view);
		System.out.println("Time Controller: start " + start + ", next " + next + ", back " + back + ", end " + end);

		assertEquals("95/12/12 05:00", start, "the start of sample.dpf");
		assertEquals("95/12/12 05:01", next, "a small step forward is a minute");
		assertEquals(start, back, "a small step back returns to the start");
		assertEquals("95/12/12 11:45", end, "the end of sample.dpf");
	}

	/** the time display: the Time Controller's only label */
	static String timeShown(final SWTBotView view) {
		return view.bot().label(0).getText();
	}
}
