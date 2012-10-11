package com.planetmayo.debrief.satc_rcp;

import java.util.Hashtable;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc.support.VehicleTypesRepository;
import com.planetmayo.debrief.satc.support.mock.MockVehicleTypesRepository;
import com.planetmayo.debrief.satc_rcp.services.RCPConverterService;
import com.planetmayo.debrief.satc_rcp.services.RCPLogService;

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

	public SATC_Activator()
	{
	}

	public static void log(int status, String message, Exception e)
	{
		getDefault().getLog().log(new Status(status,PLUGIN_ID, message, e));
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
		SupportServices.INSTANCE.initialize(new RCPLogService(), new RCPConverterService());
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
