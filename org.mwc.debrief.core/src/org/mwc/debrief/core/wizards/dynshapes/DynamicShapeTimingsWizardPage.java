/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mwc.cmap.media.PlanetmayoFormats;
import org.mwc.debrief.core.wizards.sensorarc.NewSensorArcWizard;

/**
 * This is the page used for the showing the timings for all the dynamic shape wizards including
 * sensor arcs.
 * 
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class DynamicShapeTimingsWizardPage extends DynamicShapeBaseWizardPage
{
  private CDateTime _cStartTime;
  private CDateTime _cEndTime;
  private Button _chkStartTime;
  private Button _chkEndTime;
  private final Date _startTime, _endTime;
  private final String _type;

  public DynamicShapeTimingsWizardPage(final String pageName, final String type,
      final Date startTime, final Date endTime)
  {
    super(pageName,type);
    this._startTime = startTime;
    this._endTime = endTime;
    this._type = type;
  }

  @Override
  public void createControl(final Composite parent)
  {
    final Composite mainComposite = new Composite(parent, SWT.NULL);
    mainComposite.setLayout(new GridLayout());
    mainComposite.setLayoutData(new GridData(GridData.FILL));
    final Composite baseComposite = super.createBaseControl(mainComposite);
    final Composite composite = new Composite(baseComposite, SWT.NULL);
    composite.setLayout(new GridLayout(3, false));
    new Label(composite, SWT.NULL).setText("");
    new Label(composite, SWT.BOLD).setText("Present");
    new Label(composite, SWT.BOLD).setText("Value");
    new Label(composite, SWT.NULL).setText("Start Time:");
    _chkStartTime = new Button(composite, SWT.CHECK);
    _cStartTime = new CDateTime(composite, CDT.BORDER);
    final GridData gd1 = new GridData();
    gd1.minimumWidth = 300;
    gd1.grabExcessHorizontalSpace = true;
    _cStartTime.setLayoutData(gd1);
    _cStartTime.setPattern(PlanetmayoFormats.getInstance().getDateFormat()
        .toPattern());
    _cStartTime.setEnabled(false);

    _chkStartTime.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        _cStartTime.setEnabled(_chkStartTime.getSelection());
        _cStartTime.setSelection(_startTime);
        setPageComplete(isPageComplete());
      };
    });
    new Label(composite, SWT.NULL).setText("End Time:");
    _chkEndTime = new Button(composite, SWT.CHECK);
    _cEndTime = new CDateTime(composite, CDT.BORDER);
    _cEndTime.setPattern(PlanetmayoFormats.getInstance().getDateFormat()
        .toPattern());
    _cEndTime.setEnabled(false);
    _cEndTime.setLayoutData(gd1);
    _chkEndTime.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        _cEndTime.setEnabled(_chkEndTime.getSelection());
        _cEndTime.setSelection(_endTime);
        setPageComplete(isPageComplete());
      };
    });
    _cStartTime.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        setPageComplete(isPageComplete());
      }

    });
    _cEndTime.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        setPageComplete(isPageComplete());
      }
    });
    setControl(mainComposite);
  }

  public Date getEndTime()
  {
    return _chkEndTime.getSelection() ? _cEndTime.getSelection() : null;
  }

  public Date getStartTime()
  {
    return _chkStartTime.getSelection() ? _cStartTime.getSelection() : null;
  }

  @Override
  public boolean isPageComplete()
  {

    boolean isComplete = false;
    if (!NewSensorArcWizard.SHAPE_NAME.equals(_type))
    {
      setErrorMessage(null);
      isComplete = true;
    }
    else
    {
      isComplete = ((_chkStartTime.getSelection() && _cStartTime
          .getSelection() != null) || (_chkEndTime.getSelection() && _cEndTime
              .getSelection() != null));
      if (!isComplete)
      {
        setErrorMessage("Please enter one of starttime or endtime");
      }
      else
      {
        if (_chkStartTime.getSelection() && _chkEndTime.getSelection()
            && !_cStartTime.getSelection().before(_cEndTime.getSelection()))
        {
          setErrorMessage("Please provide a starttime earlier than the endtime");
        }
        else
        {
          setErrorMessage(null);
          isComplete = true;
        }
      }
    }
    return isComplete;
  }
}
