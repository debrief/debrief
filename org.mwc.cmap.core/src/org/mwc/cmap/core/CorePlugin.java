/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.core;

import interfaces.IEarthModelProvider;

import java.awt.Color;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.gridharness.data.base60.Sexagesimal;
import org.mwc.cmap.gridharness.data.base60.SexagesimalFormat;
import org.mwc.cmap.gridharness.data.base60.SexagesimalSupport;
import org.osgi.framework.BundleContext;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.Algorithms.EarthModel;
import MWC.GUI.ToolParent;
import MWC.GUI.Chart.Painters.CoastPainter;
import MWC.GUI.Tools.Palette.CreateTOPO;
import MWC.GUI.Tools.Palette.CreateVPFLayers;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Features.VPFDatabaseHandler;

/**
 * The main plugin class to be used in the desktop.
 */
public class CorePlugin extends AbstractUIPlugin implements ClipboardOwner
{

	/**
	 * how many UI operations to remember in the undo buffer
	 * 
	 */
	private static final int LENGTH_OF_UNDO_BUFFER = 10;

	public static final String PLUGIN_ID = "org.mwc.cmap.core";

	public static final String NARRATIVES = "org.mwc.cmap.narrative.views.NarrativeView";
	public static final String NARRATIVES2 = "com.borlander.ianmayo.nviewer.app.view";

	public static final String TOTE = "org.mwc.cmap.tote.views.ToteView";

	public static final String TIME_CONTROLLER = "org.mwc.cmap.TimeController.views.TimeController";
	public static final String TIME_BAR = "org.mwc.cmap.timebar.views.TimeBarView";

	public static final String XY_PLOT = "org.mwc.cmap.xyplot.views.XYPlotView";

	public static final String OVERVIEW_PLOT = "org.mwc.cmap.overview.views.ChartOverview";

	public static final String STACKED_DOTS = "org.mwc.debrief.track_shift.views.StackedDotsView";
	public static final String FREQ_RESIDUALS = "org.mwc.debrief.track_shift.views.FrequencyResidualsView";
	public static final String GRID_EDITOR = "com.pml.grid.editor";

	public static final String POLYGON_EDITOR = "org.mwc.cmap.core.editor_views.PolygonEditorView";
	public static final String LIVE_DATA_MONITOR = "org.mwc.cmap.LiveDataMonitor";
	private static final String EARTH_MODEL_PROVIDER = "org.mwc.cmap.core.EarthModelProvider";

	/**
	 * support for lat/long editor in grid editor
	 * 
	 */
	static final public String PREF_BASE60_FORMAT_NO_SECONDS = PLUGIN_ID
			+ ".base60-no-seconds";

	// The shared instance.
	private static CorePlugin plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * the Debrief tool-parent used to provide legacy access to properties
	 */
	private static ToolParent _toolParent;

	/**
	 * where we cache our images
	 */
	private ImageRegistry _imageRegistry;

	/**
	 * our CMAP-wide clipboard
	 */
	private Clipboard _myClipboard;

	/**
	 * the undo buffer we manage/support
	 */
	private static IOperationHistory _myHistory;

	/**
	 * and the context used to describe our undo list
	 */
	public final static IUndoContext CMAP_CONTEXT = new ObjectUndoContext("CMAP");

	/**
	 * fixed string used to indicate a string is in our location format
	 */
	public static final String LOCATION_STRING_IDENTIFIER = "LOC:";

	/** special-case handler that ensures that XY Plot views are 
	 * closed when the app closes. XY-Plot views are "special" views
	 * because we don't wish them to persist between sessions.
	 */
	private  IWorkbenchListener workbenchListener = new IWorkbenchListener()
	{

		@Override
		public boolean preShutdown(IWorkbench workbench, boolean forced)
		{
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (IWorkbenchWindow window:windows) {
				IWorkbenchPage page = window.getActivePage();
				IViewReference[] viewReferences = page.getViewReferences();
				for (IViewReference viewReference:viewReferences) {
					String id = viewReference.getId();
					if (XY_PLOT.equals(id)) {
						page.hideView(viewReference);
					}
				}
			}
			return true;
		}

		@Override
		public void postShutdown(IWorkbench workbench)
		{
		}
	};

	/**
	 * The constructor.
	 */
	public CorePlugin()
	{
		super();
		plugin = this;

		// store our color property editor
		java.beans.PropertyEditorManager.registerEditor(Color.class,
				MWC.GUI.Properties.ColorPropertyEditor.class);
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(final BundleContext context) throws Exception
	{
		super.start(context);
		
		// we have an ongoing problem with the window that RCP provides for a floated XY
		// Plot staying behind after the view itself has closed, and 
		// after the app has closed/re-opened.
		PlatformUI.getWorkbench().addWorkbenchListener(workbenchListener);

		// hack to initialise the TIFF importers. I believe we're having classpath
		// issues,
		// if we don't try to get an image reader right at the start of the app,
		// we can't get a TIFF loader later on.
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
		final Iterator<ImageReader> iter2 = ImageIO.getImageReadersBySuffix("tif");
		if (!iter2.hasNext())
		{
			logError(Status.ERROR,
					"Failed to initialise TIFF reader for Java ImageIO", null);
			System.err.println("TIFF READER NOT READY");
		}
		else
		{
			logError(Status.INFO, "Successfully loaded TIFF reader for Java ImageIO",
					null);
		}

		// create something capable of handling legacy preferences
		_toolParent = new DebriefToolParent(getPreferenceStore(), getHistory());

		// tell the VPF generator where to get its preferences from
		CreateVPFLayers.initialise(_toolParent);

		// also initialise the ETOPO wrapper (if we have to)
		CreateTOPO.initialise(_toolParent);

		// and the replay importer - since it needs to know what mode (ATG/DR)
		// to use for new data
		ImportReplay.initialise(_toolParent);

		// and the coastline-reader
		CoastPainter.initialise(_toolParent);

		// and the application - so we can use our own toolparent for the properties
		Application.initialise(_toolParent);

		// and the range calculator (it needs to know for the user pref on units)
		rangeCalc.init(_toolParent);

		// and the old error logger
		Trace.initialise(_toolParent);

		// the VPF database may wish to announce some kind of warning
		// if it can't find it's data
		VPFDatabaseHandler.initialise(_toolParent);

		// there again, the track wrapper may have problems when it's
		TrackSegment.initialise(_toolParent);
		
		evaluateEarthModelProviderExtension(Platform.getExtensionRegistry());
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(final BundleContext context) throws Exception
	{
		super.stop(context);
		PlatformUI.getWorkbench().removeWorkbenchListener(workbenchListener);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static CorePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * retrieve the toolparent we're using
	 */
	public static ToolParent getToolParent()
	{
		return _toolParent;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(final String key)
	{
		final ResourceBundle bundle = CorePlugin.getDefault().getResourceBundle();
		try
		{
			return (bundle != null) ? bundle.getString(key) : key;
		}
		catch (final MissingResourceException e)
		{
			return key;
		}
	}

	public static void writeToClipboard(final String txt)
	{

		// create the clipboard buffer
		final java.awt.datatransfer.Clipboard clip = java.awt.Toolkit.getDefaultToolkit()
				.getSystemClipboard();

		// put the string in a holder
		final StringSelection sel = new java.awt.datatransfer.StringSelection(txt);

		// and put it on the clipboard
		clip.setContents(sel, null);
	}

	/**
	 * get the CMAP clipboard
	 * 
	 * @return
	 */
	public Clipboard getClipboard()
	{
		if (_myClipboard == null)
			_myClipboard = new Clipboard(Display.getCurrent());

		return _myClipboard;
	}

	/**
	 * get the undo buffer
	 * 
	 * @return the undo buffer (called a History in Eclipse)
	 */
	public static IOperationHistory getHistory()
	{
		if (_myHistory == null)
		{
			_myHistory = OperationHistoryFactory.getOperationHistory();

			// and set the buffer length
			_myHistory.setLimit(CorePlugin.CMAP_CONTEXT, LENGTH_OF_UNDO_BUFFER);
		}

		return _myHistory;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle()
	{
		try
		{
			if (resourceBundle == null)
				resourceBundle = ResourceBundle
						.getBundle("org.mwc.cmap.core.CorePluginResources");
		}
		catch (final MissingResourceException x)
		{
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * @param asSelection
	 * @param parentPart
	 */
	public static void editThisInProperties(
			final Vector<ISelectionChangedListener> selectionListeners,
			final StructuredSelection asSelection,
			final ISelectionProvider selectionProvider, final IWorkbenchPart parentPart)
	{
		// hey, better make sure the properties window is open
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();

		// get ready for the start/end times
		// right, we need the time controller if we're going to get the
		// times
		// select the part that wants to do the editing (otherwise the properties
		// window just ignores it's selection)
		page.activate(parentPart);

		// fire the update async - so the current page is clearly activated before
		// marking the selection
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				try
				{
					// introduce a pause
					try
					{
						Thread.sleep(100);
					}
					catch (final InterruptedException e)
					{
						CorePlugin.logError(Status.ERROR, "Property edit interruption", e);
					}

					// now update the selection
					if (selectionListeners != null)
					{
						final SelectionChangedEvent sEvent = new SelectionChangedEvent(
								selectionProvider, asSelection);
						for (final Iterator<ISelectionChangedListener> stepper = selectionListeners
								.iterator(); stepper.hasNext();)
						{
							final ISelectionChangedListener thisL = (ISelectionChangedListener) stepper
									.next();
							if (thisL != null)
							{
								thisL.selectionChanged(sEvent);
							}
						}
					}
					// and show the properties view
					page.showView(IPageLayout.ID_PROP_SHEET, null,
							IWorkbenchPage.VIEW_VISIBLE);
				}
				catch (final PartInitException e)
				{
					logError(Status.ERROR,
							"Failed to open properties view when showing timer properties", e);
				}
			}
		});
	}

	/**
	 * convenience method to assist in extracting a location from the clipboard
	 * 
	 * @param txt
	 * @return
	 */
	public static WorldLocation fromClipboard(final String txt)
	{
		WorldLocation res = null;

		if (txt.startsWith(LOCATION_STRING_IDENTIFIER))
		{

			// get rid of the title
			final String dataPart = txt.substring(LOCATION_STRING_IDENTIFIER.length(),
					txt.length());
			final StringTokenizer st = new StringTokenizer(dataPart);
			final String latP = st.nextToken(",");
			final String longP = st.nextToken(",");
			final String depthP = st.nextToken();
			final Double _lat = new Double(latP);
			final Double _long = new Double(longP);
			final Double _depth = new Double(depthP);
			res = new WorldLocation(_lat.doubleValue(), _long.doubleValue(),
					_depth.doubleValue());
		}
		else
		{
			// see what else we can sort out
			// get a stream from the string
			final StringTokenizer st = new StringTokenizer(txt.trim());

			final int numTokens = st.countTokens();

			if (numTokens == 2)
			{
				try
				{

					final String firstItem = st.nextToken(" \t");
					final String secondItem = st.nextToken();

					if (firstItem != null && secondItem != null)
					{
						// hey, go for it
						final double latVal = MWCXMLReader.readThisDouble(firstItem);
						final double longVal = MWCXMLReader.readThisDouble(secondItem);
						res = new WorldLocation(latVal, longVal, 0d);
					}
				}
				catch (final Exception e)
				{
					CorePlugin.logError(Status.ERROR,
							"whilst trying to get (lat,long) location off clipboard", e);
				}
			}
			else if (txt.contains("\t")) { //tab delimiter indicates the presence of a property name
				//Example : Location	 05�17'37.76"N 030�49'45.33"E
				final String subString = txt.substring(txt.indexOf("\t")).trim();
				final StringTokenizer latLong=new StringTokenizer(subString);
				if(latLong.countTokens() == 2) {
					final String latString = latLong.nextToken();
					final String longString = latLong.nextToken();
					try {
						//For some reason it is now represented as a " rather than \u2032"
						final Sexagesimal latVal = SexagesimalSupport._DD_MM_SS_SSS.parse(latString.replaceAll("\"", "\u2033").replaceAll("'","\u2032"), false);
						final Sexagesimal longVal = SexagesimalSupport._DD_MM_SS_SSS.parse(longString.replaceAll("\"", "\u2033").replaceAll("'","\u2032"), true);
						res = new WorldLocation(latVal.getCombinedDegrees(), longVal.getCombinedDegrees(), 0d);
					} catch (final Exception e) {
						CorePlugin.logError(Status.ERROR,
								"whilst trying to get (lat,long) location off clipboard", e);
					}
				}
				
				
				
				
				
			}
			else if (numTokens == 8)
			{
				try
				{
					final Double latDegs = MWCXMLReader.readThisDouble(st.nextToken());
					final Double latMin = MWCXMLReader.readThisDouble(st.nextToken());
					final Double latSec = MWCXMLReader.readThisDouble(st.nextToken());
					final char latHem = st.nextToken().charAt(0);
					final Double longDegs = MWCXMLReader.readThisDouble(st.nextToken());
					final Double longMin = MWCXMLReader.readThisDouble(st.nextToken());
					final Double longSec = MWCXMLReader.readThisDouble(st.nextToken());
					final char longHem = st.nextToken().charAt(0);
					res = new WorldLocation(latDegs, latMin, latSec, latHem, longDegs,
							longMin, longSec, longHem, 0);
				}
				catch (final Exception e)
				{
					CorePlugin
							.logError(
									Status.ERROR,
									"whilst trying to get (dd mm ss.ss H dd mm ss.ss h) location off clipboard",
									e);
				}
			}
			else if (numTokens == 6)
			{
				try
				{
					final Double latDegs = MWCXMLReader.readThisDouble(st.nextToken());
					final Double latMin = MWCXMLReader.readThisDouble(st.nextToken());
					final char latHem = st.nextToken().charAt(0);
					final Double longDegs = MWCXMLReader.readThisDouble(st.nextToken());
					final Double longMin = MWCXMLReader.readThisDouble(st.nextToken());
					final char longHem = st.nextToken().charAt(0);
					res = new WorldLocation(latDegs, latMin, 0, latHem, longDegs,
							longMin, 0, longHem, 0);
				}
				catch (final Exception e)
				{
					CorePlugin
							.logError(
									Status.ERROR,
									"whilst trying to get (dd mm.mmm H dd mm.mmm h) location off clipboard",
									e);
				}
			}
		}

		return res;
	}

	
	/**
	 * convenience method to assist placing locations on the clipboard
	 * 
	 * @param loc
	 * @return
	 */
	public static String toClipboard(final WorldLocation loc)
	{
		final String res = LOCATION_STRING_IDENTIFIER + loc.getLat() + ","
				+ loc.getLong() + "," + loc.getDepth();
		return res;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *          the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path)
	{
		return AbstractUIPlugin
				.imageDescriptorFromPlugin("org.mwc.cmap.core", path);
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
	public static void logError(final int severity, final String message, final Throwable exception)
	{
		logError(severity, message, exception, false);
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
		
		final CorePlugin singleton = getDefault();
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
	

	private static ImageRegistry getRegistry()
	{
		return plugin._imageRegistry;
	}

	public static Image getImageFromRegistry(final ImageDescriptor name)
	{
		Image res = null;

		// do we already have an image
		if (getRegistry() == null)
		{
			plugin._imageRegistry = new ImageRegistry();
		}

		// ok - do we have it already?
		res = getRegistry().get(name.toString());

		if (res == null)
		{
			getRegistry().put(name.toString(), name);
			res = getRegistry().get(name.toString());
		}

		// and return it..
		return res;
	}

	public static Image getImageFromRegistry(final String name)
	{
		Image res = null;

		// do we already have an image
		if (getRegistry() == null)
		{
			plugin._imageRegistry = new ImageRegistry();
		}

		// ok - do we have it already?
		res = getRegistry().get(name);

		if (res == null)
		{
			ImageDescriptor desc = getImageDescriptor("icons/16/" + name);
			if (desc == null)
			{
				final Status status = new Status(IStatus.ERROR, "org.mwc.cmap.core",
						"Missing image " + name);
				getDefault().getLog().log(status);
				return null;
			}
			getRegistry().put(name, desc);
			res = getRegistry().get(name);
		}

		// and return it..
		return res;
	}

	/**
	 * show a message to the user
	 * 
	 * @param title
	 * @param message
	 */
	public static void showMessage(final String title, final String message)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog.openInformation(null, title, message);
			}
		});
	}

	/**
	 * run this supplied action, then add it to our undo buffer
	 * 
	 * @param theAction
	 *          the action to run...
	 */
	public static void run(final IUndoableOperation theAction)
	{
		// check the action arrived...
		if (theAction != null)
		{
			// and now run it
			try
			{
				// add, then run the action to the buffer
				getHistory().execute(theAction, null, null);

			}
			catch (final ExecutionException e)
			{
				logError(Status.ERROR, "Whilst adding new action to history buffer", e);
			}
		}
	}
	
	public static IWorkbenchWindow getActiveWindow()
	{
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
	
	public static IWorkbenchPage getActivePage()
	{
		final IWorkbenchWindow activeWindow = getActiveWindow();
		if (activeWindow == null)
			return null;
		return activeWindow.getActivePage();
	}

	public static IViewPart openView(final String viewName)
	{
		IViewPart res = null;
		try
		{
			final IWorkbenchPage page = getActivePage();
			// right, open the view.
			if (page != null)
				res = page.showView(viewName);
		}
		catch (final PartInitException e)
		{
			logError(Status.ERROR, "Failed to open " + viewName + "view", e);
		}
		return res;
	}

	
	public static IViewPart findView(final String viewId)
	{
		final IWorkbenchPage page = getActivePage();
		if (page !=null)
			return page.findView(viewId);
		return null;
	}
	
	public static boolean isActivePart(final IWorkbenchPart part)
	{ 
		final IWorkbenchPage activePage = getActivePage();
		if (activePage == null)
			return false;
		// obtain active page from WorkbenchWindow
	    final IWorkbenchPart activePart = activePage.getActivePart();
	    return activePart == null ? false : activePart.equals(part);
	}
	
	public static IWorkbenchPart getActivePart()
	{ 
		final IWorkbenchPage activePage = getActivePage();
		if (activePage == null)
			return null;
		return activePage.getActivePart();
	}
	
	/**
	 * create an action that we can stick in our manager
	 * 
	 * @param target
	 * @param description
	 * @param host
	 * @return
	 */
	public static Action createOpenHelpAction(final String target,
			final String description, final IViewPart host)
	{
		// sort out the description
		String desc = description;
		if (desc == null)
			desc = "Help";

		final Action res = new Action(desc, Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				host.getViewSite().getWorkbenchWindow().getWorkbench().getHelpSystem()
						.displayHelp(target);
			}
		};
		res.setToolTipText("View help on this component");
		res.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/16/help.png"));
		return res;
	}

	/**
	 * make it easy to declare context sensitive help
	 * 
	 * @param parent
	 * @param context
	 */
	public static void declareContextHelp(final Composite parent,
			final String context)
	{
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, context);
	}

	public static IViewPart openSecondaryView(final String viewName,
			final String secondaryId, final int state)
	{
		IViewPart res = null;
		try
		{
			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			final IWorkbenchPage page = win.getActivePage();
			// right, open the view.
			res = page.showView(viewName, secondaryId, state);
		}
		catch (final PartInitException e)
		{
			logError(Status.ERROR, "Failed to open secondary " + viewName + "view", e);
		}
		return res;
	}

	public SexagesimalFormat getLocationFormat()
	{
		final IPreferenceStore preferenceStore = getPreferenceStore();
		final boolean noSeconds = preferenceStore
				.getBoolean(PREF_BASE60_FORMAT_NO_SECONDS);
		return noSeconds ? SexagesimalSupport._DD_MM_MMM
				: SexagesimalSupport._DD_MM_SS_SSS;
	}

	@Override
	public void lostOwnership(final java.awt.datatransfer.Clipboard arg0,
			final Transferable arg1)
	{
		// don't worry, just ignore it
	}
	
	static public final class ClipboardTest extends junit.framework.TestCase
	{
		public void testNearGreenwich()
		{
			String clipboard = "Location\t 26\u00c2\u00b008'04.55\"N 021\u00c2\u00b056'56.52\"E";
			WorldLocation fromClipboard = fromClipboard(clipboard);
			assertEquals("Expected 26 degrees, 08 minutes, 0.455 seconds north", 26.134597222222222, fromClipboard.getLat());
			assertEquals("Expected 21 degrees, 56 minutes, 56.52 seconds north", 21.949033333333333, fromClipboard.getLong());
			
			clipboard = "Location Attribute with spaces\t 26\u00c2\u00b008'04.55\"N 021\u00c2\u00b056'56.52\"E";
			fromClipboard = fromClipboard(clipboard);
			assertEquals("Expected 26 degrees, 08 minutes, 0.455 seconds north", 26.134597222222222, fromClipboard.getLat());
			assertEquals("Expected 21 degrees, 56 minutes, 56.52 seconds north", 21.949033333333333, fromClipboard.getLong());
		}
	}
	
	public static void infoDialog(final String title, final String msg)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, msg);
			}
		});
	}

	public static void errorDialog(final String title, final String msg)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog.openError(Display.getDefault().getActiveShell(), title, msg);
			}
		});
	}

	public static void warningDialog(final String title, final String msg)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog.openWarning(Display.getDefault().getActiveShell(), title, msg);
			}
		});
	}
	
	/**
	 * Searches in the Eclipse Extension registry for EARTH_MODEL_PROVIDER extensions and creates one.
	 * Throws an error if there is more than one extensions of this kind.
	 * @param registry
	 */
	private void evaluateEarthModelProviderExtension(final IExtensionRegistry registry) throws CoreException {
		final IConfigurationElement[] config =
				registry.getConfigurationElementsFor(EARTH_MODEL_PROVIDER);
		int extension_count = 0;
		Object extension = null;
		for (final IConfigurationElement e : config) {
				final Object o =
						e.createExecutableExtension("class");
				if (o instanceof IEarthModelProvider) {
					extension_count ++;
					extension = o;
				}
		}
		
		if (extension_count > 1)
			throw new CoreException(null);
		executeExtension(extension);
	}
	
	private void executeExtension(final Object o) {
		final ISafeRunnable runnable = new ISafeRunnable() {
			@Override
			public void handleException(final Throwable e) {
				CorePlugin.logError(Status.ERROR, "Exception in providing Earth model", e);
			}

			@Override
			public void run() throws Exception {
			//Setting GeodeticCalculator Adapter as the default Earth model
	    	final EarthModel model = ((IEarthModelProvider) o).getEarthModel(); 
	  		MWC.GenericData.WorldLocation.setModel(model);  
	      }
	    };
	    SafeRunner.run(runnable);
	}


}
