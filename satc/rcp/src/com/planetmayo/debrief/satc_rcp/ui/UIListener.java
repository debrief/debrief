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
	
	private UIListener(Display display, Object listener) 
	{
		this.listener = listener;
		this.display = display;
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
	
	@SuppressWarnings("unchecked")
	public static <T> T wrap(Display display, Class<T> listenerClass, T listener)
	{
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
				new Class<?>[] {listenerClass}, new UIListener(display, listener));
	}	
}
