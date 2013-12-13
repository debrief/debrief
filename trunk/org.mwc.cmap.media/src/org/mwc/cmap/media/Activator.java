package org.mwc.cmap.media;

import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.media.time.ITimeProvider;
import org.mwc.cmap.media.time.impl.TimeProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.mwc.cmap.media"; //$NON-NLS-1$

	private static Activator plugin;
	
	private BundleContext context; 
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		context.registerService(ITimeProvider.class.getName(), new TimeProvider(), new Hashtable<String, Object>());
		// this is deprecated
		//NativeLibrariesLoader.loadBundledXuggler(context.getDataFile("native"), context.getBundle());
		plugin = this;
		this.context = context;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public ITimeProvider getTimeProvider() {
		ServiceReference reference = context.getServiceReference(ITimeProvider.class.getName());
		if (reference != null) {
			return (ITimeProvider) context.getService(reference);
		}
		return null;
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static void log(Throwable t) {
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, t.getLocalizedMessage(), t);
		getDefault().getLog().log(status);
	}
}
