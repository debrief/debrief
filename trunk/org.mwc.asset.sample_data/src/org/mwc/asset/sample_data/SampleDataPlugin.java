package org.mwc.asset.sample_data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SampleDataPlugin extends AbstractUIPlugin implements org.eclipse.ui.IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.asset.sample_data";

	// The shared instance
	private static SampleDataPlugin plugin;
	
	/**
	 * The constructor
	 */
	public SampleDataPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
	public static SampleDataPlugin getDefault() {
		return plugin;
	}

	/** get the indicated file 
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public InputStream getResource(IPath filePath) throws IOException
	{
		return FileLocator.openStream(getBundle(), filePath, false);
	}
	
	public URL getFileURL(String filePath) throws IOException
	{
    final java.net.URL rawURL = getClass().getClassLoader().getResource(filePath);
		URL fileURL = FileLocator.toFileURL(rawURL);
		return fileURL;
	}

	public void earlyStartup()
	{
		// hey, I don't know...
		System.out.println("early startup fired!!!");
	}
	
}
