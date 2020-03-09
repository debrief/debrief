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
package org.mwc.cmap.NarrativeViewer2;

import java.io.Serializable;

import org.eclipse.swt.graphics.Color;

public interface INatEntry extends Serializable {
	public abstract Color getColor();

	public abstract String getLog();

	public abstract String getName();

	public abstract String getTime();

	public abstract String getType();

}