/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mwc.debrief.core.wizards.sensorarc.NewSensorArcBaseWizardPage;

/**
 * @author Ayesha
 *
 */
public class DynamicShapeStylingPage extends NewSensorArcBaseWizardPage
{
  private String _type;

  protected DynamicShapeStylingPage(String pageName,String type)
  {
    super(pageName);
    this._type = type;
    setTitle("Create dynamic "+type);
    setDescription("This wizard is used to create new dynamic shapes");
  }
  
  private Text _txtSymbology;
  private Text _txtLabel;
  
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
    new Label(composite,SWT.NONE).setText("Symbology : ");
    _txtSymbology = new Text(composite,SWT.BORDER);
    GridData gd = new GridData(SWT.BEGINNING,SWT.CENTER,true,false);
    gd.minimumWidth=125;
    _txtSymbology.setLayoutData(gd);
    _txtSymbology.setToolTipText("Color/Style to use");
    _txtSymbology.setText("@C");
    _txtSymbology.setLayoutData(gd);
    new Label(composite,SWT.NONE).setText("Label : ");
    _txtLabel = new Text(composite,SWT.BORDER);
    _txtLabel.setLayoutData(gd);
    _txtLabel.setToolTipText("Name this arc");
    _txtLabel.setText(_type);
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
    boolean isPageComplete = !_txtSymbology.getText().isEmpty() &&
           !_txtLabel.getText().isEmpty();
    if(!isPageComplete) {
      setErrorMessage("All fields are not entered");
    }
    else {
      setErrorMessage(null);
    }
    return isPageComplete;
  }
  
 
  public String getSymbology() {
    return _txtSymbology.getText();
  }

  public String getShapeLabel() {
    return _txtLabel.getText();
  }

}
