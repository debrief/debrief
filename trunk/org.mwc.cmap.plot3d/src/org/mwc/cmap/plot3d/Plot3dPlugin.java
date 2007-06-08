package org.mwc.cmap.plot3d;

import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.*;

import MWC.GUI.Java3d.ModelFactory;

/**
 * The main plugin class to be used in the desktop.
 */
public class Plot3dPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static Plot3dPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public Plot3dPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);	
		
		// ok - tell the model factory where it is...
		ModelFactory.init(new ModelFactory(){
			
			public java.net.URL getURLFor(String pre_path, java.lang.ClassLoader loader)
			{
				
				URL fileURL = null;
				Path filePath = new Path(pre_path);				
				Bundle staticBundle = Platform.getBundle("org.mwc.cmap.plot3d");
				if (staticBundle != null)
				{
					// and get the relative path compared to the Core Plugin
					fileURL = FileLocator.find(staticBundle, filePath, null);
				}
				return fileURL;
			}
			
			});
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static Plot3dPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.cmap.plot3d", path);
	}
}
