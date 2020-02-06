package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISDetonationListener;

public class DetonateFileListener extends CoreFileListener implements IDISDetonationListener {

	public DetonateFileListener(final String root, final boolean toFile, final boolean toScreen,
			final LoggingFileWriter writer) {
		super(root, toFile, toScreen, "detonate", "time, exerciseId, entityId, entityName, Lat, Lon, depth", writer);
	}

	@Override
	public void add(final long time, final short eid, final int hisId, final String hisName, final double dLat,
			final double dLon, final double depth) {
		// create the line
		final StringBuffer out = new StringBuffer();
		out.append(time);
		out.append(", ");
		out.append(eid);
		out.append(", ");
		out.append(hisId);
		out.append(", ");
		out.append(hisName);
		out.append(", ");
		out.append(dLat);
		out.append(", ");
		out.append(dLon);
		out.append(", ");
		out.append(depth);
		out.append(LINE_BREAK);

		// done, write it
		write(out.toString());
	}

}