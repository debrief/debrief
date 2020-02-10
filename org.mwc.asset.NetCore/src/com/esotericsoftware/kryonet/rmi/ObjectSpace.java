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

package com.esotericsoftware.kryonet.rmi;

import static com.esotericsoftware.minlog.Log.DEBUG;
import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.WARN;
import static com.esotericsoftware.minlog.Log.debug;
import static com.esotericsoftware.minlog.Log.trace;
import static com.esotericsoftware.minlog.Log.warn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.esotericsoftware.kryo.CustomSerialization;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.SerializationException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serialize.FieldSerializer;
import com.esotericsoftware.kryo.serialize.IntSerializer;
import com.esotericsoftware.kryo.util.IntHashMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

/**
 * Allows methods on objects to be invoked remotely over TCP. Objects are
 * {@link #register(int, Object) registered} with an ID. The remote end of
 * connections that have been {@link #addConnection(Connection) added} are
 * allowed to {@link #getRemoteObject(Connection, int, Class) access} registered
 * objects.
 * <p>
 * It costs at least 2 bytes more to use remote method invocation than just
 * sending the parameters. If the method has a return value which is not
 * {@link RemoteObject#setNonBlocking(boolean, boolean) ignored}, an extra byte
 * is written. If the type of a parameter is not final (note primitives are
 * final) then an extra byte is written for that parameter.
 *
 * @author Nathan Sweet <misc@n4te.com>
 */
public class ObjectSpace {
	static class CachedMethod {
		Method method;
		Serializer[] serializers;
	}

	/**
	 * Internal message to invoke methods remotely.
	 */
	static public class InvokeMethod implements FrameworkMessage, CustomSerialization {
		public int objectID;
		public Method method;
		public Object[] args;
		public byte responseID;

		@Override
		public void readObjectData(final Kryo kryo, final ByteBuffer buffer) {
			objectID = IntSerializer.get(buffer, true);

			final int methodClassID = IntSerializer.get(buffer, true);
			final Class<?> methodClass = kryo.getRegisteredClass(methodClassID).getType();
			final byte methodIndex = buffer.get();
			CachedMethod cachedMethod;
			try {
				cachedMethod = getMethods(kryo, methodClass)[methodIndex];
			} catch (final IndexOutOfBoundsException ex) {
				throw new SerializationException(
						"Invalid method index " + methodIndex + " for class: " + methodClass.getName());
			}
			method = cachedMethod.method;

			args = new Object[cachedMethod.serializers.length];
			for (int i = 0, n = args.length; i < n; i++) {
				final Serializer serializer = cachedMethod.serializers[i];
				if (serializer != null)
					args[i] = serializer.readObject(buffer, method.getParameterTypes()[i]);
				else
					args[i] = kryo.readClassAndObject(buffer);
			}

			if (method.getReturnType() != void.class)
				responseID = buffer.get();
		}

		@Override
		public void writeObjectData(final Kryo kryo, final ByteBuffer buffer) {
			IntSerializer.put(buffer, objectID, true);

			final int methodClassID = kryo.getRegisteredClass(method.getDeclaringClass()).getID();
			IntSerializer.put(buffer, methodClassID, true);

			final CachedMethod[] cachedMethods = getMethods(kryo, method.getDeclaringClass());
			CachedMethod cachedMethod = null;
			for (int i = 0, n = cachedMethods.length; i < n; i++) {
				cachedMethod = cachedMethods[i];
				if (cachedMethod.method.equals(method)) {
					buffer.put((byte) i);
					break;
				}
			}

			for (int i = 0, n = cachedMethod.serializers.length; i < n; i++) {
				final Serializer serializer = cachedMethod.serializers[i];
				if (serializer != null)
					serializer.writeObject(buffer, args[i]);
				else
					kryo.writeClassAndObject(buffer, args[i]);
			}

			if (method.getReturnType() != void.class)
				buffer.put(responseID);
		}
	}

	/**
	 * Internal message to return the result of a remotely invoked method.
	 */
	static public class InvokeMethodResult implements FrameworkMessage {
		public int objectID;
		public byte responseID;
		public Object result;
	}

	/**
	 * Handles network communication when methods are invoked on a proxy.
	 */
	static private class RemoteInvocationHandler implements InvocationHandler {
		private final Connection connection;
		final int objectID;
		private int timeoutMillis = 3000;
		private boolean nonBlocking, ignoreResponses;
		private Byte lastResponseID;
		final ArrayList<InvokeMethodResult> responseQueue = new ArrayList<InvokeMethodResult>();
		private byte nextResponseID = 1;
		private Listener responseListener;

		public RemoteInvocationHandler(final Connection connection, final int objectID) {
			super();
			this.connection = connection;
			this.objectID = objectID;

			responseListener = new Listener() {
				@Override
				public void disconnected(final Connection connection) {
					close();
				}

				@Override
				public void received(final Connection connection, final Object object) {
					if (!(object instanceof InvokeMethodResult))
						return;
					final InvokeMethodResult invokeMethodResult = (InvokeMethodResult) object;
					if (invokeMethodResult.objectID != objectID)
						return;
					synchronized (responseQueue) {
						responseQueue.add(invokeMethodResult);
						responseQueue.notifyAll();
					}
				}
			};
			connection.addListener(responseListener);
		}

		void close() {
			connection.removeListener(responseListener);
		}

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) {
			if (method.getDeclaringClass() == RemoteObject.class) {
				final String name = method.getName();
				if (name.equals("close")) {
					close();
					return null;
				} else if (name.equals("setResponseTimeout")) {
					timeoutMillis = (Integer) args[0];
					return null;
				} else if (name.equals("setNonBlocking")) {
					nonBlocking = (Boolean) args[0];
					ignoreResponses = (Boolean) args[1];
					return null;
				} else if (name.equals("waitForLastResponse")) {
					if (lastResponseID == null)
						throw new IllegalStateException("There is no last response to wait for.");
					return waitForResponse(lastResponseID);
				} else if (name.equals("getLastResponseID")) {
					if (lastResponseID == null)
						throw new IllegalStateException("There is no last response ID.");
					return lastResponseID;
				} else if (name.equals("waitForResponse")) {
					if (ignoreResponses)
						throw new IllegalStateException("This RemoteObject is configured to ignore all responses.");
					return waitForResponse((Byte) args[0]);
				}
			}

			final InvokeMethod invokeMethod = new InvokeMethod();
			invokeMethod.objectID = objectID;
			invokeMethod.method = method;
			invokeMethod.args = args;
			final boolean hasReturnValue = method.getReturnType() != void.class;
			if (hasReturnValue && !ignoreResponses) {
				final byte responseID = nextResponseID++;
				if (nextResponseID == 0)
					nextResponseID++; // Zero means don't send back a response.
				invokeMethod.responseID = responseID;
			}
			final int length = connection.sendTCP(invokeMethod);
			if (DEBUG) {
				String argString = "";
				if (args != null) {
					argString = Arrays.deepToString(args);
					argString = argString.substring(1, argString.length() - 1);
				}
				debug("kryonet", connection + " sent: " + method.getDeclaringClass().getSimpleName() + "#"
						+ method.getName() + "(" + argString + ") (" + length + ")");
			}

			if (!hasReturnValue)
				return null;
			if (nonBlocking) {
				if (!ignoreResponses)
					lastResponseID = invokeMethod.responseID;
				final Class<?> returnType = method.getReturnType();
				if (returnType.isPrimitive()) {
					if (returnType == int.class)
						return 0;
					if (returnType == boolean.class)
						return Boolean.FALSE;
					if (returnType == float.class)
						return 0f;
					if (returnType == char.class)
						return (char) 0;
					if (returnType == long.class)
						return 0l;
					if (returnType == short.class)
						return (short) 0;
					if (returnType == byte.class)
						return (byte) 0;
					if (returnType == double.class)
						return 0d;
				}
				return null;
			}
			try {
				return waitForResponse(invokeMethod.responseID);
			} catch (final TimeoutException ex) {
				throw new TimeoutException(
						"Response timed out: " + method.getDeclaringClass().getName() + "." + method.getName());
			}
		}

		private Object waitForResponse(final int responseID) {
			if (connection.getEndPoint().getUpdateThread() == Thread.currentThread())
				throw new IllegalStateException("Cannot wait for an RMI response on the connection's update thread.");

			final long endTime = System.currentTimeMillis() + timeoutMillis;
			synchronized (responseQueue) {
				while (true) {
					final int remaining = (int) (endTime - System.currentTimeMillis());
					for (int i = responseQueue.size() - 1; i >= 0; i++) {
						final InvokeMethodResult invokeMethodResult = responseQueue.get(i);
						if (invokeMethodResult.responseID == responseID) {
							responseQueue.remove(invokeMethodResult);
							lastResponseID = null;
							return invokeMethodResult.result;
						}
					}
					if (remaining <= 0)
						throw new TimeoutException("Response timed out.");
					try {
						responseQueue.wait(remaining);
					} catch (final InterruptedException ignored) {
					}
				}
			}
		}
	}

	static private final Object instancesLock = new Object();
	static ObjectSpace[] instances = new ObjectSpace[0];

	static private final HashMap<Class<?>, CachedMethod[]> methodCache = new HashMap<Class<?>, CachedMethod[]>();

	static CachedMethod[] getMethods(final Kryo kryo, final Class<?> type) {
		CachedMethod[] cachedMethods = methodCache.get(type);
		if (cachedMethods != null)
			return cachedMethods;

		final ArrayList<Method> allMethods = new ArrayList<Method>();
		Class<?> nextClass = type;
		while (nextClass != null && nextClass != Object.class) {
			Collections.addAll(allMethods, nextClass.getDeclaredMethods());
			nextClass = nextClass.getSuperclass();
		}
		final PriorityQueue<Method> methods = new PriorityQueue<Method>(Math.max(1, allMethods.size()),
				new Comparator<Method>() {
					@Override
					public int compare(final Method o1, final Method o2) {
						// Methods are sorted so they can be represented as an index.
						int diff = o1.getName().compareTo(o2.getName());
						if (diff != 0)
							return diff;
						final Class<?>[] argTypes1 = o1.getParameterTypes();
						final Class<?>[] argTypes2 = o2.getParameterTypes();
						if (argTypes1.length > argTypes2.length)
							return 1;
						if (argTypes1.length < argTypes2.length)
							return -1;
						for (int i = 0; i < argTypes1.length; i++) {
							diff = argTypes1[i].getName().compareTo(argTypes2[i].getName());
							if (diff != 0)
								return diff;
						}
						throw new RuntimeException("Two methods with same signature!"); // Impossible.
					}
				});
		for (int i = 0, n = allMethods.size(); i < n; i++) {
			final Method method = allMethods.get(i);
			final int modifiers = method.getModifiers();
			if (Modifier.isStatic(modifiers))
				continue;
			if (Modifier.isPrivate(modifiers))
				continue;
			if (method.isSynthetic())
				continue;
			methods.add(method);
		}

		final int n = methods.size();
		cachedMethods = new CachedMethod[n];
		for (int i = 0; i < n; i++) {
			final CachedMethod cachedMethod = new CachedMethod();
			cachedMethod.method = methods.poll();

			// Store the serializer for each final parameter.
			final Class<?>[] parameterTypes = cachedMethod.method.getParameterTypes();
			cachedMethod.serializers = new Serializer[parameterTypes.length];
			for (int ii = 0, nn = parameterTypes.length; ii < nn; ii++)
				if (Kryo.isFinal(parameterTypes[ii]))
					cachedMethod.serializers[ii] = kryo.getSerializer(parameterTypes[ii]);

			cachedMethods[i] = cachedMethod;
		}
		methodCache.put(type, cachedMethods);
		return cachedMethods;
	}

	/**
	 * Returns the first object registered with the specified ID in any of the
	 * ObjectSpaces the specified connection belongs to.
	 */
	static Object getRegisteredObject(final Connection connection, final int objectID) {
		final ObjectSpace[] instances = ObjectSpace.instances;
		for (int i = 0, n = instances.length; i < n; i++) {
			final ObjectSpace objectSpace = instances[i];
			// Check if the connection is in this ObjectSpace.
			final Connection[] connections = objectSpace.connections;
			for (int j = 0; j < connections.length; j++) {
				if (connections[j] != connection)
					continue;
				// Find an object with the objectID.
				final Object object = objectSpace.idToObject.get(objectID);
				if (object != null)
					return object;
			}
		}
		return null;
	}

	/**
	 * Returns a proxy object that implements the specified interfaces. Methods
	 * invoked on the proxy object will be invoked remotely on the object with the
	 * specified ID in the ObjectSpace for the specified connection. If the remote
	 * end of the connection has not {@link #addConnection(Connection) added} the
	 * connection to the ObjectSpace, the remote method invocations will be ignored.
	 * <p>
	 * Methods that return a value will throw {@link TimeoutException} if the
	 * response is not received with the {@link RemoteObject#setResponseTimeout(int)
	 * response timeout}.
	 * <p>
	 * If {@link RemoteObject#setNonBlocking(boolean, boolean) non-blocking} is
	 * false (the default), then methods that return a value must not be called from
	 * the update thread for the connection. An exception will be thrown if this
	 * occurs. Methods with a void return value can be called on the update thread.
	 * <p>
	 * If a proxy returned from this method is part of an object graph sent over the
	 * network, the object graph on the receiving side will have the proxy object
	 * replaced with the registered object.
	 *
	 * @see RemoteObject
	 */
	static public RemoteObject getRemoteObject(final Connection connection, final int objectID,
			final Class<?>... ifaces) {
		if (connection == null)
			throw new IllegalArgumentException("connection cannot be null.");
		if (ifaces == null)
			throw new IllegalArgumentException("ifaces cannot be null.");
		final Class<?>[] temp = new Class[ifaces.length + 1];
		temp[0] = RemoteObject.class;
		System.arraycopy(ifaces, 0, temp, 1, ifaces.length);
		return (RemoteObject) Proxy.newProxyInstance(ObjectSpace.class.getClassLoader(), temp,
				new RemoteInvocationHandler(connection, objectID));
	}

	/**
	 * Identical to {@link #getRemoteObject(Connection, int, Class...)} except
	 * returns the object cast to the specified interface type. The returned object
	 * still implements {@link RemoteObject}.
	 */
	@SuppressWarnings("unchecked")
	static public <T> T getRemoteObject(final Connection connection, final int objectID, final Class<T> iface) {
		return (T) getRemoteObject(connection, objectID, new Class[] { iface });
	}

	/**
	 * Registers the classes needed to use ObjectSpaces. This should be called
	 * before any connections are opened.
	 *
	 * @see EndPoint#getKryo()
	 * @see Kryo#register(Class, Serializer)
	 */
	static public void registerClasses(final Kryo kryo) {
		kryo.register(Object[].class);
		kryo.register(InvokeMethod.class);

		final FieldSerializer serializer = (FieldSerializer) kryo.register(InvokeMethodResult.class).getSerializer();
		serializer.getField("objectID").setClass(int.class, new IntSerializer(true));

		kryo.register(InvocationHandler.class, new Serializer() {
			@Override
			@SuppressWarnings("unchecked")
			public <T> T readObjectData(final ByteBuffer buffer, final Class<T> type) {
				final int objectID = IntSerializer.get(buffer, true);
				final Connection connection = (Connection) Kryo.getContext().get("connection");
				final Object object = getRegisteredObject(connection, objectID);
				if (WARN && object == null)
					warn("kryonet", "Unknown object ID " + objectID + " for connection: " + connection);
				return (T) object;
			}

			@Override
			public void writeObjectData(final ByteBuffer buffer, final Object object) {
				final RemoteInvocationHandler handler = (RemoteInvocationHandler) Proxy.getInvocationHandler(object);
				IntSerializer.put(buffer, handler.objectID, true);
			}
		});
	}

	final IntHashMap<Object> idToObject = new IntHashMap<Object>();

	Connection[] connections = {};

	final Object connectionsLock = new Object();

	private final Listener invokeListener = new Listener() {
		@Override
		public void disconnected(final Connection connection) {
			removeConnection(connection);
		}

		@Override
		public void received(final Connection connection, final Object object) {
			if (!(object instanceof InvokeMethod))
				return;
			if (connections != null) {
				int i = 0;
				final int n = connections.length;
				for (; i < n; i++)
					if (connection == connections[i])
						break;
				if (i == n)
					return; // The InvokeMethod message is not for a connection in this ObjectSpace.
			}
			final InvokeMethod invokeMethod = (InvokeMethod) object;
			final Object target = idToObject.get(invokeMethod.objectID);
			if (target == null) {
				if (WARN)
					warn("kryonet",
							"Ignoring remote invocation request for unknown object ID: " + invokeMethod.objectID);
				return;
			}
			invoke(connection, target, invokeMethod);
		}
	};

	/**
	 * Creates an ObjectSpace with no connections. Connections must be
	 * {@link #addConnection(Connection) added} to allow the remote end of the
	 * connections to access objects in this ObjectSpace.
	 */
	public ObjectSpace() {
		synchronized (instancesLock) {
			final ObjectSpace[] instances = ObjectSpace.instances;
			final ObjectSpace[] newInstances = new ObjectSpace[instances.length + 1];
			newInstances[0] = this;
			System.arraycopy(instances, 0, newInstances, 1, instances.length);
			ObjectSpace.instances = newInstances;
		}
	}

	/**
	 * Creates an ObjectSpace with the specified connection. More connections can be
	 * {@link #addConnection(Connection) added}.
	 */
	public ObjectSpace(final Connection connection) {
		this();
		addConnection(connection);
	}

	/**
	 * Allows the remote end of the specified connection to access objects
	 * registered in this ObjectSpace.
	 */
	public void addConnection(final Connection connection) {
		if (connection == null)
			throw new IllegalArgumentException("connection cannot be null.");

		synchronized (connectionsLock) {
			final Connection[] newConnections = new Connection[connections.length + 1];
			newConnections[0] = connection;
			System.arraycopy(connections, 0, newConnections, 1, connections.length);
			connections = newConnections;
		}

		connection.addListener(invokeListener);

		if (TRACE)
			trace("kryonet", "Added connection to ObjectSpace: " + connection);
	}

	/**
	 * Causes this ObjectSpace to stop listening to the connections for method
	 * invocation messages.
	 */
	public void close() {
		final Connection[] connections = this.connections;
		for (int i = 0; i < connections.length; i++)
			connections[i].removeListener(invokeListener);

		synchronized (instancesLock) {
			final ArrayList<Connection> temp = new ArrayList<Connection>();
			temp.remove(this);
			instances = temp.toArray(new ObjectSpace[temp.size()]);
		}

		if (TRACE)
			trace("kryonet", "Closed ObjectSpace.");
	}

	/**
	 * Invokes the method on the object and, if necessary, sends the result back to
	 * the connection that made the invocation request. This method is invoked on
	 * the update thread of the {@link EndPoint} for this ObjectSpace and can be
	 * overridden to perform invocations on a different thread.
	 *
	 * @param connection The remote side of this connection requested the
	 *                   invocation.
	 */
	protected void invoke(final Connection connection, final Object target, final InvokeMethod invokeMethod) {
		if (DEBUG) {
			String argString = "";
			if (invokeMethod.args != null) {
				argString = Arrays.deepToString(invokeMethod.args);
				argString = argString.substring(1, argString.length() - 1);
			}
			debug("kryonet", connection + " received: " + target.getClass().getSimpleName() + "#"
					+ invokeMethod.method.getName() + "(" + argString + ")");
		}

		Object result;
		final Method method = invokeMethod.method;
		try {
			result = method.invoke(target, invokeMethod.args);
		} catch (final Exception ex) {
			throw new RuntimeException(
					"Error invoking method: " + method.getDeclaringClass().getName() + "." + method.getName(), ex);
		}

		final byte responseID = invokeMethod.responseID;
		if (method.getReturnType() == void.class || responseID == 0)
			return;

		final InvokeMethodResult invokeMethodResult = new InvokeMethodResult();
		invokeMethodResult.objectID = invokeMethod.objectID;
		invokeMethodResult.responseID = responseID;
		invokeMethodResult.result = result;
		final int length = connection.sendTCP(invokeMethodResult);
		if (DEBUG)
			debug("kryonet", connection + " sent: " + result + " (" + length + ")");
	}

	/**
	 * Registers an object to allow the remote end of the ObjectSpace's connections
	 * to access it using the specified ID.
	 * <p>
	 * If a connection is added to multiple ObjectSpaces, the same object ID should
	 * not be registered in more than one of those ObjectSpaces.
	 *
	 * @see #getRemoteObject(Connection, int, Class...)
	 */
	public void register(final int objectID, final Object object) {
		if (object == null)
			throw new IllegalArgumentException("object cannot be null.");
		idToObject.put(objectID, object);
		if (TRACE)
			trace("kryonet", "Object registered with ObjectSpace as " + objectID + ": " + object);
	}

	/**
	 * Removes the specified connection, it will no longer be able to access objects
	 * registered in this ObjectSpace.
	 */
	public void removeConnection(final Connection connection) {
		if (connection == null)
			throw new IllegalArgumentException("connection cannot be null.");

		connection.removeListener(invokeListener);

		synchronized (connectionsLock) {
			final ArrayList<Connection> temp = new ArrayList<Connection>(Arrays.asList(connections));
			temp.remove(connection);
			connections = temp.toArray(new Connection[temp.size()]);
		}

		if (TRACE)
			trace("kryonet", "Removed connection from ObjectSpace: " + connection);
	}
}
