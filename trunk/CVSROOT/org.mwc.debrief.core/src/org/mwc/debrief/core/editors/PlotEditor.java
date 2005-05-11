/**
 * 
 */
package org.mwc.debrief.core.editors;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeData;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeProvider;
import org.mwc.debrief.core.interfaces.INamedItem;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.TextLabel;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 *
 */
public class PlotEditor extends org.mwc.cmap.plotViewer.editors.PlotEditor
{
	// Extension point tag and attributes in plugin.xml
	private static final String EXTENSION_POINT_ID = "DebriefPlotLoader";
	private static final String EXTENSION_TAG = "loader";
	private static final String EXTENSION_TAG_LABEL_ATTRIB = "name";
	private static final String EXTENSION_TAG_EXTENSIONS_ATTRIB = "extensions";	
	private static final String EXTENSION_TAG_ICON_ATTRIB = "icon";	
	private static final String EXTENSION_TAG_CLASS_ATTRIB = "class";	

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.debrief.core";	
	
	private LoaderManager _loader;

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// TODO Auto-generated method stub
		setSite(site);
		setInput(input);

		// hey - sort out our plot readers
		_loader = new LoaderManager(EXTENSION_POINT_ID, EXTENSION_TAG, PLUGIN_ID)
		{

			INamedItem createInstance(IConfigurationElement configElement, String label)
			{
				// get the attributes
				label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);
				String icon = configElement.getAttribute(EXTENSION_TAG_ICON_ATTRIB);
				String fileTypes = configElement.getAttribute(EXTENSION_TAG_EXTENSIONS_ATTRIB);

				// create the instance
				INamedItem res =	new IPlotLoader.DeferredPlotLoader(configElement, label, icon, fileTypes);
				
				// and return it.
				return res;
			}
			
		};

		// 
		boolean dataLoaded = false;
		
		// right, see if any of them will do our edit
		IPlotLoader[] loaders = _loader.findLoadersFor(input);
		
		if(loaders.length > 0)
		{
			for (int i = 0; i < loaders.length; i++)
			{
				IPlotLoader thisLoader = loaders[i];
				thisLoader.loadFile(this, input);
				dataLoaded = true;
			}
		}

		if(!dataLoaded)
		{
			// and populate our data
			createSampleData();
		}
		
	}
	


	/** put some sample data into our objects
	 * 
	 */
	private void createSampleData()
	{
		_theNarrativeProvider = new NarrativeProvider()
		{
			NarrativeData _myData = null;
			public NarrativeData getNarrative() {
				if(_myData == null)
				 _myData = NarrativeData.createDummyData(getEditorInput().getName(), 3 + (int)(Math.random() * 400));
				
				return _myData;
			}		
		};
		_myLayers = new Layers();
		Layer bl = new BaseLayer();
		bl.setName("First layer");
		_myLayers.addThisLayer(bl);
		bl.add(new LineShape(new WorldLocation(1,1,1), new WorldLocation(2,2,2)));
		Layer l2 = new BaseLayer();
		l2.setName("Second layer");
		_myLayers.addThisLayer(l2);
		l2.add(new TextLabel(new WorldLocation(1,2,0), "text label"));
		
		// make the time manager match the period of the narrative
		TimePeriod narrativePeriod = _theNarrativeProvider.getNarrative().getTimePeriod();
		if(narrativePeriod != null)
		_timeManager.setPeriod(this, narrativePeriod);
		else
			System.out.println("NO TIME PERIOD FOR NARRATIVE!");
		
		
	}

	
}
