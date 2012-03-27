package MWC.TacticalData.GND;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.format.ISODateTimeFormat;

import MWC.GUI.SupportsPropertyListeners;

public class GDataset implements IDataset, Serializable, SupportsPropertyListeners
{

	// /////
	// private names for data fields
	// /////
	private static final String NAME = "name";
	private static final String PLATFORM = "platform";
	private static final String DATA_TYPE = "data_type";
	private static final String METADATA = "metadata";
	// /////
	// public names for data fields
	// /////
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String TIME = "time";
	public static final String ELEVATION = "elevation";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the object we populate ourselves from
	 * 
	 */
	private transient JsonNode _myNode;

	/**
	 * the name of this dataset
	 * 
	 */
	private String _name;

	/**
	 * local copy of data items
	 * 
	 */
	final private HashMap<String, double[]> _datasets;

	/**
	 * special handling for if we have time data
	 * 
	 */
	private Date[] _times;

	private URL _source;

	private String _platform;
	
	/** property change support
	 * 
	 */
	private final PropertyChangeSupport _pSupport;

	/**
	 * constructor - populate ourselves from the supplied data item
	 * 
	 * @param node
	 */
	public GDataset(URL source)
	{
		this();
		_source = source;
	}

	public GDataset(JsonNode theDoc)
	{
		this();
		_myNode = theDoc;
	}

	protected GDataset()
	{
		_datasets = new HashMap<String, double[]>();
		_pSupport = new PropertyChangeSupport(this);
	}

	private JsonNode getNode()
	{

		if (_myNode == null)
		{
			ObjectMapper mapper = new ObjectMapper();
			try
			{
				_myNode = mapper.readValue(_source, JsonNode.class);
			}
			catch (JsonParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (JsonMappingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _myNode;
	}

	@Override
	public String getName()
	{
		JsonNode node = getNode();
		if (_name == null)
		{
			JsonNode metadata = node.get(METADATA);
			_name = metadata.get(NAME).getTextValue();
			if ((_name == null) || (_name.length() == 0))
				_name = metadata.get(PLATFORM).getTextValue();
		}
		return _name;
	}

	@Override
	public Date[] getTimes()
	{

		if (_times == null)
		{
			// have a look at the types
			ArrayList<String> types = getDataTypes();
			if (types.contains(TIME))
			{
				JsonNode node = getNode();
				if (node != null)
				{
					JsonNode data = node.get(TIME);
					if (data != null)
					{
						int len = data.size();
						_times = new Date[len];
						for (int i = 0; i < len; i++)
						{
							String thisVal = data.get(i).asText();
							_times[i] = ISODateTimeFormat.dateTimeNoMillis()
									.parseDateTime(thisVal).toDate();
						}
					}
				}
			}
		}
		return _times;
	}

	public int size()
	{
		int len = 0;
		if (getDataTypes().size() > 0)
		{
			String type1 = getDataTypes().get(0);
			double[] list = getDataset(type1);
			len = list.length;
		}

		return len;
	}

	@Override
	public double[] getDataset(String name)
	{
		double[] res = null;

		res = _datasets.get(name);

		if (res == null)
		{
			JsonNode node = getNode();
			if (node != null)
			{
				// have a look at the types
				ArrayList<String> types = getDataTypes();
				if (types.contains(name))
				{
					JsonNode data = node.get(name);
					if (data != null)
					{
						int len = data.size();
						res = new double[len];
						for (int i = 0; i < len; i++)
						{
							res[i] = data.get(i).asDouble();
						}
					}
				}
			}
			_datasets.put(name, res);
		}
		return res;
	}

	@Override
	public ArrayList<String> getDataTypes()
	{
		ArrayList<String> nodes = null;
		JsonNode node = getNode();
		if (node != null)
		{
			JsonNode metadata = node.get(METADATA);
			JsonNode types = metadata.get(DATA_TYPE);

			nodes = new ArrayList<String>();
			for (int i = 0; i < types.size(); i++)
			{
				nodes.add(types.get(i).getTextValue());
			}
		}
		return nodes;
	}

	public String getPlatform()
	{
		JsonNode node = getNode();
		if (_platform == null)
		{
			JsonNode metadata = node.get(METADATA);
			JsonNode platNode = metadata.get(PLATFORM);
			_platform = platNode.getTextValue();
		}
		return _platform;
	}

	@Override
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener)
	{
		_pSupport.addPropertyChangeListener(property, listener);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		_pSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener)
	{
	 _pSupport.removePropertyChangeListener(property, listener);
	}

	@Override
	public void firePropertyChange(String propertyChanged, Object oldValue,
			Object newValue)
	{
		_pSupport.firePropertyChange(propertyChanged, oldValue, newValue);
	}
}
