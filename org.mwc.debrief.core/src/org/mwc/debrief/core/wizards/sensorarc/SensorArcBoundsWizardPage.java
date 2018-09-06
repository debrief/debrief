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

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class SensorArcBoundsWizardPage extends NewSensorArcBaseWizardPage
{
  private Text _txtInnerRadius;
  private Text _txtOuterRadius;

  private int innerRadius;
  private int outerRadius;
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
    _txtInnerRadius.setText("0");
    _txtInnerRadius.setToolTipText("Radius (yds) for inside of shape");
    _txtInnerRadius.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false));
    new Label(composite,SWT.NONE).setText("Outer Radius (yds)");
    _txtOuterRadius = new Text(composite,SWT.BORDER);
    _txtOuterRadius.setText("1000");
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
  
  
  
  private boolean isDouble(String doubleString) {
    //from javadoc
    final String Digits     = "(\\p{Digit}+)";
    final String HexDigits  = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    final String Exp        = "[eE][+-]?"+Digits;
    final String fpRegex    =
        ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
         "[+-]?(" + // Optional sign character
         "NaN|" +           // "NaN" string
         "Infinity|" +      // "Infinity" string

         // A decimal floating-point string representing a finite positive
         // number without a leading sign has at most five basic pieces:
         // Digits . Digits ExponentPart FloatTypeSuffix
         //
         // Since this method allows integer-only strings as input
         // in addition to strings of floating-point literals, the
         // two sub-patterns below are simplifications of the grammar
         // productions from section 3.10.2 of
         // The Java Language Specification.

         // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
         "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

         // . Digits ExponentPart_opt FloatTypeSuffix_opt
         "(\\.("+Digits+")("+Exp+")?)|"+

         // Hexadecimal strings
         "((" +
          // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
          "(0[xX]" + HexDigits + "(\\.)?)|" +

          // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
          "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

          ")[pP][+-]?" + Digits + "))" +
         "[fFdD]?))" +
         "[\\x00-\\x20]*");// Optional trailing "whitespace"

    if (Pattern.matches(fpRegex, doubleString))
        return true;
    return false;
  }
}
