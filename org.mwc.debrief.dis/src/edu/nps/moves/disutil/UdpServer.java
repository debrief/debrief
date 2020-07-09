package edu.nps.moves.disutil;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * <em>Used in support of {@link PduMulticastReceiver}.</em>
 * </p>
 *
 * <p>
 * A robust class for establishing a UDP server and manipulating its listening
 * port and optionally a multicast groups to join. The {@link Event}s and
 * property change events make it an appropriate tool in a threaded, GUI
 * application. It is almost identical in design to the TcpServer class that
 * should have accompanied this class when you downloaded it.
 * </p>
 *
 * <p>
 * To start a UDP server, create a new UdpServer and call start():
 * </p>
 *
 * <pre>
 * UdpServer server = new UdpServer();
 * server.start();
 * </pre>
 *
 * <p>
 * Of course it won't be much help unless you know which port it's listening on
 * and you register as a listener so you'll know when a
 * <tt>java.net.DatagramPacket</tt> has come in:
 * </p>
 *
 * <pre>
 *  server.setPort(1234);
 *  server.addUdpServerListener( new UdpServer.Adapter(){
 *     public void udpServerPacketReceived( UdpServer.Event evt ){
 *         DatagramPacket packet = evt.getPacket();
 *         ...
 *     }   // end packet received
 * });
 * </pre>
 *
 * <p>
 * The server runs on one thread, and all events are fired on that thread. If
 * you have to offload heavy processing to another thread, be sure to make a
 * copy of the datagram data array since it will be reused the next time around.
 * You may use the {@link Event#getPacketAsBytes} command as a convenient way to
 * make a copy of the byte array.
 * </p>
 *
 * <p>
 * The full 64KB allowed by the UDP standard is set aside to receive the
 * datagrams, but it's possible that your host platform may truncate that.
 * </p>
 *
 * <p>
 * The public methods are all synchronized on <tt>this</tt>, and great care has
 * been taken to avoid deadlocks and race conditions. That being said, there may
 * still be bugs (please contact the author if you find any), and you certainly
 * still have the power to introduce these problems yourself.
 * </p>
 *
 * <p>
 * It's often handy to have your own class extend this one rather than making an
 * instance field to hold a UdpServer where you'd have to pass along all the
 * setPort(...) methods and so forth.
 * </p>
 *
 * <p>
 * The supporting {@link Event}, {@link Listener}, and {@link Adapter} classes
 * are static inner classes in this file so that you have only one file to copy
 * to your project. You're welcome.
 * </p>
 *
 * <p>
 * This code is released into the Public Domain. Since this is Public Domain,
 * you don't need to worry about licensing, and you can simply copy this
 * UdpServer.java file to your own package and use it as you like. Enjoy. Please
 * consider leaving the following statement here in this code:
 * </p>
 *
 * <p>
 * <em>This <tt>UdpServer</tt> class was copied to this project from its source
 * as found at
 * <a href="http://iharder.net" target="_blank">iHarder.net</a>.</em>
 * </p>
 *
 * @author Robert Harder
 * @author rharder@users.sourceforge.net
 * @version 0.1
 * @see UdpServer
 * @see Adapter
 * @see Event
 * @see Listener
 */
public class UdpServer {

	/**
	 * A helper class that implements all methods of the {@link UdpServer.Listener}
	 * interface with empty methods.
	 *
	 * <p>
	 * This code is released into the Public Domain. Since this is Public Domain,
	 * you don't need to worry about licensing, and you can simply copy this
	 * UdpServer.java file to your own package and use it as you like. Enjoy. Please
	 * consider leaving the following statement here in this code:
	 * </p>
	 *
	 * <p>
	 * <em>This <tt>UdpServer</tt> class was copied to this project from its source
	 * as found at
	 * <a href="http://iharder.net" target="_blank">iHarder.net</a>.</em>
	 * </p>
	 *
	 * @author Robert Harder
	 * @author rharder@users.sourceforge.net
	 * @version 0.1
	 * @see UdpServer
	 * @see Listener
	 * @see Event
	 */
	public class Adapter implements UdpServer.Listener {

		/**
		 * Empty call for {@link UdpServer.Listener#udpServerPacketReceived}.
		 *
		 * @param evt the event
		 */
		@Override
		public void udpServerPacketReceived(final UdpServer.Event evt) {
		}

		/**
		 * Empty call for {@link UdpServer.Listener#udpServerStateChanged}.
		 *
		 * @param evt the event
		 */
		@Override
		public void udpServerStateChanged(final UdpServer.Event evt) {
		}

	} // end static inner class Adapter

	/**
	 * An event representing activity by a {@link UdpServer}.
	 *
	 * <p>
	 * This code is released into the Public Domain. Since this is Public Domain,
	 * you don't need to worry about licensing, and you can simply copy this
	 * UdpServer.java file to your own package and use it as you like. Enjoy. Please
	 * consider leaving the following statement here in this code:
	 * </p>
	 *
	 * <p>
	 * <em>This <tt>UdpServer</tt> class was copied to this project from its source
	 * as found at
	 * <a href="http://iharder.net" target="_blank">iHarder.net</a>.</em>
	 * </p>
	 *
	 * @author Robert Harder
	 * @author rharder@users.sourceforge.net
	 * @version 0.1
	 * @see UdpServer
	 * @see Adapter
	 * @see Listener
	 */
	public static class Event extends java.util.EventObject {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a Event based on the given {@link UdpServer}.
		 *
		 * @param src the source of the event
		 */
		public Event(final UdpServer src) {
			super(src);
		}

		/**
		 * Returns the most recent datagram packet received by the {@link UdpServer}.
		 * Shorthand for <tt>getUdpServer().getPacket()</tt>.
		 *
		 * @return the most recent datagram
		 */
		public DatagramPacket getPacket() {
			return getUdpServer().getPacket();
		}

		/**
		 * Copies and returns the bytes in the most recently received packet, or null if
		 * not available.
		 *
		 * @return a copy of the datagram's byte array
		 */
		public byte[] getPacketAsBytes() {
			final DatagramPacket packet = getPacket();
			if (packet == null) {
				return null;
			} else {
				final byte[] data = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), data, 0, data.length);
				return data;
			} // end else
		} // end getPacketAsBytes

		/**
		 * Returns the data in the most recently-received packet as if it were a String
		 * or null if not available.
		 *
		 * @return The datagram as a string
		 */
		public String getPacketAsString() {
			final DatagramPacket packet = getPacket();
			if (packet == null) {
				return null;
			} else {
				final String s = new String(packet.getData(), packet.getOffset(), packet.getLength());
				return s;
			} // end else
		}

		/**
		 * Shorthand for <tt>getUdpServer().getState()</tt>.
		 *
		 * @return the state of the server
		 * @see UdpServer.State
		 */
		public UdpServer.State getState() {
			return getUdpServer().getState();
		}

		/**
		 * Returns the source of the event, a {@link UdpServer}. Shorthand for
		 * <tt>(UdpServer)getSource()</tt>.
		 *
		 * @return the server
		 */
		public UdpServer getUdpServer() {
			return (UdpServer) getSource();
		}

	} // end static inner class Event

	/**
	 * An interface for listening to events from a {@link UdpServer}. A single
	 * {@link Event} is shared for all invocations of these methods.
	 *
	 * <p>
	 * This code is released into the Public Domain. Since this is Public Domain,
	 * you don't need to worry about licensing, and you can simply copy this
	 * UdpServer.java file to your own package and use it as you like. Enjoy. Please
	 * consider leaving the following statement here in this code:
	 * </p>
	 *
	 * <p>
	 * <em>This <tt>UdpServer</tt> class was copied to this project from its source
	 * as found at
	 * <a href="http://iharder.net" target="_blank">iHarder.net</a>.</em>
	 * </p>
	 *
	 * @author Robert Harder
	 * @author rharder@users.sourceforge.net
	 * @version 0.1
	 * @see UdpServer
	 * @see Adapter
	 * @see Event
	 */
	public static interface Listener extends java.util.EventListener {

		/**
		 * Called when a packet is received. This is called on the IO thread, so don't
		 * take too long, and if you want to offload the processing to another thread,
		 * be sure to copy the data out of the datagram since it will be clobbered the
		 * next time around.
		 *
		 * @param evt the event
		 * @see Event#getPacket
		 */
		public abstract void udpServerPacketReceived(UdpServer.Event evt);

		/**
		 * Called when the state of the server has changed, such as "starting" or
		 * "stopped."
		 *
		 * @param evt the event
		 * @see UdpServer.State
		 */
		public abstract void udpServerStateChanged(UdpServer.Event evt);

	} // end inner static class Listener

	/**
	 * <p>
	 * One of four possible states for the server to be in:
	 * </p>
	 *
	 * <ul>
	 * <li>STARTING</li>
	 * <li>STARTED</li>
	 * <li>STOPPING</li>
	 * <li>STOPPED</li>
	 * </ul>
	 */
	public static enum State {
		STARTING, STARTED, STOPPING, STOPPED
	}

	private final static Logger LOGGER = Logger.getLogger(UdpServer.class.getName());
	/**
	 * The port property <tt>port</tt> used with the property change listeners and
	 * the preferences, if a preferences object is given.
	 */
	public final static String PORT_PROP = "port";
	private final static int PORT_DEFAULT = 8000;

	/**
	 * The multicast groups property <tt>groups</tt> used with the property change
	 * listeners and the preferences, if a preferences object is given. If the
	 * multicast groups is null, then no multicast groups will be joined.
	 */
	public final static String GROUPS_PROP = "groups";;
	private final static String GROUPS_DEFAULT = null;

	/**
	 * Static method returning the logging level using Java's
	 * <tt>java.util.logging</tt> package.
	 *
	 * @return the logging level
	 */
	public static Level getLoggingLevel() {
		return LOGGER.getLevel();
	}

	/**
	 * Static method to set the logging level using Java's
	 * <tt>java.util.logging</tt> package. Example:
	 * <code>UdpServer.setLoggingLevel(Level.OFF);</code>.
	 *
	 * @param level the new logging level
	 */
	public static void setLoggingLevel(final Level level) {
		LOGGER.setLevel(level);
	}

	private int port = PORT_DEFAULT;

	private String groups = GROUPS_DEFAULT;
	private State currentState = State.STOPPED;
	private final Collection<UdpServer.Listener> listeners = new LinkedList<UdpServer.Listener>(); // Event listeners
	private final UdpServer.Event event = new UdpServer.Event(this); // Shared event
	private final PropertyChangeSupport propSupport = new PropertyChangeSupport(this); // Properties

	/* ******** C O N S T R U C T O R S ******** */

	private final UdpServer This = this; // To aid in synchronizing

	private ThreadFactory threadFactory; // Optional thread factory

	private Thread ioThread; // Performs IO

	/* ******** R U N N I N G ******** */

	private MulticastSocket mSocket; // The server

	private final DatagramPacket packet = new DatagramPacket(new byte[64 * 1024], 64 * 1024); // Shared datagram

	/**
	 * Constructs a new UdpServer that will listen on the default port 8000 (but not
	 * until {@link #start} is called). The I/O thread will not be in daemon mode.
	 */
	public UdpServer() {
	}

	/**
	 * Constructs a new UdpServer that will listen on the given port (but not until
	 * {@link #start} is called). The I/O thread will not be in daemon mode.
	 *
	 * @param port The initial port on which to listen
	 */
	public UdpServer(final int port) {
		this.port = port;
	}

	/**
	 * Constructs a new UdpServer that will listen on the given port (but not until
	 * {@link #start} is called). The provided ThreadFactory will be used when
	 * starting and running the server.
	 *
	 * @param port    The initial port on which to listen
	 * @param factory The thread factory used to generate a thread to run the server
	 */
	public UdpServer(final int port, final ThreadFactory factory) {
		this.port = port;
		this.threadFactory = factory;
	}

	/**
	 * Add a property listener.
	 *
	 * @param listener the property change listener
	 */
	public synchronized void addPropertyChangeListener(final PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Add a property listener for the named property.
	 *
	 * @param property the sole property name for which to register
	 * @param listener the property change listener
	 */
	public synchronized void addPropertyChangeListener(final String property, final PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(property, listener);
	}

	/* ******** P A C K E T ******** */

	/**
	 * Adds a {@link Listener}.
	 *
	 * @param l the UdpServer.Listener
	 */
	public synchronized void addUdpServerListener(final UdpServer.Listener l) {
		listeners.add(l);
	}

	/* ******** R E C E I V E B U F F E R ******** */

	/**
	 * Fires property chagne events for all current values setting the old value to
	 * null and new value to the current.
	 */
	public synchronized void fireProperties() {
		firePropertyChange(PORT_PROP, null, getPort()); // Port
		firePropertyChange(GROUPS_PROP, null, getGroups()); // Multicast groups
	}

	/**
	 * Fire a property change event on the current thread.
	 *
	 * @param prop   name of property
	 * @param oldVal old value
	 * @param newVal new value
	 */
	protected synchronized void firePropertyChange(final String prop, final Object oldVal, final Object newVal) {
		try {
			propSupport.firePropertyChange(prop, oldVal, newVal);
		} catch (final Exception exc) {
			LOGGER.log(Level.WARNING, "A property change listener threw an exception: " + exc.getMessage(), exc);
		} // end catch
	} // end fire

	/* ******** P O R T ******** */

	/**
	 * Fires an event declaring the current state of the server. This may encourage
	 * lazy programming on your part, but it's handy to set yourself up as a
	 * listener and then fire an event in order to initialize this or that.
	 */
	public synchronized void fireState() {
		fireUdpServerStateChanged();
	}

	/**
	 * Fires event on calling thread.
	 */
	protected synchronized void fireUdpServerPacketReceived() {

		final UdpServer.Listener[] ll = listeners.toArray(new UdpServer.Listener[listeners.size()]);
		for (final UdpServer.Listener l : ll) {
			try {
				l.udpServerPacketReceived(event);
			} catch (final Exception exc) {
				LOGGER.warning("UdpServer.Listener " + l + " threw an exception: " + exc.getMessage());
			} // end catch
		} // end for: each listener
	} // end fireUdpServerPacketReceived

	/* ******** M U L T I C A S T G R O U P ******** */

	/**
	 * Fires event on calling thread.
	 */
	protected synchronized void fireUdpServerStateChanged() {

		final UdpServer.Listener[] ll = listeners.toArray(new UdpServer.Listener[listeners.size()]);
		for (final UdpServer.Listener l : ll) {
			try {
				l.udpServerStateChanged(event);
			} catch (final Exception exc) {
				LOGGER.warning("UdpServer.Listener " + l + " threw an exception: " + exc.getMessage());
			} // end catch
		} // end for: each listener
	} // end fireUdpServerStateChanged

	/**
	 * Returns the multicast groups to which the server has joined. May be null.
	 *
	 * @return The multicast groups
	 */
	public synchronized String getGroups() {
		return this.groups;
	}

	/* ******** E V E N T S ******** */

	/**
	 * Returns the last DatagramPacket received.
	 *
	 * @return the shared DatagramPacket
	 */
	public synchronized DatagramPacket getPacket() {
		return this.packet;
	}

	/**
	 * Returns the port on which the server is or will be listening.
	 *
	 * @return The port for listening.
	 */
	public synchronized int getPort() {
		return this.port;
	}

	/**
	 * Returns the receive buffer for the underlying MulticastSocket if the server
	 * is currently running (otherwise there is no MulticastSocket to query). Please
	 * see the javadocs for java.net.MulticastSocket for more information.
	 *
	 * @return receive buffer size
	 * @throws java.net.SocketException
	 */
	public synchronized int getReceiveBufferSize() throws SocketException {
		if (this.mSocket == null) {
			throw new SocketException("getReceiveBufferSize() cannot be called when the server is not started.");
		} else {
			return this.mSocket.getReceiveBufferSize();
		}
	} // end getReceiveBufferSize

	/**
	 * Returns the current state of the server, one of STOPPED, STARTING, or
	 * STARTED.
	 *
	 * @return state of the server
	 */
	public synchronized UdpServer.State getState() {
		return this.currentState;
	}

	/* ******** P R O P E R T Y C H A N G E ******** */

	/**
	 * Records (sets) the state and fires an event. This method does not change what
	 * the server is doing, only what is reflected by the currentState variable.
	 *
	 * @param state The new state of the server
	 */
	protected synchronized void recordState(final UdpServer.State state) {
		this.currentState = state;
		fireUdpServerStateChanged();
	}

	/**
	 * Remove a property listener.
	 *
	 * @param listener the property change listener
	 */
	public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Remove a property listener for the named property.
	 *
	 * @param property the sole property name for which to stop receiving events
	 * @param listener the property change listener
	 */
	public synchronized void removePropertyChangeListener(final String property,
			final PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(property, listener);
	}

	/**
	 * Removes a {@link Listener}.
	 *
	 * @param l the UdpServer.Listener
	 */
	public synchronized void removeUdpServerListener(final UdpServer.Listener l) {
		listeners.remove(l);
	}

	/**
	 * Resets the server, if it is running, otherwise does nothing. This is
	 * accomplished by registering as a listener, stopping the server, detecting the
	 * stop, unregistering, and starting the server again. It's a useful design
	 * pattern, and you may want to look at the source code for this method to check
	 * it out.
	 */
	public synchronized void reset() {
		switch (this.currentState) {
		case STARTED:
			this.addUdpServerListener(new Adapter() {
				@Override
				public void udpServerStateChanged(final UdpServer.Event evt) {
					if (evt.getState() == State.STOPPED) {
						final UdpServer server = (UdpServer) evt.getSource();
						server.removeUdpServerListener(this);
						server.start();
					} // end if: stopped
				} // end state changed
			}); // end adapter
			stop();
			break;
		} // end switch
	}

	/**
	 * This method starts up and listens indefinitely for UDP packets. On entering
	 * this method, the state is assumed to be STARTING. Upon exiting this method,
	 * the state will be STOPPING.
	 */
	protected void runServer() {
		try {
			this.mSocket = new MulticastSocket(getPort()); // Create server
			LOGGER.info("UDP Server established on port " + getPort());

			try {
				this.mSocket.setReceiveBufferSize(this.packet.getData().length);
				LOGGER.info("UDP Server receive buffer size (bytes): " + this.mSocket.getReceiveBufferSize());
			} catch (final IOException exc) {
				final int pl = this.packet.getData().length;
				final int bl = this.mSocket.getReceiveBufferSize();
				LOGGER.warning(String.format("Could not set receive buffer to %d. It is now at %d. Error: %s", pl, bl,
						exc.getMessage()));
			} // end catch

			final String gg = getGroups(); // Get multicast groups
			final String[] proposed = gg.split("[\\s,]+"); // Split along whitespace
			for (final String p : proposed) { // See which ones are valid
				try {
					this.mSocket.joinGroup(InetAddress.getByName(p));
					LOGGER.info("UDP Server joined multicast group " + p);
				} catch (final IOException exc) {
					LOGGER.warning("Could not join " + p + " as a multicast group: " + exc.getMessage());
				} // end catch
			} // end for: each proposed

			recordState(State.STARTED); // Mark as started
			LOGGER.info("UDP Server listening...");

			while (!this.mSocket.isClosed()) {
				synchronized (This) {
					if (this.currentState == State.STOPPING) {
						LOGGER.info("Stopping UDP Server by request.");
						this.mSocket.close();
					} // end if: stopping
				} // end sync

				if (!this.mSocket.isClosed()) {

					//////// B L O C K I N G
					this.mSocket.receive(packet);
					//////// B L O C K I N G

					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.fine("UDP Server received datagram: " + packet);
					}
					fireUdpServerPacketReceived();

				} // end if: not closed
			} // end while: keepGoing

		} catch (final Exception exc) {
			synchronized (This) {
				if (this.currentState == State.STOPPING) { // User asked to stop
					this.mSocket.close();
					LOGGER.info("Udp Server closed normally.");
				} else {
					LOGGER.log(Level.WARNING, "Server closed unexpectedly: " + exc.getMessage(), exc);
				} // end else
			} // end sync
		} finally {
			recordState(State.STOPPING);
			if (this.mSocket != null) {
				this.mSocket.close();
			} // end if: not null
			this.mSocket = null;
		}
	}

	/* ******** L O G G I N G ******** */

	/**
	 * <p>
	 * Sets the new multicast groups to which the server will join. If the server is
	 * already listening, then it will attempt to restart, generating start and stop
	 * events.
	 * </p>
	 *
	 * <p>
	 * The list of groups may be whitespace- and/or comma-separated. When the server
	 * starts up (or restarts), the list will be parsed, and only legitimate groups
	 * will actually be joined.
	 * </p>
	 * May be null.
	 *
	 * @param group the new groups to join
	 */
	public synchronized void setGroups(final String group) {

		final String oldVal = this.groups;
		this.groups = group;
		if (getState() == State.STARTED) {
			reset();
		} // end if: is running

		firePropertyChange(GROUPS_PROP, oldVal, this.groups);
	}

	/**
	 * Sets the new port on which the server will attempt to listen. If the server
	 * is already listening, then it will attempt to restart on the new port,
	 * generating start and stop events.
	 *
	 * @param port the new port for listening
	 * @throws IllegalArgumentException if port is outside 0..65535
	 */
	public synchronized void setPort(final int port) {
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Cannot set port outside range 0..65535: " + port);
		} // end if: port outside range

		final int oldVal = this.port;
		this.port = port;
		if (getState() == State.STARTED) {
			reset();
		} // end if: is running

		firePropertyChange(PORT_PROP, oldVal, port);
	}

	/* ******** ******** */
	/* ******** ******** */
	/* ******** S T A T I C I N N E R C L A S S L I S T E N E R ******** */
	/* ******** ******** */
	/* ******** ******** */

	/**
	 * Recommends a receive buffer size for the underlying MulticastSocket. Please
	 * see the javadocs for java.net.MulticastSocket for more information.
	 *
	 * @param size
	 * @throws java.net.SocketException
	 */
	public synchronized void setReceiveBufferSize(final int size) throws SocketException {
		if (this.mSocket == null) {
			throw new SocketException("setReceiveBufferSize(..) cannot be called when the server is not started.");
		} else {
			this.mSocket.setReceiveBufferSize(size);
		}
	} // end setReceiveBufferSize

	/* ******** ******** */
	/* ******** ******** */
	/* ******** S T A T I C I N N E R C L A S S A D A P T E R ******** */
	/* ******** ******** */
	/* ******** ******** */

	/**
	 * Attempts to start the server listening and returns immediately. Listen for
	 * start events to know if the server was successfully started.
	 *
	 * @see Listener
	 */
	public synchronized void start() {
		if (this.currentState == UdpServer.State.STOPPED) { // Only if we're stopped now
			assert ioThread == null : ioThread; // Shouldn't have a thread

			final Runnable run = new Runnable() {
				// @Override
				@Override
				public void run() {
					runServer(); // This runs for a long time
					ioThread = null;
					recordState(UdpServer.State.STOPPED); // Clear thread
				} // end run
			}; // end runnable

			if (this.threadFactory != null) { // User-specified threads
				this.ioThread = this.threadFactory.newThread(run);

			} else { // Our own threads
				this.ioThread = new Thread(run, this.getClass().getName()); // Named
			}

			recordState(UdpServer.State.STARTING); // Update state
			this.ioThread.start(); // Start thread
		} // end if: currently stopped
	} // end start

	/* ******** ******** */
	/* ******** ******** */
	/* ******** S T A T I C I N N E R C L A S S E V E N T ******** */
	/* ******** ******** */
	/* ******** ******** */

	/**
	 * Attempts to stop the server, if the server is in the STARTED state, and
	 * returns immediately. Be sure to listen for stop events to know if the server
	 * was successfully stopped.
	 *
	 * @see Listener
	 */
	public synchronized void stop() {
		if (this.currentState == UdpServer.State.STARTED) { // Only if already STARTED
			recordState(UdpServer.State.STOPPING); // Mark as STOPPING
			if (this.mSocket != null) {
				this.mSocket.close();
			} // end if: not null
		} // end if: already STARTED
	} // end stop

} // end class UdpServer
