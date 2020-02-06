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
package info.limpet.stackedcharts.ui.view.adapter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.ui.editor.Activator;

public class AdapterRegistry implements IStackedDatasetAdapter, IStackedScatterSetAdapter {

	private static final String DATASET_ADAPTER_ID = "info.limpet.stackedcharts.ui.dataset_adapter";

	private static final String SCATTERSET_ADAPTER_ID = "info.limpet.stackedcharts.ui.scatterset_adapter";

	@Override
	public boolean canConvertToDataset(final Object data) {
		boolean res = false;
		try {
			final IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(DATASET_ADAPTER_ID);
			for (final IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");

				if (o instanceof IStackedDatasetAdapter) {
					final IStackedDatasetAdapter sa = (IStackedDatasetAdapter) o;
					if (sa.canConvertToDataset(data)) {
						// success, drop out
						res = true;
						break;
					}
				}
			}
		} catch (final Exception ex) {
			Activator.getDefault().getLog()
					.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to load stacked charts adapter", ex));
		}

		return res;
	}

	@Override
	public boolean canConvertToScatterSet(final Object data) {
		boolean res = false;
		try {
			final IConfigurationElement[] config = Platform.getExtensionRegistry()
					.getConfigurationElementsFor(SCATTERSET_ADAPTER_ID);
			for (final IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof IStackedScatterSetAdapter) {
					final IStackedScatterSetAdapter sa = (IStackedScatterSetAdapter) o;
					if (sa.canConvertToScatterSet(data)) {
						// success, drop out
						res = true;
						break;
					}
				}
			}
		} catch (final Exception ex) {
			Activator.getDefault().getLog()
					.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to load stacked charts adapter", ex));
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Dataset> convertToDataset(final Object data) {
		List<Dataset> res = null;
		try {
			// ok, do we need to loop through the items?
			List<Object> list;
			if (data instanceof List<?>) {
				list = (List<Object>) data;
			} else {
				list = new ArrayList<Object>();
				list.add(data);
			}

			// now loop through them
			for (final Object item : list) {

				final IConfigurationElement[] config = Platform.getExtensionRegistry()
						.getConfigurationElementsFor(DATASET_ADAPTER_ID);
				for (final IConfigurationElement e : config) {
					final Object o = e.createExecutableExtension("class");
					if (o instanceof IStackedDatasetAdapter) {
						final IStackedDatasetAdapter sa = (IStackedDatasetAdapter) o;

						final List<Dataset> matches = sa.convertToDataset(item);

						if (matches != null) {
							if (res == null) {
								res = new ArrayList<Dataset>();
							}

							res.addAll(matches);
							// success, drop out
							break;
						}
					}
				}
			}
		} catch (final Exception ex) {
			Activator.getDefault().getLog()
					.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to load stacked charts adapter", ex));
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScatterSet> convertToScatterSet(final Object data) {
		List<ScatterSet> res = null;
		try {
			// ok, do we need to loop through the items?
			List<Object> list;
			if (data instanceof List<?>) {
				list = (List<Object>) data;
			} else {
				list = new ArrayList<Object>();
				list.add(data);
			}

			// now loop through them
			for (final Object item : list) {

				final IConfigurationElement[] config = Platform.getExtensionRegistry()
						.getConfigurationElementsFor(SCATTERSET_ADAPTER_ID);
				for (final IConfigurationElement e : config) {
					final Object o = e.createExecutableExtension("class");
					if (o instanceof IStackedScatterSetAdapter) {
						final IStackedScatterSetAdapter sa = (IStackedScatterSetAdapter) o;

						final List<ScatterSet> matches = sa.convertToScatterSet(item);

						if (matches != null) {
							if (res == null) {
								res = new ArrayList<ScatterSet>();
							}

							res.addAll(matches);
							// success, drop out
							break;
						}
					}
				}
			}
		} catch (final Exception ex) {
			Activator.getDefault().getLog()
					.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to load stacked charts adapter", ex));
		}

		return res;
	}

}
