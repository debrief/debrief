/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.lite.headless;

import java.io.DataInputStream;
import java.io.InputStream;

import org.mwc.debrief.lite.LiteLauncher;

import junit.framework.TestCase;

/**
 * The launcher that tells users on an old Java to install Java 21, instead of
 * failing with UnsupportedClassVersionError.
 */
public class LiteLauncherTest extends TestCase {

	/** the class file major version for Java 8 */
	private static final int JAVA_8_CLASS_FILE = 52;

	public void testLauncherLoadsOnJava8() throws Exception {
		// the whole point: the launcher must load on a Java that cannot load the
		// rest of Lite
		try (InputStream in = LiteLauncher.class.getResourceAsStream("LiteLauncher.class");
				DataInputStream data = new DataInputStream(in)) {
			assertEquals("magic", 0xCAFEBABE, data.readInt());
			data.readUnsignedShort(); // minor version
			assertEquals("class file version", JAVA_8_CLASS_FILE, data.readUnsignedShort());
		}
	}

	public void testReadsJavaVersions() throws Exception {
		final java.lang.reflect.Method featureVersion = LiteLauncher.class.getDeclaredMethod("featureVersion",
				String.class);
		featureVersion.setAccessible(true);
		assertEquals(8, featureVersion.invoke(null, "1.8"));
		assertEquals(11, featureVersion.invoke(null, "11"));
		assertEquals(17, featureVersion.invoke(null, "17"));
		assertEquals(21, featureVersion.invoke(null, "21"));
		assertEquals(25, featureVersion.invoke(null, "25"));
		assertEquals(-1, featureVersion.invoke(null, (Object) null));
		assertEquals(-1, featureVersion.invoke(null, "beta"));
	}
}
