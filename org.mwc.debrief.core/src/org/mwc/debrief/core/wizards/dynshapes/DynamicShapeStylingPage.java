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
public class DynamicShapeStylingPage extends DynamicShapeBaseWizardPage
{
  final private String _type;

  private Text _txtSymbology;

  private Text _txtLabel;

  public DynamicShapeStylingPage(final String pageName, final String type)
  {
    super(pageName,type);
    this._type = type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl(final Composite parent)
  {
    final Composite mainComposite = new Composite(parent, SWT.NULL);
    mainComposite.setLayout(new GridLayout());
    mainComposite.setLayoutData(new GridData(GridData.FILL));
    final Composite baseComposite = super.createBaseControl(mainComposite);
    final Composite composite = new Composite(baseComposite, SWT.NULL);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(GridData.FILL));
    new Label(composite, SWT.NONE).setText("Symbology : ");
    _txtSymbology = new Text(composite, SWT.BORDER);
    final GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
    gd.minimumWidth = 125;
    _txtSymbology.setLayoutData(gd);
    _txtSymbology.setToolTipText("Color/Style to use");
    _txtSymbology.setText("@C");
    _txtSymbology.setLayoutData(gd);
    new Label(composite, SWT.NONE).setText("Label : ");
    _txtLabel = new Text(composite, SWT.BORDER);
    _txtLabel.setLayoutData(gd);
    _txtLabel.setToolTipText("Name this arc");
    _txtLabel.setText(_type);
    _txtSymbology.addModifyListener(new ModifyListener()
    {

      @Override
      public void modifyText(final ModifyEvent e)
      {
        setPageComplete(isPageComplete());
      }
    });
    _txtLabel.addModifyListener(new ModifyListener()
    {

      @Override
      public void modifyText(final ModifyEvent e)
      {
        setPageComplete(isPageComplete());

      }
    });
    setControl(mainComposite);
  }

  public String getShapeLabel()
  {
    return _txtLabel.getText();
  }

  public String getSymbology()
  {
    return _txtSymbology.getText();
  }

  @Override
  public boolean isPageComplete()
  {
    final boolean isPageComplete = !_txtSymbology.getText().isEmpty()
        && !_txtLabel.getText().isEmpty();
    if (!isPageComplete)
    {
      setErrorMessage("All fields are not entered");
    }
    else
    {
      setErrorMessage(null);
    }
    return isPageComplete;
  }

}
