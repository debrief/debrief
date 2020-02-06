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

public class MoveControl extends AbstractEffect {

	/**
	 * @deprecated
	 * @param w
	 * @param x
	 * @param y
	 * @param duration
	 * @param movement
	 * @param onStop
	 * @param onCancel
	 */
	@Deprecated
	public static void move(final AnimationRunner runner, final Control w, final int x, final int y, final int duration,
			final IMovement movement, final Runnable onStop, final Runnable onCancel) {
		final Point oldSize = w.getLocation();
		final IEffect effect = new MoveControl(w, oldSize.x, x, oldSize.y, y, duration, movement, onStop, onCancel);
		runner.runEffect(effect);
	}

	int startX, endX, startY, endY, stepX, stepY;

	Control control = null;

	public MoveControl(final Control control, final int startX, final int endX, final int startY, final int endY,
			final long lengthMilli, final IMovement movement, final Runnable onStop, final Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);

		this.startX = startX;
		this.endX = endX;
		stepX = endX - startX;

		this.startY = startY;
		this.endY = endY;
		stepY = endY - startY;

		easingFunction.init(0, 1, (int) lengthMilli);

		this.control = control;
	}

	@Override
	public void applyEffect(final long currentTime) {
		if (!control.isDisposed()) {
			control.setLocation(((int) (startX + stepX * easingFunction.getValue((int) currentTime))),
					((int) (startY + stepY * easingFunction.getValue((int) currentTime))));
		}
	}

	public int getEnd() {
		return endY;
	}

	public int getStartX() {
		return startX;
	}

}