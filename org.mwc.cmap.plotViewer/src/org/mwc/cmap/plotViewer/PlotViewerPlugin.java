/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.plotViewer;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.ui_support.swt.SWTRasterPainter;
import org.mwc.cmap.plotViewer.actions.ExportRTF;
import org.mwc.cmap.plotViewer.actions.ExportWMF;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;
import org.osgi.framework.BundleContext;

import MWC.GUI.Chart.Painters.SpatialRasterPainter;

/**
 * The main plugin class to be used in the desktop.
 */
public class PlotViewerPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.mwc.cmap.plotViewer";
	//The shared instance.
	private static PlotViewerPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	
	private static PlotMouseDragger _currentMode;		
	
	/**
	 * The constructor.
	 */
	public PlotViewerPlugin() {
		super();
		plugin = this;
	}
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		
		// sort out export to WMF
		ExportWMF.init(org.mwc.cmap.core.CorePlugin.getToolParent());
		ExportRTF.init(org.mwc.cmap.core.CorePlugin.getToolParent());
		
		// override the spatial raster painter - since we're working with SWT images, not JAva ones
		SpatialRasterPainter.overridePainter(new SWTRasterPainter());
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static PlotViewerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(final String key) {
		final ResourceBundle bundle = PlotViewerPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (final MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.mwc.cmap.plotViewer.PlotViewerPluginResources");
		} catch (final MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.cmap.plotViewer", path);
	}
	
	

	/** ok, the dragging mode has changed. remember it, so new editors can adopt the same mode
	 * 
	 * @param mode
	 */
	public static void setCurrentMode(final SWTChart.PlotMouseDragger mode)
	{
		_currentMode = mode;
	}
	
	public static PlotMouseDragger getCurrentMode()
	{
		return _currentMode;
	}	
	
}
