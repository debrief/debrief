/**
 * 
 */
package org.mwc.debrief.core.editors;

import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeData;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeProvider;
import org.mwc.debrief.core.CorePlugin;
import org.mwc.debrief.core.interfaces.INamedItem;
import org.mwc.debrief.core.interfaces.IPlotLoader;

import Debrief.Tools.Tote.Watchable;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.TextLabel;
import MWC.GenericData.HiResDate;
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
	
	public PlotEditor()
	{
		_myLayers = new Layers();		
		
		_myLayers.addDataExtendedListener(new DataListener(){

			public void dataModified(Layers theData, Layer changedLayer)
			{}

			public void dataExtended(Layers theData)
			{
				layersExtended();
			}

			public void dataReformatted(Layers theData, Layer changedLayer)
			{}
			
		});
	}

	/** new data has been added - have a look at the times
	 * 
	 *
	 */
	private void layersExtended()
	{
		

	}

	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// TODO Auto-generated method stub
		setSite(site);
		setInput(input);

		// ok - declare and load the supplemental plugins which can load datafiles
		initialiseFileLoaders();

		// 
		boolean dataLoaded = false;
		
		// right, see if any of them will do our edit
		IPlotLoader[] loaders = _loader.findLoadersFor(input);
		
		// did we find any?
		if(loaders.length > 0)
		{
			// cool, give them a go...
			try
			{
				for (int i = 0; i < loaders.length; i++)
				{
					IPlotLoader thisLoader = loaders[i];
					
					// get it to load.  Just in case it's an asychronous load operation, we rely on it calling us back (loadingComplete)
					thisLoader.loadFile(this, input);
					dataLoaded = true;
				}
			}
			catch (RuntimeException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(!dataLoaded)
		{
			// and populate our data
			createSampleData();			
			dataLoaded = true;
		}
		
		
		// lastly, set the title (if we have one)
		this.setPartName(input.getName());
		this.setContentDescription("Includes imported Replay data");
	}

	/**
	 * 
	 */
	private void initialiseFileLoaders()
	{
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
	}
	
	private static TimePeriod getPeriodFor(Layers theData)
	{
		TimePeriod res = null;

		for (Enumeration iter = theData.elements(); iter.hasMoreElements();)
		{
			Layer thisLayer = (Layer) iter.nextElement();
			
			// and through this layer
			if(thisLayer instanceof TrackWrapper)
			{
				TrackWrapper thisT = (TrackWrapper) thisLayer;
				res = extend(res, thisT.getStartDTG());
				res = extend(res, thisT.getEndDTG());
			}
			else if(thisLayer instanceof BaseLayer)				
			{
				Enumeration elements = thisLayer.elements();
				while(elements.hasMoreElements())
				{
					Plottable nextP = (Plottable) elements.nextElement();
					if(nextP instanceof Watchable)
					{
						Watchable wrapped = (Watchable) nextP;
						res = extend(res, wrapped.getTime());
					}
				}
			}
		}
		
		return res;
	}
	
	private static TimePeriod extend(TimePeriod period, HiResDate date)
	{
		if(period == null)
		{
			period = new TimePeriod.BaseTimePeriod(date, date);
		}
		else		
			period.extend(date);
		
		return period;
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
	/** method called when a helper object has completed a plot-load operation
	 * 
	 * @param source
	 */
	public void loadingComplete(Object source)
	{
		CorePlugin.logError(Status.INFO, "File load received", null);
		
		super.loadingComplete(source);
		
		// and update the time management bits
		TimePeriod timePeriod = getPeriodFor(_myLayers);
		
		if(timePeriod != null)
		{
			System.out.println("time period for data found..");
			
			super._timeManager.setPeriod(this,timePeriod);
			
			// also give it a current DTG
			super._timeManager.setTime(this, timePeriod.getStartDTG());
		}				
	}
	
}
