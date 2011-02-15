package org.mwc.asset.scenariocontroller2;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.asset.SimulationController.Messages;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.asset.ScenarioController2";

	public static final String IMG_ASCEND = Messages.SimControllerPlugin_1;

	public static final String IMG_DESCEND = Messages.SimControllerPlugin_2;

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *          the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private void addImage(ImageRegistry registry, String pluginPath)
	{
		registry.put(pluginPath, ImageDescriptor.createFromURL(FileLocator.find(
				getBundle(), new Path(pluginPath), null)));
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		super.initializeImageRegistry(reg);
		addImage(reg, IMG_ASCEND);
		addImage(reg, IMG_DESCEND);
	}
}
