/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.cmap.core.wizards;

import java.awt.Color;
import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.osgi.service.prefs.Preferences;

import MWC.GUI.Editable;

public class SelectColorPage extends CoreEditableWizardPage
{

  public static class DataItem implements Editable
  {

    Color _myColor;

    public Color getColor()
    {
      return _myColor;
    }

    @Override
    public EditorType getInfo()
    {
      return null;
    }

    @Override
    public String getName()
    {
      return null;
    }

    @Override
    public boolean hasEditor()
    {
      return false;
    }

    public void setColor(final Color color)
    {
      this._myColor = color;
    }

  }

  private static final String COLOR_RED = "RED";

  private static final String COLOR_BLUE = "GREEN";

  private static final String COLOR_GREEN = "BLUE";

  public static String NAME = "Get Color";
  DataItem _myWrapper;
  private Color _startColor;
  private final String _fieldExplanation;

  /** whether we should skip using a default value from prefs
   * 
   */
  private final boolean _skipPrefs;

  public SelectColorPage(final ISelection selection, final Color startColor,
      final String pageTitle, final String pageExplanation,
      final String fieldExplanation, final String imagePath,
      final String helpContext, final String trailingMessage,
      final boolean skipPrefs)
  {
    super(selection, NAME, pageTitle, pageExplanation, imagePath, helpContext,
        false, trailingMessage);
    _startColor = startColor;
    _skipPrefs = skipPrefs;
    _fieldExplanation = fieldExplanation;
    setDefaults();
  }

  @Override
  protected Editable createMe()
  {
    if (_myWrapper == null)
    {
      _myWrapper = new DataItem();
      _myWrapper.setColor(_startColor);
    }

    return _myWrapper;
  }

  @Override
  public void dispose()
  {

    if (!_skipPrefs)
    {
      // try to store some defaults
      final Preferences prefs = getPrefs();

      prefs.putInt(COLOR_RED, _myWrapper.getColor().getRed());
      prefs.putInt(COLOR_BLUE, _myWrapper.getColor().getBlue());
      prefs.putInt(COLOR_GREEN, _myWrapper.getColor().getGreen());
    }
    super.dispose();
  }

  public Color getColor()
  {
    Color res = Color.red;
    if (_myWrapper.getColor() != null)
      res = _myWrapper.getColor();
    return res;
  }

  @Override
  protected PropertyDescriptor[] getPropertyDescriptors()
  {
    final PropertyDescriptor[] descriptors =
    {prop("Color", _fieldExplanation, getEditable())};
    return descriptors;
  }

  public String getString()
  {
    return _myWrapper.getName();
  }

  private void setDefaults()
  {
    if (!_skipPrefs)
    {
      final Preferences prefs = getPrefs();

      if (prefs != null)
      {
        final int red = prefs.getInt(COLOR_RED, 255);
        final int green = prefs.getInt(COLOR_GREEN, 0);
        final int blue = prefs.getInt(COLOR_BLUE, 0);
        _startColor = new Color(red, green, blue);
      }
    }
  }

}
