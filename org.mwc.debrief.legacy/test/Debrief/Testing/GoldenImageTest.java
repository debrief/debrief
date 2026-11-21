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

package Debrief.Testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

public class GoldenImageTest {

	@Test
	public void testMacOSUsesOwnReferences() {
		final File linux = new File(new File(GoldenFile.dir(), "rendering"), "sample.png");
		final File expected;
		if (System.getProperty("os.name", "").toLowerCase().startsWith("mac")) {
			expected = new File(new File(linux.getParentFile(), GoldenImage.MACOS_DIR), "sample.png");
		} else {
			expected = linux;
		}
		assertEquals(expected, GoldenImage.goldenFile("rendering/sample.png"), "reference for this OS");
	}
}
