package org.mwc.cmap.naturalearth;

import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.naturalearth.view.NEStyle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.cmap.NaturalEarth"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/** retrieve the user preference for library path
	 * 
	 * @return
	 */
	public String getLibraryPath()
	{
		// TODO: @Peco, can you implement this?
		return "test_data";
	}

	public ArrayList<String> getStyleNames()
	{
		// TODO: @Peco, we need to load the set of default styles from the repo (memento, I guess).
		// This part is still undefined.
		return null;
	}
	
	public NEStyle getThisStyle(String name)
	{
		// TODO: @Peco, we need to load the set of default styles from the repo (memento, I guess).
		// This part is still undefined.
		return null;
	}
	
	public void storeThisStyle(NEStyle style)
	{
		// TODO: @Peco, we need to store this style in the prefs dataset		
	}

}
