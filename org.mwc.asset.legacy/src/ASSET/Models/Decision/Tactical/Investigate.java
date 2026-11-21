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

package ASSET.Models.Decision.Tactical;

import java.util.HashMap;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Tactical.Investigate.InvestigatePatternFactory.InvestigatePattern;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * Our implementation of investigation. The host platform will close on a
 * candidate target until the required classification level is achieved, after
 * which it will break off.
 */

public class Investigate extends CoreDecision implements java.io.Serializable {
	static public class InvestigateInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public InvestigateInfo(final Investigate data) {
			super(data, data.getName(), "Investigate", true);
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("TargetType", "the type of vessel this model is evading"),
						prop("Name", "the name of this detonation model"),
						prop("DetectionLevel", "the name of this detonation model"), };
				res[2].setPropertyEditorClass(DetectionEvent.DetectionStatePropertyEditor.class);
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	static public class InvestigatePatternFactory {
		static public interface InvestigatePattern {

			SimpleDemandedStatus amendDemStatus(SimpleDemandedStatus res, Status status, Status tgtStat);

			boolean invComplete(DetectionEvent validDetection, int detectionLevel);

			void newTarget(int target);

		}

		static public class MatchDetectionLevel implements InvestigatePattern {

			@Override
			public SimpleDemandedStatus amendDemStatus(final SimpleDemandedStatus res, final Status status,
					final Status tgtStat) {
				// // are we close?
				// WorldVector dist =
				// tgtStat.getLocation().subtract(status.getLocation());
				// WorldDistance dd = new WorldDistance(dist.getRange(),
				// WorldDistance.DEGS);
				// if(dd.lessThan(new WorldDistance(2.5, WorldDistance.NM)))
				// {
				// // ok, match him for speed
				// res.setSpeed(tgtStat.getSpeed());
				// }
				//
				return res;
			}

			@Override
			public boolean invComplete(final DetectionEvent validDetection, final int detectionLevel) {
				// ok. have we reached the correct detection level?
				final int detState = validDetection.getDetectionState();
				final boolean res = (detState >= detectionLevel);
				return res;
			}

			@Override
			public void newTarget(final int target) {
			}

		}

		static InvestigatePattern getPatternFor(final String name) {
			final InvestigatePattern res = new MatchDetectionLevel();
			return res;
		}

	}

	/**
	 * convenience class for the per-scenario data storage
	 *
	 * @author ianmayo
	 *
	 */
	public static class InvestigateStore {
		static protected class MappedStores {
			private final HashMap<Integer, InvestigateStore> _myStore = new HashMap<Integer, InvestigateStore>();

			/**
			 * clear the list of stores
			 *
			 */
			public void clearStores() {
				_myStore.clear();
			}

			public InvestigateStore firstStore() {
				InvestigateStore res = null;
				if (_myStore.size() > 0)
					res = _myStore.values().iterator().next();
				return res;
			}

			/**
			 * find the right store for this type
			 *
			 * @param monitor
			 * @param id
			 * @param isCollab
			 * @return
			 */
			public InvestigateStore get(final ScenarioActivityMonitor monitor, final int id, final boolean isCollab) {
				InvestigateStore res = null;
				Integer index;
				if (isCollab)
					index = monitor.hashCode();
				else
					index = monitor.hashCode() * id;

				if (_myStore.get(index) != null) {
					res = _myStore.get(index);
				} else {
					res = new InvestigateStore();
					_myStore.put(index, res);
				}
				return res;
			}
		}

		public HashMap<Integer, Integer> currentTargets;
		public Vector<Integer> targetsDone;

		public InvestigateStore() {
			currentTargets = new HashMap<Integer, Integer>();
			targetsDone = new Vector<Integer>(5, 5);
		}

		/**
		 * remember we've spotted another target
		 *
		 * @param id
		 * @param myTarget
		 */
		public void addDoneTarget(final int id, final Integer myTarget) {
			targetsDone.add(myTarget);
		}

		public void clearCurrentTarget(final int id) {
			currentTargets.remove(id);
		}

		public Object countDoneTargets() {
			return targetsDone.size();
		}

		/**
		 * who are we after?
		 *
		 * @param id
		 * @return
		 */
		public Integer getCurrentTarget(final int id) {
			final Integer res = currentTargets.get(id);
			return res;
		}

		public boolean hasBeenDone(final Integer tgtId) {
			return targetsDone.contains(tgtId);
		}

		public boolean hasCurrentTarget(final Integer myId) {
			return currentTargets.get(myId) != null;
		}

		public boolean isACurrentTarget(final Integer tgtId) {
			final boolean res = currentTargets.containsValue(tgtId);
			// is anybody looking at this
			return res;
		}

		public void setCurrentTarget(final int myId, final int hisId) {
			if (currentTargets.get(myId) != null)
				currentTargets.remove(myId);

			currentTargets.put(myId, hisId);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean _speedChangeAllowed = true;

	/**
	 * keep track of how everybody's getting on
	 *
	 */
	protected static InvestigateStore.MappedStores _invData = new InvestigateStore.MappedStores();

	/**
	 * the type of target to investigate
	 */
	private TargetType _myTarget = new TargetType();

	/**
	 * type of target we're protecting
	 *
	 */
	private TargetType _watchType = null;

	/**
	 * the desired level of detection required to satisfy investigation
	 *
	 * @see ASSET.Models.Detection.DetectionEvent.CLASSIFIED et.al
	 */
	private int _detectionLevel;

	/**
	 * a local copy of our editable object
	 */
	private InvestigateInfo _myEditor = null;

	/****************************************************
	 * constructor
	 ***************************************************/

	/**
	 * whether to conduct a collaborative investigation
	 *
	 */
	protected boolean _collaborativeSearch;

	/**
	 * the height at which we conduct investigation
	 */
	private WorldDistance _investigateHeight;

	/**
	 * convenience value for who we're currently looking for
	 *
	 */
	private Integer _currentTarget;

	/**
	 * how we decide if investigation is complete
	 *
	 */
	private InvestigatePattern _myPattern;

	/**
	 * constructor.
	 *
	 * @param myName         the name of this behaviour
	 * @param myTarget       the target type to investigate
	 * @param detectionLevel the level of detection to achieve
	 */
	public Investigate(final String myName, final TargetType myTarget, final int detectionLevel,
			final WorldDistance investigateHeight) {
		this(myName, myTarget, null, detectionLevel, investigateHeight);
	}

	/**
	 * constructor.
	 *
	 * @param myName         the name of this behaviour
	 * @param myTarget       the target type to investigate
	 * @param watchType      the type of target we're defending
	 * @param detectionLevel the level of detection to achieve
	 */
	public Investigate(final String myName, final TargetType myTarget, final TargetType watchType,
			final int detectionLevel, final WorldDistance investigateHeight) {
		super(myName);
		_myTarget = myTarget;
		_detectionLevel = detectionLevel;
		_investigateHeight = investigateHeight;
		_watchType = watchType;
	}

	/**
	 * decide
	 *
	 * @param status parameter for decide
	 * @param time   parameter for decide
	 * @return the returned ASSET.Participants.DemandedStatus
	 */
	@Override
	public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
			final ASSET.Models.Movement.MovementCharacteristics chars,
			final ASSET.Participants.DemandedStatus demStatus, final ASSET.Models.Detection.DetectionList detections,
			final ScenarioActivityMonitor monitor, final long time) {

		SimpleDemandedStatus res = null;

		DetectionEvent validDetection = null;

		String activity = "";

		final Integer myId = status.getId();
		final InvestigateStore store = _invData.get(monitor, myId, isCollaborativeSearch());

		// do we have any detections?
		if (detections != null) {
			// get bearing to first detection
			final int len = detections.size();
			if (len > 0) {
				// first see if we are still in contact with our existing target
				// do we have an existing target?
				final Integer myTarget = store.getCurrentTarget(myId);
				if (myTarget != null) {

					// see if we can find it
					final DetectionList dl = detections.getDetectionsOf(myTarget);
					if (dl != null) {
						// of the detections of our current target. what's our best quality
						// detection?
						validDetection = dl.getBestDetection();

						if (getPattern().invComplete(validDetection, _detectionLevel))

						{
							// done. ready to move onto another
							// note that we check for greater or equal than detection state =
							// just in case
							// we've jumped a detection state
							store.addDoneTarget(myId, myTarget);

							// ok, output any results needed
							handleInvestigationComplete(validDetection, time, monitor);

							activity += "Current investigation complete. ";

							// and forget the current target
							store.clearCurrentTarget(myId);

							// and clear our local copy
							_currentTarget = null;

							// just reinforce the fact that we need to look for another target
							validDetection = null;

							// also output a message to any listeners - saying what we've done
							if (_myEditor != null) {
								if (_myEditor.hasReportListeners()) {
									final String msg = store.countDoneTargets() + " Targets identified";
									_myEditor.fireReport(this, msg);
								}
							}
						}

					} // if we have are still in contact with our target
					else {
						// oh well, current target lost. output message as necessary
						handleTargetLost(time, monitor, store.getCurrentTarget(myId));

						activity += "Current target lost. ";
					}
				} // whether we have a current target

				// do we still need to look for another target?
				if (validDetection == null) {
					// hmm, where are we?
					final WorldLocation myLoc = status.getLocation();

					// yes, go and find one amongst the remaining targets
					validDetection = findNewTarget(len, detections, time, monitor, store, myLoc);

					if (validDetection != null) {
						activity += "New target found.";
					}

				} // whether we've found the existing target
			} // if we have any detections
		} // if the detections object was received

		// ok. did we find a new target?
		if (validDetection != null) {
			// NO, GENERATE INTERCEPT BEARING
			// yes, calc the course to it
			final NetworkParticipant pt = monitor.getThisParticipant(validDetection.getTarget());

			// just check the participant is still live!!
			if (pt != null) {
				final Status tgtStat = pt.getStatus();
				res = Intercept.calculateInterceptCourseFor(status, tgtStat, time);

				// did it work?
				if (res == null) {
					// no, do we let ourselves change speed?
					if (_speedChangeAllowed) {
						final Status newStat = new Status(status);
						newStat.setSpeed(chars.getMaxSpeed());

						// and try again
						res = Intercept.calculateInterceptCourseFor(newStat, tgtStat, time);

						// did this work?
						if (res != null) {
							// yes, just try to increase speed
							res.setSpeed(chars.getMaxSpeed());
						}
					}
				}

				// just check we're capable of catching him
				if (res != null) {

					// ok, do we have a demanded height?
					if (_investigateHeight != null)
						res.setHeight(_investigateHeight);

					activity += "Turning towards target:" + pt.getName() + ". Dem brg:"
							+ MWC.Utilities.TextFormatting.GeneralFormat.formatBearing(res.getCourse());

					// see if there's any other demStatus setting to do
					res = getPattern().amendDemStatus(res, status, tgtStat);

					// output results to any listeners
					handleNewDemCourseChange((float) res.getCourse(), time, monitor, validDetection.getTarget());

					// and remember that we're heading for it
					store.setCurrentTarget(myId, validDetection.getTarget());

					// take copy, just for convenience
					_currentTarget = validDetection.getTarget();

				}
			}
		}

		if (activity == "")
			activity = "Inactive";

		super.setLastActivity(activity);

		// ok done. If we didn't find a valid target we aren't active.
		return res;
	}

	/**
	 * determine how far it is to the target
	 *
	 * @param de      the detection we're looking at
	 * @param monitor the accessor for the list of participants
	 * @param myLoc   my location
	 * @return
	 */
	private Double effectivenessOfThisTarget(final DetectionEvent de, final ScenarioActivityMonitor monitor,
			final WorldLocation myLoc) {
		Double res = null;
		if (_watchType != null) {
			// right, we're not assessing the range from ourselves, but from the
			// watched platform.

			// find a matching participant
			NetworkParticipant watched = null;

			final Integer[] parts = monitor.getListOfParticipants();
			for (int i = 0; i < parts.length; i++) {
				final int thisId = parts[i];

				final NetworkParticipant thisPart = monitor.getThisParticipant(thisId);
				if (_watchType.matches(thisPart.getCategory())) {
					watched = thisPart;
					break;
				}
			}

			if (watched != null) {
				// find tgt log
				final NetworkParticipant tgt = monitor.getThisParticipant(de.getTarget());
				final WorldLocation tgtLoc = tgt.getStatus().getLocation();

				// and the location of who I'm defending
				final WorldLocation watchLoc = watched.getStatus().getLocation();

				final double toWatch = tgtLoc.rangeFrom(watchLoc);
				final double toMe = tgtLoc.rangeFrom(myLoc);
				double outsideTargetPath = 1;
				double headingAwayFromTarget = 1;

				// sort out how to produce cost function that includes the respective
				// paths
				double watchPath = watched.getStatus().getCourse();
				double tgtToWatch = Math.toDegrees(tgtLoc.bearingFrom(watchLoc));

				// put them into some comfortable domain
				while (watchPath > 180)
					watchPath -= 360;
				while (tgtToWatch > 180)
					tgtToWatch -= 360;

				if (Math.abs(tgtToWatch - watchPath) < 45) {
					outsideTargetPath = 0.6;
				}

				watchPath = tgt.getStatus().getCourse();
				tgtToWatch = Math.toDegrees(watchLoc.bearingFrom(tgtLoc));

				// put them into some comfortable domain
				while (watchPath > 180)
					watchPath -= 360;
				while (tgtToWatch > 180)
					tgtToWatch -= 360;

				if (Math.abs(tgtToWatch - watchPath) < 45) {
					headingAwayFromTarget = 0.6;
				}

				// and calculate the range using a cost function
				res = (toMe * 0.05 + toWatch) * outsideTargetPath * headingAwayFromTarget;
			}
		} else {
			res = de.getRange().getValueIn(WorldDistance.DEGS);
		}

		return res;
	}

	/**
	 * scan through the available detections, to see if there's a new, valid one
	 *
	 * @param len        how many detections there are
	 * @param detections our current set of detetions
	 * @param time       the current time
	 * @param monitor    our scenario activity monitor
	 * @param myLoc      ownship location
	 * @return a contact representing our new target
	 */
	protected DetectionEvent findNewTarget(final int len, final DetectionList detections, final long time,
			final ScenarioActivityMonitor monitor, final InvestigateStore store, final WorldLocation myLoc) {
		DetectionEvent res = null;
		Double tmpScore = null;

		THROUGH_DETECTIONS: for (int i = 0; i < len; i++) {
			final DetectionEvent de = detections.getDetection(i);
			// do we know the range
			final Float brg = de.getBearing();
			if (brg != null) {
				// is this of our target type
				final Category thisTarget = de.getTargetType();
				if (_myTarget.matches(thisTarget)) {

					// have we found it already?
					final Integer tgtId = de.getTarget();

					final boolean beenDone = store.hasBeenDone(tgtId);
					final boolean isCurrent = store.isACurrentTarget(tgtId);

					if (beenDone || isCurrent) {
						// already found it. continue
					} else {
						// aah, just check that it hasn't already been removed from the
						// scenario
						if (monitor.getThisParticipant(de.getTarget()) != null) {
							// right, work out the range to the target
							final Double thisScore = effectivenessOfThisTarget(de, monitor, myLoc);

							// did we find a range?
							if (thisScore != null) {
								// is this our first match
								if (res != null) {
									if ((tmpScore == null) || (tmpScore > thisScore)) {
										// new target closer. switch to it
										res = de;
										tmpScore = thisScore;
									}
								} else {
									res = de;
									tmpScore = thisScore;
								}
							}
						}
					}
				} else {
					// drop out to the next detections
					continue THROUGH_DETECTIONS;
				}

			} // if we know the bearing
		} // looping through the detections

		// have we got a new target?
		if (res != null) {
			// ok, output any message as necessary
			handleNewTarget(res, time, monitor);
		}

		return res;
	}

	/**
	 * convenience method to get who we're looking for
	 *
	 * @return
	 */
	public Integer getCurrentTarget() {
		return _currentTarget;
	}

	/**
	 * return the required level of detection
	 *
	 * @return
	 * @see DetectionEvent.DETECTED
	 */
	public int getDetectionLevel() {
		return _detectionLevel;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new InvestigateInfo(this);

		return _myEditor;
	}

	/**
	 * get the (possibly null) height at which to conduct investigation
	 *
	 * @return height
	 */
	public WorldDistance getInvestigateHeight() {
		return _investigateHeight;
	}

	protected InvestigatePattern getPattern() {
		if (_myPattern == null)
			_myPattern = new InvestigatePatternFactory.MatchDetectionLevel();

		return _myPattern;
	}

	/**
	 * getTargetToEvade
	 *
	 * @return the returned TargetType
	 */
	public TargetType getTargetType() {
		return _myTarget;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: Investigate.java,v $
	 * Revision 1.1  2006/08/08 14:21:34  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:42  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.34  2004/11/03 09:54:49  Ian.Mayo
	 * Allow search speed to be set
	 *
	 * Revision 1.33  2004/11/01 15:54:55  Ian.Mayo
	 * Reflect new signature of Track Plot Observer
	 * <p/>
	 * Revision 1.32  2004/10/16 14:10:40  ian
	 * Implement progress reporting to editable subject
	 * <p/>
	 * Revision 1.31  2004/10/16 13:49:26  ian
	 * Implement firing report when we find a new target (viewable from editor pane)
	 * <p/>
	 * Revision 1.30  2004/10/15 11:11:30  Ian.Mayo
	 * Configure proper property editor for target category
	 * <p/>
	 * Revision 1.29  2004/09/27 14:53:56  Ian.Mayo
	 * Reflect fact that we now use intercept course rather than just head down bearing to tgt
	 * <p/>
	 * Revision 1.28  2004/09/24 11:08:10  Ian.Mayo
	 * Tidy test names
	 * <p/>
	 * Revision 1.27  2004/09/03 15:10:30  Ian.Mayo
	 * Output correct status messaging
	 * <p/>
	 * Revision 1.26  2004/09/02 15:59:02  Ian.Mayo
	 * Fix bug introduced in refactoring, then fix test
	 * <p/>
	 * Revision 1.25  2004/09/02 13:19:04  Ian.Mayo
	 * Tidy up, use DetectionList convenience methods
	 * <p/>
	 * Revision 1.24  2004/08/31 09:36:20  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.23  2004/08/26 16:27:02  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.22  2004/08/25 11:20:35  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.21  2004/08/20 13:32:27  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.20  2004/08/18 07:27:47  Ian.Mayo
	 * Don't run through huge scenario generation sample
	 * <p/>
	 * Revision 1.19  2004/08/17 14:22:04  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.18  2004/08/12 11:09:22  Ian.Mayo
	 * Respect observer classes refactored into tidy directories
	 * <p/>
	 * Revision 1.17  2004/08/10 14:00:32  Ian.Mayo
	 * Ditch the narrative observer, it was absorbed into Debrief Replay observer
	 * <p/>
	 * Revision 1.16  2004/08/10 13:51:44  Ian.Mayo
	 * Provide better activity message
	 * <p/>
	 * Revision 1.15  2004/08/10 08:50:06  Ian.Mayo
	 * Change functionality of Debrief replay observer so that it can record decisions & detections aswell.  Also include ability to track particular type of target
	 * <p/>
	 * Revision 1.14  2004/08/09 15:27:35  Ian.Mayo
	 * More testing
	 * <p/>
	 * Revision 1.13  2004/08/09 13:13:45  Ian.Mayo
	 * Getting closer with testing
	 * <p/>
	 * Revision 1.12  2004/08/06 12:52:01  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.11  2004/08/06 11:14:23  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.10  2004/08/06 10:27:45  Ian.Mayo
	 * Manage investigate height
	 * <p/>
	 * Revision 1.9  2004/08/05 15:20:14  Ian.Mayo
	 * Update Activity descriptor, change how we drop out of loop on finding matching contact, tidying.
	 * <p/>
	 * Revision 1.8  2004/08/04 14:26:33  Ian.Mayo
	 * Switch to ladder search, more tidying
	 * <p/>
	 * Revision 1.7  2004/08/04 09:52:10  Ian.Mayo
	 * Improve tests, test for height transition
	 * <p/>
	 * Revision 1.6  2004/08/03 15:18:59  Ian.Mayo
	 * Refactor to tidy message output
	 * <p/>
	 * Revision 1.5  2004/08/03 10:27:54  Ian.Mayo
	 * (Further) relax tests
	 * <p/>
	 * Revision 1.4  2004/08/02 15:31:19  Ian.Mayo
	 * Relax tests
	 * <p/>
	 * Revision 1.3  2004/08/02 15:00:53  Ian.Mayo
	 * Try to get comments working
	 * <p/>
	 * Revision 1.2  2004/08/02 10:05:04  Ian.Mayo
	 * Minor mod.
	 * <p/>
	 * Revision 1.1  2004/05/24 15:56:14  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.9  2004/05/05 20:39:58  ian
	 * Record demanded course
	 * <p/>
	 * Revision 1.8  2004/05/05 20:26:56  ian
	 * Switch to new narrative recorder
	 * <p/>
	 * Revision 1.7  2004/05/05 19:14:18  ian
	 * Use more typical flight height
	 * <p/>
	 * Revision 1.6  2004/04/28 21:39:04  ian
	 * Lots more changes during development
	 * <p/>
	 * Revision 1.5  2004/04/22 21:36:50  ian
	 * Tidying
	 * <p/>
	 * Revision 1.4  2004/04/19 19:00:12  ian
	 * add a note
	 * <p/>
	 * Revision 1.3  2004/04/15 21:59:41  ian
	 * Lost more
	 * <p/>
	 * Revision 1.2  2004/04/13 21:41:41  ian
	 * Getting closer
	 * <p/>
	 * Revision 1.1  2004/04/13 21:10:53  ian
	 * First version
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	public TargetType getWatchType() {
		return _watchType;
	}

	/**
	 * provide any necessary reporting of investigation complete
	 *
	 * @param de      the target we were investigating
	 * @param time    the time at which investigation was complete
	 * @param monitor the scenario activity monitor
	 */
	protected void handleInvestigationComplete(final DetectionEvent de, final long time,
			final ScenarioActivityMonitor monitor) {
	}

	/**
	 * provide any requested reporting of new demanded course
	 *
	 * @param brgToTarget the new bearing to head down
	 * @param time        the current time
	 * @param monitor     the scenario activity monitor
	 * @param targetId
	 */
	protected void handleNewDemCourseChange(final float brgToTarget, final long time,
			final ScenarioActivityMonitor monitor, final int targetId) {
	}

	/**
	 * provide any necessary reporting for we have identified a new target
	 *
	 * @param de      the new target we're going to head for
	 * @param time    the current time
	 * @param monitor the scenario activity monitor
	 */
	protected void handleNewTarget(final DetectionEvent de, final long time, final ScenarioActivityMonitor monitor) {
		// ok, clear the detection helper
		getPattern().newTarget(de.getTarget());
	}

	/**
	 * provide any necessary reporting for target not being found amongst current
	 * contacts
	 *
	 * @param time    the current time
	 * @param monitor the scenario activity monitor
	 */
	protected void handleTargetLost(final long time, final ScenarioActivityMonitor monitor, final int currentTarget) {
	}

	// ////////////////////////////////////////////////////////////////////
	// editable data
	// ////////////////////////////////////////////////////////////////////
	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	// //////////////////////////////////////////////////////////
	// model support
	// //////////////////////////////////////////////////////////

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 *
	 * @param currentStatus
	 */
	@Override
	public void interrupted(final Status currentStatus) {
		// ignore.
	}

	/**
	 * whether to collaboratively search (agree who's spotting which targets)
	 *
	 * @return yes/no
	 */
	public boolean isCollaborativeSearch() {
		return _collaborativeSearch;
	}

	/**
	 * reset this decision model
	 */
	@Override
	public void restart() {
		// clear teh store
		_invData.clearStores();
	}

	/**
	 * whether to collaboratively search (agree who's spotting which targets)
	 *
	 * @return yes/no
	 */
	public void setCollaborativeSearch(final boolean collaborativeSearch) {
		_collaborativeSearch = collaborativeSearch;
	}

	/**
	 * set the required level of detection
	 *
	 * @param detectionLevel
	 */
	public void setDetectionLevel(final int detectionLevel) {
		this._detectionLevel = detectionLevel;
	}

	/**
	 * set the (optional) height at which to conduct investigation
	 *
	 * @param investigateHeight
	 */
	public void setInvestigateHeight(final WorldDistance investigateHeight) {
		this._investigateHeight = investigateHeight;
	}

	/**
	 * setTargetToEvade
	 *
	 * @param target parameter for setTargetToEvade
	 */
	public void setTargetType(final TargetType target) {
		_myTarget = target;
	}

}