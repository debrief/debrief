
package ASSET.Models.Vessels;

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

public class FixedWing extends ASSET.Participants.CoreParticipant
{


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FixedWing(final int id)
  {
    this(id, null, null, null);
  }

  public FixedWing(final int id,
                   final ASSET.Participants.Status status,
                   final ASSET.Participants.DemandedStatus demStatus, final String name)
  {
    super(id, status, demStatus, name);
    this.setName("FixedWing");
  }
}