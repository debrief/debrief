/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mwc.cmap.media.PlanetmayoFormats;
import org.mwc.debrief.core.wizards.sensorarc.NewSensorArcBaseWizardPage;

/**
 * @author Ayesha
 *
 */
public class DynamicShapeTimingsPage extends NewSensorArcBaseWizardPage
{
  private CDateTime _cTime;
  private Date _startDate;

  protected DynamicShapeTimingsPage(String pageName,String type, Date startDate)
  {
    super(pageName);
    setTitle("Create dynamic "+type);
    setDescription("This wizard is used to create new dynamic shapes");
    _startDate = startDate;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl(Composite parent)
  {
    Composite mainComposite = new Composite(parent,SWT.BORDER);
    mainComposite.setLayout(new GridLayout());
    mainComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));
    Composite baseComposite = super.createBaseControl(mainComposite);
    Composite composite = new Composite(baseComposite,SWT.NULL);
    composite.setLayout(new GridLayout(2,false));
    new Label(composite,SWT.NULL).setText("Valid Time:");
    _cTime = new CDateTime(composite,CDT.BORDER);
    //_cTime.setToolTipText("The time this shape is displayed");
    GridData gd1 = new GridData();
    gd1.minimumWidth=125;
    _cTime.setLayoutData(gd1);
    _cTime.setPattern(PlanetmayoFormats.getInstance().getDateFormat().toPattern());
    
    _cTime.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        setPageComplete(isPageComplete());
      };
    });
    _cTime.setSelection(_startDate);
    setControl(mainComposite);
  }
  
  public Date getStartTime() {
    return _cTime.getSelection();
  }
  

}
