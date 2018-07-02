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
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
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

  private FormattedText startTime;
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
    setMessage("Please specify the actual start date and time of the video");
  }
  
  @Override
  protected Control createDialogArea(Composite parent)
  {
    final Composite base = (Composite) super.createDialogArea(parent);
    final Composite composite = new Composite(base, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH
        | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
    composite.setLayout(new GridLayout(2, false));
    {
      new Label(composite,SWT.NONE).setText("Video Start Time:");
      startTime = new FormattedText(composite);
      startTime.setFormatter(PlanetmayoFormats.getInstance().getDateTimeFormatter());
      if(_startTime == null) {
        startTime.setValue(new Date());
      }
      else {
        startTime.setValue(_startTime);
      }
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
    _startTime = ((Date)startTime.getValue());
    super.okPressed();
  }

}
