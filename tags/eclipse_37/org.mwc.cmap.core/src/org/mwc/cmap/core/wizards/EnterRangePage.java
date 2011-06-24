package org.mwc.cmap.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

public class EnterRangePage extends CoreEditableWizardPage
{

	public static class DataItem implements Editable
	{
		WorldDistance _range = new WorldDistance(5, WorldDistance.NM);

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

		public boolean hasEditor()
		{
			return false;
		}

		public void setRange(WorldDistance range)
		{
			_range = range;
		}
	}

	public static String NAME = "EnterRange";
	DataItem _myWrapper;
	final private String _rangeTitle;
	private WorldDistance _defaultRange;

	public EnterRangePage(ISelection selection, String pageName,
			String pageDescription, String rangeTitle, WorldDistance defaultRange, String imagePath, String helpContext)
	{
		super(selection, NAME, pageName, pageDescription, imagePath,helpContext,
				false);
		_rangeTitle = rangeTitle;
		_defaultRange = defaultRange;
	}
	
	public void setRange(WorldDistance range)
	{
		createMe();
		_myWrapper.setRange(range);
	}
	
	public WorldDistance getRange()
	{
		return _myWrapper.getRange();
	}

	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors =
		{
				prop("Range", _rangeTitle,
						getEditable()),};
		return descriptors;
	}

	protected Editable createMe()
	{
		if (_myWrapper == null)
			
		{
			_myWrapper = new DataItem();
			_myWrapper.setRange(_defaultRange);
		}
		return _myWrapper;
	}

}
