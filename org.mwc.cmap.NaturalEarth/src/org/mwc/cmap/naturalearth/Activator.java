package org.mwc.cmap.naturalearth;

import java.awt.Color;
import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.naturalearth.data.CachedNaturalEarthFile;
import org.mwc.cmap.naturalearth.preferences.PreferenceConstants;
import org.mwc.cmap.naturalearth.view.NEFeatureGroup;
import org.mwc.cmap.naturalearth.view.NEFeatureStore;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.mwc.cmap.naturalearth.view.NEResolution;
import org.mwc.cmap.naturalearth.view.NEStyle;
import org.mwc.cmap.naturalearth.wrapper.NELayer;
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
	private static NEFeatureStore _featureSet = null;

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

	public CachedNaturalEarthFile loadData(String fName)
	{
		CachedNaturalEarthFile res = null;

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

	public NEFeatureStore getDefaultStyleSet()
	{
		if (_featureSet == null)
		{
			final int BOLD = 1;
			final int ITALIC = 2;
			final int PLAIN = 0;

			// start off with the bathy
			_featureSet = new NEFeatureStore();
			_featureSet.setName(NELayer.NATURAL_EARTH);
			
			NEFeatureGroup bathy = new NEFeatureGroup(_featureSet, "Bathymetry");
			final String bathyFolder = "ne_10m_bathymetry_all";
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_A_10000", true,
					new Color(31, 90, 158), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_B_9000", true,
					new Color(43, 104, 173), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_C_8000", true,
					new Color(55, 115, 184), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_D_7000", true,
					new Color(66, 123, 189), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_E_6000", true,
					new Color(77, 132, 194), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_F_5000", true,
					new Color(90, 141, 199), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_G_4000", true,
					new Color(101, 149, 204), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_H_3000", true,
					new Color(115, 159, 209), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_I_2000", true,
					new Color(128, 169, 214), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_J_1000", true,
					new Color(143, 178, 219), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_K_200", true,
					new Color(156, 188, 224), null));
			bathy.add(createF(bathy, bathyFolder, "ne_10m_bathymetry_L_0", true, 
					new Color(172, 199, 230), null));

			NEFeatureGroup ne10 = new NEResolution(_featureSet, "10M", null, 3000000d);
			ne10.add(bathy);
//			ne10.add(createF(null, "ne_10m_land", true, Color.YELLOW, Color.orange));
//			// ne10.add(createF("polygonFeature", "ne_10m_geography_marine_polys",
//			// true,
//			// Color.DARK_GRAY, Color.orange));
//			ne10.add(createF(null, "ne_10m_geography_regions_polys", true,
//					Color.LIGHT_GRAY, Color.red));
			ne10.add(createF(ne10, null,
					"ne_10m_admin_0_boundary_lines_maritime_indicator", true, null,
					new Color(78, 128, 202)));
//			ne10.add(createF(null, "ne_10m_admin_0_boundary_lines_land", true, null,
//					Color.green));
			ne10.add(createF(ne10, null, "ne_10m_admin_0_countries", true, null, new Color(208, 162, 117),
					new Color(150, 117, 84), 12, PLAIN, "SansSerif"));
//			ne10.add(createF(null, "ne_10m_geography_regions_points", true, null,
//					null));
//			ne10.add(createF(null, "ne_10m_ports", true, null, null, Color.pink, 8,
//					0, "Times"));

			NEFeatureGroup ne50 = new NEResolution(_featureSet, "50M", null, 30000000d);
			
			//  ne50.add(bathy);

			ne50.add(createF(ne50, null, "ne_50m_ocean", true, new Color(165, 191, 221),
					null));
			
			ne50.add(createF(ne50, null, "ne_50m_geography_regions_points", true,
					null, null, Color.yellow, 12, 0, "SansSerif"));
			ne50.add(createF(ne50, null, "ne_50m_geography_marine_polys", true,
					new Color(165, 191, 221), null, new Color(31, 130, 180), 15, ITALIC, "Times"));
			
			ne50.add(createF(ne50, null, "ne_50m_populated_places_simple", true,
					null, null, new Color(106, 106, 106), 6, PLAIN, "SansSerif"));
			
			ne50.add(createF(ne50, null, "ne_50m_land", true, new Color(235, 219, 188),
					new Color(108, 108, 108)));
			ne50.add(createF(ne50, null, "ne_50m_admin_0_countries", true, null, new Color(208, 162, 117),
					new Color(150, 117, 84), 12, PLAIN, "SansSerif"));
			
			
			ne50.add(createF(ne50, null, "sea labels", true, null, null, new Color(16, 67,
					98), 16, ITALIC, "Serif"));
			ne50.add(createF(ne50, null, "ocean labels", true, null, null, new Color(16,
					67, 98), 20, ITALIC | BOLD, "Serif"));

			// NEFeatureGroup ne110 = new NEResolution("110M", null, null);
 		  NEFeatureGroup ne110 = new NEResolution(_featureSet, "110M", null, null);
			ne110.add(createF(ne110, null, "ne_110m_land", true, new Color(235, 219, 188),
					new Color(162, 162, 162)));
			ne110.add(createF(ne110, null, "ne_110m_ocean", true, new Color(165, 191, 221),
					Color.red, Color.green, 12, PLAIN, "SansSerif"));
			ne110.add(createF(ne110, null, "ne_110m_geography_marine_polys", true,
					new Color(165, 191, 221), null, new Color(31, 130, 180), 15, ITALIC, "Times"));
			ne110.add(createF(ne110, null, "ne_110m_geography_regions_points", true, null,
					null, Color.pink, 8, 0, "Times"));
			ne110.add(createF(ne110, null, "ne_110m_populated_places_simple", true, null,
					null, new Color(128, 128, 128), 14, 0, "SansSerif"));
			ne110.add(createF(ne110, null, "sea labels", true, null, null, new Color(16, 67,
					98), 16, ITALIC, "Serif"));
			ne110.add(createF(ne110, null, "ocean labels", true, null, null, new Color(16,
					67, 98), 20, ITALIC | BOLD, "Serif"));

			_featureSet.add(ne10);
			_featureSet.add(ne50);
			_featureSet.add(ne110);
		}

		// loop through our styles, find the one that is relevant to this scale
		return _featureSet;
	}

	private static NEFeatureStyle createF(NEFeatureGroup group, String folder, String filename,
			boolean visible, Color fillCol, Color lineCol, Color textCol,
			int textHeight, int textStyle, String textFont)
	{
		NEFeatureStyle nef = createF(group, folder, filename, visible, fillCol, lineCol);
		nef.setVisible(visible);
		nef.setTextColor(textCol);
		nef.setTextHeight(textHeight);
		nef.setTextStyle(textStyle);
		nef.setTextFont(textFont);
		return nef;
	}

	private static NEFeatureStyle createF(NEFeatureGroup group, String folder, String filename,
			boolean visible, Color fillCol, Color lineCol)
	{
		NEFeatureStyle nef = new NEFeatureStyle(group, folder, filename, visible, fillCol,
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
