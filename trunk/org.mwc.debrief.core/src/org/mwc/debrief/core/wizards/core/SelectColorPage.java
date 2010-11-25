package org.mwc.debrief.core.wizards.core;

import java.awt.Color;
import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.debrief.core.wizards.CoreEditableWizardPage;

import MWC.GUI.Editable;

public class SelectColorPage extends CoreEditableWizardPage
{
	

	public static class DataItem implements Editable
	{

		Color color;
		
		public Color getColor()
		{
			return color;
		}

		public void setColor(Color color)
		{
			this.color = color;
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

	public static String NAME = "Get Color";
	DataItem _myWrapper;
	private Color _startColor;
	private String _fieldExplanation;
  
  protected SelectColorPage(ISelection selection, Color startColor, String pageExplanation, String fieldExplanation) {
		super(selection, NAME, "SOME_TEXT",
				pageExplanation, "images/grid_wizard.gif", false);
		_startColor = startColor;
		_fieldExplanation = fieldExplanation;
  }
  
  public String getString()
  {
  	return _myWrapper.getName();
  }
  
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors = {
				prop("Name", _fieldExplanation, getEditable())
		};
		return descriptors;
	}

	protected Editable createMe()
	{
		if(_myWrapper == null)
		{
			_myWrapper = new DataItem();
			_myWrapper.setColor(_startColor);
		}
		
		return _myWrapper;
	}

	public Color getColor()
	{
		return _myWrapper.getColor();
	}

}
