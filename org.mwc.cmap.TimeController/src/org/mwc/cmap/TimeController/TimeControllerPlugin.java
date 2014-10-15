/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.TimeController;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class TimeControllerPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static TimeControllerPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private ImageRegistry _imageRegistry;
	
	/**
	 * The constructor.
	 */
	public TimeControllerPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
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
	public static TimeControllerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(final String key) {
		final ResourceBundle bundle = TimeControllerPlugin.getDefault().getResourceBundle();
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
				resourceBundle = ResourceBundle.getBundle("org.mwc.cmap.TimeController.TimeControllerPluginResources");
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.cmap.TimeController", path);
	}
	
	/** hey, not just the descriptor, return the actual image
	 * 
	 */
	public static Image getImage(final String path)
	{
		
		return getImageFromRegistry(path);
		
	}
	
	private static ImageRegistry getRegistry()
	{
		return plugin._imageRegistry;
	}
	
	public static Image getImageFromRegistry(final String path)
	{
		Image res = null;

		// do we already have an image
		if (getRegistry() == null)
		{
			plugin._imageRegistry = new ImageRegistry();
		}

		// ok - do we have it already?
		res = getRegistry().get(path);

		if (res == null)
		{
			final ImageDescriptor desc = getImageDescriptor(path);
			getRegistry().put(path, desc);
			res = getRegistry().get(path);
		}

		// and return it..
		return res;
	}
}
