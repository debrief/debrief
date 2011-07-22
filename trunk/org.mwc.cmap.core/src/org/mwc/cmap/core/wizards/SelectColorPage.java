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

		public void setColor(Color color)
		{
			this._myColor = color;
		}

		public EditorType getInfo()
		{
			return null;
		}

		public String getName()
		{
			return null;
		}

		public boolean hasEditor()
		{
			return false;
		}

	}

	private static final String COLOR_RED = "RED";

	private static final String COLOR_BLUE = "GREEN";

	private static final String COLOR_GREEN = "BLUE";

	public static String NAME = "Get Color";
	DataItem _myWrapper;
	private Color _startColor;
	private String _fieldExplanation;

	public SelectColorPage(ISelection selection, Color startColor,
			String pageTitle, String pageExplanation, String fieldExplanation,
			String imagePath, String helpContext)
	{
		super(selection, NAME, pageTitle, pageExplanation, imagePath, helpContext,
				false);
		_startColor = startColor;
		_fieldExplanation = fieldExplanation;
		setDefaults();
	}

	private void setDefaults()
	{
		final Preferences prefs = getPrefs();

		if (prefs != null)
		{
			int red = prefs.getInt(COLOR_RED, 255);
			int green = prefs.getInt(COLOR_GREEN, 0);
			int blue = prefs.getInt(COLOR_BLUE, 0);
			_startColor = new Color(red, green, blue);
		}
	}

	@Override
	public void dispose()
	{
		// try to store some defaults
		Preferences prefs = getPrefs();

		prefs.putInt(COLOR_RED, _myWrapper.getColor().getRed());
		prefs.putInt(COLOR_BLUE, _myWrapper.getColor().getBlue());
		prefs.putInt(COLOR_GREEN, _myWrapper.getColor().getGreen());

		super.dispose();
	}

	public String getString()
	{
		return _myWrapper.getName();
	}

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors =
		{ prop("Color", _fieldExplanation, getEditable()) };
		return descriptors;
	}

	protected Editable createMe()
	{
		if (_myWrapper == null)
		{
			_myWrapper = new DataItem();
			_myWrapper.setColor(_startColor);
		}

		return _myWrapper;
	}

	public Color getColor()
	{
		Color res = Color.red;
		if (_myWrapper.getColor() != null)
			res = _myWrapper.getColor();
		return res;
	}

}
