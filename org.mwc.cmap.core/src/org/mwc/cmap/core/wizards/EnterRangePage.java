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
import java.text.ParseException;

import org.eclipse.jface.viewers.ISelection;
import org.osgi.service.prefs.Preferences;

import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class EnterRangePage extends CoreEditableWizardPage
{

  public static class DataItem implements Editable
  {
    WorldDistance _range = new WorldDistance(5, WorldDistance.NM);

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

    public WorldDistance getRange()
    {
      return _range;
    }

    @Override
    public boolean hasEditor()
    {
      return false;
    }

    public void setRange(final WorldDistance range)
    {
      _range = range;
    }
  }

  private static final String DEFAULT_PREF_NAME = "RANGE";
  private final String _prefName;

  private static final String NULL_RANGE = "0,6";

  public static String NAME = "EnterRange";
  DataItem _myWrapper;
  final private String _rangeTitle;
  private WorldDistance _defaultRange;

  public EnterRangePage(final ISelection selection, final String pageName,
      final String pageDescription, final String rangeTitle,
      final WorldDistance defaultRange, final String imagePath,
      final String helpContext, final String trailingMessage)
  {
    this(selection, pageName, pageDescription, rangeTitle, defaultRange,
        imagePath, helpContext, trailingMessage, DEFAULT_PREF_NAME);
  }

  public EnterRangePage(final ISelection selection, final String pageName,
      final String pageDescription, final String rangeTitle,
      final WorldDistance defaultRange, final String imagePath,
      final String helpContext, final String trailingMessage,
      final String prefName)
  {
    super(selection, NAME, pageName, pageDescription, imagePath, helpContext,
        false, trailingMessage);
    _rangeTitle = rangeTitle;
    _defaultRange = defaultRange;

    _prefName = prefName;

    setDefaults();

  }

  @Override
  protected Editable createMe()
  {
    if (_myWrapper == null)

    {
      _myWrapper = new DataItem();
      _myWrapper.setRange(_defaultRange);
    }
    return _myWrapper;
  }

  @Override
  public void dispose()
  {
    // try to store some defaults
    final Preferences prefs = getPrefs();
    WorldDistance res = this.getRange();
    if (res == null)
    {
      // do we have default range?
      final int units;
      if (_defaultRange != null)
      {
        units = _defaultRange.getUnits();
      }
      else
      {
        units = WorldDistance.YARDS;
      }
      res = new WorldDistance(0, units);
    }

    prefs.put(_prefName, "" + res.getValue() + "," + res.getUnits());

    super.dispose();
  }

  @Override
  protected PropertyDescriptor[] getPropertyDescriptors()
  {
    final PropertyDescriptor[] descriptors =
    {prop("Range", _rangeTitle, getEditable()),};
    return descriptors;
  }

  public WorldDistance getRange()
  {
    return _myWrapper.getRange();
  }

  private void setDefaults()
  {
    final Preferences prefs = getPrefs();

    if (prefs != null)
    {
      final String speedStr = prefs.get(_prefName, NULL_RANGE);
      final String[] parts = speedStr.split(",");
      try
      {
        final double val = MWCXMLReader.readThisDouble(parts[0]);
        final int units = Integer.parseInt(parts[1]);
        final WorldDistance range = new WorldDistance(val, units);
        _defaultRange = range;
      }
      catch (final ParseException pe)
      {
        MWC.Utilities.Errors.Trace.trace(pe);
      }
    }
  }

  public void setRange(final WorldDistance range)
  {
    createMe();
    _myWrapper.setRange(range);
  }

}
