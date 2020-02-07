/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.esotericsoftware.kryonet;

import static com.esotericsoftware.minlog.Log.DEBUG;
import static com.esotericsoftware.minlog.Log.ERROR;
import static com.esotericsoftware.minlog.Log.INFO;
import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.debug;
import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;
import static com.esotericsoftware.minlog.Log.trace;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.BufferOverflowException;
import java.nio.channels.SocketChannel;

import com.esotericsoftware.kryo.Context;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.SerializationException;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;

/**
 * Represents a TCP and optionally a UDP connection between a {@link Client} and
 * a {@link Server}. If either underlying connection is closed or errors, both
 * connections are closed.
 *
 * @author Nathan Sweet <misc@n4te.com>
 */
public class Connection {
	int id = -1;
	private String name;
	EndPoint endPoint;
	TcpConnection tcp;
	UdpConnection udp;
	InetSocketAddress udpRemoteAddress;
	private Listener[] listeners = {};
	private final Object listenerLock = new Object();
	private int lastPingID;
	private long lastPingSendTime;
	private int returnTripTime;
	volatile boolean isConnected;

	protected Connection() {
	}

	/**
	 * If the listener already exists, it is not added again.
	 */
	public void addListener(final Listener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null.");
		synchronized (listenerLock) {
			final Listener[] listeners = this.listeners;
			final int n = listeners.length;
			for (int i = 0; i < n; i++)
				if (listener == listeners[i])
					return;
			final Listener[] newListeners = new Listener[n + 1];
			newListeners[0] = listener;
			System.arraycopy(listeners, 0, newListeners, 1, n);
			this.listeners = newListeners;
		}
		if (TRACE)
			trace("kryonet", "Connection listener added: " + listener.getClass().getName());
	}

	public void close() {
		final boolean wasConnected = isConnected;
		isConnected = false;
		tcp.close();
		if (udp != null && udp.connectedAddress != null)
			udp.close();
		if (wasConnected) {
			notifyDisconnected();
			if (INFO)
				info("kryonet", this + " disconnected.");
		}
		setConnected(false);
	}

	/**
	 * Returns the local {@link Client} or {@link Server} to which this connection
	 * belongs.
	 */
	public EndPoint getEndPoint() {
		return endPoint;
	}

	/**
	 * Returns the server assigned ID. Will return -1 if this connection has never
	 * been connected or the last assigned ID if this connection has been
	 * disconnected.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Returns the IP address and port of the remote end of the TCP connection, or
	 * null if this connection is not connected.
	 */
	public InetSocketAddress getRemoteAddressTCP() {
		final SocketChannel socketChannel = tcp.socketChannel;
		if (socketChannel != null) {
			final Socket socket = tcp.socketChannel.socket();
			if (socket != null) {
				return (InetSocketAddress) socket.getRemoteSocketAddress();
			}
		}
		return null;
	}

	/**
	 * Returns the IP address and port of the remote end of the UDP connection, or
	 * null if this connection is not connected.
	 */
	public InetSocketAddress getRemoteAddressUDP() {
		final InetSocketAddress connectedAddress = udp.connectedAddress;
		if (connectedAddress != null)
			return connectedAddress;
		return udpRemoteAddress;
	}

	/**
	 * Returns the last calculated TCP return trip time, or -1 if
	 * {@link #updateReturnTripTime()} has never been called or the
	 * {@link FrameworkMessage.Ping} response has not yet been received.
	 */
	public int getReturnTripTime() {
		return returnTripTime;
	}

	void initialize(final Kryo kryo, final int writeBufferSize, final int objectBufferSize) {
		tcp = new TcpConnection(kryo, writeBufferSize, objectBufferSize);
	}

	/**
	 * Returns true if this connection is connected to the remote end. Note that a
	 * connection can become disconnected at any time.
	 */
	public boolean isConnected() {
		return isConnected;
	}

	void notifyConnected() {
		if (INFO) {
			final SocketChannel socketChannel = tcp.socketChannel;
			if (socketChannel != null) {
				final Socket socket = tcp.socketChannel.socket();
				if (socket != null) {
					final InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
					if (remoteSocketAddress != null)
						info("kryonet", this + " connected: " + remoteSocketAddress.getAddress());
				}
			}
		}
		final Listener[] listeners = this.listeners;
		for (int i = 0, n = listeners.length; i < n; i++)
			listeners[i].connected(this);
	}

	void notifyDisconnected() {
		final Listener[] listeners = this.listeners;
		for (int i = 0, n = listeners.length; i < n; i++)
			listeners[i].disconnected(this);
	}

	void notifyReceived(final Object object) {
		if (object instanceof Ping) {
			final Ping ping = (Ping) object;
			if (ping.isReply) {
				if (ping.id == lastPingID - 1) {
					returnTripTime = (int) (System.currentTimeMillis() - lastPingSendTime);
					if (TRACE)
						trace("kryonet", this + " return trip time: " + returnTripTime);
				}
			} else {
				ping.isReply = true;
				sendTCP(ping);
			}
		}
		final Listener[] listeners = this.listeners;
		for (int i = 0, n = listeners.length; i < n; i++)
			listeners[i].received(this, object);
	}

	public void removeListener(final Listener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null.");
		synchronized (listenerLock) {
			final Listener[] listeners = this.listeners;
			final int n = listeners.length;
			if (n == 0)
				return;
			final Listener[] newListeners = new Listener[n - 1];
			for (int i = 0, ii = 0; i < n; i++) {
				final Listener copyListener = listeners[i];
				if (listener == copyListener)
					continue;
				if (ii == n - 1)
					return;
				newListeners[ii++] = copyListener;
			}
			this.listeners = newListeners;
		}
		if (TRACE)
			trace("kryonet", "Connection listener removed: " + listener.getClass().getName());
	}

	/**
	 * Sends the object over the network using TCP.
	 *
	 * @return The number of bytes sent.
	 * @see Kryo#register(Class, com.esotericsoftware.kryo.Serializer)
	 */
	public int sendTCP(final Object object) {
		if (object == null)
			throw new IllegalArgumentException("object cannot be null.");
		try {
			final int length = tcp.send(this, object);
			if (length == 0) {
				if (TRACE)
					trace("kryonet", this + " TCP had nothing to send.");
			} else if (DEBUG) {
				final String objectString = object == null ? "null" : object.getClass().getSimpleName();
				if (!(object instanceof FrameworkMessage)) {
					debug("kryonet", this + " sent TCP: " + objectString + " (" + length + ")");
				} else if (TRACE) {
					trace("kryonet", this + " sent TCP: " + objectString + " (" + length + ")");
				}
			}
			return length;
		} catch (final IOException ex) {
			if (DEBUG)
				debug("kryonet", "Unable to send TCP with connection: " + this, ex);
			close();
			return 0;
		} catch (final SerializationException ex) {
			if (ex.causedBy(BufferOverflowException.class)) {
				if (DEBUG)
					debug("kryonet", "Unable to send TCP with connection: " + this, ex);
			} else {
				if (ERROR)
					error("kryonet", "Unable to send TCP with connection: " + this, ex);
			}
			close();
			return 0;
		}
	}

	/**
	 * Sends the object over the network using UDP.
	 *
	 * @return The number of bytes sent.
	 * @see Kryo#register(Class, com.esotericsoftware.kryo.Serializer)
	 * @throws IllegalStateException if this connection was not opened with both TCP
	 *                               and UDP.
	 */
	public int sendUDP(final Object object) {
		if (object == null)
			throw new IllegalArgumentException("object cannot be null.");
		SocketAddress address = udpRemoteAddress;
		if (address == null && udp != null)
			address = udp.connectedAddress;
		if (address == null && isConnected)
			throw new IllegalStateException("Connection is not connected via UDP.");

		final Context context = Kryo.getContext();
		context.put("connection", this);
		context.put("connectionID", id);
		try {
			if (address == null)
				throw new SocketException("Connection is closed.");

			final int length = udp.send(this, object, address);
			if (length == 0) {
				if (TRACE)
					trace("kryonet", this + " UDP had nothing to send.");
			} else if (DEBUG) {
				if (length != -1) {
					final String objectString = object == null ? "null" : object.getClass().getSimpleName();
					if (!(object instanceof FrameworkMessage)) {
						debug("kryonet", this + " sent UDP: " + objectString + " (" + length + ")");
					} else if (TRACE) {
						trace("kryonet", this + " sent UDP: " + objectString + " (" + length + ")");
					}
				} else
					debug("kryonet", this + " was unable to send, UDP socket buffer full.");
			}
			return length;
		} catch (final IOException ex) {
			if (DEBUG)
				debug("kryonet", "Unable to send UDP with connection: " + this, ex);
			close();
			return 0;
		} catch (final SerializationException ex) {
			if (ex.causedBy(BufferOverflowException.class)) {
				if (DEBUG)
					debug("kryonet", "Unable to send UDP with connection: " + this, ex);
			} else {
				if (ERROR)
					error("kryonet", "Unable to send UDP with connection: " + this, ex);
			}
			close();
			return 0;
		}
	}

	/**
	 * Workaround for broken NIO networking on Android 1.6. If true, the underlying
	 * NIO buffer is always copied to the beginning of the buffer before being given
	 * to the SocketChannel for sending. The Harmony SocketChannel implementation in
	 * Android 1.6 ignores the buffer position, always copying from the beginning of
	 * the buffer. This is fixed in Android 2.0+.
	 */
	public void setBufferPositionFix(final boolean bufferPositionFix) {
		tcp.bufferPositionFix = bufferPositionFix;
	}

	void setConnected(final boolean isConnected) {
		this.isConnected = isConnected;
		if (isConnected && name == null)
			name = "Connection " + id;
	}

	/**
	 * An empty object will be sent if the TCP connection has not sent an object
	 * within the specified milliseconds. Periodically sending a keep alive ensures
	 * that an abnormal close is detected in a reasonable amount of time (see
	 * {@link #setTimeout(int)} ). Also, some network hardware will close a TCP
	 * connection that ceases to transmit for a period of time (typically 1+
	 * minutes). Set to zero to disable. Defaults to 8000.
	 */
	public void setKeepAliveTCP(final int keepAliveMillis) {
		tcp.keepAliveMillis = keepAliveMillis;
	}

	/**
	 * Sets the friendly name of this connection. This is returned by
	 * {@link #toString()} and is useful for providing application specific
	 * identifying information in the logging. May be null for the default name of
	 * "Connection X", where X is the connection ID.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * If the specified amount of time passes without receiving an object over TCP,
	 * the connection is considered closed. When a TCP socket is closed normally,
	 * the remote end is notified immediately and this timeout is not needed.
	 * However, if a socket is closed abnormally (eg, power loss), KryoNet uses this
	 * timeout to detect the problem. The timeout should be set higher than the
	 * {@link #setKeepAliveTCP(int) TCP keep alive} for the remote end of the
	 * connection. The keep alive ensures that the remote end of the connection will
	 * be constantly sending objects, and setting the timeout higher than the keep
	 * alive allows for network latency. Set to zero to disable. Defaults to 12000.
	 */
	public void setTimeout(final int timeoutMillis) {
		tcp.timeoutMillis = timeoutMillis;
	}

	@Override
	public String toString() {
		if (name != null)
			return name;
		return "Connection " + id;
	}

	/**
	 * Requests the connection to communicate with the remote computer to determine
	 * a new value for the {@link #getReturnTripTime() return trip time}. When the
	 * connection receives a {@link FrameworkMessage.Ping} object with
	 * {@link Ping#isReply isReply} set to true, the new return trip time is
	 * available.
	 */
	public void updateReturnTripTime() {
		final Ping ping = new Ping();
		ping.id = lastPingID++;
		lastPingSendTime = System.currentTimeMillis();
		sendTCP(ping);
	}
}
