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

package ASSET.Models.Movement;

import ASSET.Participants.DemandedStatus;
import ASSET.Util.SupportTesting;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 19-Aug-2003 Time: 14:52:26 To
 * change this template use Options | File Templates.
 */
public class HighLevelDemandedStatus extends DemandedStatus {

	/**********************************************************************
	 * member variables
	 *********************************************************************/

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public class HighLevelDemStatTest extends SupportTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public HighLevelDemStatTest(final String val) {
			super(val);
		}

		public void testNextTarget() {
			final WorldLocation wa = new WorldLocation(1, 0, 0);
			final WorldLocation wb = new WorldLocation(2, 0, 0);
			final WorldLocation wc = new WorldLocation(3, 0, 0);
			final WorldLocation wd = new WorldLocation(4, 0, 0);
			final WorldLocation we = new WorldLocation(5, 0, 0);
			final WorldLocation wf = new WorldLocation(6, 0, 0);
			final WorldLocation wg = new WorldLocation(7, 0, 0);

			final WorldPath wp = new WorldPath();
			wp.addPoint(wa);
			wp.addPoint(wb);
			wp.addPoint(wc);
			wp.addPoint(wd);
			wp.addPoint(we);
			wp.addPoint(wf);
			wp.addPoint(wg);

			final HighLevelDemandedStatus hl = new HighLevelDemandedStatus(12, 0, 2, wp, null, null);
			hl.setRunInReverse(false);

			WorldLocation res = hl.getCurrentTarget();
			assertEquals("returned current target", wc, res);

			res = hl.getNextTarget();
			assertEquals("returned next target", wd, res);

			// switch us into reverse
			hl.setRunInReverse(true);
			res = hl.getNextTarget();
			assertEquals("returned next target", wb, res);

			// and forwards again
			hl.setRunInReverse(false);

			// do the next waypoint
			hl.nextWaypointVisited();

			res = hl.getCurrentTarget();
			assertEquals("returned current target", wd, res);

			res = hl.getNextTarget();
			assertEquals("returned next target", we, res);

			// do the next few waypoint
			hl.nextWaypointVisited();
			hl.nextWaypointVisited();
			hl.nextWaypointVisited();

			res = hl.getCurrentTarget();
			assertEquals("returned current target", wg, res);

			res = hl.getNextTarget();
			assertEquals("returned next target", null, res);

			// and fall of fthe plot
			hl.nextWaypointVisited();

			res = hl.getCurrentTarget();
			assertEquals("returned current target", null, res);
			res = hl.getNextTarget();
			assertEquals("returned next target", null, res);

			// now go into reverse
			hl.setCurrentTargetIndex(3);
			hl.setRunInReverse(true);

			res = hl.getCurrentTarget();
			assertEquals("returned current target", wd, res);
			res = hl.getNextTarget();
			assertEquals("returned next target", wc, res);

			// do the next few waypoint
			hl.nextWaypointVisited();
			hl.nextWaypointVisited();
			hl.nextWaypointVisited();

			res = hl.getCurrentTarget();
			assertEquals("returned current target", wa, res);
			res = hl.getNextTarget();
			assertEquals("returned next target", null, res);

			// and fall of fthe plot
			hl.nextWaypointVisited();

			res = hl.getCurrentTarget();
			assertEquals("returned current target", null, res);
			res = hl.getNextTarget();
			assertEquals("returned next target", null, res);

		}
	}

	/**
	 * location index to mark that path is complete
	 */
	public static final int PATH_COMPLETE = -1;

	/**
	 * the path of points we want to follow
	 */
	private WorldPath _myPath;

	/**
	 * the current location we are heading for
	 */
	private int _currentTarget = 0;

	/**
	 * the visit strategy we employ
	 */
	private WaypointVisitor _visitType;

	/**
	 * whether to follow through points in reverse
	 */
	private boolean _runInReverse = false;

	/**
	 * an (optional) speed to travel at
	 */
	private WorldSpeed _demSpeed = null;

	public HighLevelDemandedStatus(final long id, final HighLevelDemandedStatus original) {
		super(id, original.getTime());
		_myPath = original.getPath();
	}

	/**
	 * *******************************************************************
	 * constructors
	 * *******************************************************************
	 */
	public HighLevelDemandedStatus(final long id, final long time) {
		super(id, time);
	}

	public HighLevelDemandedStatus(final long id, final long time, final HighLevelDemandedStatus original) {
		super(id, time);
		_myPath = original.getPath();
	}

	/**
	 * constructor to build this path
	 *
	 * @param currentTarget the current target to head for (or null if the first
	 *                      one)
	 * @param myPath        the path of points to follow
	 * @param visitType     the type of visiting process we use
	 */
	public HighLevelDemandedStatus(final long id, final long time, final int currentTarget, final WorldPath myPath,
			final WaypointVisitor visitType, final WorldSpeed demSpeed) {
		this(id, time);
		this._currentTarget = currentTarget;
		this._myPath = myPath;
		this._visitType = visitType;
		this._demSpeed = demSpeed;
	}

	public WorldLocation getCurrentTarget() {
		final WorldLocation res;

		if (_currentTarget == PATH_COMPLETE) {
			res = null;
		} else
			res = _myPath.getLocationAt(_currentTarget);

		return res;
	}

	/**
	 * the current target we are heading for (zero-indexed)
	 *
	 * @return
	 */
	public int getCurrentTargetIndex() {
		return _currentTarget;
	}

	/**
	 * retrieve the waypoint we head for after the current one. (of particular
	 * relevance when doing a MakeWaypoint manoeuvre)
	 *
	 * @return not the current target, but the one after (or null)
	 */
	public WorldLocation getNextTarget() {
		WorldLocation res = null;

		// have we finished?
		if (_currentTarget != PATH_COMPLETE) {
			if (_runInReverse) {
				if (_currentTarget != 0)
					res = _myPath.getLocationAt(_currentTarget - 1);
			} else {
				if (_currentTarget != _myPath.getPoints().size() - 1) {
					res = _myPath.getLocationAt(_currentTarget + 1);
				}
			}
		}

		return res;
	}

	public WorldPath getPath() {
		return _myPath;
	}

	public boolean getRunInReverse() {
		return _runInReverse;
	}

	public WorldSpeed getSpeed() {
		return _demSpeed;
	}

	public WaypointVisitor getVisitType() {
		return _visitType;
	}

	public void nextWaypointVisited() {
		if (_runInReverse) {
			_currentTarget--;

			// have we passed the end of our list of locaitgons?
			if (_currentTarget < 0) {
				// yes - mark path complete
				_currentTarget = PATH_COMPLETE;
			}

		} else {
			_currentTarget++;

			// have we passed the end of our list of locaitgons?
			if (_currentTarget >= _myPath.getPoints().size()) {
				// yes - mark path complete
				_currentTarget = PATH_COMPLETE;
			}
		}

	}

	/**
	 * set the current target we are heading for (zero-indexed)
	 *
	 * @param currentTarget
	 */
	public void setCurrentTargetIndex(final int currentTarget) {
		this._currentTarget = currentTarget;
	}

	public void setPath(final WorldPath myPath) {
		this._myPath = myPath;
	}

	public void setRunInReverse(final boolean runInReverse) {
		this._runInReverse = runInReverse;
	}

	public void setSpeed(final WorldSpeed _demSpeed) {
		this._demSpeed = _demSpeed;
	}

	public void setVisitType(final WaypointVisitor visitType) {
		this._visitType = visitType;
	}

	public int size() {
		return _myPath.size();
	}

}
