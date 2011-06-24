package org.mwc.cmap.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import MWC.GUI.Editable;

public class EnterBooleanPage extends CoreEditableWizardPage
{
	

	public static class DataItem implements Editable
	{
		Boolean newVal;
		
		public EditorType getInfo()
		{
			return null;
		}

		public String getName()
		{
			return "Boolean editor";
		}
		
		public Boolean getValue()
		{
			return newVal;
		}
		
		public void setValue(Boolean val)
		{
			newVal = val;
		}


		public boolean hasEditor()
		{
			return false;
		}

	}

	public static String NAME = "Get Name";
	DataItem _myWrapper;
	private String _fieldExplanation;
	private Boolean _startValue;
  
  public EnterBooleanPage(ISelection selection, Boolean startVal, String pageTitle, String pageExplanation, String fieldExplanation, String imagePath, String helpContext) {
		super(selection, NAME, pageTitle,
				pageExplanation, imagePath,helpContext, false);
		_startValue = startVal;
		_fieldExplanation = fieldExplanation;
  }
  
  public Boolean getBoolean()
  {
  	return _myWrapper.getValue();
  }
  
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors = {
				prop("Value", _fieldExplanation, getEditable())
		};
		return descriptors;
	}

	protected Editable createMe()
	{
		if(_myWrapper == null)
		{
			_myWrapper = new DataItem();
			_myWrapper.setValue(_startValue);
		}
		
		return _myWrapper;
	}

}
