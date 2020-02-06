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

package com.planetmayo.debrief.satc_rcp.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.swt.widgets.Display;

public class UIListener implements InvocationHandler {
	public static class MinimumDelay extends Parameter {
		final String method;
		final int delay;

		long lastInvoked;

		public MinimumDelay(final String method, final int delay) {
			super();
			this.method = method;
			this.delay = delay;
		}

		public boolean checkInvocation(final Method method) {
			final Class<?> returnType = method.getReturnType();
			if (Void.class.equals(returnType) || void.class.equals(returnType)) {
				if (this.method.equals(method.getName())) {
					final long current = System.currentTimeMillis();
					if (current - lastInvoked > delay) {
						lastInvoked = current;
					} else {
						return false;
					}
				}
			}
			return true;
		}
	}

	public static abstract class Parameter {
	}

	@SuppressWarnings("unchecked")
	public static <T> T wrap(final Display display, final Class<?>[] listenerClasses, final T listener,
			final Parameter... parameters) {
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), listenerClasses,
				new UIListener(display, listener, parameters));
	}

	public static <T> T wrap(final Display display, final Class<T> listenerClass, final T listener,
			final Parameter... parameters) {
		return wrap(display, new Class<?>[] { listenerClass }, listener, parameters);
	}

	private final Object listener;

	private final Display display;

	private final Parameter[] parameters;

	private UIListener(final Display display, final Object listener, final Parameter... parameters) {
		this.listener = listener;
		this.display = display;
		this.parameters = parameters == null ? new Parameter[0] : parameters;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		for (final Parameter parameter : parameters) {
			if (parameter instanceof MinimumDelay) {
				final MinimumDelay delay = (MinimumDelay) parameter;
				if (!delay.checkInvocation(method)) {
					return null;
				}
			}
		}
		final Thread thread = display.getThread();
		if (Thread.currentThread().getId() != thread.getId()) {
			final Class<?> returnType = method.getReturnType();
			if (returnType.equals(Void.class) || returnType.equals(void.class)) {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							invokeMethod(method, args);
						} catch (final Throwable ex) {
							ex.printStackTrace();
						}
					}
				});
			} else {
				final Object[] result = new Object[1];
				final Throwable[] error = new Throwable[1];
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							result[0] = invokeMethod(method, args);
						} catch (final Throwable ex) {
							error[0] = ex;
						}
					}
				});
				if (error[0] != null) {
					throw error[0];
				}
				return result[0];
			}
			return null;
		}
		return invokeMethod(method, args);
	}

	private Object invokeMethod(final Method method, final Object[] args) throws Throwable {
		try {
			return method.invoke(listener, args);
		} catch (final InvocationTargetException ex) {
			throw ex.getTargetException();
		} catch (final Exception ex) {
			throw ex;
		}

	}
}
