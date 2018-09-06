/**
 * 
 */
package org.mwc.debrief.core.wizards.sensorarc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ayesha
 *
 */
public class SensorArcStylingWizardPage extends NewSensorArcBaseWizardPage
{

  private Combo _txtTrack;
  private Text _txtSymbology;
  private Text _txtLabel;
  private String[] trackNames;
  private String _selectedTrack;
  protected SensorArcStylingWizardPage(String pageName,String[] tracks,String selectedTrack)
  {
    super(pageName);
    this.trackNames = tracks;
    this._selectedTrack = selectedTrack;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl(Composite parent)
  {
    Composite mainComposite = new Composite(parent,SWT.NULL);
    mainComposite.setLayout(new GridLayout());
    mainComposite.setLayoutData(new GridData(GridData.FILL));
    Composite baseComposite = super.createBaseControl(mainComposite);
    Composite composite = new Composite(baseComposite,SWT.NULL);
    composite.setLayout(new GridLayout(2,false));
    composite.setLayoutData(new GridData(GridData.FILL));
    new Label(composite,SWT.NONE).setText("Track : ");
    _txtTrack = new Combo(composite,SWT.BORDER);
    _txtTrack.setItems(trackNames);
    _txtTrack.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _txtTrack.setToolTipText("Track that the arc is connected to");
    new Label(composite,SWT.NONE).setText("Symbology : ");
    _txtSymbology = new Text(composite,SWT.BORDER);
    _txtSymbology.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _txtSymbology.setToolTipText("Color/Style to use");
    _txtSymbology.setText("@C");
    new Label(composite,SWT.NONE).setText("Label : ");
    _txtLabel = new Text(composite,SWT.BORDER);
    _txtLabel.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _txtLabel.setToolTipText("Name this arc");
    _txtLabel.setText("Sensor Arc");
    _txtTrack.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        setPageComplete(isPageComplete());
      }
    });
    if(_selectedTrack!=null) {
      _txtTrack.setText(_selectedTrack);
    }
    _txtSymbology.addModifyListener(new ModifyListener()
    {
      
      @Override
      public void modifyText(ModifyEvent e)
      {
        setPageComplete(isPageComplete());
      }
    });
    _txtLabel.addModifyListener(new ModifyListener()
    {
      
      @Override
      public void modifyText(ModifyEvent e)
      {
        setPageComplete(isPageComplete());
        
      }
    });
    setControl(mainComposite);
  }
  
  @Override
  public boolean isPageComplete()
  {
    boolean isPageComplete =  !_txtTrack.getText().isEmpty() &&
           !_txtSymbology.getText().isEmpty() &&
           !_txtLabel.getText().isEmpty();
    if(!isPageComplete) {
      setErrorMessage("All fields are not entered");
    }
    else {
      setErrorMessage(null);
    }
    return isPageComplete;
  }
  
  public String getTrackName(){
    return _txtTrack.getText();
  }
  public String getSymbology() {
    return _txtSymbology.getText();
  }

  public String getArcName() {
    return _txtLabel.getText();
  }
}
