package org.mwc.debrief.core.wizards.sensorarc;

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

/**
 * 
 * @author Ayesha <ayesha.ma@gmail.com>
 * 
 */
public class SensorArcTimingsWizardPage extends NewSensorArcBaseWizardPage
{
  private CDateTime _cStartTime;
  private CDateTime _cEndTime;
  private Button _chkStartTime;
  private Button _chkEndTime;
  private Date _startTime,_endTime;

  protected SensorArcTimingsWizardPage(String pageName,Date startTime,Date endTime)
  {
    super(pageName);
    this._startTime = startTime;
    this._endTime = endTime;
    setTitle("Create dynamic track shapes");
    setDescription("This wizard is used to create new track shapes (or sensor arcs)");
  }

  @Override
  public void createControl(Composite parent)
  {
    Composite mainComposite = new Composite(parent,SWT.NULL);
    mainComposite.setLayout(new GridLayout());
    mainComposite.setLayoutData(new GridData(GridData.FILL));
    Composite baseComposite = super.createBaseControl(mainComposite);
    Composite composite = new Composite(baseComposite,SWT.NULL);
    composite.setLayout(new GridLayout(3,false));
    new Label(composite,SWT.NULL).setText("");
    new Label(composite,SWT.BOLD).setText("Present");
    new Label(composite,SWT.BOLD).setText("Value");
    new Label(composite,SWT.NULL).setText("Start Time:");
    _chkStartTime = new Button(composite,SWT.CHECK);
    _cStartTime = new CDateTime(composite,CDT.BORDER);
    GridData gd1 = new GridData();
    gd1.minimumWidth=300;
    gd1.grabExcessHorizontalSpace=true;
    _cStartTime.setLayoutData(gd1);
    _cStartTime.setPattern(PlanetmayoFormats.getInstance().getDateFormat().toPattern());
    _cStartTime.setEnabled(false);
    
    _chkStartTime.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        _cStartTime.setEnabled(_chkStartTime.getSelection());
        _cStartTime.setSelection(_startTime);
        setPageComplete(isPageComplete());
      };
    });
    new Label(composite,SWT.NULL).setText("End Time:");
    _chkEndTime = new Button(composite,SWT.CHECK);
    _cEndTime = new CDateTime(composite,CDT.BORDER);
    _cEndTime.setPattern(PlanetmayoFormats.getInstance().getDateFormat().toPattern());
    _cEndTime.setEnabled(false);
    _cEndTime.setLayoutData(gd1);
    _chkEndTime.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        _cEndTime.setEnabled(_chkEndTime.getSelection());
        _cEndTime.setSelection(_endTime);
        setPageComplete(isPageComplete());
      };
    });
    _cStartTime.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        setPageComplete(isPageComplete());
      }
      
    });
    _cEndTime.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        setPageComplete(isPageComplete());
      }
    });
    setControl(mainComposite);
  }
  
  @Override
  public boolean isPageComplete()
  {
    
    boolean isComplete =  ((_chkStartTime.getSelection() && _cStartTime.getSelection()!=null) ||
        (_chkEndTime.getSelection() && _cEndTime.getSelection()!=null));
    if(!isComplete) {
      setErrorMessage("Either start time or end time is required");
    }
    else {
      if(_chkStartTime.getSelection() && _chkEndTime.getSelection() && !_cStartTime.getSelection().before(_cEndTime.getSelection())) {
        setErrorMessage("The start time must be before end time");
      }
      else {
        setErrorMessage(null);
        return true;
      }
    }
    return false;
  }

  public Date getStartTime()
  {
    return _cStartTime.getSelection();
  }
  public Date getEndTime()
  {
    return _cEndTime.getSelection();
  }
}
