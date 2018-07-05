/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

/**
 * This dialog inputs a start time for video player
 */
package org.mwc.cmap.media.dialog;

import java.util.Date;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.media.PlanetmayoFormats;

/**
 * @author Ayesha
 *
 */
public class VideoPlayerStartTimeDialog extends TitleAreaDialog
{

  private CDateTime startTime;
  private Date _startTime;
  public VideoPlayerStartTimeDialog() {
    this(Display.getDefault().getActiveShell());
  }
  
  public VideoPlayerStartTimeDialog(Shell parentShell)
  {
    super(parentShell);
  }
  
  @Override
  public void create()
  {
    super.create();
    setTitle("Video Start Time");
    setMessage("Specify the actual start date and time of the video");
  }
  
  @Override
  protected Control createDialogArea(Composite parent)
  {
    final Composite base = (Composite) super.createDialogArea(parent);
    final Composite composite = new Composite(base, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
    composite.setLayout(new GridLayout(3, false));
    {
      new Label(composite,SWT.NONE).setText("Start Time:");
      startTime = new CDateTime(composite,CDT.BORDER);
      startTime.setPattern(PlanetmayoFormats.getInstance().getDateFormat().toPattern());
      if(_startTime == null) {
        startTime.setSelection(new Date());
      }
      else {
        startTime.setSelection(_startTime);
      }
     // startTime.setToolTipText("Actual recording start time in YYYY-MM-DD HH:mm:ss format, Eg:1995-12-12 10:38:56");
      
    }
    return composite;
  }
  
  public void setStartTime(Date newStartTime) {
    _startTime = newStartTime;
    
  }
  
  public Date getStartTime() {
    return _startTime;
  }
  
  @Override
  protected void okPressed()
  {
    _startTime = ((Date)startTime.getSelection());
    super.okPressed();
  }

}
