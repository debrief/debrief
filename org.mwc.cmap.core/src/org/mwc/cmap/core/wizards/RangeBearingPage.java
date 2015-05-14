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

public class RangeBearingPage extends CoreEditableWizardPage
{
	private static final String RANGE = "RANGE";
	private static final String BEARING = "BEARING";
	private static final String NULL_RANGE = "0,1";

	public static class DataItem implements Editable
	{
		double _bearing = 0;
		WorldDistance _range = new WorldDistance(5, WorldDistance.NM);

		public double getBearing()
		{
			return _bearing;
		}

		public EditorType getInfo()
		{
			return null;
		}

		public String getName()
		{
			return null;
		}

		public WorldDistance getRange()
		{
			return _range;
		}

		public boolean hasEditor()
		{
			return false;
		}

		public void setBearing(final double bearing)
		{
			_bearing = bearing;
		}

		public void setRange(final WorldDistance range)
		{
			_range = range;
		}
	}

	public static String NAME = "Initial Offset";

	DataItem _myWrapper;
	final private String _rangeTitle;
	final private String _bearingTitle;

	public RangeBearingPage(final ISelection selection, final String pageName,
			final String pageDescription, final String rangeTitle,
			final String bearingTitle, final String imagePath,
			final String helpContext)
	{
		super(selection, NAME, pageName, pageDescription, imagePath, helpContext,
				false);
		_rangeTitle = rangeTitle;
		_bearingTitle = bearingTitle;

		setDefaults();
	}

	private void setDefaults()
	{
		final Preferences prefs = getPrefs();

		if (prefs != null)
		{
			final double bearing = prefs.getDouble("BEARING", 0d);
			final String rangeStr = prefs.get(RANGE, NULL_RANGE);
			final String[] parts = rangeStr.split(",");
			try
			{
				final double val = MWCXMLReader.readThisDouble(parts[0]);
				final int units = Integer.parseInt(parts[1]);
				final WorldDistance range = new WorldDistance(val, units);
				setData(range, bearing);
			}
			catch (final ParseException pe)
			{
				MWC.Utilities.Errors.Trace.trace(pe);
			}
		}
	}

	public void setData(final WorldDistance range, final double bearing)
	{
		createMe();
		_myWrapper.setRange(range);
		_myWrapper.setBearing(bearing);
	}

	public WorldDistance getRange()
	{
		return _myWrapper.getRange();
	}

	@Override
	public void dispose()
	{
		// try to store some defaults
		final Preferences prefs = getPrefs();
		final WorldDistance res = this.getRange();
		if (res != null)
		{
			prefs.put(RANGE, "" + res.getValue() + "," + res.getUnits());
			prefs.putDouble(BEARING, _myWrapper.getBearing());
		}

		super.dispose();
	}

	public double getBearingDegs()
	{
		return _myWrapper.getBearing();
	}

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		final PropertyDescriptor[] descriptors = {
				prop("Range", _rangeTitle, getEditable()),
				prop("Bearing", _bearingTitle, getEditable()) };
		return descriptors;
	}

	protected Editable createMe()
	{
		if (_myWrapper == null)
			_myWrapper = new DataItem();

		return _myWrapper;
	}

}
