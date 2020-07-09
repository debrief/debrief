package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISEventListener;

public class EventFileListener extends CoreFileListener implements IDISEventListener {

	/**
	 * produce a narrative data type for this event id
	 *
	 * @param id
	 * @return
	 */
	public static String eventTypeFor(final int thisId) {
		final String res;

		switch (thisId) {
		case IDISEventListener.EVENT_COMMS:
			res = "COMMS";
			break;
		case IDISEventListener.EVENT_LAUNCH:
			res = "LAUNCH";
			break;
		case IDISEventListener.EVENT_NEW_TRACK:
			res = "NEW TRACK";
			break;
		case IDISEventListener.EVENT_TACTICS_CHANGE:
			res = "TACTICS_CHANGE";
			break;
		case IDISEventListener.EVENT_NEW_TARGET_TRACK:
			res = "NEW TARGET TRACK";
			break;
		default:
			res = "EVENT (" + thisId + ")";
			break;
		}

		return res;
	}

	public EventFileListener(final String root, final boolean toFile, final boolean toScreen,
			final LoggingFileWriter writer) {
		super(root, toFile, toScreen, "event", "time, exerciseId, entityId, entityName, eventType, eventName, message",
				writer);
	}

	@Override
	public void add(final long time, final short exerciseId, final long id, final String hisName, final int eventType,
			final String message) {
		final String eventName = eventTypeFor(eventType);

		// create the line
		final StringBuffer out = new StringBuffer();
		out.append(time);
		out.append(", ");
		out.append(exerciseId);
		out.append(", ");
		out.append(id);
		out.append(", ");
		out.append(hisName);
		out.append(", ");
		out.append(eventType);
		out.append(", ");
		out.append(eventName);
		out.append(", ");
		out.append(message);
		out.append(LINE_BREAK);

		// done, write it
		write(out.toString());
	}

}