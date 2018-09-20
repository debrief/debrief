package org.mwc.cmap.core.ui_support;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class ThreadSafeDialog<T> extends
    org.eclipse.jface.dialogs.Dialog
{

  private T selection;

  protected ThreadSafeDialog(Shell parentShell)
  {
    super(parentShell);
  }

  protected abstract T internalOpenDialog();

  public T openDialog()
  {
    
    if(isUIThread())
    {

      return selection = internalOpenDialog();
      
    }
    
    final Object lock = new Object();

    final Display current = Display.getDefault();

    current.asyncExec(new Runnable()
    {
      public void run()
      {
        synchronized (lock)
        {
          try
          {
            selection = internalOpenDialog();
          }
          finally
          {
            lock.notify();
          }
        }
      }
    });

    synchronized (lock)
    {
      try
      {
        lock.wait();
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }

    return selection;
  }

  public static boolean isUIThread()
  {
      Object uiThread = null;
      try
      {
          Class<?> displayClass = Display.class;
         
          Method getThreadMethod = displayClass.getDeclaredMethod("getThread", new Class[] { });
          uiThread = getThreadMethod.invoke(Display.getDefault(), new Object[] { });
      }
      catch(Exception e)
      {

        e.printStackTrace();
      }

      return (Thread.currentThread() == uiThread);
  }
  
}


