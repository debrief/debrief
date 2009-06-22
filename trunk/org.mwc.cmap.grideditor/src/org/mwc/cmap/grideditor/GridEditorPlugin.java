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

	public static final String IMG_ADD = "icons/add.png";

	public static final String IMG_REMOVE = "icons/remove.png";

	public static final String IMG_INTERPOLATE_CALCULATOR = "icons/interpolate.png";

	public static final String IMG_INTERPOLATE_GEAR_WHEEL = "icons/interpolate2.png";

	public static final String IMG_LOCKED = "icons/locked.png";

	public static final String IMG_UNLOCKED = "icons/unlocked.png";

	private static GridEditorPlugin ourInstance;

	private String myPluginId;

	private List<IAdapterFactory> myAdapterFactories;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ourInstance = this;
		myPluginId = context.getBundle().getSymbolicName();
		IAdapterFactory locationChartAdapterFactory = new LocationChartAccess();
		IAdapterFactory locationInterpolatorAdapterFactory = new LocationInterpolatorFactory();
		myAdapterFactories = new LinkedList<IAdapterFactory>();
		myAdapterFactories.add(locationChartAdapterFactory);
		myAdapterFactories.add(locationInterpolatorAdapterFactory);
		Platform.getAdapterManager().registerAdapters(locationChartAdapterFactory, GriddableItemDescriptorExtension.class);
		Platform.getAdapterManager().registerAdapters(locationInterpolatorAdapterFactory, GriddableItemDescriptorExtension.class);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ourInstance = null;
		for (IAdapterFactory next : myAdapterFactories) {
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
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		addImage(reg, IMG_ADD);
		addImage(reg, IMG_REMOVE);
		addImage(reg, IMG_INTERPOLATE_CALCULATOR);
		addImage(reg, IMG_INTERPOLATE_GEAR_WHEEL);
		addImage(reg, IMG_UNLOCKED);
		addImage(reg, IMG_LOCKED);
	}

	private void addImage(ImageRegistry registry, String pluginPath) {
		registry.put(pluginPath, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(pluginPath), null)));
	}

}
