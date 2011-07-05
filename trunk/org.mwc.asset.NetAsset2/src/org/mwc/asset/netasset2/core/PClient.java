package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.AHandler;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.view.IVClient;

public class PClient
{
	private final IVClient _view;
	private final IMClient _model;

	public PClient(IVClient view, IMClient model)
	{
		_view = view;
		_model = model;

		_view.disableScenarios();
		_view.disableServers();

		// ok, now listen for the view events
		_view.addPingListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				pinged();
			}
		});

		// and for server selections
		_view.addServerListener(new IDoubleClickListener()
		{
			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection sel = event.getSelection();
				StructuredSelection ss = (StructuredSelection) sel;
				String address = ss.getFirstElement().toString();
				serverSelected(address);
			}
		});
		
		// and now scenario selections
		_view.addScenarioListener( new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection sel = event.getSelection();
				StructuredSelection ss = (StructuredSelection) sel;
				String scenario = ss.getFirstElement().toString();
				scenarioSelected(scenario);
			}});
	}

	protected void scenarioSelected(String scenario)
	{
	}

	protected void serverSelected(String address)
	{
		// ok, connect
		try
		{
			_model.connect(address);

			// ok, disable the server list, to stop user re-connecting
			_view.disableServers();

			AHandler<Vector<LightScenario>> handler = new AHandler<Vector<LightScenario>>()
			{
				public void onSuccess(Vector<LightScenario> results)
				{
					showScenarios(results);
				}
			};
			// and get the servers
			_model.getScenarioList(handler);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void showScenarios(final Vector<LightScenario> results)
	{
		System.out.println("received sceanrios");
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				Iterator<LightScenario> items = results.iterator();
				while (items.hasNext())
				{
					Network.LightScenario ls = (Network.LightScenario) items.next();
					// just double-check the scenario has a name
					if (ls.name != null)
						_view.getScenarioList().add(ls.name);
				}

				// and enable them
				_view.enableScenarios();

			}
		});
	}

	protected void pinged()
	{
		// ok, get any servers
		List<InetAddress> adds = _model.discoverHosts();

		if (adds != null)
		{
			ListViewer list = _view.getServerList();
			Iterator<InetAddress> items = adds.iterator();
			while (items.hasNext())
			{
				InetAddress inetAddress = (InetAddress) items.next();
				String thisItem = inetAddress.getHostAddress();
				list.add(thisItem);
			}
			_view.enableServers();
		}
	}

}
