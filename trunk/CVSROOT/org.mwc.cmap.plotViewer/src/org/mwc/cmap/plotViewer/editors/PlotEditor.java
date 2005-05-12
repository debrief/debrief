package org.mwc.cmap.plotViewer.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeData;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeProvider;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public abstract class PlotEditor extends EditorPart{

	////////////////////////////////
	// member data
	////////////////////////////////
	
	/** the graphic data we know about
	 * 
	 */
	protected Layers _myLayers;
	
	/** handle narrative management
	 * 
	 */
	protected NarrativeProvider _theNarrativeProvider;
	
	/** an object to look after all of the time bits
	 *
	 */
	protected TimeManager _timeManager;
	
	/** the object which listens to time-change events.  we remember
	 * it so that it can be deleted when we close
	 */
	protected PropertyChangeListener _timeListener;

	
	/////////////////////////////////////////////////
	// dummy bits applicable for our dummy interface
	/////////////////////////////////////////////////
	Button _myButton;
	Label _myLabel;	
	
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

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	
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
		else
		{
			res = res + "Narrative empty\n";
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
		String msg = "No data yet";
		if(_theNarrativeProvider != null)
			msg = describeData(getEditorInput().getName(),
				_myLayers, _theNarrativeProvider.getNarrative(), _timeManager);
		else
			msg = describeData(getEditorInput().getName(),
					_myLayers, null, _timeManager);
		
		
		if(_myLabel != null)
			_myLabel.setText(msg);
	}}
