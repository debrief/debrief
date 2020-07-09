
package ASSET.Models.Decision;

import java.util.Iterator;
import java.util.Vector;

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

import ASSET.Participants.Category;

public class TargetType implements java.io.Serializable {

	// ////////////////////////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////////////////////////

	public static class TargetType_Test extends junit.framework.TestCase {

		public void testAND() {
			final TargetType tt = new TargetType();
			tt.setANDOperation(true);
			tt.addTargetType(Category.Force.BLUE);
			tt.addTargetType(Category.Environment.AIRBORNE);

			final Category newCat = new Category(Category.Force.GREEN, Category.Environment.SUBSURFACE,
					Category.Type.FISHING_VESSEL);
			assertFalse("don't match - no matching types", tt.matches(newCat));

			newCat.setEnvironment(Category.Environment.AIRBORNE);
			assertFalse("don't match - not all types", tt.matches(newCat));

			newCat.setForce(Category.Force.BLUE);
			assertTrue("matches - all types", tt.matches(newCat));
		}

		public void testOR_1() {
			final TargetType tt = new TargetType();
			tt.setANDOperation(false);
			tt.addTargetType(Category.Force.BLUE);
			tt.addTargetType(Category.Environment.AIRBORNE);

			final Category newCat = new Category(Category.Force.GREEN, Category.Environment.SUBSURFACE,
					Category.Type.FISHING_VESSEL);
			assertFalse("don't match - no matching types", tt.matches(newCat));

			newCat.setEnvironment(Category.Environment.AIRBORNE);
			assertTrue("matches - some types", tt.matches(newCat));

			newCat.setForce(Category.Force.BLUE);
			assertTrue("matches - all types", tt.matches(newCat));
		}

		public void testOR_2() {
			final TargetType tt = new TargetType();
			tt.setANDOperation(false);
			tt.addTargetType(Category.Force.BLUE);
			tt.addTargetType(Category.Force.GREEN);

			final Category newCat = new Category(Category.Force.RED, Category.Environment.SUBSURFACE,
					Category.Type.FISHING_VESSEL);
			assertFalse("don't match - no matching types", tt.matches(newCat));

			newCat.setEnvironment(Category.Force.BLUE);
			assertTrue("matches - some types", tt.matches(newCat));
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private java.util.Vector<String> _myTargets;

	/**
	 * whether we are applying and 'and' or an 'or' when matching the categories
	 *
	 */
	private boolean _ANDoperation = true;

	// ////////////////////////////////////////////////////////////////////
	// constructors
	// ////////////////////////////////////////////////////////////////////
	public TargetType() {
	}

	public TargetType(final String target) {
		addTargetType(target);
	}

	// ////////////////////////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////////////////////////

	public TargetType(final TargetType other) {
		_myTargets = new Vector<String>(0, 1);
		_myTargets.addAll(other._myTargets);
	}

	/**
	 * add a target type to this behaviour
	 *
	 * @param type a ASSET.Particiants.Category
	 */
	public void addTargetType(final String type) {
		if (type == null)
			MWC.Utilities.Errors.Trace.trace("Trying to add null type to target type");
		else {
			if (_myTargets == null)
				_myTargets = new java.util.Vector<String>(1, 1);

			_myTargets.addElement(type);
		}
	}

	/**
	 * identify whether we need to match 'all' attributes (AND) or 'some' attributes
	 * (OR)
	 *
	 */
	public Boolean getANDOperation() {
		return _ANDoperation;
	}

	public java.util.Collection<String> getTargets() {
		if (_myTargets == null)
			_myTargets = new java.util.Vector<String>(1, 1);

		return _myTargets;
	}

	/**
	 * see if this target type matches the supplied category
	 *
	 * @param thisCategory the target type to match
	 * @return yes/no
	 * @see Category.Force.RED for example
	 */
	public boolean matches(final Category thisCategory) {
		boolean res = false;

		int num_matches = 0;

		if (_myTargets != null) {

			final java.util.Enumeration<String> enumer = _myTargets.elements();
			while (enumer.hasMoreElements()) {
				final String thisType = enumer.nextElement();
				if (thisCategory.isA(thisType)) {
					num_matches++;
				}
			}

			// are we applying an AND operation?
			if (_ANDoperation) {
				// how many did we find?
				if (num_matches == _myTargets.size())
					res = true;
			} else {
				// ahh, we must be using an OR operation
				if (num_matches > 0)
					res = true;
			}
		}

		return res;
	}

	public void removeTargetType(final String val) {
		if (_myTargets == null)
			_myTargets = new java.util.Vector<String>(1, 1);

		if (_myTargets != null)
			_myTargets.remove(val);
	}

	/**
	 * specify whether we need to match 'all' attributes (AND) or 'some' attributes
	 * (OR)
	 *
	 */
	public void setANDOperation(final boolean val) {
		_ANDoperation = val;
	}

	@Override
	public String toString() {
		final StringBuffer res = new StringBuffer();
		final Iterator<String> it = _myTargets.iterator();
		while (it.hasNext()) {
			final String thisCat = it.next();
			res.append(thisCat);
			res.append(", ");
		}

		return res.toString();
	}

}