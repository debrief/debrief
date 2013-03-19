package com.planetmayo.debrief.satc_rcp;

import java.util.Hashtable;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.planetmayo.debrief.satc.model.generator.BoundsManager;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.SolutionGenerator;
import com.planetmayo.debrief.satc.model.manager.IContributionsManager;
import com.planetmayo.debrief.satc.model.manager.IVehicleTypesManager;
import com.planetmayo.debrief.satc.model.manager.impl.ContributionsManagerImpl;
import com.planetmayo.debrief.satc.model.manager.mock.MockVehicleTypesManager;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc_rcp.services.RCPUtilsService;
import com.planetmayo.debrief.satc_rcp.services.RCPIOService;
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

	public static void log(int status, String message, Exception e)
	{
		getDefault().getLog().log(new Status(status, PLUGIN_ID, message, e));
	}

	private BundleContext context;

	public SATC_Activator()
	{
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
		context.registerService(IVehicleTypesManager.class,
				new MockVehicleTypesManager(), new Hashtable<String, Object>());
		context.registerService(IContributionsManager.class, 
				new ContributionsManagerImpl(), new Hashtable<String, Object>());
		BoundsManager boundsM = new BoundsManager();
		context.registerService(IBoundsManager.class, 
				boundsM, new Hashtable<String, Object>());
		SolutionGenerator solutionG = new SolutionGenerator();
		boundsM.setGenerator(solutionG);
		boundsM.addEstimateChangedListener(solutionG);
		context.registerService(ISolutionGenerator.class, 
				solutionG, new Hashtable<String, Object>());
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		SupportServices.INSTANCE.initialize(new RCPLogService(),
				new RCPUtilsService(), new RCPIOService());
		super.start(context);
		this.context = context;
		plugin = this;
		registerServices(context);
		
		// try to set the default perspective
		IPerspectiveDescriptor myPer = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("com.planetmayo.debrief.satc_rcp.perspective");
		PlatformUI.getWorkbench().getPerspectiveRegistry().revertPerspective(myPer);
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}
}
