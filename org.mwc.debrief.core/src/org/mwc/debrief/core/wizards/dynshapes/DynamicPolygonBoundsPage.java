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

/**
 * @author Ayesha
 *
 */
public class DynamicPolygonBoundsPage extends DynamicShapeBaseWizardPage
{

  private Text _coordinatesPolygon;

  protected DynamicPolygonBoundsPage(String pageName)
  {
    super(pageName);
    setTitle("Create dynamic polygon");
    setDescription("This wizard is used to create new dynamic shapes");
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
    new Label(composite,SWT.NONE).setText("Coordinates:");
    _coordinatesPolygon = new Text(composite,SWT.BORDER|SWT.MULTI);
    GridData gd = new GridData(SWT.BEGINNING,SWT.CENTER,true,true);
    gd.minimumWidth=125;
    gd.minimumHeight=150;
    _coordinatesPolygon.setLayoutData(gd);
    _coordinatesPolygon.setToolTipText("Top left of the dynamic rectangle");
    _coordinatesPolygon.addModifyListener(new ModifyListener()
    {
      
      @Override
      public void modifyText(ModifyEvent e)
      {
        setPageComplete(isPageComplete());
        
      }
    });
    
    setControl(mainComposite);
  }

  public String getCoordinates()
  {
    return _coordinatesPolygon.getText();
  }
  @Override
  public boolean isPageComplete()
  {
    return !_coordinatesPolygon.getText().isEmpty(); 
  }

}
