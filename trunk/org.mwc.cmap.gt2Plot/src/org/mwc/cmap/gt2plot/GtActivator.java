package org.mwc.cmap.gt2plot;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.gt2plot.data.GTLayer;
import org.osgi.framework.BundleContext;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * The activator class controls the plug-in life cycle
 */
public class GtActivator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.cmap.gt2Plot"; //$NON-NLS-1$

	// The shared instance
	private static GtActivator plugin;

	Layer theLayer;

	/**
	 * The constructor
	 */
	public GtActivator()
	{
	}

	public static void initialise()
	{
		// HACK:  we need to 'pre-initialise' the tiff reader in order 
		// to load world image files
		Iterator<ImageReader> iter2 = ImageIO.getImageReadersBySuffix("tif");
		if(!iter2.hasNext())
		{
			logError(Status.ERROR, "Failed to initialise TIFF reader for Java ImageIO", null);
			System.err.println("TIFF READER NOT READY");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;

		// Setting the system-wide default at startup time
		System.setProperty("org.geotools.referencing.forceXY", "true");

		RightClickContextItemGenerator clicker = new RightClickContextItemGenerator()
		{

			public void generate(IMenuManager parent, Layers theLayers,
					Layer[] parentLayers, Editable[] subjects)
			{
				if (theLayer == null)
					theLayer = new GTLayer();

				// see if it's already loaded
				Layer it = theLayers.findLayer(theLayer.getName());
				if (it == null)
					theLayers.addThisLayer(theLayer);

			}
		};
		RightClickSupport.addRightClickGenerator(clicker);

	}

	/**
	 * error logging utility
	 * 
	 * @param severity
	 *          the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *          <code>INFO</code>, <code>WARNING</code>, or <code>CANCEL</code>
	 * @param message
	 *          a human-readable message, localized to the current locale
	 * @param exception
	 *          a low-level exception, or <code>null</code> if not applicable
	 */
	public static void logError(int severity, String message, Throwable exception)
	{
		GtActivator singleton = getDefault();
		if (singleton != null)
		{
			Status stat = new Status(severity, "org.mwc.cmap.gt2plot", Status.OK,
					message, exception);
			singleton.getLog().log(stat);
		}

		// also throw it to the console
		if (exception != null)
			exception.printStackTrace();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static GtActivator getDefault()
	{
		return plugin;
	}

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
}
