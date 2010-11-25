package org.mwc.debrief.core.wizards.s2r;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Text;
import org.mwc.debrief.core.wizards.CoreEditableWizardPage;

import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

public class EnterSolutionPage extends CoreEditableWizardPage
{
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
  public EnterSolutionPage(ISelection selection,String pageTitle, String pageDescription, String imagePath) {
		super(selection, NAME, pageTitle,
				pageDescription, imagePath, false);
		
		_myWrapper = new SolutionDataItem();
  }
  

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors = {
				prop("Course", "the initial estimate of course", getEditable()),
				prop("Speed", "the initial estimate of speed", getEditable())
		};
		return descriptors;
	}

	protected Editable createMe()
	{
		return _myWrapper;
	}


	
}
