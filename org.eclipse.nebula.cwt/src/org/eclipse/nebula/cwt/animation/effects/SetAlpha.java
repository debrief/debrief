/*******************************************************************************
 * Copyright (c) 2006-2009 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.cwt.animation.effects;

import org.eclipse.nebula.cwt.animation.AnimationRunner;
import org.eclipse.nebula.cwt.animation.movement.IMovement;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Shell;

public class SetAlpha extends AbstractEffect {

	/**
	 * Add a listener that will fade the window when it get closed.
	 *
	 * @param shell
	 * @param duration
	 * @param easing
	 */
	public static void fadeOnClose(final Shell shell, final int duration, final IMovement easing) {

		final Runnable closeListener = new Runnable() {
			@Override
			public void run() {
				shell.dispose();
			}
		};

		shell.addShellListener(new ShellListener() {

			@Override
			public void shellActivated(final ShellEvent e) {
				// Do nothing
			}

			@Override
			public void shellClosed(final ShellEvent e) {
				e.doit = false;

				setAlpha(new AnimationRunner(), shell, 0, duration, easing, closeListener, null);
			}

			@Override
			public void shellDeactivated(final ShellEvent e) {
				// Do nothing
			}

			@Override
			public void shellDeiconified(final ShellEvent e) {
				// Do nothing
			}

			@Override
			public void shellIconified(final ShellEvent e) {
				// Do nothing
			}

		});

	}

	/**
	 * @deprecated
	 * @param w
	 * @param alpha
	 * @param duration
	 * @param movement
	 * @param onStop
	 * @param onCancel
	 */
	@Deprecated
	public static void setAlpha(final AnimationRunner runner, final Shell w, final int alpha, final int duration,
			final IMovement movement, final Runnable onStop, final Runnable onCancel) {
		final SetAlpha effect = new SetAlpha(w, w.getAlpha(), alpha, duration, movement, onStop, onCancel);
		runner.runEffect(effect);
	}

	int start, end, step;

	Shell shell = null;

	public SetAlpha(final Shell shell, final int start, final int end, final long lengthMilli, final IMovement movement,
			final Runnable onStop, final Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);

		this.start = start;
		this.end = end;
		step = end - start;
		this.shell = shell;
		easingFunction.init(0, 1, (int) lengthMilli);

	}

	@Override
	public void applyEffect(final long currentTime) {
		if (shell.isDisposed()) {
			return;
		}

		shell.setAlpha((int) (start + step * easingFunction.getValue((int) currentTime)));
	}

}