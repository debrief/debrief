package org.mwc.cmap.naturalearth;

import java.awt.Color;
import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.gt2plot.data.CachedNauticalEarthFile;
import org.mwc.cmap.naturalearth.preferences.PreferenceConstants;
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

	// TODO: PECO - I hope this datastore will remain "live" across Debrief plots,
	// so that each plot doesn't have to load its own data
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
		return getPreferenceStore().getString(PreferenceConstants.DATA_FOLDER);
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
			if (_dataStore == null)
			{
				_dataStore = new ShapefileDataStore();
			}

			_dataStore.setPath(pathRoot);

			res = _dataStore.get(fName);
		}

		return res;
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
	public static void logError(final int severity, final String message,
			final Throwable exception)
	{
		final Activator singleton = getDefault();
		if (singleton != null)
		{
			final Status stat = new Status(severity, "org.mwc.cmap.NaturalEarth",
					Status.OK, message, exception);
			singleton.getLog().log(stat);
		}

		// also throw it to the console
		if (exception != null)
			exception.printStackTrace();
	}

	public static NEResolution getStyleFor(double curScale)
	{
		if (_featureSet == null)
		{
			NEResolution ne10 = new NEResolution("10M", null, 200000d);
			ne10.add(createF("polygonFeature", "ne_10m_land", true, Color.YELLOW,
					Color.orange));
//			ne10.add(createF("polygonFeature", "ne_10m_geography_marine_polys", true,
//					Color.DARK_GRAY, Color.orange));
			ne10.add(createF("polygonFeature", "ne_10m_geography_regions_polys",
					true, Color.LIGHT_GRAY, Color.red));
			ne10.add(createF("lineFeature", "ne_10m_admin_0_boundary_lines_land",
					true, null, Color.green));
			ne10.add(createF("pointFeature", "ne_10m_geography_regions_points", true,
					null, null));
			ne10.add(createF("pointFeature", "ne_10m_ports", true,
					null, null, Color.pink, 8, 0, "Times"));

			NEResolution ne50 = new NEResolution("50M", 200000d, 800000d);
			 ne50.add(createF("polygonFeature", "ne_50m_land", true, Color.green,
			 Color.orange));
			 ne50.add(createF("polygonFeature", "ne_50m_ocean", true,
			 Color.lightGray, Color.green));
			ne50.add(createF("polygonFeature", "ne_50m_land", true, Color.WHITE,
					Color.orange));
//			ne50.add(createF("polygonFeature", "ne_10m_geography_marine_polys", true,
//					Color.LIGHT_GRAY, Color.orange));
			ne50.add(createF("pointFeature", "ne_50m_geography_regions_points", true,
					null, null, Color.yellow, 12, 0, "Garamond"));
			ne50.add(createF("pointFeature", "ne_50m_populated_places_simple", true,
					null, null, Color.black, 8, 0, "Serif"));

			NEResolution ne110 = new NEResolution("110M", 800000d, null);
			ne110.add(createF("polygonFeature", "ne_110m_land", true, new Color(235,
					219, 188), new Color(162, 162, 162)));
			ne110.add(createF("polygonFeature", "ne_110m_ocean", true, new Color(165,
					191, 221), null));
			ne110.add(createF("polygonFeature", "ne_110m_geography_marine_polys", true, new Color(165,
					191, 221), null, Color.red, 8, 0, "Times"));
			ne110.add(createF("pointFeature", "ne_110m_geography_regions_points",
					true, null, null, Color.pink, 8, 0, "Times"));
			ne110.add(createF("pointFeature", "ne_110m_populated_places_simple",
					true, null, null, new Color(128, 128, 128), 14, 0, "Garamond"));

			_featureSet = new NEFeatureSet();
			_featureSet.add(ne10);
			_featureSet.add(ne50);
			_featureSet.add(ne110);
		}

		// loop through our styles, find the one that is relevant to this scale
		return _featureSet.resolutionFor(curScale);
	}

	private static NEFeatureStyle createF(String featureType, String filename,
			boolean visible, Color fillCol, Color lineCol, Color textCol, int textHeight, int textStyle, String textFont)
	{
		NEFeatureStyle nef = createF(featureType, filename, visible, fillCol, lineCol);		
		nef.setVisible(visible);
		nef.setTextColor(textCol);
		nef.setTextHeight(textHeight);
		nef.setTextStyle(textStyle);
		nef.setTextFont(textFont);
		return nef;
	}


	private static NEFeatureStyle createF(String featureType, String filename,
			boolean visible, Color fillCol, Color lineCol)
	{
		NEFeatureStyle nef = new NEFeatureStyle(featureType, filename, visible, fillCol,
				lineCol);
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
	public static void logError(final int severity, final String message,
			final Throwable exception, boolean showStack)
	{
		final String fullMessage;
		if (showStack)
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
