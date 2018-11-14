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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mwc.cmap.core.custom_widget.CWorldLocation;
import org.mwc.cmap.core.custom_widget.LocationModifiedEvent;
import org.mwc.cmap.core.custom_widget.LocationModifiedListener;

import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * @author Ayesha
 *
 */
public class DynamicRectangleBoundsPage extends DynamicShapeBaseWizardPage
{
  private CWorldLocation _topLeftLocation;
  private CWorldLocation _bottomRightLocation;
  private final WorldLocation _centre;

  public DynamicRectangleBoundsPage(final String pageName,
      final WorldLocation centre)
  {
    super(pageName,"Rectangle");
    _centre = centre;
    setTitle("Create dynamic rectangle");
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
    new Label(composite, SWT.NONE).setText("Top left:");
    _topLeftLocation = new CWorldLocation(composite, SWT.NONE);
    _topLeftLocation.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true,
        false));
    _topLeftLocation.setToolTipText("Top left of the dynamic rectangle");
    _topLeftLocation.addLocationModifiedListener(new LocationModifiedListener()
    {

      @Override
      public void modifyValue(final LocationModifiedEvent e)
      {
        setPageComplete(isPageComplete());

      }
    });
    new Label(composite, SWT.NONE).setText("Bottom Right: ");
    _bottomRightLocation = new CWorldLocation(composite, SWT.NONE);
    _bottomRightLocation.setToolTipText(
        "Bottom right of the dynamic rectangle");
    _bottomRightLocation.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
        true, false));
    _bottomRightLocation.addLocationModifiedListener(
        new LocationModifiedListener()
        {

          @Override
          public void modifyValue(final LocationModifiedEvent e)
          {
            setPageComplete(isPageComplete());

          }
        });
    initializeUI();
    setControl(mainComposite);
  }

  public WorldLocation getBottomRightLocation()
  {
    return _bottomRightLocation.getValue();
  }

  public WorldLocation getTopLeftLocation()
  {
    return _topLeftLocation.getValue();
  }

  private void initializeUI()
  {
    _topLeftLocation.setValue(_centre);
    _bottomRightLocation.setValue(_centre.add(new WorldVector(
        MWC.Algorithms.Conversions.Degs2Rads(45), 0.05, 0)));
  }

  @Override
  public boolean isPageComplete()
  {
    boolean isComplete = false;
    isComplete = _topLeftLocation.getValue() != null && _topLeftLocation
        .getValue().isValid();
    if (!isComplete)
    {
      setErrorMessage("Invalid Top Left Location");
    }
    else
    {
      isComplete = _bottomRightLocation.getValue() != null
          && _bottomRightLocation.getValue().isValid();
      if (!isComplete)
      {
        setErrorMessage("Invalid Bottom Right Location");
      }
      else
      {
        setErrorMessage(null);
      }
    }
    return isComplete;
  }

}
