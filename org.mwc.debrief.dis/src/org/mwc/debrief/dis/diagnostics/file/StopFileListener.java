package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISStopListener;

public class StopFileListener extends CoreFileListener implements IDISStopListener {

	public StopFileListener(final String root, final boolean toFile, final boolean toScreen,
			final LoggingFileWriter writer) {
		super(root, toFile, toScreen, "stop", "time, exerciseId, reason, numRuns", writer);
	}

	@Override
	public void stop(final long time, final int appId, final short eid, final short reason, final long numRuns) {
		// create the line
		final StringBuffer out = new StringBuffer();
		out.append(time);
		out.append(", ");
		out.append(eid);
		out.append(", ");
		out.append(reason);
		out.append(", ");
		out.append(numRuns);
		out.append(LINE_BREAK);

		// done, write it
		write(out.toString());
	}

}