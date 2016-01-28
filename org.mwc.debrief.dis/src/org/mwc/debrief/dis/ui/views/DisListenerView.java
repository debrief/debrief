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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.core.DISModule;
import org.mwc.debrief.dis.core.IDISModule;
import org.mwc.debrief.dis.core.IDISPreferences;
import org.mwc.debrief.dis.listeners.impl.DISContext;
import org.mwc.debrief.dis.listeners.impl.DebriefFixListener;
import org.mwc.debrief.dis.listeners.impl.IDISContext;
import org.mwc.debrief.dis.providers.network.IDISNetworkPrefs;
import org.mwc.debrief.dis.providers.network.NetworkDISProvider;
import org.mwc.debrief.dis.runner.SimulationRunner;
import org.mwc.debrief.dis.ui.preferences.DebriefDISNetPrefs;
import org.mwc.debrief.dis.ui.preferences.DebriefDISSimulatorPrefs;
import org.mwc.debrief.dis.ui.preferences.DisPrefs;

public class DisListenerView extends ViewPart
{

  private Button connectButton;
  private Button disconnectButton;
  private Button stopButton;
//  private Button pauseButton;
//  private Button resumeButton;
  private Button playButton;
  private ChartComposite chartComposite;
  private Text pathText;
  private Button newPlotButton;
  private Button liveUpdatesButton;
  private Action fitToDataAction;
  private DISModule _disModule;
  private NetworkDISProvider _netProvider;
  protected Thread _simThread;
  protected Job _simJob;

  /**
   * we need to access the setting of live updates from outside the UI thread, so store it here.
   */
  private boolean _doLiveUpdates = false;

  /**
   * we need to access the setting of new plots from outside the UI thread, so store it here.
   */
  private boolean _newPlotPerReplication = false;
  /**
   * we need to access the setting of fit to data from outside the UI thread, so store it here.
   */
  private boolean _fitToDataValue = true;
  private DebriefDISSimulatorPrefs _simPrefs;
  protected SimulationRunner _simulationRunner;

  private void initModule()
  {

    _simPrefs = new DebriefDISSimulatorPrefs();

    // get the debrief prefs
    IDISNetworkPrefs netPrefs = new DebriefDISNetPrefs();

    // get the network data source
    _netProvider = new NetworkDISProvider(netPrefs);

    _disModule = new DISModule();
    _disModule.setProvider(_netProvider);

    PerformanceGraph perfGraph = new PerformanceGraph(chartComposite);
    _disModule.addGeneralPDUListener(perfGraph);
    _disModule.addScenarioListener(perfGraph);

    _simulationRunner = new SimulationRunner(_simPrefs);

    setupListeners(_disModule);
  }

  private void setupListeners(IDISModule module)
  {
    IDISContext context = new DISContext()
    {

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

    };

    // ok, Debrief fix listener
    module.addFixListener(new DebriefFixListener(context));
  }

  @Override
  public void createPartControl(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    composite.setLayoutData(gd);
    GridLayout layout = new GridLayout(1, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    composite.setLayout(layout);

    Composite buttonComposite = new Composite(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    gd.widthHint = 300;
    buttonComposite.setLayoutData(gd);
    layout = new GridLayout(4, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    buttonComposite.setLayout(layout);

    connectButton = createButton(buttonComposite, "Connect", 2);
    connectButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        _netProvider.attach();
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(true);
      }

    });
    disconnectButton = createButton(buttonComposite, "Disconnect", 2);
    disconnectButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        _netProvider.detach();
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
      }

    });

    final Link link = new Link(buttonComposite, SWT.NONE);
    gd = new GridData(SWT.END, SWT.FILL, false, false);
    gd.horizontalSpan = 2;
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

    stopButton = createButton(buttonComposite, "Stop");
    stopButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        // CustomEspduSender.terminate();
        //
        // _simJob.cancel();
        _simulationRunner.stop();

        stopButton.setEnabled(false);
        playButton.setEnabled(true);
      }

    });

//    pauseButton = createButton(buttonComposite, "Pause");
//    pauseButton.addSelectionListener(new SelectionAdapter()
//    {
//
//      @Override
//      public void widgetSelected(SelectionEvent e)
//      {
//      }
//
//    });
//
//    resumeButton = createButton(buttonComposite, "Resume");
//    resumeButton.addSelectionListener(new SelectionAdapter()
//    {
//
//      @Override
//      public void widgetSelected(SelectionEvent e)
//      {
//      }
//
//    });

    playButton = createButton(buttonComposite, "Play");
    playButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {

        _simulationRunner.run();

        // _simJob = new Job("Run simulation")
        // {
        // @Override
        // protected IStatus run(IProgressMonitor monitor)
        // {
        //
        // // CustomEspduSender.main(inArgs);
        // return Status.OK_STATUS;
        // }
        //
        // };
        // _simJob.setUser(false);
        // _simJob.schedule();

        playButton.setEnabled(false);
        stopButton.setEnabled(true);
      }

    });

    stopButton.setEnabled(false);
//    pauseButton.setEnabled(false);
//    resumeButton.setEnabled(false);
    disconnectButton.setEnabled(false);

    Label label = new Label(buttonComposite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    label.setLayoutData(gd);
    label.setText("Path to input file:");

    pathText = new Text(buttonComposite, SWT.SINGLE | SWT.BORDER);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
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
    IPreferenceStore store =
        DisActivator.getDefault().getPreferenceStore();
    pathText.setText(store.getString(DisActivator.PATH_TO_INPUT_FILE));

    final Button browseButton = new Button(buttonComposite, SWT.PUSH);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    browseButton.setLayoutData(gd);
    browseButton.setText("Browse...");
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

    Composite chartWrapperComposite = new Composite(composite, SWT.BORDER);
    gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    chartWrapperComposite.setLayoutData(gd);
    layout = new GridLayout(1, false);
    chartWrapperComposite.setLayout(layout);

    XYDataset dataset = new TimeSeriesCollection();
    JFreeChart theChart =
        ChartFactory.createTimeSeriesChart("Line Chart", "Time", "Hertz",
            dataset);
    chartComposite =
        new ChartComposite(chartWrapperComposite, SWT.NONE, theChart, 400, 600,
            300, 200, 1800, 1800, true, true, true, true, true, true)
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
    theChart.getLegend().setVisible(false);
    theChart.getTitle().setVisible(false);

    Composite checkboxComposite = new Composite(composite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    checkboxComposite.setLayoutData(gd);
    layout = new GridLayout(2, false);
    layout.marginWidth = 5;
    layout.marginHeight = 5;
    checkboxComposite.setLayout(layout);

    newPlotButton = new Button(checkboxComposite, SWT.CHECK);
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

    liveUpdatesButton = new Button(checkboxComposite, SWT.CHECK);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    liveUpdatesButton.setLayoutData(gd);
    liveUpdatesButton.setText("Live updates");
    liveUpdatesButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        _doLiveUpdates = liveUpdatesButton.getSelection();
      }

    });
    liveUpdatesButton.setSelection(_doLiveUpdates);

    fitToDataAction = new org.eclipse.jface.action.Action()
    {
      public void run()
      {
        _fitToDataValue = fitToDataAction.isChecked();
      }
    };
    fitToDataAction.setChecked(_fitToDataValue);
    fitToDataAction.setText("Fit to data");
    fitToDataAction
        .setToolTipText("Zoom the selected plot out to show the full data");
    fitToDataAction.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/fit_to_win.png"));

    contributeToActionBars();

    // ok, we can go for it
    initModule();

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
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(final IMenuManager manager)
  {
  }

  private void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(fitToDataAction);
  }

  private Button createButton(Composite composite, String label)
  {
    return createButton(composite, label, 1);
  }

  private Button createButton(Composite composite, String label,
      int horizontalSpan)
  {
    Button button = new Button(composite, SWT.PUSH);
    GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    gd.horizontalSpan = horizontalSpan;
    button.setLayoutData(gd);
    button.setText(label);
    return button;
  }

  @Override
  public void setFocus()
  {
    // TODO Auto-generated method stub

  }

}
