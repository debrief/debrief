/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mwc.cmap.core.custom_widget.CWorldLocation;
import org.mwc.cmap.core.custom_widget.LocationModifiedListener;
import org.mwc.cmap.core.custom_widget.LocationModifiedEvent;
import org.mwc.debrief.core.wizards.sensorarc.NewSensorArcBaseWizardPage;

import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class DynamicRectangleBoundsPage extends NewSensorArcBaseWizardPage
{
  private CWorldLocation _topLeftLocation;
  private CWorldLocation _bottomRightLocation;
  

  protected DynamicRectangleBoundsPage(String pageName)
  {
    super(pageName);
    setTitle("Create dynamic rectangle");
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
    new Label(composite,SWT.NONE).setText("Top left:");
    _topLeftLocation = new CWorldLocation(composite,SWT.NONE);
    _topLeftLocation.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _topLeftLocation.setToolTipText("Top left of the dynamic rectangle");
    _topLeftLocation.addLocationModifiedListener(new LocationModifiedListener()
    {
      
      @Override
      public void modifyValue(LocationModifiedEvent e)
      {
        setPageComplete(isPageComplete());
        
      }
    });
    new Label(composite,SWT.NONE).setText("Bottom Right: ");
    _bottomRightLocation = new CWorldLocation(composite,SWT.NONE);
    _bottomRightLocation.setToolTipText("Bottom right of the dynamic rectangle");
    _bottomRightLocation.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _bottomRightLocation.addLocationModifiedListener(new LocationModifiedListener()
    {
      
      @Override
      public void modifyValue(LocationModifiedEvent e)
      {
        setPageComplete(isPageComplete());
        
      }
    });
    setControl(mainComposite);
  }
  
  public WorldLocation getTopLeftLocation() {
    return (WorldLocation)_topLeftLocation.getValue();
  }
  
  public WorldLocation getBottomRightLocation() {
    return (WorldLocation)_bottomRightLocation.getValue();
  }
  
  @Override
  public boolean isPageComplete()
  {
    return _topLeftLocation.getValue()!=null && _topLeftLocation.getValue().isValid() 
        && _bottomRightLocation.getValue()!=null && _bottomRightLocation.getValue().isValid();
  }
  

}
