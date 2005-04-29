package org.mwc.cmap.plotViewer.editors;

import java.util.Enumeration;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.EditorPart;
import org.mwc.cmap.core.DataTypes.Narrative.*;
import org.mwc.cmap.core.DataTypes.Temporal.TemporalDataset;

import MWC.GUI.*;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.*;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.WorldLocation;

public class PlotEditor extends EditorPart implements NarrativeProvider {

	////////////////////////////////
	// member data
	////////////////////////////////
	
	/** the graphic data we know about
	 * 
	 */
	Layers _myLayers;
	
	/** any narrative data we know about
	 * 
	 *
	 */
	NarrativeData _myNarrative;
	
	////////////////////////////////
	// constructor
	////////////////////////////////
	
	public PlotEditor() {
		super();

	}
	
	
	/** put some sample data into our objects
	 * 
	 */
	private void createSampleData()
	{
		_myNarrative = NarrativeData.createDummyData(this.getEditorInput().getName(), (int)(Math.random() * 100));
		_myLayers = new Layers();
		Layer bl = new BaseLayer();
		bl.setName("First layer");
		_myLayers.addThisLayer(bl);
		bl.add(new LineShape(new WorldLocation(1,1,1), new WorldLocation(2,2,2)));
		Layer l2 = new BaseLayer();
		l2.setName("Second layer");
		_myLayers.addThisLayer(l2);
		l2.add(new TextLabel(new WorldLocation(1,2,0), "text label"));
	}
	public void dispose() {
		super.dispose();
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
				String msg = describeData(getEditorInput().getName(),  _myLayers, _myNarrative);
				
				_myLabel.setText(msg);
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
	
	
	
	/* (non-Javadoc)
	 * @see org.mwc.cmap.plotViewer.DataTypes.Narrative.NarrativeProvider#getNarrative()
	 */
	public NarrativeData getNarrative()
	{
		return _myNarrative;
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
			return this;
		}
		else if(adapter == TemporalDataset.class)
		{
			// ok, sort out the time period
			// first the outer time of the layers
			res = null;
		}
		
		return res;
	}

	private static String describeData(String dataName, Layers theLayers, NarrativeData narrative)
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
			res = res + "Narrative:" + narrative.getData().size() + " elements";
		}
		
		
		return res;
	}
	
}
