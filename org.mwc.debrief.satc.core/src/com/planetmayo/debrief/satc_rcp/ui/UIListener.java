/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc_rcp.ui;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.swt.widgets.Display;

public class UIListener implements InvocationHandler 
{
	private final Object listener;
	private final Display display;
	private final Parameter[] parameters;
	
	private UIListener(Display display, Object listener, Parameter... parameters) 
	{
		this.listener = listener;
		this.display = display;
		this.parameters = parameters == null ? new Parameter[0] : parameters;
	}
	
	private Object invokeMethod(Method method, Object[] args) throws Throwable 
	{
		try 
		{
			return method.invoke(listener, args);
		} 
		catch (InvocationTargetException ex) 
		{
			throw ex.getTargetException();
		} 
		catch (Exception ex) 
		{
			throw ex;
		}
	
	}

	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args)
			throws Throwable 
	{
		for (Parameter parameter : parameters)
		{
			if (parameter instanceof MinimumDelay)
			{
				MinimumDelay delay = (MinimumDelay) parameter;
				if (! delay.checkInvocation(method)) 
				{
					return null;					
				}
			}
		}
		Thread thread = display.getThread();
		if (Thread.currentThread().getId() != thread.getId()) 
		{
			Class<?> returnType = method.getReturnType();
			if (returnType.equals(Void.class) || returnType.equals(void.class)) 
			{
				display.asyncExec(new Runnable() 
				{				
					@Override
					public void run() 
					{
						try 
						{
							invokeMethod(method, args);
						} catch (Throwable ex) 
						{
							ex.printStackTrace();							
						}
					}
				});				
			} 
			else 
			{
				final Object[] result = new Object[1];
				final Throwable[] error = new Throwable[1];
				display.syncExec(new Runnable() 
				{
					@Override
					public void run()					
					{
						try 
						{
							result[0] = invokeMethod(method, args);
						}
						catch (Throwable ex) 
						{
							error[0] = ex;
						}
					}
				});
				if (error[0] != null) 
				{
					throw error[0];
				}
				return result[0];
			}
			return null;
		}		
		return invokeMethod(method, args);
	}
	
	public static <T> T wrap(Display display, Class<T> listenerClass, T listener, Parameter... parameters)
	{
		return wrap(display, new Class<?>[] {listenerClass}, listener, parameters);
	}
	
	@SuppressWarnings("unchecked")	
	public static <T> T wrap(Display display, Class<?>[] listenerClasses, T listener, Parameter... parameters) 
	{
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
				listenerClasses, new UIListener(display, listener, parameters));		
	}
	
	public static abstract class Parameter { }
	
	public static class MinimumDelay extends Parameter 
	{
		final String method;
		final int delay;
		
		long lastInvoked;
		
		public MinimumDelay(String method, int delay)
		{
			super();
			this.method = method;
			this.delay = delay;
		}
		
		public boolean checkInvocation(Method method)
		{
			Class<?> returnType = method.getReturnType();
			if (Void.class.equals(returnType) || void.class.equals(returnType))
			{
				if (this.method.equals(method.getName()))
				{
					long current = System.currentTimeMillis();
					if (current - lastInvoked > delay)
					{
						lastInvoked = current;
					}
					else
					{
						return false;
					}
				}
			}
			return true;
		}
	}
}
