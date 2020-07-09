package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISStartResumeListener;

public class StartFileListener extends CoreFileListener implements IDISStartResumeListener {

	public StartFileListener(final String root, final boolean toFile, final boolean toScreen,
			final LoggingFileWriter writer) {
		super(root, toFile, toScreen, "start resume", "time, exerciseId, replication", writer);
	}

	@Override
	public void add(final long time, final short eid, final long replication) {
		// create the line
		final StringBuffer out = new StringBuffer();
		out.append(time);
		out.append(", ");
		out.append(eid);
		out.append(", ");
		out.append(replication);
		out.append(LINE_BREAK);

		// done, write it
		write(out.toString());
	}

}