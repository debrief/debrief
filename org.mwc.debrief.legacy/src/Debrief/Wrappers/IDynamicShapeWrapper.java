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

package Debrief.Wrappers;

import java.awt.Color;

import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public interface IDynamicShapeWrapper extends Plottable {

	public Color getColor();

	public HiResDate getEndDTG();

	public HiResDate getStartDTG();

	public String getTrackName();

	public void setEndDTG(HiResDate date);

	public void setStartDTG(HiResDate date);

	public void setTrackName(String trackName);
}
