/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.kupzog.ktable.renderers;

/**
 * Interface that provides access to a percentage value. This is currently only
 * used for the BarDiagramCellRenderer to determine the length of the bar to
 * render.
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public interface IPercentage {

	/**
	 * @return Returns a percentage value between 0 and 1.
	 */
	public float getPercentage();

	/**
	 * @return Returns the absolute value that was responsible for the
	 *         percentage value.
	 */
	public float getAbsoluteValue();
}
