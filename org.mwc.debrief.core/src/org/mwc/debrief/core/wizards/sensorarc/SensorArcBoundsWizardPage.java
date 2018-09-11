/**
 * 
 */
package org.mwc.debrief.core.wizards.sensorarc;

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeBaseWizardPage;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class SensorArcBoundsWizardPage extends DynamicShapeBaseWizardPage
{
  private Text _txtInnerRadius;
  private Text _txtOuterRadius;

  private int innerRadius = 0;
  private int outerRadius = 1000;
  private ModifyListener doubleValueListener = new ModifyListener()
  {
    
    @Override
    public void modifyText(ModifyEvent e)
    {
      if(e.getSource()==_txtInnerRadius || e.getSource()==_txtOuterRadius) {
        String value = ((Text)e.getSource()).getText();
        if(isDouble(value)) {
          //valid
          if(e.getSource()==_txtInnerRadius) {
            innerRadius = Integer.valueOf(value);
          }
          else {
            outerRadius = Integer.valueOf(value);
          }
        }
        else {
          setErrorMessage("Radius must be a double value");
        }
      }
      setPageComplete(isPageComplete());
      
    }
  };
  private Text _txtArcStart;
  private Text _txtArcEnd;
  protected SensorArcBoundsWizardPage(String pageName)
  {
    super(pageName);
    setTitle("Create dynamic track shapes");
    setDescription("This wizard is used to create new track shapes (or sensor arcs)");
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
    new Label(composite,SWT.NONE).setText("Arc Start (degs)");
    _txtArcStart = new Text(composite,SWT.BORDER);
    _txtArcStart.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _txtArcStart.setToolTipText("Angle (degs) for start of shape segment");
    _txtArcStart.setText("45");
    new Label(composite,SWT.NONE).setText("Arc End (degs)");
    _txtArcEnd = new Text(composite,SWT.BORDER);
    _txtArcEnd.setText("-45");
    _txtArcEnd.setToolTipText("Angle (degs) for end of shape segment");
    _txtArcEnd.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    
    new Label(composite,SWT.NONE).setText("Inner Radius (yds)");
    _txtInnerRadius = new Text(composite,SWT.BORDER);
    _txtInnerRadius.setText("" + innerRadius);
    _txtInnerRadius.setToolTipText("Radius (yds) for inside of shape");
    _txtInnerRadius.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    new Label(composite,SWT.NONE).setText("Outer Radius (yds)");
    _txtOuterRadius = new Text(composite,SWT.BORDER);
    _txtOuterRadius.setText("" + outerRadius);
    _txtOuterRadius.setToolTipText("Radius (yds) for outside of shape");
    _txtOuterRadius.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    _txtInnerRadius.addModifyListener(doubleValueListener);
    _txtOuterRadius.addModifyListener(doubleValueListener);
    _txtArcStart.addModifyListener(new ModifyListener()
    {
      
      @Override
      public void modifyText(ModifyEvent e)
      {
        setPageComplete(isPageComplete());
      }
    });
    _txtArcEnd.addModifyListener(new ModifyListener()
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
    boolean isPageComplete =  !_txtArcStart.getText().isEmpty() &&
           !_txtArcEnd.getText().isEmpty() &&
           (!_txtInnerRadius.getText().isEmpty() && isDouble(_txtInnerRadius.getText())) && 
           (!_txtOuterRadius.getText().isEmpty() && isDouble(_txtOuterRadius.getText()));
    if(!isPageComplete) {
      setErrorMessage("All fields are not entered");
    }
    else {
      setErrorMessage(null);
    }
    return isPageComplete;
  }

  public int getArcStart() {
    return Integer.valueOf(_txtArcStart.getText());
  }
  
  public int getArcEnd() {
    return Integer.valueOf(_txtArcEnd.getText());
  }
  public int getInnerRadius()
  {
    return innerRadius;
  }
  public int getOuterRadius()
  {
    return outerRadius;
  }
  
}
