package org.mwc.debrief.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;
import org.osgi.service.prefs.Preferences;

import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

public class EnterSolutionPage extends CoreEditableWizardPage
{
	private static final String COURSE = "COURSE";

	private static final String SPEED = "SPEED";

	private static final String NULL_SPEED = "0,1";

	public static String NAME = "Initial SOLUTION";

	public static class SolutionDataItem implements Editable
	{
		public WorldSpeed _speed = new WorldSpeed(0, WorldSpeed.Kts);
		public double _course = 0;

		public WorldSpeed getSpeed()
		{
			return _speed;
		}

		public void setSpeed(WorldSpeed speed)
		{
			_speed = speed;
		}

		public double getCourse()
		{
			return _course;
		}

		public void setCourse(double course)
		{
			_course = course;
		}

		public EditorType getInfo()
		{
			return null;
		}

		public String getName()
		{
			return "Local solution";
		}

		public boolean hasEditor()
		{
			return false;
		}

	}

	SolutionDataItem _myWrapper;

	Text secondNameText;

	public EnterSolutionPage(ISelection selection, String pageTitle,
			String pageDescription, String imagePath, String helpContext)
	{
		super(selection, NAME, pageTitle, pageDescription, imagePath, helpContext,
				false);

		_myWrapper = new SolutionDataItem();

		setDefaults();
	}

	private void setDefaults()
	{
		final Preferences prefs = getPrefs();

		if (prefs != null)
		{
			double course = prefs.getDouble(COURSE, 0d);
			String speedStr = prefs.get(SPEED, NULL_SPEED);
			String[] parts = speedStr.split(",");
			double val = Double.parseDouble(parts[0]);
			int units = Integer.parseInt(parts[1]);
			WorldSpeed  speed = new WorldSpeed(val, units);
			_myWrapper.setCourse(course);
			_myWrapper.setSpeed(speed);
		}
	}

	@Override
	public void dispose()
	{

		// try to store some defaults
		Preferences prefs = getPrefs();

		prefs.putDouble(COURSE, _myWrapper.getCourse());
		String spdTxt = _myWrapper.getSpeed().getValue() + ","
				+ _myWrapper.getSpeed().getUnits();
		prefs.put(SPEED, spdTxt);

		// TODO Auto-generated method stub
		super.dispose();
	}

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors =
		{ prop("Course", "the initial estimate of course", getEditable()),
				prop("Speed", "the initial estimate of speed", getEditable()) };
		return descriptors;
	}

	public Editable createMe()
	{
		return _myWrapper;
	}

}
