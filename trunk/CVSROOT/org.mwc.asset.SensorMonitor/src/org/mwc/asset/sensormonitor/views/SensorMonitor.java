package org.mwc.asset.sensormonitor.views;

import java.beans.*;
import java.util.Iterator;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;

import ASSET.Models.SensorType;
import ASSET.Models.Detection.*;
import ASSET.Participants.ParticipantDetectedListener;
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
	private TableViewer viewer;

	/**
	 * who we're listening to.
	 */
	private PartMonitor _myPartMonitor;

	private Action _trackParticipant;

	private ISelectionChangedListener _selectionChangeListener;

	private SensorType _mySensor;

	private ParticipantDetectedListener _detectionListener;

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
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		listenToMyParts();
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

		// try to add ourselves to listen out for page changes
		// getSite().getWorkbenchWindow().getPartService().addPartListener(this);

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
						System.out.println("new editable:" + ed);
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
				_mySensor.removeParticipantDetectedListener(_detectionListener);
				_mySensor.removeSensorCalculationListener(_sensorCalcListener);
			}
		}

		if (_detectionListener == null)
		{
			_detectionListener = new ParticipantDetectedListener()
			{
				public void newDetections(DetectionList detections)
				{
					viewer.getTable().clearAll();
					for (Iterator iter = detections.iterator(); iter.hasNext();)
					{
						DetectionEvent det = (DetectionEvent) iter.next();
						String msg = "Brg:" + det.getBearing();
						msg += " Tgt:" + det.getTarget();
						viewer.add(msg);
					}
				}

				public void restart()
				{
				}
			};
			_sensorCalcListener = new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt)
				{
					System.out.println("new calc!!!");
				}
			};
		}

		_mySensor = sensor;
		_mySensor.addParticipantDetectedListener(_detectionListener);
		_mySensor.addSensorCalculationListener(_sensorCalcListener);
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(viewer.getControl().getShell(), "Sensor Monitor",
				message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}
}