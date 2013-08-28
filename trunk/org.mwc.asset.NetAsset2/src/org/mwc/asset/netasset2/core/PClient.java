package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import org.mwc.asset.netCore.common.Network;
import org.mwc.asset.netCore.common.Network.AHandler;
import org.mwc.asset.netCore.common.Network.LightParticipant;
import org.mwc.asset.netCore.common.Network.LightScenario;
import org.mwc.asset.netCore.common.Network.ScenControl;
import org.mwc.asset.netCore.core.AServer;
import org.mwc.asset.netCore.core.IMClient;
import org.mwc.asset.netCore.test.CoreTest;
import org.mwc.asset.netasset2.Activator;
import org.mwc.asset.netasset2.connect.IVConnect;
import org.mwc.asset.netasset2.connect.IVConnect.ClickHandler;
import org.mwc.asset.netasset2.connect.IVConnect.ParticipantSelected;
import org.mwc.asset.netasset2.connect.IVConnect.ScenarioSelected;
import org.mwc.asset.netasset2.connect.IVConnect.ServerSelected;
import org.mwc.asset.netasset2.part.IVPartControl;
import org.mwc.asset.netasset2.part.IVPartControl.NewDemStatus;
import org.mwc.asset.netasset2.part.IVPartMovement;
import org.mwc.asset.netasset2.time.IVTime;
import org.mwc.asset.netasset2.time.IVTimeControl;

import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDecidedListener;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.MultiScenarioLister;
import ASSET.Scenario.ScenarioSteppedListener;

public class PClient implements ScenarioSteppedListener
{
	private final IMClient _model;
	private LightScenario _listeningTo;
	private final CombinedListener _partListener;
	private final Vector<IVTime> _timeListeners;
	private final Vector<IVTimeControl> _timeControllers;
	private final Vector<IVPartControl> _partControllers;
	private final Vector<IVPartMovement> _partMovers;
	private final Vector<ParticipantDetectedListener> _partDetectors;
	private final Vector<IVConnect> _connectors;

	private AServer _selfHostServer;

	public PClient(final IMClient model)
	{
		_model = model;
		_partListener = new CombinedListener();

		// and the list of UI elements
		_partControllers = new Vector<IVPartControl>();
		_partMovers = new Vector<IVPartMovement>();
		_partDetectors = new Vector<ParticipantDetectedListener>();
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
		public void newDecision(final String description, final DemandedStatus dem_status)
		{
		}

		@Override
		public void newDetections(final DetectionList detections)
		{
			// now loop through any participant listeners
			final Iterator<ParticipantDetectedListener> iter = _partDetectors.iterator();
			while (iter.hasNext())
			{
				final ParticipantDetectedListener ivPart = iter.next();
				Display.getDefault().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						ivPart.newDetections(detections);
					}
				});
			}
		}

		@Override
		public void moved(final Status newStatus)
		{
			// now loop through any participant listeners
			final Iterator<IVPartMovement> iter = _partMovers.iterator();
			while (iter.hasNext())
			{
				final IVPartMovement ivPart = iter.next();
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
		public void restart(final ScenarioType scenario)
		{
		}

	}

	protected void participantSelected(final LightParticipant part)
	{
		final NewDemStatus newDemStatusListener = new NewDemStatus()
		{
			@Override
			public void demanded(final double course, final double speed, final double depth)
			{
				if (_listeningTo != null)
				{
					_model.controlPart(_listeningTo.name, part.id, course, speed, depth);
				}
			}
		};

		// now loop through any participant listeners
		final Iterator<IVPartControl> iter = _partControllers.iterator();
		while (iter.hasNext())
		{
			final IVPartControl ivPart = iter.next();
			ivPart.setEnabled(true);
			ivPart.setParticipant(part.name);
			ivPart.setDemStatusListener(newDemStatusListener);
		}

		final Iterator<IVPartMovement> iter2 = _partMovers.iterator();
		while (iter2.hasNext())
		{
			final IVPartMovement ivPart = iter2.next();
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
		final Iterator<IVTimeControl> iter = _timeControllers.iterator();
		while (iter.hasNext())
		{
			final IVTimeControl ivTime = iter.next();
			ivTime.setEnabled(true);
		}

		final Iterator<IVConnect> iter2 = _connectors.iterator();
		while (iter2.hasNext())
		{
			final IVConnect ivConnect = (IVConnect) iter2.next();
			ivConnect.disableScenarios();
			ivConnect.disableServers();
			ivConnect.enableParticipants();
			ivConnect.setParticipants(scenario.listOfParticipants);
			ivConnect.enableDisconnect();
		}

	}

	protected void serverSelected(final InetAddress val)
	{
		// ok, connect
		try
		{
			_model.connect(val.getHostAddress());

			// ok, disable the server list, to stop user re-connecting
			final Iterator<IVConnect> iter = _connectors.iterator();
			while (iter.hasNext())
			{
				final IVConnect ivPart = iter.next();
				ivPart.disableServers();
			}

			final AHandler<Vector<LightScenario>> handler = new AHandler<Vector<LightScenario>>()
			{
				public void onSuccess(final Vector<LightScenario> results)
				{
					showScenarios(results);
				}
			};
			// and get the servers
			_model.getScenarioList(handler);
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void showScenarios(final Vector<LightScenario> results)
	{
		System.out.println("received sceanrios");

		// ok, disable the server list, to stop user re-connecting
		final Iterator<IVConnect> iter = _connectors.iterator();
		while (iter.hasNext())
		{
			final IVConnect ivPart = iter.next();
			ivPart.setScenarios(results);

			// and enable them
			ivPart.enableScenarios();
		}
	}

	protected void pinged()
	{
		// ok, get any servers
		final List<InetAddress> adds = _model.discoverHosts();

		// ok, disable the server list, to stop user re-connecting
		final Iterator<IVConnect> iter = _connectors.iterator();
		while (iter.hasNext())
		{
			final IVConnect ivPart = iter.next();
			
			if (adds != null)
			{
				ivPart.setServers(adds);
				ivPart.enableServers();
			}
		}
	}

	@Override
	public void step(final ScenarioType scenario, final long newTime)
	{
		final Iterator<IVTime> iter = _timeListeners.iterator();
		while (iter.hasNext())
		{
			final IVTime ivTime = (IVTime) iter.next();
			ivTime.newTime(newTime);
		}
	}

	@Override
	public void restart(final ScenarioType scenario)
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
			final ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.TERMINATE);
			_model.controlScen(sc);
		}
	}

	public void doPlay()
	{
		if (_listeningTo != null)
		{
			final ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.PLAY);
			_model.controlScen(sc);
		}
	}

	public void doPause()
	{
		if (_listeningTo != null)
		{
			final ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.PAUSE);
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
			public void widgetSelected(final SelectionEvent e)
			{
				doStep();
			}
		});
		timer.addStopListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				doStop();
			}
		});
		timer.addFasterListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				doFaster();
			}
		});
		timer.addSlowerListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				doSlower();
			}
		});

		timer.addPlayListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				// right, what's the current value?
				final Button src = (Button) e.getSource();
				final String label = src.getText();
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
			final ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.SLOWER);
			_model.controlScen(sc);
		}

	}

	protected void doFaster()
	{
		if (_listeningTo != null)
		{
			final ScenControl sc = new ScenControl(_listeningTo.name, ScenControl.FASTER);
			_model.controlScen(sc);
		}

	}

	public void addTimer(final IVTime listener)
	{
		if (!_timeListeners.contains(listener))
			_timeListeners.add(listener);
	}

	public void addPartController(final IVPartControl listener)
	{
		if (!_partControllers.contains(listener))
			_partControllers.add(listener);
	}

	public void addPartDetector(final ParticipantDetectedListener listener)
	{
		if (!_partDetectors.contains(listener))
			_partDetectors.add(listener);
	}

	public void addPartUpdater(final IVPartMovement instance)
	{
		if (!_partDetectors.contains(instance))
			_partMovers.add(instance);
	}

	public void addConnector(final IVConnect view)
	{
		if (!_connectors.contains(view))
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

		view.addSelfHostListener(new IVConnect.BooleanHandler()
		{
			public void change(final boolean val)
			{
				selfHostChanged(val);
			}
		});

		view.addManualListener(new ClickHandler()
		{

			@Override
			public void clicked()
			{
				getHost();
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
			public void selected(final InetAddress val)
			{
				serverSelected(val);
			}
		});

		// and now scenario selections
		view.addScenarioListener(new ScenarioSelected()
		{
			@Override
			public void selected(final LightScenario scenario)
			{
				scenarioSelected(scenario);
			}
		});

		view.addParticipantListener(new ParticipantSelected()
		{
			@Override
			public void selected(final LightParticipant participant)
			{
				participantSelected(participant);
			}
		});

		view.setPartContentProvider(new IStructuredContentProvider()
		{

			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
			{
			}

			@Override
			public void dispose()
			{
			}

			@Override
			public Object[] getElements(final Object inputElement)
			{
				@SuppressWarnings("unchecked")
				final
				Vector<LightParticipant> res = (Vector<LightParticipant>) inputElement;
				return res.toArray();
			}
		});

		view.setPartLabelProvider(new ITableLabelProvider()
		{
			public void removeListener(final ILabelProviderListener listener)
			{
			}

			public boolean isLabelProperty(final Object element, final String property)
			{
				return false;
			}

			public void dispose()
			{
			}

			public void addListener(final ILabelProviderListener listener)
			{
			}

			public String getColumnText(final Object element, final int columnIndex)
			{
				final LightParticipant pt = (LightParticipant) element;
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

			public Image getColumnImage(final Object element, final int columnIndex)
			{
				return null;
			}
		});
	}

	protected void selfHostChanged(final boolean val)
	{

		try
		{
			if (_selfHostServer == null)
			{
				_selfHostServer = new AServer();
				// ok, give the server some data
				final MultiScenarioLister lister = new MultiScenarioLister()
				{

					@Override
					public Vector<ScenarioType> getScenarios()
					{
						return CoreTest.getScenarioList();
					}
				};
				_selfHostServer.setDataProvider(lister);

				// take note of address
				System.out.println("My address is " + InetAddress.getLocalHost());
			}

			if (val)
				_selfHostServer.start();
			else
				_selfHostServer.stop();

		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void getHost()
	{
		IVConnect conny;
		// have we got a connector?
		if (_connectors.size() > 0)
		{
			conny = _connectors.firstElement();
			final String address = conny.getString("Connect to ASSET Server",
					"Please type in IP address");
			if (address != null)
			{
				try
				{
					final InetAddress in = InetAddress.getByName(address);
					serverSelected(in);
				}
				catch (final UnknownHostException e)
				{
					Activator.logError(org.eclipse.core.runtime.Status.ERROR,
							"Whilst connecting to ASSET server", e);
				}
			}
		}
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
			final Iterator<IVConnect> iter = _connectors.iterator();
			while (iter.hasNext())
			{
				final IVConnect ivConnect = (IVConnect) iter.next();
				ivConnect.disableParticipants();
				ivConnect.enableServers();
				ivConnect.enableScenarios();
				ivConnect.disableDisconnect();
			}

			final Iterator<IVTimeControl> iter2 = _timeControllers.iterator();
			while (iter2.hasNext())
			{
				final IVTimeControl ivTime = iter2.next();
				ivTime.setEnabled(false);
			}

			final Iterator<IVPartControl> iter3 = _partControllers.iterator();
			while (iter3.hasNext())
			{
				final IVPartControl ivPart = (IVPartControl) iter3.next();
				ivPart.setEnabled(false);
			}

			_listeningTo = null;

		}
	}

}
