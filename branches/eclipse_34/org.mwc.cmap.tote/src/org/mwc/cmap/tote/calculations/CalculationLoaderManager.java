/**
 * 
 */
package org.mwc.cmap.tote.calculations;

import java.text.NumberFormat;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.mwc.cmap.tote.TotePlugin;

import Debrief.Tools.Tote.*;
import MWC.GenericData.HiResDate;

/**
 * convenience class which handles loading/creating extensions
 * 
 * @author ian.mayo
 */
public abstract class CalculationLoaderManager
{
	private ArrayList _loaders;

	// Extension point tag and attributes in plugin.xml
	private String EXTENSION_POINT_ID;

	private String EXTENSION_TAG;

	private String PLUGIN_ID;

	private String EXTENSION_TAG_LABEL_ATTRIB = "name";

	public CalculationLoaderManager(String extensionId, String extensionTag,
			String pluginId)
	{
		EXTENSION_POINT_ID = extensionId;
		EXTENSION_TAG = extensionTag;
		PLUGIN_ID = pluginId;

		getCalculations();
	}

	private Vector getCalculations()
	{
		Vector res = new Vector(0, 1);

		_loaders = new ArrayList();
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(
				PLUGIN_ID, EXTENSION_POINT_ID);

		// check: Any <extension> tags for our extension-point?
		if (point != null)
		{
			IExtension[] extensions = point.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				IConfigurationElement[] ces = extensions[i].getConfigurationElements();

				for (int j = 0; j < ces.length; j++)
				{
					// if this is the tag we want ("tool") create a descriptor
					// for it
					if (ces[j].getName().equals(EXTENSION_TAG))
						addToolActionDescriptor(ces[j]);
				}
			}
		}

		// Check if no extensions or empty extensions
		if (point == null || getToolActionDescriptors().size() == 0)
		{
			System.out.println("* No configuration found!");
		}

		return res;
	}

	private ArrayList getToolActionDescriptors()
	{
		return _loaders;
	}

	private void addToolActionDescriptor(IConfigurationElement configElement)
	{
		String label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);

		// get menu item label
		// search for double entries
		boolean doubleEntry = false;
		for (int i = 0; i < getToolActionDescriptors().size(); i++)
		{
			String l = ((toteCalculation) getToolActionDescriptors().get(i)).getTitle();
			if (l.equals(label))
				doubleEntry = true;
		}

		// we take the first matching label
		if (!doubleEntry)
		{
			toteCalculation newInstance = createInstance(configElement, label);
			getToolActionDescriptors().add(newInstance);
		}
		else
		{
			System.out.println("...failed! Reason: Label '" + label
					+ "' already exists.  Check your plugin.xml");
		}

	}

	/**
	 * create one of our objects from the details supplied
	 * 
	 * @param configElement
	 * @param label
	 * @return
	 */
	abstract public toteCalculation createInstance(
			IConfigurationElement configElement, String label);

	public toteCalculation[] findCalculations()
	{
		toteCalculation[] template = new toteCalculation[] {};
		return (toteCalculation[]) _loaders.toArray(template);
	}

	public static class DeferredCalculation implements toteCalculation
	{
		toteCalculation _myCalc = null;

		final private IConfigurationElement _config;

		/**
		 * constructor - stores the information necessary to load the data
		 * 
		 * @param configElement
		 * @param name
		 * @param icon
		 * @param fileTypes
		 */
		public DeferredCalculation(IConfigurationElement configElement,
				String name, String icon)
		{
			_config = configElement;
		}
		
		

		private void checkMe()
		{
			if (_myCalc == null)
			{
				try
				{
					// and create the loader
					_myCalc = (toteCalculation) _config.createExecutableExtension("class");
				} catch (CoreException e)
				{
					TotePlugin.logError(Status.ERROR,
							"Failed to create instance of loader:" + _config, e);
				}
			}
		}

		public String update(Watchable primary, Watchable secondary,
				HiResDate thisTime)
		{
			checkMe();
			return _myCalc.update(primary, secondary, thisTime);
		}

		public double calculate(Watchable primary, Watchable secondary,
				HiResDate thisTime)
		{
			checkMe();
			return _myCalc.calculate(primary, secondary, thisTime);
		}

		public void setPattern(NumberFormat format)
		{
			checkMe();
			_myCalc.setPattern(format);
		}

		public String getTitle()
		{
			checkMe();
			return _myCalc.getTitle();
		}

		public String getUnits()
		{
			checkMe();
			return _myCalc.getUnits();
		}

		public boolean isWrappableData()
		{
			checkMe();
			return _myCalc.isWrappableData();
		}
	}

}
