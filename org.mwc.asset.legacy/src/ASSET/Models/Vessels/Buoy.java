/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package ASSET.Models.Vessels;

import java.util.Iterator;

import ASSET.Models.SensorType;


public class Buoy extends SSN
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Buoy(final int id)
  {
    super(id);
  }

  public Buoy(final int id, final ASSET.Participants.Status status, final ASSET.Participants.DemandedStatus demStatus, final String name)
  {
    super(id, status, demStatus, name);
  }

  public void initialise()
  {
    if(getStatus() != null)
      this.getStatus().setFuelLevel(100);
  }
  
  

	@Override
	public boolean radiatesThisNoise(int medium)
	{
		boolean res = false;		
		Iterator<SensorType> sensors = this.getSensorFit().getSensors().iterator();
		while (sensors.hasNext())
		{
			SensorType thisS = (SensorType) sensors.next();
			if(thisS.getMedium() == medium)
			{
				res = true;
				break;
			}
		}
		
		return res;
	}
}
