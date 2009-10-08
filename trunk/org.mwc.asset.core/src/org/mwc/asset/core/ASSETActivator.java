package org.mwc.asset.core;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ASSETActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.asset.core";

	public static final String SCENARIO_CONTROLLER = "org.mwc.asset.ScenarioController";
	

	// The shared instance
	private static ASSETActivator plugin;

	/** somebody to help create images
	 * 
	 */
	private ASSETImageHelper _myImageHelper;
	
	/**
	 * The constructor
	 */
	public ASSETActivator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		_myImageHelper  = new ASSETImageHelper();
		// give the LayerManager our image creator.
		CoreViewLabelProvider.addImageHelper(_myImageHelper  );
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
	public static ASSETActivator getDefault() {
		return plugin;
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
		Status stat = new Status(severity, "org.mwc.asset.core", Status.OK, message, exception);
		getDefault().getLog().log(stat);
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.asset.core", path);
	}	
}
