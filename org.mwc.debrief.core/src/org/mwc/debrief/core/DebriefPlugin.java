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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.mwc.debrief.core.ContextOperations.GenerateInfillSegment;
import org.mwc.debrief.core.ContextOperations.GenerateNewNarrativeEntry;
import org.mwc.debrief.core.ContextOperations.GenerateNewSensor;
import org.mwc.debrief.core.ContextOperations.GenerateNewSensorContact;
import org.mwc.debrief.core.ContextOperations.GenerateSensorRangePlot;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegment;
import org.mwc.debrief.core.ContextOperations.GenerateTUASolution;
import org.mwc.debrief.core.ContextOperations.GenerateTrack;
import org.mwc.debrief.core.ContextOperations.GroupTracks;
import org.mwc.debrief.core.ContextOperations.ImportAsTrack;
import org.mwc.debrief.core.ContextOperations.InterpolateTrack;
import org.mwc.debrief.core.ContextOperations.MergeContacts;
import org.mwc.debrief.core.ContextOperations.MergeTracks;
import org.mwc.debrief.core.ContextOperations.RainbowShadeSonarCuts;
import org.mwc.debrief.core.ContextOperations.TrimTrack;
import org.mwc.debrief.core.creators.chartFeatures.InsertTrackSegment;
import org.mwc.debrief.core.ui.DebriefImageHelper;
import org.osgi.framework.BundleContext;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.CompositeTrackWrapper.GiveMeALeg;
import MWC.GUI.Layer;
import MWC.GUI.MessageProvider;
import MWC.Utilities.ReaderWriter.ImportManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebriefPlugin extends AbstractUIPlugin implements MessageProvider
{
	public  static final String PLUGIN_NAME = "org.mwc.debrief.core";
	
	public static final String DEBRIEF_EDITOR = "org.mwc.debrief.PlotEditor";
	public static final String SENSOR_FUSION = "org.mwc.debrief.SensorFusion";
	public static final String MULTI_PATH = "org.mwc.debrief.MultiPath2";
	public static final String MULTI_PATH_TEST = "org.mwc.debrief.MultiPath2Test";
	public static final String TIME_BAR = "org.mwc.debrief.TimeBar";
	public static final String SATC_MAINTAIN_CONTRIBUTIONS = "com.planetmayo.debrief.satc_rcp.views.MaintainContributionsView";


	// The shared instance.
	private static DebriefPlugin plugin;

	/**
	 * Returns the shared instance.
	 */
	public static DebriefPlugin getDefault()
	{
		return plugin;
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
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_NAME,
				path);
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(final String key)
	{
		final ResourceBundle bundle = DebriefPlugin.getDefault()
				.getResourceBundle();
		try
		{
			return (bundle != null) ? bundle.getString(key) : key;
		}
		catch (final MissingResourceException e)
		{
			return key;
		}
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
		final Status stat = new Status(severity, PLUGIN_NAME,
				IStatus.OK, message, exception);
		getDefault().getLog().log(stat);
		
		// also throw it to the console
		if(exception != null)
			exception.printStackTrace();
	}

	// Resource bundle.
	private ResourceBundle resourceBundle;
	private DebriefImageHelper _myImageHelper;

	/**
	 * The constructor.
	 */
	public DebriefPlugin()
	{
		super();
		plugin = this;
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
						.getBundle("org.mwc.debrief.core.CorePluginResources");
		}
		catch (final MissingResourceException x)
		{
			resourceBundle = null;
		}
		return resourceBundle;
	}

	public void show(final String title, final String  message, final int status)
	{
		Display.getCurrent().asyncExec(new Runnable()
		{
			public void run()
			{
				// sort out the status
				if (status == MessageProvider.INFO || status == MessageProvider.OK)
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
							title, message);
				else if(status == MessageProvider.WARNING)
					MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
							title, message);
				else if(status == MessageProvider.ERROR)
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							title, message);
			}
		});
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(final BundleContext context) throws Exception
	{
		super.start(context);

		// also provide someps extra functionality to the right-click editor
		RightClickSupport.addRightClickGenerator(new GenerateTrack());
		RightClickSupport.addRightClickGenerator(new GroupTracks());
		RightClickSupport.addRightClickGenerator(new GenerateInfillSegment());
		RightClickSupport.addRightClickGenerator(new MergeTracks());
		RightClickSupport.addRightClickGenerator(new MergeContacts());
		RightClickSupport.addRightClickGenerator(new GenerateTMASegment());
		RightClickSupport.addRightClickGenerator(new GenerateTUASolution());
		RightClickSupport.addRightClickGenerator(new GenerateSensorRangePlot());
		RightClickSupport.addRightClickGenerator(new GenerateNewSensor());
		RightClickSupport.addRightClickGenerator(new GenerateNewSensorContact());
		RightClickSupport.addRightClickGenerator(new GenerateNewNarrativeEntry());
		RightClickSupport.addRightClickGenerator(new ImportAsTrack());
		RightClickSupport.addRightClickGenerator(new TrimTrack());
		RightClickSupport.addRightClickGenerator(new RainbowShadeSonarCuts());
		RightClickSupport.addRightClickGenerator(new InterpolateTrack());

		// and the Replay importer/exporter (used to export items from the
		// layer-manager)
		ImportManager.addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());

		// make Debrief the default editor for XML files
		final IEditorRegistry editorRegistry = PlatformUI.getWorkbench()
				.getEditorRegistry();
		editorRegistry.setDefaultEditor("*.xml", "org.mwc.debrief.PlotEditor");

		// tell the message provider where it can fire messages to
		MessageProvider.Base.setProvider(this);
		
		_myImageHelper  = new DebriefImageHelper();
		
		// give the LayerManager our image creator.
		CoreViewLabelProvider.addImageHelper(_myImageHelper  );

//		 provide helper for triggering 'new-leg' operation
		final GiveMeALeg triggerNewLeg = new GiveMeALeg(){

			@Override
			public void createLegFor(final Layer parent)
			{
				final InsertTrackSegment ts= new InsertTrackSegment(parent);
				ts.run(null);
			}};
		
		CompositeTrackWrapper.setNewLegHelper(triggerNewLeg);
		CompositeTrackWrapper.initialise(CorePlugin.getToolParent());

	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(final BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}
}
