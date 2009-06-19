package MWC.GUI;

import java.beans.PropertyDescriptor;

public abstract class Griddable extends Editable.EditorType
{
	public Griddable(Object data, String name, String displayName)
	{
		super(data, name, displayName);
	}

	abstract public PropertyDescriptor[] getGriddablePropertyDescriptors();
}
