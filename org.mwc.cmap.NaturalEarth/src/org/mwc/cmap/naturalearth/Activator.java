package org.mwc.cmap.naturalearth;

import java.awt.Color;
import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.gt2plot.data.CachedNauticalEarthFile;
import org.mwc.cmap.naturalearth.view.NEFeatureSet;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.view.NEResolution;
import org.mwc.cmap.naturalearth.view.NEStyle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.cmap.NaturalEarth"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	// TODO: PECO - I hope this datastore will remain "live" across Debrief plots, so that each plot doesn't have to load its own data
	// the data file cache
	private static ShapefileDataStore _dataStore;

	// the set of feature types. Actually these will be drawn from the Prefs page
	private static NEFeatureSet _featureSet = null;
	

	
	/**
	 * The constructor
	 */
	public Activator()
	{
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
	public static Activator getDefault()
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

	/**
	 * retrieve the user preference for Natural Earth data folder
	 * 
	 * @return
	 */
	public String getLibraryPath()
	{
		// TODO: @Peco, can you implement this?
		// return "git/debrief/org.mwc.cmap.NaturalEarth.test/data";

		return "/Users/ian/git/debrief_github/org.mwc.cmap.NaturalEarth.test/data";
	}

	public ArrayList<String> getStyleNames()
	{
		// TODO: @Peco, we need to load the set of default styles from the repo
		// (memento, I guess).
		// This part is still undefined.
		return null;
	}

	public NEStyle getThisStyle(String name)
	{
		// TODO: @Peco, we need to load the set of default styles from the repo
		// (memento, I guess).
		// This part is still undefined.
		return null;
	}

	public void storeThisStyle(NEStyle style)
	{
		// TODO: @Peco, we need to store this style in the prefs dataset
	}

	public CachedNauticalEarthFile loadData(String fName)
	{
		CachedNauticalEarthFile res = null;

		// TODO: read the data root setting from the prefs page
		String pathRoot = getLibraryPath();

		// double check
		if (pathRoot != null)
		{
			// init the datastore, if we have to
			if(_dataStore == null)
			{
				_dataStore = new ShapefileDataStore();
			}
			
			_dataStore.setPath(pathRoot);
			
			res = _dataStore.get(fName);
		}

		return res;
	}
	
	public static NEResolution getStyleFor(double curScale)
	{
		System.out.println("scale:" + curScale);

		if(_featureSet == null)
		{
			NEResolution ne10 = new NEResolution(null,100000d);
			ne10.add(createF("polygonFeature", "ne_10m_geography_marine_polys", true, Color.DARK_GRAY, Color.orange, Color.yellow));
			ne10.add(createF("lineFeature", "ne_10m_admin_0_boundary_lines_land", true, null, Color.green, Color.blue));
			ne10.add(createF("pointFeature", "ne_10m_geography_regions_points",true,  null, null, Color.red));
			
			NEResolution ne110 = new NEResolution(100000d,null);
			ne110.add(createF("polygonFeature", "ne_110m_land", true, Color.yellow, Color.orange, Color.yellow));
			ne110.add(createF("polygonFeature", "ne_110m_ocean", true, Color.blue, Color.green, Color.pink));
			ne110.add(createF("pointFeature", "ne_110m_geography_regions_points", true, null, null, Color.red));
			ne110.add(createF("pointFeature", "ne_110m_populated_places_simple", true, null, null, Color.green));
			
			_featureSet = new NEFeatureSet();
			_featureSet.add(ne10);
			_featureSet.add(ne110);
		}
		
		// loop through our styles, find the one that is relevant to this scale
		return _featureSet.resolutionFor(curScale);
	}
	
	private static NEFeatureStyle createF(String featureType, String filename, boolean visible, Color fillCol, Color lineCol, Color textCol)
	{
		NEFeatureStyle nef =new NEFeatureStyle(featureType, filename, fillCol, lineCol, textCol);
		nef.setVisible(visible);
		return nef;
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
	public static void logError(final int severity, final String message, final Throwable exception, boolean showStack)
	{
		final String fullMessage;
		if(showStack)
		{
			String stackListing = "Trace follows\n=================\n";
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			for (int i = 0; i < stack.length; i++)
			{
				StackTraceElement ele = stack[i];
				stackListing += ele.toString() + "\n";
			}
			fullMessage = message + "\n" + stackListing;
		}
		else
		{
			fullMessage = message;
		}
		
		final Activator singleton = getDefault();
		if (singleton != null)
		{
			final Status stat = new Status(severity, "org.mwc.cmap.core", Status.OK,
					fullMessage, exception);
			singleton.getLog().log(stat);
		}

		// also throw it to the console
		if (exception != null)
			exception.printStackTrace();
	}
	

}
