package org.mwc.debrief.core.wizards;

import java.beans.PropertyDescriptor;
import java.text.ParseException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;
import org.osgi.service.prefs.Preferences;

import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

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

		public void setSpeed(final WorldSpeed speed)
		{
			_speed = speed;
		}

		public double getCourse()
		{
			return _course;
		}

		public void setCourse(final double course)
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

	public EnterSolutionPage(final ISelection selection, final String pageTitle,
			final String pageDescription, final String imagePath, final String helpContext)
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
			final double course = prefs.getDouble(COURSE, 0d);
			final String speedStr = prefs.get(SPEED, NULL_SPEED);
			final String[] parts = speedStr.split(",");
			try 
			{
				final double val = MWCXMLReader.readThisDouble(parts[0]);			
				final int units = Integer.parseInt(parts[1]);
				final WorldSpeed  speed = new WorldSpeed(val, units);
				_myWrapper.setCourse(course);
				_myWrapper.setSpeed(speed);
			} 
			catch (final ParseException e) 
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
		}
	}

	@Override
	public void dispose()
	{

		// try to store some defaults
		final Preferences prefs = getPrefs();

		prefs.putDouble(COURSE, _myWrapper.getCourse());
		final String spdTxt = _myWrapper.getSpeed().getValue() + ","
				+ _myWrapper.getSpeed().getUnits();
		prefs.put(SPEED, spdTxt);

		// TODO Auto-generated method stub
		super.dispose();
	}

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		final PropertyDescriptor[] descriptors =
		{ prop("Course", "the initial estimate of course", getEditable()),
				prop("Speed", "the initial estimate of speed", getEditable()) };
		return descriptors;
	}

	public Editable createMe()
	{
		return _myWrapper;
	}

}
