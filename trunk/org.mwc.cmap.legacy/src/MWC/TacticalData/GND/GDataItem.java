package MWC.TacticalData.GND;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import MWC.GUI.CanvasType;
import MWC.GUI.Griddable;
import MWC.GUI.Griddable.HasNonBeanPropertyDescriptors;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class GDataItem implements Plottable, TimeStampedDataItem,
		HasNonBeanPropertyDescriptors
{
	public static final String LOCATION = "Location";

	
	public static interface Setter
	{
		public void setValue(String name, Object value);
	}
	
	/** the griddable dataset for this class
	 * 
	 * @author ian
	 *
	 */
	public final class GDataInfo extends Griddable
	{

		private NonBeanPropertyDescriptor[] _myGridProps;
		private PropertyDescriptor[] _myInfoPropertyDescriptors;

		public GDataInfo(final GDataItem data)
		{
			super(data, data.getName(), "GND item:" + data.getName());
		}

		@Override
		public final String getDisplayName()
		{
			return getData().toString();
		}

		@Override
		public PropertyDescriptor[] getGriddablePropertyDescriptors()
		{
			return null;
		}

		public final BeanInfo[] getAdditionalBeanInfo()
		{
			return null;
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			if (_myInfoPropertyDescriptors == null)
			{
				try
				{
					_myInfoPropertyDescriptors = new PropertyDescriptor[]
					{ prop("Visible", "whether the whole fix is visible", VISIBILITY) };
				}
				catch (IntrospectionException ee)
				{
					ee.printStackTrace();
				}
			}
			return _myInfoPropertyDescriptors;
		}

		public final MethodDescriptor[] getMethodDescriptors()
		{
			return null;
		}

		public final SubjectAction[] getUndoableActions()
		{
			return null;
		}

		@Override
		public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors()
		{
			final GDataItem gt = (GDataItem) this.getData();
			if (_myGridProps == null)
			{
				Set<String> keys = gt._myData.keySet();
				Iterator<String> iter = keys.iterator();
				ArrayList<NonBeanPropertyDescriptor> list = new ArrayList<NonBeanPropertyDescriptor>();
				while (iter.hasNext())
				{
					String field = (String) iter.next();
					
					// skip the LON field
					if(field.equals(GDataset.LON))
						continue;
					
					// also skip the time field, we handle it separately
					if(field.equals(GDataset.TIME))
						continue;
					// if it's the LAT - convert to location
					if(field.equals(GDataset.LAT))
						field = LOCATION;
					
					final String theField = field;
					
					NonBeanPropertyDescriptor newItem = new NonBeanPropertyDescriptor()
					{

						@Override
						public String getFieldName()
						{
							// TODO Auto-generated method stub
							return theField;
						}

						@Override
						public Class<?> getDataType()
						{
							// TODO Auto-generated method stub
							Class<?> res;
							if (theField.equals(GDataset.TIME))
								res = Date.class;
							else if (theField.equals(LOCATION))
								res = WorldLocation.class;
							else
								res = Double.class;

							return res;
						}

						@Override
						public HasNonBeanPropertyDescriptors getDataObject()
						{
							return gt;
						}
					};
					list.add(newItem);
				}
				_myGridProps = list.toArray(new NonBeanPropertyDescriptor[]
				{ null });
			}
			return _myGridProps;
		}

	}

	private HashMap<String, Object> _myData;
	private String _name;
	private Setter _setter;

	public GDataItem(String name, HashMap<String, Object> fields, Setter setter)
	{
		_myData = fields;
		_name = name;
		_setter = setter;
	}

	@Override
	public String getName()
	{
		return _name;
	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	@Override
	public EditorType getInfo()
	{
		return new GDataInfo(this);
	}

	@Override
	public int compareTo(Plottable o)
	{
		if (o instanceof GDataItem)
		{
			GDataItem other = (GDataItem) o;
			return this.getTime().compareTo(other.getTime());
		}
		else
			return 0;
	}

	public HiResDate getTime()
	{
		HiResDate res = null;
		if (_myData.get("time") != null)
		{
			Date date = (Date) _myData.get("time");
			res = new HiResDate(date);
		}
		return res;
	}

	@Override
	public void paint(CanvasType dest)
	{
	}

	@Override
	public WorldArea getBounds()
	{
		return null;
	}

	@Override
	public boolean getVisible()
	{
		return true;
	}

	@Override
	public void setVisible(boolean val)
	{
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		return Plottable.INVALID_RANGE;
	}

	@Override
	public HiResDate getDTG()
	{
		return getTime();
	}

	@Override
	public void setDTG(HiResDate date)
	{
		// TODO: somehow, handle this!
	}

	public Object getValue(String fieldName)
	{
		final Object res;
		// special case, is it hte lcoation?
		if(fieldName.equals(LOCATION))
		{
			WorldLocation loc = new WorldLocation((Double)_myData.get(GDataset.LAT),(Double) _myData.get(GDataset.LON), 0d);
			Double elevation = (Double) _myData.get(GDataset.ELEVATION);
			if(elevation != null)
				loc.setDepth(-elevation.doubleValue());
			
			res = loc;
		}
		else
			res =_myData.get(fieldName);
		
		return res;
	}

	@Override
	public void setValue(String fieldName, Object newVal)
	{
		_myData.put(fieldName, newVal);
		_setter.setValue(fieldName, newVal);
	}


	/** deep copy constructor
	 * 
	 * @return
	 */
	public TimeStampedDataItem makeCopy()
	{
		HashMap<String, Object> res =new HashMap<String,Object>(_myData);
		return new GDataItem(_name, res, _setter);
	}

}