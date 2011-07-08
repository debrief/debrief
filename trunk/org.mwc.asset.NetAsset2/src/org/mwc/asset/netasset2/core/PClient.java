package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.AHandler;
import org.mwc.asset.netasset2.common.Network.LightParticipant;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.common.Network.ScenControl;
import org.mwc.asset.netasset2.connect.IVConnect;
import org.mwc.asset.netasset2.connect.IVConnect.ClickHandler;
import org.mwc.asset.netasset2.connect.IVConnect.ParticipantSelected;
import org.mwc.asset.netasset2.connect.IVConnect.ScenarioSelected;
import org.mwc.asset.netasset2.connect.IVConnect.ServerSelected;
import org.mwc.asset.netasset2.part.IVPartControl;
import org.mwc.asset.netasset2.part.IVPartControl.NewDemStatus;
import org.mwc.asset.netasset2.part.IVPartUpdate;
import org.mwc.asset.netasset2.time.IVTime;
import org.mwc.asset.netasset2.time.IVTimeControl;

import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDecidedListener;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioSteppedListener;

public class PClient implements ScenarioSteppedListener
{
	private final IMClient _model;
	private LightScenario _listeningTo;
	private CombinedListener _partListener;
	private Vector<IVTime> _timeListeners;
	private Vector<IVTimeControl> _timeControllers;
	private Vector<IVPartControl> _partControllers;
	private Vector<IVPartUpdate> _partUpdaters;
	private Vector<IVConnect> _connectors;

	public PClient(IMClient model)
	{
		_model = model;

		_partListener = new CombinedListener();

		// and the list of UI elements
		_partControllers = new Vector<IVPartControl>();
		_partUpdaters = new Vector<IVPartUpdate>();
		_timeListeners = new Vector<IVTime>();
		_timeControllers = new Vector<IVTimeControl>();
		_connectors = new Vector<IVConnect>();

		// ok, now listen for the view events

	}

	private class CombinedListener implements ParticipantMovedListener,
			ParticipantDetectedListener, ParticipantDecidedListener
	{
		public CombinedListener()
		{
		}

		@Override
		public void newDecision(String description, DemandedStatus dem_status)
		{
		}

		@Override
		public void newDetections(DetectionList detections)
		{
		}

		@Override
		public void moved(final Status newStatus)
		{
			// get the status blocks ready

			// now loop through any participant listeners
			Iterator<IVPartUpdate> iter = _partUpdaters.iterator();
			while (iter.hasNext())
			{
				final IVPartUpdate ivPart = iter.next();
				Display.getDefault().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						ivPart.moved(newStatus);
					}
				});
			}
		}

		@Override
		public void restart(ScenarioType scenario)
		{
		}

	}

	protected void participantSelected(final LightParticipant part)
	{
		NewDemStatus newDemStatusListener = new NewDemStatus()
		{
			@Override
			public void demanded(double course, double speed, double depth)
			{
				if (_listeningTo != null)
				{
					_model.controlPart(_listeningTo.name, part.id, course, speed, depth);
				}
			}
		};

		// now loop through any participant listeners
		Iterator<IVPartControl> iter = _partControllers.iterator();
		while (iter.hasNext())
		{
			final IVPartControl ivPart = iter.next();
			ivPart.setEnabled(true);
			ivPart.setParticipant(part.name);
			ivPart.setDemStatusListener(newDemStatusListener);
		}

		Iterator<IVPartUpdate> iter2 = _partUpdaters.iterator();
		while (iter2.hasNext())
		{
			final IVPartUpdate ivPart = iter2.next();
			ivPart.setParticipant(part.name);
		}

		// also start listening to him
		if (_listeningTo != null)
		{
			// cancel any existing listners
			_model.stopListenPart(_listeningTo.name, Network.DUFF_INDEX);

			_model.listenPart(_listeningTo.name, part.id, _partListener,
					_partListener, _partListener);
		}
	}

	protected void scenarioSelected(final LightScenario scenario)
	{
		if (_listeningTo != null)
		{
			_model.stopListenScen(_listeningTo.name);
		}

		// remember it
		_listeningTo = scenario;

		// start listening to it
		System.err.println("about to listen to:" + scenario.name);
		_model.listenScen(scenario.name, this);

		// enable the timer controls
		Iterator<IVTimeControl> iter = _timeControllers.iterator();
		while (iter.hasNext())
		{
			IVTimeControl ivTime = iter.next();
			ivTime.setEnabled(true);
		}

		Iterator<IVConnect> iter2 = _connectors.iterator();
		while (iter2.hasNext())
		{
			IVConnect ivConnect = (IVConnect) iter2.next();
			ivConnect.disableScenarios();
			ivConnect.disableServers();
			ivConnect.enableParticipants();
			ivConnect.setParticipants(scenario.listOfParticipants);
			ivConnect.enableDisconnect();
		}

	}

	protected void serverSelected(InetAddress val)
	{
		// ok, connect
		try
		{
			_model.connect(val.getHostAddress());

			// ok, disable the server list, to stop user re-connecting
			Iterator<IVConnect> iter = _connectors.iterator();
			while (iter.hasNext())
			{
				IVConnect ivPart = iter.next();
				ivPart.disableServers();
			}

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

		// ok, disable the server list, to stop user re-connecting
		Iterator<IVConnect> iter = _connectors.iterator();
		while (iter.hasNext())
		{
			IVConnect ivPart = iter.next();
			ivPart.setScenarios(results);

			// and enable them
			ivPart.enableScenarios();
		}
	}

	protected void pinged()
	{
		// ok, get any servers
		List<InetAddress> adds = _model.discoverHosts();

		if (adds != null)
		{
			// ok, disable the server list, to stop user re-connecting
			Iterator<IVConnect> iter = _connectors.iterator();
			while (iter.hasNext())
			{
				IVConnect ivPart = iter.next();
				ivPart.setServers(adds);
				ivPart.enableServers();
			}
		}
	}

	@Override
	public void step(ScenarioType scenario, long newTime)
	{
		Iterator<IVTime> iter = _timeListeners.iterator();
		while (iter.hasNext())
		{
			IVTime ivTime = (IVTime) iter.next();
			ivTime.newTime(newTime);
		}
	}

	@Override
	public void restart(ScenarioType scenario)
	{
		// TODO
	}

	/**
	 * trigger a scenario step
	 * 
	 * @return
	 */
	public void doStep()
	{
		if (_listeningTo != null)
		{
			_model.step(_listeningTo.name);
		}
	}

	public void doStop()
	{
		if (_listeningTo != null)
		{
			ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.TERMINATE);
			_model.controlScen(sc);
		}
	}

	public void doPlay()
	{
		if (_listeningTo != null)
		{
			ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.PLAY);
			_model.controlScen(sc);
		}
	}

	public void doPause()
	{
		if (_listeningTo != null)
		{
			ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.PAUSE);
			_model.controlScen(sc);
		}
	}

	public void addTimeController(final IVTimeControl timer)
	{
		_timeControllers.add(timer);

		// and listen to the timer
		timer.addStepListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doStep();
			}
		});
		timer.addStopListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doStop();
			}
		});
		timer.addFasterListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doFaster();
			}
		});
		timer.addSlowerListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doSlower();
			}
		});

		timer.addPlayListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// right, what's the current value?
				Button src = (Button) e.getSource();
				String label = src.getText();
				if (label.equals("Play"))
				{
					doPlay();
					timer.setPlayLabel(IVTimeControl.PAUSE);
				}
				else
				{
					doPause();
					timer.setPlayLabel(IVTimeControl.PLAY);
				}
			}
		});
	}

	protected void doSlower()
	{
		if (_listeningTo != null)
		{
			ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.SLOWER);
			_model.controlScen(sc);
		}

	}

	protected void doFaster()
	{
		if (_listeningTo != null)
		{
			ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.FASTER);
			_model.controlScen(sc);
		}

	}

	public void addTimer(final IVTime timer)
	{
		_timeListeners.add(timer);
	}

	public void addPartController(IVPartControl instance)
	{
		_partControllers.add(instance);
	}

	public void addPartUpdater(IVPartUpdate instance)
	{
		_partUpdaters.add(instance);
	}

	public void addConnector(IVConnect view)
	{
		_connectors.add(view);

		view.disableScenarios();
		view.disableServers();
		view.disableDisconnect();

		view.addPingListener(new ClickHandler()
		{
			@Override
			public void clicked()
			{
				pinged();
			}
		});

		view.addDisconnectListener(new ClickHandler()
		{
			@Override
			public void clicked()
			{
				disconnect();
			}
		});

		// and for server selections
		view.addServerListener(new ServerSelected()
		{
			@Override
			public void selected(InetAddress val)
			{
				serverSelected(val);
			}
		});

		// and now scenario selections
		view.addScenarioListener(new ScenarioSelected()
		{
			@Override
			public void selected(LightScenario scenario)
			{
				scenarioSelected(scenario);
			}
		});

		view.addParticipantListener(new ParticipantSelected()
		{
			@Override
			public void selected(LightParticipant participant)
			{
				participantSelected(participant);
			}
		});

		view.setPartContentProvider(new IStructuredContentProvider()
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

		view.setPartLabelProvider(new ITableLabelProvider()
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
				case 2:
					res = pt.activity;
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

	public void disconnect()
	{
		if (_listeningTo != null)
		{
			// ok, stop the participant listeners
			_model.stopListenPart(_listeningTo.name, Network.DUFF_INDEX);

			// and drop the scenario listening
			_model.stopListenScen(_listeningTo.name);

			// also disable the relevant bits
			Iterator<IVConnect> iter = _connectors.iterator();
			while (iter.hasNext())
			{
				IVConnect ivConnect = (IVConnect) iter.next();
				ivConnect.disableParticipants();
				ivConnect.enableServers();
				ivConnect.enableScenarios();
				ivConnect.disableDisconnect();
			}

			Iterator<IVTimeControl> iter2 = _timeControllers.iterator();
			while (iter2.hasNext())
			{
				IVTimeControl ivTime = iter2.next();
				ivTime.setEnabled(false);
			}

			Iterator<IVPartControl> iter3 = _partControllers.iterator();
			while (iter3.hasNext())
			{
				IVPartControl ivPart = (IVPartControl) iter3.next();
				ivPart.setEnabled(false);
			}

			_listeningTo = null;

		}
	}

}
