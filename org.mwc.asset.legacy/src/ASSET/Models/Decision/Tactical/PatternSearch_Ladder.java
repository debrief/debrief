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

/**
 * ASSET from PlanetMayo Ltd
 * User: Ian.Mayo
 * Date: $Date$
 * Time: $Time:$
 * Log:
 *  $Log: LadderSearch.java,v $
 *  Revision 1.2  2006/09/11 09:36:04  Ian.Mayo
 *  Fix dodgy accessor
 *
 *  Revision 1.1  2006/08/08 14:21:34  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:43  Ian.Mayo
 *  First versions
 *
 *  Revision 1.16  2004/11/25 14:29:29  Ian.Mayo
 *  Handle switch to hi-res dates
 *
 *  Revision 1.15  2004/11/03 09:54:50  Ian.Mayo
 *  Allow search speed to be set
 *
 *  Revision 1.14  2004/11/01 14:31:44  Ian.Mayo
 *  Do more elaborate processing when creating sample instance for property testing
 *
 *  Revision 1.13  2004/11/01 10:47:47  Ian.Mayo
 *  Provide accessor for series of generated points
 *
 *  Revision 1.12  2004/08/31 09:36:22  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.11  2004/08/26 16:27:03  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.10  2004/08/25 11:20:37  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.9  2004/08/24 10:36:25  Ian.Mayo
 *  Do correct activity management bits (using parent)
 *
 *  Revision 1.8  2004/08/20 13:32:29  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.7  2004/08/17 14:22:06  Ian.Mayo
 *  Refactor to introduce parent class capable of storing name & isActive flag
 *
 *  Revision 1.6  2004/08/09 15:50:31  Ian.Mayo
 *  Refactor category types into Force, Environment, Type sub-classes
 *
 *  Revision 1.5  2004/08/06 12:52:03  Ian.Mayo
 *  Include current status when firing interruption
 *
 *  Revision 1.4  2004/08/06 11:14:25  Ian.Mayo
 *  Introduce interruptable behaviours, and recalc waypoint route after interruption
 *
 *  Revision 1.3  2004/08/05 14:54:11  Ian.Mayo
 *  Provide accessor to get series of waypoints
 *
 *  Revision 1.2  2004/08/04 10:33:13  Ian.Mayo
 *  Add optional search height, use it
 *
 *  Revision 1.1  2004/05/24 15:56:29  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.4  2004/04/22 21:37:38  ian
 *  Tidying
 *
 *  Revision 1.3  2004/04/13 20:53:45  ian
 *  Implement restart functionality
 *
 *  Revision 1.2  2004/03/25 21:06:14  ian
 *  Correct placing of first point, correct tests
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:52  ian
 *  no message
 *

 */

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * Class implementing a square-shaped search pattern around an initial datum
 */
public final class PatternSearch_Ladder extends PatternSearch_Core implements Serializable {

	//////////////////////////////////////////////////
	// editable properties
	//////////////////////////////////////////////////
	static public class LadderSearchInfo extends EditorType {
		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public LadderSearchInfo(final PatternSearch_Core data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this search behaviour"),
						prop("LegLength", "how wide each leg of the ladder should be"),
						prop("MaxLegs", "the number of legs to conduct"), prop("Origin", "the origin for this search"),
						prop("Route", "the series of points comprising the ladder search"),
						prop("SearchHeight", "the height to search at"),
						prop("TrackSpacing", "how far apart to make each leg"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the default number of legs to produce
	 */
	static final int DEFAULT_ROUTE_LENGTH = 1000;

	/**
	 * the name of this behaviour
	 */
	static final String LADDER_SEARCH = "Performing ladder search";

	/**
	 * the initial heading for the manoeuvre
	 */
	double _ladderAxis;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////

	/**
	 * the leg length
	 */
	WorldDistance _legLength;

	/**
	 * the number of legs to run (optional)
	 */
	Integer _maxLegs;

	//////////////////////////////////////////////////
	// decision options
	//////////////////////////////////////////////////

	/**
	 * conduct a ladder search
	 *
	 * @param ladderAxis   the initial direction
	 * @param maxLegs      the number of legs to run (optional)
	 * @param myOrigin     the starting point
	 * @param trackSpacing the track spacing (separation in ladder axis)
	 * @param legLength    the length of each leg
	 * @param searchHeight the (optional) height/depth to conduct ladder at
	 * @param name         the name for this ladder search behaviour
	 */
	public PatternSearch_Ladder(final double ladderAxis, final Integer maxLegs, final WorldLocation myOrigin,
			final WorldDistance trackSpacing, final WorldDistance legLength, final WorldDistance searchHeight,
			final WorldSpeed searchSpeed, final String name) {
		super(name, myOrigin, searchHeight, searchSpeed, trackSpacing, null, null);
		_ladderAxis = ladderAxis;
		_maxLegs = maxLegs;
		_trackSpacing = trackSpacing;
		_legLength = legLength;
		_finished = false;
	}

	/**
	 * create the area search path as a route
	 *
	 * @param maxLegs      the maximum number of legs to fly
	 * @param origin       the start datum
	 * @param trackSpacing the spacing between loops of the spiral
	 * @return a path containing the route to fly
	 */
	@Override
	protected WorldPath createSearchRoute(final WorldLocation ignoreLocation) {
		// no - we'd better do it then.
		final WorldPath route = new WorldPath();

		// and now fill in the positions
		int routeLen = 0;

		if (_maxLegs != null)
			routeLen = _maxLegs.intValue();
		else
			routeLen = DEFAULT_ROUTE_LENGTH;

		// remember the 90 deg turn in rads
		final double turn90 = MWC.Algorithms.Conversions.Degs2Rads(90);

		// remember the ladder axis in radians
		final double ladderRads = MWC.Algorithms.Conversions.Degs2Rads(_ladderAxis);

		// remember the last location
		WorldLocation currentLocation = getOrigin();

		// and store it
		addPointToRoute(_searchHeight, currentLocation, route);

		// convert our dimensions to degrees
		final double legDist = _legLength.getValueIn(WorldDistance.DEGS);
		final double trkSpace = _trackSpacing.getValueIn(WorldDistance.DEGS);

		// keep track of whether we're doing a left or right leg
		float turnDirection = -1;

		// we will use special processing for the first case to only travel
		// down half a track length at the start - so that the areaa
		// of swept water overlaps the edge of the area (assuming that the
		// track spacing is equal to the swept water area)
		boolean first = true;

		// so, let's get going
		for (int i = 1; i <= routeLen; i++) {

			// create the two waypoint vectors (as per the modelling guide)
			WorldVector wp1 = null, wp2 = null;

			// ok. start off with wp1 (modifying it so that for WP1 we travel across by the
			// leg length)
			if (first) {
				wp1 = new WorldVector(ladderRads, trkSpace / 2, 0);
				first = false;
			} else {
				wp1 = new WorldVector(ladderRads, trkSpace, 0);
			}

			currentLocation = new WorldLocation(currentLocation.add(wp1));

			// and store it
			addPointToRoute(_searchHeight, currentLocation, route);

			// and now on to wp2
			wp2 = new WorldVector(ladderRads + turnDirection * turn90, legDist, 0);
			currentLocation = new WorldLocation(currentLocation.add(wp2));

			// and store it
			addPointToRoute(_searchHeight, currentLocation, route);

			// ok. we are now at the end of an along, up, across, down manoeuvre, ready to
			// go along again.
			turnDirection *= -1;

		}
		return route;
	}

	@Override
	protected String getDescriptor() {
		return LADDER_SEARCH;
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new LadderSearchInfo(this);

		return _myEditor;
	}

	public final double getInitialTrack() {
		return _ladderAxis;
	}

	public WorldDistance getLegLength() {
		return _legLength;
	}

	public final Integer getMaxLegs() {
		return _maxLegs;
	}

	//////////////////////////////////////////////////
	// property editing
	//////////////////////////////////////////////////

	public final void setInitialTrack(final double _initialTrack) {
		this._ladderAxis = _initialTrack;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	public void setLegLength(final WorldDistance legLength) {
		this._legLength = legLength;
	}

	public final void setMaxLegs(final Integer _maxLegs) {
		this._maxLegs = _maxLegs;
	}

}
