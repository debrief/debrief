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
/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;

public abstract class QuantityFrequencyBins extends CoreAnalysis {
	public static class Bin {
		private final double lowerVal;
		private final double upperVal;
		private final long freqVal;

		public Bin(final double lower, final double upper, final long freq) {
			upperVal = upper;
			lowerVal = lower;
			freqVal = freq;
		}

		public long getFreqVal() {
			return freqVal;
		}

		public double getLowerVal() {
			return lowerVal;
		}

		public double getUpperVal() {
			return upperVal;
		}
	}

	public static class BinnedData extends ArrayList<Bin> {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public BinnedData() {
		}
	}

	public static final int DEFAULT_NUM_BINS = 10;
	public static final int MIN_NUM_BINS = 4;
	private static final int MAX_SIZE = 10000;

	private static final double THRESHOLD_VALUE = 0.001;

	public static BinnedData binTheseValues(final double[] data) {
		// Get a DescriptiveStatistics instance
		final DescriptiveStatistics stats = new DescriptiveStatistics(data);

		// also do some frequency binning
		final double range = stats.getMax() - stats.getMin();

		// aah, double-check we don't have zero range
		final int binCount;
		if (range > DEFAULT_NUM_BINS) {
			binCount = DEFAULT_NUM_BINS;
		} else if (range > MIN_NUM_BINS) {
			binCount = (int) range;
		} else {
			binCount = MIN_NUM_BINS;
		}

		final BinnedData res = new BinnedData();

		if (range > THRESHOLD_VALUE) {

			final long[] histogram = new long[binCount];
			final EmpiricalDistribution distribution = new EmpiricalDistribution(binCount);
			distribution.load(data);

			int k = 0;
			for (final SummaryStatistics sStats : distribution.getBinStats()) {
				histogram[k++] = sStats.getN();
			}

			double rangeSoFar = stats.getMin();
			final double rangeStep = range / binCount;
			for (int i = 0; i < histogram.length; i++) {
				final long l = histogram[i];
				res.add(new Bin(rangeSoFar, rangeSoFar + rangeStep, l));
				rangeSoFar += rangeStep;
			}
		}
		return res;
	}

	public static BinnedData doBins(final NumberDocument collection) {
		// collate the values into an array
		final double[] data = new double[collection.size()];

		// Add the data from the array
		int ctr = 0;
		for (int i = 0; i < collection.size(); i++) {
			data[ctr++] = collection.getValueAt(i);
		}

		return binTheseValues(data);
	}

	private final CollectionComplianceTests aTests;

	public QuantityFrequencyBins() {
		super("Quantity Frequency Bins");
		aTests = new CollectionComplianceTests();
	}

	@Override
	public void analyse(final List<IStoreItem> selection) {
		final List<String> titles = new ArrayList<String>();
		final List<String> values = new ArrayList<String>();

		// check compatibility
		if (appliesTo(selection) && selection.size() == 1) {
			// ok, let's go for it.
			for (final Iterator<IStoreItem> iter = selection.iterator(); iter.hasNext();) {
				final NumberDocument thisC = (NumberDocument) iter.next();

				if (thisC.size() > 1 && thisC.size() < MAX_SIZE) {
					final BinnedData res = doBins(thisC);

					// now output the bins
					final StringBuffer freqBins = new StringBuffer();

					final Iterator<Bin> bIter = res.iterator();
					while (bIter.hasNext()) {
						final QuantityFrequencyBins.Bin bin = bIter.next();
						freqBins.append((int) bin.getLowerVal());
						freqBins.append("-");
						freqBins.append((int) bin.getUpperVal());
						freqBins.append(": ");
						freqBins.append(bin.getFreqVal());
						freqBins.append(", ");

					}

					titles.add("Frequency bins");
					values.add(freqBins.toString());
				}
			}
		}

		if (titles.size() > 0) {
			presentResults(titles, values);
		}

	}

	private boolean appliesTo(final List<IStoreItem> selection) {
		return aTests.allCollections(selection) && aTests.allQuantity(selection) && aTests.allEqualUnits(selection)
				&& aTests.allOneDim(selection);
	}

	protected abstract void presentResults(List<String> titles, List<String> values);
}
