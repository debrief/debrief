/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.grideditor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.grideditor.chart.location.LocationChartAccess;
import org.mwc.cmap.grideditor.interpolation.location.LocationInterpolatorFactory;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptorExtension;
import org.osgi.framework.BundleContext;


public class GridEditorPlugin extends AbstractUIPlugin {

	public static final String IMG_ADD = "icons/16/add.png";

	public static final String IMG_REMOVE = "icons/16/remove.png";

	public static final String IMG_INTERPOLATE_CALCULATOR = "icons/16/interpolate.png";

	public static final String IMG_INTERPOLATE_GEAR_WHEEL = "icons/interpolate2.png";

	public static final String IMG_LOCKED = "icons/16/locked.png";
	public static final String IMG_UNLOCKED = "icons/16/unlocked.png";
	public static final String IMG_EXPORT = "icons/16/export.png";

	private static GridEditorPlugin ourInstance;

	private String myPluginId;

	private List<IAdapterFactory> myAdapterFactories;

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		ourInstance = this;
		myPluginId = context.getBundle().getSymbolicName();
		final IAdapterFactory locationChartAdapterFactory = new LocationChartAccess();
		final IAdapterFactory locationInterpolatorAdapterFactory = new LocationInterpolatorFactory();
		myAdapterFactories = new LinkedList<IAdapterFactory>();
		myAdapterFactories.add(locationChartAdapterFactory);
		myAdapterFactories.add(locationInterpolatorAdapterFactory);
		Platform.getAdapterManager().registerAdapters(locationChartAdapterFactory, GriddableItemDescriptorExtension.class);
		Platform.getAdapterManager().registerAdapters(locationInterpolatorAdapterFactory, GriddableItemDescriptorExtension.class);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		ourInstance = null;
		for (final IAdapterFactory next : myAdapterFactories) {
			Platform.getAdapterManager().unregisterAdapters(next);
		}
		myAdapterFactories.clear();
		myAdapterFactories = null;
		super.stop(context);
	}

	public static GridEditorPlugin getInstance() {
		return ourInstance;
	}

	public String getPluginId() {
		return myPluginId;
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		addImage(reg, IMG_ADD);
		addImage(reg, IMG_REMOVE);
		addImage(reg, IMG_INTERPOLATE_CALCULATOR);
		addImage(reg, IMG_INTERPOLATE_GEAR_WHEEL);
		addImage(reg, IMG_UNLOCKED);
		addImage(reg, IMG_LOCKED);
		addImage(reg, IMG_EXPORT);
	}

	private void addImage(final ImageRegistry registry, final String pluginPath) {
		registry.put(pluginPath, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(pluginPath), null)));
	}

}
