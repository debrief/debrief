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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * Shake effect (like login failure on Mac OSX)
 *
 * @author Nicolas Richeton
 *
 */
public class Shake extends AbstractEffect {

	/**
	 * @deprecated
	 * @param w
	 * @param duration
	 * @param movement
	 * @param onStop
	 * @param onCancel
	 */
	@Deprecated
	public static void shake(final AnimationRunner runner, final Control w, final int duration,
			final IMovement movement, final Runnable onStop, final Runnable onCancel) {
		final IEffect effect = new Shake(w, w.getLocation(), new Point(w.getLocation().x + 10, w.getLocation().y + 10),
				duration, movement, onStop, onCancel);
		runner.runEffect(effect);
	}

	Point src, dest, diff;

	Control control = null;

	public Shake(final Control control, final Point src, final Point dest, final long lengthMilli,
			final IMovement movement, final Runnable onStop, final Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);
		this.src = src;
		this.dest = dest;
		this.control = control;
		this.diff = new Point(dest.x - src.x, dest.y - src.y);

		easingFunction.init(0, 1, (int) lengthMilli);
	}

	@Override
	public void applyEffect(final long currentTime) {
		if (!control.isDisposed()) {
			control.setLocation((int) (src.x - diff.x * easingFunction.getValue(currentTime)), src.y);
		}
	}
}