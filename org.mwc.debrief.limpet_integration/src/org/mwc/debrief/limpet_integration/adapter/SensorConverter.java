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
import java.util.Enumeration;
import java.util.List;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl;

public class SensorConverter {

	public SensorConverter() {
	}

	public List<Dataset> convertToDataset(final SensorWrapper sensor) {
		final List<Dataset> res = new ArrayList<Dataset>();

		final List<Dataset> datasets = getDatasets(sensor);

		if (datasets != null && datasets.size() > 0)
			res.addAll(datasets);

		return res;
	}

	public List<ScatterSet> convertToScatterset(final SensorWrapper sensor) {
		final List<ScatterSet> res = new ArrayList<ScatterSet>();

		final List<ScatterSet> datasets = getScatterset(sensor);

		if (datasets != null && datasets.size() > 0)
			res.addAll(datasets);

		return res;
	}

	List<Dataset> getDatasets(final SensorWrapper sensor) {
		List<Dataset> res = null;

		final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

		final Dataset dataset = factory.createDataset();
		dataset.setName(sensor.getName());
		dataset.setUnits("degs");
		final PlainStyling ps = factory.createPlainStyling();
		ps.setColor(sensor.getColor());
		ps.setLineThickness(2.0d);
		dataset.setStyling(ps);

		final Enumeration<Editable> enumer = sensor.elements();
		while (enumer.hasMoreElements()) {
			final SensorContactWrapper cut = (SensorContactWrapper) enumer.nextElement();
			final DataItem item = factory.createDataItem();
			item.setIndependentVal(cut.getTime().getDate().getTime());
			item.setDependentVal(cut.getBearing());

			// and store it
			dataset.getMeasurements().add(item);
		}

		// did we find any?
		if (!dataset.getMeasurements().isEmpty()) {
			if (res == null) {
				res = new ArrayList<Dataset>();
			}
			res.add(dataset);
		}

		return res;
	}

	List<ScatterSet> getScatterset(final SensorWrapper sensor) {
		List<ScatterSet> res = null;

		final StackedchartsFactoryImpl factory = new StackedchartsFactoryImpl();

		final ScatterSet scatter = factory.createScatterSet();
		scatter.setName(sensor.getName());
		scatter.setColor(sensor.getColor());

		final Enumeration<Editable> enumer = sensor.elements();
		while (enumer.hasMoreElements()) {
			final SensorContactWrapper cut = (SensorContactWrapper) enumer.nextElement();
			final Datum item = factory.createDatum();
			item.setVal(cut.getTime().getDate().getTime());
			item.setColor(cut.getColor());

			// and store it
			scatter.getDatums().add(item);
		}

		// did we find any?
		if (scatter.getDatums().size() > 0) {
			if (res == null) {
				res = new ArrayList<ScatterSet>();
			}
			res.add(scatter);
		}

		return res;

	}
}
