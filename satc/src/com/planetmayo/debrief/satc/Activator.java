package com.planetmayo.debrief.satc;

import java.util.Hashtable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.planetmayo.debrief.satc.services.VehicleTypesRepository;
import com.planetmayo.debrief.satc.services.mock.MockVehicleTypesRepository;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.planetmayo.debrief.satc";

	private static Activator plugin;
	
	private BundleContext context;
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {		
		super.start(context);
		this.context = context;		
		plugin = this;
		registerServices(context);
	}
	
	private void registerServices(BundleContext context) {
		context.registerService(VehicleTypesRepository.class, new MockVehicleTypesRepository(), new Hashtable<String, Object>());
	}
	
	public <T> T getService(Class<T> serviceClass, boolean required) {
		ServiceReference<T> reference = context.getServiceReference(serviceClass);
		if (reference == null && required) {
			throw new IllegalStateException("Service " + serviceClass.getName() + " is required but isn't registered");
		}
		if (reference == null) {
			return null;
		}
		return context.getService(reference);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
