package org.mwc.cmap.core.ui_support;

import java.lang.reflect.Method;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * custom version of dialog class that opens without use of syncExec. We need to run dialogs async
 * so that RCPTT runtime can interact with dialog controls.
 *
 * @author ian
 *
 * @param <T>
 *          Return type
 */
public abstract class ThreadSafeDialog<T> extends Dialog
{

  /** are we on the UI thread?
   * 
   * @return yes/no
   */
  private static boolean isUIThread()
  {
    Object uiThread = null;
    try
    {
      final Class<?> displayClass = Display.class;

      final Method getThreadMethod = displayClass.getDeclaredMethod("getThread",
          new Class[]
          {});
      uiThread = getThreadMethod.invoke(Display.getDefault(), new Object[]
      {});
    }
    catch (final Exception e)
    {

      e.printStackTrace();
    }

    return (Thread.currentThread() == uiThread);
  }

  /** we need to store the selection locally, so it can be passed into
   * the lock object
   */
  private T selection;

  protected ThreadSafeDialog(final Shell parentShell)
  {
    super(parentShell);
  }

  /** child classes should implement this to actually open the dialog
   * 
   * @return
   */
  protected abstract T internalOpenDialog();

  public T openDialog()
  {
    // if we're already on the UI thread we can just open dialog.    
    if (isUIThread())
    {
      return selection = internalOpenDialog();
    }

    final Object lock = new Object();

    final Display current = Display.getDefault();

    current.asyncExec(new Runnable()
    {
      @Override
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
      catch (final InterruptedException e)
      {
        e.printStackTrace();
      }
    }
    return selection;
  }

}
