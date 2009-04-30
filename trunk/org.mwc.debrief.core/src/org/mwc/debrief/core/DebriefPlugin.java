package org.mwc.debrief.core;

import java.util.*;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.property_support.RightClickSupport;

import org.mwc.debrief.core.ContextOperations.*;
import org.osgi.framework.BundleContext;

import MWC.Utilities.ReaderWriter.ImportManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebriefPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static DebriefPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public DebriefPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		// also provide someps extra functionality to the right-click editor
		RightClickSupport.addRightClickGenerator(new GenerateTrack());
		RightClickSupport.addRightClickGenerator(new InterpolateTrack());		
		RightClickSupport.addRightClickGenerator(new MergeTrack());		
		
		// and the Replay importer/exporter (used to export items from the layer-manager)
		ImportManager.addImporter(new Debrief.ReaderWriter.Replay.ImportReplay());
		
		// make Debrief the default editor for XML files
		IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
		editorRegistry.setDefaultEditor("*.xml", "org.mwc.debrief.PlotEditor");
		
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static DebriefPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DebriefPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.mwc.debrief.core.CorePluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.debrief.core", path);
	}
	
	/** error logging utility
	 * 
	 * @param severity the severity; one of <code>OK</code>, <code>ERROR</code>, 
	 * <code>INFO</code>, <code>WARNING</code>,  or <code>CANCEL</code>
	 * @param message a human-readable message, localized to the
	 *    current locale
	 * @param exception a low-level exception, or <code>null</code> if not
	 *    applicable 
	 */
	public static void logError(int severity, String message, Throwable exception)
	{
		Status stat = new Status(severity,"org.mwc.debrief.core", Status.OK, message, exception);
		getDefault().getLog().log(stat);
	}
}
