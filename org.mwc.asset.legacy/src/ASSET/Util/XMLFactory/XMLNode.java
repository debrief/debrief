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

package ASSET.Util.XMLFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;

public class XMLNode implements XMLObject {
	private XMLChoice _myOperation = null;

	public XMLNode(final Element element) {
		// read ourselves in from this node

		// get the choice
		final Element choice = (Element) element.getElementsByTagName("Choice").item(0);
		_myOperation = new XMLChoice(choice);
	}

	/**
	 * default constructor, used in cloning
	 *
	 */
	private XMLNode(final XMLNode other) {

	}

	/**
	 * create a clone of this object
	 *
	 */
	@Override
	public Object clone() {
		final XMLNode res = new XMLNode(this);
		return res;
	}

	/**
	 * perform our update to the supplied element
	 *
	 */
	@Override
	public void execute(final Element object) {

		final List<Element> list = _myOperation.getList();

		// perform deep copy
		final Vector<Element> duplicate = new Vector<Element>(0, 1);
		final Iterator<Element> iter = list.iterator();
		while (iter.hasNext()) {
			final Object o = iter.next();
			if (o instanceof Element) {
				final Element el = (Element) o;
				final Element dup = (Element) el.cloneNode(true);
				el.removeChild(dup);
				duplicate.add(dup);
			}
		}

		// pass though, double-checking that we've removed any duplicates

		// set it's parent
		// remove any existing content
		while (object.hasChildNodes())
			object.removeChild(object.getFirstChild());

		// set the data
		for (final Iterator<Element> iterator = duplicate.iterator(); iterator.hasNext();) {
			final Element element = iterator.next();
			object.appendChild(element);
		}

	}

	/**
	 * return the last value used for this attribute
	 *
	 */
	@Override
	public String getCurValue() {
		return "Empty";
	}

	@Override
	public String getCurValueIn(final Element object) {
		return "Empty";
	}

	/**
	 * return the name of this variable
	 *
	 */
	@Override
	public String getName() {
		return "un-named";
	}

	/**
	 * merge ourselves with the supplied object
	 *
	 */
	@Override
	public void merge(final XMLObject other) {
	}

	/**
	 * randomise ourselves
	 *
	 */
	@Override
	public void randomise() {
		_myOperation.newPermutation();
	}

}
