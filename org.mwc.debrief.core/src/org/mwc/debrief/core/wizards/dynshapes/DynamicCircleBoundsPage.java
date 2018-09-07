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
import org.mwc.cmap.core.custom_widget.CWorldLocation;
import org.mwc.cmap.gridharness.views.WorldLocationCellEditor;
import org.mwc.debrief.core.wizards.sensorarc.NewSensorArcBaseWizardPage;

import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class DynamicCircleBoundsPage extends NewSensorArcBaseWizardPage
{

  private CWorldLocation _txtCentre;
  private Text _txtRadius;
  protected DynamicCircleBoundsPage(String pageName)
  {
    super(pageName);
    // TODO Auto-generated constructor stub
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
    new Label(composite,SWT.NONE).setText("Centre:");
    _txtCentre = new CWorldLocation(composite,SWT.NONE);
    _txtCentre.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _txtCentre.setToolTipText("Location of centre of the dynamic circle");
    new Label(composite,SWT.NONE).setText("Radius: ");
    _txtRadius = new Text(composite,SWT.BORDER);
    _txtRadius.setToolTipText("Radius of the dynamic circle");
    _txtRadius.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _txtRadius.addModifyListener(new ModifyListener()
    {
      
      @Override
      public void modifyText(ModifyEvent e)
      {
        setPageComplete(isPageComplete());
      }
    });
    setControl(mainComposite);
  }

  public WorldLocation getCenter()
  {
    return (WorldLocation)_txtCentre.getValue();
  }
  public int getRadius()
  {
    return Integer.valueOf(_txtRadius.getText());
  }
  @Override
  public boolean isPageComplete()
  {
    if(!_txtRadius.getText().isEmpty() && isValidRadius(_txtRadius.getText())) {
      return true;
    }
    return false;
  }
  
  private boolean isValidRadius(String value) {
    if(!value.matches("\\\\d+")){
      int num = Integer.valueOf(value);
      if(num>0 && num<=400) {
        return true;
      }
    }
    return false;
  }

}
