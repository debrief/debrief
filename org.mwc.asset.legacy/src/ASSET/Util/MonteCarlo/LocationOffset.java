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

package ASSET.Util.MonteCarlo;

import java.text.ParseException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

/**
 * variance class which dictates that random locations be produced within a
 * specified range of an origin
 */
public final class LocationOffset implements XMLObject {
	//////////////////////////////////////////////////
	// property testing
	//////////////////////////////////////////////////
	public static class LocOffsetTest extends SupportTesting {
		public LocOffsetTest(final String s) {
			super(s);
		}

		public void testArea() {

			final WorldDistance myDist = new WorldDistance(1, WorldDistance.DEGS);
			final WorldLocation myOrigin = new WorldLocation(8, 0, 0);
			final int myModel = RandomGenerator.NORMAL;
			final LocationOffset la = new LocationOffset(myOrigin, myDist, myModel);

			// and get the locations
			final int limit = 100;
			for (int i = 0; i < limit; i++) {
				la.newPermutation();
				final WorldLocation thisLoc = la.thisPermutation();
				final WorldVector error = thisLoc.subtract(myOrigin);
				final double rngDegs = error.getRange();
				assertTrue("generated location isn't inside indicated area", rngDegs < 1);
			}
		}

	}

	private static final String OFFSET_NAME = "OffsetDistance";

	/**
	 * the origin for the new locations
	 */
	private WorldLocation _myOrigin;

	/**
	 * the range to stretch the positions out to
	 */
	private WorldDistance _myRange;
	/**
	 * the pattern in which our random instances are created
	 */
	protected int _myRandomModel;
	/**
	 * the current location we're creating
	 */
	private WorldLocation _currentLoc;

	private final String LOCATION_NAME = "Origin";

	/**
	 * constructor - read ourselves in from the element
	 *
	 * @param element
	 * @throws ParseException
	 */
	public LocationOffset(final Element element) throws ParseException {
		// read ourselves in from this node
		final WorldLocation myArea = XMLVariance.readInLocationFromXML(element, LOCATION_NAME);

		// and the distance offset
		final WorldDistance myDistance = WorldDistanceHandler.extractWorldDistance(element, OFFSET_NAME);

		// and get the number pattern
		final int myRandomModel = XMLVariance.readRandomNumberModel(element);

		// and store the data
		initialise(myArea, myDistance, myRandomModel);
	}

	/**
	 * private constructor - used for tests
	 *
	 * @param myArea
	 * @param myModel
	 */
	LocationOffset(final WorldLocation myArea, final WorldDistance range, final int myModel) {
		initialise(myArea, range, myModel);
	}

	/**
	 * perform our update to the supplied element
	 */
	@Override
	public final String execute(final Element currentInstance, final Document parentDocument) {
		// ok, here we go..
		// we're going to replace the object with one of our permutations
		String res = null;

		// create the new permutation we need
		newPermutation();

		// check that this element is a world area
		// generate a new location
		final Element newLocation = parentDocument.createElement("shortLocation");
		newLocation.setAttribute("Lat", "" + _currentLoc.getLat());
		newLocation.setAttribute("Long", "" + _currentLoc.getLong());

		// first remove the existing location
		final int num = currentInstance.getChildNodes().getLength();
		for (int i = 0; i < num; i++) {
			final Node thisElement = currentInstance.getChildNodes().item(i);
			if (thisElement.getNodeName().equals("shortLocation")) {
				currentInstance.removeChild(thisElement);
				break;
			} else if (thisElement.getNodeName().equals("shortLocation")) {
				currentInstance.removeChild(thisElement);
				break;
			}
		}

		// ok, insert our new item
		currentInstance.insertBefore(newLocation, null);

		// convert the new value to a string, to be used in the hashing value
		res = ScenarioGenerator.writeToString(newLocation);

		// done
		return res;

	}

	/**
	 * return the last value used for this attribute
	 */
	@Override
	public final String getCurValue() {
		return _currentLoc.toString();
	}

	@Override
	public final String getCurValueIn(final Element object) {
		return "Empty";
	}

	/**
	 * return the name of this variable
	 */
	@Override
	public final String getName() {
		return "un-named Location offset variance";
	}

	/**
	 * private initialiser - we use this so that we can create a location area
	 * without having to pass in the elements
	 *
	 * @param myArea
	 * @param myRandomModel
	 */
	private void initialise(final WorldLocation myArea, final WorldDistance myRange, final int myRandomModel) {
		_myOrigin = myArea;
		_myRandomModel = myRandomModel;
		_myRange = myRange;
	}

	/**
	 * merge ourselves with the supplied object
	 */
	@Override
	public final void merge(final XMLObject other) {
	}

	/**
	 * generate a new permutation
	 */
	void newPermutation() {
		// ok, generate new location
		final double bearing = RandomGenerator.nextRandom() * 360;
		final double range = RandomGenerator.nextRandom() * _myRange.getValueIn(WorldDistance.DEGS);
		final WorldVector offset = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearing), range, 0);
		_currentLoc = _myOrigin.add(offset);
	}

	/**
	 * randomise ourselves
	 */
	@Override
	public final void randomise() {
		newPermutation();
	}

	/**
	 * retrieve the current permutation
	 *
	 * @return
	 */
	WorldLocation thisPermutation() {
		return _currentLoc;
	}

}
