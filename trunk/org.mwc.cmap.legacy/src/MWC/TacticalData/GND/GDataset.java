package MWC.TacticalData.GND;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.format.ISODateTimeFormat;

public class GDataset implements IDataset
{

	/**
	 * the object we populate ourselves from
	 * 
	 */
	private JsonNode _myNode;

	/**
	 * the name of this dataset
	 * 
	 */
	private String _name;

	/**
	 * local copy of data items
	 * 
	 */
	private HashMap<String, double[]> _datasets;

	/**
	 * special handling for if we have time data
	 * 
	 */
	private Date[] _times;

	private URL _source;

	/**
	 * constructor - populate ourselves from the supplied data item
	 * 
	 * @param node
	 */
	public GDataset(URL source)
	{
		_source = source;
		_datasets = new HashMap<String, double[]>();
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
			JsonNode metadata = node.get("metadata");
			_name = metadata.get("name").getTextValue();
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
			if (types.contains("time"))
			{
				JsonNode node = getNode();
				if (node != null)
				{
					JsonNode data = node.get("time");
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
			JsonNode metadata = node.get("metadata");
			JsonNode types = metadata.get("data_type");

			nodes = new ArrayList<String>();
			for (int i = 0; i < types.size(); i++)
			{
				nodes.add(types.get(i).getTextValue());
			}
		}
		return nodes;
	}
}
