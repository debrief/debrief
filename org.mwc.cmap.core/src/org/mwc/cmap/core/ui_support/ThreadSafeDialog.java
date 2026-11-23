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
package org.mwc.cmap.core.ui_support;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * custom version of dialog class that opens without use of syncExec. We need to
 * run dialogs async so that RCPTT runtime can interact with dialog controls.
 *
 * @author ian
 *
 * @param <T> Return type
 */
public abstract class ThreadSafeDialog<T> extends Dialog {

	/**
	 * are we on the UI thread?
	 *
	 * @return yes/no
	 */
	private static boolean isUIThread() {
		Object uiThread = null;
		try {
			final Class<?> displayClass = Display.class;

			final Method getThreadMethod = displayClass.getDeclaredMethod("getThread", new Class[] {});
			uiThread = getThreadMethod.invoke(Display.getDefault(), new Object[] {});
		} catch (final Exception e) {

			e.printStackTrace();
		}

		return (Thread.currentThread() == uiThread);
	}

	/**
	 * we need to store the selection locally, so it can be passed into the lock
	 * object
	 */
	private volatile T selection;

	protected ThreadSafeDialog(final Shell parentShell) {
		super(parentShell);
	}

	/**
	 * child classes should implement this to actually open the dialog
	 *
	 * @return
	 */
	protected abstract T internalOpenDialog();

	public T openDialog() {
		// if we're already on the UI thread we can just open dialog.
		if (isUIThread()) {
			return selection = internalOpenDialog();
		}

		// a latch, not wait/notify: the UI thread may open (and close) the dialog
		// before this thread starts to wait, and a notification sent that early
		// would be lost, leaving this thread waiting for ever
		final CountDownLatch done = new CountDownLatch(1);

		final Display current = Display.getDefault();

		current.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					selection = internalOpenDialog();
				} finally {
					done.countDown();
				}
			}
		});

		try {
			done.await();
		} catch (final InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		return selection;
	}

}
