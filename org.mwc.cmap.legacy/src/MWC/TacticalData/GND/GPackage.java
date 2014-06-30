package MWC.TacticalData.GND;

import java.io.IOException;
import java.net.URL;
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

	public GPackage(final String name, final String dbUrl, final ArrayList<String> ids)
	{
		super(false);
		super.setName(name);
		super.setVisible(false);

		// populate ourselves in a separate thread?
		final Thread doer = new Thread()
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
	/** whether this type of BaseLayer is able to have shapes added to it
	 * 
	 * @return
	 */
	@Override
	public boolean canTakeShapes()
	{
		return false;
	}
	private void loadMe(final String dbUrl, final ArrayList<String> ids)
	{
		// collate the ids
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode root = mapper.createObjectNode();
		final ArrayNode keys = mapper.createArrayNode();
		final Iterator<String> iter = ids.iterator();
		while (iter.hasNext())
		{
			final String id = (String) iter.next();
			keys.add(id);
		}
		root.put("keys", keys);

		try
		{
			final String uri = dbUrl + "/_all_docs?include_docs=true";
			final URL databaseURL = new URL(dbUrl);
			System.err.println("to:" + uri);
			final String cStr = root.toString();
			final byte[] content = cStr.getBytes();
			final Post doIt = new Post(uri, content, 1000, 10000);
			doIt.header("Content-Type", "application/json");
			final int result = doIt.responseCode();
			if (result == 200)
			{
				final byte[] resB = doIt.bytes();
				final JsonNode list = mapper.readValue(resB, JsonNode.class);

				// ok, what happens next?
				final JsonNode tmpRows = list.get("rows");
				if (tmpRows.isArray())
				{
					final ArrayNode rows = (ArrayNode) tmpRows;
					for (int i = 0; i < rows.size(); i++)
					{
						final JsonNode theNode = rows.get(i);
						final JsonNode theDoc = theNode.get("doc");
						final GDataset newData = new GDataset(theDoc, databaseURL);
						final GTrack track = new GTrack(newData);
						add(track);
					}
				}
			}
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
		System.out.println("load complete!");

		// tell the world
		this.firePropertyChange(SupportsPropertyListeners.FORMAT, false, true);

	}

	@Override
	public Enumeration<Editable> elements()
	{
		return super.elements();
	}

}
