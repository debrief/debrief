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
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.mwc.debrief.core.ContextOperations.GenerateInfillSegment;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegment;
import org.mwc.debrief.core.ContextOperations.GenerateTrack;
import org.mwc.debrief.core.ContextOperations.GroupTracks;
import org.mwc.debrief.core.ContextOperations.MergeTracks;
import org.mwc.debrief.core.ui.DebriefImageHelper;
import org.osgi.framework.BundleContext;

import MWC.GUI.MessageProvider;
import MWC.Utilities.ReaderWriter.ImportManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebriefPlugin extends AbstractUIPlugin implements MessageProvider
{
	public  static final String PLUGIN_NAME = "org.mwc.debrief.core";
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
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_NAME,
				path);
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key)
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
	public static void logError(int severity, String message, Throwable exception)
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
	public void start(BundleContext context) throws Exception
	{
		super.start(context);

		// also provide someps extra functionality to the right-click editor
		RightClickSupport.addRightClickGenerator(new GenerateTrack());
		RightClickSupport.addRightClickGenerator(new MergeTracks());
		RightClickSupport.addRightClickGenerator(new GenerateInfillSegment());
		RightClickSupport.addRightClickGenerator(new GroupTracks());
		RightClickSupport.addRightClickGenerator(new GenerateTMASegment());

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


	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}
}
