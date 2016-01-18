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
package org.mwc.debrief.dis.views;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.debrief.dis.preferences.DisPrefs;

public class DisListenerView extends ViewPart
{

  private Button connectButton;
  private Button disconnectButton;
  private Button stopButton;
  private Button pauseButton;
  private Button resumeButton;
  private Button playButton;
  @SuppressWarnings("unused")
  private ChartComposite chartComposite;
  private Text pathText;
  private Button newPlotButton;
  private Button liveUpdatesButton;

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
        // FIXME connect
      }

    });
    disconnectButton = createButton(buttonComposite, "Disconnect", 2);
    disconnectButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        // FIXME disconnect
      }

    });

    final Link link = new Link(buttonComposite, SWT.NONE);
    gd = new GridData(SWT.END, SWT.FILL, false, false);
    gd.horizontalSpan = 4;
    link.setLayoutData(gd);
    link.setText("<a href=\"id\">Server Prefs</a>");
    link.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(link.getShell(), DisPrefs.ID,
            null, null);
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
        // FIXME stop
      }

    });

    pauseButton = createButton(buttonComposite, "Pause");
    pauseButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        // FIXME pause
      }

    });

    resumeButton = createButton(buttonComposite, "Resume");
    resumeButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        // FIXME resume
      }

    });

    playButton = createButton(buttonComposite, "Play");
    playButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        // FIXME play
      }

    });

    stopButton.setEnabled(false);
    pauseButton.setEnabled(false);
    resumeButton.setEnabled(false);
    disconnectButton.setEnabled(false);

    Label label = new Label(buttonComposite, SWT.NONE);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    label.setLayoutData(gd);
    label.setText("Path to input file:");

    Text text = new Text(buttonComposite, SWT.SINGLE | SWT.BORDER);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    gd.horizontalSpan = 2;
    gd.widthHint = 150;
    text.setLayoutData(gd);

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

    chartComposite = new ChartComposite(chartWrapperComposite, SWT.NONE, null,
        400, 600, 300, 200, 1800, 1800, true, true, true, true, true, true)
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
        // FIXME new plot ...
      }

    });
    liveUpdatesButton = new Button(checkboxComposite, SWT.CHECK);
    gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    liveUpdatesButton.setLayoutData(gd);
    liveUpdatesButton.setText("Live updates");
    liveUpdatesButton.addSelectionListener(new SelectionAdapter()
    {

      @Override
      public void widgetSelected(SelectionEvent e)
      {
        // FIXME Live updates.
      }

    });
    liveUpdatesButton.setSelection(true);

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
