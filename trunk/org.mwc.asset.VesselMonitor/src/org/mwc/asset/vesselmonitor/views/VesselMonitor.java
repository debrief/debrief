package org.mwc.asset.vesselmonitor.views;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioParticipantWrapper;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.*;
import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

import com.borlander.rac525791.dashboard.*;
import com.borlander.rac525791.dashboard.data.DashboardDataModel;

public class VesselMonitor extends ViewPart
{

	// StatusIndicator _myIndicator;

	private PartMonitor _myPartMonitor;

	/**
	 * we listen out for participants being selected
	 */
	ISelectionChangedListener _selectionChangeListener;

	private Action _trackParticipant;

	private ParticipantType _myPart;

	private ParticipantMovedListener _moveListener;

	private ParticipantDecidedListener _decisionListener;

	DashboardDataModel _dashModel;

	Dashboard _dashboard;

	/**
	 * The constructor.
	 */
	public VesselMonitor()
	{
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		// Composite myLayout = new Composite(parent, SWT.NONE);
		// GridLayout rl = new GridLayout();
		// rl.numColumns = 1;

		_dashboard = new Dashboard(parent);
		_dashModel = _dashboard.getDataModel();

		// _myIndicator = new StatusIndicator(myLayout, SWT.NONE);

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		listenToMyParts();
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
		_trackParticipant.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/synced.gif"));

	}

	private void hookDoubleClickAction()
	{
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
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
					if (ed instanceof ScenarioParticipantWrapper)
					{
						ScenarioParticipantWrapper sw = (ScenarioParticipantWrapper) ed;

						updateParticipant(sw.getParticipant());
					}
				}
			}
		}
	}

	/**
	 * right, a new participant has been selected
	 * 
	 * @param part
	 *          the new participant
	 */
	private void updateParticipant(ParticipantType part)
	{
		// is this already our participant
		if (_myPart != part)
		{
			updateName(part);

			// do we have our listener?
			if (_moveListener == null)
			{
				createListeners();
			}

			// ok, stop listening to the old one
			if (_myPart != null)
			{
				_myPart.removeParticipantMovedListener(_moveListener);
				_myPart.removeParticipantDecidedListener(_decisionListener);
			}

			_myPart = part;

			// ok, start listening to him
			_myPart.addParticipantMovedListener(_moveListener);
			_myPart.addParticipantDecidedListener(_decisionListener);

//			System.out.print("new part:" + _myPart.getName()  + " depth:" + _myPart.getStatus().getLocation().getDepth());
//			if(_myPart.getDemandedStatus() instanceof SimpleDemandedStatus)
//			{
//				SimpleDemandedStatus sds = (SimpleDemandedStatus) _myPart.getDemandedStatus();
//				if(sds != null)
//					System.out.println(" dem height: " + sds.getHeight());
//			}
			
			// let's fire one off to get us started
			updateStatus(_myPart.getStatus());

			// and sort out what it's dowing
			updateDecision(_myPart.getActivity(), _myPart.getDemandedStatus());
		}

	}

	/**
	 * @param part
	 */
	private void updateName(final ParticipantType part)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_dashboard.isDisposed())
				{
					_dashModel.setVesselName(part.getName());
				}
			}
		});
	}

	/**
	 * 
	 */
	private void createListeners()
	{
		_moveListener = new ParticipantMovedListener()
		{
			public void moved(Status newStatus)
			{
				updateStatus(newStatus);
			}

			public void restart(ScenarioType scenario)
			{
			}
		};

		_decisionListener = new ParticipantDecidedListener()
		{
			public void newDecision(String description, DemandedStatus dem_status)
			{
				updateDecision(description, dem_status);
			}

			public void restart(ScenarioType scenario)
			{
			}
		};
	}

	protected void updateDecision(final String description, final DemandedStatus dem_status)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_dashboard.isDisposed())
				{
					if (dem_status instanceof ASSET.Models.Movement.SimpleDemandedStatus)
					{
						_dashModel.setIgnoreDemandedDepth(false);
						_dashModel.setIgnoreDemandedDirection(false);
						_dashModel.setIgnoreDemandedSpeed(false);
						
						ASSET.Models.Movement.SimpleDemandedStatus sds = (SimpleDemandedStatus) dem_status;
						WorldSpeed demSpeed = new WorldSpeed(sds.getSpeed(), WorldSpeed.M_sec);
						double speed =Math.min(demSpeed.getValueIn(WorldSpeed.Kts), AutoScaler.RANGE);
						double height = sds.getHeight();
						
						height = Math.min(height,AutoScaler.RANGE);
						
						_dashModel.setDemandedDirection((int) sds.getCourse());
						_dashModel.setDemandedSpeed((int) speed);
						_dashModel.setDemandedDepth((int) Math.abs(height));
						
					}
					else
					{
						_dashModel.setIgnoreDemandedDepth(true);
						_dashModel.setIgnoreDemandedDirection(true);
						_dashModel.setIgnoreDemandedSpeed(true);
					}
				}
			}
		});
	}

	protected void updateStatus(final Status newStatus)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_dashboard.isDisposed())
				{
					_dashModel.setVesselStatus(newStatus.statusString());

					// do the other bits...
					WorldSpeed ws = newStatus.getSpeed();
					_dashModel.setActualDirection((int) newStatus.getCourse());
					_dashModel.setSpeedUnits("Kts");
					double theDepth = newStatus.getLocation().getDepth();
					if(theDepth > 0)
						_dashModel.setDepthUnits("Depth");
					else
						_dashModel.setDepthUnits("Alt");
					
					theDepth = Math.abs(theDepth);
					theDepth = Math.min(theDepth, AutoScaler.RANGE);
					
					_dashModel.setActualDepth((int)theDepth);
					double theSpeed = ws.getValueIn(WorldSpeed.Kts);
					theSpeed=Math.min(theSpeed, AutoScaler.RANGE);
					_dashModel.setActualSpeed((int)theSpeed);
				}
			}
		});
	}

	/**
	 * 
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		
		// ok, stop listening to the old one
		if (_myPart != null)
		{
			_myPart.removeParticipantMovedListener(_moveListener);
			_myPart.removeParticipantDecidedListener(_decisionListener);
		}
	}
}