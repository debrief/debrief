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
public class SATC_Activator extends AbstractUIPlugin
{

	public static final String PLUGIN_ID = "com.planetmayo.debrief.satc";

	private static SATC_Activator plugin;

	public static SATC_Activator getDefault()
	{
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private BundleContext context;

	private MockEngine _mockEngine;

	public SATC_Activator()
	{
	}

	public MockEngine getMockEngine()
	{
		if (_mockEngine == null)
			_mockEngine = new MockEngine();

		return _mockEngine;
	}

	public <T> T getService(Class<T> serviceClass, boolean required)
	{
		ServiceReference<T> reference = context.getServiceReference(serviceClass);
		if (reference == null && required)
		{
			throw new IllegalStateException("Service " + serviceClass.getName()
					+ " is required but isn't registered");
		}
		if (reference == null)
		{
			return null;
		}
		return context.getService(reference);
	}

	private void registerServices(BundleContext context)
	{
		context.registerService(VehicleTypesRepository.class,
				new MockVehicleTypesRepository(), new Hashtable<String, Object>());
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		this.context = context;
		plugin = this;
		registerServices(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}
}
