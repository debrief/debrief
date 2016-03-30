/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2016, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.dis.ui.views;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleEdge;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.core.DISModule;
import org.mwc.debrief.dis.core.IDISModule;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISStartResumeListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.listeners.impl.DISContext;
import org.mwc.debrief.dis.listeners.impl.DebriefCollisionListener;
import org.mwc.debrief.dis.listeners.impl.DebriefDetonationListener;
import org.mwc.debrief.dis.listeners.impl.DebriefEventListener;
import org.mwc.debrief.dis.listeners.impl.DebriefFireListener;
import org.mwc.debrief.dis.listeners.impl.DebriefFixListener;
import org.mwc.debrief.dis.listeners.impl.IDISContext;
import org.mwc.debrief.dis.providers.DISFilters;
import org.mwc.debrief.dis.providers.IPDUProvider;
import org.mwc.debrief.dis.providers.network.IDISController;
import org.mwc.debrief.dis.providers.network.IDISNetworkPrefs;
import org.mwc.debrief.dis.providers.network.NetworkDISProvider;
import org.mwc.debrief.dis.runner.SimulationRunner;
import org.mwc.debrief.dis.ui.preferences.DebriefDISNetPrefs;
import org.mwc.debrief.dis.ui.preferences.DebriefDISSimulatorPrefs;
import org.mwc.debrief.dis.ui.preferences.DisPrefs;

import MWC.GUI.CanvasType;
import MWC.GenericData.HiResDate;
import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.Pdu;

public class DisListenerView extends ViewPart
{

  public static final String HELP_CONTEXT = "org.mwc.debrief.help.DISSupport";

  private Button connectButton;
  private Button playButton;
  private Button pauseButton;
  private Button stopButton;
  // private Button resumeButton;
  private Button launchButton;
  private ChartComposite chartComposite;
  private Text pathText;
  private Button newPlotButton;
  private Button liveUpdatesButton;
  private Action fitToDataAction;
  private IDISModule _disModule;
  private IPDUProvider _netProvider;
  protected Thread _simThread;
  protected Job _simJob;

  /**
   * flag for if time is already being updated
   * 
   */
  boolean updatePending = false;

  /**
   * our context
   * 
   */
  private IDISContext _context;

  /**
   * we need to access the setting of live updates from outside the UI thread, so store it here.
   */
  private boolean _doLiveUpdates = true;

  /**
   * we need to access the setting of new plots from outside the UI thread, so store it here.
   */
  private boolean _newPlotPerReplication = false;
  /**
   * we need to access the setting of fit to data from outside the UI thread, so store it here.
   */
  private boolean _fitToDataValue;
  private DebriefDISSimulatorPrefs _simPrefs;
  protected SimulationRunner _simulationRunner;
  private PerformanceGraph _perfGraph;
  private PartMonitor _myPartMonitor;
  private Group controlButtons;

  final String LAUNCH_STRING = "Launch";
  final String LISTEN_STRING = "Listen";
  private IDISController _disController;
  private EntityID _ourID;

  private void initModule()
  {

    _simPrefs = new DebriefDISSimulatorPrefs();

    // get the debrief prefs
    IDISNetworkPrefs netPrefs = new DebriefDISNetPrefs();

    // get the network data source
    NetworkDISProvider prov =
        new NetworkDISProvider(netPrefs, new NetworkDISProvider.LogInterface()
        {

          @Override
          public void log(int status, String msg, Exception e)
          {
            DisActivator.log(status, msg, e);
          }
        });
    _netProvider = prov;
    _disController = prov;

    _disModule = new DISModule();
    _disModule.setProvider(_netProvider);

    _perfGraph = new PerformanceGraph(chartComposite);
    _disModule.addGeneralPDUListener(_perfGraph);
    // _disModule.addScenarioListener(_perfGraph);

    _simulationRunner = new SimulationRunner(_simPrefs);

    // sort out the part listneer. Note - we have to do this before we can setup the DIS listeners
    // (below)
    _myPartMonitor =
        new PartMonitor(getSite().getWorkbenchWindow().getPartService());

    _context = new DISContext(_myPartMonitor)
    {
      ControllableTime ct = null;

      @Override
      public boolean getLiveUpdates()
      {
        return _doLiveUpdates;
      }

      @Override
      public boolean getUseNewPlot()
      {
        return _newPlotPerReplication;
      }

      @Override
      public boolean getFitToData()
      {
        return _fitToDataValue;
      }

      @Override
      public void setNewTime(long time)
      {
        if (_doLiveUpdates)
        {
          // tell the plot editor about the newtime
          if (ct == null)
          {
            final Runnable theR = new Runnable()
            {
              @Override
              public void run()
              {
                final IWorkbench wb = PlatformUI.getWorkbench();
                final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
                if (win != null)
                {
                  final IWorkbenchPage page = win.getActivePage();
                  final IEditorPart editor = page.getActiveEditor();
                  if (editor != null)
                  {
                    ct =
                        (ControllableTime) editor
                            .getAdapter(ControllableTime.class);
                  }
                }
              }
            };
            if(Display.getCurrent() != null)
            {
              theR.run();
            }
            else
            {
              Display.getDefault().asyncExec(theR);
            }
          }

          if (ct != null)
          {
            ct.setTime(this, new HiResDate(time), true);
          }
          else
          {
            System.err.println("ct was null");
          }
        }
      }

      @Override
      public void screenUpdated()
      {
        _perfGraph.screenUpdate();
      }

      @Override
      public void zoomToFit()
      {
        Runnable doUpdate = new Runnable()
        {
          @Override
          public void run()
          {
            final IWorkbench wb = PlatformUI.getWorkbench();
            final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
            if (win != null)
            {
              final IWorkbenchPage page = win.getActivePage();
              final IEditorPart editor = page.getActiveEditor();
              if (editor != null)
              {
                CanvasType canvas =
                    (CanvasType) editor.getAdapter(CanvasType.class);
                if(canvas != null)
                {
                  canvas.rescale();
                }
              }
            }
          }
        };

        // check we're in the UI thread
        if (Display.getCurrent() != null
            && Thread.currentThread().equals(Display.getCurrent().getThread()))
        {
          doUpdate.run();
        }
        else
        {
          Display.getCurrent().asyncExec(doUpdate);
        }

      }
    };

    setupListeners(_disModule);

  }

  private void handleStopMessage(long time, final int appId, short eid,
      final short reason, final long numRuns)
  {
    final Runnable theR = new Runnable()
    {

      @Override
      public void run()
      {
        // check it wasn't us that send the message
        if (appId == NetworkDISProvider.APPLICATION_ID)
        {
          // ignore - it's us sending it
          return;
        }

        // hey, check the reason
        switch (reason)
        {
        case IDISStopListener.PDU_ITERATION_COMPLETE:
          
          // tell the context that it's complete
          _context.scenarioComplete();
          break;
        case IDISStopListener.PDU_FREEZE:
          pauseReceived();
          break;
        case IDISStopListener.PDU_STOP:
          // update the UI
          stopReceived();

          // check it wasn't from us
          short ourAppId =
              (short) DisActivator.getDefault().getPreferenceStore().getInt(
                  DisActivator.APP_ID);

          if (appId != ourAppId)
          {
            // ok, popup message
            MessageBox dialog =
                new MessageBox(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), SWT.OK);
            dialog.setText("DIS Interface");
            final String phrase;
            if(numRuns > 1)
              phrase = "runs";
            else
              phrase = "run";
            dialog.setMessage("The simulation has completed after " + numRuns + " " + phrase);

            // open dialog
            dialog.open();
          }
          break;
        default:
          CorePlugin.logError(Status.WARNING,
              "Unknown DIS stop reason received:" + reason, null);
        }

      }
    };
    if(Display.getCurrent() != null)
    {
      theR.run();
    }
    else
    {
      Display.getDefault().syncExec(theR);
    }
  }

  private void setupListeners(final IDISModule module)
  {

    // listen for stop, so we can update the UI
    module.addStopListener(new IDISStopListener()
    {

      @Override
      public void stop(long time, int appId, short eid, short reason, long numRuns)
      {
        handleStopMessage(time, appId, eid, reason, numRuns);
      }
    });

    // handle the time updates
    module.addGeneralPDUListener(new IDISGeneralPDUListener()
    {
      final long TIME_UNSET = module.convertTime(-1);

      long time = TIME_UNSET;
      long lastTime = TIME_UNSET;

      @Override
      public void logPDU(Pdu pdu)
      {
        long newTime = module.convertTime(pdu.getTimestamp());
        if (newTime != time && time != TIME_UNSET)
        {
          // are we already updating?
          if (!updatePending)
          {
            // nope, go for it.
            updatePending = true;
            final Runnable theR = new Runnable()
            {

              @Override
              public void run()
              {
                if (_doLiveUpdates)
                {
                  // first the data model
                  _context.fireUpdate(null, null);

                  // now the time
                  if (time > lastTime)
                  {
                    _context.setNewTime(time);

                    // hey, should we fit to window?
                    if (_fitToDataValue)
                    {
                      _context.zoomToFit();
                    }

                    lastTime = time;
                  }
                }
                updatePending = false;
              }
            };
            if(Display.getCurrent() != null)
            {
              theR.run();
            }
            else
            {
              Display.getDefault().asyncExec(theR);
            }
          }
        }
        time = newTime;
      }

      @Override
      public void complete(String reason)
      {
        // fire in one last update
        // note: this is necessary because we only normally
        // fire an update when we get a new time value
        // (to reduce the update frequency)
        // But, after the last time value, by definition
        // we don't get another. So, we'll fire it automatically
        final Runnable theR = new Runnable()
        {
          @Override
          public void run()
          {
            // hey, should we fit to window?
            if (_fitToDataValue)
            {
              _context.zoomToFit();
            }
            else
            {
              // ok, force update
              _context.fireUpdate(null, null);
            }
          };
        };
        if(Display.getCurrent() != null)
        {
          theR.run();
        }
        else
        {
          Display.getDefault().asyncExec(theR);
        }

        // reset the time counter
        time = TIME_UNSET;
        lastTime = TIME_UNSET;
      }
    });

    // ok, Debrief fix listener
    module.addFixListener(new DebriefFixListener(_context));
    module.addDetonationListener(new DebriefDetonationListener(_context));
    module.addEventListener(new DebriefEventListener(_context));
    module.addFireListener(new DebriefFireListener(_context));
    module.addStartResumeListener(new IDISStartResumeListener()
    {
      @Override
      public void add(long time, short eid, long replication)
      {
        playHeard();

        // also, tell the context about the new replication id
        _context.setReplicationId(replication);
      }
    });
    module.addCollisionListener(new DebriefCollisionListener(_context));
  }

  @Override
  public void createPartControl(Composite parent)
  {
    // height of the DIS icon, and the Listen button
    final int col1Width = 80;

    Composite composite = new Composite(parent, SWT.NONE);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    composite.setLayoutData(gd);
    GridLayout layout = new GridLayout(1, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    composite.setLayout(layout);

    Composite topRow = new Composite(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    topRow.setLayoutData(gd);
    layout = new GridLayout(2, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    topRow.setLayout(layout);

    Label iconLbl = new Label(topRow, SWT.CENTER);
    iconLbl.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(
        "org.mwc.debrief.dis", "icons/50px/dis_icon.png").createImage());
    GridData gd3 = new GridData(SWT.CENTER, SWT.CENTER, false, false);
    gd3.widthHint = col1Width;
    iconLbl.setLayoutData(gd3);

    Group localGroup = new Group(topRow, SWT.NONE);
    localGroup.setText("Local simulator");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    localGroup.setLayoutData(gd);
    layout = new GridLayout(5, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    localGroup.setLayout(layout);

    launchButton = new Button(localGroup, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    gd.widthHint = col1Width;
    launchButton.setText(LAUNCH_STRING);
    launchButton.setLayoutData(gd);
    launchButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        doLaunch();
      }

    });

    Label label = new Label(localGroup, SWT.NONE);
    gd =
        new GridData(GridData.HORIZONTAL_ALIGN_END,
            GridData.VERTICAL_ALIGN_CENTER, false, true);
    gd.verticalIndent = 6;
    label.setLayoutData(gd);
    label.setText("Control file:");

    pathText = new Text(localGroup, SWT.SINGLE | SWT.BORDER);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.horizontalSpan = 2;
    gd.widthHint = 150;
    pathText.setLayoutData(gd);
    pathText.addModifyListener(new ModifyListener()
    {

      @Override
      public void modifyText(ModifyEvent e)
      {
        String newText = pathText.getText();
        if (newText != null)
        {
          IPreferenceStore store =
              DisActivator.getDefault().getPreferenceStore();
          store.setValue(DisActivator.PATH_TO_INPUT_FILE, pathText.getText());
        }
      }
    });
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    pathText.setText(store.getString(DisActivator.PATH_TO_INPUT_FILE));

    final Button browseButton = new Button(localGroup, SWT.PUSH);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    browseButton.setToolTipText("Browse for control file");
    browseButton.setLayoutData(gd);
    browseButton.setText("...");
    browseButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        FileDialog dialog = new FileDialog(getSite().getShell(), SWT.SINGLE);
        String value = pathText.getText();
        if (value.trim().length() == 0)
        {
          value = Platform.getLocation().toOSString();
        }
        dialog.setFilterPath(value);

        String result = dialog.open();
        if (result == null || result.trim().length() == 0)
        {
          return;
        }
        pathText.setText(result);
      }
    });

    // /////////////////////////
    // Control
    // /////////////////////////

    Composite controlHolder = new Composite(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    controlHolder.setLayoutData(gd);
    layout = new GridLayout(2, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    controlHolder.setLayout(layout);

    connectButton = new Button(controlHolder, SWT.TOGGLE);
    connectButton.setText(LISTEN_STRING);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    gd.widthHint = col1Width;
    connectButton.setLayoutData(gd);
    connectButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        if (connectButton.getSelection())
        {
          doConnect();
        }
        else
        {
          doDisconnect();
        }
      }

    });

    controlButtons = new Group(controlHolder, SWT.NONE);
    controlButtons.setText("Control");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    controlButtons.setLayoutData(gd);
    layout = new GridLayout(3, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    controlButtons.setLayout(layout);

    playButton = new Button(controlButtons, SWT.NONE);
    playButton.setText("Play");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    playButton.setLayoutData(gd);
    playButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        sendPlay();

        // doPlay();
      }

    });

    pauseButton = new Button(controlButtons, SWT.NONE);
    pauseButton.setText("Pause");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    pauseButton.setLayoutData(gd);
    pauseButton.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        _disController.sendPause();
        // doPause();
      }
    });

    stopButton = new Button(controlButtons, SWT.NONE);
    stopButton.setText("Stop");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    stopButton.setLayoutData(gd);
    stopButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        _disController.sendStop();
        // doStop();
      }

    });

    playButton.setEnabled(false);
    pauseButton.setEnabled(false);
    stopButton.setEnabled(false);

    // /////////////////////////
    // SETTINGS
    // /////////////////////////
    Group settingsPanel = new Group(composite, SWT.NONE);
    settingsPanel.setText("Settings");
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    settingsPanel.setLayoutData(gd);
    layout = new GridLayout(3, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    settingsPanel.setLayout(layout);

    newPlotButton = new Button(settingsPanel, SWT.CHECK);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    newPlotButton.setLayoutData(gd);
    newPlotButton.setText("New plot per replication");
    newPlotButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        _newPlotPerReplication = newPlotButton.getSelection();
      }
    });
    newPlotButton.setSelection(_newPlotPerReplication);

    liveUpdatesButton = new Button(settingsPanel, SWT.CHECK);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    liveUpdatesButton.setLayoutData(gd);
    liveUpdatesButton.setText("Live updates");
    liveUpdatesButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        _doLiveUpdates = liveUpdatesButton.getSelection();

        // if it's being unselected, do a refresh all
        if (liveUpdatesButton.getSelection())
        {
          final Runnable theR = new Runnable()
          {
            @Override
            public void run()
            {
              _context.fireUpdate(null, null);
            }
          };
          if(Display.getCurrent() != null)
          {
            theR.run();
          }
          else
          {
            Display.getDefault().asyncExec(theR);
          }
        }
      }

    });
    liveUpdatesButton.setSelection(_doLiveUpdates);

    final Link link = new Link(settingsPanel, SWT.NONE);
    gd = new GridData(SWT.END, SWT.FILL, false, false);
    gd.horizontalSpan = 1;
    link.setLayoutData(gd);
    link.setText("<a href=\"id\">Server Prefs</a>");
    link.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        PreferenceDialog dialog =
            PreferencesUtil.createPreferenceDialogOn(link.getShell(),
                DisPrefs.ID, null, null);
        dialog.open();
      }
    });
    link.setToolTipText("Dis Preferences");

    // /////////////////////////
    // CHART
    // /////////////////////////
    Composite chartPanel = new Composite(composite, SWT.BORDER);
    gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    chartPanel.setLayoutData(gd);
    layout = new GridLayout(1, false);
    chartPanel.setLayout(layout);

    XYDataset dataset = new TimeSeriesCollection();
    JFreeChart theChart =
        ChartFactory.createTimeSeriesChart("Line Chart", "Time", "Hertz",
            dataset);
    chartComposite =
        new ChartComposite(chartPanel, SWT.NONE, theChart, 400, 600, 300, 200,
            1800, 1800, true, true, true, true, true, true)
        {
          @Override
          public void mouseUp(MouseEvent event)
          {
            super.mouseUp(event);
            JFreeChart c = getChart();
            if (c != null)
            {
              c.setNotify(true); // force redraw
            }
          }
        };

    fixChartComposite();

    gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    chartComposite.setLayoutData(gd);
    layout = new GridLayout(1, false);
    chartComposite.setLayout(layout);
    theChart.getLegend().setVisible(true);
    theChart.getLegend().setPosition(RectangleEdge.TOP);
    theChart.getTitle().setVisible(false);

    // create our unique originating ID
    createEntityID();

    // ok, and the location commands
    contributeToActionBars();

    // ok, we can go for it
    initModule();

    // ok, sort out the help
    PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HELP_CONTEXT);

    addDropSupport();
    
    // ok, the user is opening the DIS view. Obviously they want to connect
    connectButton.setSelection(true);
    doConnect();
    
  }

  private void addDropSupport()
  {
    int operations =
        DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_DEFAULT;
    DropTarget target = new DropTarget(pathText, operations);

    Transfer[] types = new Transfer[]
    {ResourceTransfer.getInstance()};
    target.setTransfer(types);
    target.addDropListener(new DropTargetAdapter()
    {
      @Override
      public void drop(DropTargetEvent event)
      {
        super.drop(event);
        Object data = event.data;
        IResource resource = null;
        if (data.getClass().isArray() && ((Object[]) data).length > 0)
        {
          resource = (IResource) ((Object[]) data)[0];
        }
        else if (data instanceof IResource)
        {
          resource = (IResource) data;
        }

        if (resource != null)
        {
          String fileExtension = resource.getFileExtension();
          if (fileExtension.equals("inp"))
          {
            pathText.setText(resource.getLocation().toOSString());
          }
        }
        event.detail = 1;
      }
    });
  }

  private void createEntityID()
  {
    _ourID = new EntityID();
    _ourID.setApplication((short) DisActivator.getDefault()
        .getPreferenceStore().getInt(DisActivator.APP_ID));
    _ourID.setSite((short) DisActivator.getDefault().getPreferenceStore()
        .getInt(DisActivator.SITE_ID));
  }

  private void fixChartComposite()
  {
    Class<ChartComposite> clazz = ChartComposite.class;
    try
    {
      Field field = clazz.getDeclaredField("canvas");
      field.setAccessible(true);
      Object object = field.get(chartComposite);
      if (object instanceof Canvas)
      {
        Canvas canvas = (Canvas) object;
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        canvas.setLayoutData(gd);
        GridLayout layout = new GridLayout(1, false);
        canvas.setLayout(layout);
      }
    }
    catch (NoSuchFieldException | SecurityException | IllegalArgumentException
        | IllegalAccessException e1)
    {
      DisActivator.log(e1);
    }
  }

  private void contributeToActionBars()
  {

    fitToDataAction = new org.eclipse.jface.action.Action()
    {
      public void run()
      {
        _fitToDataValue = fitToDataAction.isChecked();

        // and store the value
        IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
        store.setValue(DisActivator.FIT_TO_DATA, _fitToDataValue);

      }
    };
    fitToDataAction.setChecked(_fitToDataValue);
    fitToDataAction.setText("Fit to data");
    fitToDataAction
        .setToolTipText("Zoom the selected plot out to show the full data");
    fitToDataAction.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/fit_to_win.png"));

    // use the saved setting of this control
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    _fitToDataValue = store.getBoolean(DisActivator.FIT_TO_DATA);
    fitToDataAction.setChecked(_fitToDataValue);

    final IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());

    bars.getToolBarManager().add(fitToDataAction);

    // also provide access to the console view
    Action showConsole = new Action()
    {
      public void run()
      {
        try
        {
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .showView(IConsoleConstants.ID_CONSOLE_VIEW);
        }
        catch (PartInitException e)
        {
          DisActivator.log(Status.ERROR, "While showing console view", e);
        }
      }
    };
    showConsole.setText("Show console");
    showConsole.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/console.png"));
    bars.getToolBarManager().add(showConsole);
    bars.getMenuManager().add(showConsole);

    bars.getToolBarManager().add(
        CorePlugin.createOpenHelpAction(HELP_CONTEXT, null, this));

  }

  private void fillLocalPullDown(final IMenuManager manager)
  {
    manager.add(new Separator());
    manager.add(CorePlugin.createOpenHelpAction(HELP_CONTEXT, null, this));
  }

  @Override
  public void setFocus()
  {
    launchButton.setFocus();
  }

  // private void doKill()
  // {
  // _simulationRunner.stop();
  //
  // doDisconnect();
  //
  // // tell the perf graph that we've finished
  // _perfGraph.complete("Stop button");
  //
  // launchButton.setText(LAUNCH_STRING);
  //
  // }

  /**
   * run the simulator, passing it the specified input file
   * 
   * @param inputPath
   */
  public void doLaunch(String inputPath)
  {
    // ok, start with a "connect", if we have to
    if (!connectButton.getSelection())
    {
      connectButton.setSelection(true);
      doConnect();
      // doPlay();
    }

    _simulationRunner.run(inputPath);
  }

  private void doLaunch()
  {
    IPreferenceStore store = DisActivator.getDefault().getPreferenceStore();
    String pText = store.getString(DisActivator.PATH_TO_INPUT_FILE);
    doLaunch(pText);
  }

  private void doPlay()
  {
    playButton.setEnabled(false);
    pauseButton.setEnabled(true);
    stopButton.setEnabled(true);
  }

  private void doPause()
  {
    playButton.setEnabled(true);
    pauseButton.setEnabled(false);
    stopButton.setEnabled(true);
  }

  private void doStop()
  {
    playButton.setEnabled(true);
    pauseButton.setEnabled(false);
    stopButton.setEnabled(false);

    // tell the perf graph that we've finished
    _perfGraph.complete("Stop button");

    // tell the context that it's complete
    _context.scenarioComplete();

    _perfGraph.complete("Stopped");
  }

  protected void stopReceived()
  {
    doStop();

    // no, don't disconnect, since we may get another replication
    // doDisconnect();

    // no, don't jump to launch - we may still be running
    // launchButton.setFocus();
  }

  protected void pauseReceived()
  {
    doPause();
  }

  private void doConnect()
  {
    // collate the prefs
    final String app =
        DisActivator.getDefault().getPreferenceStore().getString(
            DisActivator.APP_FILTER);
    final String site =
        DisActivator.getDefault().getPreferenceStore().getString(
            DisActivator.SITE_FILTER);
    final String ex =
        DisActivator.getDefault().getPreferenceStore().getString(
            DisActivator.EXERCISE_FILTER);

    final DISFilters filter = new DISFilters(app, site, ex);
    _netProvider.attach(filter, _ourID);

    playButton.setEnabled(true);
    pauseButton.setEnabled(false);
    stopButton.setEnabled(false);

    connectButton.setText("Listening");
  }

  private void doDisconnect()
  {
    _netProvider.detach();

    playButton.setEnabled(false);
    pauseButton.setEnabled(false);
    stopButton.setEnabled(false);

    connectButton.setSelection(false);
    connectButton.setText(LISTEN_STRING);

    // also, stop the graph updating
    _perfGraph.complete("Disconnected");
  }

  private void playHeard()
  {
    final Runnable theR = new Runnable()
    {

      @Override
      public void run()
      {
        // ok, it's running - update the UI
        doPlay();
      }
    };
    if(Display.getCurrent() != null)
    {
      theR.run();
    }
    else
    {
      Display.getDefault().asyncExec(theR);
    }
  }

  private void sendPlay()
  {
    _disController.sendPlay();
  }

}
