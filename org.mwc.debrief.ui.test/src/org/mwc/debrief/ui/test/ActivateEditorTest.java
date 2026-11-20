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

import org.eclipse.core.resources.IFile;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mwc.debrief.core.Startup;

/**
 * A plot that is open but not the active part (as after a restart that
 * restores the workbench with a view active) leaves the Time Controller
 * blank. Debrief's startup activates the current editor, so the Time
 * Controller attaches to the plot.
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class ActivateEditorTest {

	@BeforeAll
	static void prepareWorkbench() throws Exception {
		DebriefBot.prepareWorkbench();
	}

	@AfterEach
	void closeEditors() {
		DebriefBot.closeAllEditors();
	}

	@Test
	void startupActivatesThePlot() throws Exception {
		final SWTBotView time = DebriefBot.showView(TimeStepTest.VIEW_ID);
		final IFile file = DebriefBot.sampleFile("sample.dpf");
		// open the plot behind the Time Controller, without activating it
		UIThreadRunnable.syncExec(() -> {
			try {
				IDE.openEditor(DebriefBot.window().getActivePage(), file, false);
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		});
		DebriefBot.settle();
		assertTrue(TimeStepTest.timeShown(time).startsWith("-----"), "the Time Controller is not attached yet: " + TimeStepTest.timeShown(time));

		UIThreadRunnable.syncExec(() -> {
			final IWorkbenchPage page = DebriefBot.window().getActivePage();
			Startup.activateCurrentEditor(page);
		});
		DebriefBot.waitFor(() -> "95/12/12 05:00".equals(TimeStepTest.timeShown(time)),
				"the Time Controller did not attach to the plot");
	}
}
