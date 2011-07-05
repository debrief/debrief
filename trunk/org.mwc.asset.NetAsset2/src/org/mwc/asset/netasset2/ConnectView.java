package org.mwc.asset.netasset2;

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
import org.mwc.asset.netasset2.view.IVControl;
import org.mwc.asset.netasset2.view.VControl;

import ASSET.ScenarioType;
import ASSET.Scenario.MultiScenarioLister;

public class ConnectView extends ViewPart
{
	public static final String ID = "org.mwc.asset.NetAsset2.ConnectView";
	private IVControl controlV;
	private IMClient model;
	private PClient pres;
	private AServer testServer;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		controlV = new VControl(parent, SWT.NONE);
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
}