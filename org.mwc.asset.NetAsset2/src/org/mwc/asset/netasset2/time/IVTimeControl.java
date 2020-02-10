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

package org.mwc.asset.netasset2.time;

import org.eclipse.swt.events.SelectionListener;

public interface IVTimeControl {

	String PAUSE = "Pause";
	String PLAY = "Play";

	void addFasterListener(SelectionListener listener);

	void addPlayListener(SelectionListener listener);

	void addSlowerListener(SelectionListener listener);

	void addStepListener(SelectionListener listener);

	void addStopListener(SelectionListener listener);

	void setEnabled(boolean val);

	void setPlayLabel(String text);

}
