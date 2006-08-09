package ASSET.GUI.Workbench.Plotters;

import java.beans.*;

import MWC.GUI.*;
import MWC.GenericData.*;

public class BasePlottable implements Plottable
{
	private String _name;
	
	public BasePlottable(String name)
	{
		_name = name;
	}

	public EditorType getInfo()
	{
		return new BaseEditableInfo(this);
	}

	public String getName()
	{
		// TODO Auto-generated method stub
		return _name;
	}
	
	public void setName(String name)
	{
		_name = name;
	}

	public boolean hasEditor()
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	/**
	 * class containing editable details of a track
	 */
	public final class BaseEditableInfo extends Editable.EditorType
	{

		/**
		 * constructor for this editor, takes the actual track as a parameter
		 * 
		 * @param data
		 *          track being edited
		 */
		public BaseEditableInfo(final BasePlottable data)
		{
			super(data, data.getName(), "");
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res = {
						expertProp("Name", "name of this element", FORMAT),
				};
				return res;
			}
			catch (IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	public WorldArea getBounds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getVisible()
	{
		// TODO Auto-generated method stub
		return true;
	}

	public void paint(CanvasType dest)
	{
	}

	public double rangeFrom(WorldLocation other)
	{
		return -1;
	}

	public void setVisible(boolean val)
	{
	}

	public int compareTo(Object arg0)
	{
		BasePlottable other = (BasePlottable) arg0;
		return getName().compareTo(other.getName());
	}
}
