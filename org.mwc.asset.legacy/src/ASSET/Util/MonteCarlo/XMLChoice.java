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

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ASSET.Util.RandomGenerator;

public final class XMLChoice implements XMLOperation {

	/**
	 * the title of the name field
	 *
	 */
	private final static String NAME = "name";

	/**
	 * the tag for a single value
	 *
	 */
	private static final String VALUE_TYPE = "Value";

	/**
	 * the attribute within which a value is stored
	 *
	 */
	private static final String VALUE = "value";

	public static void main(final String[] args) {
		final XMLChoice ch = new XMLChoice();

		ch._myList.add("a1");
		ch._myList.add("a2");
		ch._myList.add("a3");
		ch._myList.add("a4");

		ch.newPermutation();

		final String val = ch.getValue();
		System.out.println(val);

	}

	/**
	 * the list of values to choose from
	 *
	 */
	final Vector<String> _myList;

	/**
	 * the element we will use for this permutation
	 *
	 */
	private String _currentVal;

	/**
	 * the name given to this set of choices
	 *
	 */
	private String _myName;

	XMLChoice() {
		_myList = new Vector<String>();
	}

	/***************************************************************
	 * constructor
	 ***************************************************************/
	public XMLChoice(final Element element) {
		this();

		// you know, get the stuff
		_myName = element.getAttribute(NAME);

		// have a fish around inside it

		final NodeList vars = element.getElementsByTagName(VALUE_TYPE);

		final int len = vars.getLength();
		for (int i = 0; i < len; i++) {
			final Element thisE = (Element) vars.item(i);
			final String thisName = thisE.getAttribute(VALUE);
			_myList.add(thisName);
		}

		// stick in a random variable to start us off
		newPermutation();
	}

	private XMLChoice(final XMLChoice other) {
		this();

		// pass through and copy components
		final Iterator<String> iter = other._myList.iterator();
		while (iter.hasNext()) {
			final String thisVal = iter.next();
			_myList.add(thisVal);
		}
	}

	/**
	 * clone this object
	 *
	 */
	@Override
	public final Object clone() {
		final XMLChoice res = new XMLChoice(this);
		return res;
	}

	public final String get(final int index) {
		return _myList.get(index);
	}

	public final String getName() {
		return _myName;
	}

	/**
	 * return the current value of this permutation
	 *
	 */
	@Override
	public final String getValue() {
		return _currentVal;
	}

	/**
	 * merge ourselves with the supplied operation
	 *
	 */
	@Override
	public final void merge(final XMLOperation other) {
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/
	/**
	 * produce a new value for this operation
	 *
	 */
	@Override
	public final void newPermutation() {
		// gen random value in the correct range (we will always be less than the
		// upper limit)
		final double randDbl = ASSET.Util.RandomGenerator.generateRandomNumber(0, _myList.size(),
				RandomGenerator.UNIFORM);

		// convert to int
		final int index = (int) randDbl;

		_currentVal = _myList.get(index);
	}

	public final int size() {
		return _myList.size();
	}

}
