package org.mwc.cmap.layer_manager;

import java.util.*;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class Layer_managerPlugin extends AbstractUIPlugin
{
	// The shared instance.
	private static Layer_managerPlugin plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public Layer_managerPlugin()
	{
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);

		try
		{
			// also fire up the xy-plotter item
			RightClickSupport.addRightClickGenerator(new org.mwc.cmap.xyplot.XYPlotGeneratorButtons());
		}
		catch (Exception e)
		{
			System.out.println("still can't find xy-plot");
		}

	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static Layer_managerPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key)
	{
		ResourceBundle bundle = Layer_managerPlugin.getDefault().getResourceBundle();
		try
		{
			return (bundle != null) ? bundle.getString(key) : key;
		}
		catch (MissingResourceException e)
		{
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle()
	{
		try
		{
			if (resourceBundle == null)
				resourceBundle = ResourceBundle
						.getBundle("org.mwc.cmap.layer_manager.Layer_managerPluginResources");
		}
		catch (MissingResourceException x)
		{
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *          the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.cmap.layer_manager", path);
	}
}
