/**
 * 
 */
package org.mwc.debrief.core.wizards.sensorarc;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public abstract class NewSensorArcBaseWizardPage extends WizardPage
{
  private String pageName;
  private static Font font;
  private static Font regularFont;

  protected NewSensorArcBaseWizardPage(String pageName)
  {
    super(pageName);
    this.pageName = pageName;
    System.out.println("Current pagename:"+pageName);
  }

  protected Composite createBaseControl(Composite parent) {
    Composite control = new Composite(parent,SWT.NULL);
    control.setLayout(new GridLayout(3,false));
    control.setLayoutData(new GridData(GridData.FILL_BOTH));
    //side part
    Composite sideBar = new Composite(control,SWT.NULL);
    sideBar.setLayout(new GridLayout());
    sideBar.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    //separator vertical line 
    Label lblVerticalBar = new Label(control, SWT.SEPARATOR | SWT.VERTICAL);
    lblVerticalBar.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    //controls that go into side bar
    Label lblTimings = new Label(sideBar,SWT.NONE);
    lblTimings.setText("Timings");
    Label lblBounds = new Label(sideBar,SWT.NONE);
    lblBounds.setText("Bounds");
    Label lblStyling = new Label(sideBar,SWT.NONE);
    lblStyling.setText("Styling");
    FontData fontData = lblTimings.getFont().getFontData()[0];
    font = new Font(Display.getDefault(), new FontData(fontData.getName(), fontData
        .getHeight(), SWT.BOLD));
    regularFont = new Font(Display.getDefault(),
        new FontData(fontData.getName(),fontData.getHeight(),SWT.NORMAL));
    if(NewSensorArcWizard.TIMINGS_PAGE.equals(pageName)) {
      lblTimings.setFont(font); 
      lblBounds.setFont(regularFont);
      lblStyling.setFont(regularFont);
    }
    else if(NewSensorArcWizard.BOUNDS_PAGE.equals(pageName)) {
      lblBounds.setFont(font);
      lblTimings.setFont(regularFont);
      lblStyling.setFont(regularFont);
    }
    else {
      lblStyling.setFont(font);
      lblBounds.setFont(regularFont);
      lblTimings.setFont(regularFont);
    }
    //part where other controls get added
    Composite parentControl = new Composite(control,SWT.NULL);
    parentControl.setLayout(new GridLayout());
    parentControl.setLayoutData(new GridData(GridData.FILL));
    return parentControl;
  }
  
  @Override
  public void dispose()
  {
    super.dispose();
    font.dispose();
    regularFont.dispose();
  }
}
