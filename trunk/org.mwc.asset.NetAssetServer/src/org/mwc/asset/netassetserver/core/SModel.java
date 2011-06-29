package org.mwc.asset.netassetserver.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.mwc.asset.comms.kryo.common.ASpecs;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class SModel extends Server implements ASpecs
{
	private HashMap<DataPacket, Listener> _myHandlers;

	public SModel() throws IOException 
	{
		start();
		bind(TCP_PORT, UDP_PORT);

		// running...
		_myHandlers = new HashMap<DataPacket, Listener>();

		// config the data types
		ASpecs.Config.init(getKryo());

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
	
	/** and kill ourselves....
	 * 
	 */
	public final void stop()
	{
		// ditch handlers
		_myHandlers.clear();
		
		// and finish
		super.stop();
	}

	/** somebody wants to know about this starting
	 * 
	 * @param subjectType
	 * @param listener
	 */
	public void registerHandler(DataPacket subjectType, Listener listener)
	{
		_myHandlers.put(subjectType, listener);
	}

}
