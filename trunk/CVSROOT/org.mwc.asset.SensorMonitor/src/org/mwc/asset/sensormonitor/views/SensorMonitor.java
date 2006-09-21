package org.mwc.asset.sensormonitor.views;

import java.beans.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor.LookupSensorComponentsEvent;
import MWC.GUI.Editable;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SensorMonitor extends ViewPart
{
	private Table _table;

	/**
	 * who we're listening to.
	 */
	private PartMonitor _myPartMonitor;

	private Action _trackParticipant;

	private ISelectionChangedListener _selectionChangeListener;

	private SensorType _mySensor;

	private PropertyChangeListener _sensorCalcListener;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */

	class ViewContentProvider implements IStructuredContentProvider
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object parent)
		{
			return new String[] { "One", "Two", "Three" };
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		public Image getImage(Object obj)
		{
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The constructor.
	 */
	public SensorMonitor()
	{
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
//		Composite holder = new Composite(parent,SWT.NONE);
	//	holder.setLayout(new FillLayout());
		
		_table = new Table(parent,SWT.NONE);
		_table.setHeaderVisible(true);
		
//		Button _pusher = new Button(holder, SWT.NONE);
//		_pusher.setText("try me");
//		_pusher.addSelectionListener(new SelectionListener(){
//			public void widgetDefaultSelected(SelectionEvent e)
//			{
//			}
//			public void widgetSelected(SelectionEvent e)
//			{
//				testCall();
//			}});
//		
//		TableColumn tc1 = new TableColumn(_table, SWT.CENTER);
//		TableColumn tc2 = new TableColumn(_table, SWT.CENTER);
//		TableColumn tc3 = new TableColumn(_table, SWT.CENTER);
//		TableColumn tc4 = new TableColumn(_table, SWT.CENTER);
//		TableColumn tc5 = new TableColumn(_table, SWT.CENTER);
//		// ok - do our sensor headings.
//    tc1.setText("Name");
//    tc1.setWidth(30);
//    tc2.setText("State");
//    tc2.setWidth(30);
//    tc3.setText("RP (m)");
//    tc3.setWidth(30);
//    tc4.setText("RI (m)");
//    tc4.setWidth(30);
//    tc5.setText("Actual (m)");	
//    tc5.setWidth(30);
    
//    _table.pack();
		

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		listenToMyParts();
	}

	protected void testCall()
	{
		TableItem t1 = new TableItem(_table, SWT.NONE);
		t1.setText(new String[]{"a","b","c"});
	}

	private void listenToMyParts()
	{
		_selectionChangeListener = new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				newItemSelected(event);
			}
		};


		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						ISelectionProvider iS = (ISelectionProvider) part;
						iS.addSelectionChangedListener(_selectionChangeListener);
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.DEACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						ISelectionProvider iS = (ISelectionProvider) part;
						iS.removeSelectionChangedListener(_selectionChangeListener);
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
	}

	private void hookContextMenu()
	{

	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{

	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_trackParticipant);
	}

	private void makeActions()
	{
		_trackParticipant = new Action("Track", SWT.TOGGLE)
		{
		};
		_trackParticipant.setText("Sync");
		_trackParticipant.setChecked(true);
		_trackParticipant.setToolTipText("Follow selected participant");
		_trackParticipant.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

	}

	protected void newItemSelected(SelectionChangedEvent event)
	{

		if (_trackParticipant.isChecked())
		{
			// right, let's have a look at it.
			ISelection theSelection = event.getSelection();

			// get the first element
			if (theSelection instanceof StructuredSelection)
			{
				StructuredSelection sel = (StructuredSelection) theSelection;
				Object first = sel.getFirstElement();
				// hmm, is it adaptable?
				if (first instanceof EditableWrapper)
				{
					EditableWrapper ew = (EditableWrapper) first;
					Editable ed = ew.getEditable();
					if (ed instanceof SensorType)
					{
						updateSensor((SensorType) ed);
					}
				}
			}
		}
	}

	private void updateSensor(SensorType sensor)
	{
		
		// is this different to our current one?
		if (sensor != _mySensor)
		{
			if (_mySensor != null)
			{
				_mySensor.removeSensorCalculationListener(_sensorCalcListener);
			}
		}

		if (_sensorCalcListener == null)
		{
			_sensorCalcListener = new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt)
				{
					processNewDetection(evt);
				}
			};
		}

		_mySensor = sensor;
		_mySensor.addSensorCalculationListener(_sensorCalcListener);
		
		// and update our title
		this.setPartName(sensor.getName());
		
		// first, remove the existing columns
		TableColumn[] cols = _table.getColumns();;
		for (int i = 0; i < cols.length; i++)
		{
			TableColumn column = cols[i];
			column.dispose();
		}
		
		// ok, now sort out our table
		if(sensor instanceof LookupSensor)
		{
			TableColumn tc1 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc2 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc3 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc4 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc5 = new TableColumn(_table, SWT.CENTER);
			// ok - do our sensor headings.
	   tc1.setText("Name");
	    tc1.setWidth(130);	    
	    tc2.setText("State");
	    tc2.setWidth(60);	    
	    tc3.setText("RP (m)");
	    tc3.setWidth(60);
	    tc4.setText("RI (m)");
   tc4.setWidth(60);	    
	    tc5.setText("Actual (m)");
	    tc5.setWidth(60);
	//    _table.pack(true);
		}
		else
		{
			TableColumn tc1 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc2 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc3 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc4 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc5 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc6 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc7 = new TableColumn(_table, SWT.CENTER);
			TableColumn tc8 = new TableColumn(_table, SWT.CENTER);

			tc1.setText("Name");
			tc2.setText("Loss");
			tc3.setText("Bk Noise");
			tc4.setText("OS Noise");
			tc5.setText("Tgt Noise");
			tc6.setText("RD");
			tc7.setText("DI");
			tc8.setText("SE");			
		}
	}

	/** ok, extract the relevant bits
	 * 
	 * @param evt the event that triggered us.
	 */
	protected void processNewDetection(PropertyChangeEvent evt)
	{
		// clear the table
		_table.clearAll();
		
		if(evt.getNewValue() instanceof LookupSensorComponentsEvent)
		{
			// sort out the lookup fields
			LookupSensorComponentsEvent ev = (LookupSensorComponentsEvent) evt.getNewValue();
			TableItem item1 = new TableItem(_table, SWT.NONE);
			String[] fields = new String[]{ev.getTgtName(),ev.getStateString(),  
					"" + ev.getRP(), "" + ev.getRI(), "" + ev.getActual()};
			item1.setText(fields);
		}
		else
		{
			// sort out the component fields
		}
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		_table.setFocus();
	}
}