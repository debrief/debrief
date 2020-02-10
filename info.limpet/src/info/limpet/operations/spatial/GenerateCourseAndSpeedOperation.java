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
package info.limpet.operations.spatial;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

public class GenerateCourseAndSpeedOperation implements IOperation {

	protected abstract static class DistanceOperation extends AbstractCommand {

		public DistanceOperation(final List<IStoreItem> selection, final IStoreGroup store, final String title,
				final String description, final IContext context) {
			super(title, description, store, false, false, selection, context);
		}

		protected abstract void calcAndStore(NumberDocumentBuilder thisOut, final IGeoCalculator calc, final long timeA,
				final Point2D locA, final long timeB, final Point2D locB);

		@Override
		public void execute() {
			// get the unit
			final List<Document<?>> outputs = new ArrayList<Document<?>>();

			// ok, generate the new series
			for (int i = 0; i < getInputs().size(); i++) {
				final LocationDocument thisInput = (LocationDocument) getInputs().get(i);
				final String name = getOutputName(thisInput.getName());
				final Unit<?> units = getUnits();

				final DoubleDataset res = (DoubleDataset) performCalc(thisInput, name, units);

				final NumberDocument doc = new NumberDocument(res, this, units);

				doc.setIndexUnits(thisInput.getIndexUnits());

				outputs.add(doc);
				// store the output
				super.addOutput(doc);

				doc.fireDataChanged();
			}

			// tell each series that we're a dependent
			final Iterator<IStoreItem> iter = getInputs().iterator();
			while (iter.hasNext()) {
				final IDocument<?> iCollection = (IDocument<?>) iter.next();
				iCollection.addDependent(this);
			}

			final IStoreGroup theStore = getStore();
			for (final Document<?> thisO : outputs) {
				theStore.add(thisO);
			}
		}

		/**
		 * produce a name for the output
		 *
		 * @param string
		 *
		 * @return
		 */
		abstract protected String getOutputName(final String name);

		protected abstract Unit<?> getUnits();

		/**
		 * wrap the actual operation. We're doing this since we need to separate
		 * it from the core "execute" operation in order to support dynamic
		 * updates
		 *
		 * @param unit
		 * @param outputs
		 */
		private IDataset performCalc(final LocationDocument thisTrack, final String name, final Unit<?> units) {
			// get a calculator to use
			final IGeoCalculator calc = thisTrack.getCalculator();

			final NumberDocumentBuilder builder = new NumberDocumentBuilder(name, units, this, SampleData.MILLIS);

			// get the objects
			final ObjectDataset od = (ObjectDataset) thisTrack.getDataset();

			// get the time indices
			final AxesMetadata am = od.getFirstMetadata(AxesMetadata.class);
			if (am == null) {
				throw new IllegalArgumentException("Index metadata missing for this dataset");
			}
			final DoubleDataset amd = (DoubleDataset) am.getAxis(0)[0];

			if (amd == null) {
				// ok, failed - drop out
				throw new RuntimeException("Failed to slice lazy datset");
			}

			// remember the last value
			long lastTime = 0;
			Point2D lastLocation = null;

			for (int i = 0; i < od.getSize(); i++) {
				final Point2D thisP = (Point2D) od.getObject(i);
				final long thisT = amd.getLong(i);

				if (lastLocation != null) {
					calcAndStore(builder, calc, lastTime, lastLocation, thisT, thisP);
				}

				// and remember the values
				lastLocation = thisP;
				lastTime = thisT;

			}

			return builder.toDocument().getDataset();

		}

		/**
		 * for unitary operations we only act on a single input. We may be
		 * acting on an number of datasets, so find the relevant one, and
		 * re-calculate it
		 */
		@Override
		protected void recalculate(final IStoreItem subject) {
			// TODO: change logic, we should only re-generate the
			// single output

			// workaround: we don't know which output derives
			// from this input. So, we will have to regenerate
			// all outputs

			final Iterator<Document<?>> oIter = getOutputs().iterator();

			// we may be acting separately on multiple inputs.
			// so, loop through them
			for (final IStoreItem input : getInputs()) {
				final LocationDocument inputDoc = (LocationDocument) input;
				final NumberDocument outputDoc = (NumberDocument) oIter.next();

				// start adding values.
				final IDataset dataset = performCalc(inputDoc, outputDoc.getName(), outputDoc.getUnits());

				// store the data
				outputDoc.setDataset(dataset);

				// and fire out the update
				outputDoc.fireDataChanged();
			}
		}
	}

	private final CollectionComplianceTests aTests = new CollectionComplianceTests();

	@Override
	public List<ICommand> actionsFor(final List<IStoreItem> selection, final IStoreGroup destination,
			final IContext context) {
		final List<ICommand> res = new ArrayList<ICommand>();
		if (appliesTo(selection)) {

			final int len = selection.size();
			final String title;
			if (len > 1) {
				title = "Generate course for track";
			} else {
				title = "Generate course for tracks";
			}

			final ICommand genCourse = new DistanceOperation(selection, destination, "Generate calculated course",
					title, context) {
				@Override
				protected void calcAndStore(final NumberDocumentBuilder target, final IGeoCalculator calc,
						final long lastTime, final Point2D locA, final long thisTime, final Point2D locB) {

					// now find the bearing between them
					double angleDegs = calc.getAngleBetween(locA, locB);
					if (angleDegs < 0) {
						angleDegs += 360;
					}

					target.add(thisTime, angleDegs);
				}

				@Override
				protected String getOutputName(final String name) {
					return getContext().getInput("Generate course", "Please provide a dataset prefix",
							"Generated course for " + name);
				}

				@Override
				protected Unit<?> getUnits() {
					return SampleData.DEGREE_ANGLE;
				}
			};
			final ICommand genSpeed = new DistanceOperation(selection, destination, "Generate calculated speed", title,
					context) {
				@Override
				protected void calcAndStore(final NumberDocumentBuilder target, final IGeoCalculator calc,
						final long lastTime, final Point2D locA, final long thisTime, final Point2D locB) {
					// now find the range between them
					final double thisDist = calc.getDistanceBetween(locA, locB);
					final double calcTime = thisTime - lastTime;
					final double thisSpeed = thisDist / (calcTime / 1000d);
					target.add(thisTime, thisSpeed);
				}

				@Override
				protected String getOutputName(final String name) {
					return getContext().getInput("Generate speed", "Please provide a dataset prefix",
							"Generated speed for " + name);
				}

				@Override
				protected Unit<?> getUnits() {
					return METRE.divide(SECOND).asType(Velocity.class);
				}
			};

			res.add(genCourse);
			res.add(genSpeed);
		}

		return res;
	}

	protected boolean appliesTo(final List<IStoreItem> selection) {
		final boolean nonEmpty = aTests.nonEmpty(selection);
		final boolean allTemporal = aTests.allEqualIndexed(selection);

		final boolean allNonQuantity = aTests.allNonQuantity(selection);
		final boolean allLocation = aTests.allLocation(selection);
		final boolean allSameDistanceUnits = aTests.allEqualDistanceUnits(selection);

		return nonEmpty && allTemporal && allNonQuantity && allLocation && allSameDistanceUnits;
	}
}
