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
package org.mwc.debrief.limpet_integration.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;

public class TrackConverter {

	protected abstract static class DataChoiceProvider {
		protected final String _name;
		protected final String _units;

		DataChoiceProvider(final String name, final String units) {
			_name = name;
			_units = units;
		}

		abstract List<Dataset> getDatasets(WatchableList track);

		abstract List<ScatterSet> getScatterSets(WatchableList track);

		@Override
		public String toString() {
			return _name;
		}
	}

	private abstract static class StateChoiceProvider extends DataChoiceProvider {

		StateChoiceProvider(final String name, final String units) {
			super(name, units);
		}

		@Override
		List<Dataset> getDatasets(final WatchableList track) {
			List<Dataset> res = null;

			final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

			final Dataset dataset = factory.createDataset();
			dataset.setName(track.getName() + "-" + _name);
			dataset.setUnits(_units);
			final PlainStyling ps = factory.createPlainStyling();
			ps.setColor(track.getColor());
			ps.setLineThickness(2.0d);
			dataset.setStyling(ps);

			final Collection<Editable> elements = track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
			final Iterator<Editable> items = elements.iterator();
			while (items.hasNext()) {
				final FixWrapper fix = (FixWrapper) items.next();
				final double dependent = valueFor(fix);

				final DataItem item = factory.createDataItem();
				item.setIndependentVal(fix.getDateTimeGroup().getDate().getTime());
				item.setDependentVal(dependent);

				dataset.getMeasurements().add(item);
			}

			if (!dataset.getMeasurements().isEmpty()) {
				if (res == null) {
					res = new ArrayList<Dataset>();
				}
				res.add(dataset);
			}

			return res;
		}

		@Override
		List<ScatterSet> getScatterSets(final WatchableList track) {
			List<ScatterSet> res = null;

			final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

			final ScatterSet scatter = factory.createScatterSet();
			scatter.setName(track.getName() + "-" + _name);
			final PlainStyling ps = factory.createPlainStyling();
			ps.setColor(track.getColor());
			ps.setLineThickness(2.0d);

			final Collection<Editable> elements = track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
			final Iterator<Editable> items = elements.iterator();
			while (items.hasNext()) {
				final FixWrapper fix = (FixWrapper) items.next();
				final Datum item = factory.createDatum();
				item.setVal(fix.getDateTimeGroup().getDate().getTime());

				scatter.getDatums().add(item);
			}

			if (scatter.getDatums().size() > 0) {
				if (res == null) {
					res = new ArrayList<ScatterSet>();
				}
				res.add(scatter);
			}

			return res;
		}

		abstract protected double valueFor(FixWrapper fix);
	}

	private Object[] _previousChoices = null;

	public TrackConverter() {
	}

	public List<Dataset> convertToDataset(final WatchableList list) {
		List<Dataset> res = new ArrayList<Dataset>();

		// have a look at the type
		final DataChoiceProvider[] choices = new DataChoiceProvider[] { new StateChoiceProvider("Course", "degs") {
			@Override
			protected double valueFor(final FixWrapper fix) {
				return fix.getCourseDegs();
			}
		}, new StateChoiceProvider("Speed", "kts") {
			@Override
			protected double valueFor(final FixWrapper fix) {
				return fix.getSpeed();
			}
		}, new StateChoiceProvider("Depth", "m") {
			@Override
			protected double valueFor(final FixWrapper fix) {
				return fix.getLocation().getDepth();
			}
		} };

		final Shell shell = Display.getCurrent().getActiveShell();
		final ListSelectionDialog dialog = new ListSelectionDialog(shell, choices, ArrayContentProvider.getInstance(),
				new LabelProvider(), "Please choose which data to display");

		dialog.setTitle("Loading track:" + list.getName());

		if (_previousChoices != null) {
			dialog.setInitialSelections(_previousChoices);
		}

		dialog.open();

		_previousChoices = dialog.getResult();

		if (_previousChoices != null) {

			for (int i = 0; i < _previousChoices.length; i++) {
				final DataChoiceProvider provider = (DataChoiceProvider) _previousChoices[i];
				final List<Dataset> datasets = provider.getDatasets(list);

				if (datasets != null && datasets.size() > 0)
					if (res == null) {
						res = new ArrayList<Dataset>();
					}
				res.addAll(datasets);

			}
		}
		return res;
	}

	public List<ScatterSet> convertToScatterset(final WatchableList list) {
		List<ScatterSet> res = new ArrayList<ScatterSet>();

		// have a look at the type
		final DataChoiceProvider[] choices = new DataChoiceProvider[] { new StateChoiceProvider("Course", "degs") {
			@Override
			protected double valueFor(final FixWrapper fix) {
				return fix.getCourseDegs();
			}
		}, new StateChoiceProvider("Speed", "kts") {
			@Override
			protected double valueFor(final FixWrapper fix) {
				return fix.getSpeed();
			}
		}, new StateChoiceProvider("Depth", "m") {
			@Override
			protected double valueFor(final FixWrapper fix) {
				return fix.getLocation().getDepth();
			}
		} };

		final Shell shell = Display.getCurrent().getActiveShell();
		final ListSelectionDialog dialog = new ListSelectionDialog(shell, choices, ArrayContentProvider.getInstance(),
				new LabelProvider(), "Please choose which data to display");

		dialog.setTitle("Loading track:" + list.getName());

		if (_previousChoices != null) {
			dialog.setInitialSelections(_previousChoices);
		}

		dialog.open();

		_previousChoices = dialog.getResult();

		if (_previousChoices != null) {

			for (int i = 0; i < _previousChoices.length; i++) {
				final DataChoiceProvider provider = (DataChoiceProvider) _previousChoices[i];
				final List<ScatterSet> datasets = provider.getScatterSets(list);

				if (datasets != null && datasets.size() > 0)
					if (res == null) {
						res = new ArrayList<ScatterSet>();
					}
				res.addAll(datasets);

			}
		}
		return res;
	}

}
