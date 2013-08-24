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

	private final URL _source;

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
	public GDataset(final URL source)
	{
		_source = source;
		_datasets = new HashMap<String, double[]>();
		_pSupport = new PropertyChangeSupport(this);
	}

	public GDataset(final JsonNode theDoc, final URL source)
	{
		this(source);
		_myNode = theDoc;
	}

	private JsonNode getNode()
	{

		if (_myNode == null)
		{
			final ObjectMapper mapper = new ObjectMapper();
			try
			{
				_myNode = mapper.readValue(_source, JsonNode.class);
			}
			catch (final JsonParseException e)
			{
				e.printStackTrace();
			}
			catch (final JsonMappingException e)
			{
				e.printStackTrace();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}
		return _myNode;
	}

	@Override
	public String getName()
	{
		final JsonNode node = getNode();
		if (_name == null)
		{
			final JsonNode metadata = node.get(METADATA);
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
			final ArrayList<String> types = getDataTypes();
			if (types.contains(TIME))
			{
				final JsonNode node = getNode();
				if (node != null)
				{
					final JsonNode data = node.get(TIME);
					if (data != null)
					{
						final int len = data.size();
						_times = new Date[len];
						for (int i = 0; i < len; i++)
						{
							final String thisVal = data.get(i).asText();
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
			final String type1 = getDataTypes().get(0);
			final double[] list = getDataset(type1);
			len = list.length;
		}

		return len;
	}

	@Override
	public double[] getDataset(final String name)
	{
		double[] res = null;

		res = _datasets.get(name);

		if (res == null)
		{
			final JsonNode node = getNode();
			if (node != null)
			{
				// have a look at the types
				final ArrayList<String> types = getDataTypes();
				if (types.contains(name))
				{
					final JsonNode data = node.get(name);
					if (data != null)
					{
						final int len = data.size();
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
		final JsonNode node = getNode();
		if (node != null)
		{
			final JsonNode metadata = node.get(METADATA);
			final JsonNode types = metadata.get(DATA_TYPE);

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
		final JsonNode node = getNode();
		if (_platform == null)
		{
			final JsonNode metadata = node.get(METADATA);
			final JsonNode platNode = metadata.get(PLATFORM);
			_platform = platNode.getTextValue();
		}
		return _platform;
	}

	@Override
	public void addPropertyChangeListener(final String property,
			final PropertyChangeListener listener)
	{
		_pSupport.addPropertyChangeListener(property, listener);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener)
	{
		_pSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final String property,
			final PropertyChangeListener listener)
	{
	 _pSupport.removePropertyChangeListener(property, listener);
	}

	@Override
	public void firePropertyChange(final String propertyChanged, final Object oldValue,
			final Object newValue)
	{
		_pSupport.firePropertyChange(propertyChanged, oldValue, newValue);
	}

	public void doSave(final String message)
	{
		// ok, build up the URL
		
		// convert ourselves to JSON
		
		// store ourselves as attachment
		
		// now update ourselves
	}
}
