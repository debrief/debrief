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

package MWC.Utilities.Errors.Testing;

import java.util.Objects;

/**
 * Assertions for test-support code that lives in production classes and is
 * shared by tests in several plugins (for example
 * {@code Editable.editableTesterSupport} and
 * {@code WatchableList.TestWatchables}). It keeps those classes free of any
 * test framework: a failed check throws {@link AssertionError}, which JUnit
 * reports as a test failure.
 *
 * <p>
 * The argument order matches JUnit Jupiter's {@code Assertions}: the message
 * comes last.
 */
public final class Checks {

	public static void assertEquals(final double expected, final double actual, final double delta,
			final String message) {
		if (Double.compare(expected, actual) != 0 && !(Math.abs(expected - actual) <= delta)) {
			fail(message + " ==> expected: <" + expected + "> but was: <" + actual + ">");
		}
	}

	public static void assertEquals(final long expected, final long actual, final String message) {
		if (expected != actual) {
			fail(message + " ==> expected: <" + expected + "> but was: <" + actual + ">");
		}
	}

	public static void assertEquals(final Object expected, final Object actual, final String message) {
		if (!Objects.equals(expected, actual)) {
			fail(message + " ==> expected: <" + expected + "> but was: <" + actual + ">");
		}
	}

	public static void assertFalse(final boolean condition, final String message) {
		if (condition) {
			fail(message);
		}
	}

	public static void assertNotNull(final Object actual) {
		assertNotNull(actual, "expected: not <null>");
	}

	public static void assertNotNull(final Object actual, final String message) {
		if (actual == null) {
			fail(message);
		}
	}

	public static void assertNotSame(final Object unexpected, final Object actual, final String message) {
		if (unexpected == actual) {
			fail(message + " ==> expected: not same but was: <" + actual + ">");
		}
	}

	public static void assertNull(final Object actual, final String message) {
		if (actual != null) {
			fail(message + " ==> expected: <null> but was: <" + actual + ">");
		}
	}

	public static void assertTrue(final boolean condition, final String message) {
		if (!condition) {
			fail(message);
		}
	}

	public static void fail(final String message) {
		throw new AssertionError(message);
	}

	private Checks() {
	}
}
