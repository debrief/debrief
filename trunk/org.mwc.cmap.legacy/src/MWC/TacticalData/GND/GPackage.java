package MWC.TacticalData.GND;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.javalite.http.Post;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.SupportsPropertyListeners;

public class GPackage extends BaseLayer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GPackage(String name, final String dbUrl, final ArrayList<String> ids)
	{
		super(false);
		super.setName(name);
		super.setVisible(false);

		// populate ourselves in a separate thread?
		Thread doer = new Thread()
		{

			@Override
			public void run()
			{
				loadMe(dbUrl, ids);
				super.run();
			}
		};

		doer.start();
	}

	private void loadMe(String dbUrl, ArrayList<String> ids)
	{
		// collate the ids
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		ArrayNode keys = mapper.createArrayNode();
		Iterator<String> iter = ids.iterator();
		while (iter.hasNext())
		{
			String id = (String) iter.next();
			keys.add(id);
		}
		root.put("keys", keys);

		try
		{
			String uri = dbUrl + "/_all_docs?include_docs=true";
			System.err.println("to:" + uri);
			String cStr = root.toString();
			byte[] content = cStr.getBytes();
			Post doIt = new Post(uri, content, 1000, 10000);
			doIt.header("Content-Type", "application/json");
			int result = doIt.responseCode();
			if (result == 200)
			{
				byte[] resB = doIt.bytes();
				JsonNode list = mapper.readValue(resB, JsonNode.class);

				// ok, what happens next?
				JsonNode tmpRows = list.get("rows");
				if (tmpRows.isArray())
				{
					ArrayNode rows = (ArrayNode) tmpRows;
					for (int i = 0; i < rows.size(); i++)
					{
						JsonNode theNode = rows.get(i);
						JsonNode theDoc = theNode.get("doc");
						GDataset newData = new GDataset(theDoc);
						GTrack track = new GTrack(newData);
						add(track);
					}
				}
			}
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
		System.out.println("load complete!");

		// tell the world
		this.firePropertyChange(SupportsPropertyListeners.FORMAT, false, true);

	}

	@Override
	public Enumeration<Editable> elements()
	{
		// TODO Auto-generated method stub
		return super.elements();
	}

}
