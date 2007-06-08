package org.mwc.asset.sensormonitor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.asset.core.ASSETPlugin;
import org.mwc.asset.sensormonitor.views.SensorMonitor;
import org.mwc.cmap.core.CorePlugin;
import org.osgi.framework.BundleContext;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.CoreSensor;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.asset.SensorMonitor";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		
		// also provide the method to let sensors open their own monitor
		CoreSensor.setWatchMethod(new CoreSensor.SensorOperation(){
			public void run(SensorType me)
			{
				// open a view, based on this sensor
				// ok, open a new view
				IViewPart part = CorePlugin.openSecondaryView(ASSETPlugin.SENSOR_MONITOR, "" + System.currentTimeMillis(),
						IWorkbenchPage.VIEW_VISIBLE);
				
				SensorMonitor sm = (SensorMonitor) part;
				sm.updateSensor(me);	
				sm.setKeepMonitoring(false);
			}});		
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
}
