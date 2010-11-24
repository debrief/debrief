package org.mwc.debrief.core.wizards.core;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.debrief.core.wizards.CoreEditableWizardPage;

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

	public static String NAME = "Get Name";
	DataItem _myWrapper;
	private String _startName;
	private String _fieldExplanation;
  
  protected EnterStringPage(ISelection selection, String startName, String pageExplanation, String fieldExplanation) {
		super(selection, NAME, "Get name",
				pageExplanation, "images/grid_wizard.gif", false);
		_startName = startName;
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
			_myWrapper.setName(_startName);
		}
		
		return _myWrapper;
	}

}
