package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISFireListener;

public class FireFileListener extends CoreFileListener implements IDISFireListener {

	public FireFileListener(final String root, final boolean toFile, final boolean toScreen,
			final LoggingFileWriter writer) {
		super(root, toFile, toScreen, "fire", "time, exerciseId,  entityId, entityName, tgtId, tgtName, x, y, z",
				writer);
	}

	@Override
	public void add(final long time, final short eid, final int hisId, final String hisName, final int tgtId,
			final String tgtName, final double dLat, final double dLon, final double depth) {
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
		out.append(tgtId);
		out.append(", ");
		out.append(tgtName);
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