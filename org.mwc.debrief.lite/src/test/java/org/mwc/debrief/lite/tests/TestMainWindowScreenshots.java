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

package org.mwc.debrief.lite.tests;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import Debrief.Testing.GoldenImage;

/**
 * Screenshot tests of Debrief Lite's main window, for the Lite UI library
 * upgrade (plan Phase D2: Radiance flamingo, substance and trident, and
 * swingx). The window is set to a fixed size and painted into an image, which
 * is compared with a reference PNG in
 * {@code org.mwc.debrief.legacy/test_data/golden/lite-window/}.
 *
 * <p>
 * Like the other Lite UI tests, these need a display (CI runs them under xvfb
 * with -DliteUiTests=true). The references were made on CI's Linux runner, since
 * fonts and the look and feel's rendering differ between machines. Regenerate
 * them as described in {@code org.mwc.debrief.legacy/test_data/golden/README.md}.
 */
public class TestMainWindowScreenshots extends BaseTestCase {

	/** fixed window size: wide enough for the whole ribbon */
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 800;

	/** largest per-channel colour difference that still counts as the same pixel */
	private static final int CHANNEL_TOLERANCE = 48;

	/** largest share of different pixels that still passes: 0.5% */
	private static final double MAX_DIFFERENT = 0.005;

	/** time for the map pane's background rendering and any animations to finish */
	private static final long SETTLE_MILLIS = 3000;

	private static JRibbonFrame frame() {
		return DebriefLiteApp.getInstance().getApplicationFrame();
	}

	/** size the window, select a ribbon tab, let it settle, then paint it */
	private static BufferedImage capture(final int taskIndex) throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			final JRibbonFrame frame = frame();
			frame.setSize(WIDTH, HEIGHT);
			final JRibbon ribbon = frame.getRibbon();
			ribbon.setSelectedTask(ribbon.getTask(taskIndex));
			frame.validate();
		});
		Thread.sleep(SETTLE_MILLIS);
		final AtomicReference<BufferedImage> res = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			final JRootPane root = frame().getRootPane();
			final BufferedImage image = new BufferedImage(root.getWidth(), root.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			final Graphics2D g = image.createGraphics();
			try {
				root.paint(g);
			} finally {
				g.dispose();
			}
			res.set(image);
		});
		return res.get();
	}

	private static void check(final String name, final BufferedImage image) throws Exception {
		GoldenImage.assertMatches("lite-window/" + name + ".png", image, CHANNEL_TOLERANCE, MAX_DIFFERENT);
	}

	/**
	 * one test, in a fixed order: Lite keeps state (status bar, selection) between
	 * tests in the same JVM, and JUnit does not fix the order of test methods
	 */
	public void testMainWindow() throws Exception {
		// the window as Lite starts: Lite tab, empty plot over the coastline
		check("startup_lite_tab", capture(0));

		// each of the other ribbon tabs: the buttons, icons and bands Radiance draws
		final String[] names = { "file", "view", "insert", "time" };
		for (int i = 0; i < names.length; i++) {
			assertEquals(names[i], frame().getRibbon().getTask(i + 1).getTitle().toLowerCase());
			check("tab_" + names[i], capture(i + 1));
		}

		// boat1.rep loaded: the outline tree, the time controls and the plot
		final File rep = new File("../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep");
		SwingUtilities.invokeAndWait(() -> DebriefLiteApp.openRepFile(rep));
		check("boat1_rep_time_tab", capture(4));
	}
}
