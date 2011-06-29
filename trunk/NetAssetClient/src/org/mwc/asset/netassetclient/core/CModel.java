package org.mwc.asset.netassetclient.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;

import org.mwc.asset.comms.kryo.common.ASpecs;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class CModel extends Client implements ASpecs
{
	private HashMap<DataPacket, Listener> _myHandlers;

	public CModel() throws IOException
	{
		start();
		ASpecs.Config.init(getKryo());

		// running...
		_myHandlers = new HashMap<DataPacket, Listener>();

		// event handler
		addListener(new Listener()
		{
			public void received(Connection connection, Object object)
			{
				// loop through our packet types
				Iterator<DataPacket> pTypes = _myHandlers.keySet().iterator();
				while (pTypes.hasNext())
				{
					DataPacket datum = pTypes.next();
					if (object.getClass() == datum.getClass())
					{
						// ok, get the listner
						Listener listener = _myHandlers.get(datum);
						listener.received(connection, object);
					}
				}
			}
		});

	}

	/**
	 * and kill ourselves....
	 * 
	 */
	public final void stop()
	{
		// ditch handlers
		_myHandlers.clear();

		// and finish
		super.stop();
	}

	/**
	 * somebody wants to know about this starting
	 * 
	 * @param subjectType
	 * @param listener
	 */
	public void registerHandler(DataPacket subjectType, Listener listener)
	{
		_myHandlers.put(subjectType, listener);
	}

	public void unregisterHandler(DataPacket subjectType)
	{
		_myHandlers.remove(subjectType);
	}
	
	public void autoConnect() throws IOException
	{
		// try to find a server
		InetAddress address = discoverHost(UDP_PORT, 2000);

		// have a go
		if (address != null)
			connect(5000, address.getHostAddress(), TCP_PORT, UDP_PORT);
		
	}

}
