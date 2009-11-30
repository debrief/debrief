package com.pml.lengtheditor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.pml.internal.lengtheditor.preferences.LengthsRegistry;

/**
 * Activator creates LengthsRegistry on {@link #start(BundleContext)}
 * <p>
 * see also {@link #getLengthsRegistry()}
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.pml.lengtheditor"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private LengthsRegistry myLengthsRegistry;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		myLengthsRegistry = LengthsRegistry.getRegistry();
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		myLengthsRegistry = null;
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

	public void logWarning(String message, Throwable throwable) {
		getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, message, throwable));
	}

	public void logError(String message, Throwable throwable) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, throwable));
	}

	public LengthsRegistry getLengthsRegistry() {
		return myLengthsRegistry;
	}

}
