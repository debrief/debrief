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

		public EditorType getInfo()
		{
			return null;
		}

		public String getName()
		{
			return newName;
		}

		public boolean hasEditor()
		{
			return false;
		}

		public void setName(String name)
		{
			newName = name;
		}

	}

	private static final String VALUE = "VALUE";

	public static String NAME = "Get Name";
	DataItem _myWrapper;
	protected String _startName;
	private String _fieldExplanation;

	public EnterStringPage(ISelection selection, String startName,
			String pageTitle, String pageExplanation, String fieldExplanation,
			String imagePath, String helpContext)
	{
		super(selection, NAME, pageTitle, pageExplanation, imagePath, helpContext,
				false);
		_startName = startName;
		_fieldExplanation = fieldExplanation;
		setDefaults();
	}

	@Override
	protected String getIndex()
	{
		return "" + this.getClass() + "," + _fieldExplanation;
	}

	private void setDefaults()
	{
		final Preferences prefs = getPrefs();

		if (prefs != null)
		{
			_startName = prefs.get(VALUE, _startName);
		}
	}

	@Override
	public void dispose()
	{
		// try to store some defaults
		Preferences prefs = getPrefs();
		prefs.put(VALUE, _myWrapper.getName());

		super.dispose();
	}

	public String getString()
	{
		return _myWrapper.getName();
	}

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors =
		{ prop("Name", _fieldExplanation, getEditable()) };
		return descriptors;
	}

	protected Editable createMe()
	{
		if (_myWrapper == null)
		{
			_myWrapper = new DataItem();
			_myWrapper.setName(_startName);
		}

		return _myWrapper;
	}

}
