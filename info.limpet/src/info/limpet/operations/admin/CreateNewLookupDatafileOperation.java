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
package info.limpet.operations.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.unit.Unit;

import org.eclipse.january.MetadataException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.arithmetic.BinaryQuantityOperation;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

public class CreateNewLookupDatafileOperation extends BinaryQuantityOperation {

	public class NewIndexedLookupDatasetCommand extends BinaryQuantityCommand {
		private final NumberDocument _subject;
		private final NumberDocument _lookup;

		public NewIndexedLookupDatasetCommand(final String name, final List<IStoreItem> selection,
				final IStoreGroup store, final IContext context) {
			this(name, selection, store, null, context);
		}

		public NewIndexedLookupDatasetCommand(final String name, final List<IStoreItem> selection,
				final IStoreGroup destination, final IDocument<?> timeProvider, final IContext context) {
			super(name, "Use lookup to generate dataset", destination, false, false, selection, timeProvider, context);

			_subject = (NumberDocument) selection.get(0);
			_lookup = (NumberDocument) selection.get(1);
		}

		@Override
		protected void assignOutputIndices(final IDataset output, final Dataset outputIndices) {
			// ok, we don't do this, we want to take charge of the output
			// indices
		}

		@Override
		protected String getBinaryNameFor(final String name1, final String name2) {
			return "Lookup derived by retrieving " + _subject.getName() + "'s " + _subject.getUnits() + " values from "
					+ name2;
		}

		@Override
		protected Unit<?> getBinaryOutputUnit(final Unit<?> first, final Unit<?> second) {
			throw new IllegalArgumentException("Shouldn't be callign this. We don't use it");
		}

		@Override
		protected Unit<?> getIndexUnits() {
			return _subject.getIndexUnits();
		}

		@Override
		protected IOperationPerformer getOperation() {
			return null;
		}

		@Override
		protected Unit<?> getUnits() {
			return _lookup.getUnits();
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate
		 * it from the core "execute" operation in order to support dynamic
		 * updates. That is, we need to create it when run initially, then
		 * re-generate it on data updates
		 *
		 * @param unit the units to use
		 * @param outputs the list of output series
		 */
		@Override
		protected IDataset performCalc() {
			DoubleDataset newVals = null;

			// big picture: we're going to produce a new dataset by
			// loops through through the first dataset, looks up the value in
			// teh
			// lookup dataset, then inserts the new value at that time.

			try {
				// find the times for the output
				final List<AxesMetadata> allAxes = _subject.getDataset().getMetadata(AxesMetadata.class);
				final AxesMetadata subjectIndices = allAxes != null ? allAxes.get(0) : null;
				final DoubleDataset subjectValues = (DoubleDataset) _subject.getDataset();

				// collate the data for the interpolation
				AxesMetadata lookupIndices;
				lookupIndices = _lookup.getDataset().getMetadata(AxesMetadata.class).get(0);
				final DoubleDataset lookupIndex = (DoubleDataset) lookupIndices.getAxis(0)[0];
				final DoubleDataset lookupValues = (DoubleDataset) _lookup.getDataset();

				// find the first/last values, use them as "left" and "right" in
				// the
				// interpolation
				final double left = lookupValues.get(0);
				final double right = lookupValues.get(lookupValues.getSize() - 1);

				newVals = (DoubleDataset) Maths.interpolate(lookupIndex, lookupValues, subjectValues, left, right);

				// ok, now put the indices back in, if we have any
				if (subjectIndices != null) {
					newVals.clearMetadata(AxesMetadata.class);
					newVals.addMetadata(subjectIndices);
				}
			} catch (final MetadataException e) {
				e.printStackTrace();
			}

			// done
			return newVals;
		}

		@Override
		protected void tidyOutput(final NumberDocument output) {
			super.tidyOutput(output);
		}
	}

	@Override
	public List<ICommand> actionsFor(final List<IStoreItem> selection, final IStoreGroup destination,
			final IContext context) {
		final List<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection)) {
			final Map<Unit<?>, Integer> matches = new HashMap<Unit<?>, Integer>();
			// ok, it's worth persevering with
			for (final IStoreItem t : selection) {
				final NumberDocument nd = (NumberDocument) t;
				final Unit<?> myU = nd.getUnits();
				final Unit<?> myI = nd.getIndexUnits();

				addIfNecessary(matches, myU);
				addIfNecessary(matches, myI);
			}

			// did it work?
			final List<Unit<?>> twoPresent = new ArrayList<Unit<?>>();
			for (final Unit<?> t : matches.keySet()) {
				final Integer count = matches.get(t);
				if (count == 2) {
					twoPresent.add(t);
				}
			}

			for (final Unit<?> thisU : twoPresent) {
				// ok, find the permutations for this
				final NumberDocument d1 = (NumberDocument) selection.get(0);
				final NumberDocument d2 = (NumberDocument) selection.get(1);

				if (d1.getUnits().equals(thisU) && d2.getIndexUnits() != null && d2.getIndexUnits().equals(thisU)) {
					addInterpolatedCommands(selection, destination, res, context);
				} else if (d2.getUnits().equals(thisU) && d1.getIndexUnits() != null
						&& d1.getIndexUnits().equals(thisU)) {
					// ok, reverse the selection
					selection.clear();
					selection.add(d2);
					selection.add(d1);
					addInterpolatedCommands(selection, destination, res, context);
				}
			}
		}
		return res;
	}

	private void addIfNecessary(final Map<Unit<?>, Integer> matches, final Unit<?> myU) {
		final Integer ct = matches.get(myU);
		if (ct == null) {
			matches.put(myU, 1);
		} else {
			matches.put(myU, ct + 1);
		}
	}

	@Override
	protected void addIndexedCommands(final List<IStoreItem> selection, final IStoreGroup destination,
			final Collection<ICommand> res, final IContext context) {
		throw new RuntimeException("This operation doesn't support indexed operations");
	}

	@Override
	protected void addInterpolatedCommands(final List<IStoreItem> selection, final IStoreGroup destination,
			final Collection<ICommand> res, final IContext context) {

		final NumberDocument subject = (NumberDocument) selection.get(0);
		final NumberDocument lookup = (NumberDocument) selection.get(1);
		final ICommand newC = new NewIndexedLookupDatasetCommand(
				"Create new document by retrieving " + subject.getUnits() + " from " + lookup, selection, destination,
				context);
		res.add(newC);
	}

	@Override
	protected boolean appliesTo(final List<IStoreItem> selection) {
		final boolean nonEmpty = getATests().nonEmpty(selection);
		final boolean correctNum = getATests().exactNumber(selection, 2);
		final boolean allQuantity = getATests().allQuantity(selection);
		final boolean allIndexed = getATests().allIndexedOrSingleton(selection);

		return nonEmpty && correctNum && allQuantity && allIndexed;
	}

}
