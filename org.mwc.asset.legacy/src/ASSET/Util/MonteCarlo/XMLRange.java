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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Vector;

import org.w3c.dom.Element;

import ASSET.Util.RandomGenerator;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public final class XMLRange implements XMLOperation {

	/**
	 * the min value
	 */
	double _min;

	/**
	 * the max value
	 */
	double _max;

	/**
	 * the step to use
	 */
	private Double _step;

	/**
	 * how many random permutations to use
	 */
	Integer _numPerms;

	/**
	 * an (optional) list of random values to use when the user has specified how
	 * many permutations are required
	 *
	 */
	Vector<Double> _myPerms;

	/**
	 * the text format to use
	 */
	private java.text.DecimalFormat _format;

	/**
	 * the current value of this operation
	 */
	double _currentValue;

	/**
	 * the random generator we're going to use
	 *
	 */
	int _model;

	/**
	 * testing constructor
	 *
	 */
	XMLRange() {
		// have to set the values using the assign method
	}

	/**
	 * ************************************************************ constructor *
	 *
	 * @throws ParseException ************************************************************
	 */
	public XMLRange(final Element element) throws ParseException {
		// get the data items
		final double min = MWCXMLReader.readThisDouble(element.getAttribute("min"));
		final double max = MWCXMLReader.readThisDouble(element.getAttribute("max"));

		// see if the step size has been set
		final String stepStr = element.getAttribute("step");
		Double step;
		if (stepStr.length() > 0) {
			step = new Double(stepStr);
		} else
			step = null;

		// now the number format
		final String formatStr = element.getAttribute("format");
		DecimalFormat format;
		if (formatStr.length() > 0)
			format = new java.text.DecimalFormat(formatStr);
		else
			format = null;

		// see if the user has specified num perms
		final String perms = element.getAttribute("number_permutations");
		Integer numPerms;
		if (perms.length() > 0) {
			numPerms = new Integer(perms);
		} else
			numPerms = null;

		// and see if there is a random model
		final String modelStr = element.getAttribute("RandomModel");
		int model;
		if (modelStr.length() > 0) {
			model = XMLVariance.getModelFromString(modelStr);
		} else
			model = RandomGenerator.UNIFORM;

		assign(max, min, model, numPerms, step, format);

	}

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * default constructor, used in cloning operation
	 */
	private XMLRange(final XMLRange other) {
		assign(other._max, other._min, other._model, other._numPerms, other._step, other._format);
	}

	/**
	 * store the values in this object - used for refactoring & to help testing
	 *
	 */
	void assign(final double max, final double min, final int model, final Integer numPerms, final Double step,
			final DecimalFormat format) {
		_max = max;
		_min = min;
		_numPerms = numPerms;
		_step = step;
		_format = format;
		_model = model;

		// and generate a new random permutation
		newPermutation();
	}

	/**
	 * clone this object
	 */
	@Override
	public final Object clone() {
		final XMLRange res = new XMLRange(this);

		// but put the current value back in
		res._currentValue = this._currentValue;

		return res;
	}

	public final String getFormat() {
		return _format.toPattern();
	}

	public final double getMax() {
		return _max;
	}

	//////////////////////////////////////////////////////////////////////
	// getter/setter methods
	//////////////////////////////////////////////////////////////////////
	public final double getMin() {
		return _min;
	}

	/**
	 * helper method which generates a random number according to the user-settings
	 *
	 * @return a new random number appropriate to the current model, step size,
	 *         format
	 */
	private double getNextRandom() {
		double res;

		// has a step size been defined?
		if (_step != null) {
			// yes, create a random number using the supplied step size
			final int num_steps = (int) ((_max - _min) / _step.doubleValue());
			final int rnd = (int) (ASSET.Util.RandomGenerator.nextRandom() * num_steps);
			final double newVal = _min + rnd * _step.doubleValue();
			res = newVal;
		} else {
			// nope, just get a completely random one
			res = ASSET.Util.RandomGenerator.generateRandomNumber(_min, _max, _model);
		}
		return res;
	}

	public final Double getStep() {
		return _step;
	}

	/**
	 * return the current value of this permutation
	 */
	@Override
	public final String getValue() {
		final String res;
		if (_format == null) {
			res = "" + _currentValue;
		} else {
			res = _format.format(_currentValue);
		}

		return res;
	}

	/**
	 * merge ourselves with the supplied operation
	 */
	@Override
	public final void merge(final XMLOperation other) {
		final XMLRange xr = (XMLRange) other;
		_currentValue = xr._currentValue;

		// add a little random variance
		final double variance = 0.05 * (_max - _min);
		final double var_2 = 2 * variance;
		final double rand = ASSET.Util.RandomGenerator.nextRandom();
		_currentValue += (var_2) * rand - (var_2);

		// check we're still in range
		_currentValue = Math.max(_currentValue, _min);
		_currentValue = Math.min(_currentValue, _max);

		// double sep = xr._currentValue - _currentValue;
		// double newVal = _currentValue + sep / 2;
		// _currentValue = newVal;
	}

	/**
	 * produce a new value for this operation
	 */
	@Override
	public final void newPermutation() {
		// does the user want a restricted number of permutations?
		if (_numPerms != null) {
			// yes. Has the list of perms been defined?
			if (_myPerms == null) {
				_myPerms = new Vector<Double>(_numPerms.intValue(), 1);

				// nope, better create it
				for (int i = 0; i < _numPerms.intValue(); i++) {
					final double newVal = getNextRandom();
					_myPerms.add(new Double(newVal));
				}
			}

			// ok, we've got our list now.
			final int index = (int) (ASSET.Util.RandomGenerator.nextRandom() * _myPerms.size());
			final Double thisVal = _myPerms.get(index);
			_currentValue = thisVal.doubleValue();

		} else {
			// nope, just pull one out of the ether...
			_currentValue = getNextRandom();
		}
	}

}
