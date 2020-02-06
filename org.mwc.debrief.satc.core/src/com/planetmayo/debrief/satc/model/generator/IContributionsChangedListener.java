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

package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

/** listener for when the list of contributions has changed
 * 
 * @author ian
 *
 */
public interface IContributionsChangedListener
{
	/**
	 * a contribution has been added
	 * 
	 * @param contribution
	 */
	public void added(BaseContribution contribution);

	/**
	 * a contribution has been removed
	 * 
	 * @param contribution
	 */
	public void removed(BaseContribution contribution);
	
	/** a contribution has been modified
	 * 
	 */
	public void modified();

}
