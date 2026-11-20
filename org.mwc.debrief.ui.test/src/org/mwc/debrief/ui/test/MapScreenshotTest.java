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

import java.awt.image.BufferedImage;

import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import Debrief.Testing.GoldenImage;

/**
 * Scenario 2 of docs/testing/rcp-ui-suite-plan.md: what the RCP plot editor
 * draws, compared with a reference image. This is the check for the GeoTools
 * upgrade (Phase D1) on the RCP side; Lite's MapRenderingTest is the other.
 *
 * <p>
 * The tolerances are Lite's: a pixel differs when a colour channel is off by
 * more than 48, and up to 0.2% of pixels may differ. The references are made
 * on CI's runner (see org.mwc.debrief.legacy/test_data/golden/README.md).
 */
@ExtendWith(SWTBotJunit5Extension.class)
public class MapScreenshotTest {

	private static final int CHANNEL_TOLERANCE = 48;
	private static final double MAX_DIFFERENT = 0.002;

	@BeforeAll
	static void prepareWorkbench() throws Exception {
		DebriefBot.prepareWorkbench();
	}

	@AfterEach
	void closeEditors() {
		DebriefBot.closeAllEditors();
	}

	/** sample.dpf: tracks, sensors, shapes, labels and the chart features */
	@Test
	void samplePlot() throws Exception {
		final BufferedImage image = DebriefBot.captureChart(DebriefBot.openSample("sample.dpf"));
		GoldenImage.assertMatches("rcp/sample_dpf.png", image, CHANNEL_TOLERANCE, MAX_DIFFERENT);
	}

	/** boat1.rep, imported over the ground: a single track */
	@Test
	void repTrack() throws Exception {
		DebriefBot.openSampleAsync("boat1.rep");
		DebriefBot.answerTrackMode(true, 1);
		final BufferedImage image = DebriefBot.captureChart(DebriefBot.editorAfterImport("boat1.rep"));
		GoldenImage.assertMatches("rcp/boat1_rep.png", image, CHANNEL_TOLERANCE, MAX_DIFFERENT);
	}
}
