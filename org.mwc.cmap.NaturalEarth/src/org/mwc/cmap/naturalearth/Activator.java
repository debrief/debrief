package org.mwc.cmap.naturalearth;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.mwc.cmap.naturalearth.preferences.PreferenceConstants;
import org.mwc.cmap.naturalearth.view.NEFeatureRoot;
import org.mwc.cmap.naturalearth.wrapper.NELayer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	public static final String NE_10M_BATHYMETRY_ALL = "ne_10m_bathymetry_all";

	private static final String ORDER = ".order";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.cmap.NaturalEarth"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	// the set of feature types. Actually these will be drawn from the Prefs page
	private static NEFeatureRoot _featureSet = null;

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
		
		// register our image helper
		CoreViewLabelProvider.addImageHelper(new NE_ImageHelper());
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
		String path = getPreferenceStore().getString(PreferenceConstants.DATA_FOLDER);
		
		// is the path empty, or unsuitable for a Natural Earth directory?
		if (path == null || path.isEmpty() || ! (new File(path).isDirectory())) {
			
			// try to retrieve the directory for this plugin
			Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			
			// did we find it?
			if (bundle != null) {
				// ok, get the data subfolder.
				URL url = bundle.getEntry("/data");
				try
				{
					path = FileLocator.toFileURL(url).getFile().toString();
					return path;
				}
				catch (IOException e)
				{
					return null;
				}
			}
		} else {
			// ok - we have a valid path, return it.
			return path;
		}
		return null;
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

	public NEFeatureRoot getDefaultStyleSet()
	{
		if (_featureSet == null)
		{
			_featureSet = new NEFeatureRoot(NELayer.NATURAL_EARTH);
		}
		return _featureSet;
	}

	/** find any shapefiles beneath the specified root
	 * 
	 * @param rootFile the folder we spider down from
	 * @return an ordered list of shapefiles
	 */
	public List<String> getShapeFiles(File rootFile)
	{
		File[] files = rootFile.listFiles();
		List<Order> orderList = new LinkedList<Order>();
		for(File dir:files) {
			if (dir.isDirectory() && !dir.getName().startsWith(".")) {
				int order = getOrder(dir);
				orderList.add(new Order(order, dir));
			}
		}
		Collections.sort(orderList, new Comparator<Order>()
		{

			@Override
			public int compare(Order o1, Order o2)
			{
				return o1.order.compareTo(o2.order);
			}
		});
		List<String> shapeFiles = new ArrayList<String>();
		for (Order order : orderList)
		{
			List<String> list = addDirectory(order.directory);
			shapeFiles.addAll(list);
		}
		return shapeFiles;
	}

	/** retrieve the user preferred NAtural Earth data source
	 * 
	 * @return
	 */
	public File getRootFolder()
	{
		String rootFolder = getLibraryPath();
		if (rootFolder == null || rootFolder.isEmpty()) {
			logError(IStatus.WARNING, "Natural Earth Data Folder isn't set", null);
			return null;
		}
		File rootFile = new File(rootFolder);
		if (!rootFile.isDirectory()) {
			logError(IStatus.WARNING, "Natural Earth Data Folder doesn't exist", null);
			return null;
		}
		return rootFile;
	}
	
	/** store the order of the specified directory/dataset
	 * 
	 * @author ian
	 *
	 */
	private class Order
	{
		public Integer order;
		public File directory;

		public Order(Integer order, File directory)
		{
			super();
			this.order = order;
			this.directory = directory;
		}

	}
	
	/** if the specified folder contains a "1.order" order file,
	 * extract the order.
	 * @param dir
	 * @return the integer to use, or integer.max if un-ordered.
	 */
	private Integer getOrder(File dir)
	{
		File[] files = dir.listFiles(new FilenameFilter()
		{
			
			@Override
			public boolean accept(File dir, String name)
			{
				return name != null && name.endsWith(ORDER);
			}
		});
		if (files.length > 0)
		{
			String orderString = files[0].getName();
			orderString = orderString.substring(0,
					orderString.length() - ORDER.length());
			try
			{
				return new Integer(orderString);
			}
			catch (NumberFormatException e)
			{
				// ignore
			}
		}
		return Integer.MAX_VALUE;
	}

	/** find out if this folder contains any shape files. If it does
	 * return them in a list
	 * @param rootFile the root we search down from
	 * @return
	 */
	private List<String> addDirectory(File rootFile)
	{
		File[] files = rootFile.listFiles();
		List<String> shapeFiles = new ArrayList<String>();
		for (File file : files)
		{
			if (file.isFile() && file.getName().endsWith(".shp"))
			{
				shapeFiles.add(file.getAbsolutePath());
			}
		}
		if (shapeFiles.size() > 0) {
			sortShapeFiles(rootFile,shapeFiles);
		}
		return shapeFiles;
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
	
	public static void sortShapeFiles(File rootFile, List<String> fileNames)
	{
		if (fileNames.size() > 0)
		{
			if (NE_10M_BATHYMETRY_ALL.equals(rootFile.getName()))
			{
				Collections.sort(fileNames, new Comparator<String>()
				{

					@Override
					public int compare(String o1, String o2)
					{
						if (o1 == o2) {
							return 0;
						}
						if (o1 == null) {
							return -1;
						}
						if (o2 == null) {
							return 1;
						}
						// ne_10m_bathymetry_A_10000.shp
						String s1 = new File(o1).getName();
						String s2 = new File(o2).getName();
						int length = "ne_10m_bathymetry_A_".length();
						if (s1.length() > length && s2.length() > length)
						{
							s1 = s1.substring(length, s1.length() - 4);
							s2 = s2.substring(length, s2.length() - 4);
							try
							{
								Integer i1 = new Integer(s1);
								Integer i2 = new Integer(s2);
								return i1.compareTo(i2);
							}
							catch (NumberFormatException e)
							{
								// ignore
							}
						}
						return o1.compareTo(o2);
					}
				});
			}
			else
			{
				Collections.sort(fileNames);
			}
		}
	}

}
