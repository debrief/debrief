package org.mwc.asset.comms.kryo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.mwc.asset.comms.kryo.common.ASpecs;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AServer implements ASpecs
{
	private Server _myServer;
	private HashMap<Class<?>, Listener> _myHandlers;

	public AServer() throws IOException
	{
		_myServer = new Server();
		_myServer.start();
		_myServer.bind(TCP_PORT, UDP_PORT);

		// running...
		_myHandlers = new HashMap<Class<?>, Listener>();

		// config the data types
		ASpecs.Config.init(_myServer.getKryo());

		// event handler
		_myServer.addListener(new Listener()
		{
			public void received(Connection connection, Object object)
			{
				// loop through our packet types
				Iterator<Class<?>> pTypes = _myHandlers.keySet().iterator();
				while (pTypes.hasNext())
				{
					Class<?> datum = (Class<?>) pTypes.next();
					if (object.getClass() == datum)
					{
						// ok, get the listner
						Listener listener = _myHandlers.get(datum);
						listener.received(connection, object);
					}
				}
			}
		});

	}

	public void registerHandler(Class<?> subjectType, Listener listener)
	{
		_myHandlers.put(subjectType, listener);
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		AServer server = new AServer();
		server.registerHandler(GetScenarios.class, new Listener()
		{

			@Override
			public void received(Connection connection, Object object)
			{
				ScenarioList response = new ScenarioList();
				Vector<ScenarioItem> scn = new Vector<ScenarioItem>();
				scn.add(new ScenarioItem("one"));
				scn.add(new ScenarioItem("two"));
				response.scenarios = scn;
				connection.sendTCP(response);
			}
		});
	}

}
