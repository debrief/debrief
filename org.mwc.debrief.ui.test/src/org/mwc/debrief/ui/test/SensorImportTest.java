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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Enumeration;
import java.util.List;

import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;

/**
 * Scenario 7 of docs/testing/rcp-ui-suite-plan.md: open sen_tracks.rep and drop
 * sen_frig_sensor.dsf on it, as dragging it from Navigator does; the frigate's
 * track gains the sensor and its cuts.
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class SensorImportTest {

	@BeforeAll
	static void prepareWorkbench() throws Exception {
		DebriefBot.prepareWorkbench();
	}

	@AfterEach
	void closeEditors() {
		DebriefBot.closeAllEditors();
	}

	@Test
	void importSensorData() throws Exception {
		DebriefBot.openSampleAsync("sen_tracks.rep");
		DebriefBot.answerTrackMode(true, 2);
		final SWTBotEditor editor = DebriefBot.BOT.editorByTitle("sen_tracks.rep");
		assertEquals(List.of("Track:Frigate", "Track:New_SSK"), DebriefBot.outlineLabels(), "the tracks of sen_tracks.rep");
		final TrackWrapper frigate = (TrackWrapper) DebriefBot.layers(editor).findLayer("Frigate");
		assertEquals(0, sensorCount(frigate), "the frigate starts with no sensors");

		final String workbench = DebriefBot.BOT.activeShell().getText();
		DebriefBot.dropOnPlot(editor, "sen_frig_sensor.dsf");

		// the import may ask about the sensor in a wizard (it does not when run
		// here); finish it if it appears
		DebriefBot.waitFor(() -> sensorCount(frigate) > 0 || wizardOpen(workbench), "the sensor data was not imported");
		if (wizardOpen(workbench)) {
			final SWTBotShell wizard = DebriefBot.BOT.activeShell();
			wizard.bot().button("Finish").click();
			DebriefBot.waitFor(() -> sensorCount(frigate) > 0, "the sensor data was not imported");
		}

		// the file holds cuts for five sensors (Frigate_Optic, Frigate_Optic2, ...)
		assertEquals(5, sensorCount(frigate), "the frigate has the five imported sensors");
		final List<String> names = UIThreadRunnable.syncExec(() -> {
			final List<String> res = new java.util.ArrayList<>();
			final Enumeration<Editable> e = frigate.getSensors().elements();
			while (e.hasMoreElements()) {
				res.add(e.nextElement().getName());
			}
			return res;
		});
		assertTrue(names.contains("Frigate_Optic"), "Frigate_Optic is among the sensors: " + names);
	}

	/** a dialog (the import wizard) has taken over from the workbench window */
	private static boolean wizardOpen(final String workbenchTitle) {
		return !workbenchTitle.equals(DebriefBot.BOT.activeShell().getText());
	}

	private static int sensorCount(final TrackWrapper track) {
		return UIThreadRunnable.syncExec(() -> count(track.getSensors().elements()));
	}

	private static int count(final Enumeration<Editable> items) {
		int n = 0;
		while (items.hasMoreElements()) {
			items.nextElement();
			n++;
		}
		return n;
	}
}
