package org.mwc.cmap.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

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
  
  public EnterStringPage(ISelection selection, String startName, String pageTitle, String pageExplanation, String fieldExplanation, String imagePath, String helpContext) {
		super(selection, NAME, pageTitle,
				pageExplanation, imagePath,helpContext, false);
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
