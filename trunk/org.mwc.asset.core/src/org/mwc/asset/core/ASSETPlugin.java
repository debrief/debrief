package org.mwc.asset.core;

import java.io.InputStream;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.layer_manager.views.LayerMgrDragDropSupport;
import org.mwc.cmap.layer_manager.views.LayerMgrDragDropSupport.XMLFileDropHandler;
import org.osgi.framework.BundleContext;

import ASSET.ParticipantType;
import ASSET.GUI.Workbench.Plotters.*;
import ASSET.Models.Decision.BehaviourList;
import ASSET.Models.Sensor.SensorList;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.Layers;

/**
 * The activator class controls the plug-in life cycle
 */
public class ASSETPlugin extends AbstractUIPlugin implements IStartup
{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.asset.core";

	public static final String SCENARIO_CONTROLLER = "org.mwc.asset.ScenarioController";

	public static final String VESSEL_MONITOR = "org.mwc.asset.VesselMonitor";

	public static final String SENSOR_MONITOR = "org.mwc.asset.SensorMonitor";

	// The shared instance
	private static ASSETPlugin plugin;

	/**
	 * somebody to help create images
	 */
	private ASSETImageHelper _myImageHelper;

	/**
	 * The constructor
	 */
	public ASSETPlugin()
	{
		plugin = this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static ASSETPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * error logging utility
	 * 
	 * @param severity
	 *          the severity; one of <code>OK</code>, <code>ERROR</code>,
	 *          <code>INFO</code>, <code>WARNING</code>, or
	 *          <code>CANCEL</code>
	 * @param message
	 *          a human-readable message, localized to the current locale
	 * @param exception
	 *          a low-level exception, or <code>null</code> if not applicable
	 */
	public static void logError(int severity, String message, Throwable exception)
	{
		Status stat = new Status(severity, "org.mwc.asset.core", Status.OK, message,
				exception);
		getDefault().getLog().log(stat);
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
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.mwc.asset.core", path);
	}

	protected static class XMLParticipantDropHandler extends XMLFileDropHandler
	{
		@SuppressWarnings("unchecked")
		public XMLParticipantDropHandler(String[] elementTypes, Class[] targetTypes)
		{
			super(elementTypes, targetTypes);
		}

		public void handleDrop(InputStream source, MWC.GUI.Editable targetElement,
				Layers parent)
		{
			ParticipantType part = ASSETReaderWriter.importParticipant("unknown", source);
			ScenarioLayer layer = (ScenarioLayer) targetElement;
			CoreScenario cs = (CoreScenario) layer.getScenario();
			cs.addParticipant(part.getId(), part);
			// fire modified on the layer
			parent.fireModified(layer);
		}
	}

	/**
	 * do our pre-startup processing
	 */
	public void earlyStartup()
	{
		XMLFileDropHandler parts = new XMLParticipantDropHandler(new String[] { "SSK",
				"FixedWing", "Torpedo", "SSN", "Helo", "Surface" },
				new Class[] { ScenarioLayer.class });

		XMLFileDropHandler behaviours = new XMLFileDropHandler(new String[] { "Waterfall",
				"Sequence", "Switch" }, new Class[] { BehavioursPlottable.class })
		{
			public void handleDrop(InputStream source, MWC.GUI.Editable targetElement,
					Layers parent)
			{
				// get the model for this element
				BehavioursPlottable bp = (BehavioursPlottable) targetElement;

				if (bp.getDecisionModel() instanceof BehaviourList)
				{
					BehaviourList bl = (BehaviourList) bp.getDecisionModel();
					ASSETReaderWriter.importThis(bl, null, source);
					parent.fireModified(bp.getTopLevelLayer());
				}
			}
		};

		XMLFileDropHandler sensors = new XMLFileDropHandler(new String[] { "BroadbandSensor",
				"ActiveBroadbandSensor", "DippingActiveBroadbandSensor", "NarrowbandSensor",
				"OpticLookupSensor", "RadarLookupSensor", "MADLookupSensor",
				"ActiveInterceptSensor", }, new Class[] { SensorsPlottable.class })
		{
			public void handleDrop(InputStream source, MWC.GUI.Editable targetElement,
					Layers parent)
			{
				// get the model for this element
				SensorsPlottable bp = (SensorsPlottable) targetElement;

				SensorList sl = bp.getSensorFit();
				ASSETReaderWriter.importThis(sl, null, source);
				parent.fireModified(bp.getTopLevelLayer());
			}
		};

		LayerMgrDragDropSupport.addDropHelper(parts);
		LayerMgrDragDropSupport.addDropHelper(behaviours);
		LayerMgrDragDropSupport.addDropHelper(sensors);

		_myImageHelper = new ASSETImageHelper();
		// give the LayerManager our image creator.
		org.mwc.cmap.layer_manager.views.support.ViewLabelProvider
				.addImageHelper(_myImageHelper);
	}
}
