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

package MWC.Utilities.ReaderWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import MWC.GUI.MessageProvider;
import MWC.GUI.Dialogs.DialogFactory;
import junit.framework.TestCase;

/**
 * Collects the problems found while importing a file (malformed lines or
 * attributes that had to be skipped), so the import can carry on with the rest
 * of the data and the analyst is shown a summary at the end, rather than data
 * silently going missing.
 */
public class ImportProblems {

	public static class ImportProblemsTest extends TestCase {
		public void testSummary() {
			final ImportProblems problems = new ImportProblems();
			assertTrue(problems.isEmpty());
			assertNull(problems.summary("file.rep"));
			for (int i = 1; i <= MAX_LISTED + 5; i++) {
				problems.add(i, "bad value", "line " + i);
			}
			problems.add("no line number");
			assertEquals(MAX_LISTED + 6, problems.size());
			final String summary = problems.summary("file.rep");
			assertTrue(summary, summary.startsWith(MAX_LISTED + 6 + " problems found reading file.rep"));
			assertTrue(summary, summary.contains("Line 1: bad value [line 1]"));
			assertTrue(summary, summary.contains("...and 6 more"));
			assertFalse(summary, summary.contains("no line number"));
		}
	}

	/**
	 * how many problems we list in the message shown to the analyst (they're all
	 * written to the log)
	 */
	public static final int MAX_LISTED = 20;

	/**
	 * don't let a badly broken file eat all the memory
	 */
	private static final int MAX_STORED = 10000;

	private final List<String> _problems = new ArrayList<String>();

	private int _count = 0;

	/**
	 * record a problem at a specific line
	 *
	 * @param lineNumber the line number (1-based), or less than 1 if not known
	 * @param reason     what was wrong
	 * @param content    the offending text (optional)
	 */
	public void add(final int lineNumber, final String reason, final String content) {
		final StringBuilder sb = new StringBuilder();
		if (lineNumber > 0) {
			sb.append("Line ").append(lineNumber).append(": ");
		}
		sb.append(reason);
		if (content != null) {
			sb.append(" [").append(content.length() > 120 ? content.substring(0, 120) + "..." : content).append("]");
		}
		add(sb.toString());
	}

	/**
	 * record a problem
	 */
	public void add(final String problem) {
		_count++;
		if (_problems.size() < MAX_STORED) {
			_problems.add(problem);
		}
	}

	public void clear() {
		_problems.clear();
		_count = 0;
	}

	public List<String> getProblems() {
		return Collections.unmodifiableList(_problems);
	}

	public boolean isEmpty() {
		return _count == 0;
	}

	/**
	 * if there were any problems, write them all to the log and show the analyst
	 * a summary
	 *
	 * @param title  title for the message
	 * @param source name of the file (or other source) being imported
	 */
	public void report(final String title, final String source) {
		if (isEmpty()) {
			return;
		}
		// everything goes in the log
		final StringBuilder all = new StringBuilder();
		all.append(_count).append(" problems found reading ").append(source).append(":");
		for (final String problem : _problems) {
			all.append("\n").append(problem);
		}
		MWC.Utilities.Errors.Trace.trace(all.toString(), false);

		// and the analyst sees the summary
		try {
			DialogFactory.showMessage(title, summary(source), MessageProvider.WARNING);
		} catch (final RuntimeException e) {
			// e.g. no display available. Don't let it break the import, it's logged
			MWC.Utilities.Errors.Trace.trace(e, "Unable to show import problems");
		}
	}

	public int size() {
		return _count;
	}

	/**
	 * @return summary suitable for showing to the analyst, or null if there were no
	 *         problems
	 */
	public String summary(final String source) {
		if (isEmpty()) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(_count).append(" problems found reading ").append(source)
				.append(". The rest of the data was loaded; these items were skipped or left at their default value:");
		final int listed = Math.min(MAX_LISTED, _problems.size());
		for (int i = 0; i < listed; i++) {
			sb.append("\n").append(_problems.get(i));
		}
		if (_count > listed) {
			sb.append("\n...and ").append(_count - listed).append(" more (see the log)");
		}
		return sb.toString();
	}
}
