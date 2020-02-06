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

import java.util.List;

import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.view.adapter.IStackedDatasetAdapter;
import info.limpet.stackedcharts.ui.view.adapter.IStackedScatterSetAdapter;

public class DebriefStackedChartsAdapter implements IStackedDatasetAdapter, IStackedScatterSetAdapter {

	@Override
	public boolean canConvertToDataset(final Object data) {
		boolean res = false;

		// have a look at the type
		if (data instanceof EditableWrapper) {
			final EditableWrapper ew = (EditableWrapper) data;
			final Editable editable = ew.getEditable();
			if (editable instanceof WatchableList) {
				res = true;
			} else if (editable instanceof SensorWrapper) {
				res = true;
			}
		}

		return res;
	}

	@Override
	public boolean canConvertToScatterSet(final Object data) {
		boolean res = false;

		// have a look at the type
		if (data instanceof EditableWrapper) {
			final EditableWrapper ew = (EditableWrapper) data;
			final Editable editable = ew.getEditable();
			if (editable instanceof WatchableList) {
				res = true;
			} else if (editable instanceof SensorWrapper) {
				res = true;
			}
		}

		return res;
	}

	@Override
	public List<Dataset> convertToDataset(final Object data) {
		List<Dataset> res = null;

		// we should have already checked, but just
		// double-check we can handle it
		if (canConvertToDataset(data)) {
			final EditableWrapper wrapper = (EditableWrapper) data;
			final Editable editable = wrapper.getEditable();
			if (editable instanceof SensorWrapper) {
				final SensorWrapper sensor = (SensorWrapper) editable;

				final SensorConverter converter = new SensorConverter();

				res = converter.convertToDataset(sensor);

				// and store the data
			} else if (editable instanceof WatchableList) {
				final WatchableList list = (WatchableList) editable;

				final TrackConverter converter = new TrackConverter();

				res = converter.convertToDataset(list);

				// now store the data
				// hook up listener
			}

		}

		return res;
	}

	@Override
	public List<ScatterSet> convertToScatterSet(final Object data) {
		List<ScatterSet> res = null;

		// we should have already checked, but just
		// double-check we can handle it
		if (canConvertToDataset(data)) {
			final EditableWrapper wrapper = (EditableWrapper) data;
			final Editable editable = wrapper.getEditable();
			if (editable instanceof SensorWrapper) {
				final SensorWrapper sensor = (SensorWrapper) editable;

				final SensorConverter converter = new SensorConverter();

				res = converter.convertToScatterset(sensor);

				// and store the data
			} else if (editable instanceof WatchableList) {
				final WatchableList list = (WatchableList) editable;

				res = new TrackConverter().convertToScatterset(list);
				// now store the data
				// hook up listener
			}

		}

		return res;
	}

}
