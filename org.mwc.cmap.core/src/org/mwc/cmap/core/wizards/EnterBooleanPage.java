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
		
		public void setValue(final Boolean val)
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
	private final String _fieldExplanation;
	private final Boolean _startValue;
  
  public EnterBooleanPage(final ISelection selection, final Boolean startVal, final String pageTitle, final String pageExplanation, final String fieldExplanation, final String imagePath, final String helpContext) {
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
		final PropertyDescriptor[] descriptors = {
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
