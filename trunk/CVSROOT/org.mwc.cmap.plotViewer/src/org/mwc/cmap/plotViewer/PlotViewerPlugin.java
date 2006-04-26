package org.mwc.cmap.plotViewer;

import java.util.*;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.plotViewer.actions.ExportWMF;
import org.mwc.cmap.plotViewer.editors.chart.*;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;
import org.osgi.framework.BundleContext;

import MWC.GUI.Chart.Painters.SpatialRasterPainter;

/**
 * The main plugin class to be used in the desktop.
 */
public class PlotViewerPlugin extends AbstractUIPlugin {
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
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// sort out export to WMF
		ExportWMF.init(org.mwc.cmap.core.CorePlugin.getToolParent());
		

		// override the spatial raster painter - since we're working with SWT images, not JAva ones
		SpatialRasterPainter.overridePainter(new SWTRasterPainter());
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
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
	public static String getResourceString(String key) {
		ResourceBundle bundle = PlotViewerPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
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
		} catch (MissingResourceException x) {
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
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.cmap.plotViewer", path);
	}
	
	

	/** ok, the dragging mode has changed. remember it, so new editors can adopt the same mode
	 * 
	 * @param mode
	 */
	public static void setCurrentMode(SWTChart.PlotMouseDragger mode)
	{
		_currentMode = mode;
	}
	
	public static PlotMouseDragger getCurrentMode()
	{
		return _currentMode;
	}	
	
}
