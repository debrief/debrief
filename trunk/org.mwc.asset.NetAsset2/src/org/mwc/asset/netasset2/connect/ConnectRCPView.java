package org.mwc.asset.netasset2.connect;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset2.core.MClient;
import org.mwc.asset.netasset2.core.AServer;
import org.mwc.asset.netasset2.core.IMClient;
import org.mwc.asset.netasset2.core.PClient;
import org.mwc.asset.netasset2.test.CoreTest;

import ASSET.ScenarioType;
import ASSET.Scenario.MultiScenarioLister;

public class ConnectRCPView extends ViewPart
{
	public static final String ID = "org.mwc.asset.NetAsset2.ConnectView";
	private IVConnect controlV;
	private IMClient model;
	private PClient pres;
	private AServer testServer;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		controlV = new VConnect(parent, SWT.NONE);
		try
		{
			model = new MClient();
			pres = new PClient(controlV, model)
			{
				public IViewPart getView(String viewId)
				{
					return findView(viewId);
				}
			};
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// get ourselves a server
		doDummyWork();
	}

	private IViewPart findView(String viewId)
	{
		IViewPart res = getViewSite().getPage().findView(viewId);
		return res;
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
					return CoreTest.TrackWrapper_Test.getScenarioList();
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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	public void step()
	{
		pres.doStep();
	}

	public void stop()
	{
		pres.doStop();
	}

	public void play()
	{
		pres.doPlay();
	}

	public void pause()
	{
		pres.doPause();
	}

}