package org.mwc.asset.netasset;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.Scenario.ScenarioList;
import org.mwc.asset.comms.restlet.host.BaseHost;
import org.mwc.asset.netasset.model.RestGuest;
import org.mwc.asset.netasset.model.RestHost;
import org.mwc.asset.netasset.view.ControlPane;
import org.mwc.cmap.core.ui_support.PartMonitor;

import ASSET.ScenarioType;

public class NetControl extends ViewPart
{
	public static final String ID = "org.mwc.asset.NetAsset.NetControl";
	private PartMonitor _myPartMonitor;
	protected ScenarioType _myScenario;
	private ControlPane _myView;
	private RestHost _myHosting;

	private BaseHost _myHost;

	public NetControl()
	{
		_myHosting = new RestHost();
		_myHost = new BaseHost()
		{
			public ScenarioType getScenario(int scenarioId)
			{
				return _myScenario;
			}

			@Override
			public ScenarioList getScenarios()
			{
				ScenarioList res = new ScenarioList();
				if (_myScenario != null)
				{
					res.add(new Scenario(_myScenario.getName(), 1));
				}
				return res;
			}
		};
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_myView = new ControlPane(parent, SWT.NONE);
		_myView.addHostingListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Button btn = (Button) e.widget;
				if (btn.getSelection())
					_myHosting.startHosting(_myHost);
				else
					_myHosting.stopHosting();
			}

		});

		listenToMyParts();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	private void listenToMyParts()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(ScenarioType.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{

					@Override
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						ScenarioType iS = (ScenarioType) parentPart;
						if (iS != _myScenario)
						{
							setScenario(iS);
						}
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						ScenarioType iS = (ScenarioType) parentPart;
						if (iS == _myScenario)
						{
							setScenario(null);
						}
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}

	protected void setScenario(ScenarioType iS)
	{
		_myScenario = iS;
		if (_myScenario != null)
			_myView.setScenarioName(iS.getName());
		else
			_myView.setScenarioName("pending");
	}
}
