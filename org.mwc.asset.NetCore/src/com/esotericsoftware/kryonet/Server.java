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
import static com.esotericsoftware.minlog.Log.WARN;
import static com.esotericsoftware.minlog.Log.debug;
import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;
import static com.esotericsoftware.minlog.Log.trace;
import static com.esotericsoftware.minlog.Log.warn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.SerializationException;
import com.esotericsoftware.kryo.util.IntHashMap;
import com.esotericsoftware.kryonet.FrameworkMessage.DiscoverHost;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.FrameworkMessage.RegisterTCP;
import com.esotericsoftware.kryonet.FrameworkMessage.RegisterUDP;

/**
 * Manages TCP and optionally UDP connections from many {@link Client Clients}.
 *
 * @author Nathan Sweet <misc@n4te.com>
 */
public class Server implements EndPoint {
	private final Kryo kryo;
	private final int writeBufferSize, objectBufferSize;
	private final Selector selector;
	private ServerSocketChannel serverChannel;
	private UdpConnection udp;
	private Connection[] connections = {};
	private final IntHashMap<Connection> pendingConnections = new IntHashMap<Connection>();
	Listener[] listeners = {};
	private final Object listenerLock = new Object();
	private int nextConnectionID = 1;
	private volatile boolean shutdown;
	private final Object updateLock = new Object();
	private Thread updateThread;
	private final ByteBuffer emptyBuffer = ByteBuffer.allocate(0);

	private final Listener dispatchListener = new Listener() {
		@Override
		public void connected(final Connection connection) {
			final Listener[] listeners = Server.this.listeners;
			for (int i = 0, n = listeners.length; i < n; i++)
				listeners[i].connected(connection);
		}

		@Override
		public void disconnected(final Connection connection) {
			removeConnection(connection);
			final Listener[] listeners = Server.this.listeners;
			for (int i = 0, n = listeners.length; i < n; i++)
				listeners[i].disconnected(connection);
		}

		@Override
		public void received(final Connection connection, final Object object) {
			final Listener[] listeners = Server.this.listeners;
			for (int i = 0, n = listeners.length; i < n; i++)
				listeners[i].received(connection, object);
		}
	};

	/**
	 * Creates a Server with a write buffer size of 16384 and an object buffer size
	 * of 2048.
	 */
	public Server() {
		this(16384, 2048);
	}

	/**
	 * @param writeBufferSize  One buffer of this size is allocated for each
	 *                         connected client. Objects are serialized to the write
	 *                         buffer where the bytes are queued until they can be
	 *                         written to the socket.
	 *                         <p>
	 *                         Normally the socket is writable and the bytes are
	 *                         written immediately. If the socket cannot be written
	 *                         to and enough serialized objects are queued to
	 *                         overflow the buffer, then the connection will be
	 *                         closed.
	 *                         <p>
	 *                         The write buffer should be sized at least as large as
	 *                         the largest object that will be sent, plus some head
	 *                         room to allow for some serialized objects to be
	 *                         queued in case the buffer is temporarily not
	 *                         writable. The amount of head room needed is dependent
	 *                         upon the size of objects being sent and how often
	 *                         they are sent.
	 * @param objectBufferSize Two (using only TCP) or four (using both TCP and UDP)
	 *                         buffers of this size are allocated. These buffers are
	 *                         used to hold the bytes for a single object graph
	 *                         until it can be sent over the network or
	 *                         deserialized.
	 *                         <p>
	 *                         The object buffers should be sized at least as large
	 *                         as the largest object that will be sent or received.
	 */
	public Server(final int writeBufferSize, final int objectBufferSize) {
		this(writeBufferSize, objectBufferSize, new Kryo());
	}

	public Server(final int writeBufferSize, final int objectBufferSize, final Kryo kryo) {
		this.writeBufferSize = writeBufferSize;
		this.objectBufferSize = objectBufferSize;

		this.kryo = kryo;
		kryo.register(RegisterTCP.class);
		kryo.register(RegisterUDP.class);
		kryo.register(KeepAlive.class);
		kryo.register(DiscoverHost.class);
		kryo.register(Ping.class);

		try {
			selector = Selector.open();
		} catch (final IOException ex) {
			throw new RuntimeException("Error opening selector.", ex);
		}
	}

	private void acceptOperation(final SocketChannel socketChannel) {
		final Connection connection = newConnection();
		connection.initialize(kryo, writeBufferSize, objectBufferSize);
		connection.endPoint = this;
		final UdpConnection udp = this.udp;
		if (udp != null)
			connection.udp = udp;
		try {
			final SelectionKey selectionKey = connection.tcp.accept(selector, socketChannel);
			selectionKey.attach(connection);

			final int id = nextConnectionID++;
			if (nextConnectionID == -1)
				nextConnectionID = 1;
			connection.id = id;
			connection.setConnected(true);
			connection.addListener(dispatchListener);

			if (udp == null)
				addConnection(connection);
			else
				pendingConnections.put(id, connection);

			final RegisterTCP registerConnection = new RegisterTCP();
			registerConnection.connectionID = id;
			connection.sendTCP(registerConnection);

			if (udp == null)
				connection.notifyConnected();
		} catch (final IOException ex) {
			connection.close();
			if (DEBUG)
				debug("kryonet", "Unable to accept TCP connection.", ex);
		}
	}

	private void addConnection(final Connection connection) {
		final Connection[] newConnections = new Connection[connections.length + 1];
		newConnections[0] = connection;
		System.arraycopy(connections, 0, newConnections, 1, connections.length);
		connections = newConnections;
	}

	@Override
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
			trace("kryonet", "Server listener added: " + listener.getClass().getName());
	}

	/**
	 * Opens a TCP only server.
	 *
	 * @throws IOException if the server could not be opened.
	 */
	public void bind(final int tcpPort) throws IOException {
		bind(tcpPort, -1);
	}

	/**
	 * Opens a TCP and UDP server.
	 *
	 * @throws IOException if the server could not be opened.
	 */
	public void bind(final int tcpPort, final int udpPort) throws IOException {
		close();
		synchronized (updateLock) {
			selector.wakeup();
			try {
				serverChannel = selector.provider().openServerSocketChannel();
				serverChannel.socket().bind(new InetSocketAddress(tcpPort));
				serverChannel.configureBlocking(false);
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				if (DEBUG)
					debug("kryonet", "Accepting connections on port: " + tcpPort + "/TCP");

				if (udpPort != -1) {
					udp = new UdpConnection(kryo, objectBufferSize);
					udp.bind(selector, udpPort);
					if (DEBUG)
						debug("kryonet", "Accepting connections on port: " + udpPort + "/UDP");
				}
			} catch (final IOException ex) {
				close();
				throw ex;
			}
		}
		if (INFO)
			info("kryonet", "Server opened.");
	}

	/**
	 * Closes all open connections and the server port(s).
	 */
	@Override
	public void close() {
		Connection[] connections = this.connections;
		if (INFO && connections.length > 0)
			info("kryonet", "Closing server connections...");
		for (int i = 0, n = connections.length; i < n; i++)
			connections[i].close();
		connections = new Connection[0];

		final ServerSocketChannel serverChannel = this.serverChannel;
		if (serverChannel != null) {
			try {
				serverChannel.close();
				if (INFO)
					info("kryonet", "Server closed.");
			} catch (final IOException ex) {
				if (DEBUG)
					debug("kryonet", "Unable to close server.", ex);
			}
			this.serverChannel = null;
		}

		final UdpConnection udp = this.udp;
		if (udp != null) {
			udp.close();
			this.udp = null;
		}

		// Select one last time to complete closing the socket.
		synchronized (updateLock) {
			selector.wakeup();
			try {
				selector.selectNow();
			} catch (final IOException ignored) {
			}
		}
	}

	/**
	 * Returns the current connections. The array returned should not be modified.
	 */
	public Connection[] getConnections() {
		return connections;
	}

	@Override
	public Kryo getKryo() {
		return kryo;
	}

	@Override
	public Thread getUpdateThread() {
		return updateThread;
	}

	/**
	 * Allows the connections used by the server to be subclassed. This can be
	 * useful for storage per connection without an additional lookup.
	 */
	protected Connection newConnection() {
		return new Connection();
	}

	void removeConnection(final Connection connection) {
		final ArrayList<Connection> temp = new ArrayList<Connection>(Arrays.asList(connections));
		temp.remove(connection);
		connections = temp.toArray(new Connection[temp.size()]);

		pendingConnections.remove(connection.id);
	}

	// BOZO - Provide mechanism for sending to multiple clients without serializing
	// multiple times.

	@Override
	public void removeListener(final Listener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener cannot be null.");
		synchronized (listenerLock) {
			final Listener[] listeners = this.listeners;
			final int n = listeners.length;
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
			trace("kryonet", "Server listener removed: " + listener.getClass().getName());
	}

	@Override
	public void run() {
		if (TRACE)
			trace("kryonet", "Server thread started.");
		shutdown = false;
		while (!shutdown) {
			try {
				update(500);
			} catch (final IOException ex) {
				if (ERROR)
					error("kryonet", "Error updating server connections.", ex);
				close();
			}
		}
		if (TRACE)
			trace("kryonet", "Server thread stopped.");
	}

	public void sendToAllExceptTCP(final int connectionID, final Object object) {
		final Connection[] connections = this.connections;
		for (int i = 0, n = connections.length; i < n; i++) {
			final Connection connection = connections[i];
			if (connection.id != connectionID)
				connection.sendTCP(object);
		}
	}

	public void sendToAllExceptUDP(final int connectionID, final Object object) {
		final Connection[] connections = this.connections;
		for (int i = 0, n = connections.length; i < n; i++) {
			final Connection connection = connections[i];
			if (connection.id != connectionID)
				connection.sendUDP(object);
		}
	}

	public void sendToAllTCP(final Object object) {
		final Connection[] connections = this.connections;
		for (int i = 0, n = connections.length; i < n; i++) {
			final Connection connection = connections[i];
			connection.sendTCP(object);
		}
	}

	public void sendToAllUDP(final Object object) {
		final Connection[] connections = this.connections;
		for (int i = 0, n = connections.length; i < n; i++) {
			final Connection connection = connections[i];
			connection.sendUDP(object);
		}
	}

	public void sendToTCP(final int connectionID, final Object object) {
		final Connection[] connections = this.connections;
		for (int i = 0, n = connections.length; i < n; i++) {
			final Connection connection = connections[i];
			if (connection.id == connectionID) {
				connection.sendTCP(object);
				break;
			}
		}
	}

	public void sendToUDP(final int connectionID, final Object object) {
		final Connection[] connections = this.connections;
		for (int i = 0, n = connections.length; i < n; i++) {
			final Connection connection = connections[i];
			if (connection.id == connectionID) {
				connection.sendUDP(object);
				break;
			}
		}
	}

	@Override
	public void start() {
		new Thread(this, "Server").start();
	}

	@Override
	public void stop() {
		if (shutdown)
			return;
		close();
		if (TRACE)
			trace("kryonet", "Server thread stopping.");
		shutdown = true;
	}

	/**
	 * Accepts any new connections and reads or writes any pending data for the
	 * current connections.
	 *
	 * @param timeout Wait for up to the specified milliseconds for a connection to
	 *                be ready to process. May be zero to return immediately if
	 *                there are no connections to process.
	 */
	@Override
	public void update(final int timeout) throws IOException {
		updateThread = Thread.currentThread();
		synchronized (updateLock) { // Blocks to avoid a select while the selector is used to bind the server
									// connection.
		}
		if (timeout > 0) {
			selector.select(timeout);
		} else {
			selector.selectNow();
		}
		final Set<SelectionKey> keys = selector.selectedKeys();
		synchronized (keys) {
			final UdpConnection udp = this.udp;
			outer:
			//
			for (final Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext();) {
				final SelectionKey selectionKey = iter.next();
				iter.remove();
				try {
					final int ops = selectionKey.readyOps();
					Connection fromConnection = (Connection) selectionKey.attachment();

					if (fromConnection != null) {
						// Must be a TCP read or write operation.
						if (udp != null && fromConnection.udpRemoteAddress == null)
							continue;
						if ((ops & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
							try {
								while (true) {
									final Object object = fromConnection.tcp.readObject(fromConnection);
									if (object == null)
										break;
									if (DEBUG) {
										final String objectString = object == null ? "null"
												: object.getClass().getSimpleName();
										if (!(object instanceof FrameworkMessage)) {
											debug("kryonet", fromConnection + " received TCP: " + objectString);
										} else if (TRACE) {
											trace("kryonet", fromConnection + " received TCP: " + objectString);
										}
									}
									fromConnection.notifyReceived(object);
								}
							} catch (final IOException ex) {
								if (TRACE) {
									trace("kryonet", "Unable to read TCP from: " + fromConnection, ex);
								} else if (DEBUG) {
									debug("kryonet", fromConnection + " update: " + ex.getMessage());
								}
								fromConnection.close();
							} catch (final SerializationException ex) {
								if (ERROR)
									error("kryonet", "Error reading TCP from connection: " + fromConnection, ex);
								fromConnection.close();
							}
						}
						if ((ops & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
							try {
								fromConnection.tcp.writeOperation();
							} catch (final IOException ex) {
								if (TRACE) {
									trace("kryonet", "Unable to write TCP to connection: " + fromConnection, ex);
								} else if (DEBUG) {
									debug("kryonet", fromConnection + " update: " + ex.getMessage());
								}
								fromConnection.close();
							}
						}
						continue;
					}

					if ((ops & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
						final ServerSocketChannel serverChannel = this.serverChannel;
						if (serverChannel == null)
							continue;
						try {
							final SocketChannel socketChannel = serverChannel.accept();
							if (socketChannel != null)
								acceptOperation(socketChannel);
						} catch (final IOException ex) {
							if (DEBUG)
								debug("kryonet", "Unable to accept new connection.", ex);
						}
						continue;
					}

					// Must be a UDP read operation.
					if (udp == null)
						continue;
					InetSocketAddress fromAddress;
					try {
						fromAddress = udp.readFromAddress();
					} catch (final IOException ex) {
						if (WARN)
							warn("kryonet", "Error reading UDP data.", ex);
						continue;
					}
					if (fromAddress == null)
						continue;

					final Connection[] connections = this.connections;
					for (int i = 0, n = connections.length; i < n; i++) {
						final Connection connection = connections[i];
						if (fromAddress.equals(connection.udpRemoteAddress)) {
							fromConnection = connection;
							break;
						}
					}

					Object object;
					try {
						object = udp.readObject(fromConnection);
					} catch (final SerializationException ex) {
						if (WARN) {
							if (fromConnection != null) {
								if (ERROR)
									error("kryonet", "Error reading UDP from connection: " + fromConnection, ex);
							} else
								warn("kryonet", "Error reading UDP from unregistered address: " + fromAddress, ex);
						}
						continue;
					}

					if (object instanceof FrameworkMessage) {
						if (object instanceof RegisterUDP) {
							// Store the fromAddress on the connection and reply over TCP with a RegisterUDP
							// to
							// indicate success.
							final int fromConnectionID = ((RegisterUDP) object).connectionID;
							final Connection connection = pendingConnections.remove(fromConnectionID);
							if (connection != null) {
								if (connection.udpRemoteAddress != null)
									continue outer;
								connection.udpRemoteAddress = fromAddress;
								addConnection(connection);
								connection.sendTCP(new RegisterUDP());
								if (DEBUG)
									debug("kryonet", "Port " + udp.datagramChannel.socket().getLocalPort()
											+ "/UDP connected to: " + fromAddress);
								connection.notifyConnected();
								continue;
							}
							if (DEBUG)
								debug("kryonet", "Ignoring incoming RegisterUDP with invalid connection ID: "
										+ fromConnectionID);
							continue;
						}
						if (object instanceof DiscoverHost) {
							try {
								udp.datagramChannel.send(emptyBuffer, fromAddress);
								if (DEBUG)
									debug("kryonet", "Responded to host discovery from: " + fromAddress);
							} catch (final IOException ex) {
								if (WARN)
									warn("kryonet", "Error replying to host discovery from: " + fromAddress, ex);
							}
							continue;
						}
					}

					if (fromConnection != null) {
						if (DEBUG) {
							final String objectString = object == null ? "null" : object.getClass().getSimpleName();
							if (object instanceof FrameworkMessage) {
								if (TRACE)
									trace("kryonet", fromConnection + " received UDP: " + objectString);
							} else
								debug("kryonet", fromConnection + " received UDP: " + objectString);
						}
						fromConnection.notifyReceived(object);
						continue;
					}
					if (DEBUG)
						debug("kryonet", "Ignoring UDP from unregistered address: " + fromAddress);
				} catch (final CancelledKeyException ignored) {
					// Connection is closed.
				}
			}
		}
		final long time = System.currentTimeMillis();
		final Connection[] connections = this.connections;
		for (int i = 0, n = connections.length; i < n; i++) {
			final Connection connection = connections[i];
			if (connection.tcp.isTimedOut(time)) {
				if (DEBUG)
					debug("kryonet", connection + " timed out.");
				connection.close();
			} else {
				if (connection.tcp.needsKeepAlive(time))
					connection.sendTCP(FrameworkMessage.keepAlive);
			}
		}
	}
}
