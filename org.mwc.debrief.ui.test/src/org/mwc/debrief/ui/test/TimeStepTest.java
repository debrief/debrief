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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
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

		press(view.bot().buttonWithTooltip("Move to start of dataset"));
		final String start = timeShown(view);
		press(view.bot().buttonWithTooltip("Move forward small step (hold to repeat)"));
		final String next = timeShown(view);
		press(view.bot().buttonWithTooltip("Move backward small step (hold to repeat)"));
		final String back = timeShown(view);
		press(view.bot().buttonWithTooltip("Move to end of dataset"));
		final String end = timeShown(view);
		System.out.println("Time Controller: start " + start + ", next " + next + ", back " + back + ", end " + end);

		assertEquals("95/12/12 05:00", start, "the start of sample.dpf");
		assertEquals("95/12/12 05:01", next, "a small step forward is a minute");
		assertEquals(start, back, "a small step back returns to the start");
		assertEquals("95/12/12 11:45", end, "the end of sample.dpf");
	}

	/**
	 * press and release a time button at once, on the UI thread. The step
	 * buttons repeat while held (every 300 ms after mouse down), and SWTBot's
	 * click() sends mouse down and up as separate events, so a slow machine
	 * would see a hold and step twice.
	 */
	static void press(final SWTBotButton button) {
		final Button widget = button.widget;
		UIThreadRunnable.syncExec(() -> {
			final Event down = new Event();
			down.button = 1;
			widget.notifyListeners(SWT.MouseDown, down);
			final Event up = new Event();
			up.button = 1;
			widget.notifyListeners(SWT.MouseUp, up);
		});
	}

	/** the time display: the Time Controller's only label */
	static String timeShown(final SWTBotView view) {
		return view.bot().label(0).getText();
	}
}
