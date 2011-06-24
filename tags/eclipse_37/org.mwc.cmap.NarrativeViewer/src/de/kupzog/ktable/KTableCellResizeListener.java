/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 
 Author: Friederich Kupzog  
 fkmk@kupzog.de
 www.kupzog.de/fkmk
 */
package de.kupzog.ktable;

/**
 * @author Friederich Kupzog
 */
public interface KTableCellResizeListener {

	/**
	 * Is called when a row is resized. (but not when first row is resized!)
	 */
	public void rowResized(int row, int newHeight);

	/**
	 * Is called when a column is resized.
	 */
	public void columnResized(int col, int newWidth);
}
