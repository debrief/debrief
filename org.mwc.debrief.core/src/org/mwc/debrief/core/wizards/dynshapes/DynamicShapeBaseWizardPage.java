/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.util.regex.Pattern;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.mwc.debrief.core.wizards.sensorarc.NewSensorArcWizard;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public abstract class DynamicShapeBaseWizardPage extends WizardPage
{
  public static final String TIMINGS_PAGE = "Timings";
  public static final String BOUNDS_PAGE = "Bounds";
  public static final String STYLING_PAGE = "Styling";
  private Font font;
  private Font regularFont;

  protected DynamicShapeBaseWizardPage(final String pageName,String shapeName)
  {
    super(pageName);
    
    if(shapeName!=null && !shapeName.isEmpty()) {
      if(NewSensorArcWizard.SHAPE_NAME.equals(shapeName)) {
        setTitle("Create dynamic track shapes");
        setDescription("This wizard is used to create new track shapes (or sensor arcs)");
      }
      else {
        setTitle("Create Dynamic "+shapeName);
        setDescription("This wizard is used to create new dynamic "+shapeName.toLowerCase());
      }
    }
    
  }

  protected Composite createBaseControl(final Composite parent)
  {
    final Composite control = new Composite(parent, SWT.NULL);
    control.setLayout(new GridLayout(3, false));
    control.setLayoutData(new GridData(GridData.FILL_BOTH));
    // side part
    final Composite sideBar = new Composite(control, SWT.NULL);
    sideBar.setLayout(new GridLayout());
    sideBar.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    // separator vertical line
    final Label lblVerticalBar = new Label(control, SWT.SEPARATOR
        | SWT.VERTICAL);
    lblVerticalBar.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    // controls that go into side bar
    final Label lblTimings = new Label(sideBar, SWT.NONE);
    lblTimings.setText("Timings");
    final Label lblBounds = new Label(sideBar, SWT.NONE);
    lblBounds.setText("Bounds");
    final Label lblStyling = new Label(sideBar, SWT.NONE);
    lblStyling.setText("Styling");
    final FontData fontData = lblTimings.getFont().getFontData()[0];
    font = new Font(Display.getDefault(), new FontData(fontData.getName(),
        fontData.getHeight(), SWT.BOLD));
    regularFont = new Font(Display.getDefault(), new FontData(fontData
        .getName(), fontData.getHeight(), SWT.NORMAL));
    if (TIMINGS_PAGE.equals(getName()))
    {
      lblTimings.setFont(font);
      lblBounds.setFont(regularFont);
      lblStyling.setFont(regularFont);
    }
    else if (BOUNDS_PAGE.equals(getName()))
    {
      lblBounds.setFont(font);
      lblTimings.setFont(regularFont);
      lblStyling.setFont(regularFont);
    }
    else
    {
      lblStyling.setFont(font);
      lblBounds.setFont(regularFont);
      lblTimings.setFont(regularFont);
    }
    // part where other controls get added
    final Composite parentControl = new Composite(control, SWT.NULL);
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

  protected boolean isDouble(final String doubleString)
  {
    // from javadoc
    final String Digits = "(\\p{Digit}+)";
    final String HexDigits = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    final String Exp = "[eE][+-]?" + Digits;
    final String fpRegex = ("[\\x00-\\x20]*" + // Optional leading "whitespace"
        "[+-]?(" + // Optional sign character
        "NaN|" + // "NaN" string
        "Infinity|" + // "Infinity" string

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
        "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +

        // . Digits ExponentPart_opt FloatTypeSuffix_opt
        "(\\.(" + Digits + ")(" + Exp + ")?)|" +

        // Hexadecimal strings
        "((" +
        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "(\\.)?)|" +

        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

        ")[pP][+-]?" + Digits + "))" + "[fFdD]?))" + "[\\x00-\\x20]*");// Optional trailing
                                                                       // "whitespace"

    return Pattern.matches(fpRegex, doubleString);
  }
}
