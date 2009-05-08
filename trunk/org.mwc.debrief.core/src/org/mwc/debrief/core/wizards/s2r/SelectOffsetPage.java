package org.mwc.debrief.core.wizards.s2r;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.debrief.core.wizards.CoreEditableWizardPage;

import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

public class SelectOffsetPage extends CoreEditableWizardPage
{
	

	public static class DataItem implements Editable
	{
		double _bearing = 0;
		WorldDistance _range = new WorldDistance(5, WorldDistance.NM);

		public double getBearing()
		{
			return _bearing;
		}

		public EditorType getInfo()
		{
			return null;
		}

		public String getName()
		{
			return null;
		}

		public WorldDistance getRange()
		{
			return _range;
		}

		@Override
		public boolean hasEditor()
		{
			return false;
		}

		public void setBearing(double bearing)
		{
			_bearing = bearing;
		}

		public void setRange(WorldDistance range)
		{
			_range = range;
		}
	}

	public static String NAME = "Initial Offset";
	DataItem _myWrapper;
  
  protected SelectOffsetPage(ISelection selection) {
		super(selection, NAME, "Set offset",
				"This page lets you enter an estimate of the range bearing to the target", "images/grid_wizard.gif", false);
  }
  

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors = {
				prop("Range", "range from ownship to start of track", getEditable()),
				prop("Bearing", "bearing from ownship to start of track", getEditable())
		};
		return descriptors;
	}

	protected Editable createMe()
	{
		if(_myWrapper == null)
			_myWrapper = new DataItem();
		
		return _myWrapper;
	}

}
