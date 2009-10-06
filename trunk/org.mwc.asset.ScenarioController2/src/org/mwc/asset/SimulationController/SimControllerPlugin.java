package org.mwc.asset.SimulationController;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SimControllerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "SimulationController"; //$NON-NLS-1$

	public static final String IMG_ASCEND = Messages.SimControllerPlugin_1;

	public static final String IMG_DESCEND = Messages.SimControllerPlugin_2;

	// The shared instance
	private static SimControllerPlugin plugin;

	/**
	 * The constructor
	 */
	public SimControllerPlugin() {
		System.err.println("sim controller plugin started....");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
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
	public static SimControllerPlugin getInstance() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		addImage(reg, IMG_ASCEND);
		addImage(reg, IMG_DESCEND);
	}

	private void addImage(ImageRegistry registry, String pluginPath) {
		registry.put(pluginPath, ImageDescriptor.createFromURL(FileLocator.find(getBundle(), new Path(pluginPath), null)));
	}
}
