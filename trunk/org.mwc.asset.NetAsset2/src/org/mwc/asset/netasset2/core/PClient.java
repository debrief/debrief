package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.mwc.asset.netasset2.TimeView;
import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.AHandler;
import org.mwc.asset.netasset2.common.Network.LightParticipant;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.view.IVControl;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioSteppedListener;

public abstract class PClient implements ScenarioSteppedListener
{
	private final IVControl _view;
	private final IMClient _model;
	private LightScenario _listeningTo;
	private TimeView _timeV;

	public PClient(IVControl view, IMClient model)
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
				InetAddress address = (InetAddress) ss.getFirstElement();
				serverSelected(address.getHostAddress());
			}
		});

		// and now scenario selections
		_view.addScenarioListener(new IDoubleClickListener()
		{

			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection sel = event.getSelection();
				StructuredSelection ss = (StructuredSelection) sel;
				LightScenario scenario = (LightScenario) ss.getFirstElement();
				scenarioSelected(scenario);
			}
		});

		_view.addParticipantListener(new IDoubleClickListener()
		{

			@Override
			public void doubleClick(DoubleClickEvent event)
			{
				// TODO Auto-generated method stub

			}
		});

		_view.setPartContentProvider(new IStructuredContentProvider()
		{

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
			}

			@Override
			public void dispose()
			{
			}

			@Override
			public Object[] getElements(Object inputElement)
			{
				@SuppressWarnings("unchecked")
				Vector<LightParticipant> res = (Vector<LightParticipant>) inputElement;
				return res.toArray();
			}
		});

		_view.setPartLabelProvider(new ITableLabelProvider()
		{
			public void removeListener(ILabelProviderListener listener)
			{
			}

			public boolean isLabelProperty(Object element, String property)
			{
				return false;
			}

			public void dispose()
			{
			}

			public void addListener(ILabelProviderListener listener)
			{
			}

			public String getColumnText(Object element, int columnIndex)
			{
				LightParticipant pt = (LightParticipant) element;
				String res;
				switch (columnIndex)
				{
				case 0:
					res = pt.name;
					break;
				case 1:
					res = pt.category.toShortString();
					break;
				default:
					res = "Other";
					break;
				}
				return res;
			}

			public Image getColumnImage(Object element, int columnIndex)
			{
				return null;
			}
		});
	}

	protected void scenarioSelected(final LightScenario scenario)
	{
		if (_listeningTo != null)
		{
			_model.stopListenScen(_listeningTo.name);
		}

		_listeningTo = scenario;
		_model.listenScen(scenario.name, this);

		Display.getCurrent().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				// ok, now show the participants
				_view.setParticipants(scenario.listOfParticipants);
			}
		});
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
					_view.getScenarioList().add(ls);
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
				list.add(inetAddress);
			}
			_view.enableServers();
		}
	}

	@Override
	public void step(ScenarioType scenario, long newTime)
	{
		// ok, share the time...

		// get the timer view
		if (_timeV == null)
		{
			IViewPart v = getView(TimeView.ID);
			TimeView t = (TimeView) v;
			_timeV = t;
		}
		
		// ok, tell the timer about the time
		_timeV.setTime(newTime);
	}

	abstract public IViewPart getView(String viewId);

	@Override
	public void restart(ScenarioType scenario)
	{
		// TODO
	}

	/** trigger a scenario step
	 * 
	 * @return
	 */
	public void doStep()
	{
		if(_listeningTo != null)
		{
			_model.step(_listeningTo.name);
		}
	}

}
