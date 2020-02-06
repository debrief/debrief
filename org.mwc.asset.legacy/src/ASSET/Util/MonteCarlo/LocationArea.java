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
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * variance class which dictates that random locations be produced within a
 * specified rectangle
 */
public final class LocationArea implements XMLObject {
	// ////////////////////////////////////////////////
	// property testing
	// ////////////////////////////////////////////////
	public static class LocAreaTest extends SupportTesting {
		public LocAreaTest(final String s) {
			super(s);
		}

		public void testArea() {

			final WorldLocation wa = new WorldLocation(8, 0, 0);
			final WorldLocation wb = new WorldLocation(10, 2, 0);
			final WorldArea myArea = new WorldArea(wa, wb);
			final int myModel = RandomGenerator.NORMAL;
			final LocationArea la = new LocationArea(myArea, myModel);

			// and get the locations
			final int limit = 100;
			for (int i = 0; i < limit; i++) {
				la.newPermutation();
				final WorldLocation thisLoc = la.thisPermutation();
				assertTrue("generated location (" + thisLoc + ") isn't inside indicated area:" + myArea,
						myArea.contains(thisLoc));
			}
		}

	}

	/**
	 * the area within which the new locations are created
	 */
	private WorldArea _myArea;

	/**
	 * the pattern in which our random instances are created
	 */
	private int _myRandomModel;

	/**
	 * the current location we're creating
	 */
	private WorldLocation _currentLoc;

	/**
	 * constructor - read ourselves in from the element
	 *
	 * @param element
	 * @throws ParseException
	 */
	public LocationArea(final Element element) throws ParseException {
		// read ourselves in from this node
		final WorldArea myArea = XMLVariance.readInAreaFromXML(element);

		// and get the number pattern
		final int myRandomModel = XMLVariance.readRandomNumberModel(element);

		// and store the data
		initialise(myArea, myRandomModel);
	}

	/**
	 * private constructor - used for tests
	 *
	 * @param myArea
	 * @param myModel
	 */
	LocationArea(final WorldArea myArea, final int myModel) {
		initialise(myArea, myModel);
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
			} else if (thisElement.getNodeName().equals("relativeLocation")) {
				currentInstance.removeChild(thisElement);
				break;
			} else if (thisElement.getNodeName().equals("longLocation")) {
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
		return "un-named Location Area variance";
	}

	/**
	 * private initialiser - we use this so that we can create a location area
	 * without having to pass in the elements
	 *
	 * @param myArea
	 * @param myRandomModel
	 */
	private void initialise(final WorldArea myArea, final int myRandomModel) {
		_myArea = myArea;
		_myRandomModel = myRandomModel;
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
		do {
			_currentLoc = XMLVariance.generateRandomLocationInArea(_myArea, _myRandomModel);
		} while (!_myArea.contains(_currentLoc));
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
