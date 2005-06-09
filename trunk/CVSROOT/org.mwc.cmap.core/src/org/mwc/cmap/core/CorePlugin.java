package org.mwc.cmap.core;

import java.util.*;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.preferences.ETOPOPrefsPage;
import org.mwc.cmap.core.ui_support.LineItem;
import org.osgi.framework.BundleContext;

import MWC.GUI.Tools.Palette.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class CorePlugin extends AbstractUIPlugin
{
	// The shared instance.
	private static CorePlugin plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;
	
	/** the Debrief tool-parent used to provide legacy access to properties
	 * 
	 */
	private static DebriefToolParent _toolParent;
	

	/** the shared line of status text used across CMAP apps
	 * 
	 */
	private static LineItem _myLineItem = null;	

	/**
	 * where we cache our images
	 */
	private ImageRegistry _imageRegistry;

	/**
	 * The constructor.
	 */
	public CorePlugin()
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
		
		
		// create something capable of handling legacy preferences
		_toolParent = new DebriefToolParent(getPreferenceStore());
		
		// tell the VPF generator where to get its preferences from
		CreateVPFLayers.initialise(_toolParent);
		
		// also initialise the ETOPO wrapper (if we have to)
		CreateTOPO.initialise(_toolParent);
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
	public static CorePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key)
	{
		ResourceBundle bundle = CorePlugin.getDefault().getResourceBundle();
		try
		{
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e)
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
						.getBundle("org.mwc.cmap.core.CorePluginResources");
		} catch (MissingResourceException x)
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
		return AbstractUIPlugin
				.imageDescriptorFromPlugin("org.mwc.cmap.core", path);
	}

	/**
	 * error logging utility
	 * 
	 * @param severity
	 *          the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *          <code>INFO</code>, <code>WARNING</code>, or
	 *          <code>CANCEL</code>
	 * @param message
	 *          a human-readable message, localized to the current locale
	 * @param exception
	 *          a low-level exception, or <code>null</code> if not applicable
	 */
	public static void logError(int severity, String message, Throwable exception)
	{
		Status stat = new Status(severity, "org.mwc.cmap.core", Status.OK, message,
				exception);
		getDefault().getLog().log(stat);
	}

	private static ImageRegistry getRegistry()
	{
		return plugin._imageRegistry;
	}

	public static Image getImageFromRegistry(String name)
	{
		Image res = null;

		// do we already have an image
		if (getRegistry() == null)
		{
			plugin._imageRegistry = new ImageRegistry();
		}

		// ok - do we have it already?
		res = getRegistry().get(name);

		if (res == null)
		{
			ImageDescriptor desc = getImageDescriptor("icons/" + name);
			getRegistry().put(name, desc);
			res = getRegistry().get(name);
		}

		// and return it..
		return res;
	}


	public static LineItem getStatusLine(EditorPart editor)
	{
		if(_myLineItem == null)
		{
		IStatusLineManager mgr = editor.getEditorSite().getActionBars()
				.getStatusLineManager();
		_myLineItem = new LineItem("vv aa");
		mgr.add(_myLineItem);
		}

		return _myLineItem;
	}

}
