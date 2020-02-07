package org.mwc.debrief.dis.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;
import org.mwc.debrief.dis.listeners.IDISDetonationListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;
import org.mwc.debrief.dis.listeners.IDISFireListener;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.listeners.IDISStartResumeListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.CollisionPdu;
import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.EntityType;
import edu.nps.moves.dis.EventReportPdu;
import edu.nps.moves.dis.FirePdu;
import edu.nps.moves.dis.OneByteChunk;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.StartResumePdu;
import edu.nps.moves.dis.StopFreezePdu;
import edu.nps.moves.dis.VariableDatum;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;
import edu.nps.moves.disutil.CoordinateConversions;

public class DISModule implements IDISModule, IDISGeneralPDUListener {
	public static long convertThisTime(final long timeStamp) {
		return timeStamp;
	}

	private final List<IDISFixListener> _fixListeners = new ArrayList<IDISFixListener>();
	private final Map<Integer, List<IDISEventListener>> _eventListeners = new HashMap<Integer, List<IDISEventListener>>();
	private final List<IDISDetonationListener> _detonationListeners = new ArrayList<IDISDetonationListener>();
	private final List<IDISGeneralPDUListener> _generalListeners = new ArrayList<IDISGeneralPDUListener>();
	private final List<IDISScenarioListener> _scenarioListeners = new ArrayList<IDISScenarioListener>();
	private final List<IDISStopListener> _stopListeners = new ArrayList<IDISStopListener>();
	private final List<IDISCollisionListener> _collisionListeners = new ArrayList<IDISCollisionListener>();
	private final List<IDISStartResumeListener> _startResumeListeners = new ArrayList<IDISStartResumeListener>();
	private boolean _newStart = false;

	private final List<IDISFireListener> _fireListeners = new ArrayList<IDISFireListener>();
	private final Map<Integer, String> _entityNames = new HashMap<Integer, String>();

	final private IDISEventListener _nameListener;

	public DISModule() {
		// SPECIAL PROCESSING - declare ourselves as the first event listener
		// so we can intercept launch events (and retrieve the name)
		_nameListener = new IDISEventListener() {

			@Override
			public void add(final long time, final short exerciseId, final long senderId, final String hisName,
					final int eventType, final String message) {
				if (eventType == IDISEventListener.EVENT_LAUNCH || eventType == IDISEventListener.EVENT_NEW_TRACK
						|| eventType == IDISEventListener.EVENT_NEW_TARGET_TRACK) {
					if (_entityNames.get(senderId) == null) {
						// ok, extract the message
						final String name = extractNameFor(message, eventType);

						// did we manage it?
						if (name != null) {
							_entityNames.put((int) senderId, name);
						}
					}
				}
			}
		};
	}

	@Override
	public void addCollisionListener(final IDISCollisionListener handler) {
		_collisionListeners.add(handler);
	}

	@Override
	public void addDetonationListener(final IDISDetonationListener handler) {
		_detonationListeners.add(handler);
	}

	@Override
	public void addEventListener(final IDISEventListener handler) {
		addEventListener(handler, null);
	}

	@Override
	public void addEventListener(final IDISEventListener handler, final Integer eType) {
		List<IDISEventListener> matches = _eventListeners.get(null);
		if (matches == null) {
			matches = new ArrayList<IDISEventListener>();
			_eventListeners.put(null, matches);
		}
		matches.add(handler);
	}

	@Override
	public void addFireListener(final IDISFireListener handler) {
		_fireListeners.add(handler);
	}

	@Override
	public void addFixListener(final IDISFixListener handler) {
		_fixListeners.add(handler);
	}

	@Override
	public void addGeneralPDUListener(final IDISGeneralPDUListener listener) {
		_generalListeners.add(listener);
	}

	@Override
	public void addScenarioListener(final IDISScenarioListener handler) {
		_scenarioListeners.add(handler);
	}

	@Override
	public void addStartResumeListener(final IDISStartResumeListener handler) {
		_startResumeListeners.add(handler);
	}

	@Override
	public void addStopListener(final IDISStopListener idisStopListener) {
		_stopListeners.add(idisStopListener);
	}

	@Override
	public void complete(final String reason) {
		// tell any scenario listeners
		final Iterator<IDISScenarioListener> sIter = _scenarioListeners.iterator();
		while (sIter.hasNext()) {
			final IDISScenarioListener thisS = sIter.next();
			thisS.complete(reason);
		}

		// also tell any general liseners
		final Iterator<IDISGeneralPDUListener> gIter = _generalListeners.iterator();
		while (gIter.hasNext()) {
			final IDISGeneralPDUListener git = gIter.next();
			git.complete(reason);
		}

		// also wipe our locally cached data (entity names)
		_entityNames.clear();
	}

	/**
	 * encapsulate timestamp conversions
	 *
	 * @param timeStamp
	 * @return
	 */
	@Override
	public long convertTime(final long timeStamp) {
		return convertThisTime(timeStamp);
	}

	protected String extractNameFor(final String message, final int eventType) {
		String res = null;

		switch (eventType) {
		case IDISEventListener.EVENT_LAUNCH: {
			// Entity 1 called SubmarineSouth has been created or launched.
			final String called = "called ";
			final String has = "has been";

			if (message.contains(called) && message.contains(has)) {
				final int nameStart = message.indexOf(called) + called.length();
				final int nameEnd = message.indexOf(has) - 1;
				res = message.substring(nameStart, nameEnd);
			}
			break;
		}
		case IDISEventListener.EVENT_NEW_TARGET_TRACK:
		case IDISEventListener.EVENT_NEW_TRACK: {
			// DETECTION E4-5
			final String detection = "DETECTION ";

			if (message.contains(detection)) {
				final int nameStart = message.indexOf(detection) + detection.length() + 1;
				res = message.substring(nameStart).trim();
			}
			break;
		}
		}

		return res;
	}

	private void handleCollision(final CollisionPdu pdu) {
		final short eid = pdu.getExerciseID();
		final long time = convertTime(pdu.getTimestamp());
		final int receipientId = pdu.getIssuingEntityID().getEntity();
		final int movingId = pdu.getCollidingEntityID().getEntity();

		// sort out the location
		final Vector3Float loc = pdu.getLocation();
		final double[] locArr = new double[] { loc.getX(), loc.getY(), loc.getZ() };
		final double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);

		// sort out his name
		String movingName = nameFor(movingId);
		String recipientName = nameFor(receipientId);

		// special cases - if we have an ID of -1 it's the environment
		final String envTarget = "Environment";
		if (movingId == -1) {
			movingName = envTarget;
		}
		if (receipientId == -1) {
			recipientName = envTarget;
		}

		final Iterator<IDISCollisionListener> dIter = _collisionListeners.iterator();
		while (dIter.hasNext()) {
			final IDISCollisionListener thisD = dIter.next();
			thisD.add(time, eid, movingId, movingName, receipientId, recipientName, worldCoords[0], worldCoords[1],
					-worldCoords[2]);
		}
	}

	private void handleDetonation(final DetonationPdu pdu) {
		final short eid = pdu.getExerciseID();

		// we get two sets of coordinates in a detonation. Track both sets
		final Vector3Float eLoc = pdu.getLocationInEntityCoordinates();
		final double[] locArr = new double[] { eLoc.getX(), eLoc.getY(), eLoc.getZ() };
		@SuppressWarnings("unused")
		final double[] eWorldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);

		final Vector3Double wLoc = pdu.getLocationInWorldCoordinates();
		final double[] worldArr = new double[] { wLoc.getX(), wLoc.getY(), wLoc.getZ() };
		final double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(worldArr);

		final long time = pdu.getTimestamp();
		final int hisId = pdu.getFiringEntityID().getEntity();

		final double[] coordsToUse = worldCoords;

		// sort out his name
		final String hisName = nameFor(hisId);

		final Iterator<IDISDetonationListener> dIter = _detonationListeners.iterator();
		while (dIter.hasNext()) {
			final IDISDetonationListener thisD = dIter.next();
			thisD.add(time, eid, hisId, hisName, coordsToUse[0], coordsToUse[1], -coordsToUse[2]);
		}

	}

	private void handleEvent(final EventReportPdu pdu) {
		final short eid = pdu.getExerciseID();
		final long time = convertTime(pdu.getTimestamp());
		final int originator = pdu.getOriginatingEntityID().getEntity();
		final int eType = (int) pdu.getEventType();
		String msg = "";

		// try to get the data
		final List<VariableDatum> items = pdu.getVariableDatums();
		for (int i = 0; i < items.size(); i++) {
			final VariableDatum val = items.get(i);
			final List<OneByteChunk> chunks = val.getVariableData();
			final int thisLen = (int) val.getVariableDatumLength();
			final byte[] bytes = new byte[thisLen];
			final Iterator<OneByteChunk> iter = chunks.iterator();
			int ctr = 0;
			for (int l = 0; l < thisLen; l++) {
				final OneByteChunk thisB = iter.next();
				final byte thisByte = thisB.getOtherParameters()[0];

				if (thisByte > 10) {
					bytes[ctr++] = thisByte;
				} else if (thisByte == 1 || thisByte == 9) {
					bytes[ctr++] = 32;
				}
			}
			final String newS = new String(bytes).trim();

			msg += newS;
		}

		if (msg.length() == 0) {
			msg = "Unset";
		}

		// sort out his name
		_nameListener.add(time, eid, originator, null, eType, msg);

		// now try to retrieve name
		final String hisName = nameFor(originator);

		// first send out to specific listeners
		final List<IDISEventListener> specificListeners = _eventListeners.get(eType);
		if (specificListeners != null) {
			final Iterator<IDISEventListener> eIter = specificListeners.iterator();
			while (eIter.hasNext()) {
				final IDISEventListener thisE = eIter.next();
				thisE.add(time, eid, originator, hisName, eType, msg);
			}
		}

		// and now to general listeners
		final List<IDISEventListener> generalListeners = _eventListeners.get(null);
		if (generalListeners != null) {
			final Iterator<IDISEventListener> eIter = generalListeners.iterator();
			while (eIter.hasNext()) {
				final IDISEventListener thisE = eIter.next();
				thisE.add(time, eid, originator, hisName, eType, msg);
			}
		}

	}

	private void handleFire(final FirePdu pdu) {
		final short eid = pdu.getExerciseID();
		final Vector3Double wLoc = pdu.getLocationInWorldCoordinates();
		final long time = convertTime(pdu.getTimestamp());
		final int hisId = pdu.getFiringEntityID().getEntity();
		final int tgtId = pdu.getTargetEntityID().getEntity();

		// sort out his name
		final String hisName = nameFor(hisId);
		final String tgtName = nameFor(tgtId);

		final Iterator<IDISFireListener> dIter = _fireListeners.iterator();
		while (dIter.hasNext()) {
			final IDISFireListener thisD = dIter.next();
			thisD.add(time, eid, hisId, hisName, tgtId, tgtName, wLoc.getY(), wLoc.getX(), wLoc.getZ());
		}
	}

	private void handleFix(final EntityStatePdu pdu) {
		// unpack the data
		final short eid = pdu.getExerciseID();
		final short force = pdu.getForceId();
		final long hisId = pdu.getEntityID().getEntity();

		final boolean isOSAT = pdu.getEntityType().getEntityKind() == IDISFixListener.OSAT_TRACK;

		final long time = convertTime(pdu.getTimestamp());
		final Vector3Double loc = pdu.getEntityLocation();
		final double[] locArr = new double[] { loc.getX(), loc.getY(), loc.getZ() };
		final double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);
		final Orientation orientation = pdu.getEntityOrientation();
		final Vector3Float velocity = pdu.getEntityLinearVelocity();

		final double speedMs = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY());

		// entity state
		final String hisName = nameFor(hisId);

		final EntityType cat = pdu.getEntityType();
		final short kind = cat.getEntityKind();
		final short domain = cat.getDomain();
		final short category = cat.getCategory();

		final Iterator<IDISFixListener> fIter = _fixListeners.iterator();
		while (fIter.hasNext()) {
			final IDISFixListener thisF = fIter.next();
			thisF.add(time, eid, hisId, hisName, force, kind, domain, category, isOSAT, worldCoords[0], worldCoords[1],
					-worldCoords[2], orientation.getPsi(), speedMs, pdu.getEntityAppearance_damage());
		}
	}

	private void handleStart(final StartResumePdu pdu) {
		final short eid = pdu.getExerciseID();
		final long time = convertTime(pdu.getTimestamp());
		final long replicationId = pdu.getRequestID();

		final Iterator<IDISStartResumeListener> dIter = _startResumeListeners.iterator();
		while (dIter.hasNext()) {
			final IDISStartResumeListener thisD = dIter.next();
			thisD.add(time, eid, replicationId);
		}
	}

	private void handleStop(final StopFreezePdu pdu) {
		final long time = convertTime(pdu.getTimestamp());
		final short eid = pdu.getExerciseID();
		final short reason = pdu.getReason();
		final int appId = pdu.getOriginatingEntityID().getApplication();
		final long numRuns = pdu.getRequestID();

		final Iterator<IDISStopListener> dIter = _stopListeners.iterator();
		while (dIter.hasNext()) {
			final IDISStopListener thisD = dIter.next();
			thisD.stop(time, appId, eid, reason, numRuns);
		}

		// share the complete message
		if (reason == IDISStopListener.PDU_STOP || reason == IDISStopListener.PDU_ITERATION_COMPLETE) {
			complete("Scenario complete");
		}
	}

	@Override
	public void logPDU(final Pdu data) {
		// is this new?
		if (_newStart) {
			// share the good news
			final Iterator<IDISScenarioListener> sIter = _scenarioListeners.iterator();
			while (sIter.hasNext()) {
				final IDISScenarioListener sl = sIter.next();
				sl.restart();
			}
			_newStart = false;
		}

		// give it to any general listenrs
		final Iterator<IDISGeneralPDUListener> gIter = _generalListeners.iterator();
		while (gIter.hasNext()) {
			final IDISGeneralPDUListener gPdu = gIter.next();
			gPdu.logPDU(data);
		}

		// whether to track all messages, to learn about what is being sent
		// if(data.getPduType() == 1)
		// {
		// EntityStatePdu esp = (EntityStatePdu) data;
		// System.out.println(new java.util.Date(convertTime(esp.getTimestamp())));
		// }

		// and now the specific listeners
		final short type = data.getPduType();
		switch (type) {
		case 1: {
			handleFix((EntityStatePdu) data);
			break;
		}
		case 2: {
			handleFire((FirePdu) data);
			break;
		}
		case 3: {
			handleDetonation((DetonationPdu) data);
			break;
		}
		case 4: {
			handleCollision((CollisionPdu) data);
			break;
		}
		case 13: {
			handleStart((StartResumePdu) data);
			break;
		}
		case 21: {
			handleEvent((EventReportPdu) data);
			break;
		}
		case 14: {
			handleStop((StopFreezePdu) data);
			break;
		}
		default:
			System.err.println("PDU type not handled:" + type);
		}
	}

	/**
	 * retrieve the name for this entity id, or generate a default one
	 *
	 * @param id the id we're looking against
	 * @return its name (or a generated one)
	 */
	private String nameFor(final long id) {
		String name = _entityNames.get((int) id);
		if (name == null) {
			name = "DIS_" + id;
		}

		return name;
	}

	@Override
	public void setProvider(final IPDUProvider provider) {
		// remember we're restarting
		_newStart = true;

		// register as a listener, to hear about new data
		provider.addListener(this);
	}
}
