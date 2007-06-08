/**
 * 
 */
package org.mwc.debrief.core.loaders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.debrief.core.interfaces.IPlotLoader;

/** convenience class which handles loading/creating extensions
 * @author ian.mayo
 *
 */
public abstract class LoaderManager
{
	private ArrayList _loaders;
	
	// Extension point tag and attributes in plugin.xml
	private String EXTENSION_POINT_ID;
	private String EXTENSION_TAG;
	private String PLUGIN_ID;	
	private String EXTENSION_TAG_LABEL_ATTRIB = "name";
	

	public LoaderManager(String extensionId, String extensionTag, String pluginId)
	{
		EXTENSION_POINT_ID = extensionId;
		EXTENSION_TAG = extensionTag;
		PLUGIN_ID = pluginId;
		
		getDataLoaders();
	}
	
	private Vector getDataLoaders()
	{		
		Vector res = new Vector(0,1);
		
		CorePlugin.logError(Status.INFO, "Starting to load Debrief data importers", null);

		_loaders = new ArrayList();
		IExtensionPoint point = Platform.getExtensionRegistry()
				.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);

		// check: Any <extension> tags for our extension-point?
		if (point != null) {
			IExtension[] extensions = point.getExtensions();

			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] ces = extensions[i].getConfigurationElements();

				for (int j = 0; j < ces.length; j++) {
					// if this is the tag we want ("tool") create a descriptor
					// for it
					if (ces[j].getName().equals(EXTENSION_TAG))
						addToolActionDescriptor(ces[j]);
				}
			}
		}

		// Check if no extensions or empty extensions
		if (point == null || getToolActionDescriptors().size() == 0) {
			CorePlugin.logError(Status.WARNING, "No data loaders found", null);
		}

		CorePlugin.logError(Status.INFO, "Finished loading Debrief importers", null);
		
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
		for (int i = 0; i < getToolActionDescriptors().size(); i++) {
			String l = 
				((INamedItem) getToolActionDescriptors().get(i)).getName();
			if (l.equals(label))
				doubleEntry = true;
		}

		// we take the first matching label
		if (!doubleEntry) {
			INamedItem newInstance = createInstance(configElement, label);
			getToolActionDescriptors().add(newInstance);
		} else {
			CorePlugin.logError(Status.ERROR, "Tag:" + configElement.getName() + " failed to load. " + label + " already. Check for double-entry in plugin.xml", null);
		}

	}	

	/** create one of our objects from the details supplied
	 * 
	 * @param configElement
	 * @param label
	 * @return
	 */
	abstract public INamedItem createInstance(IConfigurationElement configElement, String label);

	public IPlotLoader[] findLoadersFor(String fileName)
	{
		Vector list = new Vector(0,1);
		
		for (Iterator iter = _loaders.iterator(); iter.hasNext();)
		{
			IPlotLoader element = (IPlotLoader) iter.next();

			// can it do it?
			if(element.canLoad(fileName))
			{
				// cool, add to the list
				list.add(element);
			}
		}
		
		IPlotLoader[] res = {};
		
		if(list.size() > 0)
		{
			Object [] tmp = list.toArray(res);
			res = (IPlotLoader[])  tmp; 
		}
		
		return res;
	}

}
