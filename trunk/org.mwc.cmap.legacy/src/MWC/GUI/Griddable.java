package MWC.GUI;

import java.beans.PropertyDescriptor;

public abstract class Griddable extends Editable.EditorType
{
	public Griddable(Object data, String name, String displayName)
	{
		super(data, name, displayName);
	}

	public static interface NonBeanPropertyDescriptor
	{
		public String getFieldName();
		public Class<?> getDataType();
		public HasNonBeanPropertyDescriptors getDataObject();
	}
	
	public static interface HasNonBeanPropertyDescriptors
	{
		public Object getValue(String fieldName);
		public void setValue(String fieldName, Object newVal);
	}
	
	abstract public PropertyDescriptor[] getGriddablePropertyDescriptors();
	abstract public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors();
}
