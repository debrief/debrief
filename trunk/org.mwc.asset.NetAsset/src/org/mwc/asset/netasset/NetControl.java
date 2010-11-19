package org.mwc.asset.netasset;

import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
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

	public NetControl()
	{
		_myHosting = new RestHost()
		{

			@Override
			public void deleteScenarioListener(int scenarioId, int listenerId)
			{
				super.deleteScenarioListener(scenarioId, listenerId);
			}

			@Override
			public int newScenarioListener(int scenarioId, final URI url)
			{
				Display.getDefault().asyncExec(new Runnable()
				{

					@Override
					public void run()
					{
						_myView.getList().add(url.toString());
					}
				});
				return super.newScenarioListener(scenarioId, url);
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
				{
					_myHosting.startHosting(_myHosting);
					_myView.setHostName(RestHost.getHostName());
				}
				else
				{
					_myHosting.stopHosting();
					_myView.setHostName("[pending]");
				}
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
						ScenarioType iS = (ScenarioType) instance;
						if (iS != _myScenario)
						{
							setScenario(iS);
						}
					}
				});
		_myPartMonitor.addPartListener(ScenarioType.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						ScenarioType iS = (ScenarioType) instance;
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

	protected void clearScenario()
	{
		if (_myScenario != null)
		{
			_myHosting.clearScenario();
			Display.getDefault().asyncExec(new Runnable()
			{

				@Override
				public void run()
				{
					_myView.setScenarioName("pending");
				}
			});
		}
		_myScenario = null;
	}

	protected void setScenario(ScenarioType iS)
	{
		_myScenario = iS;
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				_myView.setScenarioName(_myScenario.getName());
			}
		});
		_myHosting.setScenario(_myScenario, (int) Math.random() * 1000);
	}
}
