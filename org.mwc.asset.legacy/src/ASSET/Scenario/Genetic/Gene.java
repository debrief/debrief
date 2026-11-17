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

package ASSET.Scenario.Genetic;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;

import org.w3c.dom.Element;

import ASSET.Util.MonteCarlo.XMLVariance;
import ASSET.Util.MonteCarlo.XMLVarianceList;

public class Gene implements Comparable<Gene> {

	public static class ReverseComparator implements Comparator<Gene> {
		@Override
		public int compare(final Gene o1, final Gene o2) {
			int res = 0;
			final double fit1 = o1.getFitness();
			final double fit2 = o2.getFitness();
			if (o2 == o1)
				res = 0;
			else if (fit1 < fit2)
				res = 1;
			else
				res = -1;

			return res;
		}
	}

	/**
	 * formatter for the fitness function
	 */
	static private java.text.DecimalFormat _formatter = new java.text.DecimalFormat("000.0000");

	/**
	 * testing method, which returns a plain gene without having to read in data,
	 * etc
	 *
	 * @return a blank Gene
	 */
	final public static Gene getGeneForTesting() {
		return new Gene();
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/
	/**
	 * the list of variables (chromosomes) we represent
	 */
	XMLVarianceList _myList = null;

	/**
	 * the text file containing our scenario
	 */
	private final String _newDocument = null;

	/**
	 * our fitness figure (or -1 if we haven't got one)
	 */
	private double _myFitness = ScenarioRunner.ScenarioOutcome.INVALID_SCORE;

	/**
	 * the name of this gene
	 */
	private String _myName;

	/**
	 * summary of the performance of this gene
	 */
	private String _mySummary;

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * internal constructor - private since we need valid data in order to test
	 */
	private Gene() {
		this(new XMLVarianceList());
	}

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */
	public Gene(final Element varianceElement, final InputStream inputVary) {
		this(new XMLVarianceList());
		_myList.loadFrom(varianceElement);
	}

	/**
	 * copy constructor, using deep copy
	 */
	private Gene(final XMLVarianceList variables) {
		_myFitness = ScenarioRunner.ScenarioOutcome.INVALID_SCORE;
		if (variables != null) {
			_myList = (XMLVarianceList) variables.clone();
		}
	}

	@Override
	public int compareTo(final Gene o2) {
		int res = 0;
		final double fit1 = getFitness();
		final double fit2 = o2.getFitness();
		if (o2 == this)
			res = 0;
		else if (fit1 > fit2)
			res = 1;
		else
			res = -1;

		return res;
	}

	/**
	 * create a new permutation of this gene
	 */
	public Gene createRandom() {
		final Gene res = new Gene();
		res._myList = (XMLVarianceList) this._myList.clone();

		final Iterator<XMLVariance> it = res._myList.getIterator();
		while (it.hasNext()) {
			final XMLVariance variable = it.next();
			final XMLVariance new_var = (XMLVariance) variable.clone();
			new_var.randomise();
			res._myList.add(new_var);
		}

		return res;
	}

	/**
	 * comparison operator
	 */
	@Override
	public boolean equals(final Object arg0) {
		if (arg0 == null)
			return false;

		final Gene other = (Gene) arg0;

		boolean res = true;
		if (_myList.size() != other._myList.size()) {
			res = false;
		} else {
			for (int i = 0; i < _myList.size(); i++) {
				final Iterator<XMLVariance> it_a = _myList.getIterator();
				final Iterator<XMLVariance> it_b = other._myList.getIterator();

				final XMLVariance mine = it_a.next();
				final XMLVariance his = it_b.next();

				final boolean eq = mine.equals(his);

				if (!eq) {
					res = false;
					break;
				}
			}
		}

		return res;
	}

	/**
	 * get the list of variables
	 */
	public XMLVarianceList getChromosomes() {
		return _myList;
	}

	public String getDocument() {
		// if(_newDocument == null)
		// {
		// _newDocument = _myList.getNewPermutation();
		// }
		// todo: reinstate this
		return _newDocument;
	}

	public double getFitness() {
		return _myFitness;
	}

	private String getName() {
		return _myName;
	}

	public String getSummary() {
		return _mySummary;
	}

	/**
	 * merge this gene with the one supplied
	 *
	 * @param other       Gene to merge with
	 * @param probability threshold above which we merge a chromosome (0..1)
	 */
	public void mergeWith(final Gene other, final double probability) {
		// merge random genes
		final Iterator<XMLVariance> it_a = _myList.getIterator();
		final Iterator<XMLVariance> it_b = other._myList.getIterator();
		while (it_a.hasNext()) {
			final XMLVariance my_next = it_a.next();
			final XMLVariance his_next = it_b.next();

			final double nextP = ASSET.Util.RandomGenerator.nextRandom();
			if (nextP <= probability) {
				my_next.merge(his_next);
			}
		}
	}

	public void setFitness(final double val, final String summary) {
		_myFitness = val;
		_mySummary = summary;
	}

	public void setName(final String name) {
		this._myName = name;
	}

	@Override
	public String toString() {

		String res = "Fit:" + _formatter.format(getFitness()) + " || ";
		res += "Summary:" + getSummary() + " || ";
		final Iterator<XMLVariance> it = _myList.getIterator();
		while (it.hasNext()) {
			final XMLVariance variable = it.next();
			res += variable.getName() + ":" + variable.getValue() + "| ";
		}

		return res + " " + getName();
	}

}
