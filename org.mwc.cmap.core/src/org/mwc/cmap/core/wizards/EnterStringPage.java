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

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.osgi.service.prefs.Preferences;

import MWC.GUI.Editable;

public class EnterStringPage extends CoreEditableWizardPage
{

  public static class DataItem implements Editable
  {

    String newName;

    @Override
    public EditorType getInfo()
    {
      return null;
    }

    @Override
    public String getName()
    {
      return newName;
    }

    @Override
    public boolean hasEditor()
    {
      return false;
    }

    public void setName(final String name)
    {
      newName = name;
    }

  }

  private static final String DEFAULT_PREF_VALUE = "VALUE";

  public static String NAME = "Get Name";
  DataItem _myWrapper;
  protected String _startName;
  private final String _fieldExplanation;
  private final String _prefName;

  public EnterStringPage(final ISelection selection, final String startName,
      final String pageTitle, final String pageExplanation,
      final String fieldExplanation, final String imagePath,
      final String helpContext, final boolean useDefaults,
      final String trailingMessage)
  {
    this(selection, startName, pageTitle, pageExplanation, fieldExplanation,
        imagePath, helpContext, useDefaults, trailingMessage,
        DEFAULT_PREF_VALUE);
  }

  /**
   * 
   * @param selection
   *          the current selection
   * @param startName
   *          the string to put in the box
   * @param pageTitle
   *          what to call the page
   * @param pageExplanation
   *          help for the page
   * @param fieldExplanation
   *          help for the field
   * @param imagePath
   *          an image to show
   * @param helpContext
   *          context-senstivie help
   * @param useDefaults
   *          whether to re-use the last string value for this page
   * @param trailingMessage
   */
  public EnterStringPage(final ISelection selection, final String startName,
      final String pageTitle, final String pageExplanation,
      final String fieldExplanation, final String imagePath,
      final String helpContext, final boolean useDefaults,
      final String trailingMessage, final String prefName)
  {
    super(selection, NAME, pageTitle, pageExplanation, imagePath, helpContext,
        false, trailingMessage);
    _startName = startName;
    _fieldExplanation = fieldExplanation;
    _prefName = prefName;
    if (useDefaults)
      setDefaults();
  }

  @Override
  protected Editable createMe()
  {
    if (_myWrapper == null)
    {
      _myWrapper = new DataItem();
      _myWrapper.setName(_startName);
    }

    return _myWrapper;
  }

  @Override
  public void dispose()
  {
    // try to store some defaults
    final Preferences prefs = getPrefs();
    prefs.put(_prefName, _myWrapper.getName());
    super.dispose();
  }

  @Override
  protected String getIndex()
  {
    return "" + this.getClass() + "," + _prefName + "_"
        + _fieldExplanation.hashCode();
  }

  @Override
  protected PropertyDescriptor[] getPropertyDescriptors()
  {
    final PropertyDescriptor[] descriptors =
    {prop("Name", _fieldExplanation, getEditable())};
    return descriptors;
  }

  public String getString()
  {
    return _myWrapper.getName();
  }

  private void setDefaults()
  {
    final Preferences prefs = getPrefs();

    if (prefs != null)
    {
      _startName = prefs.get(_prefName, _startName);
    }
  }

}
