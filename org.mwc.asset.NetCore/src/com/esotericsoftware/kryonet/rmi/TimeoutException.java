/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.esotericsoftware.kryonet.rmi;

/**
 * Thrown when a method with a return value is invoked on a remote object and the response is not received with the
 * {@link RemoteObject#setResponseTimeout(int) response timeout}.
 * @see ObjectSpace#getRemoteObject(com.esotericsoftware.kryonet.Connection, int, Class...)
 * @author Nathan Sweet <misc@n4te.com>
 */
public class TimeoutException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TimeoutException () {
		super();
	}

	public TimeoutException (String message, Throwable cause) {
		super(message, cause);
	}

	public TimeoutException (String message) {
		super(message);
	}

	public TimeoutException (Throwable cause) {
		super(cause);
	}
}
