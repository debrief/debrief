package org.mwc.debrief.core.wizards.s2r;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Text;
import org.mwc.debrief.core.wizards.CorePlottableWizardPage;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.WorldSpeedPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;

public class EnterSolutionPage extends CorePlottableWizardPage
{
	public static class DataItem implements Plottable
	{
		public WorldSpeed _speed = new WorldSpeed(0, WorldSpeed.Kts);
		public double _sourse = 0;
		
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
			return _sourse;
		}
		public void setCourse(double course)
		{
			_sourse = course;
		}
		public static class DataInfo extends EditorType
		{
			public DataInfo(DataItem object)
			{
				super(object, "Initial Solution", "Initial solution");
			}
		   public final PropertyDescriptor[] getPropertyDescriptors()
		    {
		      try{
		        final PropertyDescriptor[] res={
		          prop("Course", "the initial estimate of course", FORMAT),
		          prop("Speed", "the initial estimate of speed"),};
		        
		        res[1].setPropertyEditorClass(WorldSpeedPropertyEditor.class);
		        return res;
		      }catch(IntrospectionException e){
		        return super.getPropertyDescriptors();
		      }
		    }
		}
		
		@Override
		public EditorType getInfo()
		{
			return new DataInfo(this);
		}
		@Override
		public String getName()
		{
			return "Local solution";
		}
		@Override
		public boolean hasEditor()
		{
			return true;
		}
		@Override
		public WorldArea getBounds()
		{
			return null;
		}
		@Override
		public boolean getVisible()
		{
			return false;
		}
		@Override
		public void paint(CanvasType dest)
		{
			
		}
		@Override
		public double rangeFrom(WorldLocation other)
		{
			return 0;
		}
		@Override
		public void setVisible(boolean val)
		{
			
		}
		@Override
		public int compareTo(Plottable arg0)
		{
			return 0;
		}
		
		
	}

	DataItem _myWrapper;
  
  Text secondNameText;
  protected EnterSolutionPage(ISelection selection) {
		super(selection, "gridPage", "Set solution",
				"This page lets you enter and initial solution", "images/grid_wizard.gif", false);
		
		_myWrapper = new DataItem();
  }
  

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors = {
				prop("Course", "the initial estimate of course", getPlottable()),
				prop("Speed", "the initial estimate of speed", getPlottable())
		};
		return descriptors;
	}

	protected Plottable createMe()
	{

		return _myWrapper;
	}


	
}
