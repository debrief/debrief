/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.comms.restlet.data;

import org.restlet.resource.Put;

/**
 * The resource associated to a contact.
 */
public interface ScenarioStateResource
{

	public final static String START = "Start";
	public final static String STOP = "Stop";
	public final static String FASTER = "Faster";
	public final static String SLOWER = "Slower";
	
	@Put
	public void store(String newState);
}
