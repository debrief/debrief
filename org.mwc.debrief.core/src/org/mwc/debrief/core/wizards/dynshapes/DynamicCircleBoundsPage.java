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
import org.mwc.cmap.core.custom_widget.CWorldLocation;
import org.mwc.cmap.core.custom_widget.LocationModifiedEvent;
import org.mwc.cmap.core.custom_widget.LocationModifiedListener;

import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class DynamicCircleBoundsPage extends DynamicShapeBaseWizardPage
{

  private static final int MAX_RADIUS = 5000;
  private CWorldLocation _txtCentre;
  private Text _txtRadius;
  private final WorldLocation _centre;

  public DynamicCircleBoundsPage(final String pageName,
      final WorldLocation centre)
  {
    super(pageName,"Circle");
    _centre = centre;
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
    mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    final Composite baseComposite = super.createBaseControl(mainComposite);
    final Composite composite = new Composite(baseComposite, SWT.NULL);
    final GridLayout layout = new GridLayout(2, false);
    layout.marginLeft = layout.marginRight = 0;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    final GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
    new Label(composite, SWT.NONE).setText("Centre:");
    _txtCentre = new CWorldLocation(composite, SWT.NONE);
    _txtCentre.setLayoutData(gd);
    _txtCentre.setToolTipText("Location of centre of the dynamic circle");

    _txtCentre.addLocationModifiedListener(new LocationModifiedListener()
    {

      @Override
      public void modifyValue(final LocationModifiedEvent e)
      {
        setPageComplete(isPageComplete());

      }
    });
    new Label(composite, SWT.NONE).setText("Radius (yds): ");
    _txtRadius = new Text(composite, SWT.BORDER);
    _txtRadius.setToolTipText("Radius of the dynamic circle");

    gd.minimumWidth = 245;
    gd.heightHint = 20;
    _txtRadius.setLayoutData(gd);
    _txtRadius.addModifyListener(new ModifyListener()
    {

      @Override
      public void modifyText(final ModifyEvent e)
      {
        setPageComplete(isPageComplete());
      }
    });
    initializeUI();
    setControl(mainComposite);
  }

  public WorldLocation getCenter()
  {
    return _txtCentre.getValue();
  }

  public int getRadius()
  {
    return Integer.valueOf(_txtRadius.getText());
  }

  private void initializeUI()
  {
    _txtRadius.setText("" + MAX_RADIUS);
    _txtCentre.setValue(_centre);
  }

  @Override
  public boolean isPageComplete()
  {
    boolean isPageComplete = false;
    isPageComplete = _txtCentre.getValue() != null && _txtCentre.getValue()
        .isValid();
    if (!isPageComplete)
    {
      setErrorMessage("Please enter a valid center for the dynamic circle");
    }
    else
    {
      isPageComplete = (!_txtRadius.getText().isEmpty() && isValidRadius(
          _txtRadius.getText()));
      if (!isPageComplete)
      {
        setErrorMessage("Please enter radius in the range 0 to " + MAX_RADIUS);
      }
      else
      {
        setErrorMessage(null);
      }
    }
    return isPageComplete;
  }

  private boolean isValidRadius(final String value)
  {
    // radius must be integer in the range 0 to 4000.
    if (!value.matches("\\\\d+"))
    {
      try
      {
        final int num = Integer.valueOf(value);
        if (num > 0 && num <= MAX_RADIUS)
        {
          return true;
        }
      }
      catch (final NumberFormatException ne)
      {
        return false;
      }
    }
    return false;
  }

}
