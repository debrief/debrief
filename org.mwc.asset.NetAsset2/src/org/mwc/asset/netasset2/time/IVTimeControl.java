/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.netasset2.time;

import org.eclipse.swt.events.SelectionListener;

public interface IVTimeControl
{

	String PAUSE = "Pause";
	String PLAY = "Play";

	void addStepListener(SelectionListener listener);

	void addPlayListener(SelectionListener listener);

	void addStopListener(SelectionListener listener);
	
	void addFasterListener(SelectionListener listener);
	void addSlowerListener(SelectionListener listener);

	void setPlayLabel(String text);

	void setEnabled(boolean val);

}
