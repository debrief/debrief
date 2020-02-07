package edu.nps.moves.disutil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.DatagramSocketImpl;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * A robust class for establishing simultaneous TCP and UDP servers and
 * manipulating their listening ports. The {@link Event}s and property change
 * events make it an appropriate tool in a threaded, GUI application. It is
 * almost identical in design to the UdpServer and TcpServer classes that should
 * have accompanied this class when you downloaded it.
 * </p>
 *
 * <p>
 * To start a server, create a new NioServer and call start():
 * </p>
 *
 * <pre>
 * NioServer server = new NioServer();
 * server.start();
 * </pre>
 *
 * <p>
 * You'll want to bind to a port or two:
 * </p>
 *
 * <pre>
 * server.addTcpBinding(new InetSocketAddress(80));
 * server.addUdpBinding(new InetSocketAddress(80));
 * </pre>
 *
 * <p>
 * Of course it won't be much help unless you register as a listener so you'll
 * know when data has come in:
 * </p>
 *
 * <pre>
 *  server.addNioServerListener( new NioServer.Adapter(){
 *     public void nioServerTcpDataReceived( NioServer.Event evt ){
 *         ByteBuffer buff = evt.getBuffer();
 *         ...
 *     }   // end data received
 *
 *     public void nioServerUdpDataReceived( NioServer.Event evt ){
 *         ByteBuffer buff = evt.getBuffer();
 *         ...
 *     }   // end data received
 * });
 * </pre>
 *
 * <p>
 * The server runs on one thread, and all events are fired on that thread.
 * Consider offloading heavy processing to another thread. Be aware that you can
 * register multiple listeners to respond to incoming data so be mindful of more
 * than one listener being around to makes calls on the data.
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
 * instance field to hold a NioServer where you'd have to pass along all the
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
 * Since the TcpServer.java, UdpServer.java, and NioServer.java are so similar,
 * and since lots of copying and pasting was going on among them, you may find
 * some comments that refer to TCP instead of UDP or vice versa. Please feel
 * free to let me know, so I can correct that.
 * </p>
 *
 * <p>
 * This code is released into the Public Domain. Since this is Public Domain,
 * you don't need to worry about licensing, and you can simply copy this
 * NioServer.java file to your own package and use it as you like. Enjoy. Please
 * consider leaving the following statement here in this code:
 * </p>
 *
 * <p>
 * <em>This <tt>NioServer</tt> class was copied to this project from its source
 * as found at
 * <a href="http://iharder.net" target="_blank">iHarder.net</a>.</em>
 * </p>
 *
 * @author Robert Harder
 * @author rharder@users.sourceforge.net
 * @version 0.1
 * @see NioServer
 * @see Adapter
 * @see Event
 * @see Listener
 */
public class NioServer {

	/**
	 * A helper class that implements all methods of the {@link NioServer.Listener}
	 * interface with empty methods.
	 *
	 * <p>
	 * This code is released into the Public Domain. Since this is Public Domain,
	 * you don't need to worry about licensing, and you can simply copy this
	 * NioServer.java file to your own package and use it as you like. Enjoy. Please
	 * consider leaving the following statement here in this code:
	 * </p>
	 *
	 * <p>
	 * <em>This <tt>NioServer</tt> class was copied to this project from its source
	 * as found at
	 * <a href="http://iharder.net" target="_blank">iHarder.net</a>.</em>
	 * </p>
	 *
	 * @author Robert Harder
	 * @author rharder@users.sourceforge.net
	 * @version 0.1
	 * @see NioServer
	 * @see Listener
	 * @see Event
	 */
	public class Adapter implements Listener {

		/**
		 * Empty method.
		 *
		 * @see Listener
		 * @param evt the shared event
		 */
		@Override
		public void nioServerConnectionClosed(final NioServer.Event evt) {
		}

		/**
		 * Empty method.
		 *
		 * @see Listener
		 * @param evt the shared event
		 */
		@Override
		public void nioServerNewConnectionReceived(final NioServer.Event evt) {
		}

		/**
		 * Empty method.
		 *
		 * @see Listener
		 * @param evt the shared event
		 */
		@Override
		public void nioServerTcpDataReceived(final NioServer.Event evt) {
		}

		/**
		 * Empty method.
		 *
		 * @see Listener
		 * @param evt the shared event
		 */
		@Override
		public void nioServerUdpDataReceived(final NioServer.Event evt) {
		}

	} // end static inner class Adapter

	/**
	 * An event representing activity by a {@link NioServer}.
	 *
	 * <p>
	 * This code is released into the Public Domain. Since this is Public Domain,
	 * you don't need to worry about licensing, and you can simply copy this
	 * NioServer.java file to your own package and use it as you like. Enjoy. Please
	 * consider leaving the following statement here in this code:
	 * </p>
	 *
	 * <p>
	 * <em>This <tt>NioServer</tt> class was copied to this project from its source
	 * as found at
	 * <a href="http://iharder.net" target="_blank">iHarder.net</a>.</em>
	 * </p>
	 *
	 * @author Robert Harder
	 * @author rharder@users.sourceforge.net
	 * @version 0.1
	 * @see NioServer
	 * @see Adapter
	 * @see Listener
	 */
	public static class Event extends java.util.EventObject {

		private final static long serialVersionUID = 1;

		/**
		 * The key associated with this (reusable) event. Use setKey(..) to change the
		 * key between firings.
		 */
		private SelectionKey key;

		/**
		 * The buffer that holds the data, for some events.
		 */
		private ByteBuffer buffer;

		/**
		 * The source address for incoming UDP datagrams. The
		 * {@link #getRemoteSocketAddress} method will return this value if data is from
		 * UDP.
		 */
		private SocketAddress remoteUdp;

		/**
		 * Creates a Event based on the given {@link NioServer}.
		 *
		 * @param src the source of the event
		 */
		public Event(final NioServer src) {
			super(src);
		}

		/**
		 * <p>
		 * Returns the {@link java.nio.ByteBuffer} that contains the data for this
		 * connection. Read from it as much as you can. Any data that remains on or
		 * after the value of <code>position()</code> will be saved for the next time an
		 * event is fired. In this way, you can defer processing incomplete data until
		 * everything arrives.
		 * </p>
		 *
		 * <p>
		 * Example: You are receiving lines of text. The ByteBuffer returned here
		 * contains one and a half lines of text. When you realize this, you process the
		 * first line as you like, but you leave this buffer's position at the beginning
		 * of the second line. In this way, The beginning of the second line will be the
		 * start of the buffer the next time around.
		 * </p>
		 *
		 * @return buffer with the data
		 */
		public ByteBuffer getBuffer() {
			return this.buffer;
		}

		/**
		 * Returns the SelectionKey associated with this event.
		 *
		 * @return the SelectionKey
		 */
		public SelectionKey getKey() {
			return this.key;
		}

		/**
		 * <p>
		 * Returns the local address/port to which this connection is bound. That is, if
		 * you are listening on port 80, then this might return something like an
		 * InetSocketAddress (probably) that indicated /127.0.0.1:80.
		 * </p>
		 * <p>
		 * This is essentially a convenience method for returning the same-name methods
		 * from the key's channel after checking the type of channel (SocketChannel or
		 * DatagramChannel).
		 * </p>
		 *
		 * @return local address that server is bound to for this connection
		 */
		public SocketAddress getLocalSocketAddress() {
			SocketAddress addr = null;
			if (this.key != null) {
				final SelectableChannel sc = this.key.channel();
				if (sc instanceof SocketChannel) {
					addr = ((SocketChannel) sc).socket().getLocalSocketAddress();
				} else if (sc instanceof DatagramChannel) {
					addr = ((DatagramChannel) sc).socket().getLocalSocketAddress();
				}
			}
			return addr;
		}

		/**
		 * Returns the source of the event, a {@link NioServer}. Shorthand for
		 * <tt>(NioServer)getSource()</tt>.
		 *
		 * @return the server
		 */
		public NioServer getNioServer() {
			return (NioServer) getSource();
		}

		/**
		 * <p>
		 * Returns the address of the endpoint this socket is connected to, or null if
		 * it is unconnected.
		 * </p>
		 * <p>
		 * This is essentially a convenience method for returning the same-name methods
		 * from the key's channel after checking the type of channel (SocketChannel or
		 * DatagramChannel).
		 * </p>
		 *
		 * @return remote address from which connection came
		 */
		public SocketAddress getRemoteSocketAddress() {
			SocketAddress addr = null;
			if (this.key != null) {
				final SelectableChannel sc = this.key.channel();
				if (sc instanceof SocketChannel) {
					addr = ((SocketChannel) sc).socket().getRemoteSocketAddress();
				} else if (sc instanceof DatagramChannel) {
					addr = this.remoteUdp;
				}
			}
			return addr;
		}

		/**
		 * Shorthand for <tt>getNioServer().getState()</tt>.
		 *
		 * @return the state of the server
		 * @see NioServer.State
		 */
		public NioServer.State getState() {
			return getNioServer().getState();
		}

		/**
		 * Convenience method for checking
		 * <code>getKey().channel() instanceof SocketChannel</code>.
		 *
		 * @return true if a TCP connection
		 */
		public boolean isTcp() {
			return this.key == null ? false : this.key.channel() instanceof SocketChannel;
		}

		/**
		 * Convenience method for checking
		 * <code>getKey().channel() instanceof DatagramChannel</code>.
		 *
		 * @return true if a UDP connection
		 */
		public boolean isUdp() {
			return this.key == null ? false : this.key.channel() instanceof DatagramChannel;
		}

		/**
		 * Resets an event between firings by updating the parameters that change.
		 *
		 * @param key       The SelectionKey for the event
		 * @param buffer
		 * @param remoteUdp the remote UDP source or null for TCP
		 */
		protected void reset(final SelectionKey key, final ByteBuffer buffer, final SocketAddress remoteUdp) {
			this.key = key;
			this.buffer = buffer;
			this.remoteUdp = remoteUdp;
		}

	} // end static inner class Event

	/**
	 * An interface for listening to events from a {@link NioServer}. A single
	 * {@link Event} is shared for all invocations of these methods.
	 *
	 * <p>
	 * This code is released into the Public Domain. Since this is Public Domain,
	 * you don't need to worry about licensing, and you can simply copy this
	 * NioServer.java file to your own package and use it as you like. Enjoy. Please
	 * consider leaving the following statement here in this code:
	 * </p>
	 *
	 * <p>
	 * <em>This <tt>NioServer</tt> class was copied to this project from its source
	 * as found at
	 * <a href="http://iharder.net" target="_blank">iHarder.net</a>.</em>
	 * </p>
	 *
	 * @author Robert Harder
	 * @author rharder@users.sourceforge.net
	 * @version 0.1
	 * @see NioServer
	 * @see Adapter
	 * @see Event
	 */
	public static interface Listener extends java.util.EventListener {

		/**
		 * Called when a connection is closed remotely. If you close the connection
		 * somewhere in your own code, this event probably won't be fired.
		 *
		 * @param evt the shared event
		 */
		public abstract void nioServerConnectionClosed(NioServer.Event evt);

		/**
		 * <p>
		 * Called when a new connection is received. The SelectionKey associated with
		 * the event (an <code>OP_READ</code> key), is the key that will be used with
		 * the data received event. In this way, you can seed a <code>Map</code> or
		 * other data structure and associate this very key with the connection. You
		 * will only get new connection events for TCP connections (not UDP).
		 * </p>
		 *
		 * <p>
		 * The key's attachment mechanism is unused by NioServer and is available for
		 * you to store whatever you like.
		 * </p>
		 *
		 * <p>
		 * If your protocol requires the server to respond to a client upon connection,
		 * this sample code demonstrates such an arrangement:
		 * </p>
		 *
		 * <pre>
		 * public void nioServerNewConnectionReceived(NioServer.Event evt) {
		 * 	SocketChannel ch = (SocketChannel) evt.getKey().channel();
		 * 	try {
		 * 		ch.write(ByteBuffer.wrap("Greetings\r\n".getBytes()));
		 * 	} catch (IOException ex) {
		 * 		ex.printStackTrace(); // Please don't do printStackTrace in production code
		 * 	}
		 * }
		 * </pre>
		 *
		 * @param evt the shared event
		 */
		public abstract void nioServerNewConnectionReceived(NioServer.Event evt);

		/**
		 * <p>
		 * Called when TCP data is received. Retrieve the associated ByteBuffer with
		 * <code>evt.getBuffer()</code>. This is the source ByteBuffer used by the
		 * server directly to receive the data. It is a "direct" ByteBuffer (created
		 * with <code>ByteBuffer.allocateDirect(..)</code>). Read from it as much as you
		 * can. Any data that remains on or after the value of <code>position()</code>
		 * will be saved for the next time an event is fired. In this way, you can defer
		 * processing incomplete data until everything arrives. <em>Be careful that you
		 * don't leave the buffer full</em> or you won't be able to receive anything
		 * next time around (unless you call {@link #setBufferSize} to resize buffer).
		 * </p>
		 *
		 * <p>
		 * The key's attachment mechanism is unused by NioServer and is available for
		 * you to store whatever you like.
		 * </p>
		 *
		 * <p>
		 * Example: You are receiving lines of text. The ByteBuffer returned here
		 * contains one and a half lines of text. When you realize this, you process the
		 * first line as you like, but you leave this buffer's position at the beginning
		 * of the second line. In this way, The beginning of the second line will be the
		 * start of the buffer the next time around.
		 * </p>
		 *
		 * @param evt the shared event
		 */
		public abstract void nioServerTcpDataReceived(NioServer.Event evt);

		/**
		 * <p>
		 * Called when UDP data is received. Retrieve the associated ByteBuffer with
		 * <code>evt.getBuffer()</code>. This is the source ByteBuffer used by the
		 * server directly to receive the data. It is a "direct" ByteBuffer (created
		 * with <code>ByteBuffer.allocateDirect(..)</code>). The contents of the
		 * ByteBuffer will be the entire contents received from the UDP datagram.
		 * </p>
		 *
		 * @param evt the shared event
		 */
		public abstract void nioServerUdpDataReceived(NioServer.Event evt);

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

	/** Standard Java logger. */
	private final static Logger LOGGER = Logger.getLogger(NioServer.class.getName());;
	/** The buffer size property. */
	public final static String BUFFER_SIZE_PROP = "bufferSize";
	private final static int BUFFER_SIZE_DEFAULT = 4096;

	public final static String STATE_PROP = "state";
	public final static String LAST_EXCEPTION_PROP = "lastException";

	public final static String TCP_BINDINGS_PROP = "tcpBindings";
	public final static String UDP_BINDINGS_PROP = "udpBindings";
	public final static String SINGLE_TCP_PORT_PROP = "singleTcpPort";
	public final static String SINGLE_UDP_PORT_PROP = "singleUdpPort";

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
	 * <code>NioServer.setLoggingLevel(Level.OFF);</code>.
	 *
	 * @param level the new logging level
	 */
	public static void setLoggingLevel(final Level level) {
		LOGGER.setLevel(level);
	}

	private int bufferSize = BUFFER_SIZE_DEFAULT;

	private State currentState = State.STOPPED;
	private Throwable lastException;
	private final Collection<NioServer.Listener> listeners = new LinkedList<NioServer.Listener>(); // Event listeners
	private NioServer.Listener[] cachedListeners = null;

	private final NioServer.Event event = new NioServer.Event(this); // Shared event
	private final PropertyChangeSupport propSupport = new PropertyChangeSupport(this); // Properties

	private ThreadFactory threadFactory; // Optional thread factory

	private Thread ioThread; // Performs IO
	private Selector selector; // Brokers all the connections

	private final Map<SocketAddress, SelectionKey> tcpBindings = new HashMap<SocketAddress, SelectionKey>();// Requested
																											// TCP
																											// bindings,
																											// e.g.,
																											// "listen
																											// on port
																											// 80"
	private final Map<SocketAddress, SelectionKey> udpBindings = new HashMap<SocketAddress, SelectionKey>();// Requested
																											// UDP
																											// bindings

	private final Map<SocketAddress, String> multicastGroups = new HashMap<SocketAddress, String>();

	/* ******** C O N S T R U C T O R S ******** */

	private final Set<SocketAddress> pendingTcpAdds = new HashSet<SocketAddress>(); // TCP bindings to add to selector
																					// on next cycle

	private final Set<SocketAddress> pendingUdpAdds = new HashSet<SocketAddress>(); // UDP bindings to add to selector
																					// on next cycle

	/* ******** R U N N I N G ******** */

	private final Map<SocketAddress, SelectionKey> pendingTcpRemoves = new HashMap<SocketAddress, SelectionKey>(); // TCP
																													// bindings
																													// to
																													// remove
																													// from
																													// selector
																													// on
																													// next
																													// cycle

	private final Map<SocketAddress, SelectionKey> pendingUdpRemoves = new HashMap<SocketAddress, SelectionKey>(); // UDP
																													// bindings
																													// to
																													// remove
																													// from
																													// selector
																													// on
																													// next
																													// cycle

	private final Map<SelectionKey, ByteBuffer> leftoverData = new HashMap<SelectionKey, ByteBuffer>(); // Store
																										// leftovers
																										// here instead
																										// of
																										// key.attachment()

	/**
	 * Constructs a new NioServer, listening to nothing, and not started.
	 */
	public NioServer() {
	}

	/**
	 * Constructs a new NioServer, listening to nothing, and not started. The
	 * provided ThreadFactory will be used when starting and running the server.
	 *
	 * @param factory the ThreadFactory to use when starting the server
	 */
	public NioServer(final ThreadFactory factory) {
		this.threadFactory = factory;
	}

	/**
	 * Adds a {@link Listener}.
	 *
	 * @param l the listener
	 */
	public synchronized void addNioServerListener(final NioServer.Listener l) {
		listeners.add(l);
		cachedListeners = null;
	}

	/**
	 * Add a property listener.
	 *
	 * @param listener the listener
	 */
	public synchronized void addPropertyChangeListener(final PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Add a property listener for the named property.
	 *
	 * @param property the property name
	 * @param listener the listener
	 */
	public synchronized void addPropertyChangeListener(final String property, final PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(property, listener);
	}

	/* ******** B U F F E R S I Z E ******** */

	/**
	 * Adds a TCP binding to the server. Effectively this is how you set which ports
	 * and on which interfaces you want the server to listen. In the simplest case,
	 * you might do the following to listen generically on port 80:
	 * <code>addTcpBinding( new InetAddress(80) );</code>. The server can listen on
	 * multiple ports at once.
	 *
	 * @param addr The address on which to listen
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer addTcpBinding(final SocketAddress addr) {
		final Set<SocketAddress> oldVal = this.getTcpBindings(); // Save old set for prop change event
		this.tcpBindings.put(addr, null); // Add binding
		final Set<SocketAddress> newVal = this.getTcpBindings(); // Save new set for prop change event
		this.pendingTcpAdds.add(addr); // Prepare pending add action
		this.pendingTcpRemoves.remove(addr); // In case it's also pending a remove

		if (this.selector != null) { // If there's a selector...
			this.selector.wakeup(); // Wake it up to handle the add action
		}
		firePropertyChange(TCP_BINDINGS_PROP, oldVal, newVal); // Fire prop change
		return this;
	}

	/**
	 * Adds a UDP binding to the server. Effectively this is how you set which ports
	 * and on which interfaces you want the server to listen. In the simplest case,
	 * you might do the following to listen generically on port 6997:
	 * <code>addUdpBinding( new InetAddress(6997) );</code>. The server can listen
	 * on multiple ports at once.
	 *
	 * @param addr The address on which to listen
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer addUdpBinding(final SocketAddress addr) {
		return addUdpBinding(addr, null);
	}

	/* ******** T C P B I N D I N G S ******** */

	/**
	 * <p>
	 * <em>Experimental Hack</em> - Adds a UDP binding to the server and joins the
	 * given multicast group (if <code>group<code>
	 * is not null and is a valid multicast group).
	 * In the simplest case, you might do the following
	 * to listen on port 16000 and multicast group 239.0.0.1:
	 * <code>addUdpBinding( new InetAddress(16000), "239.0.0.1" );</code>. The
	 * server can listen on multiple ports at once.
	 * </p>
	 *
	 * <p>
	 * As of Java 6, the <code>java.nio</code> "New IO" packages don't support
	 * multicast groups ("annoyed grunt"), however I found a clever hack at this
	 * gentleman's website (<a href=
	 * "http://www.mernst.org/blog/archives/12-01-2006_12-31-2006.html">http://www.mernst.org/blog/archives/12-01-2006_12-31-2006.html</a>)
	 * that makes multicast work -- for now.
	 * </p>
	 *
	 * @param addr  The address on which to listen
	 * @param group The multicast group to join
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer addUdpBinding(final SocketAddress addr, final String group) {
		final Map<SocketAddress, String> oldVal = this.getUdpBindings();
		this.udpBindings.put(addr, null);
		this.pendingUdpAdds.add(addr);
		this.pendingUdpRemoves.remove(addr);
		if (group != null) {
			this.multicastGroups.put(addr, group);
		} // end if: multicast too
		final Map<SocketAddress, String> newVal = this.getUdpBindings();
		if (this.selector != null) {
			this.selector.wakeup();
		}
		firePropertyChange(UDP_BINDINGS_PROP, oldVal, newVal);
		return this;
	}

	/**
	 * Clears all TCP bindings.
	 *
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer clearTcpBindings() {
		for (final SocketAddress addr : getTcpBindings()) {
			removeTcpBinding(addr);
		}
		return this;
	}

	/**
	 * Clears all UDP bindings.
	 *
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer clearUdpBindings() {
		for (final SocketAddress addr : getUdpBindings().keySet()) {
			removeUdpBinding(addr);
		}
		return this;
	}

	/**
	 * Fire when a connection is closed remotely.
	 *
	 * @param key The key for the closed connection.
	 */
	protected synchronized void fireConnectionClosed(final SelectionKey key) {

		if (cachedListeners == null) {
			cachedListeners = listeners.toArray(new NioServer.Listener[listeners.size()]);
		}
		this.event.reset(key, null, null);

		// Make a Runnable object to execute the calls to listeners.
		// In the event we don't have an Executor, this results in
		// an unnecessary object instantiation, but it also makes
		// the code more maintainable.
		for (final NioServer.Listener l : cachedListeners) {
			try {
				l.nioServerConnectionClosed(event);
			} catch (final Exception exc) {
				LOGGER.warning("NioServer.Listener " + l + " threw an exception: " + exc.getMessage());
				fireExceptionNotification(exc);
			} // end catch
		} // end for: each listener
	} // end fireNioServerPacketReceived

	/**
	 * Fires a property change event with the new exception.
	 *
	 * @param t
	 */
	protected void fireExceptionNotification(final Throwable t) {
		final Throwable oldVal = this.lastException;
		this.lastException = t;
		firePropertyChange(LAST_EXCEPTION_PROP, oldVal, t);
	}

	/* ******** U D P B I N D I N G S ******** */

	/**
	 * Fire when a new connection is established.
	 *
	 * @param key the SelectionKey associated with the connection
	 */
	protected synchronized void fireNewConnection(final SelectionKey key) {

		if (cachedListeners == null) {
			cachedListeners = listeners.toArray(new NioServer.Listener[listeners.size()]);
		}
		this.event.reset(key, null, null);

		// Make a Runnable object to execute the calls to listeners.
		// In the event we don't have an Executor, this results in
		// an unnecessary object instantiation, but it also makes
		// the code more maintainable.
		for (final NioServer.Listener l : cachedListeners) {
			try {
				l.nioServerNewConnectionReceived(event);
			} catch (final Exception exc) {
				LOGGER.warning("NioServer.Listener " + l + " threw an exception: " + exc.getMessage());
				fireExceptionNotification(exc);
			} // end catch
		} // end for: each listener
	} // end fireNioServerPacketReceived

	/**
	 * Fires property chagne events for all current values setting the old value to
	 * null and new value to the current.
	 */
	public synchronized void fireProperties() {
		firePropertyChange(STATE_PROP, null, getState());
		firePropertyChange(BUFFER_SIZE_PROP, null, getBufferSize());
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
			fireExceptionNotification(exc);
		} // end catch
	} // end fire

	/**
	 * Fire when data is received.
	 *
	 * @param key    the SelectionKey associated with the data
	 * @param buffer the buffer containing the new (and possibly leftover) data
	 */
	protected synchronized void fireTcpDataReceived(final SelectionKey key, final ByteBuffer buffer) {

		if (cachedListeners == null) {
			cachedListeners = listeners.toArray(new NioServer.Listener[listeners.size()]);
		}
		this.event.reset(key, buffer, null);

		// Make a Runnable object to execute the calls to listeners.
		// In the event we don't have an Executor, this results in
		// an unnecessary object instantiation, but it also makes
		// the code more maintainable.
		for (final NioServer.Listener l : cachedListeners) {
			try {
				l.nioServerTcpDataReceived(event);
			} catch (final Exception exc) {
				LOGGER.warning("NioServer.Listener " + l + " threw an exception: " + exc.getMessage());
				fireExceptionNotification(exc);
			} // end catch
		} // end for: each listener
	}

	/**
	 * Fire when data is received.
	 *
	 * @param key    the SelectionKey associated with the data
	 * @param buffer the buffer containing the data
	 * @param remote the source address of the datagram or null if not available
	 */
	protected synchronized void fireUdpDataReceived(final SelectionKey key, final ByteBuffer buffer,
			final SocketAddress remote) {

		if (cachedListeners == null) {
			cachedListeners = listeners.toArray(new NioServer.Listener[listeners.size()]);
		}
		this.event.reset(key, buffer, remote);

		// Make a Runnable object to execute the calls to listeners.
		// In the event we don't have an Executor, this results in
		// an unnecessary object instantiation, but it also makes
		// the code more maintainable.
		for (final NioServer.Listener l : cachedListeners) {
			try {
				l.nioServerUdpDataReceived(event);
			} catch (final Exception exc) {
				LOGGER.warning("NioServer.Listener " + l + " threw an exception: " + exc.getMessage());
				fireExceptionNotification(exc);
			} // end catch
		} // end for: each listener
	} // end fireNioServerPacketReceived

	/**
	 * Returns the size of the ByteBuffer used to read from the connections. This
	 * refers to the buffer that will be passed along with {@link Event} objects as
	 * data is received and so forth.
	 *
	 * @return The size of the ByteBuffer
	 */
	public synchronized int getBufferSize() {
		return this.bufferSize;
	}

	/* ******** S I N G L E P O R T ******** */

	/**
	 * Returns the last exception (Throwable, actually) that the server encountered.
	 *
	 * @return last exception
	 */
	public synchronized Throwable getLastException() {
		return this.lastException;
	}

	/**
	 * Returns the port for the single TCP binding in effect, or -1 (minus one) if
	 * there are no or multiple TCP bindings or some other error.
	 *
	 * @return TCP listening port or -1
	 */
	public synchronized int getSingleTcpPort() {
		int port = -1;
		final Set<SocketAddress> bindings = getTcpBindings();
		if (bindings.size() == 1) {
			final SocketAddress sa = bindings.iterator().next();
			if (sa instanceof InetSocketAddress) {
				port = ((InetSocketAddress) sa).getPort();
			} // end if: inet
		} // end if: only one binding
		return port;
	}

	/**
	 * Returns the port for the single UDP binding in effect, or -1 (minus one) if
	 * there are no or multiple UDP bindings or some other error.
	 *
	 * @return UDP listening port or -1
	 */
	public synchronized int getSingleUdpPort() {
		int port = -1;
		final Map<SocketAddress, String> bindings = getUdpBindings();
		if (bindings.size() == 1) {
			final SocketAddress sa = bindings.keySet().iterator().next();
			if (sa instanceof InetSocketAddress) {
				port = ((InetSocketAddress) sa).getPort();
			} // end if: inet
		} // end if: only one binding
		return port;
	}

	/**
	 * Returns the current state of the server, one of STOPPED, STARTING, or
	 * STARTED.
	 *
	 * @return state of the server
	 */
	public synchronized State getState() {
		return this.currentState;
	}

	/**
	 * Returns a set of socket addresses that the server is (or will be when
	 * started) bound to/listening on. This set is not backed by the actual data
	 * structures. Changes to this returned set have no effect on the server.
	 *
	 * @return set of tcp listening points
	 */
	public synchronized Set<SocketAddress> getTcpBindings() {
		final Set<SocketAddress> bindings = new HashSet<SocketAddress>();
		bindings.addAll(this.tcpBindings.keySet());
		return bindings;
	}

	/* ******** E V E N T S ******** */

	/**
	 * Returns a map of socket addresses and multicast groups that the server is (or
	 * will be when started) bound to/listening on. This set is not backed by the
	 * actual data structures. Changes to this returned set have no effect on the
	 * server. The map's value portion will be null if not multicast group is joined
	 * for that port or it may have a String which would be the requested multicast
	 * group.
	 *
	 * @return map of udp listening points
	 */
	public synchronized Map<SocketAddress, String> getUdpBindings() {
		final Map<SocketAddress, String> bindings = new HashMap<SocketAddress, String>();
		for (final SocketAddress addr : this.udpBindings.keySet()) {
			bindings.put(addr, this.multicastGroups.get(addr));
		} // end for: each address
		return bindings;
	}

	/**
	 * Handles accepting new connections.
	 *
	 * @param key The OP_ACCEPT key
	 * @throws IOException
	 */
	private void handleAccept(final SelectionKey key) throws IOException {
		assert key.isAcceptable() : key.readyOps(); // We know it should be acceptable
		assert selector.isOpen(); // Not sure this matters. Meh.

		final SelectableChannel sc = key.channel(); // Channel for th key
		assert sc instanceof ServerSocketChannel : sc; // Only our TCP connections have OP_ACCEPT
//        if( sc instanceof ServerSocketChannel ){

		final ServerSocketChannel ch = (ServerSocketChannel) key.channel(); // Server channel
		SocketChannel incoming = null; // Reusable for all pending connections
		while ((incoming = ch.accept()) != null) { // Iterate over all pending connections
			incoming.configureBlocking(false); // Non-blocking IO
			final SelectionKey incomingReadKey = incoming.register( // Register new connection
					this.selector, // With the Selector
					SelectionKey.OP_READ);// , // Want to READ data
			// (ByteBuffer)ByteBuffer.allocateDirect(128).flip() ); // Will store leftover
			// data between read ops

			fireNewConnection(incomingReadKey);

			if (LOGGER.isLoggable(Level.FINEST)) {
				LOGGER.finest("  " + incoming + ", key: " + incomingReadKey);
			}
		} // end while: each incoming connection

	}

	/**
	 * Handles reading incoming data and then firing events.
	 *
	 * @param key  The key associated with the reading
	 * @param buff the ByteBuffer to hold the data
	 * @throws IOException
	 */
	private void handleRead(final SelectionKey key, final ByteBuffer buff) throws IOException {

		final SelectableChannel sc = key.channel();
		buff.clear(); // Clear input buffer

		// TCP
		if (sc instanceof SocketChannel) {

			final SocketChannel client = (SocketChannel) key.channel(); // Source socket
			ByteBuffer leftover = this.leftoverData.get(key); // Leftover data from last read

			if (leftover != null && leftover.remaining() > 0) { // Have a leftover buffer
				buff.put(leftover); // Preload leftovers
			} // end if: have leftover buffer

			// Read into the buffer here
			// If End of Stream
			if (client.read(buff) == -1) { // End of stream?
				key.cancel(); // Cancel the key
				client.close(); // And cancel the client
				fireConnectionClosed(key); // Fire event for connection closed
				this.leftoverData.remove(key); // Remove any leftover data
				if (LOGGER.isLoggable(Level.FINER)) {
					LOGGER.finer("Connection closed: " + key);
				}

			} else {
				// Not End of Stream
				buff.flip(); // Flip the buffer to prepare to read
				fireTcpDataReceived(key, buff); // Fire event for new data

				if (buff.remaining() > 0) { // Did the user leave data for next time?
					if (leftover == null || // Leftover buffer not yet created?
							buff.remaining() > leftover.capacity()) { // Or is too small?
						leftover = ByteBuffer.allocateDirect(buff.remaining()); // Create/resize
					} // end if: need to resize
					leftover.clear(); // Clear old leftovers
					leftover.put(buff).flip(); // Save new leftovers
				} // end if: has remaining bytes
				this.leftoverData.put(key, leftover); // Save leftovers for next time
			} // end else: read
		} // end if: SocketChannel

		// Datagram
		else if (sc instanceof DatagramChannel) {
			final DatagramChannel dc = (DatagramChannel) sc; // Cast to datagram channel
			SocketAddress remote = null;
			while ((remote = dc.receive(buff)) != null) { // Loop over all pending datagrams
				buff.flip(); // Flip after reading in
				key.attach(buff); // Attach buffer to key
				fireUdpDataReceived(key, buff, remote); // Fire event
			} // end while: each pending datagram
		} // end else: UDP

	} // end handleRead

	/**
	 * Removes a {@link Listener}.
	 *
	 * @param l the listener
	 */
	public synchronized void removeNioServerListener(final NioServer.Listener l) {
		listeners.remove(l);
		cachedListeners = null;
	}

	/**
	 * Remove a property listener.
	 *
	 * @param listener the listener
	 */
	public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Remove a property listener for the named property.
	 *
	 * @param property the property name
	 * @param listener the listener
	 */
	public synchronized void removePropertyChangeListener(final String property,
			final PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(property, listener);
	}

	/* ******** P R O P E R T Y C H A N G E ******** */

	/**
	 * Removes a TCP binding. Effectively stops the server from listening to this or
	 * that port.
	 *
	 * @param addr The address to stop listening to
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer removeTcpBinding(final SocketAddress addr) {
		final Set<SocketAddress> oldVal = this.getTcpBindings(); // Save old set for prop change event
		this.pendingTcpRemoves.put(addr, this.tcpBindings.get(addr)); // Prepare pending remove action
		this.tcpBindings.remove(addr); // Remove binding
		this.pendingTcpAdds.remove(addr); // In case it's also pending an add
		final Set<SocketAddress> newVal = this.getTcpBindings(); // Save new set for prop change event

		if (this.selector != null) { // If there's a selector...
			this.selector.wakeup(); // Wake it up to handle the remove action
		}
		firePropertyChange(TCP_BINDINGS_PROP, oldVal, newVal); // Fire prop change
		return this;
	}

	/**
	 * Removes a UDP binding. Effectively stops the server from listening to this or
	 * that port.
	 *
	 * @param addr The address to stop listening to
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer removeUdpBinding(final SocketAddress addr) {
		final Map<SocketAddress, String> oldVal = this.getUdpBindings(); // Save old set for prop change event
		this.pendingUdpRemoves.put(addr, this.udpBindings.get(addr)); // Prepare pending remove action
		this.udpBindings.remove(addr); // Remove binding
		this.multicastGroups.remove(addr); // Remove multicast note
		this.pendingUdpAdds.remove(addr); // In case it's also pending an add
		final Map<SocketAddress, String> newVal = this.getUdpBindings(); // Save new set for prop change event

		if (this.selector != null) { // If there's a selector...
			this.selector.wakeup(); // Wake it up to handle the remove action
		}
		firePropertyChange(UDP_BINDINGS_PROP, oldVal, newVal); // Fire prop change
		return this;
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
			this.addPropertyChangeListener(STATE_PROP, new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					final State newState = (State) evt.getNewValue();
					if (newState == State.STOPPED) {
						final NioServer server = (NioServer) evt.getSource();
						server.removePropertyChangeListener(STATE_PROP, this);
						server.start();
					} // end if: stopped
				} // end prop change
			});
			stop();
			break;
		} // end switch
	}

	/**
	 * This method starts up and listens indefinitely for TCP packets. On entering
	 * this method, the state is assumed to be STARTING. Upon exiting this method,
	 * the state will be STOPPING.
	 */
	protected void runServer() {
		try {
			ByteBuffer buff = ByteBuffer.allocateDirect(this.bufferSize);

			// Add all the requested TCP and UDP bindings to the "pending" lists
			synchronized (this) {
				this.selector = Selector.open();
				this.pendingTcpAdds.clear();
				this.pendingTcpAdds.addAll(this.tcpBindings.keySet());
				this.pendingUdpAdds.clear();
				this.pendingUdpAdds.addAll(this.udpBindings.keySet());
				this.pendingTcpRemoves.clear();
				this.pendingTcpRemoves.clear();
			}

			setState(State.STARTED); // Mark as started
			while (this.selector.isOpen()) {

				// Check to see if the server is supposed to be stopping
				synchronized (this) {
					if (this.currentState == State.STOPPING) {
						LOGGER.finer("Stopping server by request.");
						this.selector.close();
						continue;
					} // end if: stopping
				} // end sync

				// Add and remove pending bindings
				synchronized (this) {

					// Pending TCP Adds
					for (final SocketAddress addr : this.pendingTcpAdds) { // For each add
						LOGGER.fine("Binding TCP: " + addr);
						final ServerSocketChannel sc = ServerSocketChannel.open(); // Open a channel
						sc.socket().bind(addr); // Bind as requested
						sc.configureBlocking(false); // Make non-blocking
						final SelectionKey acceptKey = sc.register( // Register with master Selector
								this.selector, SelectionKey.OP_ACCEPT); // We want to "accept" connections
						this.tcpBindings.put(addr, acceptKey); // Save the key
					} // end for: each address
					this.pendingTcpAdds.clear(); // Remove list of pending adds

					// Pending UDP Adds
					for (final SocketAddress addr : this.pendingUdpAdds) { // Same comments as for TCP
						LOGGER.fine("Binding UDP: " + addr);
						final DatagramChannel dc = DatagramChannel.open();
						dc.socket().bind(addr);
						dc.configureBlocking(false);
						final SelectionKey acceptKey = dc.register(this.selector, SelectionKey.OP_READ);
						this.udpBindings.put(addr, acceptKey);

						// Found a weird hack to support multicast -- at least for now.
						final String group = this.multicastGroups.get(addr);
						if (group != null && addr instanceof InetSocketAddress) {
							final int port = ((InetSocketAddress) addr).getPort();
							final InetSocketAddress groupAddr = new InetSocketAddress(group, port);
							if (groupAddr.getAddress().isMulticastAddress()) {

								// http://www.mernst.org/blog/archives/12-01-2006_12-31-2006.html
								// UGLY UGLY HACK: multicast support for NIO
								// create a temporary instanceof PlainDatagramSocket, set its fd and configure
								// it
								@SuppressWarnings("unchecked")
								final Constructor<? extends DatagramSocketImpl> c = (Constructor<? extends DatagramSocketImpl>) Class
										.forName("java.net.PlainDatagramSocketImpl").getDeclaredConstructor();
								c.setAccessible(true);
								final DatagramSocketImpl socketImpl = c.newInstance();

								final Field channelFd = Class.forName("sun.nio.ch.DatagramChannelImpl")
										.getDeclaredField("fd");
								channelFd.setAccessible(true);

								final Field socketFd = DatagramSocketImpl.class.getDeclaredField("fd");
								socketFd.setAccessible(true);
								socketFd.set(socketImpl, channelFd.get(dc));

								try {
									final Method m = DatagramSocketImpl.class.getDeclaredMethod("joinGroup",
											SocketAddress.class, NetworkInterface.class);
									// Method m = DatagramSocketImpl.class.getDeclaredMethod("joinGroup",
									// InetAddress.class );
									m.setAccessible(true);
									m.invoke(socketImpl, groupAddr, null);
								} catch (final Exception e) {
									e.printStackTrace();
								} finally {
									// important, otherwise the fake socket's finalizer will nuke the fd
									socketFd.set(socketImpl, null);
								}

							} // end if: multicast
							else {
								LOGGER.warning("Could not join non-multicast group: " + group);
							}
						} // end if: got group

					} // end for: each address
					this.pendingUdpAdds.clear();

					// Pending TCP Removes
					for (final Map.Entry<SocketAddress, SelectionKey> e : this.pendingTcpRemoves.entrySet()) {
						final SelectionKey key = e.getValue(); // Get the registered key
						if (key != null) { // Might be null if someone gave us bogus address
							key.channel().close(); // Close the channel
							key.cancel(); // And cancel the key (redundant?)
						} // end if: key != null
					} // end for: each remove
					this.pendingTcpRemoves.clear(); // Remove from list of pending removes

					// Pending UDP Removes
					for (final Map.Entry<SocketAddress, SelectionKey> e : this.pendingUdpRemoves.entrySet()) {
						final SelectionKey key = e.getValue(); // Get the registered key
						if (key != null) { // Might be null if someone gave us bogus address
							key.channel().close(); // Close the channel
							key.cancel(); // And cancel the key (redundant?)
						} // end if: key != null
					} // end for: each remove
					this.pendingUdpRemoves.clear(); // Remove from list of pending removes

				} // end sync: this

				//
				// The meat of the matter begins now
				//

				//////// B L O C K S H E R E
				if (this.selector.select() <= 0) { // Block until notified
					LOGGER.finer("selector.select() <= 0"); // Possible false start
					Thread.sleep(100); // Let's not run away from ourselves
				} /////// B L O C K S H E R E

				// Possibly resize buffer if a change was requested since last cycle
				if (this.bufferSize != buff.capacity()) { // Mismatch size means someone asked for something new
					assert this.bufferSize >= 0 : this.bufferSize; // We check for this in setBufferSize(..)
					buff = ByteBuffer.allocateDirect(this.bufferSize); // Resize and use direct for OS efficiencies
				}

				final Set<SelectionKey> keys = this.selector.selectedKeys(); // These keys need attention
				if (LOGGER.isLoggable(Level.FINEST)) { // Only report this at finest grained logging level
					LOGGER.finest("Keys: " + keys); // Which keys are being examined this round
				}

				final Iterator<SelectionKey> iter = keys.iterator(); // Iterate over keys -- cannot use "for" loop since
																		// we remove keys
				while (iter.hasNext()) { // Each key
					final SelectionKey key = iter.next(); // The key
					iter.remove(); // Remove from list

					// Accept connections
					// This should only be from the TCP bindings
					if (key.isAcceptable()) { // New, incoming connection?
						handleAccept(key); // Handle accepting connections
					}

					// Data to read
					// This could be an ongoing TCP connection
					// or a new (is there any other kind) UDP datagram
					else if (key.isReadable()) { // Existing connection has data (or is closing)
						handleRead(key, buff); // Handle data
					} // end if: readable

				} // end while: keys

			} // end while: selector is open

		} catch (final Exception exc) {
			synchronized (this) {
				if (this.currentState == State.STOPPING) { // User asked to stop
					try {
						this.selector.close();
						LOGGER.info("Server closed normally.");
					} catch (final IOException exc2) {
						this.lastException = exc2;
						LOGGER.log(Level.SEVERE, "An error occurred while closing the server. "
								+ "This may have left the server in an undefined state.", exc2);
						fireExceptionNotification(exc2);
					} // end catch IOException
				} else {
					LOGGER.log(Level.WARNING, "Server closed unexpectedly: " + exc.getMessage(), exc);
				} // end else
			} // end sync
			fireExceptionNotification(exc);
		} finally {
			setState(State.STOPPING);
			if (this.selector != null) {
				try {
					this.selector.close();
					LOGGER.info("Server closed normally.");
				} catch (final IOException exc2) {
					LOGGER.log(Level.SEVERE, "An error occurred while closing the server. "
							+ "This may have left the server in an undefined state.", exc2);
					fireExceptionNotification(exc2);
				} // end catch IOException
			} // end if: not null
			this.selector = null;
		} // end finally
	}

	/**
	 * Sets the size of the ByteBuffer used to read from the connections. This
	 * refers to the buffer that will be passed along with {@link Event} objects as
	 * data is received and so forth.
	 *
	 * @param size The size of the ByteBuffer
	 */
	public synchronized void setBufferSize(final int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("New buffer size must be positive: " + size);
		} // end if: size outside range

		final int oldVal = this.bufferSize;
		this.bufferSize = size;
		this.selector.wakeup();

		firePropertyChange(BUFFER_SIZE_PROP, oldVal, size);
	}

	/**
	 * Convenience method for clearing all bindings and setting up listening for TCP
	 * on the given port.
	 *
	 * @param port the port to listen to
	 * @return <code>this</code> to aid in chaining
	 */
	public synchronized NioServer setSingleTcpPort(final int port) {
		final int oldVal = getSingleTcpPort();
		if (oldVal == port) {
			return this;
		}
		clearTcpBindings();
		addTcpBinding(new InetSocketAddress(port));
		final int newVal = port;
		firePropertyChange(SINGLE_TCP_PORT_PROP, oldVal, newVal);
		return this;
	}

	/* ******** E X C E P T I O N S ******** */

	/**
	 * Convenience method for clearing all bindings and setting up listening for UDP
	 * on the given port.
	 *
	 * @param port the port to listen to
	 * @return <code>this</code> to aid in chaining
	 */
	public synchronized NioServer setSingleUdpPort(final int port) {
		return setSingleUdpPort(port, null);
	}

	/**
	 * Convenience method for clearing all bindings and setting up listening for UDP
	 * on the given port and joining the provided multicast group.
	 *
	 * @param port  the port to listen to
	 * @param group
	 * @return <code>this</code> to aid in chaining
	 */
	public synchronized NioServer setSingleUdpPort(final int port, final String group) {
		final int oldVal = getSingleUdpPort();
		if (oldVal == port) {
			return this;
		}
		clearUdpBindings();
		addUdpBinding(new InetSocketAddress(port), group);
		final int newVal = port;
		firePropertyChange(SINGLE_UDP_PORT_PROP, oldVal, newVal);
		return this;
	}

	/* ******** L O G G I N G ******** */

	/**
	 * Sets the state and fires an event. This method does not change what the
	 * server is doing, only what is reflected by the currentState variable.
	 *
	 * @param state the new state of the server
	 */
	protected synchronized void setState(final State state) {
		final State oldVal = this.currentState;
		this.currentState = state;
		firePropertyChange(STATE_PROP, oldVal, state);
	}

	/**
	 * <p>
	 * Sets the TCP bindings that the server should use. The expression
	 * <code>setTcpBindings( getTcpBindings() )</code> should result in no change to
	 * the server.
	 * </p>
	 *
	 * @param newSet
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer setTcpBindings(final Set<SocketAddress> newSet) {
		final Set<SocketAddress> toAdd = new HashSet<SocketAddress>();
		final Set<SocketAddress> toRemove = new HashSet<SocketAddress>();

		toRemove.addAll(getTcpBindings());
		for (final SocketAddress addr : newSet) {
			if (toRemove.contains(addr)) {
				toRemove.remove(addr);
			} else {
				toAdd.add(addr);
			}
		} // end for: each new addr

		for (final SocketAddress addr : toRemove) {
			removeTcpBinding(addr);
		} // end for: each new addr

		for (final SocketAddress addr : toAdd) {
			addTcpBinding(addr);
		} // end for: each new addr

		return this;
	}

	/* ******** ******** */
	/* ******** ******** */
	/* ******** S T A T I C I N N E R C L A S S L I S T E N E R ******** */
	/* ******** ******** */
	/* ******** ******** */

	/**
	 * <p>
	 * Sets the UDP bindings that the server should use. The expression
	 * <code>setTcpBindings( getTcpBindings() )</code> should result in no change to
	 * the server.
	 * </p>
	 *
	 * <p>
	 * The map consists of socket addresses (probably InetSocketAddress) and
	 * multicast addresses (the String value).
	 * </p>
	 *
	 * @param newMap
	 * @return "this" to aid in chaining commands
	 */
	public synchronized NioServer setUdpBindings(final Map<SocketAddress, String> newMap) {
		final Map<SocketAddress, String> toAdd = new HashMap<SocketAddress, String>();
		final Map<SocketAddress, String> toRemove = new HashMap<SocketAddress, String>();

		toRemove.putAll(getUdpBindings());
		for (final Map.Entry<SocketAddress, String> e : newMap.entrySet()) {
			final SocketAddress addr = e.getKey();
			final String group = e.getValue();
			if (toRemove.containsKey(addr)) {
				toRemove.remove(addr);
			} else {
				toAdd.put(addr, group);
			}
		} // end for: each new addr

		for (final Map.Entry<SocketAddress, String> e : toRemove.entrySet()) {
			removeUdpBinding(e.getKey());
		} // end for: each new addr

		for (final Map.Entry<SocketAddress, String> e : toAdd.entrySet()) {
			addUdpBinding(e.getKey(), e.getValue());
		} // end for: each new addr

		return this;
	}

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
		if (this.currentState == State.STOPPED) { // Only if we're stopped now
			assert ioThread == null : ioThread; // Shouldn't have a thread

			final Runnable run = new Runnable() {
				@Override
				public void run() {
					runServer(); // This runs for a long time
					ioThread = null;
					setState(State.STOPPED); // Clear thread
				} // end run
			}; // end runnable

			if (this.threadFactory != null) { // User-specified threads
				this.ioThread = this.threadFactory.newThread(run);

			} else { // Our own threads
				this.ioThread = new Thread(run, this.getClass().getName()); // Named
			}

			setState(State.STARTING); // Update state
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
		if (this.currentState == State.STARTED || this.currentState == State.STARTING) { // Only if already STARTED
			setState(State.STOPPING); // Mark as STOPPING
			if (this.selector != null) {
				try {
					final Set<SelectionKey> keys = this.selector.keys();
					for (final SelectionKey key : this.selector.keys()) {
						key.channel().close();
						key.cancel();
					}
					this.selector.close();
				} catch (final IOException exc) {
					fireExceptionNotification(exc);
					LOGGER.log(Level.SEVERE, "An error occurred while closing the server. "
							+ "This may have left the server in an undefined state.", exc);
				}
			} // end if: not null
		} // end if: already STARTED
	} // end stop

} // end class NioServer
