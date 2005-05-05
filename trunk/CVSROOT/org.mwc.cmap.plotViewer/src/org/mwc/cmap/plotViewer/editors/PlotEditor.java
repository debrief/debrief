package org.mwc.cmap.plotViewer.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.DataTypes.Narrative.*;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;

import MWC.GUI.*;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.*;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public class PlotEditor extends EditorPart{

	////////////////////////////////
	// member data
	////////////////////////////////
	
	/** the graphic data we know about
	 * 
	 */
	Layers _myLayers;
	
	/** handle narrative management
	 * 
	 */
	NarrativeProvider _theNarrativeProvider;
	
	/** an object to look after all of the time bits
	 *
	 */
	private TimeManager _timeManager;
	
	/** the object which listens to time-change events.  we remember
	 * it so that it can be deleted when we close
	 */
	private PropertyChangeListener _timeListener;
	
	////////////////////////////////
	// constructor
	////////////////////////////////
	
	public PlotEditor() {
		super();
		
		// create the time manager.  cool
		_timeManager = new TimeManager();
		
		// and listen for new times
		_timeListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent arg0)
			{
				// right, the time has changed.  better redraw parts of the plot
				updateLabel();
			}
		};
		
		_timeManager.addListener(_timeListener, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

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
				 _myData = NarrativeData.createDummyData(getEditorInput().getName(), (int)(Math.random() * 100));
				
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
	public void dispose() {
		super.dispose();
		
		// stop listening to the time manager
		_timeManager.removeListener(_timeListener, TimeProvider.TIME_CHANGED_PROPERTY_NAME);
	}
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// TODO Auto-generated method stub
		setSite(site);
		setInput(input);

		System.out.println("loading:" + input.getName());

		// and populate our data
		createSampleData();
	}
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	Button _myButton;
	Label _myLabel;
	
	public void createPartControl(Composite parent) {
		Composite myHolder = new Composite(parent, SWT.NONE);
		myHolder.setLayout(new FillLayout());
		_myButton = new Button(myHolder, SWT.NONE);
		_myButton.setText("push me");
		

		
		_myButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				updateLabel();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		_myLabel = new Label(myHolder, SWT.NONE);
		_myLabel.setText("the label");

	}
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public Object getAdapter(Class adapter)
	{
		Object res = null;
		
		// so, is he looking for the layers?
		if(adapter == Layers.class)
		{
			if(_myLayers != null)
				res = _myLayers;
		}
		else if(adapter == NarrativeProvider.class)
		{
			return _theNarrativeProvider;
		}
		else if(adapter == TimeProvider.class)
		{
			return _timeManager;
		}
		else if(adapter == ControllableTime.class)
		{
			return _timeManager;
		}
		
		return res;
	}

	private static String describeData(String dataName, Layers theLayers, 
				NarrativeData narrative, TimeManager timeManager)
	{
		String res = dataName + "\n";
		
		Enumeration enumer = theLayers.elements();
		while(enumer.hasMoreElements())
		{
			Layer thisL = (Layer) enumer.nextElement();
			res = res + thisL.getName() + "\n"; 
		}
		
		if(narrative != null)
		{
			res = res + "Narrative:" + narrative.getData().size() + " elements" + "\n";
		}
		
		if(timeManager != null)
		{
			HiResDate tNow = timeManager.getTime();
			if(tNow != null)			
				res = res + DebriefFormatDateTime.toStringHiRes(tNow);
			else
				res = res + " time not set";
		}
		
		return res;
	}
	
	/** ok, the time has changed.  update our own time, inform the listeners
	 * 
	 * @param origin
	 * @param newDate
	 */
	public void setNewTime(Object origin, HiResDate newDate)
		{
				updateLabel();
		}
	
	private void updateLabel()
	{
		String msg = describeData(getEditorInput().getName(),
				_myLayers, _theNarrativeProvider.getNarrative(), _timeManager);
		_myLabel.setText(msg);
	}}
