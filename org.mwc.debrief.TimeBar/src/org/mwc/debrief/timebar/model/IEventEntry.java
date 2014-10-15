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
package org.mwc.debrief.timebar.model;

import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.graphics.Color;

public interface IEventEntry 
{
	public boolean isVisible();
	
	public Object getSource();
	
	public Calendar getStart();
	
	public Calendar getEnd();
	
	public String getName();
	
	public Color getColor();
	
	public String getToolTipText();
	
	/**
	 * Returns series of events, for example narrative entries for a narrative wrapper.
	 */
	public List<IEventEntry> getChildren();
	
}
