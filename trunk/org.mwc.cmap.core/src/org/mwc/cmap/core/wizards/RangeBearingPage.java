package org.mwc.cmap.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.osgi.service.prefs.Preferences;

import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

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

		public void setBearing(double bearing)
		{
			_bearing = bearing;
		}

		public void setRange(WorldDistance range)
		{
			_range = range;
		}
	}

	public static String NAME = "Initial Offset";

	DataItem _myWrapper;
	final private String _rangeTitle;
	final private String _bearingTitle;

	public RangeBearingPage(ISelection selection, String pageName,
			String pageDescription, String rangeTitle, String bearingTitle,
			String imagePath, String helpContext)
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
			double bearing = prefs.getDouble("BEARING", 0d);
			String rangeStr = prefs.get(RANGE, NULL_RANGE);
			String[] parts = rangeStr.split(",");
			double val = Double.parseDouble(parts[0]);
			int units = Integer.parseInt(parts[1]);
			WorldDistance range = new WorldDistance(val, units);
			setData(range, bearing);
		}
	}

	public void setData(WorldDistance range, double bearing)
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
		Preferences prefs = getPrefs();
		WorldDistance res = this.getRange();
		prefs.put(RANGE, "" + res.getValue() + "," + res.getUnits());
		prefs.putDouble(BEARING, _myWrapper.getBearing());

		// TODO Auto-generated method stub
		super.dispose();
	}

	public double getBearingDegs()
	{
		return _myWrapper.getBearing();
	}

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors =
		{ prop("Range", _rangeTitle, getEditable()),
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
