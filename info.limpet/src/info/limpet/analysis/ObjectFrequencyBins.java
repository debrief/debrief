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

import org.apache.commons.math3.stat.Frequency;

import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.operations.CollectionComplianceTests;

public abstract class ObjectFrequencyBins extends CoreAnalysis {
	public static class Bin {
		private final Object indexVal;
		private final long freqVal;

		public Bin(final Object index, final long freq) {
			indexVal = index;
			freqVal = freq;
		}

		public long getFreqVal() {
			return freqVal;
		}

		public Object getIndexVal() {
			return indexVal;
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

	private static final int MAX_SIZE = 2000;

	public static BinnedData doBins(final IDocument<?> collection) {

		// build up the histogram
		final Frequency freq = new Frequency();
		final Iterator<?> iter2 = collection.getIterator();
		while (iter2.hasNext()) {
			final Object object = iter2.next();
			freq.addValue(object.toString());
		}

		final BinnedData res = new BinnedData();

		final Iterator<Comparable<?>> vIter = freq.valuesIterator();
		while (vIter.hasNext()) {
			final Comparable<?> value = vIter.next();
			res.add(new Bin(value, freq.getCount(value)));
		}

		return res;
	}

	private final CollectionComplianceTests aTests = new CollectionComplianceTests();

	public ObjectFrequencyBins() {
		super("Quantity Frequency Bins");
	}

	@Override
	public void analyse(final List<IStoreItem> selection) {
		final List<String> titles = new ArrayList<String>();
		final List<String> values = new ArrayList<String>();

		// check compatibility
		if (appliesTo(selection) && selection.size() == 1) {
			// ok, let's go for it.
			for (final Iterator<IStoreItem> iter = selection.iterator(); iter.hasNext();) {
				final IDocument<?> thisC = (IDocument<?>) iter.next();

				if (thisC.size() <= MAX_SIZE) {
					final BinnedData res = doBins(thisC);

					titles.add("Unique values");
					values.add(res.size() + "");

					final StringBuffer freqBins = new StringBuffer();

					final Iterator<Bin> bIter = res.iterator();
					while (bIter.hasNext()) {
						final ObjectFrequencyBins.Bin bin = bIter.next();
						freqBins.append(bin.getIndexVal());
						freqBins.append(':');
						freqBins.append(bin.getFreqVal());
						freqBins.append(", ");
					}
					titles.add("Frequency");
					values.add(freqBins.toString());
				}
			}
		}

		if (titles.size() > 0) {
			presentResults(titles, values);
		}

	}

	private boolean appliesTo(final List<IStoreItem> selection) {
		return aTests.allCollections(selection) && aTests.allNonQuantity(selection) && aTests.allNonLocation(selection)
				&& aTests.allOneDim(selection);
	}

	protected abstract void presentResults(List<String> titles, List<String> values);
}
