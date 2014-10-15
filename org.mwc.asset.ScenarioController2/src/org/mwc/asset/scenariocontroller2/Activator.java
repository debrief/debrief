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
	public static ImageDescriptor getImageDescriptor(final String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private void addImage(final ImageRegistry registry, final String pluginPath)
	{
		registry.put(pluginPath, ImageDescriptor.createFromURL(FileLocator.find(
				getBundle(), new Path(pluginPath), null)));
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry reg)
	{
		super.initializeImageRegistry(reg);
		addImage(reg, IMG_ASCEND);
		addImage(reg, IMG_DESCEND);
	}
}
