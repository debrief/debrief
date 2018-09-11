/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.WorldLocation;

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

  public PolygonShape getPolygonShape() {
    String text = _coordinatesPolygon.getText();
    Vector<PolygonNode> coordinates = new Vector<PolygonNode>();
    final PolygonShape  polygon = new PolygonShape(coordinates);
  
    StringTokenizer st = new StringTokenizer(text);
  
    while (st.hasMoreTokens())
    {
      // meet the label
      final String sts = st.nextToken();
      String[] coords = sts.split(",");
      final WorldLocation wl = new WorldLocation(Double.valueOf(coords[0]), Double.valueOf(coords[1]),0);
      final PolygonNode newNode = new PolygonNode("1",
          wl, polygon);
      polygon.add(newNode);
    }
    return polygon;
  }
  @Override
  public boolean isPageComplete()
  {
    boolean isPageComplete =  !_coordinatesPolygon.getText().isEmpty() &&
        isCoordinatesValid(_coordinatesPolygon.getText());
    if(!isPageComplete) {
      setErrorMessage("The coordinates are a pair of floating point numbers"
          + " separated by comma and each coordinate separated by WhiteSpace. Eg: 12.3,234.5 11.3,44.2 12.5,45.6 -12.3,5.78, 3.65432,-14.5\r\n"); 
    }
    else {
      setErrorMessage(null);
    }
    return isPageComplete;
  }
  
  private boolean isCoordinatesValid(String text) {
    //first break the text by spaces
    boolean valid = true;
    StringTokenizer tokens = new StringTokenizer(text);
    while(tokens.hasMoreTokens() && valid) {
      String token = tokens.nextToken();
      valid = token.indexOf(",")!=-1;
      if(valid) {    
        String[] coords = token.split(",");
        valid = coords.length==2;
        valid = valid && isDouble(coords[0]) && isDouble(coords[1]);
        WorldLocation wl = new WorldLocation(Double.valueOf(coords[0]),Double.valueOf(coords[1]),0);
        valid=(wl==null)?false:true;
      }
    }
    return valid;
  }

}
