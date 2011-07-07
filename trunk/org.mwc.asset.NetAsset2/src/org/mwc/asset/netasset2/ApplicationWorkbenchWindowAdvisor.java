package org.mwc.asset.netasset2;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.mwc.asset.netasset2.connect.IVConnect;
import org.mwc.asset.netasset2.core.AServer;
import org.mwc.asset.netasset2.core.MClient;
import org.mwc.asset.netasset2.core.PClient;
import org.mwc.asset.netasset2.part.IVPart;
import org.mwc.asset.netasset2.test.CoreTest;
import org.mwc.asset.netasset2.time.IVTime;
import org.mwc.cmap.core.ui_support.PartMonitor;

import ASSET.ScenarioType;
import ASSET.Scenario.MultiScenarioLister;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

	private PartMonitor _myPartMonitor;
	private PClient _presenter;
	private AServer testServer;

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
		
		try
		{
			MClient model = new MClient();
			_presenter = new PClient(model);
		}
		catch (IOException e)
		{
			Activator.logError(Status.ERROR, "Failed to create network model", e);
		}
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen()
	{
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 700));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);

	}

	@Override
	public void postWindowOpen()
	{
		// TODO Auto-generated method stub
		super.postWindowOpen();
		
		// get ourselves a server
		doDummyWork();

		// ok, get ready to learn about open windows
		setupListeners();

		// and examine any that are already open
		triggerListeners();

	}
	
	

	@Override
	public boolean preWindowShellClose()
	{
		// ok, now shut down the server-et-al
		_presenter.disconnect();
		
		return super.preWindowShellClose();
	}

	private void triggerListeners()
	{
		// ok, cycle through the open views and check if we're after any of them
		@SuppressWarnings("deprecation")
		IViewPart[] views = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViews();
		for (int i = 0; i < views.length; i++)
		{
			IViewPart iViewPart = views[i];
			_myPartMonitor.partOpened(iViewPart);
		}

	}

	public void setupListeners()
	{

		// declare the listener
		_myPartMonitor = new PartMonitor(Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getPartService());

		// now listen for types to open
		_myPartMonitor.addPartListener(IVTime.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						storeTimer((IVTime) instance);
					}
				});
		_myPartMonitor.addPartListener(IVConnect.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						storeConnector((IVConnect) instance);
					}
				});
		_myPartMonitor.addPartListener(IVPart.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object instance,
							IWorkbenchPart parentPart)
					{
						storeParticipant((IVPart) instance);
					}
				});

	}

	protected void storeParticipant(IVPart instance)
	{
		_presenter.addPart(instance);
	}

	protected void storeConnector(IVConnect instance)
	{
		_presenter.addConnector(instance);
	}

	protected void storeTimer(IVTime instance)
	{
		_presenter.addTimer(instance);
	}
	

	private void doDummyWork()
	{
		try
		{
			testServer = new AServer();
			// ok, give the server some data
			MultiScenarioLister lister = new MultiScenarioLister()
			{

				@Override
				public Vector<ScenarioType> getScenarios()
				{
					return CoreTest.getScenarioList();
				}
			};
			testServer.setDataProvider(lister);

			// take note of address
			System.out.println("My address is " + InetAddress.getLocalHost());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
