package org.mwc.asset.scenariocontroller;

import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.asset.core.property_support.TargetTypeHelper;
import org.mwc.cmap.core.property_support.DebriefProperty;
import org.mwc.cmap.core.property_support.EditorHelper;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScenarioControllerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.asset.ScenarioController";

	// The shared instance
	private static ScenarioControllerPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ScenarioControllerPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// sort out the additional helpers for asset-related items
		Vector<EditorHelper> myHelpers = getHelpers();
		DebriefProperty.addSupplementalHelpers(myHelpers);
	}

	/** sort out the ASSET specific helpers
	 * 
	 * @return
	 */
	private Vector<EditorHelper> getHelpers()
	{
		Vector<EditorHelper> res = new Vector<EditorHelper>(1,1);
		res.add(new TargetTypeHelper());
		return res;
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
	public static ScenarioControllerPlugin getDefault() {
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
	
	/** hey, not just the descriptor, return the actual image
	 * 
	 */
	public static Image getImage(String path)
	{
		Image res = null;
		ImageDescriptor desk = getImageDescriptor(path);
		if(desk != null)
			res = desk.createImage();
		
		return res;
		
	}	
}
