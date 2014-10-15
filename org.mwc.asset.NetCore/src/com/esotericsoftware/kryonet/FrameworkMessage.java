/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.esotericsoftware.kryonet;

import com.esotericsoftware.minlog.Log;

/**
 * Marker interface to denote that a message is used by the Ninja framework and is generally invisible to the developer. Eg, these
 * messages are only logged at the {@link Log#LEVEL_TRACE} level.
 * @author Nathan Sweet <misc@n4te.com>
 */
public interface FrameworkMessage {
	static final FrameworkMessage.KeepAlive keepAlive = new KeepAlive();

	/**
	 * Internal message to give the client the server assigned connection ID.
	 */
	static public class RegisterTCP implements FrameworkMessage {
		public int connectionID;
	}

	/**
	 * Internal message to give the server the client's UDP port.
	 */
	static public class RegisterUDP implements FrameworkMessage {
		public int connectionID;
	}

	/**
	 * Internal message to keep connections alive.
	 */
	static public class KeepAlive implements FrameworkMessage {
	}

	/**
	 * Internal message to discover running servers.
	 */
	static public class DiscoverHost implements FrameworkMessage {
	}

	/**
	 * Internal message to determine round trip time.
	 */
	static public class Ping implements FrameworkMessage {
		public int id;
		public boolean isReply;
	}
}
