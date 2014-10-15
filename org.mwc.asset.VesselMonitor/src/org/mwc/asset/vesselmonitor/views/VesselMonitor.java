/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

import ASSET.NetworkParticipant;
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
	public void createPartControl(final Composite parent)
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
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(final IMenuManager manager)
	{

	}

	private void fillLocalToolBar(final IToolBarManager manager)
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
			public void selectionChanged(final SelectionChangedEvent event)
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
					public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
					{
						final ISelectionProvider iS = (ISelectionProvider) part;
						iS.addSelectionChangedListener(_selectionChangeListener);
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.DEACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
					{
						final ISelectionProvider iS = (ISelectionProvider) part;
						iS.removeSelectionChangedListener(_selectionChangeListener);
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
	}

	protected void newItemSelected(final SelectionChangedEvent event)
	{

		if (_trackParticipant.isChecked())
		{
			// right, let's have a look at it.
			final ISelection theSelection = event.getSelection();

			// get the first element
			if (theSelection instanceof StructuredSelection)
			{
				final StructuredSelection sel = (StructuredSelection) theSelection;
				final Object first = sel.getFirstElement();
				// hmm, is it adaptable?
				if (first instanceof EditableWrapper)
				{
					final EditableWrapper ew = (EditableWrapper) first;
					final Editable ed = ew.getEditable();
					if (ed instanceof ScenarioParticipantWrapper)
					{
						final ScenarioParticipantWrapper sw = (ScenarioParticipantWrapper) ed;

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
	private void updateParticipant(final ParticipantType part)
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
	private void updateName(final NetworkParticipant part)
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
			public void moved(final Status newStatus)
			{
				updateStatus(newStatus);
			}

			public void restart(final ScenarioType scenario)
			{
			}
		};

		_decisionListener = new ParticipantDecidedListener()
		{
			public void newDecision(final String description, final DemandedStatus dem_status)
			{
				updateDecision(description, dem_status);
			}

			public void restart(final ScenarioType scenario)
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
						
						final ASSET.Models.Movement.SimpleDemandedStatus sds = (SimpleDemandedStatus) dem_status;
						final WorldSpeed demSpeed = new WorldSpeed(sds.getSpeed(), WorldSpeed.M_sec);
						final double speed =Math.min(demSpeed.getValueIn(WorldSpeed.Kts), AutoScaler.RANGE);
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
					final WorldSpeed ws = newStatus.getSpeed();
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
		
		if (_myPartMonitor != null)
		{
			_myPartMonitor.ditch();
		}
		// ok, stop listening to the old one
		if (_myPart != null)
		{
			_myPart.removeParticipantMovedListener(_moveListener);
			_myPart.removeParticipantDecidedListener(_decisionListener);
		}
	}
}